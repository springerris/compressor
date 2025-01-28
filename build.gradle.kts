plugins {
    id("java")
}

group = "com.github.springerris"
version = "1.0-SNAPSHOT"

repositories {
    mavenCentral()
}

dependencies {
    testImplementation(platform("org.junit:junit-bom:5.10.0"))
    testImplementation("org.junit.jupiter:junit-jupiter")
    implementation("io.github.wasabithumb:yandisk4j:0.2.1")
    implementation("io.github.wasabithumb:magma4j:0.1.0")
}

tasks.test {
    useJUnitPlatform()
}