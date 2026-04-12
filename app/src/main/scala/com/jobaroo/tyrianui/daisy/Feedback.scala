package com.jobaroo.tyrianui.daisy

import cats.syntax.semigroup.*
import tyrian.Html.*
import tyrian.{Html as TyrianHtml}
import com.jobaroo.tyrianui.core.UiAttrs
import com.jobaroo.tyrianui.html.Tags.{div, span}
import com.jobaroo.tyrianui.icons.Icons
import com.jobaroo.ui.core.Css
import com.jobaroo.ui.preset.Daisy

object Feedback:

  enum Tone:

    case Info, Success, Warning, Error

    def classes: Css = this match
      case Info    => Daisy.tone.alertInfo
      case Success => Daisy.tone.alertSuccess
      case Warning => Daisy.tone.alertWarning
      case Error   => Daisy.tone.alertError

  def alert[Msg](message: String, tone: Tone, attrs: UiAttrs[Msg] = UiAttrs.empty[Msg]): TyrianHtml[Msg] =
    val allAttrs =
      attrs |+| UiAttrs.classes(
        Daisy.groups.alertBase |+| Css.literal(
          "items-start gap-3 rounded-[1.25rem] border border-base-300 bg-base-100 px-4 py-4 text-sm leading-6 shadow-none"
        ) |+| tone.classes
      )

    val icon = tone match
      case Tone.Success => Icons.check[Msg]()
      case Tone.Warning => Icons.warn[Msg]()
      case Tone.Error   => Icons.error[Msg]()
      case Tone.Info    => Icons.info[Msg]()

    div(allAttrs)(
      icon,
      span()(text(message))
    )
