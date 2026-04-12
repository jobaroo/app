package com.jobaroo.tyrianui.syntax

import tyrian.Attr
import com.jobaroo.tyrianui.core.UiAttrs

object all:

  extension [Msg](attr: Attr[Msg])
    def attrs: UiAttrs[Msg] = UiAttrs(attr)
