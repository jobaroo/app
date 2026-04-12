package com.jobaroo.core

import tyrian.*
import cats.effect.*
import fs2.dom.History
import com.jobaroo.App
import org.scalajs.dom.*

final case class Router private (location: String, history: History[IO, String], backStack: List[String]):

  import Router.*

  def update(msg: Msg): (Router, Cmd[IO, Msg]) = msg match
    case ChangeLocation(newLocation, _) if location == newLocation => (this, Cmd.None)
    case ChangeLocation(newLocation, browserTriggered)             =>
      val nextRouter =
        if browserTriggered then
          backStack match
            case head :: tail if head == newLocation => this.copy(location = newLocation, backStack = tail)
            case _                                   => this.copy(location = newLocation)
        else this.copy(location = newLocation, backStack = location :: backStack)

      (nextRouter, if browserTriggered then Cmd.None else goto(newLocation))
    case GoBack(fallback)                                          =>
      if backStack.nonEmpty then
        window.history.back()
        (this, Cmd.None)
      else if location == fallback then (this, Cmd.None)
      else (this.copy(location = fallback), goto(fallback))
    case ExternalRedirect(location)                                => 
      window.location.href = if location.startsWith("\"") then location.substring(1, location.length()) else location
      (this, Cmd.None)

  def goto[M](location: String): Cmd[IO, M] = Cmd.SideEffect[IO] {
    history.pushState(location, location)
  }

object Router:

  trait Msg                                                                            extends App.Msg
  final case class ChangeLocation(location: String, browserTriggered: Boolean = false) extends Msg
  final case class GoBack(fallback: String = "/")                                      extends Msg
  final case class ExternalRedirect(location: String)                                  extends Msg

  def startAt(initialLocation: String): (Router, Cmd[IO, Msg]) =
    (Router(initialLocation, History[IO, String], Nil), Cmd.None)
