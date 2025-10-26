package com.jobaroo.core

import cats.effect.MonadCancelThrow
import cats.*
import cats.syntax.all.*
import cats.implicits.*
import org.typelevel.log4cats.*
import com.jobaroo.config.EmailServiceConfig
import cats.effect.kernel.Resource
import java.util.Properties

trait Emails[F[_]]:

  def send(to: String, subject: String, content: String): F[Unit]
  def sendPasswordRecovery(to: String, token: String): F[Unit]

final class LiveEmails[F[_] : MonadCancelThrow : Logger] private (emailServiceConfig: EmailServiceConfig)
  extends Emails[F]:

  override def send(to: String, subject: String, content: String): F[Unit] =
    val messageResource =
      for
        props   <- propsResource
        auth    <- authenticatorResource
        session <- createSessionResource(props, auth)
        message <- createMessageResource(session)("dev.leowajda@tuta.io", to, subject, content)
      yield message

    messageResource.use(msg => javax.mail.Transport.send(msg).pure[F])

  override def sendPasswordRecovery(to: String, token: String): F[Unit] =
    val subject = "Jobaroo: Password Recovery"
    val content = s"""
      <div style="
        border: 1px solid black;
        padding: 20px;
        font-family: sans-serif;
        line-height: 2;
        font-size: 20px;
      ">

      <h1>$subject</h1>
      <p>Your password recovery token is: $token</p>

      <p>
        Click <a href="${emailServiceConfig.frontendUrl}/login">here</a> to get back to the application.
      </p>

    </div>
    """
    send(to, subject, content)

  private val propsResource: Resource[F, Properties] =
    val props = new Properties()
    props.put("mail.smtp.auth", true)
    props.put("mail.smtp.starttls.enable", true)
    props.put("mail.smtp.host", emailServiceConfig.host)
    props.put("mail.smtp.port", emailServiceConfig.port)
    props.put("mail.smtp.ssl.trust", emailServiceConfig.host)
    Resource.pure(props)

  private val authenticatorResource: Resource[F, javax.mail.Authenticator] =
    import javax.mail.*
    val authenticator = new Authenticator:

      override protected def getPasswordAuthentication(): PasswordAuthentication =
        new PasswordAuthentication(emailServiceConfig.user, emailServiceConfig.password)

    Resource.pure(authenticator)

  private def createSessionResource(
    props: Properties,
    authenticator: javax.mail.Authenticator
  ): Resource[F, javax.mail.Session] = Resource.pure(javax.mail.Session.getInstance(props, authenticator))

  private def createMessageResource(session: javax.mail.Session)(
    from: String,
    to: String,
    subject: String,
    content: String
  ): Resource[F, javax.mail.internet.MimeMessage] =
    import javax.mail.*
    import javax.mail.internet.*

    val message = MimeMessage(session)
    message.setFrom(from)
    message.setRecipients(Message.RecipientType.TO, to)
    message.setSubject(subject)
    message.setContent(content, "text/html; charset=utf-8")
    Resource.pure(message)

object LiveEmails:

  def apply[F[_] : MonadCancelThrow : Logger](emailServiceConfig: EmailServiceConfig): F[LiveEmails[F]] =
    new LiveEmails[F](emailServiceConfig).pure[F]
