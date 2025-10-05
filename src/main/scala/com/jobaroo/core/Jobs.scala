package com.jobaroo.core

import cats.*
import cats.syntax.*
import cats.implicits.*
import cats.Applicative
import com.jobaroo.domain.job.*
import doobie.*
import doobie.util.*
import doobie.implicits.*
import doobie.postgres.implicits.*
import cats.effect.MonadCancelThrow

import java.util.UUID

trait Jobs[F[_]]:

  def create(ownerEmail: String, jobInfo: JobInfo): F[UUID]
  def update(id: UUID, jobInfo: JobInfo): F[Option[Job]]
  def delete(id: UUID): F[Int]
  def find(id: UUID): F[Option[Job]]
  def all(): F[List[Job]]

class LiveJobs[F[_]: MonadCancelThrow] private (xa: Transactor[F]) extends Jobs[F]:

  override def create(ownerEmail: String, jobInfo: JobInfo): F[UUID] =
    sql"""
         INSERT INTO jobs(
          date,
          ownerEmail,
          company,
          title,
          description,
          externalUrl,
          location,
          remote,
          salaryLow,
          salaryHigh,
          currency,
          country,
          tags,
          image,
          seniority,
          other,
          active
         ) VALUES (
          ${System.currentTimeMillis()},
          $ownerEmail,
          ${jobInfo.company},
          ${jobInfo.title},
          ${jobInfo.description},
          ${jobInfo.externalUrl},
          ${jobInfo.location},
          ${jobInfo.remote},
          ${jobInfo.salaryLow},
          ${jobInfo.salaryHigh},
          ${jobInfo.currency},
          ${jobInfo.country},
          ${jobInfo.tags},
          ${jobInfo.image},
          ${jobInfo.seniority},
          ${jobInfo.other},
          false
         )
       """
      .update
      .withUniqueGeneratedKeys[UUID]("id")
      .transact(xa)

  override def update(id: UUID, jobInfo: JobInfo): F[Option[Job]] =
    sql"""
         UPDATE jobs
         SET
          company = ${jobInfo.company},
          title = ${jobInfo.title},
          description = ${jobInfo.description},
          externalUrl = ${jobInfo.externalUrl},
          location = ${jobInfo.location},
          remote = ${jobInfo.remote},
          salaryLow = ${jobInfo.salaryLow},
          salaryHigh = ${jobInfo.salaryHigh},
          currency = ${jobInfo.currency},
          country = ${jobInfo.country},
          tags = ${jobInfo.tags},
          image = ${jobInfo.image},
          seniority = ${jobInfo.seniority},
          other = ${jobInfo.other}
        WHERE id = $id
       """
      .update
      .run
      .transact(xa)
      .flatMap(_ => find(id))

  override def delete(id: UUID): F[Int] =
    sql"""
         DELETE FROM jobs WHERE id = $id
       """
      .update
      .run
      .transact(xa)

  override def find(id: UUID): F[Option[Job]] =
    sql"""
         SELECT
          id,
          date,
          ownerEmail,
          company,
          title,
          description,
          externalUrl,
          location,
          remote,
          salaryLow,
          salaryHigh,
          currency,
          country,
          tags,
          image,
          seniority,
          other,
          active
        FROM jobs
        WHERE id = $id
       """
      .query[Job]
      .option
      .transact(xa)

  override def all(): F[List[Job]] =
    sql"""
         SELECT
          id,
          date,
          ownerEmail,
          company,
          title,
          description,
          externalUrl,
          location,
          remote,
          salaryLow,
          salaryHigh,
          currency,
          country,
          tags,
          image,
          seniority,
          other,
          active
        FROM jobs
       """
      .query[Job]
      .to[List]
      .transact(xa)

object LiveJobs:

  def apply[F[_]: MonadCancelThrow](xa: Transactor[F]): F[LiveJobs[F]] = new LiveJobs[F](xa).pure[F]
