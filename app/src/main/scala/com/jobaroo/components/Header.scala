package com.jobaroo.components

import tyrian.*
import tyrian.Html.*
import com.jobaroo.core.*
import scala.scalajs.js
import scala.scalajs.js.annotation.*
import com.jobaroo.pages.Page.*

object Header:

  def view =
    div(`class` := "header-container")(
      logo,
      div(`class` := "header-nav")(
        ul(`class` := "header-links")(
          renderNavLink("Jobs", urls.jobs),
          renderNavLink("Sign Up", urls.signup),
          renderNavLink("Login", urls.login)
        )
      )
    )

  @js.native
  @JSImport("url:/static/img/jobaroo.png", JSImport.Default)
  private val logoImage: String = js.native

  private def logo =
    a(
      href := "/",
      onEvent(
        "click",
        e =>
          e.preventDefault()
          Router.ChangeLocation("/")
      )
    )(img(`class` := "home-logo", src := logoImage, alt := "Jobaroo"))

  private def renderNavLink(text: String, location: String) =
    li(`class` := "nav-item")(
      a(
        href    := location,
        `class` := "nav-link",
        onEvent(
          "click",
          e =>
            e.preventDefault()
            Router.ChangeLocation(location)
        )
      )(text)
    )
