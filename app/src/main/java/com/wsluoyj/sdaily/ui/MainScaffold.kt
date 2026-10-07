package com.wsluoyj.sdaily.ui

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.navigationBarsPadding
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.BarChart
import androidx.compose.material.icons.outlined.CalendarMonth
import androidx.compose.material.icons.outlined.Home
import androidx.compose.material.icons.outlined.PersonOutline
import androidx.compose.material.icons.rounded.Add
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.wsluoyj.sdaily.ui.calendar.CalendarScreen
import com.wsluoyj.sdaily.ui.home.HomeScreen
import com.wsluoyj.sdaily.ui.me.AboutScreen
import com.wsluoyj.sdaily.ui.me.MeScreen
import com.wsluoyj.sdaily.ui.me.PartnerManageScreen
import com.wsluoyj.sdaily.ui.record.AddRecordScreen
import com.wsluoyj.sdaily.ui.stats.StatsScreen

private enum class Tab(val label: String, val icon: ImageVector) {
    HOME("首页", Icons.Outlined.Home),
    STATS("统计", Icons.Outlined.BarChart),
    CALENDAR("日历", Icons.Outlined.CalendarMonth),
    ME("我的", Icons.Outlined.PersonOutline)
}

private val barHeight = 68.dp

@Composable
fun MainScaffold() {
    var tab by remember { mutableStateOf(Tab.HOME) }
    var addMode by remember { mutableStateOf(false) }
    var editing by remember { mutableStateOf<com.wsluoyj.sdaily.data.local.entity.EjacRecord?>(null) }
    var showAbout by remember { mutableStateOf(false) }
    var showPartners by remember { mutableStateOf(false) }
    val snackbarHostState = remember { SnackbarHostState() }

    if (showPartners) {
        PartnerManageScreen(onBack = { showPartners = false })
        return
    }

    if (showAbout) {
        AboutScreen(onBack = { showAbout = false })
        return
    }

    Scaffold(
        snackbarHost = { SnackbarHost(snackbarHostState) },
        bottomBar = {
            BottomBar(
                current = if (addMode) null else tab,
                onTabSelected = {
                    addMode = false
                    tab = it
                },
                onAddClick = {
                    // 首次点打开记录，记录态再点则收起回首页
                    addMode = !addMode
                    if (addMode) editing = null
                    if (!addMode) tab = Tab.HOME
                }
            )
        }
    ) { innerPadding ->
        when {
            addMode -> AddRecordScreen(
                modifier = Modifier.padding(innerPadding),
                editRecord = editing,
                onSaved = {
                    addMode = false
                    editing = null
                    tab = Tab.HOME
                },
                snackbarHostState = snackbarHostState
            )
            tab == Tab.HOME -> HomeScreen(
                modifier = Modifier.padding(innerPadding),
                onRecordClick = { addMode = true },
                onEditRecord = { record ->
                    editing = record
                    addMode = true
                }
            )
            tab == Tab.STATS -> StatsScreen(Modifier.padding(innerPadding))
            tab == Tab.CALENDAR -> CalendarScreen(
                Modifier.padding(innerPadding),
                onEditRecord = { record ->
                    editing = record
                    addMode = true
                }
            )
            tab == Tab.ME -> MeScreen(
                modifier = Modifier.padding(innerPadding),
                onAboutClick = { showAbout = true },
                onPartnerManageClick = { showPartners = true },
                snackbarHostState = snackbarHostState
            )
        }
    }
}

@Composable
private fun BottomBar(
    current: Tab?,
    onTabSelected: (Tab) -> Unit,
    onAddClick: () -> Unit
) {
    val left = listOf(Tab.HOME, Tab.STATS)
    val right = listOf(Tab.CALENDAR, Tab.ME)

    Box(
        Modifier
            .fillMaxWidth()
    ) {
        Surface(
            modifier = Modifier
                .fillMaxWidth()
                .align(Alignment.BottomCenter),
            color = MaterialTheme.colorScheme.surface,
            tonalElevation = 3.dp,
            shadowElevation = 8.dp
        ) {
            Column {
                androidx.compose.foundation.layout.Row(
                    Modifier
                        .fillMaxWidth()
                        .height(barHeight)
                        .padding(horizontal = 8.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    left.forEach { BarItem(it, it == current, Modifier.weight(1f), onTabSelected) }
                    // 中间留给凸起按钮
                    Box(Modifier.weight(1f))
                    right.forEach { BarItem(it, it == current, Modifier.weight(1f), onTabSelected) }
                }
                // 让底栏背景延伸到导航栏后面，内容避开系统导航栏
                Spacer(Modifier.navigationBarsPadding())
            }
        }

        // 中间凸起的圆形记录按钮
        Surface(
            onClick = onAddClick,
            shape = CircleShape,
            color = if (current == null) MaterialTheme.colorScheme.primaryContainer
            else MaterialTheme.colorScheme.primary,
            contentColor = if (current == null) MaterialTheme.colorScheme.onPrimaryContainer
            else MaterialTheme.colorScheme.onPrimary,
            tonalElevation = 6.dp,
            shadowElevation = 10.dp,
            modifier = Modifier
                .align(Alignment.TopCenter)
                .offset(y = (-18).dp)
                .size(58.dp)
        ) {
            Box(contentAlignment = Alignment.Center) {
                Icon(
                    Icons.Rounded.Add,
                    contentDescription = "记录一次",
                    modifier = Modifier.size(30.dp)
                )
            }
        }
    }
}

@Composable
private fun BarItem(
    item: Tab,
    selected: Boolean,
    modifier: Modifier = Modifier,
    onClick: (Tab) -> Unit
) {
    Column(
        modifier = modifier
            .height(barHeight)
            .padding(top = 8.dp)
            .clickable { onClick(item) },
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Icon(
            item.icon,
            contentDescription = item.label,
            tint = if (selected) MaterialTheme.colorScheme.primary
            else MaterialTheme.colorScheme.onSurfaceVariant,
            modifier = Modifier.size(24.dp)
        )
        Text(
            item.label,
            style = MaterialTheme.typography.labelSmall,
            fontWeight = if (selected) FontWeight.SemiBold else FontWeight.Normal,
            color = if (selected) MaterialTheme.colorScheme.primary
            else MaterialTheme.colorScheme.onSurfaceVariant
        )
    }
}
