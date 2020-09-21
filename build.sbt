lazy val root = project.in(file("."))
  .settings(
    skip in publish := true
  )
  .aggregate(moduleAApi, moduleB, moduleA)

val moduleAApi =
  project
    .in(file("module-a-api"))
    .enablePlugins(Fs2Grpc)

lazy val moduleB =
  project
    .in(file("module-b"))
    .settings(
      libraryDependencies ++= List(
        "io.grpc" % "grpc-netty" % "1.11.0"
      )
    )
    .dependsOn(moduleAApi)

lazy val moduleA =
  project
    .in(file("module-a"))
    .settings(
      libraryDependencies ++= List(
        "io.grpc" % "grpc-netty" % "1.11.0",
        "io.grpc" % "grpc-services" % "1.11.0"
      )
    )
    .dependsOn(moduleAApi)
