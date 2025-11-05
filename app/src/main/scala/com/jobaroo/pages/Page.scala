package com.jobaroo.pages

import tyrian.*
import cats.effect.*
import com.jobaroo.pages.Page.urls.login

abstract class Page:

  import Page.*

  def initCmd: Cmd[IO, Msg]
  def update(msg: Msg): (Page, Cmd[IO, Msg])
  def view: Html[Msg]

object Page:

  trait Msg

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
