package com.jobaroo.http.routes

import cats.*
import cats.implicits.*
import org.http4s.*
import org.http4s.dsl.*
import org.http4s.dsl.impl.*
import org.http4s.server.*

class HttpApi[F[_]: Monad] private:

  private val healthRoutes = HealthRoutes[F].routes
  private val jobRoutes    = JobRoutes[F].routes

  val endpoints = Router(
    "/api" -> (healthRoutes <+> jobRoutes)
  )

object HttpApi:
  def apply[F[_]: Monad]: HttpApi[F] = new HttpApi[F]
