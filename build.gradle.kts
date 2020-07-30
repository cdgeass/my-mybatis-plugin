plugins {
    id("org.jetbrains.intellij") version "0.4.20"
    java
}

group = "io.github.cdgeass"
version = "1.2.2"

repositories {
    mavenCentral()
}

dependencies {
    testImplementation("junit", "junit", "4.12")
    implementation("com.github.jsqlparser:jsqlparser:3.1")
}

// See https://github.com/JetBrains/gradle-intellij-plugin/
intellij {
    type = "IU"
    version = "IU-2020.1"
    setPlugins("java", "com.intellij.database")
}
configure<JavaPluginConvention> {
    sourceCompatibility = JavaVersion.VERSION_11
}
tasks.getByName<org.jetbrains.intellij.tasks.PatchPluginXmlTask>("patchPluginXml") {
    changeNotes("""
    1.2.2<br>
    <ul>
        <li>replace druid sql parser with jsqlparser</li>
        <li>rewrite dialog</li>
        <li>add tool window</li>
        <li>fix bugs</li>
    </ul>
      """)
}
tasks.publishPlugin {
    channels("stable")
    token(System.getenv("ORG_GRADLE_PROJECT_intellijPublishToken"))
}
