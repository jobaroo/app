package com.jobaroo.pages

import io.circe.syntax.*
import io.circe.parser.*
import io.circe.generic.auto.*
import tyrian.*
import tyrian.http.*
import tyrian.Html.*
import cats.effect.IO
import com.jobaroo.App
import com.jobaroo.common.*
import com.jobaroo.pages.Page
import com.jobaroo.common.Endpoint
import com.jobaroo.domain.job.*
import com.jobaroo.pages.Page.urls
import com.jobaroo.App.Msg
import com.jobaroo.core.Session
import tyrian.cmds.Logger
import org.scalajs.dom.File
import org.scalajs.dom.FileReader
import scala.util.Try

final case class PostJobPage(
  company    : String = "",
  title      : String = "",
  description: String = "",
  externalUrl: String = "",
  location   : String = "",
  remote     : Boolean = false,
  salaryLow  : Option[Int] = None,
  salaryHigh : Option[Int] = None,
  currency   : Option[String] = None,
  country    : Option[String] = None,
  tags       : Option[String] = None,
  image      : Option[String] = None,
  seniority  : Option[String] = None,
  other      : Option[String] = None,
  status     : Option[Page.Status] = None
) extends FormPage("Post Job", status):

  import PostJobPage.*

  override def view: Html[App.Msg] =
    if Session.isActive then super.view else div(h1("Post Job"), div("You need to be logged in to post a job."))

  override protected def renderFormContent(): List[Html[App.Msg]] = List(
    renderInput("Company", "company", "text", true, UpdateCompany(_)),
    renderInput("Title", "title", "text", true, UpdateTitle(_)),
    renderTextArea("Description", "description", true, UpdateDescription(_)),
    renderInput("ExternalUrl", "externalUrl", "text", true, UpdateExternalUrl(_)),
    renderInput("Location", "location", "text", true, UpdateLocation(_)),
    renderToggle("Remote", "remote", true, _ => ToggleRemote),
    renderInput("SalaryLow", "salaryLow", "number", false, amount => UpdateSalaryLow(Try(amount.toInt).getOrElse(0))),
    renderInput("SalaryHigh", "salaryHigh", "number", false, amount => UpdateSalaryHigh(Try(amount.toInt).getOrElse(0))),
    renderInput("Currency", "currency", "text", false, UpdateCurrency(_)),
    renderInput("Country", "country", "text", false, UpdateCountry(_)),
    renderInput("Tags", "tags", "text", false, UpdateTags(_)),
    renderImageUploadInput("Logo", "logo", image, UpdateImageFile(_)),
    renderInput("Seniority", "seniority", "text", false, UpdateSeniority(_)),
    renderInput("Other", "other", "text", false, UpdateOther(_)),
    button(`type` := "button", onClick(PostJob))("Post Job")
  )

  override def update(msg: App.Msg): (Page, Cmd[IO, App.Msg]) = msg match
    case UpdateCompany(company)         => (this.copy(company = company), Cmd.None)
    case UpdateTitle(title)             => (this.copy(title = title), Cmd.None)
    case UpdateDescription(description) => (this.copy(description = description), Cmd.None)
    case UpdateExternalUrl(externalUrl) => (this.copy(externalUrl = externalUrl), Cmd.None)
    case UpdateLocation(location)       => (this.copy(location = location), Cmd.None)
    case ToggleRemote                   => (this.copy(remote = !this.remote), Cmd.None)
    case UpdateSalaryLow(salaryLow)     => (this.copy(salaryLow = Some(salaryLow)), Cmd.None)
    case UpdateSalaryHigh(salaryHigh)   => (this.copy(salaryHigh = Some(salaryHigh)), Cmd.None)
    case UpdateCurrency(currency)       => (this.copy(currency = Some(currency)), Cmd.None)
    case UpdateCountry(country)         => (this.copy(country = Some(country)), Cmd.None)
    case UpdateTags(tags)               => (this.copy(tags = Some(tags)), Cmd.None)
    case UpdateImage(optImage)          => (this.copy(image = optImage), Cmd.None)
    case UpdateImageFile(optFile)       => (this, commands.loadFile(optFile))
    case UpdateSeniority(seniority)     => (this.copy(seniority = Some(seniority)), Cmd.None)
    case UpdateOther(other)             => (this.copy(other = Some(other)), Cmd.None)
    case PostJobFailure(error)          => (setErrorStatus(error), Cmd.None)
    case PostJobSuccess(jobId)          => (setSuccessStatus("Job created"), Logger.consoleLog[IO](s"jobId: $jobId"))
    case PostJob                        =>
      (
        this,
        commands.postJob(
          company = this.company,
          title = this.title,
          description = this.description,
          externalUrl = this.externalUrl,
          location = this.location,
          remote = this.remote,
          salaryLow = this.salaryLow,
          salaryHigh = this.salaryHigh,
          currency = this.currency,
          country = this.country,
          tags = this.tags,
          image = this.image,
          seniority = this.seniority,
          other = this.other
        ))

  private def setErrorStatus(message: String): Page   = this.copy(status = Some(Page.Status(message, Page.Kind.ERROR)))
  private def setSuccessStatus(message: String): Page = this.copy(status = Some(Page.Status(message, Page.Kind.SUCCESS)))

