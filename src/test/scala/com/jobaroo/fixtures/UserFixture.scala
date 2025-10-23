package com.jobaroo.fixtures

import com.jobaroo.domain.user.*
import com.jobaroo.domain.user.Role.RECRUITER
import com.jobaroo.core.Users
import cats.effect.IO
import cats.syntax.all.*

trait UserFixture:

  val christopherNolanPassword = "secret"
  val johnnyDeppPassword       = "another_secret"
  val jenniferLawrencePassword = "pwd"

  val christopherNolan = User(
    email = "christopher@nolan.com",
    hashedPassword = "$2a$10$wjYnY4RXhmgIAuf6ZGVSiOeScly6.lzSTWpsLTxCoAM4QyK4C5Xr6",
    role = Role.ADMIN,
    firstName = Some("Christopher"),
    lastName = Some("Nolan"),
    company = Some("Google")
  )

  val johnnyDepp = User(
    email = "johnny@depp.com",
    hashedPassword = "$2a$10$P101kyiWnYie4mNL58Wrgua/w2DKDo22eTh/5HgkWIP.43MY7KEte",
    role = Role.RECRUITER,
    firstName = Some("Johnny"),
    lastName = Some("Depp"),
    company = Some("Amazon")
  )

  val jenniferLawrence = User(
    email = "jennifer@lawrence.com",
    hashedPassword = "$2a$10$xQt8MazSRexmSWIDRz2hauTKehjhT9xKAzPim5AC1vN0SgMZBF7Uy", // pwd
    role = Role.ADMIN,
    firstName = Some("Jennifer"),
    lastName = Some("Lawrence"),
    company = Some("Meta")
  )

  val mockedUsers: Users[IO] = new Users[IO]:

    override def find(email: String): IO[Option[User]] =
      if email == christopherNolan.email then IO.pure(christopherNolan.some) else IO.pure(None)

    override def create(user: User): IO[String]       = IO.pure(user.email)
    override def update(user: User): IO[Option[User]] = IO.pure(user.some)
    override def delete(email: String): IO[Boolean]   = IO.pure(true)
