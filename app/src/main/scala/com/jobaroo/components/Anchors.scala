package com.jobaroo.components

import cats.syntax.semigroup.*
import tyrian.*
import tyrian.http.*
import tyrian.Html.*
import com.jobaroo.App
import com.jobaroo.core.Router
import com.jobaroo.tyrianui.core.UiAttrs
import com.jobaroo.tyrianui.html.Tags.a
import com.jobaroo.ui.core.Css
import com.jobaroo.ui.preset.Jobaroo

object Anchors:

  def renderAuxLink(location: String, text: String, classes: Css = Jobaroo.nav.auxLink): Html[App.Msg] =
    val attrs =
      UiAttrs(href := location) |+|
        UiAttrs.classes(classes) |+|
        UiAttrs(
          onEvent(
            "click",
            e =>
              e.preventDefault()
              Router.ChangeLocation(location)
          )
        )

    a(attrs)(tyrian.Html.text(text))

  def renderNavLink(text: String, location: String, classes: Css = Css.empty)(location2msg: String => App.Msg) =
    val attrs =
      UiAttrs(href := location) |+|
        UiAttrs.classes(classes) |+|
        UiAttrs(
          onEvent(
            "click",
            e =>
              e.preventDefault()
              location2msg(location)
          )
        )

    a(attrs)(tyrian.Html.text(text))
