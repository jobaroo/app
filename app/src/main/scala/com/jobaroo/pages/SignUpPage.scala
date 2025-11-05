package com.jobaroo.pages

import io.circe.syntax.*
import io.circe.parser.*
import io.circe.generic.auto.*
import tyrian.*
import tyrian.http.*
import tyrian.Html.*
import cats.effect.IO
import tyrian.cmds.Logger
import com.jobaroo.common.constants
import com.jobaroo.pages.Page.Status
import com.jobaroo.pages.Page.Kind
import com.jobaroo.domain.auth.*
import com.jobaroo.common.Endpoint

final case class SignUpPage(
  email          : String = "",
  password       : String = "",
  confirmPassword: String = "",
  firstName      : String = "",
  lastName       : String = "",
  company        : String = "",
  status         : Option[Status] = None
) extends Page:

  import SignUpPage.*

  override def initCmd: Cmd[IO, Page.Msg] = Cmd.None

  override def update(msg: Page.Msg): (Page, Cmd[IO, Page.Msg]) = msg match
    case UpdateEmail(email)                     => (this.copy(email = email), Cmd.None)
    case UpdatePassword(password)               => (this.copy(password = password), Cmd.None)
    case UpdateConfirmPassword(confirmPassword) => (this.copy(confirmPassword = confirmPassword), Cmd.None)
    case UpdateFirstName(firstName)             => (this.copy(firstName = firstName), Cmd.None)
    case UpdateLastName(lastName)               => (this.copy(lastName = lastName), Cmd.None)
    case UpdateCompany(company)                 => (this.copy(company = company), Cmd.None)
    case SignUp                                 =>
      if !email.matches(constants.emailRegex) then (setErrorStatus("Email is invalid"), Cmd.None)
      else if password.isEmpty then (setErrorStatus("Please enter a password"), Cmd.None)
      else if password != confirmPassword then (setErrorStatus("Password fields do not match"), Cmd.None)
      else
        val newUserInfo = NewUserInfo(
          email = this.email,
          password = this.password,
          firstName = Option.when(this.firstName.nonEmpty)(this.firstName),
          lastName = Option.when(this.lastName.nonEmpty)(this.lastName),
          company = Option.when(this.company.nonEmpty)(this.company)
        )
        (this, commands.signup(newUserInfo))
    case SignUpError(message)                   => (setErrorStatus(message), Cmd.None)
    case SignUpSuccess(message)                 => (setSuccessStatus(message), Cmd.None)
    case NoOp                                   => (this, Cmd.None)

  override def view: Html[Page.Msg] =
    div(`class` := "form-section")(
      div(`class` := "top-section")(
        h1("Sign Up")
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
        renderInput("Confirm Password", "confirm-password", "password", true, UpdateConfirmPassword(_)),
        renderInput("First Name", "first-name", "text", false, UpdateFirstName(_)),
        renderInput("Last Name", "last-name", "text", false, UpdateLastName(_)),
        renderInput("Company", "company", "text", false, UpdateCompany(_)),
        button(`type` := "button", onClick(SignUp))("Sign Up"),
        status.fold(div())(s => div(s.message))
      )
    )

  private def renderInput(name: String, uid: String, kind: String, isRequired: Boolean, onChange: String => Msg) =
    div(`class` := "form-input")(
      label(`for` := name, `class` := "form-label")(if isRequired then span("*") else span(), text(name)),
      input(`type` := kind, `class` := "form-control", id := uid, onInput(onChange))
    )

  private def setErrorStatus(message: String): SignUpPage   = this.copy(status = Some(Status(message, Kind.ERROR)))
  private def setSuccessStatus(message: String): SignUpPage = this.copy(status = Some(Status(message, Kind.SUCCESS)))

object SignUpPage:

  trait Msg                                                       extends Page.Msg
  final case class UpdatePassword(password: String)               extends Msg
  final case class UpdateConfirmPassword(confirmPassword: String) extends Msg
  final case class UpdateEmail(email: String)                     extends Msg
  final case class UpdateFirstName(firstName: String)             extends Msg
  final case class UpdateLastName(lastName: String)               extends Msg
  final case class UpdateCompany(company: String)                 extends Msg
  case object SignUp                                              extends Msg
  case object NoOp                                                extends Msg
  final case class SignUpError(message: String)                   extends Msg
  final case class SignUpSuccess(message: String)                 extends Msg

  object endpooints:

    val signup = new Endpoint[Msg](
      location = constants.endpoints.signUp,
      method = Method.Post,
      onError = e => SignUpError(e.toString),
      onSuccess = resp =>
        resp.status match
          case tyrian.http.Status(201, _)                      => SignUpSuccess("Success! Log in now.")
          case tyrian.http.Status(s, _) if s >= 400 && s < 500 =>
            parse(resp.body).flatMap(json => json.hcursor.get[String]("error")) match
              case Left(e)      => SignUpError(s"Error: ${e.getMessage}")
              case Right(value) => SignUpError(value)
    ) {}

  object commands:

    def signup(newUserInfo: NewUserInfo): Cmd[IO, Msg] = endpooints.signup.call(newUserInfo)
