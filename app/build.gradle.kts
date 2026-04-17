plugins {
    alias(libs.plugins.android.application)
    alias(libs.plugins.kotlin.compose)
    id("com.google.devtools.ksp")
    id("com.google.dagger.hilt.android")
}

android {
    namespace = "com.gshoaib998.progressly"
    compileSdk {
        version = release(36) {
            minorApiLevel = 1
        }
    }

    defaultConfig {
        applicationId = "com.gshoaib998.progressly"
        minSdk = 26
        targetSdk = 36
        versionCode = 1
        versionName = "1.0"

        testInstrumentationRunner = "androidx.test.runner.AndroidJUnitRunner"
    }

    buildTypes {
        release {
            isMinifyEnabled = true
            isShrinkResources = true
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
        compose = true
    }
}

dependencies {
    implementation(libs.androidx.core.ktx)
    implementation(libs.androidx.lifecycle.runtime.ktx)
    implementation(libs.androidx.activity.compose)
    implementation(platform(libs.androidx.compose.bom))
    implementation(libs.androidx.compose.ui)
    implementation(libs.androidx.compose.ui.graphics)
    implementation(libs.androidx.compose.ui.tooling.preview)
    implementation(libs.androidx.compose.material3)
    testImplementation(libs.junit)
    androidTestImplementation(libs.androidx.junit)
    androidTestImplementation(libs.androidx.espresso.core)
    androidTestImplementation(platform(libs.androidx.compose.bom))
    androidTestImplementation(libs.androidx.compose.ui.test.junit4)
    debugImplementation(libs.androidx.compose.ui.tooling)
    debugImplementation(libs.androidx.compose.ui.test.manifest)

    val room_version = "2.8.4"

    implementation("androidx.room:room-runtime:$room_version")

    // 3. Kotlin Extensions for Coroutine/Flow Support
    implementation("androidx.room:room-ktx:$room_version")

    // 4. KSP Compiler (The Code Generator)
    // NOTE: You must use the 'ksp' configuration, not 'kapt'
    ksp("androidx.room:room-compiler:$room_version")

    // 5. Testing Library (For migration tests)
    testImplementation("androidx.room:room-testing:$room_version")

    // navigation component
    implementation("androidx.navigation:navigation-compose:2.9.7")

    implementation("androidx.core:core-splashscreen:1.2.0")

    implementation("androidx.compose.material3:material3:1.5.0-alpha14")

    implementation("com.google.dagger:hilt-android:2.59.2")
    ksp("com.google.dagger:hilt-compiler:2.59.2")

    implementation("androidx.hilt:hilt-navigation-compose:1.3.0")

    implementation("androidx.datastore:datastore-preferences:1.2.0")

    // core icons library
    implementation("androidx.compose.material:material-icons-core:1.7.3")
// Full Extended Icon Set
    implementation("androidx.compose.material:material-icons-extended:1.7.3")
//    lottie animation for jetpack compose
    implementation("com.airbnb.android:lottie-compose:6.7.1")


}