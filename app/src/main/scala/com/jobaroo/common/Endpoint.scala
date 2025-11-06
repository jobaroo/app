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
