package com.jobaroo.components

import tyrian.*
import tyrian.Html.*
import com.jobaroo.core.*
import scala.scalajs.js
import scala.scalajs.js.annotation.*
import com.jobaroo.pages.Page.*
import com.jobaroo.App

object Header:

  def view =
    div(`class` := "header-container")(
      logo,
      div(`class` := "header-nav")(
        ul(`class` := "header-links")(
          renderNavLinks()
        )
      )
    )

  private def renderNavLinks(): List[Html[App.Msg]] =
    val constantLinks = List(renderNavLink("Jobs", urls.jobs)(Router.ChangeLocation(_)))

    val unauthedLinks = List(
      renderNavLink("Sign Up", urls.signup)(Router.ChangeLocation(_)),
      renderNavLink("Login", urls.login)(Router.ChangeLocation(_))
    )

    val authedLinks = List(
      renderNavLink("Profile", urls.profile)(Router.ChangeLocation(_)),
      renderNavLink("Log Out", urls.hash)(_ => Session.Logout)
    )

    constantLinks ++ (if Session.isActive then authedLinks else unauthedLinks)

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

  private def renderNavLink(text: String, location: String)(location2msg: String => App.Msg) =
    li(`class` := "nav-item")(
      a(
        href    := location,
        `class` := "nav-link",
        onEvent(
          "click",
          e =>
            e.preventDefault()
            location2msg(location)
        )
      )(text)
    )
