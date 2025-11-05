package com.jobaroo.common

object constants:

  val emailRegex =
    """^[a-zA-Z0-9\.!#$%&'*+/=?^_`{|}~-]+@[a-zA-Z0-9](?:[a-zA-Z0-9-]{0,61}[a-zA-Z0-9])?(?:\.[a-zA-Z0-9](?:[a-zA-Z0-9-]{0,61}[a-zA-Z0-9])?)*$"""

  object endpoints:
    val root = "http://localhost:8080"
    val signUp = s"$root/api/auth/users"
    val login = s"$root/api/auth/login"