package com.jobaroo.core

import cats.*
import cats.syntax.*
import cats.implicits.*
import com.stripe.model.checkout.Session
import com.stripe.param.checkout.SessionCreateParams
import com.jobaroo.logging.syntax.*
import com.stripe.net.Webhook
import org.typelevel.log4cats.Logger
import com.stripe.Stripe as JavaStripe
import com.jobaroo.config.StripeConfig
import scala.util.Try
import scala.jdk.OptionConverters.*

trait Stripe[F[_]]:

  def createCheckoutSession(jobId: String, userEmail: String): F[Option[Session]]
  def handleWebhookEvent[A](payload: String, signature: String, action: String => F[A]): F[Option[A]]

final class LiveStripe[F[_] : MonadThrow : Logger] private (stripeConfig: StripeConfig) extends Stripe[F]:

  JavaStripe.apiKey = stripeConfig.key

  override def createCheckoutSession(jobId: String, userEmail: String): F[Option[Session]] =
    SessionCreateParams.builder()
      .setMode(SessionCreateParams.Mode.PAYMENT)
      .setInvoiceCreation(
        SessionCreateParams.InvoiceCreation.builder()
          .setEnabled(true)
          .build()
      )
      .setPaymentIntentData(
        SessionCreateParams.PaymentIntentData.builder()
          .setReceiptEmail(userEmail)
          .build()
      )
      .setSuccessUrl(s"${stripeConfig.successUrl}/$jobId")
      .setCancelUrl(stripeConfig.cancelUrl)
      .setCustomerEmail(userEmail)
      .setClientReferenceId(jobId)
      .addLineItem(
        SessionCreateParams.LineItem.builder()
          .setQuantity(1L)
          .setPrice(stripeConfig.price)
          .build()
      )
      .build()
      .pure[F]
      .map(builder => Session.create(builder))
      .map(_.some)
      .logError(err => s"Creating checkout session failed: $err")
      .recover(_ => None)

  override def handleWebhookEvent[A](payload: String, signature: String, action: String => F[A]): F[Option[A]] =
    MonadThrow[F].fromTry(Try(Webhook.constructEvent(payload, signature, stripeConfig.webhookSecret)))
      .logError(e => "Stripe security verification failed - possibly fake attempt")
      .flatMap { event =>
        event.getType() match
          case "checkout.session.completed" =>
            Try(event
              .getDataObjectDeserializer()
              .deserializeUnsafe()
              .asInstanceOf[Session]
              .getClientReferenceId())
              .toOption
              .traverse(action)
              .log(
                success = {
                  case None      => s"Event: ${event.getId()} not producing any effect"
                  case Some(res) => s"Event: ${event.getId()} fully paid"
                },
                error = e => s"Webhoook action failed: $e"
              )
          case _                            => None.pure[F]
      }
      .recover(_ => None)

object LiveStripe:

  def apply[F[_] : MonadThrow : Logger](stripeConfig: StripeConfig): F[LiveStripe[F]] =
    new LiveStripe[F](stripeConfig).pure[F]
