package com.jobaroo.components

import cats.syntax.semigroup.*
import tyrian.*
import tyrian.Html.*
import com.jobaroo.App
import com.jobaroo.common.constants
import com.jobaroo.core.*
import com.jobaroo.pages.Page.*
import com.jobaroo.tyrianui.core.UiAttrs
import com.jobaroo.tyrianui.html.Tags.{a, button, div, img, li, p, span, ul}
import com.jobaroo.tyrianui.icons.Icons
import com.jobaroo.ui.core.Css
import com.jobaroo.ui.preset.Jobaroo
import com.jobaroo.ui.theme.ThemeName
import org.scalajs.dom

object Header:

  def view: Html[App.Msg] =
    div(UiAttrs.classes(Jobaroo.nav.outer))(
      div(UiAttrs.classes(Jobaroo.nav.navbar))(
        div(UiAttrs.classes(Jobaroo.nav.start))(logo),
        div(UiAttrs.classes(Jobaroo.nav.center))(
          navLink("Jobs", urls.jobs, Jobaroo.nav.link, Some(Icons.briefcase(Jobaroo.icon.small))),
          navLink("Post Job", urls.postJob, Css.literal("btn btn-primary btn-sm font-medium normal-case"), Some(Icons.plus(Jobaroo.icon.small)))
        ),
        div(UiAttrs.classes(Jobaroo.nav.end))(
          themeToggle,
          sessionActions,
          mobileMenu
        )
      )
    )

  private def navLink(
    label: String,
    location: String,
    classes: Css,
    icon: Option[Html[App.Msg]] = None
  ): Html[App.Msg] =
    val attrs =
      UiAttrs(href := location) |+|
        UiAttrs.classes(classes) |+|
        UiAttrs(
          onEvent(
            "click",
            e =>
              e.preventDefault()
              (Router.ChangeLocation(location): App.Msg)
          )
        )

    val children = icon.toSeq :+ span()(text(label))

    a[App.Msg](attrs)(children*)

  private def logo: Html[App.Msg] =
    val attrs =
      UiAttrs(href := "/") |+|
        UiAttrs.classes(Jobaroo.nav.logo) |+|
        UiAttrs(
          onEvent(
            "click",
            e =>
              e.preventDefault()
              Router.ChangeLocation("/")
          )
        )

    a(attrs)(
      img(UiAttrs.classes(Jobaroo.nav.logoImage) |+| UiAttrs(src := constants.logoImage, alt := "Jobaroo")),
      span(UiAttrs.classes(Jobaroo.nav.logoTitle))(text("Jobaroo"))
    )

  private def themeToggle: Html[App.Msg] =
    val attrs =
      UiAttrs(`type` := "button", attribute("aria-label", "Toggle theme")) |+|
        UiAttrs.classes(Jobaroo.nav.themeBtn) |+|
        UiAttrs(
          onEvent(
            "click",
            _ =>
              val root         = dom.document.documentElement
              val currentTheme = Option(root.getAttribute("data-theme")).getOrElse(ThemeName.Light.value)
              val nextTheme    = if currentTheme == ThemeName.Dark.value then ThemeName.Light else ThemeName.Dark

              root.setAttribute("data-theme", nextTheme.value)
              dom.window.localStorage.setItem(ThemeName.storageKey, nextTheme.value)
              App.NoOp
          )
        )

    button(attrs)(
      span(UiAttrs.classes(Jobaroo.nav.themeLabel))(text("Theme")),
      span(UiAttrs.classes(Jobaroo.nav.darkOnly))(Icons.moon(Jobaroo.icon.small)),
      span(UiAttrs.classes(Jobaroo.nav.lightOnly))(Icons.sun(Jobaroo.icon.small))
    )

  private def sessionActions: Html[App.Msg] =
    val actions =
      if Session.isActive then
        List(
          navLink("Profile", urls.profile, Jobaroo.nav.link),
          button(
            UiAttrs(`type` := "button") |+|
              UiAttrs.classes(Css.literal("btn btn-primary btn-sm font-medium normal-case")) |+|
              UiAttrs(onClick(Session.Logout))
          )(text("Log Out"))
        )
      else
        List(
          navLink("Login", urls.login, Jobaroo.nav.link),
          navLink("Sign Up", urls.signup, Css.literal("btn btn-primary btn-sm font-medium normal-case"))
        )

    div(UiAttrs.classes(Jobaroo.nav.sessionRow))(actions*)

  private def mobileMenu: Html[App.Msg] =
    val items =
      List(
        mobileItem("Browse Jobs", urls.jobs),
        mobileItem("Post a Job", urls.postJob)
      ) ++
        (if Session.isActive then
           List(
             mobileItem("Profile", urls.profile),
             li()(
               button(
                 UiAttrs(`type` := "button") |+| UiAttrs.classes(Css.literal("w-full text-left")) |+| UiAttrs(onClick(Session.Logout))
               )(text("Log Out"))
             )
           )
         else
           List(
             mobileItem("Login", urls.login),
             mobileItem("Sign Up", urls.signup)
           ))

    div(UiAttrs.classes(Jobaroo.nav.mobileMenu))(
      button(
        UiAttrs(attribute("tabindex", "0"), attribute("aria-label", "Open menu"), `type` := "button") |+|
          UiAttrs.classes(Jobaroo.nav.mobileMenuButton)
      )(Icons.bars3(Jobaroo.icon.regular)),
      ul(UiAttrs(attribute("tabindex", "0")) |+| UiAttrs.classes(Jobaroo.nav.mobileDropdown))(items*)
    )

  private def mobileItem(label: String, location: String): Html[App.Msg] =
    li()(
      navLink(label, location, Css.literal("font-medium"))
    )
