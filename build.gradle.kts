plugins {
    id("java")
}

group = "kr.shkworld"
version = "1.1.0"

repositories {
    mavenCentral()
    maven("https://repo.papermc.io/repository/maven-public/")
    maven("https://repo.momirealms.net/releases")
}

dependencies {
    compileOnly("io.papermc.paper:paper-api:1.21.11-R0.1-SNAPSHOT")
    compileOnly("net.momirealms:craft-engine-core:0.0.67")
    compileOnly("net.momirealms:craft-engine-bukkit:0.0.67")
    compileOnly("io.github.toxicity188:BetterHud-bukkit-api:2.0.0")
}
java {
    toolchain {
        languageVersion.set(JavaLanguageVersion.of(25))
    }
}
