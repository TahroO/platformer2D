plugins {
    id("java")
    application
}

group = "main"
version = "0.0.1-SNAPSHOT"

repositories {
    mavenCentral()
}

application {
    mainClass.set("main.Main")
}