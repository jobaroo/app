package com.jobaroo.http.routes

import io.circe.generic.auto.*
import org.http4s.circe.CirceEntityCodec.*
import org.http4s.*
import org.http4s.implicits.*
import org.http4s.dsl.*
import org.http4s.dsl.impl.*
import cats.effect.testing.scalatest.AsyncIOSpec
import com.jobaroo.fixtures.JobFixture
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

import java.util.UUID

class JobRoutesSpec extends AsyncFreeSpec with AsyncIOSpec with Matchers with Http4sDsl[IO] with JobFixture:

  ////////////////////////////////////////////////////////////////////////////////////
  // prep
  ////////////////////////////////////////////////////////////////////////////////////

  val jobs: Jobs[IO] = new Jobs[IO]:

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

  ////////////////////////////////////////////////////////////////////////////////////
  // tests
  ////////////////////////////////////////////////////////////////////////////////////

  given Logger[IO]              = Slf4jLogger.getLogger[IO]
  val jobRoutes: HttpRoutes[IO] = JobRoutes[IO](jobs).routes

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
        resp <- jobRoutes.orNotFound.run(
                  Request(method = Method.POST, uri = uri"/jobs/create").withEntity(berlinTechLeadJobInfo)
                )
        id   <- resp.as[UUID]
      yield
        resp.status shouldBe Status.Created
        id shouldBe berlinTechLeadJobId
    }

    "should only update a job that exists" in {
      for
        resp <- jobRoutes.orNotFound.run(
                  Request(method = Method.PUT, uri = uri"/jobs/843df718-ec6e-4d49-9289-f799c0f40064").withEntity(
                    berlinTechLeadJobInfo
                  )
                )
      yield resp.status shouldBe Status.Ok
    }

    "should not update a job that doesn't exist" in {
      for
        resp <- jobRoutes.orNotFound.run(
                  Request(method = Method.PUT, uri = uri"/jobs/843df718-ec6e-4d49-9289-000000000000").withEntity(
                    berlinTechLeadJobInfo
                  )
                )
      yield resp.status shouldBe Status.NotFound
    }

    "should only delete a job that exists" in {
      for
        resp <- jobRoutes.orNotFound.run(
                  Request(method = Method.DELETE, uri = uri"/jobs/843df718-ec6e-4d49-9289-f799c0f40064")
                )
      yield resp.status shouldBe Status.Ok
    }

    "should not delete a job that doesn't exist" in {
      for
        resp <- jobRoutes.orNotFound.run(
                  Request(method = Method.DELETE, uri = uri"/jobs/843df718-ec6e-4d49-9289-000000000000")
                )
      yield resp.status shouldBe Status.NotFound
    }

  }
