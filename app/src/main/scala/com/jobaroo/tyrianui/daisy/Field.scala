package com.jobaroo.tyrianui.daisy

import cats.syntax.semigroup.*
import tyrian.Html.*
import tyrian.{Html as TyrianHtml}
import com.jobaroo.tyrianui.core.UiAttrs
import com.jobaroo.tyrianui.html.Tags.{div, fieldset, input, label, option, p, select, span, textarea}
import com.jobaroo.ui.core.Css
import com.jobaroo.ui.core.UiId
import com.jobaroo.ui.core.UiText
import com.jobaroo.ui.preset.Daisy
import com.jobaroo.ui.syntax.all.*

object Field:

  enum InputKind(val value: String):

    case Text     extends InputKind("text")
    case Email    extends InputKind("email")
    case Password extends InputKind("password")
    case Number   extends InputKind("number")
    case Url      extends InputKind("url")

  final case class Meta(
    id      : UiId,
    label   : String,
    required: Boolean = false,
    hint    : Option[String] = None
  )

  final case class Validation(
    hint     : Option[String] = None,
    pattern  : Option[String] = None,
    title    : Option[String] = None,
    minValue : Option[String] = None,
    maxValue : Option[String] = None,
    minLength: Option[Int] = None,
    maxLength: Option[Int] = None,
    useNative: Boolean = false
  )

  object Validation:

    val none: Validation = Validation()

    val email: Validation =
      Validation(
        hint = Some("Enter a valid email address."),
        useNative = true
      )

    val url: Validation =
      Validation(
        hint = Some("Enter a full URL starting with http:// or https://."),
        pattern = Some("""https?://.+"""),
        title = Some("Enter a full URL starting with http:// or https://."),
        useNative = true
      )

    def number(
      minValue: Option[Int] = Some(0),
      maxValue: Option[Int] = None,
      hint: Option[String] = None
    ): Validation =
      Validation(
        hint = hint,
        minValue = minValue.map(_.toString),
        maxValue = maxValue.map(_.toString),
        useNative = minValue.nonEmpty || maxValue.nonEmpty
      )

    extension (validation: Validation)
      def enabled: Boolean =
        validation.useNative || validation.hint.nonEmpty || validation.pattern.nonEmpty || validation.title.nonEmpty ||
          validation.minValue.nonEmpty || validation.maxValue.nonEmpty || validation.minLength.nonEmpty || validation.maxLength.nonEmpty

  object Meta:

    inline def static(
      inline id: String,
      inline label: String,
      required: Boolean = false,
      hint: Option[String] = None
    ): Meta =
      Meta(UiId.literal(id), UiText.literal(label, "field label").value, required, hint)

    def dynamic(id: UiId, label: String, required: Boolean = false, hint: Option[String] = None): Meta =
      Meta(id = id, label = label.trim, required = required, hint = hint.map(_.trim).filter(_.nonEmpty))

  def textInput[Msg](
    meta: Meta,
    currentValue: String,
    onValue: String => Msg,
    kind: InputKind = InputKind.Text,
    placeholderText: Option[String] = None,
    autoCompleteHint: Option[String] = None,
    attrs: UiAttrs[Msg] = UiAttrs.empty[Msg],
    fieldAttrs: UiAttrs[Msg] = UiAttrs.empty[Msg],
    labelAttrs: UiAttrs[Msg] = UiAttrs.empty[Msg],
    hintAttrs: UiAttrs[Msg] = UiAttrs.empty[Msg],
    controlAttrs: UiAttrs[Msg] = UiAttrs.empty[Msg],
    validation: Validation = Validation.none
  ): TyrianHtml[Msg] =
    wrap(meta, fieldAttrs, labelAttrs, hintAttrs, validation)(
      input(
        attrs |+|
          controlAttrs |+|
          UiAttrs.classes(Daisy.groups.inputBase |+| Css.literal(
            "input-bordered h-10 w-full rounded-md border-base-300 bg-base-100 px-3 text-sm shadow-none focus:border-primary focus:outline-none"
          ) |+| Css.literal("validator").when(validation.enabled)) |+|
          validationAttrs(validation) |+|
          UiAttrs(
            id     := meta.id.value,
            `type` := kind.value,
            value  := currentValue,
            onInput(onValue),
            required(meta.required)
          ) |+|
          autoCompleteHint.map(value => UiAttrs(attribute("autocomplete", value))).getOrElse(UiAttrs.empty) |+|
          placeholderText.map(text => UiAttrs(placeholder := text)).getOrElse(UiAttrs.empty)
      )
    )

  def textAreaField[Msg](
    meta: Meta,
    currentValue: String,
    onValue: String => Msg,
    rows: Int = 8,
    placeholderText: Option[String] = None,
    attrs: UiAttrs[Msg] = UiAttrs.empty[Msg],
    fieldAttrs: UiAttrs[Msg] = UiAttrs.empty[Msg],
    labelAttrs: UiAttrs[Msg] = UiAttrs.empty[Msg],
    hintAttrs: UiAttrs[Msg] = UiAttrs.empty[Msg],
    controlAttrs: UiAttrs[Msg] = UiAttrs.empty[Msg],
    validation: Validation = Validation.none
  ): TyrianHtml[Msg] =
    wrap(meta, fieldAttrs, labelAttrs, hintAttrs, validation)(
      textarea(
        attrs |+|
          controlAttrs |+|
          UiAttrs.classes(Daisy.groups.textareaBase |+| Css.literal(
            "textarea-bordered min-h-32 w-full rounded-md border-base-300 bg-base-100 px-3 py-3 text-sm shadow-none focus:border-primary focus:outline-none"
          ) |+| Css.literal("validator").when(validation.enabled)) |+|
          validationAttrs(validation) |+|
          UiAttrs(
            id := meta.id.value,
            onInput(onValue),
            required(meta.required),
            attribute("rows", rows.toString)
          ) |+|
          placeholderText.map(text => UiAttrs(placeholder := text)).getOrElse(UiAttrs.empty)
      )(text(currentValue))
    )

  def selectField[Msg](
    meta: Meta,
    selected: String,
    options: List[(String, String)],
    onValue: String => Msg,
    attrs: UiAttrs[Msg] = UiAttrs.empty[Msg],
    fieldAttrs: UiAttrs[Msg] = UiAttrs.empty[Msg],
    labelAttrs: UiAttrs[Msg] = UiAttrs.empty[Msg],
    hintAttrs: UiAttrs[Msg] = UiAttrs.empty[Msg],
    controlAttrs: UiAttrs[Msg] = UiAttrs.empty[Msg],
    validation: Validation = Validation.none
  ): TyrianHtml[Msg] =
    wrap(meta, fieldAttrs, labelAttrs, hintAttrs, validation)(
      select(
        attrs |+|
          controlAttrs |+|
          UiAttrs.classes(Daisy.groups.selectBase |+| Css.literal(
            "select-bordered h-10 w-full rounded-md border-base-300 bg-base-100 px-3 text-sm shadow-none focus:border-primary focus:outline-none"
          ) |+| Css.literal("validator").when(validation.enabled)) |+|
          validationAttrs(validation) |+|
          UiAttrs(
            id := meta.id.value,
            onInput(onValue),
            required(meta.required)
          )
      )(
        options.map { case (optionValue, optionLabel) =>
          option(
            UiAttrs(value := optionValue) |+|
              UiAttrs(attribute("selected", "selected")).when(optionValue == selected)
          )(text(optionLabel))
        }*
      )
    )

  def checkboxField[Msg](
    meta: Meta,
    checkedValue: Boolean,
    onChangeValue: Boolean => Msg,
    attrs: UiAttrs[Msg] = UiAttrs.empty[Msg],
    wrapperAttrs: UiAttrs[Msg] = UiAttrs.empty[Msg],
    copyAttrs: UiAttrs[Msg] = UiAttrs.empty[Msg],
    titleAttrs: UiAttrs[Msg] = UiAttrs.empty[Msg],
    hintAttrs: UiAttrs[Msg] = UiAttrs.empty[Msg],
    controlAttrs: UiAttrs[Msg] = UiAttrs.empty[Msg]
  ): TyrianHtml[Msg] =
    label(wrapperAttrs)(
      div(copyAttrs)(
        div(titleAttrs)(text(meta.label)),
        meta.hint.fold(div())(hint => p(hintAttrs)(text(hint)))
      ),
      input(
        attrs |+|
          controlAttrs |+|
          UiAttrs.classes(Daisy.groups.checkboxBase) |+|
          UiAttrs(
            id     := meta.id.value,
            `type` := "checkbox",
            onEvent(
              "change",
              event => onChangeValue(event.target.asInstanceOf[org.scalajs.dom.HTMLInputElement].checked)
            )
          ) |+|
          UiAttrs.booleanDomProperty("checked", checkedValue)
      )
    )

  def toggleField[Msg](
    meta: Meta,
    checkedValue: Boolean,
    onChangeValue: Boolean => Msg,
    attrs: UiAttrs[Msg] = UiAttrs.empty[Msg],
    wrapperAttrs: UiAttrs[Msg] = UiAttrs.empty[Msg],
    copyAttrs: UiAttrs[Msg] = UiAttrs.empty[Msg],
    titleAttrs: UiAttrs[Msg] = UiAttrs.empty[Msg],
    hintAttrs: UiAttrs[Msg] = UiAttrs.empty[Msg],
    controlAttrs: UiAttrs[Msg] = UiAttrs.empty[Msg]
  ): TyrianHtml[Msg] =
    label(wrapperAttrs)(
      div(copyAttrs)(
        div(titleAttrs)(text(meta.label)),
        meta.hint.fold(div())(hint => p(hintAttrs)(text(hint)))
      ),
      input(
        attrs |+|
          controlAttrs |+|
          UiAttrs.classes(Daisy.groups.toggleBase) |+|
          UiAttrs(
            id     := meta.id.value,
            `type` := "checkbox",
            onEvent(
              "change",
              event => onChangeValue(event.target.asInstanceOf[org.scalajs.dom.HTMLInputElement].checked)
            )
          ) |+|
          UiAttrs.booleanDomProperty("checked", checkedValue)
      )
    )

  private def wrap[Msg](
    meta: Meta,
    fieldAttrs: UiAttrs[Msg],
    labelAttrs: UiAttrs[Msg],
    hintAttrs: UiAttrs[Msg],
    validation: Validation
  )(control: TyrianHtml[Msg]): TyrianHtml[Msg] =
    fieldset(fieldAttrs)(
      label(UiAttrs(`for` := meta.id.value) |+| labelAttrs)(
        span()(text(meta.label)),
        if meta.required then span()(text("*")) else span()()
      ),
      control,
      validation.hint.fold(div()) { hint =>
        p(UiAttrs.classes(Css.literal("validator-hint hidden text-xs text-error")))(text(hint))
      },
      meta.hint.fold(div())(hint => p(hintAttrs)(text(hint)))
    )

  private def validationAttrs[Msg](validation: Validation): UiAttrs[Msg] =
    validation.pattern.map(value => UiAttrs(attribute("pattern", value))).getOrElse(UiAttrs.empty) |+|
      validation.title.map(value => UiAttrs(attribute("title", value))).getOrElse(UiAttrs.empty) |+|
      validation.minValue.map(value => UiAttrs(attribute("min", value))).getOrElse(UiAttrs.empty) |+|
      validation.maxValue.map(value => UiAttrs(attribute("max", value))).getOrElse(UiAttrs.empty) |+|
      validation.minLength.map(value => UiAttrs(attribute("minlength", value.toString))).getOrElse(UiAttrs.empty) |+|
      validation.maxLength.map(value => UiAttrs(attribute("maxlength", value.toString))).getOrElse(UiAttrs.empty)
