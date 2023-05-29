import java.util.function.BiConsumer

plugins {
    java
    id("site.siredvin.root") version "0.3.8"
}

subprojectShaking {
    withKotlin.set(true)
}

val setupSubproject = subprojectShaking::setupSubproject

subprojects {
    setupSubproject(this)
}

repositories {
    mavenCentral()
}