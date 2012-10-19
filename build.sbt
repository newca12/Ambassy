name := "Ambassy"

organization := "org.edla"

version := "0.1-SNAPSHOT"

scalaVersion := "2.10.0-RC1"

scalacOptions ++= Seq("-unchecked", "-deprecation", "-feature", "-optimize")

scalacOptions in (Compile, doc) ++= Seq("-diagrams","-implicits")

resolvers += "spray repo" at "http://repo.spray.io"

 // "com.typesafe.akka" % "akka-actor" % "2.0.3",
 // "com.typesafe.akka" % "akka-slf4j" % "2.0.3",
 // "ch.qos.logback" % "logback-classic" % "1.0.6",
 // "org.clapper" %% "grizzled-slf4j" % "0.6.9",
 // "com.typesafe" % "config" % "0.6.0" */
libraryDependencies ++= Seq(
  "io.spray" % "spray-can" % "1.1-M5-SNAPSHOT",
  "com.typesafe.akka" % "akka-actor_2.10.0-RC1" % "2.1.0-RC1",
  "com.typesafe.akka" % "akka-slf4j_2.10.0-RC1" % "2.1.0-RC1",
  "com.typesafe.akka" % "akka-testkit_2.10.0-RC1" % "2.1.0-RC1",
  "ch.qos.logback" % "logback-classic" % "1.0.6"
)

// Uncomment the following line to use one-jar (https://github.com/sbt/sbt-onejar)
//seq(com.github.retronym.SbtOneJar.oneJarSettings: _*)

licenses := Seq("GNU GPL v3" -> url("http://www.gnu.org/licenses/gpl.html"))

homepage := Some(url("http://github.com/newca12/ambassy"))


publishMavenStyle := true

publishTo <<= version { (v: String) =>
  val nexus = "https://oss.sonatype.org/"
  if (v.trim.endsWith("SNAPSHOT")) 
    Some("snapshots" at nexus + "content/repositories/snapshots") 
  else
    Some("releases"  at nexus + "service/local/staging/deploy/maven2")
}

publishArtifact in Test := false

pomIncludeRepository := { _ => false }

pomExtra := (
  <!-- repositories not handled yet by sbt make-pom so added manually -->
  <repositories>
    <repository>
      <id>spray</id>
      <name>spray-can</name>
      <url>http://repo.spray.io</url>
    </repository>
    <repository>
      <id>typesafe</id>
      <name>Typesafe Repository</name>
      <url>http://repo.typesafe.com/typesafe/releases/</url>
    </repository>    
  </repositories>
  <scm>
    <url>git@github.com:newca12/ambassy.git</url>
    <connection>scm:git:git@github.com:newca12/ambassy.git</connection>
  </scm>
  <developers>
    <developer>
      <id>newca12</id>
      <name>Olivier ROLAND</name>
      <url>http://www.edla.org</url>
    </developer>
  </developers>
  <contributors>
  </contributors>
  	<properties>
		<encoding>UTF-8</encoding>
	</properties>
  	<build>
  	<!-- source and test directories not handled yet by sbt make-pom so added manually -->
  	<sourceDirectory>src/main/scala</sourceDirectory>
  	<testSourceDirectory>src/test/scala</testSourceDirectory>
    <plugins>
			<plugin>
				<groupId>net.alchim31.maven</groupId>
				<artifactId>scala-maven-plugin</artifactId>
				<version>3.1.0</version>
				<configuration>
                	<args>
                    	<arg>-diagrams</arg>
                    	<arg>-implicits</arg>
                	</args>
                </configuration>
				<executions>
					<execution>
						<goals>
							<goal>compile</goal>
							<goal>testCompile</goal>
						</goals>
					</execution>
				</executions>
			</plugin>
		</plugins>
	</build>	
	<reporting>
		<plugins>
			<plugin>
				<groupId>net.alchim31.maven</groupId>
				<artifactId>scala-maven-plugin</artifactId>
				<version>3.1.0</version>
			</plugin>
		</plugins>
	</reporting>
)
