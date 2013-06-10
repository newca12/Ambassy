import AssemblyKeys._ // put this at the top of the file

assemblySettings

name := "Ambassy"

organization := "org.edla"

version := "0.1-SNAPSHOT"

scalaVersion := "2.10.1"

scalacOptions ++= Seq("-unchecked", "-deprecation", "-feature", "-optimize")

scalacOptions in (Compile, doc) ++= Seq("-diagrams","-implicits")

resolvers += "Typesafe Repository" at "http://repo.typesafe.com/typesafe/releases/"

resolvers += "spray repo" at "http://repo.spray.io"

resolvers += "spray nightly repo" at "http://nightlies.spray.io"

resolvers += "Sonatype OSS Snapshots" at "http://oss.sonatype.org/content/repositories/snapshots"

libraryDependencies ++= Seq(
  "net.debasishg" %% "redisclient" % "2.10",
  "io.spray" % "spray-can" % "1.2-20130605",
  "io.spray" % "spray-io" % "1.2-20130605",
  "io.spray" % "spray-routing" % "1.2-20130605",
  "io.spray" % "spray-caching" % "1.2-20130605",
  "io.spray" %%  "spray-json" % "1.2.5",
  "com.typesafe.akka" %% "akka-actor" % "2.2.0-RC1",
  "com.typesafe.akka" %% "akka-slf4j" % "2.2.0-RC1",
  "com.typesafe.akka" %% "akka-testkit" % "2.2.0-RC1",
  "ch.qos.logback" % "logback-classic" % "1.0.7",
  "io.spray" % "spray-testkit" % "1.2-20130605" % "test",
  "org.scalatest" %% "scalatest" % "1.9.1" % "test",
  "org.specs2" %% "specs2" % "1.14" % "test"
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

pomIncludeRepository := { _ => true }

pomExtra := (
  <!-- repositories not handled yet by sbt make-pom so added manually 
       pluginRepository needed for add-source goal
  -->
  <pluginRepositories>
	<pluginRepository>
		<id>el4.elca-services.ch</id>
        <name>el4</name>
        <url>http://el4.elca-services.ch/el4j/maven2repository</url>
    </pluginRepository>
  </pluginRepositories>  
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
				<executions>
					<execution>
						<goals>
							<goal>add-source</goal>
							<goal>compile</goal>
							<goal>testCompile</goal>
						</goals>
					</execution>
				</executions>
			</plugin>
            <plugin>
				<artifactId>maven-surefire-plugin</artifactId>
				<version>2.12.4</version>
				<configuration>
					<!-- surefire is disabled for scalatest, specs2 probably need it -->
				    <skipTests>true</skipTests>
					<useFile>false</useFile>
					<!--
					<includes>
						<include>**/?Test.scala</include>
                        <include>**/?Suite.scala</include>
                        <include>**/?Spec.scala</include>
					</includes> -->
				</configuration>
			</plugin>
			<!-- enable scalatest -->
			<plugin>
				<groupId>org.scalatest</groupId>
				<artifactId>scalatest-maven-plugin</artifactId>
				<version>1.0-M2</version>
				<configuration>
					<!-- <reportsDirectory>${project.build.directory}/surefire-reports</reportsDirectory> -->
					<junitxml>.</junitxml>
					<filereports>WDF TestSuite.txt</filereports>
				</configuration>
				<executions>
					<execution>
						<id>test</id>
						<goals>
							<goal>test</goal>
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
