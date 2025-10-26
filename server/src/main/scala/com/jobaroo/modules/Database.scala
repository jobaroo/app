package com.jobaroo.modules

import cats.effect.*
import com.jobaroo.config.PostgresConfig
import doobie.util.ExecutionContexts
import doobie.hikari.HikariTransactor

case object Database:

  def makePostgresResource[F[_]: Async](postgresConfig: PostgresConfig): Resource[F, HikariTransactor[F]] =
    for
      ec <- ExecutionContexts.fixedThreadPool(postgresConfig.nThreads)
      xa <- HikariTransactor.newHikariTransactor[F](
              driverClassName = "org.postgresql.Driver",
              url = postgresConfig.url,
              user = postgresConfig.user,
              pass = postgresConfig.password,
              connectEC = ec
            )
    yield xa
