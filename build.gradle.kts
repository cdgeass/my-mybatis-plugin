plugins {
    id("org.jetbrains.intellij") version "0.4.20"
    java
}

group = "io.github.cdgeass"
version = "1.0.6-alpha"

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
    version = "IC-2020.1"
    setPlugins("java")
}
configure<JavaPluginConvention> {
    sourceCompatibility = JavaVersion.VERSION_11
}
tasks.getByName<org.jetbrains.intellij.tasks.PatchPluginXmlTask>("patchPluginXml") {
    changeNotes("""
    1.0.7-alpha<br>
    <ul>
        <li>add mapper navigation</li>
        <li>modify sql dialog font to Jetbrains Mono</li>
        <li>fix issues</>
    </ul>
    
    1.0.1<br>
    <ul>
        <li>add log format</li>
    </ul>
      """)
}
tasks.publishPlugin {
    channels("alpha")
    token(System.getenv("ORG_GRADLE_PROJECT_intellijPublishToken"))
}
