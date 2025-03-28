plugins {
    alias(libs.plugins.android.application)
    alias(libs.plugins.kotlin.android)
    //alias(libs.plugins.kotlin.kapt)
}

android {
    namespace = "dev.jullls.safeapp"
    compileSdk = 35

    defaultConfig {
        applicationId = "dev.jullls.safeapp"
        minSdk = 26
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
    viewBinding {
        enable = true
    }
}

dependencies {

    // Retrofit
    implementation(libs.retrofit)
    implementation(libs.retrofit.converter.gson)

    // Glide
    implementation(libs.glide)
    implementation(libs.androidx.navigation.fragment)
    annotationProcessor(libs.glide.compiler)

    // Core KTX
    implementation(libs.androidx.core.ktx)

    // Корутины
    implementation(libs.kotlinx.coroutines.android)

    // ViewModel с поддержкой корутин
    implementation(libs.lifecycle.viewmodel.ktx)
    // Для viewModels() делегата
    implementation(libs.androidx.fragment.ktx)

    // Для LiveData
    implementation(libs.androidx.lifecycle.livedata.ktx)
    implementation(libs.room.runtime)
    implementation(libs.room.ktx)
    annotationProcessor(libs.room.compiler)
    //kapt(libs.room.compiler)
    implementation(libs.androidx.appcompat)
    implementation(libs.material)
    implementation(libs.androidx.activity)
    implementation(libs.androidx.constraintlayout)
    testImplementation(libs.junit)
    androidTestImplementation(libs.androidx.junit)
    androidTestImplementation(libs.androidx.espresso.core)
}