package com.jobaroo.logging

import cats.MonadError
import cats.implicits.*
import org.typelevel.log4cats.*

object syntax:

  extension [F[_], E, A](fa: F[A])(using monadError: MonadError[F, E], logger: Logger[F])

    def log(success: A => String, error: E => String): F[A] = fa.attemptTap {
      case Left(e)  => logger.error(error(e))
      case Right(a) => logger.info(success(a))
    }

    def logError(error: E => String): F[A] = fa.attemptTap {
      case Left(e)  => logger.error(error(e))
      case Right(a) => ().pure[F]
    }
