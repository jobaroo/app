package com.jobaroo.tyrianui.daisy

import cats.syntax.semigroup.*
import tyrian.{Html as TyrianHtml}
import com.jobaroo.tyrianui.core.UiAttrs
import com.jobaroo.tyrianui.html.Tags.div
import com.jobaroo.ui.preset.Daisy

object Card:

  def surface[Msg](attrs: UiAttrs[Msg] = UiAttrs.empty[Msg])(children: TyrianHtml[Msg]*): TyrianHtml[Msg] =
    div(attrs |+| UiAttrs.classes(Daisy.groups.cardBase))(children*)

  def body[Msg](attrs: UiAttrs[Msg] = UiAttrs.empty[Msg])(children: TyrianHtml[Msg]*): TyrianHtml[Msg] =
    div(attrs |+| UiAttrs.classes(Daisy.groups.cardBodyBase))(children*)
