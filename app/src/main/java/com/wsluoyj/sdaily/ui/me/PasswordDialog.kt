package com.wsluoyj.sdaily.ui.me

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.input.PasswordVisualTransformation

/**
 * 备份口令对话框。
 * @param confirmEnabled 是否需要二次输入一致（导出=true，导入=false）
 */
@Composable
fun BackupPasswordDialog(
    title: String,
    confirmText: String,
    confirmEnabled: Boolean = false,
    onDismiss: () -> Unit,
    onConfirm: (CharArray) -> Unit
) {
    var pwd by remember { mutableStateOf("") }
    var pwd2 by remember { mutableStateOf("") }

    val canConfirm = if (confirmEnabled) {
        pwd.length >= 6 && pwd == pwd2
    } else {
        pwd.isNotEmpty()
    }

    AlertDialog(
        onDismissRequest = onDismiss,
        title = { Text(title) },
        text = {
            Column {
                OutlinedTextField(
                    value = pwd,
                    onValueChange = { pwd = it },
                    label = { Text(if (confirmEnabled) "设置备份口令（至少 6 位）" else "输入备份口令") },
                    singleLine = true,
                    visualTransformation = PasswordVisualTransformation(),
                    keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Password),
                    modifier = Modifier.fillMaxWidth()
                )
                if (confirmEnabled) {
                    OutlinedTextField(
                        value = pwd2,
                        onValueChange = { pwd2 = it },
                        label = { Text("再次输入口令") },
                        singleLine = true,
                        isError = pwd2.isNotEmpty() && pwd != pwd2,
                        supportingText = {
                            if (pwd2.isNotEmpty() && pwd != pwd2) Text("两次输入不一致")
                        },
                        visualTransformation = PasswordVisualTransformation(),
                        keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Password),
                        modifier = Modifier.fillMaxWidth()
                    )
                    Text(
                        "口令用于加密备份文件，丢失后无法找回",
                        style = androidx.compose.material3.MaterialTheme.typography.bodySmall,
                        color = androidx.compose.material3.MaterialTheme.colorScheme.onSurfaceVariant
                    )
                }
            }
        },
        confirmButton = {
            TextButton(
                enabled = canConfirm,
                onClick = { onConfirm(pwd.toCharArray()) }
            ) { Text(confirmText) }
        },
        dismissButton = { TextButton(onClick = onDismiss) { Text("取消") } }
    )
}
