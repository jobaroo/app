package com.jobaroo.domain

import cats.Applicative
import cats.syntax.applicative.*
import doobie.util.meta.Meta
import tsec.authorization.{AuthGroup, AuthorizationInfo, SimpleAuthEnum}
import com.jobaroo.domain.job.Job

object user:

  final case class User(
    email         : String,
    hashedPassword: String,
    role          : Role,
    firstName     : Option[String],
    lastName      : Option[String],
    company       : Option[String]
  ):

    def owns(job: Job): Boolean = email == job.ownerEmail
    def isAdmin: Boolean        = role == Role.ADMIN
    def isRecruiter: Boolean    = role == Role.RECRUITER

  enum Role:
    case ADMIN, RECRUITER

  object Role:

    given Meta[Role] = Meta[String].imap(Role.valueOf)(_.toString)

    given SimpleAuthEnum[Role, String] with

      override protected val values: AuthGroup[Role] = AuthGroup(Role.ADMIN, Role.RECRUITER)
      override def getRepr(role: Role): String       = role.toString

    given authRole[F[_]: Applicative]: AuthorizationInfo[F, Role, User] with
      override def fetchInfo(u: User): F[Role] = u.role.pure[F]
