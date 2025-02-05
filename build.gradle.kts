import org.jetbrains.kotlin.gradle.dsl.KotlinJsCompile

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
        useEsModules()
        browser {
        }
        binaries.executable()

        compilerOptions {
            useEsClasses = true
        }
    }
    sourceSets {
        val jsMain by getting {
            dependencies {
                implementation(kotlinWrappers.react)
                implementation(kotlinWrappers.reactDom)
                implementation(npm("lit", "3.2.1"))
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