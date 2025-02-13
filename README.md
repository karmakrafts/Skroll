# Skroll

Skroll is a lightweight logging framework for Kotlin/Native that offers the following main features:
* Appenders
* Markers
* Filters
* Levels
* Formatters
* Thread support (through [Multiplatform mman](https://git.karmakrafts.dev/kk/multiplatform-mman))
* Logcat support on Android
* ULS support on macOS and iOS

### Platform support

* Windows x64
* Linux x64
* Linux arm64
* macOS x64
* macOS arm64
* iOS x64
* iOS arm64
* Android Native x64
* Android Native arm64
* Android Native arm32

### How to use it

First of all, you need to add the dependency to your Gradle buildscript:

```kotlin
repositories {
    maven("https://files.karmakrafts.dev/maven")
}

dependencies {
    implementation("io.karma.skroll:skroll:<version>")
}
```

Using the Skroll API is as simple as setting the default log configuration and creating a logger afterwards:

```kotlin
init {
    Logger.setDefaultConfig() // Platform console logging, latest and debug log files
}

val logger: Logger = Logger("My logger")

class MyClass {
    val logger: Logger = Logger(this::class)
}
```