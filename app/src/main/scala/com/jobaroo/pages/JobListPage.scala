package com.jobaroo.pages

import io.circe.generic.auto.*
import io.circe.parser.*
import io.circe.syntax.*
import cats.effect.IO
import cats.syntax.semigroup.*
import tyrian.*
import tyrian.http.*
import tyrian.Html.*
import com.jobaroo.App
import com.jobaroo.common.*
import com.jobaroo.common.Endpoint
import com.jobaroo.components.AppLayout
import com.jobaroo.components.FilterPanel
import com.jobaroo.components.JobComponents
import com.jobaroo.domain.job.*
import com.jobaroo.pages.JobListPage.FilterJobs
import com.jobaroo.pages.Page.Kind
import com.jobaroo.tyrianui.core.UiAttrs
import com.jobaroo.tyrianui.daisy.Badge
import com.jobaroo.tyrianui.daisy.Button
import com.jobaroo.tyrianui.daisy.Feedback
import com.jobaroo.tyrianui.html.Tags.div
import com.jobaroo.ui.preset.Jobaroo

final case class JobListPage(
  jobs       : List[Job] = Nil,
  jobFilters : JobFilter = JobFilter(),
  canLoadMore: Boolean = true,
  filterPanel: FilterPanel = FilterPanel(filterAction = FilterJobs(_)),
  status     : Option[Page.Status] = Some(Page.Status("Loading...", Page.Kind.LOADING))
) extends Page:

  import JobListPage.*

  override def initCmd: Cmd[IO, App.Msg] = commands.getJobs(jobFilter = jobFilters) |+| filterPanel.initCmd

  override def update(msg: App.Msg): (Page, Cmd[IO, App.Msg]) = msg match
    case AddJobs(jobs, canLoadMore)      =>
      (setSuccessStatus(s"Loaded ${jobs.length}").copy(jobs = this.jobs ++ jobs, canLoadMore = canLoadMore), Cmd.None)
    case JobFailure(error)               => (setErrorStatus(error), Cmd.None)
    case LoadMoreJobs                    => (this, commands.getJobs(jobFilter = this.jobFilters, offset = jobs.length))
    case FilterJobs(filters)             =>
      val newJobFilters = parseJobFilters(filters)
      (this.copy(jobs = Nil, jobFilters = newJobFilters), commands.getJobs(jobFilter = newJobFilters))
    case filterPanelMsg: FilterPanel.Msg =>
      val (newFilterPanel, cmd) = filterPanel.update(filterPanelMsg)
      (this.copy(filterPanel = newFilterPanel), cmd)

  override def view: Html[App.Msg] =
    AppLayout.pageContainer(
      AppLayout.hero(
        title = "Discover JVM roles that are easy to scan and worth your time.",
        subtitle = "Search curated Scala, Java, and backend openings with clearer compensation, cleaner filters, and calmer browsing.",
        eyebrow = "Jobaroo Market",
        actions = Seq(
          Badge.render(s"${jobs.length} live roles", Badge.Tone.Primary),
          Badge.render("Backend teams", Badge.Tone.Outline)
        )
      ),
      AppLayout.split(
        sidebar = filterPanel.view,
        content = div(UiAttrs.classes(Jobaroo.shell.stack))(
          div(UiAttrs.classes(Jobaroo.jobs.toolbar))(
            AppLayout.sectionTitle(
              eyebrow = "Live board",
              title = "Open roles for serious backend teams",
              subtitle = "Browse by company, location, seniority, and compensation without losing the thread."
            ),
            div(UiAttrs.classes(Jobaroo.jobs.statPill))(text(s"${jobs.length} visible roles"))
          ),
          div(UiAttrs.classes(Jobaroo.shell.gridGap5))(
            (jobs.map(JobComponents.renderJob) :+ renderLoadMore)*
          )
        )
      )
    )

  private def renderLoadMore: Html[App.Msg] = status match
    case Some(s) =>
      div(UiAttrs.classes(Jobaroo.state.centeredTight))(
        s.kind match
          case Kind.SUCCESS =>
            if canLoadMore then
              Button.render(
                Button.props[App.Msg]("Load More Roles").copy(
                  tone = Button.Tone.Primary,
                  onPress = Some(LoadMoreJobs)
                )
              )
            else
              Feedback.alert("All matching jobs are loaded.", Feedback.Tone.Success)
          case Kind.ERROR   => Feedback.alert(s.message, Feedback.Tone.Error)
          case Kind.LOADING => Feedback.alert("Loading jobs...", Feedback.Tone.Info)
      )
    case None    => div()

  // TODO - too lose
  def parseJobFilters(filters: Map[String, Set[String]]): JobFilter =
    new JobFilter(
      companies = filters.getOrElse("Companies", Set.empty).toList,
      locations = filters.getOrElse("Locations", Set.empty).toList,
      countries = filters.getOrElse("Countries", Set.empty).toList,
      seniorities = filters.getOrElse("Seniorities", Set.empty).toList,
      tags = filters.getOrElse("Tags", Set.empty).toList,
      maxSalary = Option.when(filterPanel.maxSalary > 0)(filterPanel.maxSalary),
      remote = filterPanel.remote
    )

  private def setErrorStatus(message: String)   = this.copy(status = Some(Page.Status(message, Page.Kind.ERROR)))
  private def setSuccessStatus(message: String) = this.copy(status = Some(Page.Status(message, Page.Kind.SUCCESS)))

object JobListPage:

  trait Msg                                                       extends App.Msg
  final case class JobFailure(error: String)                      extends Msg
  final case class AddJobs(jobs: List[Job], canLoadMore: Boolean) extends Msg
  final case class FilterJobs(filters: Map[String, Set[String]])  extends Msg
  case object LoadMoreJobs                                        extends Msg

  object endpoints:

    def getJobs(limit: Int, offset: Int): Endpoint[Msg] = new Endpoint[Msg](
      location = s"${constants.endpoints.jobs}?limit=$limit&offset=$offset",
      method = Method.Post,
      onError = e => JobFailure(e.toString),
      onResponse = Endpoint.onResponse[List[Job], Msg](
        onError = JobFailure(_),
        onSuccess = jobs => AddJobs(jobs, offset == 0 || jobs.nonEmpty)
      )
    ) {}

  object commands:

    def getJobs(
      jobFilter: JobFilter,
      limit: Int = constants.defaultPageSize,
      offset: Int = 0
    ): Cmd[IO, Msg] = endpoints.getJobs(limit, offset).call(jobFilter)
