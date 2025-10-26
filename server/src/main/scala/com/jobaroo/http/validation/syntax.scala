package com.jobaroo.http.validation

import io.circe.generic.auto.*
import org.http4s.circe.CirceEntityCodec.*
import org.http4s.*
import org.http4s.dsl.*
import org.http4s.implicits.*
import cats.effect.*
import cats.effect.implicits.*
import com.jobaroo.http.validation.validators.*
import cats.*
import cats.data.Validated.{Invalid, Valid}
import cats.syntax.all.*
import org.typelevel.log4cats.Logger
import com.jobaroo.logging.syntax.*
import com.jobaroo.http.response.*

object syntax:

  trait Http4sValidationDsl[F[_] : MonadThrow : Logger] extends Http4sDsl[F]:

    extension (req: Request[F])

      def validate[A: Validator](serverLogic: A => F[Response[F]])(using EntityDecoder[F, A]): F[Response[F]] =
        req
          .as[A]
          .logError(exception => s"Parsing payload failed: $exception")
          .map(Validator[A].validate)
          .flatMap {
            case Valid(entity)   => serverLogic(entity)
            case Invalid(errors) => BadRequest(FailureResponse(errors.toList.map(_.errorMessage).mkString(",")))
          }
