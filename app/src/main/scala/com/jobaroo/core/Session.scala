package com.jobaroo.core

import tyrian.*
import tyrian.cmds.Logger
import cats.effect.IO
import com.jobaroo.App

import org.scalajs.dom.document
import scala.scalajs.js.Date
import com.jobaroo.common.constants

final case class Session(email: Option[String] = None, token: Option[String] = None):

  import Session.*

  def initCmd: Cmd[IO, Msg] =
    val optCommand =
      for
        email <- getCookie(constants.cookies.email)
        token <- getCookie(constants.cookies.token)
      yield Cmd.Emit(SetToken(email, token, isNewUser = false))

    optCommand.fold(Cmd.None)(identity)

  def update(msg: Msg): (Session, Cmd[IO, Msg]) = msg match
    case SetToken(email, token, isNewUser) =>
      (this.copy(email = Some(email), token = Some(token)), commands.setAllSessionCookies(email, token, isNewUser))

object Session:

  trait Msg                                                                           extends App.Msg
  final case class SetToken(email: String, token: String, isNewUser: Boolean = false) extends Msg

  object commands:

    def setSessionCookie(name: String, value: String, isFresh: Boolean): Cmd[IO, Msg] = Cmd.SideEffect[IO] {
      if getCookie(name).isEmpty || isFresh then
        document.cookie = s"${name}=${value};expires=${new Date(Date.now() + constants.cookies.duration)};path=/"
    }

    def clearSessionCookie(name: String): Cmd[IO, Msg] = Cmd.SideEffect[IO] {
      document.cookie = s"${name}=;expires=${new Date(0)};path=/"
    }

    def setAllSessionCookies(email: String, token: String, isFresh: Boolean = false): Cmd[IO, Msg] =
      setSessionCookie(constants.cookies.email, email, isFresh) |+|
        setSessionCookie(constants.cookies.token, token, isFresh)

    def clearAllSessionCookies(): Cmd[IO, Msg] =
      clearSessionCookie(constants.cookies.email) |+| clearSessionCookie(constants.cookies.token)

  private def getCookie(name: String): Option[String] =
    document.cookie.split(";").map(_.trim).find(_.startsWith(s"$name=")).map(_.split("=")).map(_(1))
