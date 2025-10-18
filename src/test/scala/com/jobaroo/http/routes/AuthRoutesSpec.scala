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
import com.jobaroo.fixtures.UserFixture
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

import scala.concurrent.duration.*

class AuthRoutesSpec extends AsyncFreeSpec with AsyncIOSpec with Matchers with Http4sDsl[IO] with UserFixture:

  ////////////////////////////////////////////////////////////////////////////////////
  // prep
  ////////////////////////////////////////////////////////////////////////////////////

  private val mockAuthenticator: Authenticator[IO] =
    val idStore: IdentityStore[IO, String, User] = (email: String) =>
      if email == jenniferLawrence.email then OptionT.pure(jenniferLawrence) else OptionT.none[IO, User]

    JWTAuthenticator.unbacked.inBearerToken(
      expiryDuration = 1.day,
      maxIdle = None,
      identityStore = idStore,
      signingKey = HMACSHA256.unsafeGenerateKey
    )

  private val mockAuth: Auth[IO] = new Auth[IO]:

    override def login(email: String, password: String): IO[Option[JwtToken]] =
      if email == jenniferLawrence.email && password == jenniferLawrencePassword then
        mockAuthenticator.create(jenniferLawrence.email).map(Some(_))
      else IO.pure(None)

    override def signUp(newUserInfo: NewUserInfo): IO[Option[User]] =
      if newUserInfo.email == johnnyDepp.email then IO.pure(Some(johnnyDepp)) else IO.pure(None)

    override def changePassword(email: String, newPasswordInfo: NewPasswordInfo): IO[Either[String, Option[User]]] =
      if email == jenniferLawrence.email then
        if newPasswordInfo.oldPassword == jenniferLawrencePassword then IO.pure(Right(Some(jenniferLawrence)))
        else IO.pure(Left("Invalid password"))
      else IO.pure(Right(None))

    override def authenticator: Authenticator[IO] = mockAuthenticator

  extension (r: Request[IO])

    def withBearerToken(jwtToken: JwtToken): Request[IO] = r.putHeaders {
      val jwtString = JWTMac.toEncodedString[IO, Crypto](jwtToken.jwt)
      Authorization(Credentials.Token(AuthScheme.Bearer, jwtString))
    }

  ////////////////////////////////////////////////////////////////////////////////////
  // tests
  ////////////////////////////////////////////////////////////////////////////////////

  given Logger[IO]               = Slf4jLogger.getLogger[IO]
  val authRoutes: HttpRoutes[IO] = AuthRoutes[IO](mockAuth).routes

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
                      Request(method = Method.POST, uri = uri"/auth/logout").withBearerToken(jwtToken)
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
                      Request(method = Method.PUT, uri = uri"/auth/users/password")
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
                      Request(method = Method.PUT, uri = uri"/auth/users/password")
                        .withBearerToken(jwtToken)
                        .withEntity(NewPasswordInfo(jenniferLawrencePassword, "new_password"))
                    )
      yield resp.status shouldBe Status.Ok
    }

  }
