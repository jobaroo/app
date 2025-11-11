package com.jobaroo.components

import cats.effect.IO
import com.jobaroo.App
import tyrian.Cmd
import tyrian.Html

trait Component[Msg, +Model]:

  def initCmd: Cmd[IO, Msg]
  def update(msg: App.Msg): (Model, Cmd[IO, Msg])
  def view: Html[Msg]
