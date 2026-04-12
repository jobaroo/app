package com.jobaroo.pages

import tyrian.*
import cats.effect.IO
import com.jobaroo.App
import com.jobaroo.components.AppLayout
import com.jobaroo.pages.Page.urls
import com.jobaroo.tyrianui.core.UiAttrs
import com.jobaroo.tyrianui.daisy.Card
import com.jobaroo.tyrianui.html.Tags.{div, h1, p}
import com.jobaroo.ui.preset.Jobaroo

final case class NotFoundPage() extends Page:

  override def initCmd: Cmd[IO, App.Msg]                      = Cmd.None
  override def update(msg: App.Msg): (Page, Cmd[IO, App.Msg]) = (this, Cmd.None)

  override def view: Html[App.Msg] =
    AppLayout.pageContainer(
      div(UiAttrs.classes(Jobaroo.state.centered))(
        Card.surface(UiAttrs.classes(Jobaroo.surface.card))(
          Card.body(UiAttrs.classes(Jobaroo.surface.bodySpacious))(
            p(UiAttrs.classes(Jobaroo.section.eyebrow))(tyrian.Html.text("404")),
            h1(UiAttrs.classes(Jobaroo.notFound.title))(tyrian.Html.text("This page doesn't exist.")),
            p(UiAttrs.classes(Jobaroo.notFound.description))(
              tyrian.Html.text("The route could not be found. Use the main navigation to get back to the active hiring pages.")
            ),
            AppLayout.backLink(fallback = urls.home)
          )
        )
      )
    )
