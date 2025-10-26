package com.jobaroo.http.routes

import cats.data.OptionT
import io.circe.generic.auto.*
import org.http4s.circe.CirceEntityCodec.*
import org.http4s.*
import org.http4s.implicits.*
import org.http4s.dsl.*
import org.http4s.dsl.impl.*
import cats.effect.testing.scalatest.AsyncIOSpec
import org.http4s.dsl.Http4sDsl
import org.scalatest.freespec.AsyncFreeSpec
import org.scalatest.matchers.should.Matchers
import cats.effect.*
import cats.implicits.*
import com.jobaroo.core.Auth
import com.jobaroo.domain.security.*
import com.jobaroo.domain.auth.{LoginInfo, NewPasswordInfo, NewUserInfo}
import com.jobaroo.domain.security.JwtToken
import com.jobaroo.domain.user.User
import com.jobaroo.domain.{auth, user}
import com.jobaroo.fixtures.SecuredRouteFixture
import org.http4s.HttpRoutes
import org.http4s.headers.Authorization
import org.typelevel.ci.CIStringSyntax
import org.typelevel.log4cats.Logger
import org.typelevel.log4cats.slf4j.Slf4jLogger
import tsec.authentication.JWTAuthenticator
import tsec.mac.jca.HMACSHA256
import tsec.authentication.IdentityStore
import tsec.jws.mac.JWTMac
import tsec.passwordhashers.PasswordHash
import tsec.passwordhashers.jca.BCrypt
import com.jobaroo.fixtures.SecuredRouteFixture.withBearerToken
import scala.concurrent.duration.*
import com.jobaroo.domain.auth.ForgottenPasswordInfo
import com.jobaroo.domain.auth.RecoverPasswordInfo

