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

  private val mockedUsers: Users[IO] = new Users[IO]:

    override def find(email: String): IO[Option[User]] =
      if email == christopherNolan.email then IO.pure(christopherNolan.some) else IO.pure(None)

    override def create(user: User): IO[String]       = IO.pure(user.email)
    override def update(user: User): IO[Option[User]] = IO.pure(user.some)
    override def delete(email: String): IO[Boolean]   = IO.pure(true)
    
  "Auth 'algebra'" - {
    "login should return None if the user doesn't exist" in {
      val program =
        for
          auth  <- LiveAuth[IO](mockedUsers)
          token <- auth.login("some@email.com", "test")
        yield token

      program.asserting { _ shouldBe None }
    }

    "login should return None if the user exists but the password is wrong" in {
      val program =
        for
          auth  <- LiveAuth[IO](mockedUsers)
          token <- auth.login(christopherNolan.email, "wrongPassword")
        yield token

      program.asserting { _ shouldBe None }
    }

    "login should return a token if the user exists and the password is correct" in {
      val program =
        for
          auth  <- LiveAuth[IO](mockedUsers)
          token <- auth.login(christopherNolan.email, "secret")
        yield token

      program.asserting { _ shouldBe defined }
    }

    "signing up should not create a user with an existing email" in {
      val program =
        for
          auth <- LiveAuth[IO](mockedUsers)
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
          auth <- LiveAuth[IO](mockedUsers)
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
          auth <- LiveAuth[IO](mockedUsers)
          user <- auth.changePassword("some@email.com", NewPasswordInfo("old", "new"))
        yield user

      program.asserting { _ shouldBe Right(None) }
    }

    "changing password should return Left with an error if the password is incorrect" in {
      val program =
        for
          auth <- LiveAuth[IO](mockedUsers)
          user <- auth.changePassword(christopherNolan.email, NewPasswordInfo("old", "new"))
        yield user

      program.asserting { _ shouldBe Left("Invalid password") }
    }

    "changing password should correctly change password if all details are correct" in {
      val program =
        for
          auth      <- LiveAuth[IO](mockedUsers)
          user      <- auth.changePassword(christopherNolan.email, NewPasswordInfo("secret", "new"))
          isCorrect <- user match
                         case Right(Some(value)) =>
                           BCrypt.checkpwBool[IO]("new", PasswordHash[BCrypt](value.hashedPassword))
                         case _                  => IO.pure(false)
        yield isCorrect

      program.asserting { _ shouldBe true }
    }

  }
