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
    attrs: UiAttrs[Msg] = UiAttrs.empty[Msg],
    fieldAttrs: UiAttrs[Msg] = UiAttrs.empty[Msg],
    labelAttrs: UiAttrs[Msg] = UiAttrs.empty[Msg],
    hintAttrs: UiAttrs[Msg] = UiAttrs.empty[Msg],
    controlAttrs: UiAttrs[Msg] = UiAttrs.empty[Msg]
  ): TyrianHtml[Msg] =
    wrap(meta, fieldAttrs, labelAttrs, hintAttrs)(
      input(
        attrs |+|
          controlAttrs |+|
          UiAttrs.classes(Daisy.groups.inputBase |+| Css.literal(
            "h-12 w-full rounded-[1.1rem] border-base-300 bg-base-200/70 px-4 text-base shadow-sm shadow-black/5 focus:border-neutral focus:outline-none"
          )) |+|
          UiAttrs(
            id     := meta.id.value,
            `type` := kind.value,
            value  := currentValue,
            onInput(onValue),
            required(meta.required)
          ) |+|
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
    controlAttrs: UiAttrs[Msg] = UiAttrs.empty[Msg]
  ): TyrianHtml[Msg] =
    wrap(meta, fieldAttrs, labelAttrs, hintAttrs)(
      textarea(
        attrs |+|
          controlAttrs |+|
          UiAttrs.classes(Daisy.groups.textareaBase |+| Css.literal(
            "min-h-56 w-full rounded-[1.25rem] border-base-300 bg-base-200/70 px-4 py-3 text-base shadow-sm shadow-black/5 focus:border-neutral focus:outline-none"
          )) |+|
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
    controlAttrs: UiAttrs[Msg] = UiAttrs.empty[Msg]
  ): TyrianHtml[Msg] =
    wrap(meta, fieldAttrs, labelAttrs, hintAttrs)(
      select(
        attrs |+|
          controlAttrs |+|
          UiAttrs.classes(Daisy.groups.selectBase |+| Css.literal(
            "h-12 w-full rounded-[1.1rem] border-base-300 bg-base-200/70 px-4 text-base shadow-sm shadow-black/5 focus:border-neutral focus:outline-none"
          )) |+|
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
            checked(checkedValue),
            onEvent(
              "change",
              event => onChangeValue(event.target.asInstanceOf[org.scalajs.dom.HTMLInputElement].checked)
            )
          )
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
            checked(checkedValue),
            onEvent(
              "change",
              event => onChangeValue(event.target.asInstanceOf[org.scalajs.dom.HTMLInputElement].checked)
            )
          )
      )
    )

  private def wrap[Msg](
    meta: Meta,
    fieldAttrs: UiAttrs[Msg],
    labelAttrs: UiAttrs[Msg],
    hintAttrs: UiAttrs[Msg]
  )(control: TyrianHtml[Msg]): TyrianHtml[Msg] =
    fieldset(fieldAttrs)(
      label(UiAttrs(`for` := meta.id.value) |+| labelAttrs)(
        span()(text(meta.label)),
        if meta.required then span()(text("*")) else div()
      ),
      control,
      meta.hint.fold(div())(hint => p(hintAttrs)(text(hint)))
    )
