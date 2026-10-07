package com.wsluoyj.sdaily.ui.me

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
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.Switch
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.unit.dp
import com.wsluoyj.sdaily.BuildConfig
import kotlinx.coroutines.launch

@Composable
fun MeScreen(
    modifier: Modifier = Modifier,
    onAboutClick: () -> Unit,
    snackbarHostState: SnackbarHostState
) {
    val scope = rememberCoroutineScope()
    var appLock by remember { mutableStateOf(false) }
    val comingSoon: suspend (String) -> Unit = { name ->
        snackbarHostState.showSnackbar("「$name」将在 v0.2 上线")
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
                    onChange = {
                        appLock = it
                        scope.launch { comingSoon("指纹应用锁") }
                    }
                )
            }
        }

        item {
            SettingCard {
                ActionItem(Icons.Outlined.Save, "导出加密备份（.mbak）", "AES-256-GCM，口令加密") {
                    scope.launch { comingSoon("加密备份导出") }
                }
                ActionItem(Icons.Outlined.UploadFile, "导入备份 / CSV", "从加密备份恢复数据") {
                    scope.launch { comingSoon("备份导入") }
                }
                ActionItem(Icons.Outlined.PeopleOutline, "伴侣档案管理", "新增、归档性伴侣") {
                    scope.launch { comingSoon("伴侣档案管理") }
                }
                ActionItem(Icons.Outlined.Notifications, "记录提醒", "定时提醒记录") {
                    scope.launch { comingSoon("记录提醒") }
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
