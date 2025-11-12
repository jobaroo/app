package com.jobaroo.pages

import tyrian.*
import cats.effect.*
import com.jobaroo.App
import com.jobaroo.components.Component
import java.util.UUID

abstract class Page extends Component[App.Msg, Page]:

  import Page.*

  def initCmd: Cmd[IO, App.Msg]
  def update(msg: App.Msg): (Page, Cmd[IO, App.Msg])
  def view: Html[App.Msg]

object Page:

  enum Kind:
    case SUCCESS, ERROR, LOADING

  final case class Status(message: String, kind: Kind)

  object urls:

    val login                 = "/login"
    val signup                = "/signup"
    val jobs                  = "/jobs"
    val forgotPassword        = "/forgot-password"
    val resetPassword         = "/recover-password"
    val profile               = "/profile"
    val postJob               = "/post-job"
    val empty                 = ""
    val home                  = "/"
    val hash                  = "#"
    def job(id: UUID): String = s"/jobs/$id"

  def apply(location: String): Page = location match
    case urls.`login`                             => LoginPage()
    case urls.`signup`                            => SignUpPage()
    case urls.`forgotPassword`                    => ForgotPasswordPage()
    case urls.`resetPassword`                     => ResetPasswordPage()
    case urls.`profile`                           => ProfilePage()
    case urls.`postJob`                           => PostJobPage()
    case urls.`empty` | urls.`home` | urls.`jobs` => JobListPage()
    case s"/jobs/$id"                             => JobPage(id)
    case _                                        => NotFoundPage()
