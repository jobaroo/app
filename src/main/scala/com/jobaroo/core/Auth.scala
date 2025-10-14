package com.jobaroo.core

import tsec.authentication.{AugmentedJWT, JWTAuthenticator}
import tsec.mac.jca.HMACSHA256
import com.jobaroo.domain.security.*
import com.jobaroo.domain.auth.*
import com.jobaroo.domain.user.*
import cats.*
import cats.effect.Async
import cats.syntax.all.*
import cats.implicits.*
import cats.Applicative
import com.jobaroo.domain.job.*
import doobie.*
import doobie.util.*
import doobie.implicits.*
import doobie.postgres.implicits.*
import cats.effect.{IO, MonadCancelThrow}
import com.jobaroo.domain.pagination.Pagination
import org.typelevel.log4cats.*
import com.jobaroo.logging.syntax.*
import tsec.passwordhashers.PasswordHash
import tsec.passwordhashers.jca.BCrypt

trait Auth[F[_]]:

  def login(email: String, password: String): F[Option[JwtToken]]
  def signUp(newUserInfo: NewUserInfo): F[Option[User]]
  def changePassword(email: String, newPasswordInfo: NewPasswordInfo): F[Either[String, Option[User]]]

final class LiveAuth[F[_] : Async : Logger] private (
  val users        : Users[F],
  val authenticator: Authenticator[F]
) extends Auth[F]:

  override def login(email: String, password: String): F[Option[JwtToken]] =
    for
      optUser       <- users.find(email)
      validatedUser <- optUser.filterA { user =>
                         BCrypt.checkpwBool[F](password, PasswordHash[BCrypt](user.hashedPassword))
                       }
      optJwtToken   <- validatedUser.traverse { user => authenticator.create(user.email) }
    yield optJwtToken

  override def signUp(newUserInfo: NewUserInfo): F[Option[User]] =
    for
      optUser       <- users.find(newUserInfo.email)
      validatedUser <- optUser match
                         case Some(user) => None.pure[F]
                         case None       =>
                           for
                             hashedPwd <- BCrypt.hashpw[F](newUserInfo.password)
                             newUser = User(
                                         email = newUserInfo.email,
                                         hashedPassword = hashedPwd,
                                         role = Role.RECRUITER,
                                         firstName = newUserInfo.firstName,
                                         lastName = newUserInfo.lastName,
                                         company = newUserInfo.company
                                       )
                             _ <- users.create(newUser)
                           yield Some(newUser)
    yield validatedUser

  override def changePassword(email: String, newPasswordInfo: NewPasswordInfo): F[Either[String, Option[User]]] =
    def updatePassword(user: User): F[Option[User]] =
      for
        hashedPassword <- BCrypt.hashpw[F](newPasswordInfo.newPassword)
        updatedUser    <- users.update(user.copy(hashedPassword = hashedPassword))
      yield updatedUser

    for
      optUser       <- users.find(email)
      validatedUser <- optUser match
                         case None       => Right(None).pure[F]
                         case Some(user) =>
                           for
                             passCheck <- BCrypt.checkpwBool[F](
                                            newPasswordInfo.oldPassword,
                                            PasswordHash[BCrypt](user.hashedPassword)
                                          )
                             res       <- if passCheck then updatePassword(user).map(Right(_))
                                          else Left("Invalid password").pure[F]
                           yield res
    yield validatedUser

object LiveAuth:

  def apply[F[_] : Async : Logger](users: Users[F], authenticator: Authenticator[F]): F[LiveAuth[F]] =
    new LiveAuth[F](users, authenticator).pure[F]
