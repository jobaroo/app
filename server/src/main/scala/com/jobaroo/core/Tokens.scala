package com.jobaroo.core

import cats.effect.MonadCancelThrow
import cats.*
import cats.syntax.all.*
import cats.implicits.*
import org.typelevel.log4cats.*
import doobie.util.transactor.Transactor
import com.jobaroo.config.TokenConfig
import doobie.*
import doobie.util.*
import doobie.implicits.*
import doobie.postgres.implicits.*
import scala.util.Random

trait Tokens[F[_]]:

  def getToken(email: String): F[Option[String]]
  def checkToken(email: String, token: String): F[Boolean]

final class LiveTokens[F[_] : MonadCancelThrow : Logger] private (
  users      : Users[F],
  xa         : Transactor[F],
  tokenConfig: TokenConfig
) extends Tokens[F]:

  override def getToken(email: String): F[Option[String]] =
    users.find(email).flatMap {
      case None    => None.pure[F]
      case Some(_) => getFreshToken(email).map(Some(_))
    }

  override def checkToken(email: String, token: String): F[Boolean] =
    sql"""SELECT token FROM tokens WHERE email = $email AND token = $token AND expiration > ${System.currentTimeMillis()}"""
      .query[String]
      .option
      .transact(xa)
      .map(_.nonEmpty)

  private def randomToken(maxLength: Int): F[String] =
    Random.alphanumeric.map(Character.toUpperCase).take(maxLength).mkString.pure[F]

  private def getFreshToken(email: String): F[String] =
    findToken(email).flatMap {
      case None    => insertToken(email)
      case Some(_) => updateToken(email)
    }

  private def updateToken(email: String): F[String] =
    for
      token <- randomToken(8)
      _     <-
        sql"""UPDATE tokens SET token = $token, expiration = ${System.currentTimeMillis() + tokenConfig.tokenDuration} WHERE email = $email"""
          .update
          .run
          .transact(xa)
    yield token

  private def insertToken(email: String): F[String] =
    for
      token <- randomToken(8)
      _     <-
        sql"""INSERT INTO tokens (email, token, expiration) VALUES ($email, $token, ${System.currentTimeMillis() + tokenConfig.tokenDuration})"""
          .update
          .run
          .transact(xa)
    yield token

  private def findToken(email: String): F[Option[String]] =
    sql"""SELECT token from tokens WHERE email = $email"""
      .query[String]
      .option
      .transact(xa)

object LiveTokens:

  def apply[F[_] : MonadCancelThrow : Logger](
    users: Users[F],
    xa: Transactor[F],
    tokenConfig: TokenConfig
  ): F[LiveTokens[F]] = new LiveTokens[F](users, xa, tokenConfig).pure[F]
