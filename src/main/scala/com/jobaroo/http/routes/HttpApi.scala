package com.jobaroo.http.routes

import cats.*
import cats.effect.Concurrent
import cats.implicits.*
import org.http4s.*
import org.http4s.dsl.*
import org.http4s.dsl.impl.*
import org.http4s.server.*

class HttpApi[F[_]: Concurrent] private:

  private val healthRoutes = HealthRoutes[F].routes
  private val jobRoutes    = JobRoutes[F].routes

  val endpoints = Router(
    "/api" -> (healthRoutes <+> jobRoutes)
  )

object HttpApi:
  def apply[F[_]: Concurrent]: HttpApi[F] = new HttpApi[F]
