package com.jobaroo.core

import tyrian.*
import cats.effect.*
import fs2.dom.History

final case class Router private (location: String, history: History[IO, String]):

  import Router.*
  import Router.Msg.*

  def update(msg: Msg): (Router, Cmd[IO, Msg]) = msg match
    case ChangeLocation(newLocation, browserTriggered) if location == newLocation | browserTriggered => (this, Cmd.None)
    case ChangeLocation(newLocation, _)                                                              => (this.copy(location = newLocation), goto(newLocation))
    case ExternalRedirect(location)                                                                  => (this, Cmd.None)

  def goto[M](location: String): Cmd[IO, M] = Cmd.SideEffect[IO] {
    history.pushState(location, location)
  }

object Router:

  enum Msg:

    case ChangeLocation(location: String, browserTriggered: Boolean = false)
    case ExternalRedirect(location: String)

  def startAt(initialLocation: String): (Router, Cmd[IO, Msg]) = (Router(initialLocation, History[IO, String]), Cmd.None)
