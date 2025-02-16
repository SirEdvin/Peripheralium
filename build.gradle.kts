plugins {
    java
    id("site.siredvin.root") version "0.8.5"
    id("site.siredvin.release") version "0.8.5"
    id("com.github.ben-manes.versions") version "0.51.0"
}

subprojectShaking {
    withKotlin.set(true)
    kotlinVersion.set("2.0.0")
}

val setupSubproject = subprojectShaking::setupSubproject

subprojects {
    setupSubproject(this)
}

githubShaking {
    modBranch.set("1.19")
    shake()
}

repositories {
    mavenCentral()
}