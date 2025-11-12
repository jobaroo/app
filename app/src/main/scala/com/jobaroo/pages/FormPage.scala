package com.jobaroo.pages

import cats.effect.IO
import tyrian.*
import tyrian.Html.*
import com.jobaroo.App
import com.jobaroo.pages.Page
import com.jobaroo.core.Router
import org.scalajs.dom.*
import org.scalajs.dom.HTMLInputElement

abstract class FormPage(title: String, status: Option[Page.Status]) extends Page:

  override def initCmd: Cmd[IO, App.Msg] = clearForm()

  override def view: Html[App.Msg] = renderForm()

  protected def renderFormContent(): List[Html[App.Msg]]

  protected def renderForm(): Html[App.Msg] =
    div(`class` := "form-section")(
      div(`class` := "top-section")(
        h1(title)
      ),
      form(
        name    := "sign-in",
        `class` := "form",
        id      := "form",
        onEvent(
          "submit",
          e =>
            e.preventDefault()
            App.NoOp
        )
      )(renderFormContent()),
      status.fold(div())(s => div(s.message))
    )

  protected def renderInput(name: String, uid: String, kind: String, isRequired: Boolean, onChange: String => App.Msg) =
    div(`class` := "form-input")(
      label(`for` := uid, `class` := "form-label")(if isRequired then span("*") else span(), text(name)),
      input(`type` := kind, `class` := "form-control", id := uid, onInput(onChange))
    )

  protected def renderTextArea(name: String, uid: String, isRequired: Boolean, onChange: String => App.Msg) =
    div(`class` := "form-input")(
      label(`for` := uid, `class` := "form-label")(if isRequired then span("*") else span(), text(name)),
      textarea(`class` := "form-control", id := uid, onInput(onChange))("")
    )

  protected def renderImageUploadInput(
    name: String,
    uid: String,
    imgSrc: Option[String],
    onChange: Option[File] => App.Msg
  ) =
    div(`class` := "form-input")(
      label(`for` := uid, `class` := "form-label")(name),
      input(
        `type`  := "file",
        `class` := "form-control",
        id      := uid,
        accept  := "image/*",
        onEvent(
          "change",
          e =>
            val imageInput = e.target.asInstanceOf[HTMLInputElement]
            val fileList   = imageInput.files
            onChange(Option.when(fileList.length > 0)(fileList(0)))
        )
      ),
      img(id    := "preview", src := imgSrc.getOrElse(""), alt := "Preview", width := "100", height := "100")
    )

  private def clearForm() =
    import scala.concurrent.duration.*

    def effect: IO[Option[HTMLFormElement]] =
      for
        optForm <- IO(Option(document.getElementById("form").asInstanceOf[HTMLFormElement]))
        eff     <- if optForm.isEmpty then IO.sleep(100.millis) *> effect else IO(optForm)
      yield eff

    Cmd.Run[IO, Unit, App.Msg](effect.map(_.foreach(_.reset())))(_ => App.NoOp)
