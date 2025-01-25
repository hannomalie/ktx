plugins {
    kotlin("multiplatform") version "2.1.0"
    id("io.exoquery.terpal-plugin") version "2.1.0-2.0.0.PL"
}

group = "de.hanno"
version = "0.0.1-SNAPSHOT"

repositories {
    mavenCentral()
}

kotlin {
    jvm()
    js {
        browser {
        }
        binaries.executable()
    }
    sourceSets {
        val jsMain by getting {
            dependencies {
                implementation(kotlinWrappers.react)
                implementation(kotlinWrappers.reactDom)
            }
        }
    }
}

dependencies {
    commonMainApi("io.exoquery:terpal-runtime:2.1.0-0.2.0")

    commonTestImplementation(kotlin("test"))
}

kotlin {
    jvmToolchain(17)
}