import scala.Console._
import scalariform.formatter.preferences._
import scala.sys.process.Process
import App.versions._

cancelable in Global := true

lazy val testScalastyle = taskKey[Unit]("testScalaStyle")

lazy val srcScalastyle = taskKey[Unit]("srcScalaStyle")

lazy val scalaFormattingSettings = scalariformPreferences := scalariformPreferences.value
  .setPreference(PlaceScaladocAsterisksBeneathSecondAsterisk, true)
  .setPreference(DanglingCloseParenthesis, Preserve)


lazy val scalaStyleSettings = Seq(
  testScalastyle := scalastyle.in(Test).toTask("").value,
  srcScalastyle := scalastyle.in(Compile).toTask("").value,
  (test in Test) := ((test in Test) dependsOn (testScalastyle, srcScalastyle)).value
)


lazy val testOptionsSettings = Seq(
  (testOptions in Test) += Tests.Argument(TestFrameworks.ScalaTest,
    "-h", (target in Test).value + "/test-reports/html",
    "-f", (target in Test).value + "/test-report.txt"),
  libraryDependencies += "org.pegdown" % "pegdown" % "1.6.0"
)

//ThisBuild / scalacOptions += "-Yrangepos"
//ThisBuild / semanticdbEnabled := true
//ThisBuild / semanticdbVersion := scalafixSemanticdb.revision

lazy val commonSettings = Seq(
  organization := "com.github.inanme",
  version := "1.0",
  scalaVersion := scalaV,
  semanticdbEnabled := true,
  semanticdbVersion := scalafixSemanticdb.revision,
  addCompilerPlugin(scalafixSemanticdb),
  sources in doc in Compile := List(),
  scalacOptions in Test ++= Seq(
    //A -X option suggests permanence, while a -Y could disappear at any time
    "-Yrangepos",
    "-encoding", "UTF-8", // source files are in UTF-8
    "-explaintypes", // Explain type errors in more detail.
    "-deprecation", // warn about use of deprecated APIs
    "-unchecked", // warn about unchecked type parameters
    "-feature", // warn about misused language features
    "-language:postfixOps", // allow higher kinded types without `import scala.language.postfixOps`
    "-language:higherKinds", // allow higher kinded types without `import scala.language.higherKinds`
    "-language:implicitConversions", // Allow definition of implicit functions called views
    "-language:existentials", // Existential types (besides wildcard types) can be written and inferred
    "-language:reflectiveCalls",
    "-Xlint",
    "-Wunused",
  ),
  //You should make sure Xplugin is present
  scalacOptions in Compile ++= Seq(
    //A -X option suggests permanence, while a -Y could disappear at any time
    "-Yrangepos",
    "-encoding", "UTF-8", // source files are in UTF-8
    "-explaintypes", // Explain type errors in more detail.
    "-deprecation", // warn about use of deprecated APIs
    "-unchecked", // warn about unchecked type parameters
    "-feature", // warn about misused language features
    "-language:postfixOps", // allow higher kinded types without `import scala.language.postfixOps`
    "-language:higherKinds", // allow higher kinded types without `import scala.language.higherKinds`
    "-language:implicitConversions", // Allow definition of implicit functions called views
    "-language:existentials", // Existential types (besides wildcard types) can be written and inferred
    "-language:reflectiveCalls",
    "-Xlint",
    "-Wunused"
    //"-Xfatal-warnings", // turn compiler warnings into errors
  ),
  libraryDependencies += "ch.qos.logback" % "logback-classic" % "1.2.3",
  libraryDependencies += "org.codehaus.janino" % "janino" % "3.0.12",
  libraryDependencies += "net.logstash.logback" % "logstash-logback-encoder" % "5.3",    // Newer versions are not compatible. They require Jackson 2.9.8. Take care when upgrading next time :)
  shellPrompt in ThisBuild := {
    state =>
      val sbtMarker = WHITE + "sbt:" + RESET
      val project = CYAN + Project.extract(state).currentRef.project + RESET
      val branch = BLUE + Process("git rev-parse --abbrev-ref HEAD").lineStream.head + RESET
      s"$sbtMarker $project ($branch) > "
  }
) ++ scalaStyleSettings ++ scalaFormattingSettings ++ Revolver.settings

lazy val commonPlugins = Seq(
  SbtNativePackager,
  JavaServerAppPackaging
)

lazy val `service` = project
  .settings(commonSettings: _*)
  .settings(testOptionsSettings: _*)

