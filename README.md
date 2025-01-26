# Skroll

Skroll is a lightweight logging framework for Kotlin/Native that offers the following main features:
* Appenders
* Markers
* Filters
* Levels
* Thread support
* Formatters

### How to use it

First of all, you need to add the dependency to your Gradle buildscript:

```kotlin
repositories {
    maven("https://maven.karmakrafts.dev/maven")
}

dependencies {
    implementation("io.karma.skroll:skroll:<version>")
}
```

Using the Skroll API is as simple as setting the default log configuration and creating a logger afterwards:

```kotlin
init {
    Logger.setDefaultConfig() // Console logging, latest and debug log files
}

val logger: Logger = Logger.create("My logger")

class MyClass {
    val logger: Logger = Logger.create(this::class)
}
```