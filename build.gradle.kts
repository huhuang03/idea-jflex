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

// Kotlin 编译器版本
kotlin {
    jvmToolchain(17) // 现代 IDEA 插件推荐使用 Java 17
}

dependencies {
    intellijPlatform {
        create("IC", "2025.1.4.1")
        testFramework(org.jetbrains.intellij.platform.gradle.TestFrameworkType.Platform)

        // Add necessary plugin dependencies for compilation here, example:
        // bundledPlugin("com.intellij.java")
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