object PostJobPage:

  trait Msg                                               extends App.Msg
  final case class UpdateCompany(company: String)         extends Msg
  final case class UpdateTitle(title: String)             extends Msg
  final case class UpdateDescription(description: String) extends Msg
  final case class UpdateExternalUrl(externalUrl: String) extends Msg
  final case class UpdateLocation(location: String)       extends Msg
  case object ToggleRemote                                extends Msg
  final case class UpdateSalaryLow(salaryLow: Int)        extends Msg
  final case class UpdateSalaryHigh(salaryHigh: Int)      extends Msg
  final case class UpdateCurrency(currency: String)       extends Msg
  final case class UpdateCountry(country: String)         extends Msg
  final case class UpdateTags(tags: String)               extends Msg
  final case class UpdateImageFile(optFile: Option[File]) extends Msg
  final case class UpdateImage(optImage: Option[String])  extends Msg
  final case class UpdateSeniority(seniority: String)     extends Msg
  final case class UpdateOther(other: String)             extends Msg
  case object PostJob                                     extends Msg
  final case class PostJobSuccess(jobId: String)          extends Msg
  final case class PostJobFailure(error: String)          extends Msg

  object endpoints:

    val createJob = new Endpoint[Msg](
      location = constants.endpoints.createJob,
      method = Method.Post,
      onError = e => PostJobFailure(e.toString),
      onResponse = resp =>
        resp.status match
          case Status(s, _) if s >= 200 && s < 300 => PostJobSuccess(resp.body)
          case Status(s, _) if s >= 400 && s < 500 =>
            parse(resp.body).flatMap(json => json.hcursor.get[String]("error")) match
              case Left(e)      => PostJobFailure(s"Error: ${e.getMessage}")
              case Right(value) => PostJobFailure(value)
    ) {}

  object commands:

    import cats.syntax.traverse.*

    def loadFile(optFile: Option[File]) = Cmd.Run[IO, Option[String], Msg](
      optFile.traverse { file =>
        IO.async_ { cb =>
          val reader = new FileReader
          reader.onload = _ => cb(Right(reader.result.toString))
          reader.readAsDataURL(file)
        }
      }
    )(UpdateImage(_))

    def postJob(
      company: String,
      title: String,
      description: String,
      externalUrl: String,
      location: String,
      remote: Boolean,
      salaryLow: Option[Int],
      salaryHigh: Option[Int],
      currency: Option[String],
      country: Option[String],
      tags: Option[String],
      image: Option[String],
      seniority: Option[String],
      other: Option[String]
    ): Cmd[IO, App.Msg] =

      val newJobInfo = JobInfo(
        company = company,
        title = title,
        description = description,
        externalUrl = externalUrl,
        location = location,
        remote = remote,
        salaryLow = salaryLow,
        salaryHigh = salaryHigh,
        currency = currency,
        country = country,
        tags = tags.map(_.split(",").map(_.trim).toList),
        image = image,
        seniority = seniority,
        other = other
      )

      endpoints.createJob.callAuthorized(newJobInfo)
