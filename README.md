## Setup

Add jitpack.io to settings.gradle.kts

```gradle
dependencyResolutionManagement {
    repositoriesMode.set(RepositoriesMode.FAIL_ON_PROJECT_REPOS)
    repositories {
        google()
        mavenCentral()
        maven { setUrl("https://jitpack.io") }
    }
}
```
Add dependency to build.gradle.kts

```gradle
implementation("com.github.buyukyilmaz:helcaraxe:1.0.0")
```

## AppConfig

|                   |        |
|-------------------|--------|
| compileSdk        | 34     |
| buildToolsVersion | 34.0.0 |
| minSdk            | 27     |
| kotlinVersion     | 1.9.24 |
| gradleVersion     | 8.4.1  |
| jvmToolchain      | 17     |

## Dependencies

```sh
implementation("com.squareup.retrofit2:retrofit:2.11.0")
implementation("com.squareup.retrofit2:converter-gson:2.11.0")
implementation("com.google.code.gson:gson:2.11.0")
implementation("org.jetbrains.kotlinx:kotlinx-coroutines-android:1.8.1")
implementation("com.squareup.okhttp3:okhttp:4.12.0")
```
