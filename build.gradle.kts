import java.util.function.Consumer

plugins {
    java
    id("peripheralium.root")
}

val setupSubproject: Consumer<Project> by extra

subprojects {
    setupSubproject.accept(this)
}

repositories {
    mavenCentral()
}