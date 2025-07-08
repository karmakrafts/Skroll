# Skroll

[![](https://git.karmakrafts.dev/kk/skroll/badges/master/pipeline.svg)](https://git.karmakrafts.dev/kk/skroll/-/pipelines)
[![](https://img.shields.io/maven-metadata/v?metadataUrl=https%3A%2F%2Frepo.maven.apache.org%2Fmaven2%2Fdev%2Fkarmakrafts%2Fskroll%2Fskroll-core%2Fmaven-metadata.xml
)](https://git.karmakrafts.dev/kk/introspekt/-/packages)
[![](https://img.shields.io/maven-metadata/v?metadataUrl=https%3A%2F%2Fcentral.sonatype.com%2Frepository%2Fmaven-snapshots%2Fdev%2Fkarmakrafts%2Fskroll%2Fskroll-core%2Fmaven-metadata.xml
)](https://git.karmakrafts.dev/kk/skroll/-/packages)

Skroll is a lightweight logging framework for Kotlin/Multiplatform that offers the following main features:

* Standalone API/facade
* Appenders
* Markers
* Filters
* Levels
* Formatters
* Logcat support on Android & Android Native
* ULS support on macOS and iOS
* Event Log support on Windows

### How to use it

First, add the official Karma Krafts maven repository to your `settings.gradle.kts`:

```kotlin
pluginManagement {
    repositories {
        maven("https://central.sonatype.com/repository/maven-snapshots")
        mavenCentral()
    }
}

dependencyResolutionManagement {
    repositories {
        maven("https://central.sonatype.com/repository/maven-snapshots")
        mavenCentral()
    }
}
```

Then add a dependency on the library in your buildscript:

```kotlin
kotlin {
    commonMain {
        dependencies {
            implementation("dev.karmakrafts.skroll:skroll-api:<version>")
            implementation("dev.karmakrafts.skroll:skroll-core:<version>")
        }
    }
}
```