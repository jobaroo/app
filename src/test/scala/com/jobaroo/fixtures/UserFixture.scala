package com.jobaroo.fixtures

import com.jobaroo.domain.user.*
import com.jobaroo.domain.user.Role.RECRUITER

trait UserFixture:

  val christopherNolan = User(
    email = "christopher@nolan.com",
    hashedPassword = "secret",
    role = Role.ADMIN,
    firstName = Some("Christopher"),
    lastName = Some("Nolan"),
    company = Some("Google")
  )

  val johnnyDepp = User(
    email = "johnny@depp.com",
    hashedPassword = "another_secret",
    role = Role.RECRUITER,
    firstName = Some("Johnny"),
    lastName = Some("Depp"),
    company = Some("Amazon")
  )

  val jenniferLawrence = User(
    email = "jennifer@lawrence.com",
    hashedPassword = "pwd",
    role = Role.RECRUITER,
    firstName = Some("Jennifer"),
    lastName = Some("Lawrence"),
    company = Some("Meta")
  )
