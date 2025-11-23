package com.jobaroo.common

import scala.scalajs.js
import scala.scalajs.js.annotation.*

object constants:

  @js.native
  @JSImport("url:/static/img/jobaroo.png", JSImport.Default)
  val logoImage: String = js.native

  val emailRegex =
    """^[a-zA-Z0-9\.!#$%&'*+/=?^_`{|}~-]+@[a-zA-Z0-9](?:[a-zA-Z0-9-]{0,61}[a-zA-Z0-9])?(?:\.[a-zA-Z0-9](?:[a-zA-Z0-9-]{0,61}[a-zA-Z0-9])?)*$"""

  val defaultPageSize = 20

  object cookies:

    val duration = 10 * 24 * 3600 * 1000
    val email    = "email"
    val token    = "token"

  object endpoints:

    val root              = "http://localhost:8080"
    val signUp            = s"$root/api/auth/users"
    val login             = s"$root/api/auth/login"
    val logout            = s"$root/api/auth/logout"
    val checkToken        = s"$root/api/auth/checkToken"
    val forgotPassword    = s"$root/api/auth/reset"
    val resetPassword     = s"$root/api/auth/recover"
    val changePassword    = s"$root/api/auth/users/password"
    val createJob         = s"$root/api/jobs/create"
    val createJobPromoted = s"$root/api/jobs/promoted"
    val jobs              = s"$root/api/jobs"
    val jobFilters        = s"$root/api/jobs/filters"
