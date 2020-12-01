plugins {
    id("org.jetbrains.intellij") version "0.4.20"
    java
    kotlin("jvm") version "1.4.10"
}

group = "io.github.cdgeass"
version = "1.2.8"

repositories {
    mavenCentral()
}

dependencies {
    testImplementation("junit", "junit", "4.12")
    implementation("com.github.jsqlparser:jsqlparser:3.1")
    implementation("org.mybatis.generator:mybatis-generator-core:1.4.0")

    compileOnly("org.projectlombok:lombok:1.18.12")
    annotationProcessor("org.projectlombok:lombok:1.18.12")
}

// See https://github.com/JetBrains/gradle-intellij-plugin/
intellij {
    type = "IU"
    version = "IU-203-EAP-SNAPSHOT"
    setPlugins("java", "com.intellij.database")
}
configure<JavaPluginConvention> {
    sourceCompatibility = JavaVersion.VERSION_11
}
tasks.getByName<org.jetbrains.intellij.tasks.PatchPluginXmlTask>("patchPluginXml") {
    changeNotes("""
    1.2.8</br>
    <ul>
        <li>fix item in <foreach></li>
        <li>fix scale tool window</li>
    </ul>
      """)
}
tasks.publishPlugin {
    channels("stable")
    token(System.getenv("ORG_GRADLE_PROJECT_intellijPublishToken"))
}
