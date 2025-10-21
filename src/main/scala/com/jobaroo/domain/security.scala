package com.jobaroo.domain

import cats.MonadThrow
import cats.syntax.applicative.*
import cats.syntax.semigroup.*
import com.jobaroo.domain.user.{Role, User}
import org.http4s.{Response, Status}
import tsec.authentication.{AugmentedJWT, JWTAuthenticator, SecuredRequest, SecuredRequestHandler}
import tsec.authorization.BasicRBAC
import tsec.mac.jca.HMACSHA256
import tsec.authentication.TSecAuthService
import cats.Applicative
import cats.Monad
import cats.kernel.Semigroup

object security:

  type Crypto               = HMACSHA256
  type JwtToken             = AugmentedJWT[Crypto, String]
  type Authenticator[F[_]]  = JWTAuthenticator[F, String, User, Crypto]
  type AuthRoute[F[_]]      = PartialFunction[SecuredRequest[F, User, JwtToken], F[Response[F]]]
  type AuthRBAC[F[_]]       = BasicRBAC[F, Role, User, JwtToken]
  type SecuredHandler[F[_]] = SecuredRequestHandler[F, String, User, JwtToken]

  object SecuredHandler:
    def apply[F[_]](using handler: SecuredHandler[F]): SecuredHandler[F] = handler
  
  def allRoles[F[_]: MonadThrow]: AuthRBAC[F]      = BasicRBAC.all[F, Role, User, JwtToken]
  def adminOnly[F[_]: MonadThrow]: AuthRBAC[F]     = BasicRBAC(Role.ADMIN)
  def recruiterOnly[F[_]: MonadThrow]: AuthRBAC[F] = BasicRBAC(Role.RECRUITER)

  final case class Authorizations[F[_]](rbacRoutes: Map[AuthRBAC[F], List[AuthRoute[F]]])

  object Authorizations:

    given [F[_]]: Semigroup[Authorizations[F]] = Semigroup.instance { (a, b) =>
      Authorizations(a.rbacRoutes |+| b.rbacRoutes)
    }

  extension [F[_]](authRoute: AuthRoute[F])

    def restrictedTo(rbac: AuthRBAC[F]): Authorizations[F] =
      Authorizations(Map(rbac -> List(authRoute)))

  given auth2tsec[F[_]: Monad]: Conversion[Authorizations[F], TSecAuthService[User, JwtToken, F]] = auth =>
    val unauthorizedService: TSecAuthService[User, JwtToken, F] = TSecAuthService { _ =>
      Response[F](Status.Unauthorized).pure[F]
    }

    auth.rbacRoutes
      .toSeq
      .foldLeft(unauthorizedService) { case (acc, (rbac, routes)) =>
        val combinedRoute = routes.reduce(_ orElse _)
        TSecAuthService.withAuthorizationHandler(rbac)(combinedRoute, acc.run)
      }
