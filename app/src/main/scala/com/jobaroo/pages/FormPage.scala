package com.jobaroo.pages

import cats.effect.IO
import cats.syntax.semigroup.*
import tyrian.*
import tyrian.Html.*
import com.jobaroo.App
import com.jobaroo.components.AppLayout
import org.scalajs.dom.*
import org.scalajs.dom.HTMLInputElement
import com.jobaroo.tyrianui.core.UiAttrs
import com.jobaroo.tyrianui.daisy.Button
import com.jobaroo.tyrianui.daisy.Card
import com.jobaroo.tyrianui.daisy.Field
import com.jobaroo.tyrianui.daisy.Feedback
import com.jobaroo.tyrianui.html.Tags.{div, fieldset, form, h1, img, label, p}
import com.jobaroo.ui.core.UiId
import com.jobaroo.ui.preset.Jobaroo

abstract class FormPage(title: String, status: Option[Page.Status]) extends Page:

  override def initCmd: Cmd[IO, App.Msg] = clearForm()

  override def view: Html[App.Msg] = renderForm()

  protected def renderFormContent(): List[Html[App.Msg]]

  protected def renderForm(): Html[App.Msg] =
    AppLayout.pageContainer(
      div(UiAttrs.classes(Jobaroo.form.scaffold))(
        div(UiAttrs.classes(Jobaroo.form.marketing))(
          div(UiAttrs.classes(Jobaroo.form.marketingText))(
            p(UiAttrs.classes(Jobaroo.form.marketingEyebrow))(text("Jobaroo Account")),
            h1(UiAttrs.classes(Jobaroo.form.marketingTitle))(text(title)),
            p(UiAttrs.classes(Jobaroo.form.marketingSubtitle))(
              text("A cleaner hiring flow starts with a predictable UI surface. This branch keeps the application behavior intact while replacing the presentation layer.")
            )
          ),
          div(UiAttrs.classes(Jobaroo.form.marketingStats))(
            statCard("Fast", "Tyrian-powered flow with predictable state transitions."),
            statCard("Typed", "Cats-based composition primitives and immutable props.")
          )
        ),
        Card.surface(UiAttrs.classes(Jobaroo.surface.card |+| Jobaroo.surface.fullHeight))(
          Card.body(UiAttrs.classes(Jobaroo.surface.bodySpacious))(
            AppLayout.sectionTitle("Secure access", title, "No backend behavior changes. UI concerns only."),
            renderStatus,
            form(
              UiAttrs(name := "sign-in", id := "form") |+|
                UiAttrs.classes(Jobaroo.form.grid) |+|
                UiAttrs(
                  onEvent(
                    "submit",
                    e =>
                      e.preventDefault()
                      App.NoOp
                  )
                )
            )(renderFormContent()*)
          )
        )
      )
    )

  private def renderStatus: Html[App.Msg] =
    status.fold(div()) { s =>
      val tone = s.kind match
        case Page.Kind.SUCCESS => Feedback.Tone.Success
        case Page.Kind.ERROR   => Feedback.Tone.Error
        case Page.Kind.LOADING => Feedback.Tone.Info

      Feedback.alert(s.message, tone)
    }

  private def statCard(titleValue: String, description: String): Html[App.Msg] =
    div(UiAttrs.classes(Jobaroo.form.statCard))(
      p(UiAttrs.classes(Jobaroo.form.statTitle))(text(titleValue)),
      p(UiAttrs.classes(Jobaroo.form.statDescription))(text(description))
    )

  protected def renderPrimaryAction(label: String, onPress: App.Msg): Html[App.Msg] =
    Button.render(
      Button.props[App.Msg](label).copy(
        width = Button.Width.Full,
        onPress = Some(onPress)
      )
    )

  protected def renderInput(
    name: String,
    uid: String,
    kind: String,
    isRequired: Boolean,
    currentValue: String = "",
    onChange: String => App.Msg
  ): Html[App.Msg] =
    val fieldKind = kind match
      case "email"    => Field.InputKind.Email
      case "password" => Field.InputKind.Password
      case "number"   => Field.InputKind.Number
      case "url"      => Field.InputKind.Url
      case _          => Field.InputKind.Text

    Field.textInput(
      meta = Field.Meta.dynamic(UiId.sanitized(uid), name, required = isRequired),
      currentValue = currentValue,
      onValue = onChange,
      kind = fieldKind,
      fieldAttrs = UiAttrs.classes(Jobaroo.form.compactFieldset),
      labelAttrs = UiAttrs.classes(Jobaroo.form.fieldLabel),
      hintAttrs = UiAttrs.classes(Jobaroo.form.fieldHint)
    )

  protected def renderToggle(
    name: String,
    uid: String,
    isRequired: Boolean,
    checkedValue: Boolean,
    onChange: Boolean => App.Msg,
    hint: Option[String] = None
  ): Html[App.Msg] =
    Field.toggleField(
      meta = Field.Meta.dynamic(UiId.sanitized(uid), name, required = isRequired, hint = hint),
      checkedValue = checkedValue,
      onChangeValue = onChange,
      wrapperAttrs = UiAttrs.classes(Jobaroo.form.fileLabel),
      copyAttrs = UiAttrs.classes(Jobaroo.form.fileCopy),
      titleAttrs = UiAttrs.classes(Jobaroo.form.fileTitle),
      hintAttrs = UiAttrs.classes(Jobaroo.form.fileDescription),
      controlAttrs = UiAttrs.classes(Jobaroo.button.ghostSurface)
    )

  protected def renderTextArea(
    name: String,
    uid: String,
    isRequired: Boolean,
    currentValue: String = "",
    onChange: String => App.Msg
  ): Html[App.Msg] =
    Field.textAreaField(
      meta = Field.Meta.dynamic(UiId.sanitized(uid), name, required = isRequired),
      currentValue = currentValue,
      onValue = onChange,
      fieldAttrs = UiAttrs.classes(Jobaroo.form.compactFieldset),
      labelAttrs = UiAttrs.classes(Jobaroo.form.fieldLabel),
      hintAttrs = UiAttrs.classes(Jobaroo.form.fieldHint)
    )

  protected def renderImageUploadInput(
    name: String,
    uid: String,
    imgSrc: Option[String],
    onChange: Option[File] => App.Msg
  ): Html[App.Msg] =
    val preview = imgSrc.fold[Html[App.Msg]](div()) { srcValue =>
      div(UiAttrs.classes(Jobaroo.form.previewFrame))(
        img(UiAttrs.classes(Jobaroo.form.previewImage) |+| UiAttrs(src := srcValue, alt := s"$name preview"))
      )
    }

    fieldset(UiAttrs.classes(Jobaroo.form.fieldset))(
      label(UiAttrs(`for` := uid) |+| UiAttrs.classes(Jobaroo.form.fieldLabel))(text(name)),
      label(UiAttrs.classes(Jobaroo.form.fileLabel))(
        div(UiAttrs.classes(Jobaroo.form.fileCopy))(
          p(UiAttrs.classes(Jobaroo.form.fileTitle))(text("Upload company logo")),
          p(UiAttrs.classes(Jobaroo.form.fileDescription))(text("Images are resized client-side before submit."))
        ),
        com.jobaroo.tyrianui.html.Tags.input(
          UiAttrs(`type` := "file", id := uid, accept := "image/*") |+|
            UiAttrs.classes(Jobaroo.form.fileInput) |+|
            UiAttrs(
              onEvent(
                "change",
                e =>
                  val imageInput = e.target.asInstanceOf[HTMLInputElement]
                  val fileList   = imageInput.files
                  onChange(Option.when(fileList.length > 0)(fileList(0)))
              )
            )
        )
      ),
      preview
    )

  private def clearForm() =
    import scala.concurrent.duration.*

    def effect: IO[Option[HTMLFormElement]] =
      for
        optForm <- IO(Option(document.getElementById("form").asInstanceOf[HTMLFormElement]))
        eff     <- if optForm.isEmpty then IO.sleep(100.millis) *> effect else IO(optForm)
      yield eff

    Cmd.Run[IO, Unit, App.Msg](effect.map(_.foreach(_.reset())))(_ => App.NoOp)
