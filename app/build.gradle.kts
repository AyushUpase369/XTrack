plugins {
    alias(libs.plugins.android.application)
    alias(libs.plugins.kotlin.android)
    id("kotlin-kapt")
}

android {
    namespace = "com.example.xtrack"
    compileSdk = 35

    defaultConfig {
        applicationId = "com.example.xtrack"
        minSdk = 24
        targetSdk = 35
        versionCode = 1
        versionName = "1.0"

        testInstrumentationRunner = "androidx.test.runner.AndroidJUnitRunner"
    }

    buildTypes {
        release {
            isMinifyEnabled = false
            proguardFiles(
                getDefaultProguardFile("proguard-android-optimize.txt"),
                "proguard-rules.pro"
            )
        }
    }
    compileOptions {
        sourceCompatibility = JavaVersion.VERSION_11
        targetCompatibility = JavaVersion.VERSION_11
    }
    kotlinOptions {
        jvmTarget = "11"
    }
}

dependencies {

    implementation(libs.androidx.core.ktx)
    implementation(libs.androidx.appcompat)
    implementation(libs.material)
    implementation(libs.androidx.activity)
    implementation(libs.androidx.constraintlayout)
    testImplementation(libs.junit)
    androidTestImplementation(libs.androidx.junit)
    androidTestImplementation(libs.androidx.espresso.core)
    implementation (libs.androidx.recyclerview)
    implementation(libs.androidx.cardview)
    implementation(libs.material.v1100)
    implementation (libs.view)
    implementation (libs.threetenabp)
    implementation (libs.material)
    implementation (libs.mpandroidchart.vv310)
    implementation (libs.material.v170)
    implementation (libs.google.material.v1100)

    implementation(libs.sceneview) // Replace with the latest version if available
    implementation(libs.kotlinx.coroutines.android)

    implementation (libs.glide)
    kapt (libs.compiler)
    implementation (libs.lottie)

}