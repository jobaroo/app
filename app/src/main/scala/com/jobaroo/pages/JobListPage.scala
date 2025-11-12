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
import com.jobaroo.components.FilterPanel
import com.jobaroo.pages.JobListPage.FilterJobs
import com.jobaroo.components.Anchors
import com.jobaroo.core.Router

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
      (setSuccessStatus("Loaded").copy(jobs = this.jobs ++ jobs, canLoadMore = canLoadMore), Cmd.None)
    case JobFailure(error)               => (setErrorStatus(error), Cmd.None)
    case LoadMoreJobs                    => (this, commands.getJobs(jobFilter = this.jobFilters, offset = jobs.length))
    case FilterJobs(filters)             =>
      val newJobFilters = parseJobFilters(filters)
      (this.copy(jobs = Nil, jobFilters = newJobFilters), commands.getJobs(jobFilter = newJobFilters))
    case filterPanelMsg: FilterPanel.Msg =>
      val (newFilterPanel, cmd) = filterPanel.update(filterPanelMsg)
      (this.copy(filterPanel = newFilterPanel), cmd)

  override def view: Html[App.Msg] =
    div(`class` := "job-list-page")(
      filterPanel.view,
      div(`class` := "jobs-container")(
        jobs.map(renderJob) ++ optRenderLoadMore
      )
    )

  private def renderJob(job: Job): Html[App.Msg] =
    div(`class` := "job-card")(
      div(`class` := "job-card-image")(img(
        `class` := "job-logo",
        src     := job.jobInfo.image.getOrElse(""),
        alt     := job.jobInfo.title
      )),
      div(`class` := "job-card-content")(
        h4(
          Anchors.renderNavLink(s"${job.jobInfo.company} - ${job.jobInfo.title}", urls.job(job.id))(
            Router.ChangeLocation(_)
          )
        )
      ),
      div(`class` := "job-card-apply")(a(href := job.jobInfo.externalUrl, target := "blank")("Apply"))
    )

  private def optRenderLoadMore: Option[Html[App.Msg]] = status.map { s =>
    div(`class` := "load-more-action")(
      s.kind match
        case Kind.SUCCESS =>
          if canLoadMore then button(`type` := "button", onClick(LoadMoreJobs))("Load more jobs")
          else div("All jobs loaded.")
        case Kind.ERROR   => div(s.message)
        case Kind.LOADING => div("Loading...")
    )
  }

  // TODO - too lose
  def parseJobFilters(filters: Map[String, Set[String]]): JobFilter =
    new JobFilter(
      companies = filters.getOrElse("Companies", Set.empty).toList,
      locations = filters.getOrElse("Locations", Set.empty).toList,
      countries = filters.getOrElse("Countries", Set.empty).toList,
      seniorities = filters.getOrElse("Seniorities", Set.empty).toList,
      tags = filters.getOrElse("Tags", Set.empty).toList,
      maxSalary = Some(filterPanel.maxSalary),
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
