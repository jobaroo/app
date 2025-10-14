package com.jobaroo.domain

object auth:

  final case class LoginInfo(
    email   : String,
    password: String
  )

  final case class NewPasswordInfo(
    oldPassword: String,
    newPassword: String
  )

  final case class NewUserInfo(
    email    : String,
    password : String,
    firstName: Option[String],
    lastName : Option[String],
    company  : Option[String]
  )
