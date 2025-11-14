package com.jobaroo.components

import tyrian.*
import tyrian.Html.*
import com.jobaroo.core.*
import com.jobaroo.pages.Page.*
import com.jobaroo.App
import com.jobaroo.components.*
import com.jobaroo.common.constants

object Header:

  def view =
    div(`class` := "container-fluid p-0")(
      div(`class` := "jvm-nav")(
        div(`class` := "container")(
          nav(`class` := "navbar navbar-expand-lg navbar-light JVM-nav")(
            div(`class` := "container")(
              logo,
              button(
                `class` := "navbar-toggler",
                `type`  := "button",
                attribute("data-bs-toggle", "collapse"),
                attribute("data-bs-target", "#navbarNav"),
                attribute("aria-controls", "navbarNav"),
                attribute("aria-expanded", "false"),
                attribute("aria-label", "Toggle navigation")
              )(
                span(`class` := "navbar-toggler-icon")()
              ),
              div(`class` := "collapse navbar-collapse", id := "navbarNav")(
                ul(`class` := "navbar-nav ms-auto menu align-center expanded text-center SMN_effect-3")(
                  renderNavLinks()
                )
              )
            )
          )
        )
      )
    )

  private def renderNavLinks(): List[Html[App.Msg]] =
    val constantLinks = List(
      renderNavLink("Jobs", urls.jobs)(Router.ChangeLocation(_)),
      renderNavLink("Post Job", urls.postJob)(Router.ChangeLocation(_))
    )

    val unauthedLinks = List(
      renderNavLink("Sign Up", urls.signup)(Router.ChangeLocation(_)),
      renderNavLink("Login", urls.login)(Router.ChangeLocation(_))
    )

    val authedLinks = List(
      renderNavLink("Profile", urls.profile)(Router.ChangeLocation(_)),
      renderNavLink("Log Out", urls.hash)(_ => Session.Logout)
    )

    constantLinks ++ (if Session.isActive then authedLinks else unauthedLinks)

  def renderNavLink(text: String, location: String)(location2msg: String => App.Msg) =
    li(`class` := "nav-item")(
      Anchors.renderNavLink(text, location, "nav-link jvm-item Home active-item")(location2msg)
    )

  private def logo =
    a(
      href    := "/",
      `class` := "navbar-brand",
      onEvent(
        "click",
        e =>
          e.preventDefault()
          Router.ChangeLocation("/")
      )
    )(img(`class` := "home-logo", src := constants.logoImage, alt := "Jobaroo"))
