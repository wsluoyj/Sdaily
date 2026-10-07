package com.wsluoyj.sdaily.ui.lock

import android.app.Activity
import android.hardware.biometrics.BiometricManager
import android.hardware.biometrics.BiometricPrompt
import android.os.CancellationSignal
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.statusBarsPadding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.Lock
import androidx.compose.material3.Button
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.core.content.ContextCompat

private val AUTHENTICATORS =
    BiometricManager.Authenticators.BIOMETRIC_WEAK or BiometricManager.Authenticators.DEVICE_CREDENTIAL

/** 应用锁解锁页：进入时自动拉起系统指纹验证（Android 11+ 平台 API，任意 Activity 可用） */
@Composable
fun LockScreen(onUnlocked: () -> Unit) {
    val context = LocalContext.current
    var hint by remember { mutableStateOf("验证指纹或屏幕锁以进入") }
    var autoTried by remember { mutableStateOf(false) }

    fun authenticate() {
        val activity = context as? Activity
        if (activity == null) {
            hint = "无法启动验证，请重进应用"
            return
        }
        val manager = activity.getSystemService(BiometricManager::class.java)
        if (manager == null ||
            manager.canAuthenticate(AUTHENTICATORS) != BiometricManager.BIOMETRIC_SUCCESS
        ) {
            hint = "本机未录入指纹或未设置屏幕锁，请先到系统设置完成后再试"
            return
        }
        val prompt = BiometricPrompt.Builder(context)
            .setTitle("解锁 私迹")
            .setSubtitle("验证通过后才能查看记录")
            .setAllowedAuthenticators(AUTHENTICATORS)
            .build()
        prompt.authenticate(
            CancellationSignal(),
            ContextCompat.getMainExecutor(context),
            object : BiometricPrompt.AuthenticationCallback() {
                override fun onAuthenticationSucceeded(result: BiometricPrompt.AuthenticationResult?) {
                    onUnlocked()
                }

                override fun onAuthenticationError(errorCode: Int, errString: CharSequence?) {
                    hint = if (errorCode == BiometricPrompt.BIOMETRIC_ERROR_USER_CANCELED ||
                        errorCode == BiometricPrompt.BIOMETRIC_ERROR_CANCELED
                    ) {
                        "已取消，点击下方按钮重新验证"
                    } else {
                        (errString ?: "验证失败").toString()
                    }
                }
            }
        )
    }

    LaunchedEffect(Unit) {
        if (!autoTried) {
            autoTried = true
            authenticate()
        }
    }

    Surface(modifier = Modifier.fillMaxSize(), color = MaterialTheme.colorScheme.background) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .statusBarsPadding(),
            verticalArrangement = Arrangement.Center,
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Icon(
                Icons.Outlined.Lock,
                contentDescription = null,
                tint = MaterialTheme.colorScheme.primary,
                modifier = Modifier.size(56.dp)
            )
            Spacer(Modifier.height(16.dp))
            Text("私迹", style = MaterialTheme.typography.headlineMedium, fontWeight = FontWeight.Bold)
            Spacer(Modifier.height(8.dp))
            Text(
                hint,
                style = MaterialTheme.typography.bodyMedium,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )
            Spacer(Modifier.height(24.dp))
            Button(onClick = { authenticate() }) { Text("解锁") }
        }
    }
}
