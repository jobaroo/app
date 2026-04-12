package com.jobaroo.components

import com.jobaroo.tyrianui.core.UiAttrs
import com.jobaroo.tyrianui.html.Tags.{div, h1, h2, p, span}
import com.jobaroo.ui.preset.Jobaroo
import tyrian.{Html => TyrianHtml}

object AppLayout:

  def appShell[Msg](children: TyrianHtml[Msg]*): TyrianHtml[Msg] =
    div(UiAttrs.classes(Jobaroo.shell.root))(
      div(UiAttrs.classes(Jobaroo.shell.inner))(children*)
    )

  def pageContainer[Msg](children: TyrianHtml[Msg]*): TyrianHtml[Msg] =
    div(UiAttrs.classes(Jobaroo.shell.page))(children*)

  def hero[Msg](
    title: String,
    subtitle: String,
    eyebrow: String,
    actions: Seq[TyrianHtml[Msg]] = Nil
  ): TyrianHtml[Msg] =
    div(UiAttrs.classes(Jobaroo.hero.root))(
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
