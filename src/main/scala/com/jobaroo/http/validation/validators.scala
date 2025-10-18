package com.jobaroo.http.validation

import cats.*
import cats.implicits.*
import cats.data.*
import cats.data.Validated.*
import com.jobaroo.domain.auth.*
import com.jobaroo.domain.job.JobInfo

import scala.util.{Failure, Success, Try}
import java.net.URL

object validators:

  sealed trait ValidationFailure(val errorMessage: String)
  final case class EmptyString(fieldName: String) extends ValidationFailure(s"$fieldName is empty")
  final case class InvalidUrl(url: String)        extends ValidationFailure(s"$url is not a valid URL")
  final case class InvalidEmail(email: String)    extends ValidationFailure(s"$email is not a valid email")

  type ValidationResult[A] = ValidatedNel[ValidationFailure, A]

  trait Validator[A]:
    def validate(value: A): ValidationResult[A]

  object Validator:

    private[this] val emailRegex =
      """^[a-zA-Z0-9\.!#$%&'*+/=?^_`{|}~-]+@[a-zA-Z0-9](?:[a-zA-Z0-9-]{0,61}[a-zA-Z0-9])?(?:\.[a-zA-Z0-9](?:[a-zA-Z0-9-]{0,61}[a-zA-Z0-9])?)*$""".r

    def apply[A](using validator: Validator[A]): Validator[A] = validator

    private[validators] def validateRequired[A](field: A, fieldName: String)(cond: A => Boolean): ValidationResult[A] =
      if cond(field) then field.validNel else EmptyString(fieldName).invalidNel

    private[validators] def validateEmail(field: String, fieldName: String): ValidationResult[String] =
      if emailRegex.findFirstMatchIn(field).isDefined then field.validNel else InvalidEmail(fieldName).invalidNel

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

  given Validator[LoginInfo] with

    def validate(value: LoginInfo): ValidationResult[LoginInfo] = value match
      case LoginInfo(email, password) =>
        import Validator.*
        val validEmail    = validateRequired(email, "email")(_.nonEmpty).andThen(validateEmail(_, "email"))
        val validPassword = validateRequired(password, "password")(_.nonEmpty)

        (validEmail, validPassword).mapN(LoginInfo.apply)

  given Validator[NewUserInfo] with

    def validate(value: NewUserInfo): ValidationResult[NewUserInfo] = value match
      case NewUserInfo(email, password, firstName, lastName, company) =>
        import Validator.*

        val validEmail    = validateRequired(email, "email")(_.nonEmpty).andThen(validateEmail(_, "email"))
        val validPassword = validateRequired(password, "password")(_.nonEmpty)

        (
          validEmail,
          validPassword,
          firstName.validNel,
          lastName.validNel,
          company.validNel
        ).mapN(NewUserInfo.apply)

  given Validator[NewPasswordInfo] with

    def validate(value: NewPasswordInfo): ValidationResult[NewPasswordInfo] = value match
      case NewPasswordInfo(oldPassword, newPassword) =>
        import Validator.*
        val validOldPassword = validateRequired(oldPassword, "oldPassword")(_.nonEmpty)
        val validNewPassword = validateRequired(newPassword, "newPassword")(_.nonEmpty)

        (validOldPassword, validNewPassword).mapN(NewPasswordInfo.apply)
