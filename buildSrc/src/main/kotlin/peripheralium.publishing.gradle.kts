import org.gradle.api.publish.maven.MavenPublication

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
