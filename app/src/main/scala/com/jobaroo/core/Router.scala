package com.jobaroo.core

import tyrian.*
import cats.effect.*
import fs2.dom.History
import com.jobaroo.App
import tyrian.cmds.Logger
import org.scalajs.dom.*

final case class Router private (location: String, history: History[IO, String]):

  import Router.*

  def update(msg: Msg): (Router, Cmd[IO, Msg]) = msg match
    case ChangeLocation(newLocation, _) if location == newLocation => (this, Cmd.None)
    case ChangeLocation(newLocation, browserTriggered)             =>
      (this.copy(location = newLocation), if browserTriggered then Cmd.None else goto(newLocation))
    case ExternalRedirect(location)                                => 
      window.location.href = if location.startsWith("\"") then location.substring(1, location.length()) else location
      (this, Cmd.None)

  def goto[M](location: String): Cmd[IO, M] = Cmd.SideEffect[IO] {
    history.pushState(location, location)
  }

object Router:

  trait Msg                                                                            extends App.Msg
  final case class ChangeLocation(location: String, browserTriggered: Boolean = false) extends Msg
  final case class ExternalRedirect(location: String)                                  extends Msg

  def startAt(initialLocation: String): (Router, Cmd[IO, Msg]) = (Router(initialLocation, History[IO, String]), Cmd.None)
