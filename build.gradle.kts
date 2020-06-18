plugins {
    id("org.jetbrains.intellij") version "0.4.20"
    java
}

group = "io.github.cdgeass"
version = "1.2.0"

repositories {
    mavenCentral()
}

dependencies {
    testCompile("junit", "junit", "4.12")
    implementation("com.github.jsqlparser:jsqlparser:3.1")
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
    1.2.0<br>
    <ul>
        <li>replace druid sql parser with jsqlparser</li>
        <li>rewrite dialog</li>
        <li>add tool window</li>
    </ul>
      """)
}
tasks.publishPlugin {
    channels("stable")
    token(System.getenv("ORG_GRADLE_PROJECT_intellijPublishToken"))
}
