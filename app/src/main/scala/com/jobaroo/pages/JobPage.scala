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
import com.jobaroo.components.PreviewText
import com.jobaroo.domain.job.Job
import com.jobaroo.pages.Page.Kind
import com.jobaroo.pages.Page.urls
import com.jobaroo.tyrianui.core.UiAttrs
import com.jobaroo.tyrianui.daisy.Badge
import com.jobaroo.tyrianui.daisy.Button
import com.jobaroo.tyrianui.daisy.Card
import com.jobaroo.tyrianui.daisy.Feedback
import com.jobaroo.tyrianui.html.Tags.{div, h1, h2, li, p, ul}
import com.jobaroo.tyrianui.icons.Icons
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
    val tagBadges     = job.jobInfo.tags.getOrElse(Nil).take(6).map(tag => Badge.render(tag, Badge.Tone.Outline))
    val relativeTime  = MomentLib.unix(job.date / 1_000L).fromNow().toString
    val markdownHtml  = renderMarkdownHtml(job)
    val roleSummary   = PreviewText.fromMarkdown(PreviewText.withoutLeadingTitle(job.jobInfo.description, job.jobInfo.title))

    AppLayout.pageContainer(
      div(UiAttrs.classes(Jobaroo.jobs.detailHero))(
        div(UiAttrs.classes(Jobaroo.jobs.detailHeroInner))(
          div(UiAttrs.classes(Jobaroo.jobs.detailLead))(
            div(UiAttrs.classes(Jobaroo.jobs.detailBackRow))(
              AppLayout.backLink(fallback = urls.jobs, classes = Jobaroo.section.backLinkInverse)
            ),
            p(UiAttrs.classes(Jobaroo.jobs.companyWide))(text(job.jobInfo.company)),
            h1(UiAttrs.classes(Jobaroo.jobs.detailHeroTitle))(text(job.jobInfo.title)),
            p(UiAttrs.classes(Jobaroo.jobs.detailHeroCopy))(
              text(
                if roleSummary.nonEmpty then roleSummary.take(190)
                else "A focused JVM role on Jobaroo with the full description and application action below."
              )
            ),
            if tagBadges.nonEmpty then div(UiAttrs.classes(Jobaroo.jobs.detailHeroTags))(tagBadges*)
            else div()
          ),
          div(UiAttrs.classes(Jobaroo.jobs.detailHeroActions))(
            div(UiAttrs.classes(Jobaroo.jobs.detailHeroCard))(
              p(UiAttrs.classes(Jobaroo.section.eyebrow))(text("Apply now")),
              h2(UiAttrs.classes(Jobaroo.jobs.detailSectionTitle))(text("Take the next step")),
              p(UiAttrs.classes(Jobaroo.jobs.detailSectionCopy))(text("Use the employer link to continue the application outside Jobaroo.")),
              Button.link(
                Button.props[App.Msg]("Apply on Site").copy(
                  tone = Button.Tone.Primary,
                  width = Button.Width.Full
                ),
                hrefValue = job.jobInfo.externalUrl,
                newTab = true
              )
            )
          )
        )
      ),
      div(UiAttrs.classes(Jobaroo.jobs.detailGrid))(
        div(UiAttrs.classes(Jobaroo.jobs.detailMain))(
          Card.surface(UiAttrs.classes(Jobaroo.surface.card))(
            Card.body(UiAttrs.classes(Jobaroo.surface.bodySpacious))(
              div(UiAttrs.classes(Jobaroo.jobs.detailSection))(
                p(UiAttrs.classes(Jobaroo.section.eyebrow))(text("Description")),
                h2(UiAttrs.classes(Jobaroo.jobs.detailSectionTitle))(text("About the role")),
                div(UiAttrs.classes(Jobaroo.markdown.prose))().innerHtml(markdownHtml)
              )
            )
          )
        ),
        div(UiAttrs.classes(Jobaroo.jobs.detailRail))(
          Card.surface(UiAttrs.classes(Jobaroo.surface.card))(
            Card.body(UiAttrs.classes(Jobaroo.surface.bodyCompact))(
              p(UiAttrs.classes(Jobaroo.section.eyebrow))(text("Role facts")),
              h2(UiAttrs.classes(Jobaroo.jobs.detailSectionTitle))(text("Everything important, once")),
              ul(UiAttrs.classes(Jobaroo.jobs.detailList))(
                detailItem(if job.jobInfo.remote then Icons.globe(Jobaroo.icon.small) else Icons.mapPin(Jobaroo.icon.small), JobComponents.locationText(job)),
                detailItem(Icons.banknotes(Jobaroo.icon.small), JobComponents.salaryText(job)),
                detailItem(Icons.briefcase(Jobaroo.icon.small), job.jobInfo.seniority.getOrElse("Open seniority")),
                detailItem(Icons.document(Jobaroo.icon.small), s"Posted $relativeTime")
              )
            )
          )
        )
      )
    )

  private def renderMarkdownHtml(job: Job): String =
    markdownTransformer.transform(PreviewText.withoutLeadingTitle(job.jobInfo.description, job.jobInfo.title)) match
      case Left(_)     => "error"
      case Right(html) => html

  private def detailItem(icon: Html[App.Msg], value: String): Html[App.Msg] =
    li(UiAttrs.classes(Jobaroo.jobs.detailListItem))(
      icon,
      p()(text(value))
    )

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
