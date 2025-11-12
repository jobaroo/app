package com.jobaroo.pages

import io.circe.syntax.*
import io.circe.parser.*
import io.circe.generic.auto.*
import tyrian.*
import tyrian.http.*
import tyrian.Html.*
import cats.effect.IO
import com.jobaroo.pages.Page.*
import com.jobaroo.pages.FormPage
import com.jobaroo.common.constants
import com.jobaroo.domain.auth.*
import com.jobaroo.common.Endpoint
import com.jobaroo.core.Session
import com.jobaroo.App
import com.jobaroo.components.Anchors

final case class LoginPage(email: String = "", password: String = "", status: Option[Page.Status] = None)
  extends FormPage("Log In", status):

  import LoginPage.*

  override def update(msg: App.Msg): (Page, Cmd[IO, App.Msg]) = msg match
    case UpdateEmail(email)       => (this.copy(email = email), Cmd.None)
    case UpdatePassword(password) => (this.copy(password = password), Cmd.None)
    case LoginError(message)      => (setErrorStatus(message), Cmd.None)
    case LoginSuccess(token)      =>
      (setSuccessStatus("Login successful"), Cmd.Emit(Session.SetToken(email, token, isNewUser = true)))
    case Login                    =>
      if !email.matches(constants.emailRegex) then (setErrorStatus("Email is invalid"), Cmd.None)
      else if password.isEmpty then (setErrorStatus("Please enter a password"), Cmd.None)
      else
        val loginInfo = LoginInfo(email = this.email, password = this.password)
        (this, commands.login(loginInfo))

  override def renderFormContent(): List[Html[App.Msg]] = List(
    renderInput("Email", "email", "text", true, UpdateEmail(_)),
    renderInput("Password", "password", "password", true, UpdatePassword(_)),
    button(`type` := "button", onClick(Login))("Log In"),
    Anchors.renderAuxLink(urls.forgotPassword, "Forgot Password?")
  )

  private def setErrorStatus(message: String): LoginPage   = this.copy(status = Some(Page.Status(message, Kind.ERROR)))
  private def setSuccessStatus(message: String): LoginPage = this.copy(status = Some(Page.Status(message, Kind.SUCCESS)))

object LoginPage:

  trait Msg                                         extends App.Msg
  final case class UpdatePassword(password: String) extends Msg
  final case class UpdateEmail(email: String)       extends Msg
  final case class LoginError(message: String)      extends Msg
  final case class LoginSuccess(token: String)      extends Msg
  case object Login                                 extends Msg

  object endpoints:

    val login = new Endpoint[Msg](
      location = constants.endpoints.login,
      method = Method.Post,
      onError = e => LoginError(e.toString),
      onResponse = _.headers.get("authorization") match
        case Some(token) => LoginSuccess(token)
        case None        => LoginError("Invalid username/password")
    ) {}

  object commands:
    def login(loginInfo: LoginInfo): Cmd[IO, Msg] = endpoints.login.call(loginInfo)
