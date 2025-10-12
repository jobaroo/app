package com.jobaroo.core

import cats.*
import cats.syntax.all.*
import cats.implicits.*
import cats.Applicative
import com.jobaroo.domain.job.*
import doobie.*
import doobie.util.*
import doobie.implicits.*
import doobie.postgres.implicits.*
import cats.effect.MonadCancelThrow
import com.jobaroo.domain.pagination.Pagination
import org.typelevel.log4cats.*
import com.jobaroo.logging.syntax.*
import java.util.UUID

trait Jobs[F[_]]:

  def create(ownerEmail: String, jobInfo: JobInfo): F[UUID]
  def update(id: UUID, jobInfo: JobInfo): F[Option[Job]]
  def delete(id: UUID): F[Int]
  def find(id: UUID): F[Option[Job]]
  def all(): F[List[Job]]
  def all(filter: JobFilter, pagination: Pagination): F[List[Job]]

class LiveJobs[F[_] : MonadCancelThrow : Logger] private (xa: Transactor[F]) extends Jobs[F]:

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

  override def all(filter: JobFilter, pagination: Pagination): F[List[Job]] =
    val selectFragment: Fragment =
      fr"""
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
        """

    val fromFragment: Fragment =
      fr"""FROM jobs"""

    val whereFragment: Fragment =
      Fragments.whereAndOpt(
        filter.companies.toNel.map { Fragments.in(fr"company", _) },
        filter.locations.toNel.map { Fragments.in(fr"location", _) },
        filter.countries.toNel.map { Fragments.in(fr"country", _) },
        filter.seniorities.toNel.map { Fragments.in(fr"seniority", _) },
        filter.remote.some.map { remote => fr"remote = $remote" },
        filter.maxSalary.map { maxSalary => fr"salary > $maxSalary" },
        filter.tags.toNel.map { tags => Fragments.or(tags.toList.map(tag => fr"$tag=any(tags)")*) }
      )

    val paginationFragment: Fragment =
      fr"""ORDER BY id LIMIT ${pagination.limit} OFFSET ${pagination.offset}"""

    val statement = selectFragment |+| fromFragment |+| whereFragment |+| paginationFragment
    Logger[F].info(statement.toString) *> statement.query[Job].to[List].transact(xa).logError(_.getMessage)

object LiveJobs:

  def apply[F[_] : MonadCancelThrow : Logger](xa: Transactor[F]): F[LiveJobs[F]] = new LiveJobs[F](xa).pure[F]
