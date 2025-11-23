package com.jobaroo.core

import cats.effect.*
import doobie.*
import doobie.util.*
import doobie.postgres.implicits.*
import doobie.implicits.*
import cats.effect.testing.scalatest.AsyncIOSpec
import com.jobaroo.domain.job.JobFilter
import com.jobaroo.domain.pagination.Pagination
import com.jobaroo.fixtures.JobFixture
import org.scalatest.freespec.AsyncFreeSpec
import org.scalatest.matchers.should.Matchers
import org.typelevel.log4cats.Logger
import org.typelevel.log4cats.slf4j.Slf4jLogger

class JobsSpec extends AsyncFreeSpec with AsyncIOSpec with Matchers with DoobieSpec("sql/jobs.sql") with JobFixture:

  given Logger[IO] = Slf4jLogger.getLogger[IO]

  "Jobs 'algebra'" - {
    "should return no job if the given UUID does not exist" in {
      transactor.use { xa =>
        val program =
          for
            jobs <- LiveJobs[IO](xa)
            res  <- jobs.find(idNotFound)
          yield res

        program.asserting { _ shouldBe None }
      }
    }

    "should retrieve a job by id" in {
      transactor.use { xa =>
        val program =
          for
            jobs <- LiveJobs[IO](xa)
            res  <- jobs.find(remoteSoftwareEngineerJobId)
          yield res

        program.asserting { _ shouldBe Some(remoteSoftwareEngineerJob) }
      }
    }

    "should retrieve all jobs" in {
      transactor.use { xa =>
        val program =
          for
            jobs <- LiveJobs[IO](xa)
            res  <- jobs.all()
          yield res

        program.asserting { _ shouldBe List(remoteSoftwareEngineerJob) }
      }
    }

    "should create a new job, initially inactive" in {
      transactor.use { xa =>
        val program =
          for
            jobs   <- LiveJobs[IO](xa)
            jobId  <- jobs.create(berlinTechLeadJob.ownerEmail, berlinTechLeadJobInfo)
            newJob <- jobs.find(jobId)
          yield newJob

        program.asserting { _.map(_.jobInfo) shouldBe None }
      }
    }

    "should activate a new job" in {
      transactor.use { xa =>
        val program =
          for
            jobs   <- LiveJobs[IO](xa)
            jobId  <- jobs.create(berlinTechLeadJob.ownerEmail, berlinTechLeadJobInfo)
            _      <- jobs.activate(jobId)
            newJob <- jobs.find(jobId)
          yield newJob

        program.asserting { _.map(_.jobInfo) shouldBe Some(berlinTechLeadJobInfo) }
      }
    }

    "should return an updated job if it exists" in {
      transactor.use { xa =>
        val program =
          for
            jobs <- LiveJobs[IO](xa)
            newJobInfo = remoteSoftwareEngineerJobInfo.copy(description = "other")
            newJob <- jobs.update(remoteSoftwareEngineerJobId, newJobInfo)
          yield newJob

        program.asserting { _.map(_.jobInfo) shouldBe Some(remoteSoftwareEngineerJobInfo.copy(description = "other")) }
      }
    }

    "should return None when trying to update a job that does not exists" in {
      transactor.use { xa =>
        val program =
          for
            jobs <- LiveJobs[IO](xa)
            newJobInfo = remoteSoftwareEngineerJobInfo.copy(description = "other")
            newJob <- jobs.update(idNotFound, newJobInfo)
          yield newJob

        program.asserting { _ shouldBe None }
      }
    }

    "should delete a job if it exists" in {
      transactor.use { xa =>
        val program =
          for
            jobs               <- LiveJobs[IO](xa)
            deletedJobsCount   <- jobs.delete(remoteSoftwareEngineerJobId)
            remainingJobsCount <-
              sql"""SELECT COUNT(*) FROM jobs WHERE id = $remoteSoftwareEngineerJobId""".query[Int].unique.transact(xa)
          yield (deletedJobsCount, remainingJobsCount)

        program.asserting { (deletedJobsCount, remainingJobsCount) =>
          deletedJobsCount shouldBe 1
          remainingJobsCount shouldBe 0
        }
      }
    }

    "should return zero updated rows if the job id is not found" in {
      transactor.use { xa =>
        val program =
          for
            jobs             <- LiveJobs[IO](xa)
            deletedJobsCount <- jobs.delete(idNotFound)
          yield deletedJobsCount

        program.asserting { _ shouldBe 0 }
      }
    }

    "should return remote jobs" in {
      transactor.use { xa =>
        val program =
          for
            jobs    <- LiveJobs[IO](xa)
            allJobs <- jobs.all(JobFilter(remote = true), Pagination(None, None))
          yield allJobs

        program.asserting { _ shouldBe List(remoteSoftwareEngineerJob) }
      }
    }

    "should filter jobs by tags" in {
      transactor.use { xa =>
        val program =
          for
            jobs    <- LiveJobs[IO](xa)
            allJobs <- jobs.all(JobFilter(remote = true, tags = List("c++", "haskell")), Pagination(None, None))
          yield allJobs

        program.asserting { _ shouldBe Nil }
      }
    }

    "should surface a comprehensive filter out of all jobs contained" in {
      transactor.use { xa =>
        val program =
          for
            jobs      <- LiveJobs[IO](xa)
            jobFilter <- jobs.possibleFilters()
          yield jobFilter

        program.asserting {
          case JobFilter(companies, locations, countries, seniorities, tags, maxSalary, remote) =>
            companies shouldBe List("Apple")
            locations shouldBe List("From remote")
            seniorities shouldBe List("High")
            tags.toSet shouldBe Set("scala", "scala-3", "cats", "akka", "spark", "flink", "zio")
            maxSalary shouldBe Option(3500)
        }
      }
    }

  }
