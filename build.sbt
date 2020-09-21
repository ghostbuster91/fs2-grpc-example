lazy val root = project.in(file("."))
  .settings(
    skip in publish := true
  )
  .aggregate(moduleAApi, moduleB, moduleA)

val moduleAApi =
  project
    .in(file("module-a-api"))
    .enablePlugins(Fs2Grpc)
    .settings(
      libraryDependencies ++= Seq(
        "com.thesamet.scalapb.common-protos" %% "proto-google-common-protos-scalapb_0.10" % "1.18.0-0" % "protobuf",
        "com.thesamet.scalapb.common-protos" %% "proto-google-common-protos-scalapb_0.10" % "1.18.0-0"
      ),
      scalapbCodeGeneratorOptions += CodeGeneratorOption.FlatPackage,
      // Generate scala classes for all protos under "google/type"
      PB.protoSources in Compile += PB.externalIncludePath.value / "google" / "type",
    )

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
