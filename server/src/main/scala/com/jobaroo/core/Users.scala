package com.jobaroo.core

import cats.effect.MonadCancelThrow
import cats.*
import cats.syntax.all.*
import cats.implicits.*
import org.typelevel.log4cats.*
import com.jobaroo.domain.user.*
import doobie.*
import doobie.util.*
import doobie.implicits.*
import doobie.postgres.implicits.*

trait Users[F[_]]:

  def find(email: String): F[Option[User]]
  def create(user: User): F[String]
  def update(user: User): F[Option[User]]
  def delete(email: String): F[Boolean]

final class LiveUsers[F[_] : MonadCancelThrow : Logger] private (val xa: Transactor[F]) extends Users[F]:

  override def find(email: String): F[Option[User]] =
    sql"""SELECT * FROM users WHERE email = $email"""
      .query[User]
      .option
      .transact(xa)

  override def create(user: User): F[String] =
    sql"""
          INSERT INTO users(
            email,
            hashedPassword,
            role,
            firstName,
            lastName,
            company
          ) VALUES (
            ${user.email},
            ${user.hashedPassword},
            ${user.role},
            ${user.firstName},
            ${user.lastName},
            ${user.company}
          )"""
      .update
      .run
      .transact(xa)
      .map(_ => user.email)

  override def update(user: User): F[Option[User]] =
    sql"""
         UPDATE users SET
          hashedPassword = ${user.hashedPassword},
          role = ${user.role},
          firstName = ${user.firstName},
          lastName = ${user.lastName},
          company = ${user.company}
        WHERE email = ${user.email}
       """
      .update
      .run
      .transact(xa)
      .flatMap(_ => find(user.email))

  override def delete(email: String): F[Boolean] =
    sql"""DELETE FROM users WHERE email = $email"""
      .update
      .run
      .transact(xa)
      .map(_ > 0)

object LiveUsers:
  def apply[F[_] : MonadCancelThrow : Logger](xa: Transactor[F]): F[LiveUsers[F]] = new LiveUsers[F](xa).pure[F]
