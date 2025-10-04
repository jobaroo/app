package com.jobaroo.http.routes

import org.http4s.*
import org.http4s.dsl.*
import org.http4s.dsl.impl.*
import org.http4s.server.*
import cats.*
import cats.implicits.*

class JobRoutes[F[_]: Monad] private extends Http4sDsl[F]:

  // TODO: POST /jobs?offset=xyz&limit=y { filters }
  private val allJobsRoute: HttpRoutes[F] = HttpRoutes.of[F] {
    case POST -> Root => Ok("TODO")
  }

  // TODO: GET /jobs/id
  private val findJobRoute: HttpRoutes[F] = HttpRoutes.of[F] {
    case GET -> Root / UUIDVar(id) => Ok("TODO")
  }

  // TODO: POST /jobs/create { jobInfo }
  private val createJobRoute: HttpRoutes[F] = HttpRoutes.of[F] {
    case POST -> Root / "create" => Ok("TODO")
  }

  // TODO: PUT /jobs/id { jobInfo }
  private val updateJobRoute: HttpRoutes[F] = HttpRoutes.of[F] {
    case PUT -> Root / UUIDVar(id) => Ok("TODO")
  }

  // TODO: DELETE /jobs/id
  private val deleteJobRoute: HttpRoutes[F] = HttpRoutes.of[F] {
    case DELETE -> Root / UUIDVar(id) => Ok("TODO")
  }

  val routes = Router(
    "/jobs" -> (allJobsRoute <+> findJobRoute <+> createJobRoute <+> updateJobRoute <+> deleteJobRoute)
  )

object JobRoutes:
  def apply[F[_]: Monad]: JobRoutes[F] = new JobRoutes[F]
