package com.jobaroo.core

import tyrian.*
import tyrian.cmds.Logger
import cats.effect.IO
import com.jobaroo.App

final case class Session(email: Option[String] = None, token: Option[String] = None):

  import Session.*

  def initCmd: Cmd[IO, Msg] = Cmd.None

  def update(msg: Msg): (Session, Cmd[IO, Msg]) = msg match
    case SetToken(email, token) => (this.copy(email = Some(email), token = Some(token)), Logger.consoleLog[IO](s"email = $email, token = $token"))

object Session:

  trait Msg                                               extends App.Msg
  final case class SetToken(email: String, token: String) extends Msg
