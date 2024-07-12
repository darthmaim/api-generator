/*
 * Copyright (c) 2019-2024 Leon Linhart
 *
 * Permission is hereby granted, free of charge, to any person obtaining a copy
 * of this software and associated documentation files (the "Software"), to deal
 * in the Software without restriction, including without limitation the rights
 * to use, copy, modify, merge, publish, distribute, sublicense, and/or sell
 * copies of the Software, and to permit persons to whom the Software is
 * furnished to do so, subject to the following conditions:
 *
 * The above copyright notice and this permission notice shall be included in
 * all copies or substantial portions of the Software.
 *
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
 * IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
 * FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE
 * AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
 * LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
 * OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN THE
 * SOFTWARE.
 */
import org.jetbrains.kotlin.gradle.dsl.JvmTarget
import org.jetbrains.kotlin.gradle.dsl.KotlinVersion
import org.jetbrains.kotlin.gradle.tasks.*

plugins {
    alias(buildDeps.plugins.binary.compatibility.validator)
    alias(buildDeps.plugins.dokkatoo.html)
    alias(buildDeps.plugins.dokkatoo.javadoc)
    alias(buildDeps.plugins.gradle.toolchain.switches)
    alias(buildDeps.plugins.kotlin.jvm)
    alias(buildDeps.plugins.kotlin.plugin.serialization)
    id("com.gw2tb.maven-publish-conventions")
}

java {
    toolchain {
        languageVersion = JavaLanguageVersion.of(22)
    }

    withJavadocJar()
    withSourcesJar()
}

kotlin {
    explicitApi()

    compilerOptions {
        apiVersion = KotlinVersion.KOTLIN_1_8
        languageVersion = KotlinVersion.KOTLIN_1_8

        jvmTarget = JvmTarget.JVM_1_8

        freeCompilerArgs.add("-Xjdk-release=1.8")
    }
}

dokkatoo {
    dokkaGeneratorIsolation = ProcessIsolation {
        maxHeapSize = "4G"
    }

    dokkatooSourceSets.configureEach {
        reportUndocumented = true
        skipEmptyPackages = true
        jdkVersion = 8

        val localKotlinSourceDir = layout.projectDirectory.dir("src/main/kotlin")
        val version = project.version

        sourceLink {
            localDirectory = localKotlinSourceDir

            remoteUrl("https://github.com/GW2ToolBelt/api-generator/tree/v${version}/src/main/kotlin")
            remoteLineSuffix = "#L"
        }
    }

    dokkatooPublications.configureEach {
        failOnWarning = true
    }

    versions {
        jetbrainsDokka = buildDeps.versions.dokka
    }
}

tasks {
    withType<JavaCompile>().configureEach {
        options.release = 8
    }

    withType<Test>().configureEach {
        useJUnitPlatform()

        testLogging {
            showStandardStreams = true
        }
    }

    withType<Jar>().configureEach {
        isPreserveFileTimestamps = false
        isReproducibleFileOrder = true

        includeEmptyDirs = false
    }

    named<Jar>("javadocJar") {
        from(dokkatooGeneratePublicationJavadoc.get().outputs)
    }

    dokkatooGeneratePublicationHtml {
        outputDirectory = layout.projectDirectory.dir("docs/site/api")
    }
}

publishing {
    publications {
        register<MavenPublication>("mavenJava") {
            from(components["java"])
        }
    }
}

dependencies {
    api(kotlin("stdlib"))
    api(libs.kotlinx.serialization.json)

    testImplementation(buildDeps.junit.jupiter.api)
    testRuntimeOnly(buildDeps.junit.jupiter.engine)
}