class AuthRoutesSpec extends AsyncFreeSpec with AsyncIOSpec with Matchers with Http4sDsl[IO] with SecuredRouteFixture:

  ////////////////////////////////////////////////////////////////////////////////////
  // prep
  ////////////////////////////////////////////////////////////////////////////////////

  private val mockAuth: Auth[IO] = probedAuth(None)

  private def probedAuth(optUsers: Option[Ref[IO, Map[String, String]]]): Auth[IO] = new Auth[IO]:

    override def login(email: String, password: String): IO[Option[User]] =
      if email == jenniferLawrence.email && password == jenniferLawrencePassword then
        IO.pure(Some(jenniferLawrence))
      else IO.pure(None)

    override def signUp(newUserInfo: NewUserInfo): IO[Option[User]] =
      if newUserInfo.email == johnnyDepp.email then IO.pure(Some(johnnyDepp)) else IO.pure(None)

    override def changePassword(email: String, newPasswordInfo: NewPasswordInfo): IO[Either[String, Option[User]]] =
      if email == jenniferLawrence.email then
        if newPasswordInfo.oldPassword == jenniferLawrencePassword then IO.pure(Right(Some(jenniferLawrence)))
        else IO.pure(Left("Invalid password"))
      else IO.pure(Right(None))

    override def delete(email: String): IO[Boolean] = IO.pure(true)

    override def recoverPasswordFromToken(email: String, token: String, newPassword: String): IO[Boolean] =
      optUsers.traverse { ref => ref.get.map { map => map.get(email).filter(_ == token) }.map(_.nonEmpty) }.map(
        _.getOrElse(false)
      )

    override def sendPasswordRecoveryToken(email: String): IO[Unit] =
      optUsers.traverse { ref => ref.modify { map => (map + (email -> "abc"), ()) } }.map(_ => ())

  ////////////////////////////////////////////////////////////////////////////////////
  // tests
  ////////////////////////////////////////////////////////////////////////////////////

  given Logger[IO]               = Slf4jLogger.getLogger[IO]
  val authRoutes: HttpRoutes[IO] = AuthRoutes[IO](mockAuth, mockAuthenticator).routes

  "AuthRoutes" - {
    "should return a 401 - unauthorized if login fails" in {
      for
        resp <- authRoutes.orNotFound.run(
                  Request(method = Method.POST, uri = uri"/auth/login")
                    .withEntity(LoginInfo(jenniferLawrence.email, "wrong_password"))
                )
      yield resp.status shouldBe Status.Unauthorized
    }

    "should return a 200 - OK + JWT if login is successful" in {
      for
        resp <- authRoutes.orNotFound.run(
                  Request(method = Method.POST, uri = uri"/auth/login")
                    .withEntity(LoginInfo(jenniferLawrence.email, jenniferLawrencePassword))
                )
      yield
        resp.status shouldBe Status.Ok
        resp.headers.get(ci"Authorization") shouldBe defined
    }

    "should return a 400 - Bad Request if the user to create already exists" in {
      for
        resp <- authRoutes.orNotFound.run(
                  Request(method = Method.POST, uri = uri"/auth/users")
                    .withEntity(
                      NewUserInfo(
                        email = jenniferLawrence.email,
                        password = jenniferLawrencePassword,
                        firstName = jenniferLawrence.firstName,
                        lastName = jenniferLawrence.lastName,
                        company = jenniferLawrence.company
                      )
                    )
                )
      yield resp.status shouldBe Status.BadRequest
    }

    "should return a 201 - Crated if the user creation succeeds" in {
      for
        resp <- authRoutes.orNotFound.run(
                  Request(method = Method.POST, uri = uri"/auth/users")
                    .withEntity(
                      NewUserInfo(
                        email = johnnyDepp.email,
                        password = johnnyDeppPassword,
                        firstName = johnnyDepp.firstName,
                        lastName = johnnyDepp.lastName,
                        company = johnnyDepp.company
                      )
                    )
                )
      yield resp.status shouldBe Status.Created
    }

    "should return a 200 - Ok with a valid JWT token" in {
      for
        jwtToken <- mockAuthenticator.create(jenniferLawrence.email)
        resp     <- authRoutes.orNotFound.run(
                      Request[IO](method = Method.POST, uri = uri"/auth/logout").withBearerToken(jwtToken)
                    )
      yield resp.status shouldBe Status.Ok
    }

    "should return a 401 - Unauthorized if logging out without a valid JWT token" in {
      for
        resp <- authRoutes.orNotFound.run(Request(method = Method.POST, uri = uri"/auth/logout"))
      yield resp.status shouldBe Status.Unauthorized
    }

    "should return a 403 - Forbidden if old password is incorrect" in {
      for
        jwtToken <- mockAuthenticator.create(jenniferLawrence.email)
        resp     <- authRoutes.orNotFound.run(
                      Request[IO](method = Method.PUT, uri = uri"/auth/users/password")
                        .withBearerToken(jwtToken)
                        .withEntity(NewPasswordInfo("wrong_password", "new_password"))
                    )
      yield resp.status shouldBe Status.Forbidden
    }

    "should return a 401 - Unauthorized if changing password without a JWT token" in {
      for
        resp <- authRoutes.orNotFound.run(
                  Request(method = Method.PUT, uri = uri"/auth/users/password")
                    .withEntity(NewPasswordInfo(jenniferLawrencePassword, "new_password"))
                )
      yield resp.status shouldBe Status.Unauthorized
    }

    "should return a 200 - Ok if changing password for a user with a valid JWT token" in {
      for
        jwtToken <- mockAuthenticator.create(jenniferLawrence.email)
        resp     <- authRoutes.orNotFound.run(
                      Request[IO](method = Method.PUT, uri = uri"/auth/users/password")
                        .withBearerToken(jwtToken)
                        .withEntity(NewPasswordInfo(jenniferLawrencePassword, "new_password"))
                    )
      yield resp.status shouldBe Status.Ok
    }

    "should return a 401 - Unauthorized if a non-admin tries to delete a user" in {
      for
        jwtToken <- mockAuthenticator.create(johnnyDepp.email)
        resp     <- authRoutes.orNotFound.run(
                      Request[IO](method = Method.DELETE, uri = uri"/auth/users/jennifer@lawrence.com")
                        .withBearerToken(jwtToken)
                    )
      yield resp.status shouldBe Status.Unauthorized
    }

    "should return a 200 - Ok if an admin tries to delete a user" in {
      for
        jwtToken <- mockAuthenticator.create(jenniferLawrence.email)
        resp     <- authRoutes.orNotFound.run(
                      Request[IO](method = Method.DELETE, uri = uri"/auth/users/jennifer@lawrence.com")
                        .withBearerToken(jwtToken)
                    )
      yield resp.status shouldBe Status.Ok
    }

    "should return a 200 - Ok when resetting a password and an email should be triggered" in {
      for
        usersRef <- IO.ref(Map.empty[String, String])
        auth   = probedAuth(Some(usersRef))
        routes = AuthRoutes[IO](auth, mockAuthenticator).routes
        resp    <- routes.orNotFound.run(
                     Request[IO](method = Method.POST, uri = uri"/auth/reset")
                       .withEntity(ForgottenPasswordInfo(christopherNolan.email))
                   )
        userMap <- usersRef.get
      yield
        resp.status shouldBe Status.Ok
        userMap shouldBe Map(christopherNolan.email -> "abc")
    }

    "should return a 200 - Ok when recovering a password for a correct user/token combination" in {
      for
        usersRef <- IO.ref(Map(christopherNolan.email -> "abc"))
        auth   = probedAuth(Some(usersRef))
        routes = AuthRoutes[IO](auth, mockAuthenticator).routes
        resp    <- routes.orNotFound.run(
                     Request[IO](method = Method.POST, uri = uri"/auth/recover")
                       .withEntity(RecoverPasswordInfo(christopherNolan.email, "abc", "new_password"))
                   )
        userMap <- usersRef.get
      yield resp.status shouldBe Status.Ok
    }

    "should return a 403 - Forbidden when recovering a password for a user with an incorrect token" in {
      for
        usersRef <- IO.ref(Map(christopherNolan.email -> "abc"))
        auth   = probedAuth(Some(usersRef))
        routes = AuthRoutes[IO](auth, mockAuthenticator).routes
        resp    <- routes.orNotFound.run(
                     Request[IO](method = Method.POST, uri = uri"/auth/recover")
                       .withEntity(RecoverPasswordInfo(christopherNolan.email, "wrong_token", "new_password"))
                   )
        userMap <- usersRef.get
      yield resp.status shouldBe Status.Forbidden
    }
    
  }
