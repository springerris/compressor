plugins {
    id("java")
    id("io.github.goooler.shadow") version "8.1.8"
}

group = "com.github.springerris"
version = "1.0-SNAPSHOT"

repositories {
    mavenCentral()
}

dependencies {
    implementation("io.github.wasabithumb:yandisk4j:0.4.0")
    implementation("io.github.wasabithumb:magma4j:0.1.1")
    implementation("com.hierynomus:sshj:0.39.0")
    implementation("org.jetbrains:annotations:26.0.2")
}

tasks.compileJava {
    targetCompatibility = "21"
    sourceCompatibility = "21"
}

tasks.shadowJar {
    archiveClassifier.set("")
    manifest {
        attributes["Main-Class"] = "com.github.springerris.Main"
    }
}

tasks.build {
    dependsOn(tasks.shadowJar)
}
