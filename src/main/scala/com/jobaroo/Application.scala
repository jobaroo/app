package com.jobaroo

import cats.*
import cats.implicits.*
import org.http4s.*
import cats.effect.*
import com.jobaroo.config.EmberConfig
import com.jobaroo.http.routes.HealthRoutes
import org.http4s.dsl.*
import org.http4s.dsl.impl.*
import org.http4s.ember.server.EmberServerBuilder
import org.http4s.server.*
import pureconfig.ConfigSource
import pureconfig.error.ConfigReaderException

case object Application extends IOApp.Simple:

  import com.jobaroo.config.syntax.*

  override def run: IO[Unit] = ConfigSource.default.loadF[IO, EmberConfig].flatMap { config =>
    EmberServerBuilder
      .default[IO]
      .withHost(config.host)
      .withPort(config.port)
      .withHttpApp(HealthRoutes[IO].routes.orNotFound)
      .build
      .use(_ => IO.println("jobaroo is online") *> IO.never)
  }
