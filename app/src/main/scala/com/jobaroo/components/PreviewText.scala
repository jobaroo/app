package com.jobaroo.components

object PreviewText:

  private val inlineNoise = Set('*', '_', '`', '>', '-')

  def fromMarkdown(value: String): String =
    value.linesIterator
      .map(normalizeLine)
      .mkString(" ")
      .replaceAll("\\s+", " ")
      .trim

  def withoutLeadingTitle(value: String, title: String): String =
    val lines                     = value.linesIterator.toList
    val (leadingBlank, remaining) = lines.span(_.trim.isEmpty)

    remaining match
      case first :: tail if matchesHeading(first, title) =>
        (leadingBlank ++ tail.dropWhile(_.trim.isEmpty)).mkString("\n")
      case _ => value

  private def normalizeLine(line: String): String =
    line.trim
      .dropWhile(_ == '#')
      .trim
      .map(ch => if inlineNoise.contains(ch) then ' ' else ch)

  private def matchesHeading(line: String, title: String): Boolean =
    line.trim.startsWith("#") && normalizeLine(line).equalsIgnoreCase(normalizeLine(title))
