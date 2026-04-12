package com.jobaroo.pages

import cats.effect.IO
import cats.syntax.semigroup.*
import tyrian.*
import tyrian.Html.*
import com.jobaroo.App
import com.jobaroo.components.AppLayout
import org.scalajs.dom.*
import org.scalajs.dom.HTMLButtonElement
import org.scalajs.dom.HTMLInputElement
import com.jobaroo.tyrianui.core.UiAttrs
import com.jobaroo.tyrianui.daisy.Button
import com.jobaroo.tyrianui.daisy.Card
import com.jobaroo.tyrianui.daisy.Field
import com.jobaroo.tyrianui.daisy.Feedback
import com.jobaroo.tyrianui.html.Tags.{div, fieldset, form, h1, img, label, p}
import com.jobaroo.ui.core.Css
import com.jobaroo.ui.core.UiId
import com.jobaroo.ui.preset.Jobaroo

abstract class FormPage(title: String, status: Option[Page.Status]) extends Page:

  override def initCmd: Cmd[IO, App.Msg] = clearForm()

  override def view: Html[App.Msg] = renderForm()

  protected def renderFormContent(): List[Html[App.Msg]]

  protected def backFallback: String = Page.urls.home
  protected def accountEyebrow: String = "Jobaroo Account"
  protected def marketingSubtitle: String =
    "Manage login, recovery, and recruiter access from a calm, focused account surface."
  protected def marketingStats: List[(String, String)] = List(
    "Fast setup" -> "Move through account flows without leaving the current hiring workflow.",
    "One account" -> "Use the same identity for browsing, posting, and profile management."
  )
  protected def sectionEyebrow: String = "Account access"
  protected def sectionSubtitle: String =
    "Use your Jobaroo account to sign in, recover access, or manage recruiter settings."

  protected def renderForm(): Html[App.Msg] =
    AppLayout.pageContainer(
      div(UiAttrs.classes(Jobaroo.form.page))(
        div(UiAttrs.classes(Jobaroo.form.backRow))(
          AppLayout.backLink(fallback = backFallback)
        ),
        div(UiAttrs.classes(Jobaroo.form.marketing))(
          div(UiAttrs.classes(Jobaroo.form.marketingText))(
            p(UiAttrs.classes(Jobaroo.form.marketingEyebrow))(text(accountEyebrow)),
            h1(UiAttrs.classes(Jobaroo.form.marketingTitle))(text(title)),
            p(UiAttrs.classes(Jobaroo.form.marketingSubtitle))(text(marketingSubtitle))
          ),
          div(UiAttrs.classes(Jobaroo.form.marketingStats))(
            marketingStats.map { case (statTitle, description) =>
              statCard(statTitle, description)
            }*
          )
        ),
        Card.surface(UiAttrs.classes(Jobaroo.form.formCard))(
          Card.body(UiAttrs.classes(Jobaroo.surface.bodyComfortable))(
            AppLayout.sectionTitle(sectionEyebrow, title, sectionSubtitle),
            renderStatus,
            form(
              UiAttrs(name := "account-form", id := "form") |+|
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
        attrs = UiAttrs(
          onEvent(
            "click",
            event =>
              val form = event.target.asInstanceOf[HTMLButtonElement].form
              if form == null || form.reportValidity() then onPress else App.NoOp
          )
        )
      )
    )

  protected def renderInput(
    name: String,
    uid: String,
    kind: String,
    isRequired: Boolean,
    currentValue: String = "",
    onChange: String => App.Msg,
    validation: Field.Validation = Field.Validation.none
  ): Html[App.Msg] =
    val fieldKind = kind match
      case "email"    => Field.InputKind.Email
      case "password" => Field.InputKind.Password
      case "number"   => Field.InputKind.Number
      case "url"      => Field.InputKind.Url
      case _          => Field.InputKind.Text

    val resolvedValidation =
      if validation != Field.Validation.none then validation
      else
        kind match
          case "email"  => Field.Validation.email
          case "url"    => Field.Validation.url
          case "number" => Field.Validation.number()
          case _        => Field.Validation.none

    val autoCompleteHint =
      autocompleteFor(uid, kind)

    Field.textInput(
      meta = Field.Meta.dynamic(UiId.sanitized(uid), name, required = isRequired),
      currentValue = currentValue,
      onValue = onChange,
      kind = fieldKind,
      autoCompleteHint = autoCompleteHint,
      fieldAttrs = UiAttrs.classes(Jobaroo.form.compactFieldset),
      labelAttrs = UiAttrs.classes(Jobaroo.form.fieldLabel),
      hintAttrs = UiAttrs.classes(Jobaroo.form.fieldHint),
      validation = resolvedValidation
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
      wrapperAttrs = UiAttrs.classes(Css.literal("flex items-center justify-between rounded-lg bg-base-200 p-4")),
      copyAttrs = UiAttrs.classes(Jobaroo.form.fileCopy),
      titleAttrs = UiAttrs.classes(Jobaroo.form.fileTitle),
      hintAttrs = UiAttrs.classes(Jobaroo.form.fileDescription)
    )

  protected def renderTextArea(
    name: String,
    uid: String,
    isRequired: Boolean,
    currentValue: String = "",
    onChange: String => App.Msg,
    validation: Field.Validation = Field.Validation.none
  ): Html[App.Msg] =
    Field.textAreaField(
      meta = Field.Meta.dynamic(UiId.sanitized(uid), name, required = isRequired),
      currentValue = currentValue,
      onValue = onChange,
      fieldAttrs = UiAttrs.classes(Jobaroo.form.compactFieldset),
      labelAttrs = UiAttrs.classes(Jobaroo.form.fieldLabel),
      hintAttrs = UiAttrs.classes(Jobaroo.form.fieldHint),
      validation = validation
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
          p(UiAttrs.classes(Jobaroo.form.fileDescription))(text("PNG, JPG, or SVG. Used in the listing preview and job cards."))
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

  private def autocompleteFor(uid: String, kind: String): Option[String] =
    val normalized = uid.trim.toLowerCase

    kind match
      case "email"    => Some("email")
      case "password" => Some(if normalized.contains("new") then "new-password" else "current-password")
      case "url"      => Some("url")
      case _ if normalized.contains("company")  => Some("organization")
      case _ if normalized.contains("title")    => Some("organization-title")
      case _ if normalized.contains("country")  => Some("country-name")
      case _ if normalized.contains("location") => Some("address-level2")
      case _ => None
