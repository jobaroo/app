package com.jobaroo.domain

import java.util.UUID

object job:

  final case class Job(id: UUID, date: Long, ownerEmail: String, jobInfo: JobInfo, active: Boolean = false)

  final case class JobInfo(
    company    : String,
    title      : String,
    description: String,
    externalUrl: String,
    location   : String,
    remote     : Boolean,
    salaryLow  : Option[Int],
    salaryHigh : Option[Int],
    currency   : Option[String],
    country    : Option[String],
    tags       : Option[List[String]],
    image      : Option[String],
    seniority  : Option[String],
    other      : Option[String]
  )

  object JobInfo:

    val empty: JobInfo = JobInfo(
      company = "",
      title = "",
      description = "",
      externalUrl = "",
      location = "",
      remote = false,
      salaryLow = None,
      salaryHigh = None,
      currency = None,
      country = None,
      tags = None,
      image = None,
      seniority = None,
      other = None
    )

  final case class JobFilter(
    companies  : List[String] = Nil,
    locations  : List[String] = Nil,
    countries  : List[String] = Nil,
    seniorities: List[String] = Nil,
    tags       : List[String] = Nil,
    maxSalary  : Option[Int] = None,
    remote     : Boolean = false
  )
