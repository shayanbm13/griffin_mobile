plugins {
    alias(libs.plugins.android.application)
    alias(libs.plugins.kotlin.android)
}

android {



    namespace = "com.example.griffinmobile"
    compileSdk = 35

    defaultConfig {
        applicationId = "com.example.griffinmobile"
        minSdk = 24
        targetSdk = 34
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
        sourceCompatibility = JavaVersion.VERSION_1_8
        targetCompatibility = JavaVersion.VERSION_1_8
    }
    kotlinOptions {
        jvmTarget = "1.8"
    }
    buildFeatures {
        viewBinding = true
        dataBinding = true
    }
}

dependencies {

    implementation(libs.androidx.core.ktx)
    implementation (libs.androidx.databinding.runtime)
    implementation(libs.androidx.appcompat)
    implementation(libs.material)
    implementation(libs.androidx.activity)
    implementation(libs.androidx.constraintlayout)
    implementation(libs.androidx.preference)
    testImplementation(libs.junit)
    androidTestImplementation(libs.androidx.junit)
    androidTestImplementation(libs.androidx.espresso.core)

    implementation (libs.material.v180)

    implementation ("com.github.qamarelsafadi:CurvedBottomNavigation:0.1.3")
    implementation ("androidx.databinding:databinding-runtime:4.1.0")
    implementation ("com.github.MrNouri:DynamicSizes:1.0")
    implementation ("com.google.android.material:material:1.9.0")
    implementation("com.squareup.okhttp3:okhttp-dnsoverhttps:4.10.0")
    implementation ("com.squareup.okhttp3:okhttp:4.9.3")
    implementation ("com.github.bumptech.glide:glide:4.12.0")
    implementation ("pl.droidsonroids.gif:android-gif-drawable:1.2.23")
    annotationProcessor ("com.github.bumptech.glide:compiler:4.12.0")
    implementation (libs.androidx.lifecycle.viewmodel.ktx)
    implementation (libs.lottie)

    implementation (libs.androidx.fragment.ktx)
    implementation (libs.play.services.location)
}