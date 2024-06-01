plugins {
    id("com.android.library")
    kotlin("android")
    id("org.jmailen.kotlinter")
}

android {
    namespace = "com.glorfindel.helcaraxe"
    compileSdk = 34
    buildToolsVersion = "34.0.0"
    defaultConfig.minSdk = 27
    buildTypes.getByName("release").isMinifyEnabled = false
    kotlin.jvmToolchain(17)

    androidComponents {
        beforeVariants(selector().withBuildType("debug")) { variantBuilder ->
            variantBuilder.enable = false
        }
    }

    libraryVariants.all {
        outputs.all {
            val output = this as com.android.build.gradle.internal.api.BaseVariantOutputImpl
            output.outputFileName = "helcaraxe-1.0.0.aar"
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
