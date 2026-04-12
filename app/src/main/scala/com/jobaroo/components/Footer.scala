package com.jobaroo.components

import tyrian.*
import tyrian.Html.*
import com.jobaroo.App
import com.jobaroo.pages.Page.*
import com.jobaroo.tyrianui.core.UiAttrs
import com.jobaroo.tyrianui.daisy.Navigation
import com.jobaroo.tyrianui.html.Tags.{a, aside, h3, nav, p}
import com.jobaroo.ui.preset.Jobaroo

object Footer:

  def view: Html[App.Msg] =
    Navigation.footer(UiAttrs.classes(Jobaroo.footer.root))(
      aside(UiAttrs.classes(Jobaroo.footer.aside))(
        p(UiAttrs.classes(Jobaroo.footer.eyebrow))(text("Built for focused hiring")),
        h3(UiAttrs.classes(Jobaroo.footer.title))(text("The JVM jobs board, rebuilt with discipline.")),
        p(UiAttrs.classes(Jobaroo.footer.description))(
          text("Fast search, clearer job cards, cleaner forms, and no styling debt leaked into page logic.")
        )
      ),
      nav(UiAttrs.classes(Jobaroo.footer.nav))(
        Anchors.renderAuxLink(urls.jobs, "Browse Jobs"),
        Anchors.renderAuxLink(urls.postJob, "Post a Job"),
        Anchors.renderAuxLink(urls.login, "Log In"),
        a(UiAttrs(href := "https://typelevel.org/cats/", target := "_blank", rel := "noreferrer"))(text("Cats"))
      ),
      p(UiAttrs.classes(Jobaroo.footer.caption))(text("Scala.js, Tyrian, Cats, Tailwind CSS, and daisyUI."))
    )
