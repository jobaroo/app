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
import fs2.Stream
import com.jobaroo.logging.syntax.*
import java.util.UUID
import java.{util => ju}

trait Jobs[F[_]]:

  def create(ownerEmail: String, jobInfo: JobInfo): F[UUID]
  def update(id: UUID, jobInfo: JobInfo): F[Option[Job]]
  def delete(id: UUID): F[Int]
  def find(id: UUID): F[Option[Job]]
  def all(): Stream[F, Job]
  def all(filter: JobFilter, pagination: Pagination): F[List[Job]]
  def possibleFilters(): F[JobFilter]
  def activate(id: UUID): F[Int]

final class LiveJobs[F[_] : MonadCancelThrow : Logger] private (val xa: Transactor[F]) extends Jobs[F]:

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

  override def activate(id: UUID): F[Int] = 
    sql"""
        UPDATE jobs SET active = true WHERE id = $id
        """
        .update
        .run
        .transact(xa)
        
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
        WHERE id = $id AND active = true
       """
      .query[Job]
      .option
      .transact(xa)

  override def all(): Stream[F, Job] =
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
        WHERE active = true
       """
      .query[Job]
      .stream
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
        filter.remote.some.filter(identity).map { remote => fr"remote = $remote" },
        /* TODO - if salary not specified on UI then it's set to 0, this case is error prone when salary range is not a
         * required field */
        filter.maxSalary.map { maxSalary => fr"salaryHigh > $maxSalary OR salaryHigh IS NULL" },
        filter.tags.toNel.map { tags => Fragments.or(tags.toList.map(tag => fr"$tag=any(tags)")*) },
        fr"active = true".some
      )

    val paginationFragment: Fragment =
      fr"""ORDER BY id LIMIT ${pagination.limit} OFFSET ${pagination.offset}"""

    val statement = selectFragment |+| fromFragment |+| whereFragment |+| paginationFragment

    statement.query[Job].to[List].transact(xa).log(
      success = jobs => jobs.map(_.id.toString).mkString("\n"),
      error = _.getMessage
    )

  override def possibleFilters(): F[JobFilter] =
    sql"""
        SELECT
            ARRAY(SELECT DISTINCT(company) FROM jobs WHERE active = true) AS companies,
            ARRAY(SELECT DISTINCT(location) FROM jobs WHERE active = true) AS locations,
            ARRAY(SELECT DISTINCT(country) FROM jobs WHERE country IS NOT NULL AND active = true) AS countries,
            ARRAY(SELECT DISTINCT(seniority) FROM jobs WHERE seniority IS NOT NULL AND active = true) AS seniorities,
            ARRAY(SELECT DISTINCT(UNNEST(tags)) FROM jobs WHERE active = true) AS tags,
            MAX(salaryHigh), false AS remote from jobs WHERE active = true
    """
      .query[JobFilter]
      .option
      .transact(xa)
      .map(_.getOrElse(JobFilter()))

object LiveJobs:

  def apply[F[_] : MonadCancelThrow : Logger](xa: Transactor[F]): F[LiveJobs[F]] = new LiveJobs[F](xa).pure[F]
