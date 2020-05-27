plugins {
    id("org.jetbrains.intellij") version "0.4.20"
    java
}

group = "io.github.cdgeass"
version = "1.1.3"

repositories {
    mavenCentral()
}

dependencies {
    testCompile("junit", "junit", "4.12")
    implementation("com.alibaba:druid:1.1.22")
}

// See https://github.com/JetBrains/gradle-intellij-plugin/
intellij {
    version = "IC-2020.1"
    setPlugins("java")
}
configure<JavaPluginConvention> {
    sourceCompatibility = JavaVersion.VERSION_11
}
tasks.getByName<org.jetbrains.intellij.tasks.PatchPluginXmlTask>("patchPluginXml") {
    changeNotes("""
    1.1.3<br>
    <ul>
        <li>add xml reference</li>
        <li>fix some issues</li>
    </ul>
      """)
}
tasks.publishPlugin {
    channels("stable")
    token(System.getenv("ORG_GRADLE_PROJECT_intellijPublishToken"))
}
