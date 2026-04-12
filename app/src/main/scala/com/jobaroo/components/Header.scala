package com.jobaroo.components

import cats.syntax.semigroup.*
import tyrian.*
import tyrian.Html.*
import com.jobaroo.App
import com.jobaroo.common.constants
import com.jobaroo.core.*
import com.jobaroo.pages.Page.*
import com.jobaroo.tyrianui.core.UiAttrs
import com.jobaroo.tyrianui.daisy.Button
import com.jobaroo.tyrianui.daisy.Navigation
import com.jobaroo.tyrianui.html.Tags.{a, button, div, h1, img, p, span}
import com.jobaroo.tyrianui.icons.Icons
import com.jobaroo.ui.preset.Jobaroo
import com.jobaroo.ui.theme.ThemeName
import org.scalajs.dom

object Header:

  def view: Html[App.Msg] =
    Navigation.navbar(UiAttrs.classes(Jobaroo.nav.sticky |+| Jobaroo.nav.navbar))(
      div(UiAttrs.classes(Jobaroo.nav.start))(
        logo,
        div(UiAttrs.classes(Jobaroo.nav.desktopCopy))(
          p(UiAttrs.classes(Jobaroo.nav.subtitle))(text("JVM Jobs Platform")),
          h1(UiAttrs.classes(Jobaroo.nav.title))(text("Jobaroo"))
        )
      ),
      div(UiAttrs.classes(Jobaroo.nav.center))(
        Navigation.menu[App.Msg](UiAttrs.classes(Jobaroo.nav.menu))(renderNavLinks()*)
      ),
      div(UiAttrs.classes(Jobaroo.nav.end))(
        themeToggle,
        sessionActions
      )
    )

  private def renderNavLinks(): List[Html[App.Msg]] =
    val constantLinks = List(
      navMenuLink("Jobs", urls.jobs),
      navMenuLink("Post Job", urls.postJob)
    )

    val unauthedLinks = List(
      navMenuLink("Sign Up", urls.signup),
      navMenuLink("Login", urls.login)
    )

    val authedLinks = List(
      navMenuLink("Profile", urls.profile),
      Navigation.menuItem(
        button(UiAttrs.classes(Jobaroo.nav.link) |+| UiAttrs(onClick(Session.Logout)))(
          text("Log Out")
        )
      )
    )

    constantLinks ++ (if Session.isActive then authedLinks else unauthedLinks)

  private def navMenuLink(textValue: String, location: String): Html[App.Msg] =
    Navigation.menuItem(
      Anchors.renderNavLink(
        text = textValue,
        location = location,
        classes = Jobaroo.nav.link
      )(Router.ChangeLocation(_))
    )

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
      div(UiAttrs.classes(Jobaroo.nav.logoCopy))(
        p(UiAttrs.classes(Jobaroo.section.eyebrow))(text("Hiring better")),
        p(UiAttrs.classes(Jobaroo.nav.logoTitle))(text("Jobaroo"))
      )
    )

  private def themeToggle: Html[App.Msg] =
    val attrs =
      UiAttrs(`type` := "button") |+|
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
          Button.render(
            Button.props[App.Msg]("Profile").copy(
              tone = Button.Tone.Ghost,
              attrs = UiAttrs.classes(Jobaroo.button.ghostSurface),
              onPress = Some(Router.ChangeLocation(urls.profile))
            )
          ),
          Button.render(
            Button.props[App.Msg]("Log Out").copy(
              tone = Button.Tone.Primary,
              onPress = Some(Session.Logout)
            )
          )
        )
      else
        List(
          Button.render(
            Button.props[App.Msg]("Login").copy(
              tone = Button.Tone.Ghost,
              attrs = UiAttrs.classes(Jobaroo.button.ghostSurface),
              onPress = Some(Router.ChangeLocation(urls.login))
            )
          ),
          Button.render(
            Button.props[App.Msg]("Sign Up").copy(
              tone = Button.Tone.Primary,
              onPress = Some(Router.ChangeLocation(urls.signup))
            )
          )
        )

    div(UiAttrs.classes(Jobaroo.nav.sessionRow))(actions*)
