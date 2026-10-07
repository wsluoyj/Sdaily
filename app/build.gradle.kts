import java.util.Properties

plugins {
    id("com.android.application")
    id("org.jetbrains.kotlin.plugin.compose")
    id("com.google.devtools.ksp")
}

// 签名信息从 local.properties 读取（不入库）：SDAILY_STORE_FILE / SDAILY_STORE_PASSWORD / SDAILY_KEY_ALIAS / SDAILY_KEY_PASSWORD
val localProps = Properties().apply {
    val f = rootProject.file("local.properties")
    if (f.exists()) f.inputStream().use { load(it) }
}

android {
    namespace = "com.wsluoyj.sdaily"
    compileSdk = 36

    defaultConfig {
        applicationId = "com.wsluoyj.sdaily"
        minSdk = 30          // Android 11
        targetSdk = 35
        versionCode = 1
        versionName = "0.1.0"
        vectorDrawables { useSupportLibrary = true }
    }

    signingConfigs {
        create("release") {
            // local.properties 经 Properties.load 按 ISO-8859-1 解码，含中文的绝对路径会乱码；
            // 路径解析失败时回退到项目根的 keystore/sdaily-release.keystore（密码仍从 local.properties 读取）
            storeFile = localProps.getProperty("SDAILY_STORE_FILE")
                ?.let { rootProject.file(it) }
                ?.takeIf { it.exists() }
                ?: rootProject.file("keystore/sdaily-release.keystore")
            storePassword = localProps.getProperty("SDAILY_STORE_PASSWORD")
            keyAlias = localProps.getProperty("SDAILY_KEY_ALIAS")
            keyPassword = localProps.getProperty("SDAILY_KEY_PASSWORD")
        }
    }

    buildTypes {
        release {
            isMinifyEnabled = false
            signingConfig = signingConfigs.getByName("release")
        }
    }

    compileOptions {
        sourceCompatibility = JavaVersion.VERSION_17
        targetCompatibility = JavaVersion.VERSION_17
    }

    buildFeatures {
        compose = true
        buildConfig = true
    }

    packaging {
        resources {
            excludes += "/META-INF/{AL2.0,LGPL2.1}"
        }
    }
}

dependencies {
    implementation(platform("androidx.compose:compose-bom:2026.02.00"))
    implementation("androidx.core:core-ktx:1.18.0")
    implementation("androidx.activity:activity-compose:1.12.4")
    implementation("androidx.compose.ui:ui")
    implementation("androidx.compose.ui:ui-graphics")
    implementation("androidx.compose.material3:material3")
    implementation("androidx.compose.material:material-icons-extended")
    implementation("androidx.compose.ui:ui-tooling-preview")
    debugImplementation("androidx.compose.ui:ui-tooling")

    // 数据库：Room（KSP 代码生成）+ SQLCipher 整库加密
    implementation("androidx.room:room-runtime:2.8.5")
    implementation("androidx.room:room-ktx:2.8.5")
    ksp("androidx.room:room-compiler:2.8.5")
    implementation("net.zetetic:sqlcipher-android:4.19.1")

    implementation("org.jetbrains.kotlinx:kotlinx-coroutines-android:1.10.2")
}
