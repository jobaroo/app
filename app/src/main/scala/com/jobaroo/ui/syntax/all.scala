package com.jobaroo.ui.syntax

import cats.Monoid
import cats.syntax.semigroup.*
import com.jobaroo.ui.core.Css

object all:

  extension (css: Css)
    def ++(other: Css): Css = css |+| other

  extension [A: Monoid](value: A)
    def when(condition: Boolean): A =
      if condition then value else Monoid[A].empty

    def unless(condition: Boolean): A =
      if condition then Monoid[A].empty else value
