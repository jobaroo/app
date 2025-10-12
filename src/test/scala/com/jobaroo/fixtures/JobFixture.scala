package com.jobaroo.fixtures

import cats.syntax.all.*
import com.jobaroo.domain.job.Job
import com.jobaroo.domain.job.JobInfo

import java.util.UUID

trait JobFixture:

  val idNotFound: UUID                       = UUID.fromString("6ea79557-3112-4c84-a8f5-1d1e2c300948")
  val berlinTechLeadJobId: UUID              = UUID.fromString("843df718-ec6e-4d49-9289-f799c0f40064")
  val barcelonaEngineeringManagerJobId: UUID = UUID.fromString("efcd2a64-4463-453a-ada8-b1bae1db4377")
  val remoteSoftwareEngineerJobId: UUID      = UUID.fromString("19a941d0-aa19-477b-9ab0-a7033ae65c2b")

  val remoteSoftwareEngineerJobInfo: JobInfo = JobInfo(
    company = "Apple",
    title = "Software Engineer",
    description = "Cassandra Storage",
    externalUrl = "https://apple.com/something",
    remote = true,
    location = "From remote",
    salaryLow = 2000.some,
    salaryHigh = 3500.some,
    currency = "EUR".some,
    country = "France".some,
    tags = Some(List("scala", "scala-3", "cats", "akka", "spark", "flink", "zio")),
    image = None,
    seniority = "High".some,
    other = None
  )

  val barcelonaEngineeringManagerJobInfo: JobInfo = JobInfo(
    company = "Awesome Company (Spain Branch)",
    title = "Engineering Manager",
    description = "An awesome job in Barcelona",
    externalUrl = "http://www.awesome.com",
    remote = false,
    location = "Barcelona",
    salaryLow = 2200.some,
    salaryHigh = 3200.some,
    currency = "USD".some,
    country = "Spain".some,
    tags = Some(List("scala", "scala-3", "zio")),
    image = "http://www.awesome.com/logo.png".some,
    seniority = "Highest".some,
    other = "Some additional info".some
  )

  val berlinTechLeadJobInfo: JobInfo = JobInfo(
    company = "Awesome Company",
    title = "Tech Lead",
    description = "An awesome job in Berlin",
    externalUrl = "https://some.website/awesomejob",
    remote = false,
    location = "Berlin",
    salaryLow = 2000.some,
    salaryHigh = 3000.some,
    currency = "EUR".some,
    country = "Germany".some,
    tags = Some(List("scala", "scala-3", "cats")),
    image = None,
    seniority = "Senior".some,
    other = None
  )

  val berlinTechLeadJob: Job = Job(
    id = berlinTechLeadJobId,
    date = 1659186086L,
    ownerEmail = "some@email.com",
    jobInfo = berlinTechLeadJobInfo
  )

  val barcelonaEngineeringManagerJob: Job = Job(
    id = barcelonaEngineeringManagerJobId,
    date = 1657186086L,
    ownerEmail = "some@email.com",
    jobInfo = barcelonaEngineeringManagerJobInfo
  )

  val remoteSoftwareEngineerJob: Job = Job(
    id = remoteSoftwareEngineerJobId,
    date = 1659186086L,
    ownerEmail = "some@email.com",
    jobInfo = remoteSoftwareEngineerJobInfo
  )

  val invalidJob: Job = Job(
    id = null,
    date = 42L,
    ownerEmail = "nothing@gmail.com",
    jobInfo = JobInfo.empty
  )

  val jobWithIdNotFound: Job = berlinTechLeadJob.copy(id = idNotFound)
