import com.moowork.gradle.node.yarn.YarnTask

plugins {
  id("java")
  id("com.moowork.node") version "1.3.1"
}

node {
  version = "12.14.1"
  yarnVersion = "1.22.4"
}

task("run_linters") {
  inputs.files("src")

  dependsOn("yarn_lint", "yarn_prettier", "yarn_htmlhint")
}

task("run_tests", YarnTask::class) {
  inputs.files("src")

  setEnvironment(mapOf(Pair("CI", "true")))
  setYarnCommand("test")
}

task("build_main_app", YarnTask::class) {
  inputs.dir("src")
  inputs.file("package.json")
  inputs.file("yarn.lock")

  outputs.dir("dist/albums")

  setYarnCommand("build")
}

tasks.clean {
  delete("dist")
}

tasks.test {
  dependsOn("run_linters", "run_tests")
}

tasks.jar {
  mustRunAfter("test")
  dependsOn("build_main_app")
  from("dist/albums")
  into("static")
}
