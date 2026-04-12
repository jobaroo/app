package com.jobaroo.tyrianui.core

import cats.Monoid
import cats.data.Chain
import cats.syntax.all.*
import com.jobaroo.ui.core.Css
import tyrian.Attr
import tyrian.Html.`class`
import tyrian.Html.attribute

private final case class UiAttrsData[Msg](classes: Css, attrs: Chain[Attr[Msg]])

opaque type UiAttrs[Msg] = UiAttrsData[Msg]

object UiAttrs:

  def empty[Msg]: UiAttrs[Msg] =
    UiAttrsData(Css.empty, Chain.empty)

  def apply[Msg](values: Attr[Msg]*): UiAttrs[Msg] =
    UiAttrsData(Css.empty, Chain.fromSeq(values))

  def fromIterable[Msg](values: Iterable[Attr[Msg]]): UiAttrs[Msg] =
    UiAttrsData(Css.empty, Chain.fromSeq(values.iterator.toSeq))

  def classes[Msg](css: Css): UiAttrs[Msg] =
    UiAttrsData(css, Chain.empty)

  def data[Msg](name: String, value: String): UiAttrs[Msg] =
    UiAttrs(attribute(s"data-$name", value))

  given [Msg]: Monoid[UiAttrs[Msg]] with
    override def empty: UiAttrs[Msg] = UiAttrs.empty

    override def combine(x: UiAttrs[Msg], y: UiAttrs[Msg]): UiAttrs[Msg] =
      val left  = x.asInstanceOf[UiAttrsData[Msg]]
      val right = y.asInstanceOf[UiAttrsData[Msg]]
      UiAttrsData(left.classes |+| right.classes, left.attrs ++ right.attrs)

  extension [Msg](attrs: UiAttrs[Msg])
    def render: Seq[Attr[Msg]] =
      val data      = attrs.asInstanceOf[UiAttrsData[Msg]]
      val classAttr = Option.when(data.classes.nonEmpty)(`class` := data.classes.render)
      (classAttr.iterator ++ data.attrs.iterator).toSeq
