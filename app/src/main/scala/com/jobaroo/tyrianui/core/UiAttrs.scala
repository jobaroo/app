package com.jobaroo.tyrianui.core

import cats.Monoid
import cats.data.Chain
import cats.syntax.all.*
import com.jobaroo.ui.core.Css
import tyrian.Attr
import tyrian.Html.`class`
import tyrian.Html.attribute

opaque type UiAttrs[Msg] = Chain[Attr[Msg]]

object UiAttrs:

  def empty[Msg]: UiAttrs[Msg] = Chain.empty

  def apply[Msg](values: Attr[Msg]*): UiAttrs[Msg] =
    Chain.fromSeq(values)

  def fromIterable[Msg](values: Iterable[Attr[Msg]]): UiAttrs[Msg] =
    Chain.fromSeq(values.iterator.toSeq)

  def classes[Msg](css: Css): UiAttrs[Msg] =
    if css.isEmpty then empty else UiAttrs(`class` := css.render)

  def data[Msg](name: String, value: String): UiAttrs[Msg] =
    UiAttrs(attribute(s"data-$name", value))

  given [Msg]: Monoid[UiAttrs[Msg]] with
    override def empty: UiAttrs[Msg] = UiAttrs.empty
    override def combine(x: UiAttrs[Msg], y: UiAttrs[Msg]): UiAttrs[Msg] = x ++ y

  extension [Msg](attrs: UiAttrs[Msg])
    def render: Seq[Attr[Msg]] = attrs.asInstanceOf[Chain[Attr[Msg]]].iterator.toSeq
