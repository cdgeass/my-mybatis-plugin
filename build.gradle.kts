plugins {
    id("org.jetbrains.intellij") version "0.4.18"
    java
}

group = "io.github.cdgeass"
version = "0.0.4-alpha"

repositories {
    mavenCentral()
}

dependencies {
    testCompile("junit", "junit", "4.12")
    implementation("com.google.guava:guava:28.2-jre")
    implementation("com.alibaba:druid:1.1.22")
}

// See https://github.com/JetBrains/gradle-intellij-plugin/
intellij {
    version = "2020.1"
}
configure<JavaPluginConvention> {
    sourceCompatibility = JavaVersion.VERSION_11
}
tasks.getByName<org.jetbrains.intellij.tasks.PatchPluginXmlTask>("patchPluginXml") {
    changeNotes("""
      0.0.4-alpha<br>
      1. fix with multi threads
      2. fix npe when without preparing
      """)
}
tasks.publishPlugin {
    channels("alpha")
    token(System.getenv("ORG_GRADLE_PROJECT_intellijPublishToken"))
}
