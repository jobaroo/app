package com.jobaroo.http.routes

import io.circe.generic.auto.*
import org.http4s.circe.CirceEntityCodec.*
import org.http4s.*
import org.http4s.dsl.*
import org.http4s.dsl.impl.*
import org.http4s.server.*
import cats.*
import cats.syntax.semigroup.*
import cats.implicits.*
import cats.effect.*
import tsec.authentication.{asAuthed, SecuredRequestHandler, TSecAuthService}
import org.typelevel.log4cats.Logger
import com.jobaroo.logging.syntax.*
import com.jobaroo.domain.security.*
import com.jobaroo.http.validation.syntax.*
import com.jobaroo.domain.user.User
import com.jobaroo.core.Auth
import com.jobaroo.http.response.FailureResponse
import com.jobaroo.domain.auth.{LoginInfo, NewPasswordInfo, NewUserInfo}

import scala.language.implicitConversions

class AuthRoutes[F[_] : Concurrent : Logger : SecuredHandler] private (
  auth          : Auth[F],
  authenticator : Authenticator[F]
) extends Http4sValidationDsl[F]:

  private val loginRoute: HttpRoutes[F] = HttpRoutes.of[F] {
    case req @ POST -> Root / "login" =>
      req.validate[LoginInfo] { loginInfo =>
        val optJwtToken =
          for
            validatedUser <- auth.login(loginInfo.email, loginInfo.password)
            _             <- Logger[F].info(s"user logging: ${loginInfo.email}")
            optJwtToken   <- validatedUser.traverse { user => authenticator.create(user.email) }
          yield optJwtToken

        optJwtToken.map {
          case Some(jwtToken) => authenticator.embed(Response(Status.Ok), jwtToken)
          case None           => Response(Status.Unauthorized)
        }
      }
  }

  private val createUserRoute: HttpRoutes[F] = HttpRoutes.of[F] {
    case req @ POST -> Root / "users" =>
      req.validate[NewUserInfo] { newUserInfo =>
        for
          optUser <- auth.signUp(newUserInfo)
          resp    <- optUser match
                       case Some(user) => Created(user.email)
                       case None       => BadRequest(s"User with email: ${newUserInfo.email} already exists.")
        yield resp
      }
  }

  private val changePasswordRoute: AuthRoute[F] = {
    case req @ PUT -> Root / "users" / "password" asAuthed user =>
      req.request.validate[NewPasswordInfo] { newPasswordInfo =>
        for
          eitherOptUser <- auth.changePassword(user.email, newPasswordInfo)
          resp          <- eitherOptUser match
                             case Right(Some(_)) => Ok()
                             case Right(None)    => NotFound(FailureResponse(s"user: ${user.email} not found."))
                             case Left(_)        => Forbidden()
        yield resp
      }
  }

  private val logoutRoute: AuthRoute[F] = {
    case req @ POST -> Root / "logout" asAuthed _ =>
      val token = req.authenticator
      for
        _    <- authenticator.discard(token)
        resp <- Ok()
      yield resp
  }

  private val deleteRoute: AuthRoute[F] = {
    case req @ DELETE -> Root / "users" / email asAuthed _ =>
      auth.delete(email).flatMap {
        case true  => Ok()
        case false => NotFound()
      }
  }

  private val unauthedRoutes = loginRoute <+> createUserRoute

  private val authedRoutes = SecuredHandler[F].liftService(
    changePasswordRoute.restrictedTo(allRoles) |+|
      logoutRoute.restrictedTo(allRoles) |+|
      deleteRoute.restrictedTo(adminOnly)
  )

  val routes = Router(
    "/auth" -> (unauthedRoutes <+> authedRoutes)
  )

object AuthRoutes:

  def apply[F[_] : Concurrent : Logger : SecuredHandler](
    auth: Auth[F],
    authenticator: Authenticator[F]
  ): AuthRoutes[F] =
    new AuthRoutes[F](auth, authenticator)
