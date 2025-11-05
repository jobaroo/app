package com.jobaroo.domain

object pagination:

  final case class Pagination(limit: Int, offset: Int)

  object Pagination:

    // TODO: make parameters configurable
    def apply(limit: Option[Int], offset: Option[Int]): Pagination =
      new Pagination(limit.getOrElse(20), offset.getOrElse(0))
