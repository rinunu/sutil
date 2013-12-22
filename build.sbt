releaseSettings

name := "sutil"

scalaVersion := "2.10.2"

organization := "nu.rinu"

libraryDependencies ++= Seq(
  "com.jcraft" % "jsch" % "0.1.50",
  "org.specs2" %% "specs2" % "1.13" % "test",
  "com.typesafe" %% "scalalogging-slf4j" % "1.0.1"
)

publishTo <<= version { (v: String) =>
  val nexus = "https://oss.sonatype.org/"
  if (v.trim.endsWith("SNAPSHOT"))
    Some("snapshots" at nexus + "content/repositories/snapshots")
  else
    Some("releases" at nexus + "service/local/staging/deploy/maven2")
}

licenses := Seq("BSD-style" -> url("http://www.opensource.org/licenses/bsd-license.php"))

homepage := Some(url("https://github.com/rinunu/sutil"))

publishArtifact in Test := false

pomExtra :=
  <parent>
    <groupId>org.sonatype.oss</groupId>
    <artifactId>oss-parent</artifactId>
    <version>7</version>
  </parent>
    <scm>
      <connection>scm:git:git@github.com:rinunu/sutil.git</connection>
      <developerConnection>scm:git:git@github.com:rinunu/sutil.git</developerConnection>
      <url>git@github.com:rinunu/sutil.git</url>
    </scm>
    <developers>
      <developer>
        <id>rinunu</id>
        <name>Rintaro Tsuchihashi</name>
        <url>https://github.com/rinunu</url>
      </developer>
    </developers>


