package com.jobaroo.modules

import cats.syntax.all.*
import cats.effect.*
import com.jobaroo.core.*
import doobie.util.transactor.Transactor
import org.typelevel.log4cats.Logger
import com.jobaroo.config.SecurityConfig
import com.jobaroo.config.TokenConfig
import com.jobaroo.config.EmailServiceConfig
import com.jobaroo.config.StripeConfig

final class Core[F[_]] private (val jobs: Jobs[F], val users: Users[F], val auth: Auth[F], val stripe: Stripe[F])

object Core:

  def apply[F[_] : Async : Logger](
    xa: Transactor[F],
    tokenConfig: TokenConfig,
    emailServiceConfig: EmailServiceConfig,
    stripeConfig: StripeConfig
  ): Resource[F, Core[F]] =
    val coreF =
      for
        jobs   <- LiveJobs[F](xa)
        users  <- LiveUsers[F](xa)
        tokens <- LiveTokens[F](users, xa, tokenConfig)
        emails <- LiveEmails[F](emailServiceConfig)
        auth   <- LiveAuth[F](users, emails, tokens)
        stripe <- LiveStripe[F](stripeConfig)
      yield new Core[F](jobs, users, auth, stripe)

    Resource.eval(coreF)
