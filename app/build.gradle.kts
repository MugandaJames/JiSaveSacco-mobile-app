plugins {
    alias(libs.plugins.android.application)
    // Applies the Google Services tool cleanly via the new Gradle 9 configuration profile
//    id("com.google.gms.google-services")
}

android {
    namespace = "com.jisavesacco"
    compileSdk = 36

    defaultConfig {
        applicationId = "com.jisavesacco"
        minSdk = 24
        targetSdk = 36
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
}

dependencies {
    implementation(libs.activity.ktx)
    implementation(libs.appcompat)
    implementation(libs.material)
    implementation("androidx.constraintlayout:constraintlayout:2.2.1")
    implementation("androidx.cardview:cardview:1.0.0")

    // FIREBASE REALTIME DATABASE INFRASTRUCTURE
//    implementation(platform("com.google.firebase:firebase-bom:33.1.0"))
//    implementation("com.google.firebase:firebase-database")

    testImplementation(libs.junit)
    androidTestImplementation(libs.espresso.core)
    androidTestImplementation(libs.ext.junit)
}