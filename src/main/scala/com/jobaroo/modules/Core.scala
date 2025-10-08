package com.jobaroo.modules

import cats.effect.*
import doobie.util.ExecutionContexts
import doobie.hikari.HikariTransactor
import com.jobaroo.core.*

final class Core[F[_]] private (val jobs: Jobs[F])

object Core:

  private def postgresResource[F[_] : Async]: Resource[F, HikariTransactor[F]] =
    for
      ec <- ExecutionContexts.fixedThreadPool(32)
      xa <- HikariTransactor.newHikariTransactor[F](
        driverClassName = "org.postgresql.Driver",
        url = "jdbc:postgresql:board",
        user = "docker",
        pass = "docker",
        connectEC = ec
      )
    yield xa


  def apply[F[_] : Async]: Resource[F, Core[F]] =
    postgresResource[F]
      .evalMap(LiveJobs[F](_))
      .map(new Core[F](_))
