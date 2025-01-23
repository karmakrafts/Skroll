# Skroll

Skroll is a lightweight logging framework for Kotlin/Native that offers the following main features:
* Appenders
* Markers
* Filters
* Levels
* Thread support
* Formatters

### How to use it

Using Skroll is as simple as setting the default log configuration and creating a logger afterwards:

```kotlin
init {
    Logger.setDefaultConfig() // Console logging, latest and debug log files
}

val logger: Logger = Logger.create("My logger")

class MyClass {
    val logger: Logger = Logger.create(this::class)
}
```