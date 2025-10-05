package com.jobaroo.http.routes

import io.circe.generic.auto.*
import org.http4s.circe.CirceEntityCodec.*
import org.http4s.*
import org.http4s.dsl.*
import org.http4s.dsl.impl.*
import org.http4s.server.*
import cats.*
import cats.implicits.*
import cats.effect.*
import com.jobaroo.domain.job.{Job, JobInfo}
import com.jobaroo.http.response.FailureResponse
import org.typelevel.log4cats.Logger
import com.jobaroo.logging.syntax.*

import java.util.UUID
import scala.collection.mutable

class JobRoutes[F[_] : Concurrent : Logger] private extends Http4sDsl[F]:

  private val database: mutable.Map[UUID, Job] = mutable.Map.empty

  // TODO: POST /jobs?offset=xyz&limit=y { filters }
  private val allJobsRoute: HttpRoutes[F] = HttpRoutes.of[F] {
    case POST -> Root => Ok(database.values)
  }

  // TODO: GET /jobs/id
  private val findJobRoute: HttpRoutes[F] = HttpRoutes.of[F] {
    case GET -> Root / UUIDVar(id) => database.get(id) match
        case Some(job) => Ok(job)
        case None      => NotFound(FailureResponse(s"Job $id not found."))
  }

  // TODO: POST /jobs/create { jobInfo }
  private def createJob(jobInfo: JobInfo): F[Job] =
    Job(
      id = UUID.randomUUID(), // TODO: Java uses UUID v4, change them to v6
      date = System.currentTimeMillis(),
      ownerEmail = "TODO@jobaroo.com",
      jobInfo = jobInfo
    ).pure[F]

  private val createJobRoute: HttpRoutes[F] = HttpRoutes.of[F] {
    case req @ POST -> Root / "create" =>
      for
        jobInfo <- req.as[JobInfo].logError(e => s"payload parsing has failed: $e")
        job     <- createJob(jobInfo)
        _       <- database.put(job.id, job).pure[F]
        resp    <- Created(job.id)
      yield resp
  }

  // TODO: PUT /jobs/id { jobInfo }
  private val updateJobRoute: HttpRoutes[F] = HttpRoutes.of[F] {
    case req @ PUT -> Root / UUIDVar(id) => database.get(id) match
        case Some(job) =>
          for
            newJobInfo <- req.as[JobInfo]
            _          <- database.put(id, job.copy(jobInfo = newJobInfo)).pure[F]
            resp       <- Ok()
          yield resp
        case None      => NotFound(FailureResponse(s"Job $id not found."))
  }

  // TODO: DELETE /jobs/id
  private val deleteJobRoute: HttpRoutes[F] = HttpRoutes.of[F] {
    case DELETE -> Root / UUIDVar(id) => database.get(id) match
        case Some(job) =>
          for
            _    <- database.remove(id).pure[F]
            resp <- Ok()
          yield resp
        case None      => NotFound(FailureResponse(s"Job $id not found."))
  }

  val routes = Router(
    "/jobs" -> (allJobsRoute <+> findJobRoute <+> createJobRoute <+> updateJobRoute <+> deleteJobRoute)
  )

object JobRoutes:
  def apply[F[_] : Concurrent : Logger]: JobRoutes[F] = new JobRoutes[F]
