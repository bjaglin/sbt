ThisBuild / scalaVersion := "2.12.12"
ThisBuild / semanticdbEnabled := true
ThisBuild / semanticdbIncludeInJar := true

// see https://github.com/sbt/sbt/issues/5886
lazy val check = taskKey[Unit]("Checks that scalacOptions have the same number of parameters across configurations")
lazy val anyConfigInThisProject = ScopeFilter(configurations = inAnyConfiguration)

lazy val Custom = config("custom").extend(Compile)
lazy val SystemTest = config("st").extend(IntegrationTest)

lazy val root = (project in file("."))
  .configs(IntegrationTest, Custom, SystemTest)
  .settings(
    Defaults.itSettings,
    inConfig(Custom)(Defaults.configSettings),
    inConfig(SystemTest)(Defaults.testSettings),
    check := {
      val scalacOptionsCountsAcrossConfigs = scalacOptions.?.all(anyConfigInThisProject)
        .value
        .map(_.toSeq.flatten.size)
        .filterNot(_ == 0)
        .distinct
      assert(
        scalacOptionsCountsAcrossConfigs.size == 1,
        s"Configurations expected to have the same number of scalacOptions but found different numbers: $scalacOptionsCountsAcrossConfigs"
      )
    }

  )
