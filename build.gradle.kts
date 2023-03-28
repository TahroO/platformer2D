plugins {
    id("java")
    application
}

group = "main"
version = "0.0.1-SNAPSHOT"

repositories {
    mavenCentral()
}

dependencies {
    implementation("org.jbox2d:jbox2d-library:2.2.1.1")
}

application {
    mainClass.set("main.Main")
}