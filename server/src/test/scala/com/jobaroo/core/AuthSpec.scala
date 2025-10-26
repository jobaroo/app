package com.jobaroo.core

import cats.data.OptionT
import cats.effect.*
import doobie.*
import cats.syntax.all.*
import doobie.util.*
import doobie.postgres.implicits.*
import doobie.implicits.*
import com.jobaroo.domain.security.*
import cats.effect.testing.scalatest.AsyncIOSpec
import com.jobaroo.domain.user.*
import com.jobaroo.domain.auth.*
import com.jobaroo.fixtures.UserFixture
import org.scalatest.freespec.AsyncFreeSpec
import org.scalatest.matchers.should.Matchers
import org.typelevel.log4cats.Logger
import org.typelevel.log4cats.slf4j.Slf4jLogger
import tsec.authentication.JWTAuthenticator
import tsec.mac.jca.HMACSHA256
import tsec.authentication.IdentityStore
import tsec.passwordhashers.PasswordHash
import tsec.passwordhashers.jca.BCrypt

import concurrent.duration.*
import com.jobaroo.config.SecurityConfig

class AuthSpec extends AsyncFreeSpec with AsyncIOSpec with Matchers with UserFixture:

  given Logger[IO] = Slf4jLogger.getLogger[IO]

  val mockedTokens: Tokens[IO] = new Tokens[IO]:

    override def checkToken(email: String, token: String): IO[Boolean] =
      IO.pure(token == "abc")

    override def getToken(email: String): IO[Option[String]] =
      if email == christopherNolan.email then IO.pure(Some("abc")) else IO.pure(None)

  val mockedEmails: Emails[IO] = new Emails[IO]:

    override def send(to: String, subject: String, content: String): IO[Unit] = IO.unit
    override def sendPasswordRecovery(to: String, token: String): IO[Unit]    = IO.unit

  def probedEmails(users: Ref[IO, Set[String]]): Emails[IO] = new Emails[IO]:

    override def send(to: String, subject: String, content: String): IO[Unit] = users.update(_ + to) *> IO.unit
    override def sendPasswordRecovery(to: String, token: String): IO[Unit]    = send(to, "subject", "content")

  "Auth 'algebra'" - {
    "login should return None if the user doesn't exist" in {
      val program =
        for
          auth  <- LiveAuth[IO](mockedUsers, mockedEmails, mockedTokens)
          token <- auth.login("some@email.com", "test")
        yield token

      program.asserting { _ shouldBe None }
    }

    "login should return None if the user exists but the password is wrong" in {
      val program =
        for
          auth  <- LiveAuth[IO](mockedUsers, mockedEmails, mockedTokens)
          token <- auth.login(christopherNolan.email, "wrongPassword")
        yield token

      program.asserting { _ shouldBe None }
    }

    "login should return a token if the user exists and the password is correct" in {
      val program =
        for
          auth  <- LiveAuth[IO](mockedUsers, mockedEmails, mockedTokens)
          token <- auth.login(christopherNolan.email, "secret")
        yield token

      program.asserting { _ shouldBe defined }
    }

    "signing up should not create a user with an existing email" in {
      val program =
        for
          auth <- LiveAuth[IO](mockedUsers, mockedEmails, mockedTokens)
          newUserInfo = NewUserInfo(
                          email = christopherNolan.email,
                          password = christopherNolan.hashedPassword,
                          firstName = christopherNolan.firstName,
                          lastName = christopherNolan.lastName,
                          company = christopherNolan.company
                        )
          user <- auth.signUp(newUserInfo)
        yield user

      program.asserting { _ shouldBe None }
    }

    "signing up should create a new user" in {
      val newUserInfo = NewUserInfo(
        email = jenniferLawrence.email,
        password = "pwd",
        firstName = jenniferLawrence.firstName,
        lastName = jenniferLawrence.lastName,
        company = jenniferLawrence.company
      )

      val program =
        for
          auth <- LiveAuth[IO](mockedUsers, mockedEmails, mockedTokens)
          user <- auth.signUp(newUserInfo)
        yield user

      program.asserting {
        case None       => fail()
        case Some(user) =>
          user.email shouldBe newUserInfo.email
          user.firstName shouldBe newUserInfo.firstName
          user.lastName shouldBe newUserInfo.lastName
          user.company shouldBe newUserInfo.company
      }
    }

    "changing password should return None if the user doesn't exist" in {
      val program =
        for
          auth <- LiveAuth[IO](mockedUsers, mockedEmails, mockedTokens)
          user <- auth.changePassword("some@email.com", NewPasswordInfo("old", "new"))
        yield user

      program.asserting { _ shouldBe Right(None) }
    }

    "changing password should return Left with an error if the password is incorrect" in {
      val program =
        for
          auth <- LiveAuth[IO](mockedUsers, mockedEmails, mockedTokens)
          user <- auth.changePassword(christopherNolan.email, NewPasswordInfo("old", "new"))
        yield user

      program.asserting { _ shouldBe Left("Invalid password") }
    }

    "changing password should correctly change password if all details are correct" in {
      val program =
        for
          auth      <- LiveAuth[IO](mockedUsers, mockedEmails, mockedTokens)
          user      <- auth.changePassword(christopherNolan.email, NewPasswordInfo("secret", "new"))
          isCorrect <- user match
                         case Right(Some(value)) =>
                           BCrypt.checkpwBool[IO]("new", PasswordHash[BCrypt](value.hashedPassword))
                         case _                  => IO.pure(false)
        yield isCorrect

      program.asserting { _ shouldBe true }
    }

    "recoverPassword should fail for a user that does not exists, even if the token is correct" in {
      val program =
        for
          auth <- LiveAuth[IO](mockedUsers, mockedEmails, mockedTokens)
          res  <- auth.recoverPasswordFromToken("someone@gmail.com", "abc", "new_password")
        yield res

      program.asserting { _ shouldBe false }
    }

    "recoverPassword should fail for a user that exists, but the token is wrong" in {
      val program =
        for
          auth <- LiveAuth[IO](mockedUsers, mockedEmails, mockedTokens)
          res  <- auth.recoverPasswordFromToken(christopherNolan.email, "wrong_token", "new_password")
        yield res

      program.asserting { _ shouldBe false }
    }

    "recoverPassword should succeed for a user that exists and the token is correct" in {
      val program =
        for
          auth <- LiveAuth[IO](mockedUsers, mockedEmails, mockedTokens)
          res  <- auth.recoverPasswordFromToken(christopherNolan.email, "abc", "new_password")
        yield res

      program.asserting { _ shouldBe true }
    }

    "sending recovery passwords should fail for a user that doesn't exist" in {
      val program =
        for
          set <- IO.ref(Set.empty[String])
          emails = probedEmails(set)
          auth <- LiveAuth[IO](mockedUsers, emails, mockedTokens)
          res  <- auth.sendPasswordRecoveryToken("some@one.gmail.com")
          res  <- set.get
        yield res

      program.asserting { _ shouldBe empty }
    }
    
    "sending recovery passwords should succeed for a user that exist" in {
      val program =
        for
          set <- IO.ref(Set.empty[String])
          emails = probedEmails(set)
          auth <- LiveAuth[IO](mockedUsers, emails, mockedTokens)
          res  <- auth.sendPasswordRecoveryToken(christopherNolan.email)
          res  <- set.get
        yield res

      program.asserting { _ should contain(christopherNolan.email) }
    }

  }
