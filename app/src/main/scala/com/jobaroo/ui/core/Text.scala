package com.jobaroo.ui.core

import cats.Eq
import cats.Show
import cats.syntax.all.*
import scala.quoted.*

opaque type UiText = String

object UiText:

  inline def literal(inline value: String, inline fieldName: String = "text"): UiText =
    ${ textLiteralImpl('value, 'fieldName) }

  def from(value: String, fieldName: String = "text"): UiValidated[UiText] =
    val normalized = value.trim
    if normalized.nonEmpty then (normalized: UiText).validNec
    else s"$fieldName must not be empty".invalidNec

  given Eq[UiText]   = Eq.fromUniversalEquals
  given Show[UiText] = Show.show(identity)

  extension (text: UiText)
    def value: String = text

  private def textLiteralImpl(valueExpr: Expr[String], fieldNameExpr: Expr[String])(using Quotes): Expr[UiText] =
    val fieldName = fieldNameExpr.valueOrAbort
    val value     = valueExpr.valueOrAbort
    from(value, fieldName).fold(
      errors => quotes.reflect.report.errorAndAbort(errors.toNonEmptyList.toList.mkString(", ")),
      text => Expr(text.value).asExprOf[UiText]
    )

opaque type UiId = String

object UiId:

  private val idPattern = "^[A-Za-z][A-Za-z0-9\\-_:]*$".r

  inline def literal(inline value: String): UiId =
    ${ idLiteralImpl('value) }

  def from(value: String): UiValidated[UiId] =
    val normalized = value.trim
    if normalized.isEmpty then "id must not be empty".invalidNec
    else if !idPattern.matches(normalized) then
      "id must start with a letter and contain only letters, numbers, '-', '_', ':'".invalidNec
    else (normalized: UiId).validNec

  def sanitized(value: String): UiId =
    from(value).fold(_ => slug(value), identity)

  def slug(head: String, rest: String*): UiId =
    val segments   = (head +: rest).iterator.map(_.trim.toLowerCase).filter(_.nonEmpty).map(_.replaceAll(
      "[^a-z0-9:_-]+",
      "-"
    )).map(_.stripPrefix("-").stripSuffix("-")).filter(_.nonEmpty).toList
    val joined     = segments.mkString("-")
    val normalized =
      if joined.isEmpty then "ui-generated-id"
      else if joined.headOption.exists(_.isLetter) then joined
      else s"ui-$joined"

    normalized.asInstanceOf[UiId]

  given Eq[UiId]   = Eq.fromUniversalEquals
  given Show[UiId] = Show.show(identity)

  extension (id: UiId)
    def value: String = id

  private def idLiteralImpl(valueExpr: Expr[String])(using Quotes): Expr[UiId] =
    val value = valueExpr.valueOrAbort
    from(value).fold(
      errors => quotes.reflect.report.errorAndAbort(errors.toNonEmptyList.toList.mkString(", ")),
      id => Expr(id.value).asExprOf[UiId]
    )
