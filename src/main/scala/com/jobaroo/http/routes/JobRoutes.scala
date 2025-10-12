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
import com.jobaroo.core.Jobs
import com.jobaroo.domain.job.{Job, JobFilter, JobInfo}
import com.jobaroo.domain.pagination.Pagination
import com.jobaroo.http.response.FailureResponse
import org.typelevel.log4cats.Logger
import com.jobaroo.logging.syntax.*
import com.jobaroo.http.validation.syntax.*

import java.util.UUID
import scala.collection.mutable

class JobRoutes[F[_] : Concurrent : Logger] private (jobs: Jobs[F]) extends Http4sValidationDsl[F]:

  object OffsetQueryParam extends OptionalQueryParamDecoderMatcher[Int]("offset")
  object LimitQueryParam  extends OptionalQueryParamDecoderMatcher[Int]("limit")

  // TODO: POST /jobs?offset=xyz&limit=y { filters }
  private val allJobsRoute: HttpRoutes[F] = HttpRoutes.of[F] {
    case req @ POST -> Root :? LimitQueryParam(limit) +& OffsetQueryParam(offset) =>
      for
        filter <- req.as[JobFilter]
        allJobs <- jobs.all(filter, Pagination(limit, offset))
        resp    <- Ok(allJobs)
      yield resp
  }

  // TODO: GET /jobs/id
  private val findJobRoute: HttpRoutes[F] = HttpRoutes.of[F] {
    case GET -> Root / UUIDVar(id) => jobs.find(id).flatMap {
        case Some(job) => Ok(job)
        case None      => NotFound(FailureResponse(s"Job $id not found."))
      }
  }

  private val createJobRoute: HttpRoutes[F] = HttpRoutes.of[F] {
    case req @ POST -> Root / "create" =>
      req.validate[JobInfo] { jobInfo =>
        for
          jobId <- jobs.create("TODO", jobInfo)
          resp  <- Created(jobId)
        yield resp
      }
  }

  // TODO: PUT /jobs/id { jobInfo }
  private val updateJobRoute: HttpRoutes[F] = HttpRoutes.of[F] {
    case req @ PUT -> Root / UUIDVar(id) =>
      req.validate[JobInfo] { jobInfo =>
        for
          newJob <- jobs.update(id, jobInfo)
          resp   <- newJob match
                      case Some(_) => Ok()
                      case None    => NotFound(FailureResponse(s"Job $id not found."))
        yield resp
      }
  }

  // TODO: DELETE /jobs/id
  private val deleteJobRoute: HttpRoutes[F] = HttpRoutes.of[F] {
    case DELETE -> Root / UUIDVar(id) => jobs.find(id).flatMap {
        case Some(job) =>
          for
            _    <- jobs.delete(id)
            resp <- Ok()
          yield resp
        case None      => NotFound(FailureResponse(s"Job $id not found."))
      }
  }

  val routes = Router(
    "/jobs" -> (allJobsRoute <+> findJobRoute <+> createJobRoute <+> updateJobRoute <+> deleteJobRoute)
  )

object JobRoutes:
  def apply[F[_] : Concurrent : Logger](jobs: Jobs[F]): JobRoutes[F] = new JobRoutes[F](jobs)
