package dojo

import cats.{Applicative, Monad}
import cats.effect.{IO, IOApp}
import org.http4s.{Header, HttpRoutes}
import org.http4s.dsl.Http4sDsl
import org.http4s.dsl.impl.{OptionalValidatingQueryParamDecoderMatcher, QueryParamDecoderMatcher}
import org.http4s.ember.server.EmberServerBuilder

case object http4sSketch extends IOApp.Simple:

  type Student = String
  final case class Instructor(firstName: String, lastName: String)
  final case class Course(id: String, title: String, year: Int, students: List[Student], instructorName: String)

  object CourseRepository:

    private val someCourse = Course(
      id = "course-1",
      title = "Math",
      year = 2004,
      students = List("Leo", "Daniel"),
      instructorName = "Martin"
    )

    private val courses: Map[String, Course] = Map(someCourse.id -> someCourse)

    def findCourseById(id: String): Option[Course]         = courses.get(id)
    def findCourseByInstructor(name: String): List[Course] = courses.values.filter(_.instructorName == name).toList

  object InstructorQueryParamMatcher extends QueryParamDecoderMatcher[String]("instructor")
  object YearQueryParamMatcher       extends OptionalValidatingQueryParamDecoderMatcher[Int]("year")

  def healthRoutes[F[_] : Monad]: HttpRoutes[F] =
    val dsl = Http4sDsl[F]
    import dsl.*

    HttpRoutes.of[F] {
      case GET -> Root / "healt" => Ok("all good")
    }

  def courseRoutes[F[_]: Monad]: HttpRoutes[F] =
    val dsl = Http4sDsl[F]
    import dsl.*
    import io.circe.generic.auto.*
    import io.circe.syntax.*
    import org.http4s.circe.*

    HttpRoutes.of[F] {
      case GET -> Root / "courses" :? InstructorQueryParamMatcher(name) +& YearQueryParamMatcher(year) =>
        val courses = CourseRepository.findCourseByInstructor(name)
        year match
          case Some(value) => value.fold(
              _ => BadRequest("param year is invalid"),
              validYear => Ok(courses.filter(_.year == validYear).asJson)
            )
          case None        => Ok(courses.asJson)
      case GET -> Root / "courses" / courseId / "students" =>
        CourseRepository.findCourseById(courseId).map(_.students) match
          case Some(value) => Ok(value.asJson)
          case None => NotFound(s"No course with id: $courseId")
    }

  import cats.syntax.all.*

  def allRoutes[F[_] : Monad] = healthRoutes[F] <+> courseRoutes[F]

  override def run: IO[Unit] =
    EmberServerBuilder
      .default[IO]
      .withHttpApp(allRoutes[IO].orNotFound)
      .build
      .use(_ => IO.println("server is ready") *> IO.never)
