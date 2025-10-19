package com.jobaroo.modules

import cats.syntax.all.*
import cats.effect.*
import com.jobaroo.core.*
import doobie.util.transactor.Transactor
import org.typelevel.log4cats.Logger
import com.jobaroo.config.SecurityConfig

final class Core[F[_]] private (val jobs: Jobs[F], val auth: Auth[F])

object Core:

  def apply[F[_] : Async : Logger](xa: Transactor[F], securityConfig: SecurityConfig): Resource[F, Core[F]] =
    val coreF =
      for
        jobs  <- LiveJobs[F](xa)
        users <- LiveUsers[F](xa)
        auth  <- LiveAuth[F](users, securityConfig)
      yield new Core[F](jobs, auth)

    Resource.eval(coreF)
