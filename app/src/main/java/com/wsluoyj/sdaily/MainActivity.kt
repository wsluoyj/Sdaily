package com.wsluoyj.sdaily

import android.os.Bundle
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.compose.ui.platform.ComposeView
import androidx.core.view.WindowCompat
import androidx.fragment.app.FragmentActivity
import androidx.lifecycle.setViewTreeLifecycleOwner
import androidx.savedstate.setViewTreeSavedStateRegistryOwner
import com.wsluoyj.sdaily.ui.MainScaffold
import com.wsluoyj.sdaily.ui.lock.LockScreen
import com.wsluoyj.sdaily.ui.theme.SdailyTheme

/**
 * 继承 FragmentActivity 是 BiometricPrompt 的硬性要求；
 * Compose 通过 ComposeView 挂载，并手动接管 ViewTree 的 lifecycle/savedstate owner。
 */
class MainActivity : FragmentActivity() {

    private val settings get() = getSharedPreferences("sdaily_settings", MODE_PRIVATE)

    /** 应用锁状态：开启时冷启动与从后台返回都需要验证 */
    private var locked by mutableStateOf(false)

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        // edge-to-edge（FragmentActivity 没有 enableEdgeToEdge 扩展，手动开启）
        WindowCompat.setDecorFitsSystemWindows(window, false)

        locked = settings.getBoolean("app_lock_enabled", false)

        val composeView = ComposeView(this).apply {
            setViewTreeLifecycleOwner(this@MainActivity)
            setViewTreeSavedStateRegistryOwner(this@MainActivity)
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
        setContentView(composeView)
    }

    override fun onStop() {
        super.onStop()
        // 切到后台立即上锁（onPause 不触发，避免系统指纹对话框误触发）
        if (settings.getBoolean("app_lock_enabled", false)) {
            locked = true
        }
    }
}
