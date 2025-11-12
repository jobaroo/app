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
import com.jobaroo.pages.Page.Kind

import laika.api.*
import laika.format.*

final case class JobPage(
  id    : String,
  optJob: Option[Job] = None,
  status: Page.Status = Page.Status("Loading...", Page.Kind.LOADING)
) extends Page:

  import JobPage.*

  override def initCmd: Cmd[IO, App.Msg] = commands.getJob(id)

  override def update(msg: App.Msg): (Page, Cmd[IO, App.Msg]) = msg match
    case JobFailure(error) => (setErrorStatus(error), Cmd.None)
    case SetJob(job)       => (this.copy(optJob = Some(job)), Cmd.None)

  override def view: Html[App.Msg] = optJob match
    case Some(job) => div(`class` := "job-page")(
        div(`class` := "job-hero")(img(
          `class` := "job-logo",
          src     := job.jobInfo.image.getOrElse(""),
          alt     := job.jobInfo.title
        )),
        h1(s"${job.jobInfo.company} - ${job.jobInfo.title}"),
        div(`class` := "job-overview")(renderJobDetails(job)),
        renderJobDescription(job),
        a(href := job.jobInfo.externalUrl, `class` := "job-apply-action", target := "blank")("Apply")
      )
    case None      => status.kind match
        case Kind.SUCCESS | Kind.ERROR => div("This job doesn't exists")
        case Kind.LOADING              => div("Loading...")

  private def renderJobDetails(job: Job): Html[App.Msg] =
    def renderDetail(value: String): Html[App.Msg] =
      if value.isEmpty then div() else li(`class` := "job-detail-value")(value)

    val currencyText = job.jobInfo.currency.getOrElse("")
    val locationText = job.jobInfo.country.fold(job.jobInfo.location)(c => s"$c, ${job.jobInfo.location}")
    val salaryText   = (job.jobInfo.salaryLow, job.jobInfo.salaryHigh) match
      case (Some(low), Some(high)) => s"$currencyText $low-$high"
      case (Some(low), None)       => s"> $currencyText $low"
      case (None, Some(high))      => s"<= $currencyText $high"
      case _                       => "N/A"

    div(`class` := "job-details")(
      ul(`class` := "job-detail")(
        renderDetail(locationText),
        renderDetail(salaryText),
        renderDetail(job.jobInfo.seniority.getOrElse("All levels")),
        renderDetail(job.jobInfo.tags.getOrElse(List.empty).mkString(","))
      )
    )

  private def renderJobDescription(job: Job): Html[App.Msg] =
    val htmlText = markdownTransformer.transform(job.jobInfo.description) match
      case Left(error) => "error"
      case Right(html) => html
    div(`class` := "job-description")().innerHtml(htmlText)

  val markdownTransformer = Transformer.from(Markdown).to(HTML).build

  private def setErrorStatus(message: String)   = this.copy(status = Page.Status(message, Page.Kind.ERROR))
  private def setSuccessStatus(message: String) = this.copy(status = Page.Status(message, Page.Kind.SUCCESS))

object JobPage:

  trait Msg                                  extends App.Msg
  final case class JobFailure(error: String) extends Msg
  final case class SetJob(job: Job)          extends Msg

  object commands:

    def getJob(id: String): Cmd[IO, App.Msg] = endpoints.getJob(id).call()

  object endpoints:

    def getJob(id: String): Endpoint[Msg] = new Endpoint[Msg](
      location = s"${constants.endpoints.jobs}/$id",
      method = Method.Get,
      onError = e => JobFailure(e.toString),
      onResponse = Endpoint.onResponse(
        onError = JobFailure(_),
        onSuccess = SetJob(_)
      )
    ) {}
