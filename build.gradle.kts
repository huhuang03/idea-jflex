plugins {
    // 使用最新的 IntelliJ Platform Plugin
    id("org.jetbrains.intellij.platform") version "2.10.5" // 注意这里
    // Kotlin JVM 支持
    kotlin("jvm") version "2.2.21"
    // Java 插件
    java
    // IDEA 插件（可选，用于生成 IDEA 配置）
    idea
}

group = "de.jflex.ide"
version = "1.0.0"

// 仓库
repositories {
    mavenCentral()
    gradlePluginPortal()
    maven("https://www.jetbrains.com/intellij-repository/releases")
}

// Kotlin 编译器版本
kotlin {
    jvmToolchain(17) // 现代 IDEA 插件推荐使用 Java 17
}

// IntelliJ 平台插件配置
intellij {
    // 指定要编译/运行的 IDEA 版本
    version.set("2024.3")
    type.set("IC") // IC = IntelliJ Community, IU = Ultimate
    plugins.set(listOf()) // 依赖的 IntelliJ 插件，例如 "java", "Kotlin" 等
}

// 可选：设置 JVM 编译选项
tasks.withType<JavaCompile> {
    sourceCompatibility = "17"
    targetCompatibility = "17"
    options.encoding = "UTF-8"
}

tasks.withType<org.jetbrains.kotlin.gradle.tasks.KotlinCompile> {
    kotlinOptions.jvmTarget = "17"
}

// 打包插件
tasks {
    patchPluginXml {
        changeNotes.set(
            """
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
        )
    }
}
