package com.jobaroo.components

import cats.syntax.semigroup.*
import com.jobaroo.App
import com.jobaroo.core.Router
import com.jobaroo.tyrianui.core.UiAttrs
import com.jobaroo.tyrianui.html.Tags.{button, div, h1, h2, p, section, span}
import com.jobaroo.tyrianui.icons.Icons
import com.jobaroo.ui.core.Css
import com.jobaroo.ui.preset.Jobaroo
import tyrian.{Html => TyrianHtml}
import tyrian.Html.*

object AppLayout:

  def appShell[Msg](children: TyrianHtml[Msg]*): TyrianHtml[Msg] =
    div(UiAttrs.classes(Jobaroo.shell.root))(
      div(UiAttrs.classes(Jobaroo.shell.inner))(children*)
    )

  def pageContainer[Msg](children: TyrianHtml[Msg]*): TyrianHtml[Msg] =
    div(UiAttrs.classes(Jobaroo.shell.page))(children*)

  def contentWidth[Msg](children: TyrianHtml[Msg]*): TyrianHtml[Msg] =
    div(UiAttrs.classes(Jobaroo.shell.contentWidth))(children*)

  def narrowWidth[Msg](children: TyrianHtml[Msg]*): TyrianHtml[Msg] =
    div(UiAttrs.classes(Jobaroo.shell.narrowWidth))(children*)

  def hero[Msg](
    title: String,
    subtitle: String,
    eyebrow: String,
    actions: Seq[TyrianHtml[Msg]] = Nil
  ): TyrianHtml[Msg] =
    section(UiAttrs.classes(Jobaroo.hero.root))(
      div(UiAttrs.classes(Jobaroo.hero.content))(
        div(UiAttrs.classes(Jobaroo.hero.grid))(
          div(UiAttrs.classes(Jobaroo.hero.textStack))(
            span(UiAttrs.classes(Jobaroo.hero.eyebrow))(tyrian.Html.text(eyebrow)),
            h1(UiAttrs.classes(Jobaroo.hero.title))(tyrian.Html.text(title)),
            p(UiAttrs.classes(Jobaroo.hero.subtitle))(tyrian.Html.text(subtitle))
          ),
          div(UiAttrs.classes(Jobaroo.hero.actions))(actions*)
        )
      )
    )

  def heroStat[Msg](value: String, label: String): TyrianHtml[Msg] =
    div(UiAttrs.classes(Jobaroo.hero.statCard))(
      p(UiAttrs.classes(Jobaroo.hero.statValue))(tyrian.Html.text(value)),
      p(UiAttrs.classes(Jobaroo.hero.statLabel))(tyrian.Html.text(label))
    )

  def heroStatsRow[Msg](items: TyrianHtml[Msg]*): TyrianHtml[Msg] =
    div(UiAttrs.classes(Jobaroo.hero.statsRow))(items*)

  def split[Msg](sidebar: TyrianHtml[Msg], content: TyrianHtml[Msg]): TyrianHtml[Msg] =
    div(UiAttrs.classes(Jobaroo.shell.split))(
      sidebar,
      content
    )

  def sectionTitle[Msg](eyebrow: String, title: String, subtitle: String): TyrianHtml[Msg] =
    div(UiAttrs.classes(Jobaroo.section.wrap))(
      p(UiAttrs.classes(Jobaroo.section.eyebrow))(tyrian.Html.text(eyebrow)),
      h2(UiAttrs.classes(Jobaroo.section.title))(tyrian.Html.text(title)),
      p(UiAttrs.classes(Jobaroo.section.subtitle))(tyrian.Html.text(subtitle))
    )

  def backLink(
    label: String = "Back",
    fallback: String = "/",
    classes: Css = Jobaroo.section.backLink
  ): TyrianHtml[App.Msg] =
    button(
      UiAttrs(`type` := "button", attribute("aria-label", label)) |+|
        UiAttrs.classes(classes) |+|
        UiAttrs(onClick(Router.GoBack(fallback)))
    )(
      Icons.arrowLeft(Jobaroo.icon.small),
      span()(tyrian.Html.text(label))
    )
