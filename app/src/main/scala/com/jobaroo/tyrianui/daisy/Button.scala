package com.jobaroo.tyrianui.daisy

import cats.syntax.semigroup.*
import tyrian.Html.*
import tyrian.{Html as TyrianHtml}
import com.jobaroo.tyrianui.core.UiAttrs
import com.jobaroo.tyrianui.syntax.all.*
import com.jobaroo.tyrianui.html.Tags.{a, button}
import com.jobaroo.ui.core.Css
import com.jobaroo.ui.preset.Daisy
import com.jobaroo.ui.syntax.all.*

object Button:

  enum Tone:

    case Primary, Secondary, Accent, Neutral, Ghost, Outline, Soft

    def classes: Css = this match
      case Primary   => Daisy.tone.buttonPrimary
      case Secondary => Daisy.tone.buttonSecondary
      case Accent    => Daisy.tone.buttonAccent
      case Neutral   => Daisy.tone.buttonNeutral
      case Ghost     => Daisy.tone.buttonGhost
      case Outline   => Daisy.tone.buttonOutline
      case Soft      => Daisy.tone.buttonSoft

  enum Size:

    case Small, Medium, Large

    def classes: Css = this match
      case Small  => Daisy.size.buttonSmall
      case Medium => Css.empty
      case Large  => Daisy.size.buttonLarge

  enum Width:

    case Auto, Full

    def classes: Css = this match
      case Auto => Css.empty
      case Full => Daisy.size.buttonFull

  enum HtmlType(val value: String):

    case Button extends HtmlType("button")
    case Submit extends HtmlType("submit")

  final case class Props[Msg](
    label   : String,
    tone    : Tone = Tone.Primary,
    size    : Size = Size.Medium,
    width   : Width = Width.Auto,
    htmlType: HtmlType = HtmlType.Button,
    disabled: Boolean = false,
    active  : Boolean = false,
    attrs   : UiAttrs[Msg] = UiAttrs.empty,
    onPress : Option[Msg] = None
  )

  def props[Msg](label: String): Props[Msg] =
    Props(label = label, attrs = UiAttrs.empty[Msg])

  def render[Msg](props: Props[Msg]): TyrianHtml[Msg] =
    val classes  = classesFor(props)
    val allAttrs =
      props.attrs |+|
        UiAttrs.classes(classes) |+|
        UiAttrs(`type` := props.htmlType.value) |+|
        UiAttrs(disabled(props.disabled)) |+|
        props.onPress.map(onClick(_).attrs).getOrElse(UiAttrs.empty)

    button(allAttrs)(text(props.label))

  def link[Msg](
    props: Props[Msg],
    hrefValue: String,
    newTab: Boolean = false
  ): TyrianHtml[Msg] =
    val linkAttrs =
      props.attrs |+|
        UiAttrs.classes(classesFor(props)) |+|
        UiAttrs(href := hrefValue) |+|
        UiAttrs(target := (if newTab then "_blank" else "_self")) |+|
        UiAttrs(rel := "noreferrer noopener").when(newTab)

    a(linkAttrs)(text(props.label))

  private def classesFor[Msg](props: Props[Msg]): Css =
    val classes =
      Daisy.groups.buttonBase |+|
        Css.literal(
          "rounded-md border px-4 text-sm font-medium normal-case shadow-none transition hover:brightness-[0.98]"
        ) |+|
        props.tone.classes |+|
        props.size.classes |+|
        props.width.classes |+|
        Daisy.atoms.btnActive.when(props.active)
    classes
