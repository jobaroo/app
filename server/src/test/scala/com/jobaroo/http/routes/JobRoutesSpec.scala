package com.jobaroo.http.routes

import io.circe.generic.auto.*
import org.http4s.circe.CirceEntityCodec.*
import org.http4s.*
import org.http4s.implicits.*
import org.http4s.dsl.*
import org.http4s.dsl.impl.*
import cats.effect.testing.scalatest.AsyncIOSpec
import com.jobaroo.fixtures.{JobFixture, SecuredRouteFixture}
import com.jobaroo.fixtures.SecuredRouteFixture.withBearerToken
import org.http4s.dsl.Http4sDsl
import org.scalatest.freespec.AsyncFreeSpec
import org.scalatest.matchers.should.Matchers
import cats.effect.*
import cats.implicits.*
import com.jobaroo.core.Jobs
import com.jobaroo.domain.job.*
import com.jobaroo.domain.pagination.*
import com.jobaroo.domain.job.Job
import org.http4s.HttpRoutes
import org.typelevel.log4cats.Logger
import org.typelevel.log4cats.slf4j.Slf4jLogger
import com.stripe.model.checkout.Session
import com.stripe.param.checkout.SessionCreateParams

import java.util.UUID
import com.jobaroo.core.LiveStripe
import com.jobaroo.config.StripeConfig
import com.jobaroo.core.Stripe
import com.stripe.model.checkout.Session
import java.{util => ju}

class JobRoutesSpec extends AsyncFreeSpec with AsyncIOSpec with Matchers with Http4sDsl[IO] with JobFixture
    with SecuredRouteFixture:

  ////////////////////////////////////////////////////////////////////////////////////
  // prep
  ////////////////////////////////////////////////////////////////////////////////////

  private val mockJobs: Jobs[IO] = new Jobs[IO]:

    override def activate(id: ju.UUID): IO[Int] = IO.pure(1)
    
    override def create(ownerEmail: String, jobInfo: JobInfo): IO[UUID] = IO.pure(berlinTechLeadJobId)

    override def update(id: UUID, jobInfo: JobInfo): IO[Option[Job]] =
      IO.pure(Option.when(id == berlinTechLeadJobId)(remoteSoftwareEngineerJob))

    override def delete(id: UUID): IO[Int] =
      IO.pure(if id == berlinTechLeadJobId then 1 else 0)

    override def find(id: UUID): IO[Option[Job]] =
      IO.pure(Option.when(id == berlinTechLeadJobId)(berlinTechLeadJob))

    override def all(): IO[List[Job]] = IO.pure(berlinTechLeadJob :: Nil)

    override def all(filter: JobFilter, pagination: Pagination): IO[List[Job]] =
      if filter.remote then IO.pure(Nil) else IO.pure(berlinTechLeadJob :: Nil)

    override def possibleFilters(): IO[JobFilter] = IO.pure(JobFilter())

  val mockStripe = new Stripe[IO]:
    override def createCheckoutSession(jobId: String, userEmail: String): IO[Option[Session]] = 
      IO.pure(Some(Session.create(SessionCreateParams.builder().build())))
      
    override def handleWebhookEvent[A](payload: String, signature: String, action: String => IO[A]): IO[Option[A]] =
      IO.pure(None)

  ////////////////////////////////////////////////////////////////////////////////////
  // tests
  ////////////////////////////////////////////////////////////////////////////////////

  given Logger[IO]              = Slf4jLogger.getLogger[IO]
  val jobRoutes: HttpRoutes[IO] = JobRoutes[IO](mockJobs, mockStripe).routes

  "JobRoutes" - {
    "should return a job with a given id" in {
      for
        resp <-
          jobRoutes.orNotFound.run(Request(method = Method.GET, uri = uri"/jobs/843df718-ec6e-4d49-9289-f799c0f40064"))
        job  <- resp.as[Job]
      yield
        resp.status shouldBe Status.Ok
        job shouldBe berlinTechLeadJob
    }

    "should return all jobs" in {
      for
        resp <- jobRoutes.orNotFound.run(Request(method = Method.POST, uri = uri"/jobs").withEntity(JobFilter()))
        jobs <- resp.as[List[Job]]
      yield
        resp.status shouldBe Status.Ok
        jobs shouldBe List(berlinTechLeadJob)
    }

    "should return all jobs that satisfy a filter" in {
      for
        resp <-
          jobRoutes.orNotFound.run(Request(method = Method.POST, uri = uri"/jobs").withEntity(JobFilter(remote = true)))
        jobs <- resp.as[List[Job]]
      yield
        resp.status shouldBe Status.Ok
        jobs shouldBe Nil
    }

    "should create a new job" in {
      for
        jwtToken <- mockAuthenticator.create(jenniferLawrence.email)
        resp     <- jobRoutes.orNotFound.run(
                      Request[IO](method = Method.POST, uri = uri"/jobs/create")
                        .withEntity(berlinTechLeadJobInfo)
                        .withBearerToken(jwtToken)
                    )
        id       <- resp.as[UUID]
      yield
        resp.status shouldBe Status.Created
        id shouldBe berlinTechLeadJobId
    }

    "should only update a job that exists" in {
      for
        jwtToken <- mockAuthenticator.create(jenniferLawrence.email)
        resp     <- jobRoutes.orNotFound.run(
                      Request[IO](method = Method.PUT, uri = uri"/jobs/843df718-ec6e-4d49-9289-f799c0f40064")
                        .withEntity(berlinTechLeadJobInfo)
                        .withBearerToken(jwtToken)
                    )
      yield resp.status shouldBe Status.Ok
    }

    "should not update a job that doesn't exist" in {
      for
        jwtToken <- mockAuthenticator.create(jenniferLawrence.email)
        resp     <- jobRoutes.orNotFound.run(
                      Request[IO](method = Method.PUT, uri = uri"/jobs/843df718-ec6e-4d49-9289-000000000000")
                        .withEntity(berlinTechLeadJobInfo)
                        .withBearerToken(jwtToken)
                    )
      yield resp.status shouldBe Status.NotFound
    }

    "should only delete a job that exists" in {
      for
        jwtToken <- mockAuthenticator.create(jenniferLawrence.email)
        resp     <- jobRoutes.orNotFound.run(
                      Request[IO](method = Method.DELETE, uri = uri"/jobs/843df718-ec6e-4d49-9289-f799c0f40064")
                        .withBearerToken(jwtToken)
                    )
      yield resp.status shouldBe Status.Ok
    }

    "should not delete a job that doesn't exist" in {
      for
        jwtToken <- mockAuthenticator.create(jenniferLawrence.email)
        resp     <- jobRoutes.orNotFound.run(
                      Request[IO](method = Method.DELETE, uri = uri"/jobs/843df718-ec6e-4d49-9289-000000000000")
                        .withBearerToken(jwtToken)
                    )
      yield resp.status shouldBe Status.NotFound
    }

    "should surface all possible filters" in {
      for
        resp      <- jobRoutes.orNotFound.run(
                       Request[IO](method = Method.GET, uri = uri"/jobs/filters")
                     )
        jobFilter <- resp.as[JobFilter]
      yield jobFilter shouldBe JobFilter()
    }

  }
