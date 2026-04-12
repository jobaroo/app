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
      """<svg viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="2" stroke-linecap="round" stroke-linejoin="round" width="1em" height="1em"><path d="M21 13.255A23.931 23.931 0 0 1 12 15c-3.183 0-6.22-.62-9-1.745"/><path d="M16 6V4a2 2 0 0 0-2-2h-4a2 2 0 0 0-2 2v2"/><path d="M5 20h14a2 2 0 0 0 2-2V8a2 2 0 0 0-2-2H5a2 2 0 0 0-2 2v10a2 2 0 0 0 2 2Z"/></svg>"""
    )

  def mapPin[Msg](classes: Css = Jobaroo.icon.regular): TyrianHtml[Msg] =
    glyph(
      classes,
      """<svg viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="2" stroke-linecap="round" stroke-linejoin="round" width="1em" height="1em"><path d="M17.657 16.657 13.414 20.9a2 2 0 0 1-2.828 0l-4.243-4.243a8 8 0 1 1 11.314 0Z"/><path d="M15 11a3 3 0 1 1-6 0 3 3 0 0 1 6 0Z"/></svg>"""
    )

  def globe[Msg](classes: Css = Jobaroo.icon.regular): TyrianHtml[Msg] =
    glyph(
      classes,
      """<svg viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="2" stroke-linecap="round" stroke-linejoin="round" width="1em" height="1em"><path d="M3.055 11H5a2 2 0 0 1 2 2v1a2 2 0 0 0 2 2 2 2 0 0 1 2 2v2.945"/><path d="M8 3.935V5.5A2.5 2.5 0 0 0 10.5 8h.5a2 2 0 0 1 2 2 2 2 0 1 0 4 0 2 2 0 0 1 2-2h1.064"/><path d="M15 20.488V18a2 2 0 0 1 2-2h3.064"/><path d="M21 12a9 9 0 1 1-18 0 9 9 0 0 1 18 0Z"/></svg>"""
    )

  def banknotes[Msg](classes: Css = Jobaroo.icon.regular): TyrianHtml[Msg] =
    glyph(
      classes,
      """<svg viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="2" stroke-linecap="round" stroke-linejoin="round" width="1em" height="1em"><path d="M2 12h20"/><path d="M6 6h12a2 2 0 0 1 2 2v8a2 2 0 0 1-2 2H6a2 2 0 0 1-2-2V8a2 2 0 0 1 2-2Z"/><path d="M12 9.5v5"/><path d="M10 11.5h4"/></svg>"""
    )

  def tag[Msg](classes: Css = Jobaroo.icon.regular): TyrianHtml[Msg] =
    glyph(
      classes,
      """<svg viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="2" stroke-linecap="round" stroke-linejoin="round" width="1em" height="1em"><path d="M7 7h.01"/><path d="M7 3h5a4 4 0 0 1 2.828 1.172l6 6a2 2 0 0 1 0 2.828l-7 7a2 2 0 0 1-2.828 0l-7-7A2 2 0 0 1 3 11V7a4 4 0 0 1 4-4Z"/></svg>"""
    )

  def funnel[Msg](classes: Css = Jobaroo.icon.regular): TyrianHtml[Msg] =
    glyph(
      classes,
      """<svg viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="2" stroke-linecap="round" stroke-linejoin="round" width="1em" height="1em"><path d="M3 4a1 1 0 0 1 1-1h16a1 1 0 0 1 1 1v2.586a1 1 0 0 1-.293.707l-6.414 6.414a1 1 0 0 0-.293.707V17l-4 4v-6.586a1 1 0 0 0-.293-.707L3.293 7.293A1 1 0 0 1 3 6.586V4Z"/></svg>"""
    )

  def building[Msg](classes: Css = Jobaroo.icon.regular): TyrianHtml[Msg] =
    glyph(
      classes,
      """<svg viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="2" stroke-linecap="round" stroke-linejoin="round" width="1em" height="1em"><path d="M19 21V5a2 2 0 0 0-2-2H7a2 2 0 0 0-2 2v16"/><path d="M3 21h18"/><path d="M9 7h1"/><path d="M9 11h1"/><path d="M14 7h1"/><path d="M14 11h1"/><path d="M10 21v-4a1 1 0 0 1 1-1h2a1 1 0 0 1 1 1v4"/></svg>"""
    )

  def document[Msg](classes: Css = Jobaroo.icon.regular): TyrianHtml[Msg] =
    glyph(
      classes,
      """<svg viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="2" stroke-linecap="round" stroke-linejoin="round" width="1em" height="1em"><path d="M7 3h7l5 5v13a1 1 0 0 1-1 1H7a2 2 0 0 1-2-2V5a2 2 0 0 1 2-2Z"/><path d="M14 3v5h5"/><path d="M9 13h6"/><path d="M9 17h6"/><path d="M9 9h1"/></svg>"""
    )

  def photo[Msg](classes: Css = Jobaroo.icon.regular): TyrianHtml[Msg] =
    glyph(
      classes,
      """<svg viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="2" stroke-linecap="round" stroke-linejoin="round" width="1em" height="1em"><rect x="3" y="5" width="18" height="14" rx="2"/><path d="m3 15 4-4 4 4 3-3 7 7"/><circle cx="8.5" cy="9.5" r="1.5"/></svg>"""
    )

  def plus[Msg](classes: Css = Jobaroo.icon.regular): TyrianHtml[Msg] =
    glyph(
      classes,
      """<svg viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="2" stroke-linecap="round" stroke-linejoin="round" width="1em" height="1em"><path d="M12 4v16"/><path d="M20 12H4"/></svg>"""
    )

  def bars3[Msg](classes: Css = Jobaroo.icon.regular): TyrianHtml[Msg] =
    glyph(
      classes,
      """<svg viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="2" stroke-linecap="round" stroke-linejoin="round" width="1em" height="1em"><path d="M4 6h16"/><path d="M4 12h16"/><path d="M4 18h16"/></svg>"""
    )

  def xMark[Msg](classes: Css = Jobaroo.icon.regular): TyrianHtml[Msg] =
    glyph(
      classes,
      """<svg viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="2" stroke-linecap="round" stroke-linejoin="round" width="1em" height="1em"><path d="M6 18 18 6"/><path d="m6 6 12 12"/></svg>"""
    )

  def sun[Msg](classes: Css = Jobaroo.icon.regular): TyrianHtml[Msg] =
    glyph(
      classes,
      """<svg viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="2" stroke-linecap="round" stroke-linejoin="round" width="1em" height="1em"><path d="M12 3v1"/><path d="M12 20v1"/><path d="M21 12h-1"/><path d="M4 12H3"/><path d="m18.364 5.636-.707.707"/><path d="m6.343 17.657-.707.707"/><path d="m18.364 18.364-.707-.707"/><path d="m6.343 6.343-.707-.707"/><circle cx="12" cy="12" r="4"/></svg>"""
    )

  def moon[Msg](classes: Css = Jobaroo.icon.regular): TyrianHtml[Msg] =
    glyph(
      classes,
      """<svg viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="2" stroke-linecap="round" stroke-linejoin="round" width="1em" height="1em"><path d="M20.354 15.354A9 9 0 0 1 8.646 3.646 9.003 9.003 0 0 0 12 21a9.003 9.003 0 0 0 8.354-5.646Z"/></svg>"""
    )

  def arrowRight[Msg](classes: Css = Jobaroo.icon.regular): TyrianHtml[Msg] =
    glyph(
      classes,
      """<svg viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="2" stroke-linecap="round" stroke-linejoin="round" width="1em" height="1em"><path d="M5 12h14"/><path d="m13 6 6 6-6 6"/></svg>"""
    )

  def arrowLeft[Msg](classes: Css = Jobaroo.icon.regular): TyrianHtml[Msg] =
    glyph(
      classes,
      """<svg viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="2" stroke-linecap="round" stroke-linejoin="round" width="1em" height="1em"><path d="M19 12H5"/><path d="m11 18-6-6 6-6"/></svg>"""
    )

  def check[Msg](classes: Css = Jobaroo.icon.regular): TyrianHtml[Msg] =
    glyph(
      classes |+| Css.literal("shrink-0"),
      """<svg viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="2" stroke-linecap="round" stroke-linejoin="round" width="1em" height="1em"><path d="m5 13 4 4L19 7"/></svg>"""
    )

  def info[Msg](classes: Css = Jobaroo.icon.regular): TyrianHtml[Msg] =
    glyph(
      classes |+| Css.literal("shrink-0"),
      """<svg viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="2" stroke-linecap="round" stroke-linejoin="round" width="1em" height="1em"><circle cx="12" cy="12" r="9"/><path d="M12 10v5"/><path d="M12 7h.01"/></svg>"""
    )

  def warn[Msg](classes: Css = Jobaroo.icon.regular): TyrianHtml[Msg] =
    glyph(
      classes |+| Css.literal("shrink-0"),
      """<svg viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="2" stroke-linecap="round" stroke-linejoin="round" width="1em" height="1em"><path d="M12 4 4 19h16Z"/><path d="M12 10v4"/><path d="M12 17h.01"/></svg>"""
    )

  def error[Msg](classes: Css = Jobaroo.icon.regular): TyrianHtml[Msg] =
    glyph(
      classes |+| Css.literal("shrink-0"),
      """<svg viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="2" stroke-linecap="round" stroke-linejoin="round" width="1em" height="1em"><circle cx="12" cy="12" r="9"/><path d="m15 9-6 6"/><path d="m9 9 6 6"/></svg>"""
    )

  private def glyph[Msg](classes: Css, markup: String): TyrianHtml[Msg] =
    span(UiAttrs.classes(Jobaroo.icon.tokenWrap |+| classes))().innerHtml(markup)
