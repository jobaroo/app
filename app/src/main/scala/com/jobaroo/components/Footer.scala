package com.jobaroo.components

import cats.syntax.semigroup.*
import tyrian.*
import tyrian.Html.*
import com.jobaroo.App
import com.jobaroo.common.constants
import com.jobaroo.core.Session
import com.jobaroo.pages.Page.*
import com.jobaroo.tyrianui.core.UiAttrs
import com.jobaroo.tyrianui.html.Tags.{Children, aside, div, h3, img, nav, p}
import com.jobaroo.ui.preset.Jobaroo

object Footer:

  def view: Html[App.Msg] =
    val accountLinks =
      if Session.isActive then
        Seq(Anchors.renderAuxLink(urls.profile, "Profile", Jobaroo.footer.link))
      else
        Seq(
          Anchors.renderAuxLink(urls.login, "Log In", Jobaroo.footer.link),
          Anchors.renderAuxLink(urls.signup, "Create Account", Jobaroo.footer.link),
          Anchors.renderAuxLink(urls.forgotPassword, "Forgot Password", Jobaroo.footer.link)
        )

    div(UiAttrs.classes(Jobaroo.footer.outer))(
      div(UiAttrs.classes(Jobaroo.footer.root))(
        aside(UiAttrs.classes(Jobaroo.footer.aside))(
          img(UiAttrs.classes(Jobaroo.footer.logo) |+| UiAttrs(src := constants.logoImage, alt := "Jobaroo")),
          h3(UiAttrs.classes(Jobaroo.footer.title))(text("Jobaroo")),
          p(UiAttrs.classes(Jobaroo.footer.description))(text("Hop into your next career with a focused, no-noise JVM hiring experience."))
        ),
        footerColumn(
          "Browse",
          Anchors.renderAuxLink(urls.jobs, "Browse Jobs", Jobaroo.footer.link),
          Anchors.renderAuxLink(urls.postJob, "Post a Job", Jobaroo.footer.link)
        ),
        footerColumn("Account", accountLinks*)
      ),
      p(UiAttrs.classes(Jobaroo.footer.caption))(text("© 2026 Jobaroo. All rights reserved."))
    )

  private def footerColumn(title: String, children: Html[App.Msg]*): Html[App.Msg] =
    nav(UiAttrs.classes(Jobaroo.footer.nav))(
      Children.concat(
        Children.one(p(UiAttrs.classes(Jobaroo.footer.columnTitle))(text(title))),
        children
      )
    )
