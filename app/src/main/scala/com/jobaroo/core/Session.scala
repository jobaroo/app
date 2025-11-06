package com.jobaroo.core

import tyrian.*
import tyrian.cmds.Logger
import cats.effect.IO
import com.jobaroo.App

import org.scalajs.dom.document
import scala.scalajs.js.Date
import com.jobaroo.common.constants
import com.jobaroo.pages.Page
import com.jobaroo.common.Endpoint
import tyrian.http.*
import com.jobaroo.pages.Page.urls

final case class Session(email: Option[String] = None, token: Option[String] = None):

  import Session.*

  def initCmd: Cmd[IO, Msg] =
    val optCommand =
      for
        email <- getCookie(constants.cookies.email)
        token <- getCookie(constants.cookies.token)
      yield Cmd.Emit(SetToken(email, token, isNewUser = false))

    optCommand.fold(Cmd.None)(identity)

  def update(msg: Msg): (Session, Cmd[IO, App.Msg]) = msg match
    case SetToken(email, token, isNewUser) =>
      val cookieCmd   = commands.setAllSessionCookies(email, token, isNewUser)
      val redirectCmd = if isNewUser then Cmd.Emit(Router.ChangeLocation(Page.urls.home)) else commands.checkToken
      (this.copy(email = Some(email), token = Some(token)), cookieCmd |+| redirectCmd)
    case Logout                            =>
      val cmd = token.fold(Cmd.None)(_ => commands.logout)
      (this, cmd)
    case LogoutSuccess | InvalidateToken                   => (
        this.copy(email = None, token = None),
        commands.clearAllSessionCookies() |+| Cmd.Emit(Router.ChangeLocation(urls.home)))
    case LogoutFailure                     => ???
    case KeepToken => (this, Cmd.None)

object Session:

  trait Msg                                                                           extends App.Msg
  final case class SetToken(email: String, token: String, isNewUser: Boolean = false) extends Msg
  case object Logout                                                                  extends Msg
  case object LogoutSuccess                                                           extends Msg
  case object LogoutFailure                                                           extends Msg
  case object InvalidateToken extends Msg
  case object KeepToken extends Msg

  def isActive     = getUserToken.nonEmpty
  def getUserToken = getCookie(constants.cookies.token)

  object endpoints:

    val logout = new Endpoint[Msg](
      location = constants.endpoints.logout,
      method = Method.Post,
      onResponse = _ => LogoutSuccess,
      onError = _ => LogoutFailure
    ) {}

    val checkToken = new Endpoint[Msg](
      location = constants.endpoints.checkToken,
      method = Method.Get,
      onResponse = resp =>
        resp.status match
          case Status(200, _) => KeepToken
          case _              => InvalidateToken,
      onError = _ => InvalidateToken
    ) {}

  object commands:

    def logout: Cmd[IO, Msg] = endpoints.logout.callAuthorized()
    def checkToken: Cmd[IO, Msg] = endpoints.checkToken.callAuthorized()

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
