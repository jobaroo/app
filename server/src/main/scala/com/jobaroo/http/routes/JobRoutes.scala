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
import org.typelevel.ci.CIStringSyntax
import tsec.authentication.asAuthed
import com.jobaroo.domain.security.*
import com.jobaroo.core.Jobs
import com.jobaroo.domain.job.{Job, JobFilter, JobInfo}
import com.jobaroo.domain.pagination.Pagination
import com.jobaroo.http.response.FailureResponse
import org.typelevel.log4cats.Logger
import com.jobaroo.logging.syntax.*
import com.jobaroo.http.validation.syntax.*

import java.util.UUID
import scala.collection.mutable
import com.jobaroo.domain.security.Authenticator
import com.jobaroo.domain.security.adminOnly
import tsec.authentication.SecuredRequestHandler

import scala.language.implicitConversions
import com.jobaroo.core.Stripe

class JobRoutes[F[_] : Concurrent : Logger : SecuredHandler] private (jobs: Jobs[F], stripe: Stripe[F])
  extends Http4sValidationDsl[F]:

  object OffsetQueryParam extends OptionalQueryParamDecoderMatcher[Int]("offset")
  object LimitQueryParam  extends OptionalQueryParamDecoderMatcher[Int]("limit")

  private val allFiltersRoute: HttpRoutes[F] = HttpRoutes.of[F] {
    case req @ GET -> Root / "filters" =>
      for
        jobFilter <- jobs.possibleFilters()
        resp      <- Ok(jobFilter)
      yield resp
  }

  private val allJobsRoute: HttpRoutes[F] = HttpRoutes.of[F] {
    case req @ POST -> Root :? LimitQueryParam(limit) +& OffsetQueryParam(offset) =>
      for
        filter  <- req.as[JobFilter]
        allJobs <- jobs.all(filter, Pagination(limit, offset))
        resp    <- Ok(allJobs)
      yield resp
  }

  private val findJobRoute: HttpRoutes[F] = HttpRoutes.of[F] {
    case GET -> Root / UUIDVar(id) => jobs.find(id).flatMap {
        case Some(job) => Ok(job)
        case None      => NotFound(FailureResponse(s"Job $id not found."))
      }
  }

  private val createJobRoute: AuthRoute[F] = {
    case req @ POST -> Root / "create" asAuthed user =>
      req.request.validate[JobInfo] { jobInfo =>
        for
          jobId <- jobs.create(user.email, jobInfo)
          resp  <- Created(jobId)
        yield resp
      }
  }

  private val updateJobRoute: AuthRoute[F] = {
    case req @ PUT -> Root / UUIDVar(id) asAuthed user =>
      req.request.validate[JobInfo] { jobInfo =>
        jobs.find(id).flatMap {
          case Some(oldJob) if user.owns(oldJob) || user.isAdmin => jobs.update(id, jobInfo) *> Ok()
          case None                                              => NotFound(FailureResponse(s"Job $id not found."))
          case _                                                 => Forbidden("You can only update your jobs")
        }
      }
  }

  private val deleteJobRoute: AuthRoute[F] = {
    case DELETE -> Root / UUIDVar(id) asAuthed user =>
      jobs.find(id).flatMap {
        case Some(job) if user.owns(job) || user.isAdmin => jobs.delete(id) *> Ok()
        case None                                        => NotFound(FailureResponse(s"Job $id not found."))
        case _                                           => Forbidden("You can only delete your jobs")
      }
  }

  private val promotedJobRoute: AuthRoute[F] = {
    case req @ POST -> Root / "promoted" asAuthed user =>
      req.request.validate[JobInfo] { jobInfo =>
        for
          jobId   <- jobs.create(user.email, jobInfo)
          session <- stripe.createCheckoutSession(jobId.toString, user.email)
          resp    <- session.fold(NotFound())(sess => Ok(sess.getUrl))
        yield resp
      }
  }

  private val promotedJobWebhook: HttpRoutes[F] = HttpRoutes.of[F] {
    case req @ POST -> Root / "webhook" =>
      req.headers.get(ci"Stripe-Signature").flatMap(_.toList.headOption).map(_.value) match
        case None                  => Logger[F].info("Webhook event with no Stripe signature") *> Forbidden("No Stripe signature")
        case Some(stripeSignature) =>
          for
            payload <- req.bodyText.compile.string
            res     <- stripe.handleWebhookEvent(payload, stripeSignature, jobId => jobs.activate(UUID.fromString(jobId)))
            resp    <- res.fold(NoContent())(_ => Ok())
          yield resp
  }

  private val unauthedRoutes = allJobsRoute <+> findJobRoute <+> allFiltersRoute <+> promotedJobWebhook

  private val authedRoutes = SecuredHandler[F].liftService(
    promotedJobRoute.restrictedTo(allRoles) |+| createJobRoute.restrictedTo(adminOnly) |+|
      updateJobRoute.restrictedTo(allRoles) |+| deleteJobRoute.restrictedTo(allRoles)
  )

  val routes = Router(
    "/jobs" -> (unauthedRoutes <+> authedRoutes)
  )

object JobRoutes:

  def apply[F[_] : Concurrent : Logger : SecuredHandler](jobs: Jobs[F], stripe: Stripe[F]): JobRoutes[F] =
    new JobRoutes[F](jobs, stripe)
