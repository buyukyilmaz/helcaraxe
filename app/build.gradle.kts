plugins {
    id("com.android.library")
    kotlin("android")
    id("org.jmailen.kotlinter")
    `maven-publish`
}

android {
    namespace = "com.glorfindel.helcaraxe"
    compileSdk = 34
    buildToolsVersion = "34.0.0"
    defaultConfig.minSdk = 28
    buildTypes.getByName("release").isMinifyEnabled = false
    kotlin.jvmToolchain(17)

    androidComponents {
        beforeVariants(selector().withBuildType("debug")) { variantBuilder ->
            variantBuilder.enable = false
        }
    }
}

dependencies {
    implementation("com.squareup.retrofit2:retrofit:2.11.0")
    implementation("com.squareup.retrofit2:converter-gson:2.11.0")
    implementation("com.google.code.gson:gson:2.11.0")
    implementation("org.jetbrains.kotlinx:kotlinx-coroutines-android:1.8.1")
    implementation("com.squareup.okhttp3:okhttp:4.12.0")
}

afterEvaluate {
    publishing {
        publications {
            register("release", MavenPublication::class) {
                from(components["release"])
                groupId = "com.glorfindel.helcaraxe"
                artifactId = "helcaraxe"
                version = "1.0.2"
            }
        }
    }
}
