package com.jobaroo.core

import cats.effect.*
import doobie.*
import doobie.util.*
import doobie.postgres.implicits.*
import com.jobaroo.domain.user.*
import doobie.implicits.*
import cats.effect.testing.scalatest.AsyncIOSpec
import com.jobaroo.fixtures.UserFixture
import org.postgresql.util.PSQLException
import org.scalatest.Inside
import org.scalatest.freespec.AsyncFreeSpec
import org.scalatest.matchers.should.Matchers
import org.typelevel.log4cats.Logger
import org.typelevel.log4cats.slf4j.Slf4jLogger

class UsersSpec extends AsyncFreeSpec with AsyncIOSpec with Matchers with Inside with DoobieSpec("sql/users.sql")
    with UserFixture:

  given Logger[IO] = Slf4jLogger.getLogger[IO]

  "Users 'algebra'" - {
    "should retrieve a user by email" in {
      transactor.use { xa =>
        val program =
          for
            users <- LiveUsers[IO](xa)
            user  <- users.find("christopher@nolan.com")
          yield user

        program.asserting { _ shouldBe Some(christopherNolan) }
      }
    }

    "should return None if the email doesn't exist" in {
      transactor.use { xa =>
        val program =
          for
            users <- LiveUsers[IO](xa)
            user  <- users.find("some@email.com")
          yield user

        program.asserting { _ shouldBe None }
      }
    }

    "should create a new user" in {
      transactor.use { xa =>
        val program =
          for
            users  <- LiveUsers[IO](xa)
            userId <- users.create(jenniferLawrence)
          yield userId

        program.asserting { _ shouldBe jenniferLawrence.email }
      }
    }

    "should fail creating a new user if the email already exists" in {
      transactor.use { xa =>
        val program =
          for
            users  <- LiveUsers[IO](xa)
            userId <- users.create(johnnyDepp).attempt
          yield userId

        program.asserting { outcome =>
          inside(outcome) {
            case Left(e) => e shouldBe a[PSQLException]
            case _       => fail()
          }
        }
      }
    }

    "should return None when updating a user that does not exists" in {
      transactor.use { xa =>
        val program =
          for
            users <- LiveUsers[IO](xa)
            user  <- users.update(jenniferLawrence)
          yield user

        program.asserting { _ shouldBe None }
      }
    }

    "should update an existent user" in {
      transactor.use { xa =>
        val program =
          for
            users <- LiveUsers[IO](xa)
            user  <- users.update(johnnyDepp.copy(hashedPassword = "!"))
          yield user

        program.asserting { _ shouldBe Some(johnnyDepp.copy(hashedPassword = "!")) }
      }
    }

    "should delete a user" in {
      transactor.use { xa =>
        val program =
          for
            users <- LiveUsers[IO](xa)
            res   <- users.delete(christopherNolan.email)
          yield res

        program.asserting { _ shouldBe true }
      }
    }

    "should not delete a user that does not exist" in {
      transactor.use { xa =>
        val program =
          for
            users <- LiveUsers[IO](xa)
            res   <- users.delete("some@one.com")
          yield res

        program.asserting { _ shouldBe false }
      }
    }

  }
