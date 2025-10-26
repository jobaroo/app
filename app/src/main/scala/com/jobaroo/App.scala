package com.jobaroo

import scala.scalajs.js.annotation.*
import org.scalajs.dom.document

@JSExportTopLevel("JobarooApp")
class App:

  @JSExport
  def doSomething(containerId: String) = document.getElementById(containerId).innerHTML = "THIS ROCKS!!!!!!!!!"
