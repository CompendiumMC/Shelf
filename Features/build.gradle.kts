plugins {
    java
    id("io.papermc.paperweight.userdev") version "1.5.11"
}

group = "de.sirywell"
version = "unspecified"

repositories {
    mavenCentral()
}

dependencies {
    compileOnly("org.graalvm.sdk:graal-sdk:23.1.1")
    compileOnly("org.slf4j:slf4j-api:2.0.9")
    compileOnly("org.apache.logging.log4j:log4j-core:2.20.0")
    paperweight.paperDevBundle("1.20.4-R0.1-SNAPSHOT")
    testImplementation(platform("org.junit:junit-bom:5.9.1"))
    testImplementation("org.junit.jupiter:junit-jupiter")
}

java {
    toolchain {
        languageVersion.set(JavaLanguageVersion.of(21))
    }
}

tasks.test {
    useJUnitPlatform()
}