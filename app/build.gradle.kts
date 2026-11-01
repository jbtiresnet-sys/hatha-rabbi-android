plugins {
    id("com.android.application")
    id("com.google.gms.google-services")
}
android {
    namespace = "org.itsmygod.hatharabbi"
    compileSdk = 35
    defaultConfig {
        applicationId = "org.itsmygod.hatharabbi"
        minSdk = 29
        targetSdk = 35
        versionCode = 2
        versionName = "0.2.0"
    }
    compileOptions {
        sourceCompatibility = JavaVersion.VERSION_17
        targetCompatibility = JavaVersion.VERSION_17
    }
}
dependencies {
    implementation("com.google.firebase:firebase-messaging:24.1.2")
}
