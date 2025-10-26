package com.jobaroo.core

import cats.effect.*
import doobie.*
import doobie.util.*
import doobie.postgres.implicits.*
import doobie.implicits.*
import cats.effect.testing.scalatest.AsyncIOSpec
import com.jobaroo.domain.pagination.Pagination
import org.scalatest.freespec.AsyncFreeSpec
import org.scalatest.matchers.should.Matchers
import org.typelevel.log4cats.Logger
import org.typelevel.log4cats.slf4j.Slf4jLogger
import com.jobaroo.fixtures.UserFixture
import com.jobaroo.config.TokenConfig

import scala.concurrent.duration.*

class TokensSpec extends AsyncFreeSpec with AsyncIOSpec with Matchers with UserFixture with DoobieSpec("sql/tokens.sql"):

  given Logger[IO]             = Slf4jLogger.getLogger[IO]
  val tokenConfig: TokenConfig = TokenConfig(1_000_000L)

  "Tokens 'algebra'" - {

    "should not create a new token for a non-existing user" in {
      transactor.use { xa =>
        val program =
          for
            tokens <- LiveTokens[IO](mockedUsers, xa, tokenConfig)
            token  <- tokens.getToken("not@present.com")
          yield token

        program.asserting { _ shouldBe None }
      }
    }

    "should create a new token for an existing user" in {
      transactor.use { xa =>
        val program =
          for
            tokens <- LiveTokens[IO](mockedUsers, xa, tokenConfig)
            token  <- tokens.getToken(christopherNolan.email)
          yield token

        program.asserting { _ shouldBe defined }
      }
    }

    "should not validate an expired token" in {
      transactor.use { xa =>
        val program =
          for
            tokens       <- LiveTokens[IO](mockedUsers, xa, tokenConfig.copy(tokenDuration = 100L))
            optToken     <- tokens.getToken(christopherNolan.email)
            _            <- IO.sleep(500.millis)
            isValidToken <- optToken.fold(IO.pure(false))(tokens.checkToken(christopherNolan.email, _))
          yield isValidToken

        program.asserting { _ shouldBe false }
      }
    }

    "should validate a token that has not yet expired" in {
      transactor.use { xa =>
        val program =
          for
            tokens       <- LiveTokens[IO](mockedUsers, xa, tokenConfig)
            optToken     <- tokens.getToken(christopherNolan.email)
            _            <- IO.sleep(500.millis)
            isValidToken <- optToken.fold(IO.pure(false))(tokens.checkToken(christopherNolan.email, _))
          yield isValidToken

        program.asserting { _ shouldBe true }
      }
    }

    "should only validate a token for the user that generated them" in {
      transactor.use { xa =>
        val program =
          for
            tokens       <- LiveTokens[IO](mockedUsers, xa, tokenConfig)
            optToken     <- tokens.getToken(christopherNolan.email)
            _            <- IO.sleep(500.millis)
            isValidToken <- optToken.fold(IO.pure(false))(tokens.checkToken("some@email.com", _))
          yield isValidToken

        program.asserting { _ shouldBe false }
      }
    }

  }
