import java.nio.charset.StandardCharsets
import com.diffplug.gradle.spotless.FormatExtension
import com.diffplug.spotless.LineEnding

plugins {
    `java-library`
    id("com.diffplug.spotless")
}

val javaVersion = JavaVersion.VERSION_17
java {
    toolchain { languageVersion.set(JavaLanguageVersion.of(javaVersion.toString())) }
    sourceCompatibility = javaVersion
    targetCompatibility = javaVersion
    withSourcesJar()
}
tasks {
    withType<JavaCompile> {
        options.encoding = "UTF-8"
        sourceCompatibility = javaVersion.toString()
        targetCompatibility = javaVersion.toString()
        options.release.set(javaVersion.toString().toInt())
    }
    withType<org.jetbrains.kotlin.gradle.tasks.KotlinCompile> {
        kotlinOptions { jvmTarget = javaVersion.toString() }
    }
}

spotless {
    encoding = StandardCharsets.UTF_8
    lineEndings = LineEnding.UNIX

    fun FormatExtension.defaults() {
        endWithNewline()
        trimTrailingWhitespace()
        indentWithSpaces(4)
    }

    java {
        defaults()
        removeUnusedImports()
    }

    val ktlintConfig = mapOf(
        "ktlint_standard_no-wildcard-imports" to "disabled",
    )

    kotlinGradle {
        defaults()
        ktlint().editorConfigOverride(ktlintConfig)
    }

    kotlin {
        defaults()
        ktlint().editorConfigOverride(ktlintConfig)
    }
}