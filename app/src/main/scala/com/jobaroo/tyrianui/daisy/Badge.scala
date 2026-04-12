package com.jobaroo.tyrianui.daisy

import cats.syntax.semigroup.*
import tyrian.Html.*
import tyrian.{Html => TyrianHtml}
import com.jobaroo.tyrianui.core.UiAttrs
import com.jobaroo.tyrianui.html.Tags.span
import com.jobaroo.ui.core.Css
import com.jobaroo.ui.preset.Daisy

object Badge:

  enum Tone:
    case Neutral, Primary, Secondary, Accent, Outline

    def classes: Css = this match
      case Neutral   => Daisy.tone.badgeNeutral
      case Primary   => Daisy.tone.badgePrimary
      case Secondary => Daisy.tone.badgeSecondary
      case Accent    => Daisy.tone.badgeAccent
      case Outline   => Daisy.tone.badgeOutline

  def render[Msg](label: String, tone: Tone = Tone.Outline, attrs: UiAttrs[Msg] = UiAttrs.empty[Msg]): TyrianHtml[Msg] =
    val allAttrs =
      attrs |+| UiAttrs.classes(
        Daisy.groups.badgeBase |+| Css.literal(
          "badge-sm gap-1 rounded-md px-2 py-1 text-[0.65rem] font-medium normal-case"
        ) |+| tone.classes
      )

    span(allAttrs)(text(label))
