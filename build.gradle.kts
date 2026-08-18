plugins {
    id("java")
}

group = "kr.shkworld"
version = "1.1.1"

repositories {
    mavenCentral()
    maven("https://repo.papermc.io/repository/maven-public/")
}

dependencies {
    compileOnly("io.papermc.paper:paper-api:1.21.11-R0.1-SNAPSHOT")
    compileOnly("io.github.toxicity188:BetterHud-bukkit-api:2.0.0")
}
java {
    toolchain {
        languageVersion.set(JavaLanguageVersion.of(25))
    }
}
