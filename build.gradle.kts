plugins {
    id("org.jetbrains.intellij") version "0.7.2"
    java
    kotlin("jvm") version "1.4.10"
}

group = "io.github.cdgeass"
version = "1.3.0"

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
    type = "IU"
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
        1.3.0</br>
        <ul>
            <li>add MyBatis Generator</li>
        </ul>
        """
    )
}
tasks.publishPlugin {
    channels("stable")
    token(System.getenv("ORG_GRADLE_PROJECT_intellijPublishToken"))
}
