lazy val root = project.in(file("."))
  .settings(
    skip in publish := true
  )
  .aggregate(moduleAApi, moduleB, moduleA)

val moduleAApi =
  project
    .in(file("module-a-api"))
    .settings(
      libraryDependencies ++= Seq(
        "com.thesamet.scalapb.common-protos" %% "proto-google-common-protos-scalapb_0.10" % "1.18.0-0" % "protobuf",
        "com.thesamet.scalapb.common-protos" %% "proto-google-common-protos-scalapb_0.10" % "1.18.0-0"
      )
    )


lazy val moduleB =
  project
    .in(file("module-b"))
    .settings(
      libraryDependencies ++= List(
        "io.grpc" % "grpc-netty" % "1.11.0",
      ),
      (PB.protoSources in Compile) += (baseDirectory in moduleAApi).value / "src" / "main" / "protobuf",
      scalapbCodeGeneratorOptions += CodeGeneratorOption.FlatPackage,
      // Generate scala classes for all protos under "google/type"
      PB.protoSources in Compile += PB.externalIncludePath.value / "google" / "type",
    )
    .dependsOn(moduleAApi)
    .dependsOn(moduleAApi % "protobuf")
    .enablePlugins(Fs2Grpc)

lazy val moduleA =
  project
    .in(file("module-a"))
    .settings(
      libraryDependencies ++= List(
        "io.grpc" % "grpc-netty" % "1.11.0",
        "io.grpc" % "grpc-services" % "1.11.0",
      ),
      (PB.protoSources in Compile) += (baseDirectory in moduleAApi).value / "src" / "main" / "protobuf",
      scalapbCodeGeneratorOptions += CodeGeneratorOption.FlatPackage,
      // Generate scala classes for all protos under "google/type"
      PB.protoSources in Compile += PB.externalIncludePath.value / "google" / "type",
    )
    .dependsOn(moduleAApi)
    .dependsOn(moduleAApi % "protobuf")
    .enablePlugins(Fs2Grpc)

