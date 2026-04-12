package com.jobaroo.pages

import io.circe.generic.auto.*
import io.circe.parser.*
import io.circe.syntax.*
import cats.effect.IO
import cats.syntax.semigroup.*
import laika.api.*
import laika.format.*
import tyrian.*
import tyrian.http.*
import tyrian.Html.*
import com.jobaroo.App
import com.jobaroo.common.*
import com.jobaroo.common.Endpoint
import com.jobaroo.common.constants
import com.jobaroo.components.AppLayout
import com.jobaroo.components.JobComponents
import com.jobaroo.domain.job.Job
import com.jobaroo.pages.Page.Kind
import com.jobaroo.tyrianui.core.UiAttrs
import com.jobaroo.tyrianui.daisy.Badge
import com.jobaroo.tyrianui.daisy.Button
import com.jobaroo.tyrianui.daisy.Card
import com.jobaroo.tyrianui.daisy.Feedback
import com.jobaroo.tyrianui.html.Tags.{div, h1, img, p}
import com.jobaroo.ui.preset.Jobaroo
import scala.scalajs.js
import scala.scalajs.js.annotation.*

@js.native
@JSGlobal()
object MomentLib extends js.Object:
  def unix(ts: Double): js.Dynamic = js.native

final case class JobPage(
  id    : String,
  optJob: Option[Job] = None,
  status: Page.Status = Page.Status("Loading...", Page.Kind.LOADING)
) extends Page:

  import JobPage.*

  override def initCmd: Cmd[IO, App.Msg] = commands.getJob(id)

  override def update(msg: App.Msg): (Page, Cmd[IO, App.Msg]) = msg match
    case GetJobSuccess(job) => (copy(optJob = Some(job), status = Page.Status("Loaded", Kind.SUCCESS)), Cmd.None)
    case GetJobError(error) => (copy(status = Page.Status(error, Kind.ERROR)), Cmd.None)

  override def view: Html[App.Msg] =
    optJob.fold(renderNoJobPage)(renderJobPage)

  private def renderNoJobPage: Html[App.Msg] =
    AppLayout.pageContainer(
      div(UiAttrs.classes(Jobaroo.state.centered))(
        status.kind match
          case Kind.SUCCESS | Kind.ERROR => Feedback.alert("This job does not exist.", Feedback.Tone.Error)
          case Kind.LOADING              => Feedback.alert("Loading job details...", Feedback.Tone.Info)
      )
    )

  private def renderJobPage(job: Job): Html[App.Msg] =
    val roleBadges =
      Badge.render(if job.jobInfo.remote then "Remote" else "On-site", if job.jobInfo.remote then Badge.Tone.Primary else Badge.Tone.Outline) ::
        job.jobInfo.seniority.toList.map(value => Badge.render(value, Badge.Tone.Outline))

    AppLayout.pageContainer(
      Card.surface(UiAttrs.classes(Jobaroo.surface.card))(
        Card.body(UiAttrs.classes(Jobaroo.surface.bodySpacious))(
          div(UiAttrs.classes(Jobaroo.jobs.cardLayoutDetail))(
            div(UiAttrs.classes(Jobaroo.jobs.copyColumnLarge))(
              div(UiAttrs.classes(Jobaroo.jobs.previewRowLarge))(
                div(UiAttrs.classes(Jobaroo.jobs.avatarWrap))(
                  div(UiAttrs.classes(Jobaroo.jobs.avatarFrameLarge))(
                    img(
                      UiAttrs(src := job.jobInfo.image.getOrElse(constants.fallbackImage), alt := job.jobInfo.title) |+|
                        UiAttrs.classes(Jobaroo.jobs.avatarImageLarge)
                    )
                  )
                ),
                div(UiAttrs.classes(Jobaroo.jobs.copyColumnLarge))(
                  p(UiAttrs.classes(Jobaroo.jobs.companyWide))(text(job.jobInfo.company)),
                  h1(UiAttrs.classes(Jobaroo.jobs.detailTitle))(text(job.jobInfo.title)),
                  div(UiAttrs.classes(Jobaroo.jobs.metaRow))(
                    roleBadges*
                  )
                )
              ),
              JobComponents.renderJobSummary(job),
              div(UiAttrs.classes(Jobaroo.jobs.metaRow))(
                job.jobInfo.tags.getOrElse(Nil).take(5).map(tag => Badge.render(tag, Badge.Tone.Outline))*
              )
            ),
            div(UiAttrs.classes(Jobaroo.jobs.actionsStack))(
              p(UiAttrs.classes(Jobaroo.jobs.detailTime))(text(MomentLib.unix(job.date / 1_000L).fromNow().toString)),
              Button.link(
                Button.props[App.Msg]("Apply on Site").copy(
                  tone = Button.Tone.Primary
                ),
                hrefValue = job.jobInfo.externalUrl,
                newTab = true
              )
            )
          ),
          renderMarkdown(job)
        )
      )
    )

  private def renderMarkdown(job: Job): Html[App.Msg] =
    val htmlText = markdownTransformer.transform(job.jobInfo.description) match
      case Left(_)     => "error"
      case Right(html) => html

    div(UiAttrs.classes(Jobaroo.markdown.prose))().innerHtml(htmlText)

  val markdownTransformer = Transformer.from(Markdown).to(HTML).build

object JobPage:

  trait Msg                                   extends App.Msg
  final case class GetJobSuccess(job: Job)    extends Msg
  final case class GetJobError(error: String) extends Msg

  object endpoints:

    def getJob(id: String): Endpoint[Msg] = new Endpoint[Msg](
      location = s"${constants.endpoints.jobs}/$id",
      method = Method.Get,
      onError = e => GetJobError(e.toString),
      onResponse = Endpoint.onResponse[Job, Msg](onError = GetJobError(_), onSuccess = GetJobSuccess(_))
    ) {}

  object commands:

    def getJob(id: String): Cmd[IO, Msg] = endpoints.getJob(id).call()
