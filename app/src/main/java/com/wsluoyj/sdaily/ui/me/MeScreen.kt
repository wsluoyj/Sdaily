package com.wsluoyj.sdaily.ui.me

import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.biometric.BiometricManager
import androidx.biometric.BiometricPrompt
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.KeyboardArrowRight
import androidx.compose.material.icons.outlined.Fingerprint
import androidx.compose.material.icons.outlined.Info
import androidx.compose.material.icons.outlined.Notifications
import androidx.compose.material.icons.outlined.PeopleOutline
import androidx.compose.material.icons.outlined.Save
import androidx.compose.material.icons.outlined.UploadFile
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.Switch
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import androidx.core.content.ContextCompat
import androidx.fragment.app.FragmentActivity
import com.wsluoyj.sdaily.BuildConfig
import com.wsluoyj.sdaily.SdailyApp
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import java.time.LocalDate
import javax.crypto.AEADBadTagException

private enum class DialogMode { NONE, EXPORT_PWD, IMPORT_PWD, IMPORT_CONFIRM }

private val AUTHENTICATORS =
    BiometricManager.Authenticators.BIOMETRIC_WEAK or BiometricManager.Authenticators.DEVICE_CREDENTIAL

/** 首次开启应用锁：当场验证一次，通过后写入开关 */
private fun enableAppLock(
    context: android.content.Context,
    onSuccess: () -> Unit,
    onError: (String) -> Unit
) {
    val act = context as? FragmentActivity
    if (act == null) {
        onError("无法启动验证")
        return
    }
    val can = BiometricManager.from(context).canAuthenticate(AUTHENTICATORS)
    if (can != BiometricManager.BIOMETRIC_SUCCESS) {
        onError("请先在系统设置录入指纹或设置屏幕锁")
        return
    }
    val prompt = BiometricPrompt(
        act,
        ContextCompat.getMainExecutor(context),
        object : BiometricPrompt.AuthenticationCallback() {
            override fun onAuthenticationSucceeded(result: BiometricPrompt.AuthenticationResult) {
                onSuccess()
            }

            override fun onAuthenticationError(errorCode: Int, errString: CharSequence) {
                onError(
                    if (errorCode == BiometricPrompt.ERROR_USER_CANCELED ||
                        errorCode == BiometricPrompt.ERROR_NEGATIVE_BUTTON
                    ) "已取消，未开启" else "开启失败：$errString"
                )
            }
        }
    )
    val info = BiometricPrompt.PromptInfo.Builder()
        .setTitle("开启指纹应用锁")
        .setSubtitle("验证一次以启用，打开 App 时将要求验证")
        .setAllowedAuthenticators(AUTHENTICATORS)
        .build()
    prompt.authenticate(info)
}

