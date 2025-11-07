package com.jobaroo.pages

import cats.effect.IO
import tyrian.*
import tyrian.Html.*
import com.jobaroo.App
import com.jobaroo.pages.Page
import com.jobaroo.core.Router

abstract class FormPage(title: String, status: Option[Page.Status]) extends Page:

  override def initCmd: Cmd[IO, App.Msg] = Cmd.None
  override def view: Html[App.Msg]       = renderForm()

  protected def renderFormContent(): List[Html[App.Msg]]

  protected def renderForm(): Html[App.Msg] =
    div(`class` := "form-section")(
      div(`class` := "top-section")(
        h1(title)
      ),
      form(
        name    := "sign-in",
        `class` := "form",
        onEvent(
          "submit",
          e =>
            e.preventDefault()
            App.NoOp
        )
      )(renderFormContent()),
      status.fold(div())(s => div(s.message))
    )

  protected def renderAuxLink(location: String, text: String): Html[App.Msg] = 
    a(
      href    := location,
      `class` := "aux-link",
      onEvent(
        "click",
        e =>
          e.preventDefault()
          Router.ChangeLocation(location)
      )
    )(text)
    
  protected def renderInput(name: String, uid: String, kind: String, isRequired: Boolean, onChange: String => App.Msg) =
    div(`class` := "form-input")(
      label(`for` := name, `class` := "form-label")(if isRequired then span("*") else span(), text(name)),
      input(`type` := kind, `class` := "form-control", id := uid, onInput(onChange))
    )
