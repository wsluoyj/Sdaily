package com.wsluoyj.sdaily.ui.record

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.FlowRow
import androidx.compose.foundation.layout.ExperimentalLayoutApi
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Star
import androidx.compose.material.icons.filled.StarBorder
import androidx.compose.material.icons.outlined.Add
import androidx.compose.material.icons.outlined.Remove
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.AssistChip
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.FilterChip
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.Switch
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
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
import com.wsluoyj.sdaily.data.local.entity.EjacRecord
import com.wsluoyj.sdaily.data.local.entity.Partner
import com.wsluoyj.sdaily.data.local.entity.RecordMethod
import com.wsluoyj.sdaily.util.toLocalDate
import com.wsluoyj.sdaily.util.formatDayLabel
import com.wsluoyj.sdaily.util.formatTime
import kotlinx.coroutines.launch
import java.time.LocalDate

@OptIn(ExperimentalLayoutApi::class)
@Composable
fun AddRecordScreen(
    modifier: Modifier = Modifier,
    editRecord: EjacRecord? = null,
    onSaved: () -> Unit,
    snackbarHostState: SnackbarHostState
) {
    val context = LocalContext.current
    val app = context.applicationContext as SdailyApp
    val scope = rememberCoroutineScope()
    val partners by app.partnerRepository.activePartners.collectAsState(initial = emptyList())
    val allPartners by app.partnerRepository.allPartners.collectAsState(initial = emptyList())

    var method by remember { mutableStateOf(editRecord?.method ?: RecordMethod.SEX) }
    var durationFilled by remember { mutableStateOf(editRecord?.durationMin != null) }
    var duration by remember { mutableStateOf(editRecord?.durationMin ?: 10) }
    var rating by remember { mutableStateOf(editRecord?.rating) }
    var partnerId by remember { mutableStateOf(editRecord?.partnerId) }
    // 编辑已归档伴侣的历史记录时，把该并入选项，保证名字可见、数据可原样保存
    val pickerPartners = remember(partners, allPartners, partnerId) {
        val archivedSelected = partnerId?.let { id ->
            allPartners.find { it.id == id && partners.none { p -> p.id == id } }
        }
        if (archivedSelected != null) {
            partners + archivedSelected.copy(name = "${archivedSelected.name}（已归档）")
        } else partners
    }
    var location by remember { mutableStateOf(editRecord?.location ?: "") }
    var internalEjac by remember { mutableStateOf(editRecord?.internalEjac ?: false) }
    var protection by remember { mutableStateOf(editRecord?.protectionUsed ?: false) }
    var usedToy by remember { mutableStateOf(editRecord?.usedToy ?: false) }
    var note by remember { mutableStateOf(editRecord?.note ?: "") }
    var saving by remember { mutableStateOf(false) }
    var confirmDelete by remember { mutableStateOf(false) }

    Column(
        modifier = modifier
            .fillMaxSize()
            .verticalScroll(rememberScrollState())
            .padding(start = 16.dp, end = 16.dp, top = 16.dp, bottom = 28.dp),
        verticalArrangement = Arrangement.spacedBy(12.dp)
    ) {
        Text(
            if (editRecord == null) "记录一次" else "编辑记录",
            style = MaterialTheme.typography.headlineSmall,
            fontWeight = FontWeight.Bold
        )
        val shown = editRecord?.timeMs ?: System.currentTimeMillis()
        Text(
            formatDayLabel(shown.toLocalDate()) + "  " + formatTime(shown),
            style = MaterialTheme.typography.bodyMedium,
            color = MaterialTheme.colorScheme.onSurfaceVariant
        )

        // 方式
        SectionCard("方式") {
            FlowRow(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                RecordMethod.entries.forEach { m ->
                    FilterChip(
                        selected = method == m,
                        onClick = { method = m },
                        label = { Text(m.label) }
                    )
                }
            }
        }

        // 时长（可空）
        SectionCard("时长（选填）") {
            Row(verticalAlignment = Alignment.CenterVertically) {
                FilterChip(
                    selected = !durationFilled,
                    onClick = { durationFilled = false },
                    label = { Text("未填") }
                )
                Spacer(Modifier.size(12.dp))
                if (durationFilled) {
                    IconButton(onClick = { if (duration > 1) duration-- }) {
                        Icon(Icons.Outlined.Remove, contentDescription = "减少")
                    }
                    Text(
                        "$duration 分钟",
                        style = MaterialTheme.typography.titleMedium,
                        modifier = Modifier.padding(horizontal = 8.dp)
                    )
                    IconButton(onClick = { if (duration < 600) duration++ }) {
                        Icon(Icons.Outlined.Add, contentDescription = "增加")
                    }
                } else {
                    AssistChip(onClick = { durationFilled = true }, label = { Text("填写时长") })
                }
            }
        }

        // 满意度
        SectionCard("满意度（选填）") {
            Row(verticalAlignment = Alignment.CenterVertically) {
                (1..5).forEach { star ->
                    IconButton(onClick = { rating = if (rating == star) null else star }) {
                        Icon(
                            imageVector = if (star <= (rating ?: 0)) Icons.Filled.Star else Icons.Filled.StarBorder,
                            contentDescription = "$star 星",
                            tint = MaterialTheme.colorScheme.primary
                        )
                    }
                }
                if (rating != null) {
                    Text(
                        "清除",
                        style = MaterialTheme.typography.labelMedium,
                        color = MaterialTheme.colorScheme.onSurfaceVariant,
                        modifier = Modifier
                            .padding(start = 8.dp)
                            .clickable { rating = null }
                    )
                }
            }
        }

        // 自慰专属：是否使用器具
        if (method == RecordMethod.MASTURBATION) {
            SectionCard("自慰选项") {
                SwitchRow("使用器具", usedToy) { usedToy = it }
            }
        }

        // 性生活专属
        if (method == RecordMethod.SEX) {
            SectionCard("性生活详情（选填）") {
                Text("性伴侣", style = MaterialTheme.typography.labelLarge)
                Spacer(Modifier.height(6.dp))
                PartnerPicker(
                    partners = pickerPartners,
                    selectedId = partnerId,
                    onSelect = { partnerId = it },
                    onAdd = { name ->
                        scope.launch {
                            val id = app.partnerRepository.save(Partner(name = name))
                            partnerId = id
                            snackbarHostState.showSnackbar("已添加伴侣：$name")
                        }
                    }
                )
                Spacer(Modifier.height(12.dp))
                Text("地点", style = MaterialTheme.typography.labelLarge)
                Spacer(Modifier.height(6.dp))
                FlowRow(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                    listOf("家", "酒店", "其他").forEach { loc ->
                        FilterChip(
                            selected = location == loc,
                            onClick = { location = loc },
                            label = { Text(loc) }
                        )
                    }
                }
                Spacer(Modifier.height(8.dp))
                OutlinedTextField(
                    value = location,
                    onValueChange = { location = it },
                    label = { Text("自定义地点") },
                    singleLine = true,
                    modifier = Modifier.fillMaxWidth()
                )
                Spacer(Modifier.height(8.dp))
                SwitchRow("体内射精", internalEjac) { internalEjac = it }
                HorizontalDivider(modifier = Modifier.padding(vertical = 4.dp))
                SwitchRow("采取防护措施", protection) { protection = it }
            }
        }

        // 备注
        SectionCard("备注") {
            OutlinedTextField(
                value = note,
                onValueChange = { note = it },
                placeholder = { Text("感受、情况备注…") },
                minLines = 2,
                modifier = Modifier.fillMaxWidth()
            )
        }

        if (editRecord != null) {
            TextButton(
                onClick = { confirmDelete = true },
                modifier = Modifier.fillMaxWidth()
            ) {
                Text("删除这条记录", color = MaterialTheme.colorScheme.error)
            }
        }

        Button(
            onClick = {
                    if (saving) return@Button
                    saving = true
                    scope.launch {
                        app.recordRepository.save(
                            EjacRecord(
                                id = editRecord?.id ?: 0L,
                                timeMs = editRecord?.timeMs ?: System.currentTimeMillis(),
                                createdAt = editRecord?.createdAt ?: System.currentTimeMillis(),
                                updatedAt = System.currentTimeMillis(),
                                method = method,
                                durationMin = if (durationFilled) duration else null,
                                rating = rating,
                                partnerId = if (method == RecordMethod.SEX) partnerId else null,
                                location = if (method == RecordMethod.SEX && location.isNotBlank()) location.trim() else null,
                                internalEjac = if (method == RecordMethod.SEX) internalEjac else null,
                                protectionUsed = if (method == RecordMethod.SEX) protection else null,
                                usedToy = if (method == RecordMethod.MASTURBATION) usedToy else null,
                                note = note.trim().ifBlank { null }
                            )
                        )
                        saving = false
                        // 提示与返回并行，不阻塞返回
                        scope.launch { snackbarHostState.showSnackbar(if (editRecord == null) "已记录" else "已保存修改") }
                        onSaved()
                    }
                },
            enabled = !saving,
            modifier = Modifier.fillMaxWidth().height(52.dp),
            shape = RoundedCornerShape(16.dp)
        ) {
            Text(
                if (editRecord == null) "保存记录" else "保存修改",
                style = MaterialTheme.typography.titleMedium
            )
        }
    }

    if (confirmDelete && editRecord != null) {
        AlertDialog(
            onDismissRequest = { confirmDelete = false },
            title = { Text("删除这条记录？") },
            text = { Text("删除后无法恢复") },
            confirmButton = {
                TextButton(onClick = {
                    confirmDelete = false
                    saving = true
                    scope.launch {
                        app.recordRepository.delete(editRecord)
                        saving = false
                        // 提示与返回并行，删除后立即返回列表
                        scope.launch { snackbarHostState.showSnackbar("已删除") }
                        onSaved()
                    }
                }) { Text("删除", color = MaterialTheme.colorScheme.error) }
            },
            dismissButton = {
                TextButton(onClick = { confirmDelete = false }) { Text("取消") }
            }
        )
    }
}

@Composable
private fun SectionCard(title: String, content: @Composable () -> Unit) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface)
    ) {
        Column(Modifier.padding(16.dp)) {
            Text(title, style = MaterialTheme.typography.titleSmall, fontWeight = FontWeight.SemiBold)
            Spacer(Modifier.height(10.dp))
            content()
        }
    }
}

@Composable
private fun SwitchRow(label: String, checked: Boolean, onChange: (Boolean) -> Unit) {
    Row(
        Modifier.fillMaxWidth(),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.SpaceBetween
    ) {
        Text(label, style = MaterialTheme.typography.bodyLarge)
        Switch(checked = checked, onCheckedChange = onChange)
    }
}
