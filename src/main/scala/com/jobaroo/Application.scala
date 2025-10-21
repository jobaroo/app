package com.jobaroo

import cats.*
import cats.implicits.*
import org.http4s.*
import cats.effect.*
import com.jobaroo.config.{AppConfig, EmberConfig}
import com.jobaroo.http.routes.HealthRoutes
import com.jobaroo.modules.*
import org.http4s.dsl.*
import org.http4s.dsl.impl.*
import org.http4s.ember.server.EmberServerBuilder
import org.http4s.server.*
import pureconfig.ConfigSource
import pureconfig.error.ConfigReaderException
import org.typelevel.log4cats.Logger
import org.typelevel.log4cats.slf4j.Slf4jLogger

case object Application extends IOApp.Simple:

  import com.jobaroo.config.syntax.*

  given Logger[IO] = Slf4jLogger.getLogger[IO]

  override def run: IO[Unit] = ConfigSource.default.loadF[IO, AppConfig].flatMap {
    case AppConfig(postgresConfig, emberConfig, securityConfig) =>
      val app =
        for
          xa      <- Database.makePostgresResource[IO](postgresConfig)
          core    <- Core[IO](xa)
          httpApi <- HttpApi[IO](core, securityConfig)
          server  <- EmberServerBuilder
                       .default[IO]
                       .withHost(emberConfig.host)
                       .withPort(emberConfig.port)
                       .withHttpApp(httpApi.endpoints.orNotFound)
                       .build
        yield server

      app.use(_ => IO.println("jobaroo is online") *> IO.never)
  }
