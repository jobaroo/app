package com.jobaroo.tyrianui.daisy

import cats.syntax.semigroup.*
import tyrian.Html.*
import tyrian.{Html as TyrianHtml}
import com.jobaroo.tyrianui.core.UiAttrs
import com.jobaroo.tyrianui.html.Tags.{button, span}
import com.jobaroo.ui.syntax.all.*

object Toggle:

  final case class Props[Msg](
    checked       : Boolean,
    onToggle      : Boolean => Msg,
    ariaLabel     : Option[String] = None,
    onLabel       : String = "On",
    offLabel      : String = "Off",
    attrs         : UiAttrs[Msg] = UiAttrs.empty[Msg],
    checkedAttrs  : UiAttrs[Msg] = UiAttrs.empty[Msg],
    uncheckedAttrs: UiAttrs[Msg] = UiAttrs.empty[Msg],
    stateAttrs    : UiAttrs[Msg] = UiAttrs.empty[Msg],
    thumbAttrs    : UiAttrs[Msg] = UiAttrs.empty[Msg]
  )

  def props[Msg](checked: Boolean, onToggle: Boolean => Msg): Props[Msg] =
    Props(checked = checked, onToggle = onToggle)

  def render[Msg](props: Props[Msg]): TyrianHtml[Msg] =
    val controlAttrs =
      UiAttrs(
        `type` := "button",
        attribute("role", "switch"),
        attribute("aria-checked", props.checked.toString)
      ) |+|
        props.ariaLabel.map(label => UiAttrs(attribute("aria-label", label))).getOrElse(UiAttrs.empty) |+|
        props.attrs |+|
        props.checkedAttrs.when(props.checked) |+|
        props.uncheckedAttrs.unless(props.checked) |+|
        UiAttrs(
          onEvent(
            "click",
            event =>
              val current = event.currentTarget.asInstanceOf[org.scalajs.dom.Element].getAttribute("aria-checked") == "true"
              props.onToggle(!current)
          )
        )

    button(controlAttrs)(
      span(props.stateAttrs)(text(if props.checked then props.onLabel else props.offLabel)),
      span(props.thumbAttrs)()
    )
