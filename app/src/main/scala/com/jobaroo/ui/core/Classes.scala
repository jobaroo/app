package com.jobaroo.ui.core

import cats.Eq
import cats.Monoid
import cats.Show
import cats.syntax.all.*
import scala.quoted.*

opaque type Css = String

object Css:

  val empty: Css = ""

  inline def atom(inline value: String): Css =
    ${ literalImpl('value, allowWhitespace = false) }

  inline def literal(inline value: String): Css =
    ${ literalImpl('value, allowWhitespace = true) }

  def fromString(value: String): UiValidated[Css] =
    normalize(value, allowWhitespace = true).map(_.asInstanceOf[Css])

  def fromTokens(values: Iterable[String]): UiValidated[Css] =
    values.toList.traverse(value => normalize(value, allowWhitespace = false)).map(_.mkString(" ").asInstanceOf[Css])

  def fromOption(value: Option[String]): UiValidated[Css] =
    value.fold(empty.validNec[String])(fromString)

  def of(values: Css*): Css =
    values.foldLeft(empty)(_ |+| _)

  given Eq[Css]   = Eq.fromUniversalEquals
  given Show[Css] = Show.show(_.asInstanceOf[String])

  given Monoid[Css] with

    override def empty: Css = Css.empty

    override def combine(x: Css, y: Css): Css =
      (x.render, y.render) match
        case (empty, right) => right.asInstanceOf[Css]
        case (left, empty)  => left.asInstanceOf[Css]
        case (left, right)  =>
          s"$left $right".asInstanceOf[Css]

  extension (css: Css)

    def render: String    = css
    def nonEmpty: Boolean = css.asInstanceOf[String].length > 0
    def isEmpty: Boolean  = css.asInstanceOf[String].length == 0

  private def normalize(value: String, allowWhitespace: Boolean): UiValidated[String] =
    val normalized = value.trim
    if normalized.isEmpty then "css value must not be empty".invalidNec
    else if !allowWhitespace && normalized.exists(_.isWhitespace) then
      "css token must not contain whitespace".invalidNec
    else
      normalized
        .split("\\s+")
        .toList
        .filter(_.nonEmpty)
        .mkString(" ")
        .validNec

  private def literalImpl(valueExpr: Expr[String], allowWhitespace: Boolean)(using Quotes): Expr[Css] =
    val value      = valueExpr.valueOrAbort
    val normalized =
      normalize(value, allowWhitespace).fold(
        errors => quotes.reflect.report.errorAndAbort(errors.toNonEmptyList.toList.mkString(", ")),
        identity
      )
    Expr(normalized).asExprOf[Css]