@Composable
fun MeScreen(
    modifier: Modifier = Modifier,
    onAboutClick: () -> Unit,
    onPartnerManageClick: () -> Unit,
    snackbarHostState: SnackbarHostState
) {
    val context = LocalContext.current
    val app = context.applicationContext as SdailyApp
    val scope = rememberCoroutineScope()

    var appLock by remember {
        mutableStateOf(app.settings.getBoolean("app_lock_enabled", false))
    }
    var dialog by remember { mutableStateOf(DialogMode.NONE) }
    var busy by remember { mutableStateOf(false) }
    var pendingPassword by remember { mutableStateOf<CharArray?>(null) }
    var pendingImportUri by remember { mutableStateOf<android.net.Uri?>(null) }
    var pendingBackupData by remember {
        mutableStateOf<com.wsluoyj.sdaily.data.backup.BackupManager.BackupData?>(null)
    }
    var importSummary by remember { mutableStateOf("") }

    val toast: suspend (String) -> Unit = { snackbarHostState.showSnackbar(it) }

    // 创建备份文件
    val createDoc = rememberLauncherForActivityResult(
        ActivityResultContracts.CreateDocument("application/octet-stream")
    ) { uri ->
        val pwd = pendingPassword
        if (uri != null && pwd != null) {
            busy = true
            scope.launch {
                val msg = withContext(Dispatchers.IO) {
                    runCatching {
                        context.contentResolver.openOutputStream(uri)?.use { os ->
                            app.backupManager.export(os, pwd)
                        } ?: error("无法写入目标文件")
                        "备份已导出"
                    }.getOrElse { "导出失败：${it.message ?: "未知错误"}" }
                }
                pendingPassword = null
                busy = false
                toast(msg)
            }
        } else {
            pendingPassword = null
        }
    }

    // 选择备份文件
    val openDoc = rememberLauncherForActivityResult(
        ActivityResultContracts.OpenDocument()
    ) { uri ->
        if (uri != null) {
            pendingImportUri = uri
            dialog = DialogMode.IMPORT_PWD
        }
    }

    if (busy) {
        AlertDialog(
            onDismissRequest = {},
            confirmButton = {},
            title = { Text("请稍候") },
            text = {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    CircularProgressIndicator(modifier = Modifier.size(28.dp))
                    Spacer(Modifier.size(16.dp))
                    Text(if (dialog == DialogMode.NONE) "正在处理备份文件…" else "正在处理…")
                }
            }
        )
    }

    when (dialog) {
        DialogMode.EXPORT_PWD -> BackupPasswordDialog(
            title = "导出加密备份",
            confirmText = "选择保存位置",
            confirmEnabled = true,
            onDismiss = { dialog = DialogMode.NONE },
            onConfirm = { pwd ->
                pendingPassword = pwd
                dialog = DialogMode.NONE
                val name = "sdaily-backup-${LocalDate.now()}.mbak"
                createDoc.launch(name)
            }
        )
        DialogMode.IMPORT_PWD -> BackupPasswordDialog(
            title = "导入加密备份",
            confirmText = "下一步",
            confirmEnabled = false,
            onDismiss = {
                dialog = DialogMode.NONE
                pendingImportUri = null
            },
            onConfirm = { pwd ->
                val uri = pendingImportUri
                if (uri == null) { dialog = DialogMode.NONE; return@BackupPasswordDialog }
                busy = true
                dialog = DialogMode.NONE
                scope.launch {
                    val result = withContext(Dispatchers.IO) {
                        runCatching {
                            context.contentResolver.openInputStream(uri)?.use { ins ->
                                app.backupManager.parse(ins, pwd)
                            } ?: error("无法读取所选文件")
                        }
                    }
                    busy = false
                    val data = result.getOrNull()
                    if (data != null) {
                        importSummary = "将恢复 ${data.records.size} 条记录、${data.partners.size} 个伴侣档案，当前数据会被覆盖"
                        pendingBackupData = data
                        dialog = DialogMode.IMPORT_CONFIRM
                    } else {
                        pendingPassword = null
                        pendingImportUri = null
                        val err = result.exceptionOrNull()
                        val msg = when (err) {
                            is AEADBadTagException -> "口令错误，无法解密"
                            is IllegalArgumentException -> err.message ?: "备份文件无效"
                            else -> "导入失败：${err?.message ?: "未知错误"}"
                        }
                        toast(msg)
                    }
                }
            }
        )
        DialogMode.IMPORT_CONFIRM -> AlertDialog(
            onDismissRequest = {
                dialog = DialogMode.NONE
                pendingPassword = null
                pendingImportUri = null
            },
            title = { Text("确认覆盖当前数据？") },
            text = { Text(importSummary) },
            confirmButton = {
                TextButton(onClick = {
                    val data = pendingBackupData
                    dialog = DialogMode.NONE
                    pendingPassword = null
                    pendingImportUri = null
                    pendingBackupData = null
                    if (data != null) {
                        busy = true
                        scope.launch {
                            val msg = withContext(Dispatchers.IO) {
                                runCatching { app.backupManager.applyRestore(data) }
                                    .fold({ "导入完成：${it.records} 条记录" }, { "写入失败：${it.message}" })
                            }
                            busy = false
                            toast(msg)
                        }
                    }
                }) { Text("确认覆盖并导入") }
            },
            dismissButton = {
                TextButton(onClick = {
                    dialog = DialogMode.NONE
                    pendingPassword = null
                    pendingImportUri = null
                    pendingBackupData = null
                }) { Text("取消") }
            }
        )
        DialogMode.NONE -> Unit
    }

    LazyColumn(
        modifier = modifier.fillMaxSize(),
        contentPadding = androidx.compose.foundation.layout.PaddingValues(16.dp),
        verticalArrangement = Arrangement.spacedBy(12.dp)
    ) {
        item {
            Text("我的", style = MaterialTheme.typography.headlineSmall)
        }

        item {
            SettingCard {
                SwitchItem(
                    icon = Icons.Outlined.Fingerprint,
                    title = "指纹应用锁",
                    subtitle = "打开 App 时需指纹验证",
                    checked = appLock,
                    onChange = { want ->
                        if (!want) {
                            app.settings.edit().putBoolean("app_lock_enabled", false).apply()
                            appLock = false
                            scope.launch { toast("已关闭指纹应用锁") }
                        } else {
                            enableAppLock(
                                context = context,
                                onSuccess = {
                                    app.settings.edit().putBoolean("app_lock_enabled", true).apply()
                                    appLock = true
                                },
                                onError = { msg -> scope.launch { toast(msg) } }
                            )
                        }
                    }
                )
            }
        }

        item {
            SettingCard {
                ActionItem(Icons.Outlined.Save, "导出加密备份（.mbak）", "AES-256-GCM，口令加密") {
                    dialog = DialogMode.EXPORT_PWD
                }
                ActionItem(Icons.Outlined.UploadFile, "导入加密备份", "从 .mbak 恢复，将覆盖当前数据") {
                    openDoc.launch(arrayOf("*/*"))
                }
                ActionItem(Icons.Outlined.PeopleOutline, "伴侣档案管理", "新增、重命名、归档性伴侣") {
                    onPartnerManageClick()
                }
                ActionItem(Icons.Outlined.Notifications, "记录提醒", "定时提醒记录") {
                    scope.launch { toast("「记录提醒」将在后续版本上线") }
                }
            }
        }

        item {
            SettingCard {
                ActionItem(
                    Icons.Outlined.Info,
                    "关于",
                    "v${BuildConfig.VERSION_NAME} · 应用更新",
                    onClick = onAboutClick
                )
            }
        }

        item {
            Spacer(Modifier.height(4.dp))
            Text(
                "所有数据仅保存在本机，数据库采用 SQLCipher 加密\n备份文件使用 AES-256-GCM，口令不可找回请妥善保管",
                style = MaterialTheme.typography.bodySmall,
                color = MaterialTheme.colorScheme.onSurfaceVariant,
                modifier = Modifier.padding(horizontal = 8.dp)
            )
        }
    }
}

