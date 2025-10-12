package com.jobaroo.http.validation

import cats.*
import cats.implicits.*
import cats.data.*
import cats.data.Validated.*
import com.jobaroo.domain.job.JobInfo

import scala.util.{Failure, Success, Try}
import java.net.URL

object validators:

  sealed trait ValidationFailure(val errorMessage: String)
  final case class EmptyString(fieldName: String) extends ValidationFailure(s"$fieldName is empty")
  final case class InvalidUrl(url: String)        extends ValidationFailure(s"$url is invalid")

  type ValidationResult[A] = ValidatedNel[ValidationFailure, A]

  trait Validator[A]:
    def validate(value: A): ValidationResult[A]

  object Validator:
    def apply[A](using validator: Validator[A]): Validator[A] = validator

    private[validators] def validateRequired[A](field: A, fieldName: String)(cond: A => Boolean): ValidationResult[A] =
      if cond(field) then field.validNel else EmptyString(fieldName).invalidNel

    private[validators] def validateUrl(field: String, fieldName: String): ValidationResult[String] =
      Try(URL(field).toURI).fold(_ => InvalidUrl(fieldName).invalidNel, _ => field.validNel)

  // TODO: move to instances object
  given Validator[JobInfo] with

    def validate(value: JobInfo): ValidationResult[JobInfo] = value match
      case JobInfo(
          company,
          title,
          description,
          externalUrl,
          location,
          remote,
          salaryLow,
          salaryHigh,
          currency,
          country,
          tags,
          image,
          seniority,
          other
        ) =>

        import Validator.*

        val validCompany     = validateRequired(company, "company")(_.nonEmpty)
        val validTitle       = validateRequired(title, "title")(_.nonEmpty)
        val validDescription = validateRequired(description, "description")(_.nonEmpty)
        val validExternalUrl = validateUrl(externalUrl, "externalUrl")
        val validLocation    = validateRequired(location, "location")(_.nonEmpty)

        (
          validCompany,
          validTitle,
          validDescription,
          validExternalUrl,
          validLocation,
          remote.validNel,
          salaryLow.validNel,
          salaryHigh.validNel,
          currency.validNel,
          country.validNel,
          tags.validNel,
          image.validNel,
          seniority.validNel,
          other.validNel
        ).mapN(JobInfo.apply)
