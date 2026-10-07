package com.wsluoyj.sdaily.ui.record

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.selection.selectable
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.AssistChip
import androidx.compose.material3.FilterChip
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.RadioButton
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.wsluoyj.sdaily.data.local.entity.Partner

/** 伴侣选择：chip 展示当前选择，弹窗单选 + 可直接新建 */
@Composable
fun PartnerPicker(
    partners: List<Partner>,
    selectedId: Long?,
    onSelect: (Long?) -> Unit,
    onAdd: (String) -> Unit
) {
    var showDialog by remember { mutableStateOf(false) }
    val selectedName = partners.firstOrNull { it.id == selectedId }?.name

    Row(
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.spacedBy(8.dp)
    ) {
        FilterChip(
            selected = selectedId != null,
            onClick = { showDialog = true },
            label = { Text(selectedName ?: "选择伴侣（可选）") }
        )
        AssistChip(onClick = { showDialog = true }, label = { Text("管理/新增") })
    }

    if (showDialog) {
        PartnerDialog(
            partners = partners,
            selectedId = selectedId,
            onDismiss = { showDialog = false },
            onSelect = {
                onSelect(it)
                showDialog = false
            },
            onAdd = {
                onAdd(it)
                showDialog = false
            }
        )
    }
}

@Composable
private fun PartnerDialog(
    partners: List<Partner>,
    selectedId: Long?,
    onDismiss: () -> Unit,
    onSelect: (Long?) -> Unit,
    onAdd: (String) -> Unit
) {
    var newName by remember { mutableStateOf("") }

    AlertDialog(
        onDismissRequest = onDismiss,
        title = { Text("选择伴侣") },
        text = {
            Column {
                if (partners.isEmpty()) {
                    Text(
                        "暂无伴侣档案",
                        style = MaterialTheme.typography.bodyMedium,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                }
                partners.forEach { p ->
                    Row(
                        Modifier
                            .fillMaxWidth()
                            .selectable(selected = p.id == selectedId) { onSelect(p.id) }
                            .padding(vertical = 4.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        RadioButton(selected = p.id == selectedId, onClick = { onSelect(p.id) })
                        Text(p.name, style = MaterialTheme.typography.bodyLarge)
                    }
                }
                Row(
                    Modifier.padding(top = 8.dp),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    OutlinedTextField(
                        value = newName,
                        onValueChange = { newName = it },
                        label = { Text("新伴侣名称") },
                        singleLine = true,
                        modifier = Modifier.weight(1f)
                    )
                    OutlinedButton(
                        enabled = newName.isNotBlank(),
                        onClick = {
                            onAdd(newName.trim())
                            newName = ""
                        }
                    ) { Text("添加") }
                }
            }
        },
        confirmButton = {
            TextButton(onClick = onDismiss) { Text("完成") }
        },
        dismissButton = {
            TextButton(onClick = { onSelect(null) }) { Text("清除选择") }
        }
    )
}
