package com.jobaroo.ui.theme

enum ThemeName(val value: String):
  case Light extends ThemeName("jobaroo-light")
  case Dark  extends ThemeName("jobaroo-dark")

  def toggle: ThemeName = this match
    case Light => Dark
    case Dark  => Light

object ThemeName:
  val storageKey = "jobaroo-theme"
