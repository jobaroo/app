package dojo

import cats.effect.*
import com.jobaroo.domain.job.JobInfo
import doobie.hikari.HikariTransactor
import doobie.implicits.*
import doobie.util.*
import org.typelevel.log4cats.Logger
import org.typelevel.log4cats.slf4j.Slf4jLogger

case object jobsPlayground extends IOApp.Simple:

  val postgresResource: Resource[IO, HikariTransactor[IO]] =
    for
      ec <- ExecutionContexts.fixedThreadPool(32)
      xa <- HikariTransactor.newHikariTransactor[IO](
              driverClassName = "org.postgresql.Driver",
              url = "jdbc:postgresql:board",
              user = "docker",
              pass = "docker",
              connectEC = ec
            )
    yield xa

  val jobInfo = JobInfo(
    company = "Apple",
    title = "Software Engineer",
    description = "Best job!",
    externalUrl = "http://localhost:8080",
    location = "Warsaw",
    remote = false,
    salaryLow = None,
    salaryHigh = None,
    currency = Option("USD"),
    country = Option("Poland"),
    tags = None,
    image = None,
    seniority = None,
    other = None
  )

  import com.jobaroo.core.*

  given Logger[IO] = Slf4jLogger.getLogger[IO]

  override def run: IO[Unit] = postgresResource.use { xa =>
    for
      _        <- IO.println("Inserting jobs...")
      jobs     <- LiveJobs[IO](xa)
      id       <- jobs.create("com.leowajda@tuta.io", jobInfo)
      _        <- IO.println(s"Job id: $id")
      newJob   <- jobs.update(id, jobInfo.copy(company = "Google"))
      _        <- IO.println(s"New job is: $newJob")
      count    <- jobs.delete(id)
      _        <- IO.println(s"rows affected: $count")
    yield ()
  }
