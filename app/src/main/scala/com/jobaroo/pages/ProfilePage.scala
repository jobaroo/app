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

import com.jobaroo.core.Session
import com.jobaroo.components.AppLayout
import com.jobaroo.tyrianui.core.UiAttrs
import com.jobaroo.tyrianui.html.Tags.{div, span}
import com.jobaroo.ui.preset.Jobaroo

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
    renderInput("Old Password", "oldPassword", "password", true, oldPassword, UpdateOldPassword(_)),
    renderInput("New Password", "newPassword", "password", true, newPassword, UpdateNewPassword(_)),
    renderPrimaryAction("Change Password", ChangePassword)
  )

  override def view: Html[App.Msg] =
    if Session.isActive then super.view
    else
      AppLayout.pageContainer(
        div(UiAttrs.classes(Jobaroo.state.centeredWide))(
          div(UiAttrs.classes(Jobaroo.alert.warningCard))(
            span()(text("You need to be logged in to manage your profile settings."))
          )
        )
      )

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
