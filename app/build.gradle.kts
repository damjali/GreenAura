plugins {
    alias(libs.plugins.android.application)
    alias(libs.plugins.google.gms.google.services)
    alias(libs.plugins.google.firebase.crashlytics)
    alias(libs.plugins.kotlin.android)
}

android {
    namespace = "com.example.greenaura"
    compileSdk = 35

    defaultConfig {
        applicationId = "com.example.greenaura"
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
    buildFeatures {
        viewBinding = true
    }
    kotlinOptions {
        jvmTarget = "11"
    }
}

dependencies {
    implementation(libs.appcompat)
    implementation(libs.material)
    implementation(libs.activity)
    implementation(libs.constraintlayout)
    implementation(libs.firebase.auth)
    implementation(libs.firebase.firestore)
    implementation(libs.cardview)
    implementation(libs.navigation.fragment)
    implementation(libs.navigation.ui)
    implementation(libs.gridlayout)
    implementation(libs.firebase.database)
    implementation(libs.firebase.crashlytics)
    implementation(libs.recyclerview)
    implementation(libs.core.ktx)
    implementation(libs.play.services.location)
    implementation(libs.play.services.maps)
    testImplementation(libs.junit)
    androidTestImplementation(libs.ext.junit)
    androidTestImplementation(libs.espresso.core)

    // Explicit versions for dependencies
    implementation(libs.firebase.firestore.v2430)
    implementation(libs.play.services.base)
    implementation(libs.firebase.auth.v2110)
    implementation(libs.glide)
    annotationProcessor(libs.compiler)
    implementation(libs.retrofit)
    implementation(libs.converter.gson)
    implementation(libs.okhttp)
    implementation(libs.okio)
    implementation(libs.material.v130)
    implementation (libs.logging.interceptor)
    implementation (libs.converter.gson)

}
