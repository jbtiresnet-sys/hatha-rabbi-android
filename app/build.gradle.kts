plugins { id("com.android.application") }
android { namespace = "org.itsmygod.hatharabbi"; compileSdk = 35
 defaultConfig { applicationId = "org.itsmygod.hatharabbi"; minSdk = 29; targetSdk = 35; versionCode = 1; versionName = "0.1.0" }
 compileOptions { sourceCompatibility = JavaVersion.VERSION_17; targetCompatibility = JavaVersion.VERSION_17 }
}

// Firebase configuration will be added during setup.
