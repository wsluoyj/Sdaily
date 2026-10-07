// Sdaily 顶层构建文件
// AGP 9.x 内置 Kotlin（KGP 2.2.10），无需 kotlin-android 插件
plugins {
    id("com.android.application") version "9.4.0" apply false
    id("org.jetbrains.kotlin.plugin.compose") version "2.2.10" apply false
    id("com.google.devtools.ksp") version "2.2.10-2.0.2" apply false
}
