package com.jobaroo.components

import cats.syntax.semigroup.*
import tyrian.*
import tyrian.Html.*
import com.jobaroo.App
import com.jobaroo.common.constants
import com.jobaroo.core.Router
import com.jobaroo.domain.job.Job
import com.jobaroo.pages.Page.urls
import com.jobaroo.tyrianui.core.UiAttrs
import com.jobaroo.tyrianui.daisy.Badge
import com.jobaroo.tyrianui.daisy.Button
import com.jobaroo.tyrianui.html.Tags.{div, h2, img, p, span}
import com.jobaroo.tyrianui.icons.Icons
import com.jobaroo.ui.preset.Jobaroo

object JobComponents:

  def renderJob(job: Job): Html[App.Msg] =
    val roleBadges =
      Badge.render(if job.jobInfo.remote then "Remote" else "On-site", if job.jobInfo.remote then Badge.Tone.Primary else Badge.Tone.Outline) ::
        job.jobInfo.seniority.toList.map(value => Badge.render(value, Badge.Tone.Outline))

    div(UiAttrs.classes(Jobaroo.surface.card |+| Jobaroo.surface.interactive))(
      div(UiAttrs.classes(Jobaroo.jobs.cardBody |+| Jobaroo.jobs.cardLayout))(
        div(UiAttrs.classes(Jobaroo.jobs.previewRow))(
          div(UiAttrs.classes(Jobaroo.jobs.avatarWrap))(
            div(UiAttrs.classes(Jobaroo.jobs.avatarFrame))(
              img(
                UiAttrs(src := job.jobInfo.image.getOrElse(constants.fallbackImage), alt := job.jobInfo.title) |+|
                  UiAttrs.classes(Jobaroo.jobs.avatarImage)
              )
            )
          ),
          div(UiAttrs.classes(Jobaroo.jobs.copyColumn))(
            div(UiAttrs.classes(Jobaroo.jobs.heading))(
              div(UiAttrs.classes(Jobaroo.jobs.metaRow))(
                roleBadges*
              ),
              p(UiAttrs.classes(Jobaroo.jobs.company))(text(job.jobInfo.company)),
              h2(UiAttrs.classes(Jobaroo.jobs.title))(
                Anchors.renderNavLink(
                  text = job.jobInfo.title,
                  location = urls.job(job.id),
                  classes = Jobaroo.jobs.titleLink
                )(Router.ChangeLocation(_))
              )
            ),
            p(UiAttrs.classes(Jobaroo.jobs.description))(text(descriptionPreview(job))),
            renderJobSummary(job),
            renderTags(job)
          )
        ),
        div(UiAttrs.classes(Jobaroo.jobs.actionsStack))(
          p(UiAttrs.classes(Jobaroo.jobs.actionHint))(text("Apply on company site")),
          Button.link(
            Button.props[App.Msg]("Apply Now").copy(tone = Button.Tone.Primary),
            hrefValue = job.jobInfo.externalUrl,
            newTab = true
          )
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
    job.jobInfo.country.fold(job.jobInfo.location)(c => s"$c, ${job.jobInfo.location}")

  def salaryText(job: Job): String =
    val currencyText = job.jobInfo.currency.getOrElse("")

    (job.jobInfo.salaryLow, job.jobInfo.salaryHigh) match
      case (Some(low), Some(high)) => s"$currencyText $low-$high"
      case (Some(low), None)       => s"> $currencyText $low"
      case (None, Some(high))      => s"<= $currencyText $high"
      case _                       => "N/A"

  private def descriptionPreview(job: Job): String =
    val preview = PreviewText.fromMarkdown(job.jobInfo.description).take(180)

    if preview.nonEmpty then preview else "No summary provided."

  private def renderMeta(job: Job): List[Html[App.Msg]] =
    List(
      renderDetail(Icons.banknotes(Jobaroo.icon.small), salaryText(job)),
      renderDetail(Icons.mapPin(Jobaroo.icon.small), locationText(job))
    )

  private def renderTags(job: Job): Html[App.Msg] =
    div(UiAttrs.classes(Jobaroo.jobs.metaRow))(
      job.jobInfo.tags.getOrElse(Nil).take(3).map(tag => Badge.render(tag, Badge.Tone.Outline))*
    )
