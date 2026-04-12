package com.jobaroo.tyrianui.daisy

import cats.syntax.semigroup.*
import tyrian.{Html as TyrianHtml}
import com.jobaroo.tyrianui.core.UiAttrs
import com.jobaroo.tyrianui.html.Tags.{div, li, ul}
import com.jobaroo.ui.preset.Daisy

object Navigation:

  def navbar[Msg](attrs: UiAttrs[Msg] = UiAttrs.empty[Msg])(children: TyrianHtml[Msg]*): TyrianHtml[Msg] =
    div(attrs |+| UiAttrs.classes(Daisy.groups.navbarBase))(children*)

  def menu[Msg](attrs: UiAttrs[Msg] = UiAttrs.empty[Msg])(children: TyrianHtml[Msg]*): TyrianHtml[Msg] =
    ul(attrs |+| UiAttrs.classes(Daisy.groups.menuBase))(children*)

  def menuItem[Msg](child: TyrianHtml[Msg]): TyrianHtml[Msg] =
    li()(child)

  def footer[Msg](attrs: UiAttrs[Msg] = UiAttrs.empty[Msg])(children: TyrianHtml[Msg]*): TyrianHtml[Msg] =
    div(attrs |+| UiAttrs.classes(Daisy.groups.footerBase))(children*)
