package com.jobaroo.components

import cats.syntax.semigroup.*
import tyrian.*
import tyrian.Html.*
import com.jobaroo.App
import com.jobaroo.core.Router
import com.jobaroo.domain.job.Job
import com.jobaroo.pages.Page.urls
import com.jobaroo.tyrianui.core.UiAttrs
import com.jobaroo.tyrianui.daisy.Badge
import com.jobaroo.tyrianui.html.Tags.{a, div, h2, p, span}
import com.jobaroo.tyrianui.icons.Icons
import com.jobaroo.ui.core.Css
import com.jobaroo.ui.preset.Jobaroo

object JobComponents:

  def renderJob(job: Job): Html[App.Msg] =
    val route = urls.job(job.id)
    val headerBadge =
      if job.jobInfo.remote then Badge.render("Remote", Badge.Tone.Primary)
      else job.jobInfo.seniority.fold(Badge.render("Full-time", Badge.Tone.Outline))(value => Badge.render(value, Badge.Tone.Outline))

    val attrs =
      UiAttrs(href := route) |+|
        UiAttrs.classes(Jobaroo.jobs.summaryCard |+| Jobaroo.surface.interactive |+| Jobaroo.jobs.clickableCard) |+|
        UiAttrs(
          onEvent(
            "click",
            e =>
              e.preventDefault()
              (Router.ChangeLocation(route): App.Msg)
          )
        )

    a[App.Msg](attrs)(
      div(UiAttrs.classes(Jobaroo.jobs.cardBody |+| Jobaroo.jobs.cardLayout))(
        div(UiAttrs.classes(Jobaroo.jobs.previewRow))(
          div(UiAttrs.classes(Jobaroo.jobs.copyColumn))(
            h2(UiAttrs.classes(Jobaroo.jobs.title))(text(job.jobInfo.title)),
            p(UiAttrs.classes(Jobaroo.jobs.company))(text(job.jobInfo.company)),
            div(UiAttrs.classes(Jobaroo.jobs.metaRow))(
              metaInline(Icons.mapPin(Jobaroo.icon.small), locationText(job))
            )
          ),
          headerBadge
        ),
        p(UiAttrs.classes(Jobaroo.jobs.salary))(text(salaryText(job))),
        p(UiAttrs.classes(Jobaroo.jobs.description))(text(descriptionPreview(job))),
        div(UiAttrs.classes(Jobaroo.jobs.metaRow))(
          renderTags(job)*
        ),
        div(UiAttrs.classes(Jobaroo.jobs.footerRow))(
          span(UiAttrs.classes(Jobaroo.jobs.footerMuted))(text(relativePosted(job.date))),
          span(UiAttrs.classes(Jobaroo.jobs.footerLink))(text("View Job →"))
        )
      )
    )

  def renderDetail(icon: Html[App.Msg], value: String): Html[App.Msg] =
    div(UiAttrs.classes(Jobaroo.jobs.detailPill))(
      icon,
      span()(text(value))
    )

  def renderJobSummary(job: Job): Html[App.Msg] =
    div(UiAttrs.classes(Jobaroo.jobs.metaRow))(
      renderMeta(job)*
    )

  def locationText(job: Job): String =
    if job.jobInfo.remote then "Remote Worldwide"
    else job.jobInfo.country.fold(job.jobInfo.location)(c => s"${job.jobInfo.location}, $c")

  def salaryText(job: Job): String =
    val currencyText = job.jobInfo.currency.getOrElse("")

    (job.jobInfo.salaryLow, job.jobInfo.salaryHigh) match
      case (Some(low), Some(high)) if currencyText.nonEmpty => s"$currencyText $low - $currencyText $high"
      case (Some(low), Some(high))                          => s"$low - $high"
      case (Some(low), None) if currencyText.nonEmpty       => s"$currencyText $low+"
      case (Some(low), None)                                => s"$low+"
      case (None, Some(high)) if currencyText.nonEmpty      => s"Up to $currencyText $high"
      case (None, Some(high))                               => s"Up to $high"
      case _                                                => "Salary not specified"

  private def descriptionPreview(job: Job): String =
    val preview = PreviewText.fromMarkdown(PreviewText.withoutLeadingTitle(job.jobInfo.description, job.jobInfo.title)).take(140)

    if preview.nonEmpty then preview else "No summary provided."

  private def renderMeta(job: Job): List[Html[App.Msg]] =
    List(
      renderDetail(Icons.banknotes(Jobaroo.icon.small), salaryText(job)),
      renderDetail(if job.jobInfo.remote then Icons.globe(Jobaroo.icon.small) else Icons.mapPin(Jobaroo.icon.small), locationText(job))
    ) ++ job.jobInfo.seniority.toList.map(value => renderDetail(Icons.briefcase(Jobaroo.icon.small), value))

  private def renderTags(job: Job): List[Html[App.Msg]] =
    job.jobInfo.tags.getOrElse(Nil).take(3).map(tag => Badge.render(tag, Badge.Tone.Outline))

  private def metaInline(icon: Html[App.Msg], value: String): Html[App.Msg] =
    span(UiAttrs.classes(Css.literal("inline-flex items-center gap-1 text-sm text-base-content/70")))(
      icon,
      span()(text(value))
    )

  private def relativePosted(timestamp: Long): String =
    val dayMs = 24L * 60L * 60L * 1000L
    val days  = math.max(0L, (System.currentTimeMillis() - timestamp) / dayMs)

    days match
      case 0 => "Posted today"
      case 1 => "Posted 1 day ago"
      case n => s"Posted $n days ago"
