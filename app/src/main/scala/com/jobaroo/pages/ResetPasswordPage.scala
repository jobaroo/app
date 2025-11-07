package com.jobaroo.pages

import io.circe.syntax.*
import io.circe.parser.*
import io.circe.generic.auto.*
import tyrian.*
import tyrian.http.*
import tyrian.Html.*
import cats.effect.IO
import com.jobaroo.App
import com.jobaroo.common.*
import com.jobaroo.pages.Page
import com.jobaroo.common.Endpoint
import com.jobaroo.domain.auth.*
import com.jobaroo.App.Msg
import com.jobaroo.pages.Page.urls

final case class ResetPasswordPage(
  email   : String = "",
  token   : String = "",
  password: String = "",
  status  : Option[Page.Status] = None
) extends FormPage("Reset Password", status):

  import ResetPasswordPage.*

  override def update(msg: App.Msg): (Page, Cmd[IO, App.Msg]) = msg match
    case UpdateEmail(email)          => (this.copy(email = email), Cmd.None)
    case UpdateToken(token)          => (this.copy(token = token), Cmd.None)
    case UpdatePassword(password)    => (this.copy(password = password), Cmd.None)
    case ResetPassword               =>
      if !email.matches(constants.emailRegex) then (setErrorStatus("Invalid email"), Cmd.None)
      else if token.isEmpty then (setErrorStatus("Please add a token."), Cmd.None)
      else if password.isEmpty then (setErrorStatus("Please add a password."), Cmd.None)
      else (this, commands.resetPassword(email, token, password))
    case ResetPasswordFailure(error) => (setErrorStatus(error), Cmd.None)
    case ResetPasswordSuccess        => (setSuccessStatus("Success! You can log in now!"), Cmd.None)

  override protected def renderFormContent(): List[Html[App.Msg]] = List(
    renderInput("Email", "email", "text", true, UpdateEmail(_)),
    renderInput("Token", "token", "text", true, UpdateToken(_)),
    renderInput("Password", "password", "password", true, UpdatePassword(_)),
    button(`type` := "button", onClick(ResetPassword))("Set Password"),
    renderAuxLink(urls.forgotPassword, "Don't have a token yet?")
  )

  private def setErrorStatus(message: String): Page   = this.copy(status = Some(Page.Status(message, Page.Kind.ERROR)))
  private def setSuccessStatus(message: String): Page = this.copy(status = Some(Page.Status(message, Page.Kind.SUCCESS)))

object ResetPasswordPage:

  trait Msg                                            extends App.Msg
  final case class UpdateEmail(email: String)          extends Msg
  final case class UpdateToken(token: String)          extends Msg
  final case class UpdatePassword(password: String)    extends Msg
  final case class ResetPasswordFailure(error: String) extends Msg
  case object ResetPasswordSuccess                     extends Msg
  case object ResetPassword                            extends Msg

  object endpoints:

    val resetPassword = new Endpoint[Msg](
      location = constants.endpoints.resetPassword,
      method = Method.Post,
      onError = e => ResetPasswordFailure(e.toString),
      onResponse = resp =>
        resp.status match
          case Status(200, _)                      => ResetPasswordSuccess
          case Status(s, _) if s >= 400 && s < 500 =>
            parse(resp.body).flatMap(json => json.hcursor.get[String]("error")) match
              case Left(e)      => ResetPasswordFailure(s"Error: ${e.getMessage}")
              case Right(value) => ResetPasswordFailure(value)
    ) {}

  object commands:

    def resetPassword(email: String, token: String, password: String): Cmd[IO, App.Msg] =
      val recoverPasswordInfo = RecoverPasswordInfo(email, token, password)
      endpoints.resetPassword.call(recoverPasswordInfo)
