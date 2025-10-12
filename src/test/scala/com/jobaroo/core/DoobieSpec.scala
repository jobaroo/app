package com.jobaroo.core

import doobie.*
import doobie.util.*
import doobie.implicits.*
import cats.effect.*
import cats.effect.implicits.*
import cats.syntax.applicative.*
import doobie.hikari.HikariTransactor
import org.testcontainers.containers.PostgreSQLContainer

trait DoobieSpec(val initScript: String):

  val postgres: Resource[IO, PostgreSQLContainer[Nothing]] =
    val acquire = IO {
      val container = new PostgreSQLContainer("postgres")
      container.withInitScript(initScript)
      container.start()
      container
    }

    def release(container: PostgreSQLContainer[Nothing]) = IO(container.stop())
    Resource.make(acquire)(release)

  val transactor: Resource[IO, Transactor[IO]] =
    for
      db <- postgres
      ce <- ExecutionContexts.fixedThreadPool[IO](16)
      xa <- HikariTransactor.newHikariTransactor[IO](
              driverClassName = "org.postgresql.Driver",
              url = db.getJdbcUrl,
              user = db.getUsername,
              pass = db.getPassword,
              connectEC = ce
            )
    yield xa
