package com.jobaroo.common

import tyrian.*
import tyrian.http.*
import io.circe.Encoder
import io.circe.syntax.*
import cats.effect.IO

trait Endpoint[M](location: String, method: Method, onSuccess: Response => M, onError: HttpError => M):

  def call[A: Encoder](payload: A): Cmd[IO, M] =
    Http.send(
      Request(
        url = location,
        method = method,
        headers = Nil,
        body = Body.json(payload.asJson.toString),
        timeout = Request.DefaultTimeOut,
        withCredentials = false
      ),
      Decoder[M](onError = onError(_), onResponse = onSuccess(_))
    )
