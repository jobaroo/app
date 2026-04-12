package com.jobaroo.tyrianui.icons

import cats.syntax.semigroup.*
import tyrian.Html.*
import tyrian.Html as TyrianHtml
import com.jobaroo.tyrianui.core.UiAttrs
import com.jobaroo.tyrianui.html.Tags.span
import com.jobaroo.ui.core.Css
import com.jobaroo.ui.preset.Jobaroo

object Icons:

  def briefcase[Msg](classes: Css = Jobaroo.icon.regular): TyrianHtml[Msg] =
    token(classes, "JOB")

  def mapPin[Msg](classes: Css = Jobaroo.icon.regular): TyrianHtml[Msg] =
    token(classes, "LOC")

  def banknotes[Msg](classes: Css = Jobaroo.icon.regular): TyrianHtml[Msg] =
    token(classes, "USD")

  def tag[Msg](classes: Css = Jobaroo.icon.regular): TyrianHtml[Msg] =
    token(classes, "TAG")

  def funnel[Msg](classes: Css = Jobaroo.icon.regular): TyrianHtml[Msg] =
    token(classes, "FLT")

  def sun[Msg](classes: Css = Jobaroo.icon.regular): TyrianHtml[Msg] =
    token(classes, "SUN")

  def moon[Msg](classes: Css = Jobaroo.icon.regular): TyrianHtml[Msg] =
    token(classes, "MON")

  def arrowRight[Msg](classes: Css = Jobaroo.icon.regular): TyrianHtml[Msg] =
    token(classes, "GO")

  def check[Msg](classes: Css = Jobaroo.icon.regular): TyrianHtml[Msg] =
    token(classes |+| Css.literal("shrink-0"), "OK")

  def info[Msg](classes: Css = Jobaroo.icon.regular): TyrianHtml[Msg] =
    token(classes |+| Css.literal("shrink-0"), "INF")

  def warn[Msg](classes: Css = Jobaroo.icon.regular): TyrianHtml[Msg] =
    token(classes |+| Css.literal("shrink-0"), "WRN")

  def error[Msg](classes: Css = Jobaroo.icon.regular): TyrianHtml[Msg] =
    token(classes |+| Css.literal("shrink-0"), "ERR")

  private def token[Msg](classes: Css, value: String): TyrianHtml[Msg] =
    span(UiAttrs.classes(Jobaroo.icon.tokenWrap |+| classes))(text(value))
