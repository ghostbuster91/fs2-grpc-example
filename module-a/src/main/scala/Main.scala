import cats.effect.{ExitCode, IO, IOApp}
import com.example.protos._
import fs2._
import io.grpc._
import org.lyranthe.fs2_grpc.java_runtime.implicits._

import scala.concurrent.ExecutionContext.Implicits.global

class ExampleImplementation extends GreeterFs2Grpc[IO, Metadata] {
  override def sayHello(request: HelloRequest,
                        clientHeaders: Metadata): IO[HelloReply] = {
    IO(HelloReply("Request name is: " + request.name))
  }

  override def sayHelloStream(
      request: Stream[IO, HelloRequest],
      clientHeaders: Metadata): Stream[IO, HelloReply] = {
    request.evalMap(req => sayHello(req, clientHeaders))
  }
}

object Main extends IOApp {
  val helloService: ServerServiceDefinition =
    GreeterFs2Grpc.bindService(new ExampleImplementation)

  override def run(args: List[String]): IO[ExitCode] = {
    ServerBuilder
      .forPort(9999)
      .addService(helloService)
      .stream[IO]
      .evalMap(server => IO(server.start()))
      .evalMap(_ => IO.never)
      .compile
      .drain
      .as(ExitCode.Success)
  }
}
