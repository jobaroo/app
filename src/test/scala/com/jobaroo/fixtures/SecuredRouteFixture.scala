package com.jobaroo.fixtures

import cats.effect.*

import com.jobaroo.domain.security.Authenticator
import tsec.authentication.IdentityStore
import cats.data.OptionT
import com.jobaroo.domain.user.User
import tsec.authentication.JWTAuthenticator
import scala.concurrent.duration.*
import tsec.mac.jca.HMACSHA256
import com.jobaroo.domain.security.JwtToken
import tsec.jws.mac.JWTMac
import com.jobaroo.domain.security.Crypto
import org.http4s.*
import org.http4s.headers.*

trait SecuredRouteFixture extends UserFixture with JobFixture:

  val mockAuthenticator: Authenticator[IO] =
    val idStore: IdentityStore[IO, String, User] = (email: String) =>
      if email == jenniferLawrence.email then OptionT.pure(jenniferLawrence) else OptionT.none[IO, User]

    JWTAuthenticator.unbacked.inBearerToken(
      expiryDuration = 1.day,
      maxIdle = None,
      identityStore = idStore,
      signingKey = HMACSHA256.unsafeGenerateKey
    )

object SecuredRouteFixture:

  extension (r: Request[IO])

    def withBearerToken(jwtToken: JwtToken): Request[IO] = r.putHeaders {
      val jwtString = JWTMac.toEncodedString[IO, Crypto](jwtToken.jwt)
      Authorization(Credentials.Token(AuthScheme.Bearer, jwtString))
    }
