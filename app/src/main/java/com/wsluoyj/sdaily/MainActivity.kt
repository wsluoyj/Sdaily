package com.wsluoyj.sdaily

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import com.wsluoyj.sdaily.ui.MainScaffold
import com.wsluoyj.sdaily.ui.lock.LockScreen
import com.wsluoyj.sdaily.ui.theme.SdailyTheme

/**
 * 保持 ComponentActivity：activity-compose 的 setContent 与
 * ActivityResultLauncher（备份导入导出、权限申请）都依赖它。
 * 指纹锁使用 Android 11+ 平台 BiometricPrompt，无需 FragmentActivity。
 */
class MainActivity : ComponentActivity() {

    private val settings get() = getSharedPreferences("sdaily_settings", MODE_PRIVATE)

    /** 应用锁状态：开启时冷启动与从后台返回都需要验证 */
    private var locked by mutableStateOf(false)

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        locked = settings.getBoolean("app_lock_enabled", false)
        setContent {
            SdailyTheme {
                if (locked) {
                    LockScreen(onUnlocked = { locked = false })
                } else {
                    MainScaffold()
                }
            }
        }
    }

    override fun onStop() {
        super.onStop()
        // 切到后台立即上锁（onPause 不触发，避免系统指纹对话框误触发）
        if (settings.getBoolean("app_lock_enabled", false)) {
            locked = true
        }
    }
}
