package com.jobaroo.domain

import doobie.util.meta.Meta

object user:

  final case class User(
    email         : String,
    hashedPassword: String,
    role          : Role,
    firstName     : Option[String],
    lastName      : Option[String],
    company       : Option[String]
  )

  enum Role:
    case ADMIN, RECRUITER

  object Role:

    given Meta[Role] = Meta[String].imap(Role.valueOf)(_.toString)
