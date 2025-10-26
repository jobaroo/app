package com.jobaroo.modules

import cats.*
import cats.effect.Concurrent
import cats.implicits.*
import cats.effect.Resource
import com.jobaroo.http.routes.{HealthRoutes, JobRoutes}
import org.http4s.*
import org.http4s.dsl.*
import org.http4s.dsl.impl.*
import org.http4s.server.*
import org.typelevel.log4cats.Logger
import com.jobaroo.http.routes.AuthRoutes
import tsec.common.SecureRandomId
import com.jobaroo.domain.security.JwtToken
import cats.effect.kernel.Async
import cats.data.OptionT
import com.jobaroo.config.SecurityConfig
import com.jobaroo.core.Users
import tsec.authentication.BackingStore
import tsec.mac.jca.HMACSHA256
import cats.effect.kernel.Ref
import com.jobaroo.domain.security.*
import tsec.authentication.JWTAuthenticator
import tsec.authentication.SecuredRequestHandler

class HttpApi[F[_] : Concurrent : Logger] private (core: Core[F], authenticator: Authenticator[F]):

  given securedHandler: SecuredHandler[F] = SecuredRequestHandler(authenticator)
  private val healthRoutes                = HealthRoutes[F].routes
  private val jobRoutes                   = JobRoutes[F](core.jobs).routes
  private val authRoutes                  = AuthRoutes[F](core.auth, authenticator).routes

  val endpoints = Router(
    "/api" -> (healthRoutes <+> jobRoutes <+> authRoutes)
  )

object HttpApi:

  def makeAuthenticator[F[_] : Async : Logger](users: Users[F], securityConfig: SecurityConfig): F[Authenticator[F]] =
    val refF: F[Ref[F, Map[SecureRandomId, JwtToken]]] = Ref.of[F, Map[SecureRandomId, JwtToken]](Map.empty)
    val keyF                                           = HMACSHA256.buildKey[F](securityConfig.secret.getBytes("UTF-8"))

    for
      ref <- refF
      tokenStore = new BackingStore[F, SecureRandomId, JwtToken]:

                     override def delete(id: SecureRandomId): F[Unit]           = ref.modify(store => (store - id, ()))
                     override def get(id: SecureRandomId): OptionT[F, JwtToken] = OptionT(ref.get.map(_.get(id)))
                     override def update(e: JwtToken): F[JwtToken]              = put(e)
                     override def put(e: JwtToken): F[JwtToken]                 = ref.modify(store => (store + (e.id -> e), e))

      key <- keyF
      authenticator = JWTAuthenticator.backed.inBearerToken(
                        expiryDuration = securityConfig.jwtExpiryDuration,
                        maxIdle = None,
                        tokenStore = tokenStore,
                        identityStore = email => OptionT(users.find(email)),
                        signingKey = key
                      )
    yield authenticator

  def apply[F[_] : Async : Logger](core: Core[F], securityConfig: SecurityConfig): Resource[F, HttpApi[F]] =
    Resource.eval(makeAuthenticator(core.users, securityConfig)).map(new HttpApi[F](core, _))
