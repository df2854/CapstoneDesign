plugins {
    alias(libs.plugins.android.application)
}

android {
    namespace = "com.example.pacemaker"
    compileSdk = 34

    defaultConfig {
        applicationId = "com.example.pacemaker"
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
}

dependencies {implementation(libs.appcompat)
    implementation(libs.material)
    implementation(libs.activity)
    implementation(libs.constraintlayout)
    implementation(files("libs\\com.skt.Tmap_1.76.jar"))
    testImplementation(libs.junit)
    androidTestImplementation(libs.ext.junit)
    implementation("androidx.preference:preference-ktx:1.2.0")
    androidTestImplementation(libs.espresso.core)
}