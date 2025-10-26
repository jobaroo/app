package com.jobaroo.modules

import cats.syntax.all.*
import cats.effect.*
import com.jobaroo.core.*
import doobie.util.transactor.Transactor
import org.typelevel.log4cats.Logger
import com.jobaroo.config.SecurityConfig
import com.jobaroo.config.TokenConfig
import com.jobaroo.config.EmailServiceConfig

final class Core[F[_]] private (val jobs: Jobs[F], val users: Users[F], val auth: Auth[F])

object Core:

  def apply[F[_] : Async : Logger](
    xa: Transactor[F],
    tokenConfig: TokenConfig,
    emailServiceConfig: EmailServiceConfig
  ): Resource[F, Core[F]] =
    val coreF =
      for
        jobs   <- LiveJobs[F](xa)
        users  <- LiveUsers[F](xa)
        tokens <- LiveTokens[F](users, xa, tokenConfig)
        emails <- LiveEmails[F](emailServiceConfig)
        auth   <- LiveAuth[F](users, emails, tokens)
      yield new Core[F](jobs, users, auth)

    Resource.eval(coreF)
