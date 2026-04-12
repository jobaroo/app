package com.jobaroo.tyrianui.html

import com.jobaroo.tyrianui.core.UiAttrs
import tyrian.Elem
import tyrian.Html as raw
import tyrian.{Html as TyrianHtml}

object Tags:

  object Children:

    def one[Msg](child: Elem[Msg]): List[Elem[Msg]] =
      List(child)

    def fromOption[Msg](child: Option[Elem[Msg]]): List[Elem[Msg]] =
      child.fold(List.empty[Elem[Msg]])(List(_))

    def concat[Msg](segments: IterableOnce[Elem[Msg]]*): List[Elem[Msg]] =
      segments.iterator.flatMap(_.iterator).toList

  def a[Msg](attrs: UiAttrs[Msg] = UiAttrs.empty[Msg])(children: Elem[Msg]*): TyrianHtml[Msg] =
    raw.a(attrs.render*)(children*)

  def a[Msg](attrs: UiAttrs[Msg])(children: IterableOnce[Elem[Msg]]): TyrianHtml[Msg] =
    raw.a(attrs.render*)(children.iterator.toSeq*)

  def aside[Msg](attrs: UiAttrs[Msg] = UiAttrs.empty[Msg])(children: Elem[Msg]*): TyrianHtml[Msg] =
    raw.aside(attrs.render*)(children*)

  def aside[Msg](attrs: UiAttrs[Msg])(children: IterableOnce[Elem[Msg]]): TyrianHtml[Msg] =
    raw.aside(attrs.render*)(children.iterator.toSeq*)

  def button[Msg](attrs: UiAttrs[Msg] = UiAttrs.empty[Msg])(children: Elem[Msg]*): TyrianHtml[Msg] =
    raw.button(attrs.render*)(children*)

  def button[Msg](attrs: UiAttrs[Msg])(children: IterableOnce[Elem[Msg]]): TyrianHtml[Msg] =
    raw.button(attrs.render*)(children.iterator.toSeq*)

  def div[Msg](): TyrianHtml[Msg] =
    raw.div()()

  def div[Msg](attrs: UiAttrs[Msg] = UiAttrs.empty[Msg])(children: Elem[Msg]*): TyrianHtml[Msg] =
    raw.div(attrs.render*)(children*)

  def div[Msg](attrs: UiAttrs[Msg])(children: IterableOnce[Elem[Msg]]): TyrianHtml[Msg] =
    raw.div(attrs.render*)(children.iterator.toSeq*)

  def fieldset[Msg](attrs: UiAttrs[Msg] = UiAttrs.empty[Msg])(children: Elem[Msg]*): TyrianHtml[Msg] =
    raw.fieldset(attrs.render*)(children*)

  def fieldset[Msg](attrs: UiAttrs[Msg])(children: IterableOnce[Elem[Msg]]): TyrianHtml[Msg] =
    raw.fieldset(attrs.render*)(children.iterator.toSeq*)

  def footer[Msg](attrs: UiAttrs[Msg] = UiAttrs.empty[Msg])(children: Elem[Msg]*): TyrianHtml[Msg] =
    raw.footer(attrs.render*)(children*)

  def footer[Msg](attrs: UiAttrs[Msg])(children: IterableOnce[Elem[Msg]]): TyrianHtml[Msg] =
    raw.footer(attrs.render*)(children.iterator.toSeq*)

  def form[Msg](attrs: UiAttrs[Msg] = UiAttrs.empty[Msg])(children: Elem[Msg]*): TyrianHtml[Msg] =
    raw.form(attrs.render*)(children*)

  def form[Msg](attrs: UiAttrs[Msg])(children: IterableOnce[Elem[Msg]]): TyrianHtml[Msg] =
    raw.form(attrs.render*)(children.iterator.toSeq*)

  def h1[Msg](attrs: UiAttrs[Msg] = UiAttrs.empty[Msg])(children: Elem[Msg]*): TyrianHtml[Msg] =
    raw.h1(attrs.render*)(children*)

  def h1[Msg](attrs: UiAttrs[Msg])(children: IterableOnce[Elem[Msg]]): TyrianHtml[Msg] =
    raw.h1(attrs.render*)(children.iterator.toSeq*)

  def h2[Msg](attrs: UiAttrs[Msg] = UiAttrs.empty[Msg])(children: Elem[Msg]*): TyrianHtml[Msg] =
    raw.h2(attrs.render*)(children*)

  def h2[Msg](attrs: UiAttrs[Msg])(children: IterableOnce[Elem[Msg]]): TyrianHtml[Msg] =
    raw.h2(attrs.render*)(children.iterator.toSeq*)

  def h3[Msg](attrs: UiAttrs[Msg] = UiAttrs.empty[Msg])(children: Elem[Msg]*): TyrianHtml[Msg] =
    raw.h3(attrs.render*)(children*)

  def h3[Msg](attrs: UiAttrs[Msg])(children: IterableOnce[Elem[Msg]]): TyrianHtml[Msg] =
    raw.h3(attrs.render*)(children.iterator.toSeq*)

  def h4[Msg](attrs: UiAttrs[Msg] = UiAttrs.empty[Msg])(children: Elem[Msg]*): TyrianHtml[Msg] =
    raw.h4(attrs.render*)(children*)

  def h4[Msg](attrs: UiAttrs[Msg])(children: IterableOnce[Elem[Msg]]): TyrianHtml[Msg] =
    raw.h4(attrs.render*)(children.iterator.toSeq*)

  def h5[Msg](attrs: UiAttrs[Msg] = UiAttrs.empty[Msg])(children: Elem[Msg]*): TyrianHtml[Msg] =
    raw.h5(attrs.render*)(children*)

  def h5[Msg](attrs: UiAttrs[Msg])(children: IterableOnce[Elem[Msg]]): TyrianHtml[Msg] =
    raw.h5(attrs.render*)(children.iterator.toSeq*)

  def img[Msg](attrs: UiAttrs[Msg] = UiAttrs.empty[Msg]): TyrianHtml[Msg] =
    raw.img(attrs.render*)

  def input[Msg](attrs: UiAttrs[Msg] = UiAttrs.empty[Msg]): TyrianHtml[Msg] =
    raw.input(attrs.render*)

  def label[Msg](attrs: UiAttrs[Msg] = UiAttrs.empty[Msg])(children: Elem[Msg]*): TyrianHtml[Msg] =
    raw.label(attrs.render*)(children*)

  def label[Msg](attrs: UiAttrs[Msg])(children: IterableOnce[Elem[Msg]]): TyrianHtml[Msg] =
    raw.label(attrs.render*)(children.iterator.toSeq*)

  def li[Msg](attrs: UiAttrs[Msg] = UiAttrs.empty[Msg])(children: Elem[Msg]*): TyrianHtml[Msg] =
    raw.li(attrs.render*)(children*)

  def li[Msg](attrs: UiAttrs[Msg])(children: IterableOnce[Elem[Msg]]): TyrianHtml[Msg] =
    raw.li(attrs.render*)(children.iterator.toSeq*)

  def main[Msg](attrs: UiAttrs[Msg] = UiAttrs.empty[Msg])(children: Elem[Msg]*): TyrianHtml[Msg] =
    raw.main(attrs.render*)(children*)

  def main[Msg](attrs: UiAttrs[Msg])(children: IterableOnce[Elem[Msg]]): TyrianHtml[Msg] =
    raw.main(attrs.render*)(children.iterator.toSeq*)

  def nav[Msg](attrs: UiAttrs[Msg] = UiAttrs.empty[Msg])(children: Elem[Msg]*): TyrianHtml[Msg] =
    raw.nav(attrs.render*)(children*)

  def nav[Msg](attrs: UiAttrs[Msg])(children: IterableOnce[Elem[Msg]]): TyrianHtml[Msg] =
    raw.nav(attrs.render*)(children.iterator.toSeq*)

  def option[Msg](attrs: UiAttrs[Msg] = UiAttrs.empty[Msg])(children: Elem[Msg]*): TyrianHtml[Msg] =
    raw.option(attrs.render*)(children*)

  def option[Msg](attrs: UiAttrs[Msg])(children: IterableOnce[Elem[Msg]]): TyrianHtml[Msg] =
    raw.option(attrs.render*)(children.iterator.toSeq*)

  def p[Msg](attrs: UiAttrs[Msg] = UiAttrs.empty[Msg])(children: Elem[Msg]*): TyrianHtml[Msg] =
    raw.p(attrs.render*)(children*)

  def p[Msg](attrs: UiAttrs[Msg])(children: IterableOnce[Elem[Msg]]): TyrianHtml[Msg] =
    raw.p(attrs.render*)(children.iterator.toSeq*)

  def section[Msg](attrs: UiAttrs[Msg] = UiAttrs.empty[Msg])(children: Elem[Msg]*): TyrianHtml[Msg] =
    raw.section(attrs.render*)(children*)

  def section[Msg](attrs: UiAttrs[Msg])(children: IterableOnce[Elem[Msg]]): TyrianHtml[Msg] =
    raw.section(attrs.render*)(children.iterator.toSeq*)

  def select[Msg](attrs: UiAttrs[Msg] = UiAttrs.empty[Msg])(children: Elem[Msg]*): TyrianHtml[Msg] =
    raw.select(attrs.render*)(children*)

  def select[Msg](attrs: UiAttrs[Msg])(children: IterableOnce[Elem[Msg]]): TyrianHtml[Msg] =
    raw.select(attrs.render*)(children.iterator.toSeq*)

  def span[Msg](attrs: UiAttrs[Msg] = UiAttrs.empty[Msg])(children: Elem[Msg]*): TyrianHtml[Msg] =
    raw.span(attrs.render*)(children*)

  def span[Msg](attrs: UiAttrs[Msg])(children: IterableOnce[Elem[Msg]]): TyrianHtml[Msg] =
    raw.span(attrs.render*)(children.iterator.toSeq*)

  def textarea[Msg](attrs: UiAttrs[Msg] = UiAttrs.empty[Msg])(children: Elem[Msg]*): TyrianHtml[Msg] =
    raw.textarea(attrs.render*)(children*)

  def textarea[Msg](attrs: UiAttrs[Msg])(children: IterableOnce[Elem[Msg]]): TyrianHtml[Msg] =
    raw.textarea(attrs.render*)(children.iterator.toSeq*)

  def ul[Msg](attrs: UiAttrs[Msg] = UiAttrs.empty[Msg])(children: Elem[Msg]*): TyrianHtml[Msg] =
    raw.ul(attrs.render*)(children*)

  def ul[Msg](attrs: UiAttrs[Msg])(children: IterableOnce[Elem[Msg]]): TyrianHtml[Msg] =
    raw.ul(attrs.render*)(children.iterator.toSeq*)