@Composable
private fun SettingCard(content: @Composable () -> Unit) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface)
    ) {
        Column(Modifier.padding(vertical = 4.dp)) { content() }
    }
}

@Composable
private fun ActionItem(icon: ImageVector, title: String, subtitle: String?, onClick: () -> Unit) {
    Row(
        Modifier
            .fillMaxWidth()
            .clickable(onClick = onClick)
            .padding(horizontal = 16.dp, vertical = 14.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Icon(
            icon,
            contentDescription = null,
            tint = MaterialTheme.colorScheme.primary,
            modifier = Modifier.size(22.dp)
        )
        Column(Modifier.weight(1f).padding(start = 14.dp)) {
            Text(title, style = MaterialTheme.typography.bodyLarge)
            if (subtitle != null) {
                Text(
                    subtitle,
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
            }
        }
        Icon(
            Icons.AutoMirrored.Filled.KeyboardArrowRight,
            contentDescription = null,
            tint = MaterialTheme.colorScheme.onSurfaceVariant
        )
    }
}

@Composable
private fun SwitchItem(
    icon: ImageVector,
    title: String,
    subtitle: String,
    checked: Boolean,
    onChange: (Boolean) -> Unit
) {
    Row(
        Modifier
            .fillMaxWidth()
            .padding(horizontal = 16.dp, vertical = 8.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Icon(icon, contentDescription = null, tint = MaterialTheme.colorScheme.primary, modifier = Modifier.size(22.dp))
        Column(Modifier.weight(1f).padding(start = 14.dp)) {
            Text(title, style = MaterialTheme.typography.bodyLarge)
            Text(subtitle, style = MaterialTheme.typography.bodySmall, color = MaterialTheme.colorScheme.onSurfaceVariant)
        }
        Switch(checked = checked, onCheckedChange = onChange)
    }
}
