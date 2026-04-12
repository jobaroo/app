package com.jobaroo.components

import tyrian.*
import tyrian.Html.*
import com.jobaroo.App
import com.jobaroo.pages.Page.*
import com.jobaroo.tyrianui.core.UiAttrs
import com.jobaroo.tyrianui.daisy.Navigation
import com.jobaroo.tyrianui.html.Tags.{aside, h3, nav, p}
import com.jobaroo.ui.preset.Jobaroo

object Footer:

  def view: Html[App.Msg] =
    Navigation.footer(UiAttrs.classes(Jobaroo.footer.root))(
      aside(UiAttrs.classes(Jobaroo.footer.aside))(
        p(UiAttrs.classes(Jobaroo.footer.eyebrow))(text("Built for focused hiring")),
        h3(UiAttrs.classes(Jobaroo.footer.title))(text("The job board for serious JVM teams.")),
        p(UiAttrs.classes(Jobaroo.footer.description))(
          text("Search roles faster, compare compensation clearly, and publish openings with a cleaner recruiter workflow.")
        )
      ),
      nav(UiAttrs.classes(Jobaroo.footer.nav))(
        Anchors.renderAuxLink(urls.jobs, "Browse Jobs"),
        Anchors.renderAuxLink(urls.postJob, "Post a Job"),
        Anchors.renderAuxLink(urls.signup, "Create Account"),
        Anchors.renderAuxLink(urls.login, "Log In")
      ),
      p(UiAttrs.classes(Jobaroo.footer.caption))(text("Scala, Java, and backend roles for teams that care about craft."))
    )
