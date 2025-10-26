package dojo

import cats.effect.*
import tsec.passwordhashers.PasswordHash
import tsec.passwordhashers.jca.BCrypt

object passwordHashing extends IOApp.Simple:

  override def run: IO[Unit] =
    BCrypt.hashpw[IO]("secret").flatMap(IO.println) *>
      BCrypt.hashpw[IO]("another_secret").flatMap(IO.println) *>
        BCrypt.hashpw[IO]("pwd").flatMap(IO.println)
