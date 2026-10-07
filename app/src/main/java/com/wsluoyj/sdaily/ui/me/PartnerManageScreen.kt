package com.wsluoyj.sdaily.ui.me

import androidx.activity.compose.BackHandler
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.Add
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.wsluoyj.sdaily.SdailyApp
import com.wsluoyj.sdaily.data.local.entity.Partner
import kotlinx.coroutines.launch

private enum class PartnerDialog { NONE, ADD, ACTION, RENAME }

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun PartnerManageScreen(onBack: () -> Unit) {
    val context = LocalContext.current
    val app = context.applicationContext as SdailyApp
    val scope = rememberCoroutineScope()

    val partners by app.partnerRepository.allPartners.collectAsState(initial = emptyList())
    val records by app.recordRepository.allRecords.collectAsState(initial = emptyList())
    val counts = remember(records) { records.groupingBy { it.partnerId }.eachCount() }

    var dialog by remember { mutableStateOf(PartnerDialog.NONE) }
    var selected by remember { mutableStateOf<Partner?>(null) }
    var nameInput by remember { mutableStateOf("") }

    val active = partners.filter { !it.archived }
    val archived = partners.filter { it.archived }

    // 有对话框先关对话框，否则系统返回键回到我的页
    BackHandler {
        if (dialog != PartnerDialog.NONE) dialog = PartnerDialog.NONE else onBack()
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("伴侣档案") },
                navigationIcon = {
                    IconButton(onClick = onBack) {
                        Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = "返回")
                    }
                }
            )
        },
        floatingActionButton = {
            FloatingActionButton(onClick = {
                nameInput = ""
                dialog = PartnerDialog.ADD
            }) {
                Icon(Icons.Filled.Add, contentDescription = "新增伴侣")
            }
        }
    ) { padding ->
        LazyColumn(
            modifier = Modifier
                .fillMaxSize()
                .padding(padding),
            contentPadding = androidx.compose.foundation.layout.PaddingValues(16.dp),
            verticalArrangement = androidx.compose.foundation.layout.Arrangement.spacedBy(10.dp)
        ) {
            if (active.isEmpty() && archived.isEmpty()) {
                item {
                    Text(
                        "还没有伴侣档案\n点击右下角 + 新增",
                        style = MaterialTheme.typography.bodyLarge,
                        color = MaterialTheme.colorScheme.onSurfaceVariant,
                        modifier = Modifier.padding(top = 48.dp).fillMaxWidth(),
                        textAlign = androidx.compose.ui.text.style.TextAlign.Center
                    )
                }
            }

            if (active.isNotEmpty()) {
                item { SectionLabel("当前（${active.size}）") }
                items(active, key = { "a${it.id}" }) { p ->
                    PartnerCard(p, counts[p.id] ?: 0) {
                        selected = p
                        dialog = PartnerDialog.ACTION
                    }
                }
            }

            if (archived.isNotEmpty()) {
                item { SectionLabel("已归档（${archived.size}）") }
                items(archived, key = { "z${it.id}" }) { p ->
                    PartnerCard(p, counts[p.id] ?: 0) {
                        selected = p
                        dialog = PartnerDialog.ACTION
                    }
                }
            }
        }
    }

    // 新增
    if (dialog == PartnerDialog.ADD) {
        NameEditDialog(
            title = "新增伴侣",
            initial = "",
            confirmText = "保存",
            onDismiss = { dialog = PartnerDialog.NONE },
            onConfirm = { name ->
                scope.launch {
                    app.partnerRepository.save(Partner(name = name))
                    dialog = PartnerDialog.NONE
                }
            }
        )
    }

    // 操作选择
    if (dialog == PartnerDialog.ACTION) {
        val p = selected
        if (p != null) {
            AlertDialog(
                onDismissRequest = { dialog = PartnerDialog.NONE },
                title = { Text(p.name) },
                text = {
                    Column {
                        TextButton(
                            onClick = {
                                nameInput = p.name
                                dialog = PartnerDialog.RENAME
                            },
                            modifier = Modifier.fillMaxWidth()
                        ) { Text("重命名", modifier = Modifier.fillMaxWidth()) }
                        TextButton(
                            onClick = {
                                scope.launch {
                                    app.partnerRepository.save(p.copy(archived = !p.archived))
                                    dialog = PartnerDialog.NONE
                                }
                            },
                            modifier = Modifier.fillMaxWidth()
                        ) {
                            Text(
                                if (p.archived) "恢复为当前伴侣" else "归档（历史记录保留）",
                                modifier = Modifier.fillMaxWidth()
                            )
                        }
                    }
                },
                confirmButton = {
                    TextButton(onClick = { dialog = PartnerDialog.NONE }) { Text("关闭") }
                }
            )
        }
    }

    // 重命名
    if (dialog == PartnerDialog.RENAME) {
        val p = selected
        if (p != null) {
            NameEditDialog(
                title = "重命名",
                initial = p.name,
                confirmText = "保存",
                onDismiss = { dialog = PartnerDialog.NONE },
                onConfirm = { name ->
                    scope.launch {
                        app.partnerRepository.save(p.copy(name = name))
                        dialog = PartnerDialog.NONE
                    }
                }
            )
        }
    }
}

@Composable
private fun SectionLabel(text: String) {
    Text(
        text,
        style = MaterialTheme.typography.labelLarge,
        color = MaterialTheme.colorScheme.onSurfaceVariant,
        modifier = Modifier.padding(start = 4.dp, top = 4.dp)
    )
}

@Composable
private fun PartnerCard(partner: Partner, recordCount: Int, onClick: () -> Unit) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .clickable(onClick = onClick),
        shape = RoundedCornerShape(14.dp),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface)
    ) {
        Row(
            Modifier.padding(horizontal = 16.dp, vertical = 14.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Column(Modifier.weight(1f)) {
                Text(
                    partner.name,
                    style = MaterialTheme.typography.bodyLarge,
                    fontWeight = FontWeight.Medium
                )
                Text(
                    buildString {
                        append("$recordCount 条记录")
                        if (partner.archived) append(" · 已归档")
                    },
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
            }
        }
    }
}

@Composable
private fun NameEditDialog(
    title: String,
    initial: String,
    confirmText: String,
    onDismiss: () -> Unit,
    onConfirm: (String) -> Unit
) {
    var text by remember { mutableStateOf(initial) }
    AlertDialog(
        onDismissRequest = onDismiss,
        title = { Text(title) },
        text = {
            OutlinedTextField(
                value = text,
                onValueChange = { text = it },
                label = { Text("称呼（不会上传）") },
                singleLine = true,
                modifier = Modifier.fillMaxWidth()
            )
        },
        confirmButton = {
            TextButton(
                enabled = text.isNotBlank(),
                onClick = { onConfirm(text.trim()) }
            ) { Text(confirmText) }
        },
        dismissButton = { TextButton(onClick = onDismiss) { Text("取消") } }
    )
}
