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
import com.jobaroo.pages.Page.urls
import com.jobaroo.components.Anchors

final case class ForgotPasswordPage(email: String = "", status: Option[Page.Status] = None)
  extends FormPage("Recover Password", status):

  import ForgotPasswordPage.*

  override def renderFormContent(): List[Html[App.Msg]] = List(
    renderInput("Email", "email", "email", true, email, UpdateEmail(_)),
    renderPrimaryAction("Send Recovery Email", ResetPassword),
    Anchors.renderAuxLink(urls.resetPassword, "Have a token?")
  )

  override def update(msg: App.Msg): (Page, Cmd[IO, App.Msg]) = msg match
    case UpdateEmail(email)          => (this.copy(email = email), Cmd.None)
    case ResetPassword               =>
      if !email.matches(constants.emailRegex) then (setErrorStatus("Invalid email"), Cmd.None)
      else (this, commands.resetPassword(email))
    case ResetPasswordSuccess        => (setSuccessStatus("Check your inbox!"), Cmd.None)
    case ResetPasswordFailure(error) => (setErrorStatus(error), Cmd.None)

  private def setErrorStatus(message: String): Page   = this.copy(status = Some(Page.Status(message, Page.Kind.ERROR)))
  private def setSuccessStatus(message: String): Page = this.copy(status = Some(Page.Status(message, Page.Kind.SUCCESS)))

object ForgotPasswordPage:

  trait Msg                                            extends App.Msg
  final case class UpdateEmail(email: String)          extends Msg
  case object ResetPassword                            extends Msg
  final case class ResetPasswordFailure(error: String) extends Msg
  case object ResetPasswordSuccess                     extends Msg

  object endpoints:

    val resetPassword = new Endpoint[Msg](
      location = constants.endpoints.forgotPassword,
      method = Method.Post,
      onError = e => ResetPasswordFailure(e.toString),
      onResponse = _ => ResetPasswordSuccess
    ) {}

  object commands:

    def resetPassword(email: String): Cmd[IO, App.Msg] = endpoints.resetPassword.call(ForgottenPasswordInfo(email))
