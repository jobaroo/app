package com.jobaroo.components

import tyrian.*
import tyrian.Html.*
import com.jobaroo.core.*
import com.jobaroo.pages.Page.*
import com.jobaroo.App
import com.jobaroo.components.*

object Footer:

  def view = div(`class` := "footer")(
    p(text("Written in "), a(href := "https://scala-lang.org", target := "blank")("Scala"))
  )
