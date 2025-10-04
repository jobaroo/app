package com.jobaroo.config

import cats.MonadThrow
import cats.implicits.*
import pureconfig.error.ConfigReaderException
import pureconfig.{ConfigReader, ConfigSource}

import scala.reflect.ClassTag

object syntax:

  extension (source: ConfigSource)

    def loadF[F[_], A : ConfigReader : ClassTag](using F: MonadThrow[F]): F[A] =
      F.pure(source.load[A]).flatMap {
        case Left(errors)  => F.raiseError[A](ConfigReaderException(errors))
        case Right(config) => F.pure(config) 
      }
