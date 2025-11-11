package com.jobaroo.common

import tyrian.*
import tyrian.http.*
import io.circe.Encoder
import io.circe.syntax.*
import cats.effect.IO
import com.jobaroo.core.Session

trait Endpoint[M](location: String, method: Method, onResponse: Response => M, onError: HttpError => M):

  def call[A: Encoder](payload: A): Cmd[IO, M]           = internalCall(payload, None)
  def callAuthorized[A: Encoder](payload: A): Cmd[IO, M] = internalCall(payload, Session.getUserToken)
  def call(): Cmd[IO, M]                                 = internalCall(None)
  def callAuthorized(): Cmd[IO, M]                       = internalCall(Session.getUserToken)

  private def internalCall[A: Encoder](payload: A, authorization: Option[String]): Cmd[IO, M] =
    Http.send(
      Request(
        url = location,
        method = method,
        headers = authorization.map(Header("Authorization", _)).toList,
        body = Body.json(payload.asJson.toString),
        timeout = Request.DefaultTimeOut,
        withCredentials = false
      ),
      Decoder[M](onError = onError(_), onResponse = onResponse(_))
    )

  private def internalCall(authorization: Option[String]): Cmd[IO, M] =
    Http.send(
      Request(
        url = location,
        method = method,
        headers = authorization.map(Header("Authorization", _)).toList,
        body = Body.Empty,
        timeout = Request.DefaultTimeOut,
        withCredentials = false
      ),
      Decoder[M](onError = onError(_), onResponse = onResponse(_))
    )

object Endpoint:

  import io.circe.parser.*

  def onResponse[A: io.circe.Decoder, Msg](onError: String => Msg, onSuccess: A => Msg): Response => Msg = resp =>
    resp.status match
      case Status(s, _) if s >= 200 && s < 300   =>
        val json = parse(resp.body).flatMap(_.as[A])
        json match
          case Left(error)  => onError(s"Parsing error: $error}")
          case Right(value) => onSuccess(value)
      case Status(s, msg) if s >= 400 && s < 600 => onError(msg)
