package com.jobaroo.components

object PreviewText:

  private val inlineNoise = Set('*', '_', '`', '>', '-')

  def fromMarkdown(value: String): String =
    value.linesIterator
      .map(normalizeLine)
      .mkString(" ")
      .replaceAll("\\s+", " ")
      .trim

  private def normalizeLine(line: String): String =
    line.trim
      .dropWhile(_ == '#')
      .trim
      .map(ch => if inlineNoise.contains(ch) then ' ' else ch)
