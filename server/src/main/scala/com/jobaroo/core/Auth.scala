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
import cats.data.OptionT
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
import tsec.authentication.IdentityStore
import scala.concurrent.duration.*
import tsec.authentication.BackingStore
import tsec.common.SecureRandomId
import cats.effect.kernel.Ref
import com.jobaroo.config.SecurityConfig

trait Auth[F[_]]:

  def login(email: String, password: String): F[Option[User]]
  def signUp(newUserInfo: NewUserInfo): F[Option[User]]
  def changePassword(email: String, newPasswordInfo: NewPasswordInfo): F[Either[String, Option[User]]]
  def delete(email: String): F[Boolean]
  def sendPasswordRecoveryToken(email: String): F[Unit]
  def recoverPasswordFromToken(email: String, token: String, newPassword: String): F[Boolean]

final class LiveAuth[F[_] : Async : Logger] private (val users: Users[F], val emails: Emails[F], val tokens: Tokens[F])
  extends Auth[F]:

  override def login(email: String, password: String): F[Option[User]] =
    for
      optUser       <- users.find(email)
      validatedUser <- optUser.filterA { user =>
                         BCrypt.checkpwBool[F](password, PasswordHash[BCrypt](user.hashedPassword))
                       }
    yield validatedUser

  override def signUp(newUserInfo: NewUserInfo): F[Option[User]] =
    for
      optUser       <- users.find(newUserInfo.email)
      validatedUser <- optUser match
                         case Some(_) => None.pure[F]
                         case None    =>
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
                             res       <- if passCheck then updatePassword(user, newPasswordInfo.newPassword).map(Right(_))
                                          else Left("Invalid password").pure[F]
                           yield res
    yield validatedUser

  override def delete(email: String): F[Boolean] = users.delete(email)

  override def recoverPasswordFromToken(email: String, token: String, newPassword: String): F[Boolean] =
    for
      optUser      <- users.find(email)
      isTokenValid <- tokens.checkToken(email, token)
      res          <- (optUser, isTokenValid) match
                        case (Some(user), true) => updatePassword(user, newPassword).map(_.nonEmpty)
                        case _                  => false.pure[F]
    yield res

  override def sendPasswordRecoveryToken(email: String): F[Unit] = tokens.getToken(email).flatMap {
    case Some(token) => emails.sendPasswordRecovery(email, token)
    case None        => ().pure[F]
  }

  private def updatePassword(user: User, newPassword: String): F[Option[User]] =
    for
      hashedPassword <- BCrypt.hashpw[F](newPassword)
      updatedUser    <- users.update(user.copy(hashedPassword = hashedPassword))
    yield updatedUser

object LiveAuth:

  def apply[F[_] : Async : Logger](users: Users[F], emails: Emails[F], tokens: Tokens[F]): F[LiveAuth[F]] =
    new LiveAuth[F](users, emails, tokens).pure[F]
