plugins {
    kotlin("jvm") version "2.2.21"
    id("org.jetbrains.intellij.platform") version "2.7.1"
    java
}

group = "de.jflex.ide"
version = "1.0.0"

repositories {
    mavenCentral()
    intellijPlatform {
        defaultRepositories()
    }
}

tasks {
    withType<JavaCompile> {
        sourceCompatibility = "21"
        targetCompatibility = "21"
    }
}

kotlin {
    compilerOptions {
        jvmTarget.set(org.jetbrains.kotlin.gradle.dsl.JvmTarget.JVM_21)
    }
}

dependencies {
    intellijPlatform {
        create("IC", "2025.1.4.1")
        testFramework(org.jetbrains.intellij.platform.gradle.TestFrameworkType.Platform)

        bundledPlugin("com.intellij.java")
    }
}


intellijPlatform {
    pluginConfiguration {
        ideaVersion {
            sinceBuild = "251"
        }

        changeNotes = """
        v1.8
        <ul>
        <li>
        Upgraded to IntelliJ Platform 2024.3
        </li>
        <li>
        Kotlin and Java 17 support
        </li>
        <ul>
        
        v1.7
        <ul>
        <li>Update for JFlex 1.7.0.
        </ul>

        v1.6.1
        <ul>
        <li>Updated for JFlex 1.6.1
        <li>.jflex extension added to JFlex file type. (Steve Rowe)
        </ul>
        """.trimIndent()
    }
}


kotlin {
    compilerOptions {
        jvmTarget.set(org.jetbrains.kotlin.gradle.dsl.JvmTarget.JVM_21)
    }
}


tasks.processResources {
    duplicatesStrategy = DuplicatesStrategy.EXCLUDE
}