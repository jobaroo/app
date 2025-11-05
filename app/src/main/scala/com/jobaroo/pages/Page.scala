package com.jobaroo.pages

import tyrian.*
import cats.effect.*
import com.jobaroo.pages.Page.urls.login
import com.jobaroo.App

abstract class Page:

  import Page.*

  def initCmd: Cmd[IO, App.Msg]
  def update(msg: App.Msg): (Page, Cmd[IO, App.Msg])
  def view: Html[App.Msg]

object Page:

  enum Kind:
    case SUCCESS, ERROR, LOADING

  final case class Status(message: String, kind: Kind)

  object urls:

    val login           = "/login"
    val signup          = "/signup"
    val jobs            = "/jobs"
    val forgotPassword  = "/forgot-password"
    val recoverPassword = "/recover-password"
    val empty           = ""
    val home            = "/"

  def apply(location: String): Page = location match
    case urls.`login`                             => LoginPage()
    case urls.`signup`                            => SignUpPage()
    case urls.`forgotPassword`                    => ForgotPasswordPage()
    case urls.`recoverPassword`                   => RecoverPasswordPage()
    case urls.`empty` | urls.`home` | urls.`jobs` => JobListPage()
    case s"/jobs/$id"                             => JobPage(id)
    case _                                        => NotFoundPage()
