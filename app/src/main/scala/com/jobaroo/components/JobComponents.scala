package com.jobaroo.components

import tyrian.*
import tyrian.Html.*
import com.jobaroo.App
import com.jobaroo.domain.job.Job
import com.jobaroo.core.Router
import com.jobaroo.pages.Page.urls

object JobComponents:

  def renderJob(job: Job): Html[App.Msg] =
    div(`class` := "jvm-recent-jobs-cards")(
      div(`class` := "jvm-recent-jobs-card-img")(
        img(
          `class` := "img-fluid",
          src     := job.jobInfo.image.getOrElse(""),
          alt     := job.jobInfo.title
        )
      ),
      div(`class` := "jvm-recent-jobs-card-contents")(
        h5(
          Anchors.renderNavLink(
            text = s"${job.jobInfo.company} - ${job.jobInfo.title}",
            location = urls.job(job.id),
            cssClass = "job-title-link"
          )(
            Router.ChangeLocation(_)
          )
        ),
        JobComponents.renderJobSummary(job)
      ),
      div(`class` := "jvm-recent-jobs-card-btn-apply")(
        a(href := job.jobInfo.externalUrl, target := "blank")(
          button(`type` := "button", `class` := "btn btn-danger")("Apply")
        )
      )
    )

  def optRenderDetail(icon: String, value: Option[String]): Html[App.Msg] =
    value.map(v => renderDetail(icon, v)).getOrElse(div())

  def renderDetail(icon: String, value: String): Html[App.Msg] =
    div(`class` := "job-detail")(
      i(`class` := s"fa fa-$icon job-detail-icon")(),
      p(`class` := "job-detail-value")(value)
    )

  def renderJobSummary(job: Job): Html[App.Msg] =
    div(`class` := "job-summary")(
      renderDetail("dollar", salaryText(job)),
      renderDetail("location-dot", locationText(job)),
      optRenderDetail("ranking-star", job.jobInfo.seniority),
      optRenderDetail("tags", job.jobInfo.tags.map(_.mkString(", ")))
    )

  def locationText(job: Job) = job.jobInfo.country.fold(job.jobInfo.location)(c => s"$c, ${job.jobInfo.location}")

  def salaryText(job: Job) =
    val currencyText = job.jobInfo.currency.getOrElse("")

    (job.jobInfo.salaryLow, job.jobInfo.salaryHigh) match
      case (Some(low), Some(high)) => s"$currencyText $low-$high"
      case (Some(low), None)       => s"> $currencyText $low"
      case (None, Some(high))      => s"<= $currencyText $high"
      case _                       => "N/A"
