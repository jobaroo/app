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
import com.jobaroo.components.JobComponents

import scala.scalajs.*
import scala.scalajs.js.*
import scala.scalajs.js.annotation.*

@js.native
@JSGlobal()
class Moment extends js.Object:
  def fromNow(): String = js.native

@js.native
@JSImport("moment", JSImport.Default)
object MomentLib extends js.Object:
  def unix(date: Long): Moment = js.native

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
    case Some(job) => renderJobPage(job)
    case None      => renderNoJobPage

  private def renderNoJobPage: Html[App.Msg] =
    val errorHtml = status.kind match
      case Kind.SUCCESS | Kind.ERROR => h1("This job doesn't exists")
      case Kind.LOADING              => h1("Loading...")

    div(`class` := "container-fluid the-rock")(
      div(`class` := "row jvm-jobs-details-top-card")(errorHtml)
    )

  private def renderJobPage(job: Job) =
    div(`class` := "container-fluid the-rock")(
      div(`class` := "row jvm-jobs-details-top-card")(
        div(`class` := "col-md-12 p-0")(
          div(`class` := "jvm-jobs-details-card-profile-img")(
            img(
              `class` := "img-fluid",
              src     := job.jobInfo.image.getOrElse(constants.fallbackImage),
              alt     := job.jobInfo.title
            )
          ),
          div(`class` := "jvm-jobs-details-card-profile-title")(
            h1(s"${job.jobInfo.company} - ${job.jobInfo.title}"),
            div(`class` := "jvm-jobs-details-card-profile-job-details-company-and-location")(
              JobComponents.renderJobSummary(job)
            )
          ),
          div(`class` := "jvm-jobs-details-card-apply-now-btn")(
            a(href := job.jobInfo.externalUrl, target := "blank")(
              button(`type` := "button", `class` := "btn btn-warning")("Apply now")
            ),
            p(MomentLib.unix(job.date / 1_000L).fromNow())
          )
        )
      ),
      div(`class` := "container-fluid")(
        div(`class` := "container")(
          div(`class` := "markdown-body overview-section")(
            renderJobDescription(job)
          )
        ),
        div(`class` := "container")(
          div(`class` := "rok-last")(
            div(`class` := "row")(
              div(`class` := "col-md-6 col-sm-6 col-6")(
                span(`class` := "rock-apply")("Apply for this job.")
              ),
              div(`class` := "col-md-6 col-sm-6 col-6")(
                a(href := job.jobInfo.externalUrl, target := "blank")(
                  button(`type` := "button", `class` := "rock-apply-btn")("Apply now")
                )
              )
            )
          )
        )
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
