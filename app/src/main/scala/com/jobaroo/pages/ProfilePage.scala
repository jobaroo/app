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

import tyrian.Cmd

import cats.effect.IO
import com.jobaroo.core.Session

final case class ProfilePage(oldPassword: String = "", newPassword: String = "", status: Option[Page.Status] = None)
  extends FormPage("Profile", status):

  import ProfilePage.*

  override def update(msg: App.Msg): (Page, Cmd[IO, App.Msg]) = msg match
    case UpdateNewPassword(newPassword) => (this.copy(newPassword = newPassword), Cmd.None)
    case UpdateOldPassword(oldPassword) => (this.copy(oldPassword = oldPassword), Cmd.None)
    case ChangePassword                 =>
      if oldPassword.isEmpty then (setErrorStatus("Insert old password"), Cmd.None)
      else if newPassword.isEmpty then (setErrorStatus("Insert new password"), Cmd.None)
      else (this, commands.changePassword(oldPassword, newPassword))
    case ChangePasswordFailure(error)   => (setErrorStatus(error), Cmd.None)
    case ChangePasswordSuccess          => (setSuccessStatus("Password was successfully changed!"), Cmd.None)

  override protected def renderFormContent(): List[Html[App.Msg]] = List(
    renderInput("Old Password", "oldPassword", "password", true, UpdateOldPassword(_)),
    renderInput("New Password", "newPassword", "password", true, UpdateNewPassword(_)),
    button(`type` := "button", onClick(ChangePassword))("Change Password")
  )

  override def view: Html[App.Msg] =
    if Session.isActive then super.view else div(h1("Profile"), div("It seems you're not logged in."))

  private def setErrorStatus(message: String): Page   = this.copy(status = Some(Page.Status(message, Page.Kind.ERROR)))
  private def setSuccessStatus(message: String): Page = this.copy(status = Some(Page.Status(message, Page.Kind.SUCCESS)))

object ProfilePage:

  trait Msg                                               extends App.Msg
  final case class UpdateNewPassword(newPassword: String) extends Msg
  final case class UpdateOldPassword(oldPassword: String) extends Msg
  final case class ChangePasswordFailure(error: String)   extends Msg
  case object ChangePassword                              extends Msg
  case object ChangePasswordSuccess                       extends Msg

  object endpoints:

    val changePassword = new Endpoint[Msg](
      location = constants.endpoints.changePassword,
      method = Method.Put,
      onError = e => ChangePasswordFailure(e.toString),
      onResponse = resp =>
        resp.status match
          case Status(200, _)                      => ChangePasswordSuccess
          case Status(403, _)                      => ChangePasswordFailure("Invalid password")
          case Status(s, _) if s >= 400 && s < 500 =>
            parse(resp.body).flatMap(json => json.hcursor.get[String]("error")) match
              case Left(e)      => ChangePasswordFailure(s"Error: ${e.getMessage}")
              case Right(value) => ChangePasswordFailure(value)
    ) {}

  object commands:

    def changePassword(oldPassword: String, newPassword: String): Cmd[IO, App.Msg] =
      endpoints.changePassword.callAuthorized(NewPasswordInfo(oldPassword, newPassword))
