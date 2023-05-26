import org.gradle.api.publish.maven.MavenPublication
import org.gradle.kotlin.dsl.get
import org.gradle.kotlin.dsl.`maven-publish`
import org.gradle.kotlin.dsl.repositories
import site.siredvin.peripheralium.gradle.ConfigureProject
import site.siredvin.peripheralium.gradle.mavenDependencies

plugins {
    `java-library`
    `maven-publish`
}


publishing {
    publications {
        register<MavenPublication>("maven") {
            artifactId = base.archivesName.get()
            from(components["java"])
        }
    }

    repositories {
        mavenLocal()
    }
}
