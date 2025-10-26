package dojo

import cats.effect.*
import com.jobaroo.core.*
import com.jobaroo.config.*
import org.typelevel.log4cats.slf4j.Slf4jLogger
import org.typelevel.log4cats.Logger
/*

Host 	smtp.ethereal.email
Port 	587
Security 	STARTTLS
Username 	orlando75@ethereal.email
Password 	8nEeJR18AArXG4ayQU

*/
object emailSketch extends IOApp.Simple {
  
  given Logger[IO] = Slf4jLogger.getLogger[IO]
   
  override def run: IO[Unit] =
    for
      emails <- LiveEmails[IO](EmailServiceConfig(host = "smtp.ethereal.email", port = 587, user = "orlando75@ethereal.email", password = "8nEeJR18AArXG4ayQU", frontendUrl = "https://google.com"))
      _ <- emails.sendPasswordRecovery("someone@gmail.com", "very_cool_token")
    yield ()
}
