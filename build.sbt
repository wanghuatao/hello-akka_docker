lazy val scalaSettings = Seq(
  scalaVersion := "2.12.2",
  scalacOptions ++= Seq(
    "-deprecation",
    "-Ywarn-value-discard",
    "-Xfatal-warnings"
  )
)

lazy val dockerSettings = Seq(
  // https://github.com/marcuslonnberg/sbt-docker
  dockerfile in docker := {
    // The assembly task generates a fat JAR file
    val artifact: File = assembly.value
    // val artifactTargetPath = s"/app/${artifact.name}"
    // use fixed artifact path to avoid fails on version change when using custom docker command
    val artifactTargetPath = s"/app/${name.value}.jar"

    new Dockerfile {
      from("openjdk:8")
      add(artifact, artifactTargetPath)
      expose(8080)
      entryPoint("java")
      cmd("-jar", artifactTargetPath)
    }
  },
  imageNames in docker := Seq(
    ImageName(s"tank/${name.value}:latest"),
    ImageName(
      namespace = Some("tank"),
      repository = name.value,
      tag = Some(version.value)
    )
  )
)

val akkaV = "2.5.2"

lazy val `hello-akka` = (project in file("."))
  .settings(dockerSettings: _*)
  .settings(scalaSettings: _*)
  .settings(
    organization := "yuzhonglele",
    name := "hello-akka",
    version := "0.1.1",
    mainClass in Compile := Some("com.lostintimedev.hello.Boot"),
    libraryDependencies ++= Seq(
      // akka
      "com.typesafe" % "config" % "1.3.1",
      "com.typesafe.akka" %% "akka-actor" % akkaV,
      "com.typesafe.akka" %% "akka-cluster" % akkaV,
      "com.typesafe.akka" %% "akka-cluster-metrics" % akkaV,
      "com.typesafe.akka" %% "akka-cluster-tools" % akkaV
    )
  )
  .enablePlugins(DockerPlugin)
