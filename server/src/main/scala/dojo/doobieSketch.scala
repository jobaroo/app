package dojo

import cats.effect.kernel.{MonadCancelThrow, Resource}
import cats.effect.{IO, IOApp}
import doobie.hikari.HikariTransactor
import doobie.util.ExecutionContexts
import doobie.util.transactor.Transactor

object doobieSketch extends IOApp.Simple:

  final case class Student(id: Int, name: String)

  val xa: Transactor[IO] = Transactor.fromDriverManager[IO](
    driver = "org.postgresql.Driver",
    url = "jdbc:postgresql:demo",
    user = "docker",
    pass = "docker"
  )

  import doobie.implicits.*

  def findAllStudentNames: IO[List[String]] =
    sql"""select name from students""".query[String]
      .to[List]
      .transact(xa)

  def insertStudent(id: Int, name: String): IO[Int] =
    sql"""insert into students(id, name) values ($id, $name)"""
      .update
      .run
      .transact(xa)

  def findStudentsByInitial(letter: String): IO[List[Student]] =
    sql"""select id, name from students where left(name, 1) = $letter"""
      .query[Student]
      .to[List]
      .transact(xa)

  trait Students[F[_]]:

    def findById(id: Int): F[Option[Student]]
    def findAll: F[List[Student]]
    def add(name: String): F[Int]

  object Students:

    def make[F[_]: MonadCancelThrow](transactor: Transactor[F]): Students[F] = new Students[F]:

      override def add(name: String): F[Int] =
        sql"""insert into students(name) values ($name)""".update.withUniqueGeneratedKeys[Int]("id").transact(transactor)

      override def findById(id: Int): F[Option[Student]] =
        sql"""select id, name from students where id = $id""".query[Student].option.transact(transactor)

      override def findAll: F[List[Student]] =
        sql"""select id, name from students""".query[Student].to[List].transact(transactor)

  val postgresResource: Resource[IO, HikariTransactor[IO]] =
    for
      ec <- ExecutionContexts.fixedThreadPool[IO](16)
      xa <- HikariTransactor.newHikariTransactor[IO](
              driverClassName = "org.postgresql.Driver",
              url = "jdbc:postgresql:demo",
              user = "docker",
              pass = "docker",
              connectEC = ec
            )
    yield xa

  override def run: IO[Unit] = postgresResource.use { xa =>
    val studentsRepo = Students.make(xa)
    for
      id     <- studentsRepo.add("daniel")
      daniel <- studentsRepo.findById(id)
      _      <- IO.println(s"inserted record: $daniel")
    yield ()
  }
