// Sdaily 顶层构建文件
// AGP 9.0 起内置 Kotlin 支持，无需再声明 kotlin-android 插件
plugins {
    id("com.android.application") version "9.4.0" apply false
    id("org.jetbrains.kotlin.plugin.compose") version "2.4.10" apply false
}
