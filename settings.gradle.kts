pluginManagement {
    repositories {
        mavenLocal()
        gradlePluginPortal()
        maven { url = uri("https://artifacts.itemis.cloud/repository/maven-mps/") }
        mavenCentral()
    }
}
plugins {
    id("de.fayard.refreshVersions") version "0.51.0"
}

rootProject.name = "modelix.core"

include("authorization")
include("light-model-client")
include("model-server-lib")
include("metamodel-export")
include("model-api-gen")
include("model-api-gen-gradle")
include("model-api-gen-runtime")
include("model-api")
include("model-client")
include("model-server")
include("model-server-api")
include("ts-model-api")

