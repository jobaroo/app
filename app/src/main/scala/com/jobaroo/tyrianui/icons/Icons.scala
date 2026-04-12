package com.jobaroo.tyrianui.icons

import cats.syntax.semigroup.*
import tyrian.{Html as TyrianHtml}
import com.jobaroo.tyrianui.core.UiAttrs
import com.jobaroo.tyrianui.html.Tags.span
import com.jobaroo.ui.core.Css
import com.jobaroo.ui.preset.Jobaroo

object Icons:

  def briefcase[Msg](classes: Css = Jobaroo.icon.regular): TyrianHtml[Msg] =
    glyph(
      classes,
      """<svg viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="1.8" stroke-linecap="round" stroke-linejoin="round" width="1em" height="1em"><path d="M8 7V5.5A1.5 1.5 0 0 1 9.5 4h5A1.5 1.5 0 0 1 16 5.5V7"/><path d="M4.5 7h15A1.5 1.5 0 0 1 21 8.5v8A1.5 1.5 0 0 1 19.5 18h-15A1.5 1.5 0 0 1 3 16.5v-8A1.5 1.5 0 0 1 4.5 7Z"/><path d="M3 12h18"/></svg>"""
    )

  def mapPin[Msg](classes: Css = Jobaroo.icon.regular): TyrianHtml[Msg] =
    glyph(
      classes,
      """<svg viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="1.8" stroke-linecap="round" stroke-linejoin="round" width="1em" height="1em"><path d="M12 21s6-4.35 6-10a6 6 0 1 0-12 0c0 5.65 6 10 6 10Z"/><circle cx="12" cy="11" r="2.25"/></svg>"""
    )

  def banknotes[Msg](classes: Css = Jobaroo.icon.regular): TyrianHtml[Msg] =
    glyph(
      classes,
      """<svg viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="1.8" stroke-linecap="round" stroke-linejoin="round" width="1em" height="1em"><rect x="3" y="6" width="18" height="12" rx="2"/><path d="M7 9.5c0 1.38-1.12 2.5-2.5 2.5M19.5 12c-1.38 0-2.5-1.12-2.5-2.5M7 14.5c0-1.38-1.12-2.5-2.5-2.5M19.5 12c-1.38 0-2.5 1.12-2.5 2.5"/><circle cx="12" cy="12" r="2.25"/></svg>"""
    )

  def tag[Msg](classes: Css = Jobaroo.icon.regular): TyrianHtml[Msg] =
    glyph(
      classes,
      """<svg viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="1.8" stroke-linecap="round" stroke-linejoin="round" width="1em" height="1em"><path d="M20 10 12 18 4 10V5h5Z"/><circle cx="8.5" cy="8.5" r=".8" fill="currentColor" stroke="none"/></svg>"""
    )

  def funnel[Msg](classes: Css = Jobaroo.icon.regular): TyrianHtml[Msg] =
    glyph(
      classes,
      """<svg viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="1.8" stroke-linecap="round" stroke-linejoin="round" width="1em" height="1em"><path d="M4 6h16l-6.5 7.5V18l-3 1.8v-6.3Z"/></svg>"""
    )

  def sun[Msg](classes: Css = Jobaroo.icon.regular): TyrianHtml[Msg] =
    glyph(
      classes,
      """<svg viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="1.8" stroke-linecap="round" stroke-linejoin="round" width="1em" height="1em"><circle cx="12" cy="12" r="4"/><path d="M12 2.5v2.25M12 19.25v2.25M4.93 4.93l1.6 1.6M17.47 17.47l1.6 1.6M2.5 12h2.25M19.25 12h2.25M4.93 19.07l1.6-1.6M17.47 6.53l1.6-1.6"/></svg>"""
    )

  def moon[Msg](classes: Css = Jobaroo.icon.regular): TyrianHtml[Msg] =
    glyph(
      classes,
      """<svg viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="1.8" stroke-linecap="round" stroke-linejoin="round" width="1em" height="1em"><path d="M20.4 14.5A8.5 8.5 0 1 1 9.5 3.6a7 7 0 0 0 10.9 10.9Z"/></svg>"""
    )

  def arrowRight[Msg](classes: Css = Jobaroo.icon.regular): TyrianHtml[Msg] =
    glyph(
      classes,
      """<svg viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="1.8" stroke-linecap="round" stroke-linejoin="round" width="1em" height="1em"><path d="M5 12h14"/><path d="m13 6 6 6-6 6"/></svg>"""
    )

  def check[Msg](classes: Css = Jobaroo.icon.regular): TyrianHtml[Msg] =
    glyph(
      classes |+| Css.literal("shrink-0"),
      """<svg viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="1.8" stroke-linecap="round" stroke-linejoin="round" width="1em" height="1em"><circle cx="12" cy="12" r="9"/><path d="m8.5 12 2.3 2.3L15.8 9.7"/></svg>"""
    )

  def info[Msg](classes: Css = Jobaroo.icon.regular): TyrianHtml[Msg] =
    glyph(
      classes |+| Css.literal("shrink-0"),
      """<svg viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="1.8" stroke-linecap="round" stroke-linejoin="round" width="1em" height="1em"><circle cx="12" cy="12" r="9"/><path d="M12 10v6"/><circle cx="12" cy="7.5" r=".8" fill="currentColor" stroke="none"/></svg>"""
    )

  def warn[Msg](classes: Css = Jobaroo.icon.regular): TyrianHtml[Msg] =
    glyph(
      classes |+| Css.literal("shrink-0"),
      """<svg viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="1.8" stroke-linecap="round" stroke-linejoin="round" width="1em" height="1em"><path d="M12 4.5 20 19H4Z"/><path d="M12 9.5v4.5"/><circle cx="12" cy="16.75" r=".8" fill="currentColor" stroke="none"/></svg>"""
    )

  def error[Msg](classes: Css = Jobaroo.icon.regular): TyrianHtml[Msg] =
    glyph(
      classes |+| Css.literal("shrink-0"),
      """<svg viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="1.8" stroke-linecap="round" stroke-linejoin="round" width="1em" height="1em"><circle cx="12" cy="12" r="9"/><path d="m9 9 6 6"/><path d="m15 9-6 6"/></svg>"""
    )

  private def glyph[Msg](classes: Css, markup: String): TyrianHtml[Msg] =
    span(UiAttrs.classes(Jobaroo.icon.tokenWrap |+| classes))().innerHtml(markup)
