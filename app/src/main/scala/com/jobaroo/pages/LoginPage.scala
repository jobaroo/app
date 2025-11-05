package com.jobaroo.pages

import io.circe.syntax.*
import io.circe.parser.*
import io.circe.generic.auto.*
import tyrian.*
import tyrian.http.*
import tyrian.Html.*
import cats.effect.IO
import com.jobaroo.pages.Page.Status
import com.jobaroo.pages.Page.Kind
import com.jobaroo.common.constants
import com.jobaroo.domain.auth.*
import com.jobaroo.common.Endpoint

final case class LoginPage(email: String = "", password: String = "", status: Option[Status] = None) extends Page:

  import LoginPage.*

  override def initCmd: Cmd[IO, Page.Msg] = Cmd.None

  override def update(msg: Page.Msg): (Page, Cmd[IO, Page.Msg]) = msg match
    case UpdateEmail(email)       => (this.copy(email = email), Cmd.None)
    case UpdatePassword(password) => (this.copy(password = password), Cmd.None)
    case LoginError(message)      => (setErrorStatus(message), Cmd.None)
    case LoginSuccess(token)      => (setSuccessStatus(token), Cmd.None)
    case Login                    =>
      if !email.matches(constants.emailRegex) then (setErrorStatus("Email is invalid"), Cmd.None)
      else if password.isEmpty then (setErrorStatus("Please enter a password"), Cmd.None)
      else
        val loginInfo = LoginInfo(email = this.email, password = this.password)
        (this, commands.login(loginInfo))
    case NoOp                     => (this, Cmd.None)

  override def view: Html[Page.Msg] =
    div(`class` := "form-section")(
      div(`class` := "top-section")(
        h1("Log In")
      ),
      form(
        name    := "sign-in",
        `class` := "form",
        onEvent(
          "submit",
          e =>
            e.preventDefault()
            NoOp
        )
      )(
        renderInput("Email", "email", "text", true, UpdateEmail(_)),
        renderInput("Password", "password", "password", true, UpdatePassword(_)),
        button(`type` := "button", onClick(Login))("Log In"),
        status.fold(div())(s => div(s.message))
      )
    )

  private def renderInput(name: String, uid: String, kind: String, isRequired: Boolean, onChange: String => Msg) =
    div(`class` := "form-input")(
      label(`for` := name, `class` := "form-label")(if isRequired then span("*") else span(), text(name)),
      input(`type` := kind, `class` := "form-control", id := uid, onInput(onChange))
    )

  private def setErrorStatus(message: String): LoginPage   = this.copy(status = Some(Status(message, Kind.ERROR)))
  private def setSuccessStatus(message: String): LoginPage = this.copy(status = Some(Status(message, Kind.SUCCESS)))

object LoginPage:

  trait Msg                                         extends Page.Msg
  final case class UpdatePassword(password: String) extends Msg
  final case class UpdateEmail(email: String)       extends Msg
  final case class LoginError(message: String)      extends Msg
  final case class LoginSuccess(token: String)      extends Msg
  case object Login                                 extends Msg
  case object NoOp                                  extends Msg

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
