plugins {
    id("org.jetbrains.intellij") version "0.7.2"
    java
    kotlin("jvm") version "1.4.10"
}

group = "io.github.cdgeass"
version = "2.0.0"

repositories {
    mavenCentral()
}

dependencies {
    testImplementation("junit", "junit", "4.12")
    implementation("org.mybatis.generator:mybatis-generator-core:1.4.0")
    implementation("org.jetbrains.kotlin:kotlin-reflect")

    compileOnly("org.projectlombok:lombok:1.18.12")
    annotationProcessor("org.projectlombok:lombok:1.18.12")
}

// See https://github.com/JetBrains/gradle-intellij-plugin/
intellij {
    version = "IU-LATEST-EAP-SNAPSHOT"
    setPlugins("java", "com.intellij.database")
}
configure<JavaPluginConvention> {
    sourceCompatibility = JavaVersion.VERSION_11
}
tasks.withType<org.jetbrains.kotlin.gradle.tasks.KotlinCompile>().configureEach {
    kotlinOptions {
        jvmTarget = "11"
    }
}
tasks.getByName<org.jetbrains.intellij.tasks.PatchPluginXmlTask>("patchPluginXml") {
    sinceBuild(202)
    changeNotes(
        """
        2.0.0</br>
        <ul>
            <li>rewrite with Kotlin</li>
            <li>add localization support</li>
            <li>improve log formatter ui</li>
            <li>improve xml code completion and navigation</li>
        </ul>
        <ul>
            <li>使用Kotlin重写</li>
            <li>添加本地化</li>
            <li>改进日志格式化ui</li>
            <li>改进Xml代码补全和跳转</li>
        </ul>
        """
    )
}
tasks.publishPlugin {
    channels("stable")
    token(System.getenv("ORG_GRADLE_PROJECT_intellijPublishToken"))
}
