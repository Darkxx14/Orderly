plugins {
    id("java")
    id("com.gradleup.shadow") version "9.0.0-beta10"
}

group = "com.xyrisdev"
version = "1.0.0"
java.sourceCompatibility = JavaVersion.VERSION_21

repositories {
    mavenCentral()
    mavenLocal()
    maven("https://repo.papermc.io/repository/maven-public/")
    maven("https://oss.sonatype.org/content/groups/public/")
    maven("https://repo.xyrisdev.com/repository/maven-public/")
    maven("https://repo.extendedclip.com/content/repositories/placeholderapi/")
    maven("https://repo.codemc.io/repository/maven-snapshots")
    maven("https://repo.aikar.co/content/groups/aikar/")
}

dependencies {
    annotationProcessor("org.projectlombok:lombok:1.18.36")

    compileOnly("org.projectlombok:lombok:1.18.36")
    compileOnly("io.papermc.paper:paper-api:1.21.4-R0.1-SNAPSHOT")
    compileOnly("me.clip:placeholderapi:2.11.7")
    compileOnly("com.zaxxer:HikariCP:7.0.2")

    implementation("com.xyrisdev:XLibrary:1.0.0")
}

tasks.withType<JavaCompile> {
    options.encoding = "UTF-8"
    options.compilerArgs.add("-parameters")
}

tasks.withType<Javadoc> {
    options.encoding = "UTF-8"
}

tasks.processResources {
    filesMatching("plugin.yml") {
        expand("version" to project.version)
    }
}

tasks.shadowJar {
    archiveClassifier.set("")
    relocate("com.xyrisdev.library", "com.xyrisdev.orderly.shaded.library")
    relocate("com.zaxxer.hikari", "com.xyrisdev.orderly.shaded.hikari")
}

tasks.build {
    dependsOn(tasks.shadowJar)
}
