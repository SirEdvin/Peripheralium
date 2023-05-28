import org.gradle.api.publish.maven.MavenPublication

plugins {
    `java-library`
    `maven-publish`
}

class PublishingShakingExtension(private val targetProject: Project) {
    fun shake() {
        targetProject.publishing {
            publications {
                register<MavenPublication>("maven") {
                    artifactId = targetProject.base.archivesName.get()
                    from(components["java"])
                }
            }

            repositories {
                val modVersion: String by targetProject.extra
                val isUnstable = modVersion.split("-").size > 1
                if (isUnstable) {
                    maven("https://mvn.siredvin.site/snapshots") {
                        name = "SirEdvin"
                        credentials(PasswordCredentials::class)
                    }
                } else {
                    maven("https://mvn.siredvin.site/minecraft") {
                        name = "SirEdvin"
                        credentials(PasswordCredentials::class)
                    }
                }
            }
        }
    }
}

val publishingShaking = PublishingShakingExtension(project)
project.extensions.add("publishingShaking", publishingShaking)
