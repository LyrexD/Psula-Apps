package com.example.ui

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.shrinkVertically
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.List
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.window.Dialog
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.example.data.Goal
import com.example.data.Habit
import com.example.data.Task
import com.example.ui.theme.*
import java.text.SimpleDateFormat
import java.util.*

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun DashboardScreen(
    viewModel: DashboardViewModel,
    modifier: Modifier = Modifier
) {
    val goals by viewModel.goals.collectAsStateWithLifecycle()
    val tasks by viewModel.tasks.collectAsStateWithLifecycle()
    val habits by viewModel.habits.collectAsStateWithLifecycle()

    var showAddGoalDialog by remember { mutableStateOf(false) }
    var showAddTaskDialog by remember { mutableStateOf(false) }
    var showAddHabitDialog by remember { mutableStateOf(false) }
    var showSyncBackupDialog by remember { mutableStateOf(false) }

    // Seed demo data once so the UI isn't empty and feels incredibly refined
    LaunchedEffect(Unit) {
        viewModel.seedDemoData()
    }

    Scaffold(
        modifier = modifier.fillMaxSize(),
        topBar = {
            CenterAlignedTopAppBar(
                title = {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        val compassBgColor = if (isSystemInDarkTheme()) Color(0xFF211F26) else Color(0xFFFFFFFF)
                        val compassSouthColor = if (isSystemInDarkTheme()) Color(0xFFD0BCFF) else Color(0xFF6750A4)
                        Box(
                            modifier = Modifier
                                .size(32.dp)
                                .clip(RoundedCornerShape(8.dp))
                                .background(Brush.horizontalGradient(listOf(EmeraldPrimary, MintSecondary))),
                            contentAlignment = Alignment.Center
                        ) {
                            Canvas(modifier = Modifier.size(18.dp)) {
                                // Draw compass background circle
                                drawCircle(
                                    color = compassBgColor,
                                    radius = size.minDimension / 2f
                                )
                                // Draw the red north-pointing needle
                                val pathNorth = androidx.compose.ui.graphics.Path().apply {
                                    moveTo(size.width / 2f, 3f.dp.toPx())
                                    lineTo(size.width * 0.75f, size.height / 2f)
                                    lineTo(size.width / 2f, size.height * 0.45f)
                                    close()
                                }
                                drawPath(
                                    path = pathNorth,
                                    color = Color(0xFFB3261E) // PriorityHigh red
                                )
                                // Draw the south-pointing needle
                                val pathSouth = androidx.compose.ui.graphics.Path().apply {
                                    moveTo(size.width / 2f, size.height - 3f.dp.toPx())
                                    lineTo(size.width * 0.25f, size.height / 2f)
                                    lineTo(size.width / 2f, size.height * 0.55f)
                                    close()
                                }
                                drawPath(
                                    path = pathSouth,
                                    color = compassSouthColor
                                )
                            }
                        }
                        Spacer(modifier = Modifier.width(10.dp))
                        Text(
                            text = "Psula",
                            fontFamily = FontFamily.SansSerif,
                            fontWeight = FontWeight.Bold,
                            fontSize = 20.sp,
                            color = MaterialTheme.colorScheme.onBackground
                        )
                    }
                },
                actions = {
                    IconButton(
                        onClick = { showSyncBackupDialog = true },
                        modifier = Modifier.testTag("sync_backup_button")
                    ) {
                        Icon(
                            imageVector = Icons.Default.Refresh,
                            contentDescription = "Bulut Senkronizasyonu",
                            tint = EmeraldPrimary
                        )
                    }
                },
                colors = TopAppBarDefaults.centerAlignedTopAppBarColors(
                    containerColor = MaterialTheme.colorScheme.background
                )
            )
        },
        bottomBar = {
            NavigationBar(
                modifier = Modifier.testTag("bottom_navigation"),
                containerColor = MaterialTheme.colorScheme.surfaceVariant, // #F3EDF7 matching design HTML
                tonalElevation = 8.dp
            ) {
                NavigationBarItem(
                    selected = viewModel.selectedTab == 0,
                    onClick = { viewModel.selectedTab = 0 },
                    icon = { Icon(Icons.Default.Home, contentDescription = "Pano") },
                    label = { Text("Pano", fontSize = 11.sp, fontWeight = FontWeight.Bold) },
                    colors = NavigationBarItemDefaults.colors(
                        selectedIconColor = Color(0xFF21005D), // Dark intense purple from HTML
                        selectedTextColor = Color.White,
                        indicatorColor = Color(0xFFE8DEF8), // Pill active background from HTML
                        unselectedIconColor = MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.65f),
                        unselectedTextColor = MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.65f)
                    ),
                    modifier = Modifier.testTag("nav_pano")
                )
                NavigationBarItem(
                    selected = viewModel.selectedTab == 1,
                    onClick = { viewModel.selectedTab = 1 },
                    icon = { Icon(Icons.Default.Star, contentDescription = "Hedefler") },
                    label = { Text("Hedefler", fontSize = 11.sp, fontWeight = FontWeight.Bold) },
                    colors = NavigationBarItemDefaults.colors(
                        selectedIconColor = Color(0xFF21005D),
                        selectedTextColor = Color.White,
                        indicatorColor = Color(0xFFE8DEF8),
                        unselectedIconColor = MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.65f),
                        unselectedTextColor = MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.65f)
                    ),
                    modifier = Modifier.testTag("nav_hedefler")
                )
                NavigationBarItem(
                    selected = viewModel.selectedTab == 2,
                    onClick = { viewModel.selectedTab = 2 },
                    icon = { Icon(Icons.AutoMirrored.Filled.List, contentDescription = "Görevler") },
                    label = { Text("Görevler", fontSize = 11.sp, fontWeight = FontWeight.Bold) },
                    colors = NavigationBarItemDefaults.colors(
                        selectedIconColor = Color(0xFF21005D),
                        selectedTextColor = Color.White,
                        indicatorColor = Color(0xFFE8DEF8),
                        unselectedIconColor = MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.65f),
                        unselectedTextColor = MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.65f)
                    ),
                    modifier = Modifier.testTag("nav_gorevler")
                )
                NavigationBarItem(
                    selected = viewModel.selectedTab == 3,
                    onClick = { viewModel.selectedTab = 3 },
                    icon = { Icon(Icons.Default.DateRange, contentDescription = "Alışkanlıklar") },
                    label = { Text("Alışkanlık", fontSize = 11.sp, fontWeight = FontWeight.Bold) },
                    colors = NavigationBarItemDefaults.colors(
                        selectedIconColor = Color(0xFF21005D),
                        selectedTextColor = Color.White,
                        indicatorColor = Color(0xFFE8DEF8),
                        unselectedIconColor = MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.65f),
                        unselectedTextColor = MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.65f)
                    ),
                    modifier = Modifier.testTag("nav_aliskanliklar")
                )
            }
        },
        floatingActionButton = {
            when (viewModel.selectedTab) {
                1 -> {
                    FloatingActionButton(
                        onClick = { showAddGoalDialog = true },
                        containerColor = Color(0xFFD0BCFF), // Light purple FAB background from HTML
                        contentColor = Color(0xFF21005D), // Dark text/icon color from HTML
                        shape = RoundedCornerShape(16.dp), // rounded-2xl from HTML
                        modifier = Modifier.testTag("add_goal_fab")
                    ) {
                        Icon(Icons.Default.Add, contentDescription = "Hedef Ekle", modifier = Modifier.size(28.dp))
                    }
                }
                2 -> {
                    FloatingActionButton(
                        onClick = { showAddTaskDialog = true },
                        containerColor = Color(0xFFD0BCFF),
                        contentColor = Color(0xFF21005D),
                        shape = RoundedCornerShape(16.dp),
                        modifier = Modifier.testTag("add_task_fab")
                    ) {
                        Icon(Icons.Default.Add, contentDescription = "Görev Ekle", modifier = Modifier.size(28.dp))
                    }
                }
                3 -> {
                    FloatingActionButton(
                        onClick = { showAddHabitDialog = true },
                        containerColor = Color(0xFFD0BCFF),
                        contentColor = Color(0xFF21005D),
                        shape = RoundedCornerShape(16.dp),
                        modifier = Modifier.testTag("add_habit_fab")
                    ) {
                        Icon(Icons.Default.Add, contentDescription = "Alışkanlık Ekle", modifier = Modifier.size(28.dp))
                    }
                }
            }
        },
        contentWindowInsets = WindowInsets.safeDrawing
    ) { innerPadding ->
        Box(
            modifier = Modifier
                .padding(innerPadding)
                .fillMaxSize()
                .background(MaterialTheme.colorScheme.background)
        ) {
            when (viewModel.selectedTab) {
                0 -> PanelView(
                    goals = goals,
                    tasks = tasks,
                    habits = habits,
                    viewModel = viewModel,
                    onNavigateToTab = { id -> viewModel.selectedTab = id },
                    onSyncRequested = { showSyncBackupDialog = true }
                )
                1 -> GoalsView(
                    goals = goals,
                    viewModel = viewModel,
                    onDeleteGoal = { viewModel.deleteGoal(it) }
                )
                2 -> TasksView(
                    tasks = tasks,
                    viewModel = viewModel,
                    onDeleteTask = { viewModel.deleteTask(it) }
                )
                3 -> HabitsView(
                    habits = habits,
                    viewModel = viewModel,
                    onDeleteHabit = { viewModel.deleteHabit(it) }
                )
            }
        }
    }

    // Dialogs
    if (showAddGoalDialog) {
        AddGoalDialog(
            onDismiss = { showAddGoalDialog = false },
            onConfirm = { title, desc, cat, date, targetVal ->
                viewModel.addGoal(title, desc, cat, date, targetVal, 0f)
                showAddGoalDialog = false
            }
        )
    }

    if (showAddTaskDialog) {
        AddTaskDialog(
            goals = goals,
            onDismiss = { showAddTaskDialog = false },
            onConfirm = { title, desc, priority, date, goalId ->
                viewModel.addTask(title, desc, priority, date, goalId)
                showAddTaskDialog = false
            }
        )
    }

    if (showAddHabitDialog) {
        AddHabitDialog(
            onDismiss = { showAddHabitDialog = false },
            onConfirm = { title, cat, freq ->
                viewModel.addHabit(title, cat, freq)
                showAddHabitDialog = false
            }
        )
    }

    if (showSyncBackupDialog) {
        SyncBackupDialog(
            viewModel = viewModel,
            onDismiss = { showSyncBackupDialog = false }
        )
    }
}

// ==================== VIEW 1: PANELVIEW (DASHBOARD) ====================
@Composable
fun PanelView(
    goals: List<Goal>,
    tasks: List<Task>,
    habits: List<Habit>,
    viewModel: DashboardViewModel,
    onNavigateToTab: (Int) -> Unit,
    onSyncRequested: () -> Unit
) {
    val todayPendingTasks = remember(tasks) {
        tasks.filter { !it.isCompleted } // simple demo filter, active tasks
    }
    
    val completedTasksCount = remember(tasks) { tasks.count { it.isCompleted } }
    val totalTasksCount = remember(tasks) { tasks.size }
    val taskProgressFraction = if (totalTasksCount > 0) completedTasksCount.toFloat() / totalTasksCount else 0f

    val activeGoals = remember(goals) { goals.filter { !it.isCompleted } }
    val completedGoalsCount = remember(goals) { goals.count { it.isCompleted } }

    val motivationIndex = remember { (0..4).random() }
    val dynamicGreeting = remember {
        val hour = Calendar.getInstance().get(Calendar.HOUR_OF_DAY)
        when {
            hour < 12 -> "Harika Bir Sabah!"
            hour < 18 -> "Verimli Günler!"
            else -> "Huzurlu Akşamlar!"
        }
    }

    val motivationSentence = when (motivationIndex) {
        0 -> "Küçük adımlar, büyük hedeflere giden yolu inşa eder."
        1 -> "Bugün yapacağın her görev, yarınki başarının temelidir."
        2 -> "İstikrar, yeteneği disipline dönüştüren sihirli güçtür."
        3 -> "Görevlerini birer sorumluluk değil, gelişim basamağı olarak gör."
        else -> "Kendinle yarış, her gün dünden bir adım daha ileri git."
    }

    LazyColumn(
        modifier = Modifier
            .fillMaxSize()
            .testTag("panel_view"),
        contentPadding = PaddingValues(16.dp),
        verticalArrangement = Arrangement.spacedBy(20.dp)
    ) {
        // Dynamic Greeting & Professional Polish Summary Header
        item {
            Card(
                modifier = Modifier
                    .fillMaxWidth()
                    .shadow(4.dp, RoundedCornerShape(24.dp)), // rounded-3xl
                shape = RoundedCornerShape(24.dp),
                colors = CardDefaults.cardColors(
                    containerColor = if (isSystemInDarkTheme()) Color(0xFF2B2930) else Color(0xFFEADDFF), // EADDFF light bg or M3 dark Elevated
                    contentColor = if (isSystemInDarkTheme()) Color(0xFFE6E1E5) else Color(0xFF21005D) // 21005D intense dark purple or dark text
                )
            ) {
                Column(
                    modifier = Modifier
                        .padding(20.dp)
                        .fillMaxWidth()
                ) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.Top
                    ) {
                        Column {
                            Text(
                                text = "Merhaba, $dynamicGreeting 👋",
                                fontSize = 14.sp,
                                color = if (isSystemInDarkTheme()) Color(0xFFCCC2DC) else Color(0xFF21005D).copy(alpha = 0.8f),
                                fontWeight = FontWeight.Bold
                            )
                            Spacer(modifier = Modifier.height(4.dp))
                            Text(
                                text = "Günlük Özet 📈",
                                fontSize = 24.sp,
                                fontWeight = FontWeight.Black,
                                color = if (isSystemInDarkTheme()) Color(0xFFE6E1E5) else Color(0xFF21005D)
                            )
                        }
                        
                        // Custom styled circular tracking badge
                        Box(
                            modifier = Modifier
                                .size(48.dp)
                                .background(
                                    if (isSystemInDarkTheme()) Color(0xFF141218) else Color(0xFFFFFFFF).copy(alpha = 0.4f),
                                    RoundedCornerShape(12.dp)
                                ),
                            contentAlignment = Alignment.Center
                        ) {
                            Icon(
                                imageVector = Icons.Default.Star,
                                contentDescription = "Trend",
                                tint = if (isSystemInDarkTheme()) Color(0xFFD0BCFF) else Color(0xFF21005D),
                                modifier = Modifier.size(24.dp)
                            )
                        }
                    }
                    
                    Spacer(modifier = Modifier.height(16.dp))
                    
                    // Linear progress gauge
                    val animatedProgress by animateFloatAsState(targetValue = taskProgressFraction, label = "progress")
                    val progressPercentage = (taskProgressFraction * 100).toInt()
                    
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.Bottom
                    ) {
                        Text(
                            text = "%$progressPercentage Başarı",
                            fontSize = 24.sp,
                            fontWeight = FontWeight.Bold,
                            color = if (isSystemInDarkTheme()) Color(0xFFD0BCFF) else Color(0xFF6750A4)
                        )
                        Text(
                            text = "$completedTasksCount / $totalTasksCount Tamamlandı",
                            fontSize = 12.sp,
                            fontWeight = FontWeight.Bold,
                            color = if (isSystemInDarkTheme()) Color(0xFFE6E1E5).copy(alpha = 0.7f) else Color(0xFF49454F)
                        )
                    }
                    
                    Spacer(modifier = Modifier.height(8.dp))
                    
                    LinearProgressIndicator(
                        progress = { animatedProgress },
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(10.dp)
                            .clip(RoundedCornerShape(5.dp)),
                        color = if (isSystemInDarkTheme()) Color(0xFFD0BCFF) else Color(0xFF6750A4), // 6750A4 primary purple
                        trackColor = if (isSystemInDarkTheme()) Color(0xFF211F26) else Color(0xFFD0BCFF) // EADDFF light purple progress bar trail
                    )
                    
                    Spacer(modifier = Modifier.height(14.dp))
                    
                    Text(
                        text = "Bugün için belirlenen $totalTasksCount görev içerisinden $completedTasksCount tanesi başarıyla tamamlandı. Harika bir ritim yakaladın!",
                        fontSize = 13.sp,
                        color = if (isSystemInDarkTheme()) Color(0xFF938F99) else Color(0xFF49454F),
                        lineHeight = 18.sp
                    )
                }
            }
        }

        // Stats Row Widget (Quick Asymmetric visual style)
        item {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                // Goal stat
                Card(
                    modifier = Modifier
                        .weight(1f)
                        .clickable { onNavigateToTab(1) },
                    shape = RoundedCornerShape(12.dp),
                    colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceVariant), // #F3EDF7 or Dark elevated
                    border = BorderStroke(1.dp, if (isSystemInDarkTheme()) Color(0xFF2B2930) else Color(0xFFCAC4D0)) // #CAC4D0 thin border
                ) {
                    Column(modifier = Modifier.padding(14.dp)) {
                        Text("Hedefler", fontSize = 12.sp, color = if (isSystemInDarkTheme()) Color(0xFF938F99) else Color(0xFF49454F), fontWeight = FontWeight.Bold)
                        Spacer(modifier = Modifier.height(6.dp))
                        Row(verticalAlignment = Alignment.Bottom) {
                            Text(
                                text = "$completedGoalsCount",
                                fontSize = 28.sp,
                                fontWeight = FontWeight.Black,
                                color = EmeraldPrimary
                            )
                            Text(
                                text = " / ${goals.size} Tamam",
                                fontSize = 12.sp,
                                fontWeight = FontWeight.Bold,
                                color = if (isSystemInDarkTheme()) Color(0xFF938F99) else Color(0xFF49454F),
                                modifier = Modifier.padding(bottom = 4.dp, start = 4.dp)
                            )
                        }
                    }
                }
                
                // Habit Streak stat
                val highestStreak = remember(habits) { habits.maxOfOrNull { it.streak } ?: 0 }
                Card(
                    modifier = Modifier
                        .weight(1f)
                        .clickable { onNavigateToTab(3) },
                    shape = RoundedCornerShape(12.dp),
                    colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceVariant),
                    border = BorderStroke(1.dp, if (isSystemInDarkTheme()) Color(0xFF2B2930) else Color(0xFFCAC4D0))
                ) {
                    Column(modifier = Modifier.padding(14.dp)) {
                        Text("En İyi Seri", fontSize = 12.sp, color = if (isSystemInDarkTheme()) Color(0xFF938F99) else Color(0xFF49454F), fontWeight = FontWeight.Bold)
                        Spacer(modifier = Modifier.height(6.dp))
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Text(
                                text = "$highestStreak",
                                fontSize = 28.sp,
                                fontWeight = FontWeight.Black,
                                color = PriorityHigh
                            )
                            Spacer(modifier = Modifier.width(4.dp))
                            Text(
                                text = "Gün 🔥",
                                fontSize = 14.sp,
                                fontWeight = FontWeight.Bold,
                                color = PriorityHigh
                            )
                        }
                    }
                }
            }
        }

        // Active Goals Section
        item {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = "Aktif Hedeflerim 🎯",
                    fontSize = 18.sp,
                    fontWeight = FontWeight.Bold,
                    color = MaterialTheme.colorScheme.onBackground
                )
                TextButton(
                    onClick = { onNavigateToTab(1) },
                    modifier = Modifier.testTag("expand_goals")
                ) {
                    Text("Tümünü Gör", color = EmeraldPrimary, fontWeight = FontWeight.Bold)
                }
            }
            
            if (activeGoals.isEmpty()) {
                Surface(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(top = 8.dp),
                    shape = RoundedCornerShape(12.dp),
                    color = MaterialTheme.colorScheme.surface.copy(alpha = 0.5f),
                    border = BorderStroke(1.dp, MaterialTheme.colorScheme.surface)
                ) {
                    Text(
                        text = "Henüz aktif bir hedef belirlenmemiş.\nDüşün, karar ver ve yeni bir hedef ekle!",
                        fontSize = 13.sp,
                        color = TextSecondaryDark,
                        textAlign = TextAlign.Center,
                        modifier = Modifier.padding(24.dp)
                    )
                }
            } else {
                LazyRow(
                    horizontalArrangement = Arrangement.spacedBy(12.dp),
                    contentPadding = PaddingValues(vertical = 8.dp)
                ) {
                    items(activeGoals) { goal ->
                        Card(
                            modifier = Modifier
                                .width(220.dp)
                                .shadow(2.dp, RoundedCornerShape(12.dp))
                                .clickable { onNavigateToTab(1) },
                            shape = RoundedCornerShape(12.dp),
                            colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceVariant), // #F3EDF7 surface Variant
                            border = BorderStroke(1.dp, if (isSystemInDarkTheme()) Color(0xFF2B2930) else Color(0xFFCAC4D0)) // #CAC4D0 outline border
                        ) {
                            Column(modifier = Modifier.padding(14.dp)) {
                                Text(
                                    text = goal.category,
                                    fontSize = 10.sp,
                                    fontWeight = FontWeight.Bold,
                                    color = if (isSystemInDarkTheme()) Color(0xFFD0BCFF) else Color(0xFF6750A4),
                                    modifier = Modifier
                                        .background(
                                            if (isSystemInDarkTheme()) Color(0xFF6750A4).copy(alpha = 0.3f) else Color(0xFFEADDFF),
                                            RoundedCornerShape(4.dp)
                                        )
                                        .padding(horizontal = 6.dp, vertical = 2.dp)
                                )
                                Spacer(modifier = Modifier.height(10.dp))
                                Text(
                                    text = goal.title,
                                    fontSize = 15.sp,
                                    fontWeight = FontWeight.Bold,
                                    maxLines = 1,
                                    overflow = TextOverflow.Ellipsis,
                                    color = MaterialTheme.colorScheme.onSurface
                                )
                                Text(
                                    text = goal.description,
                                    fontSize = 12.sp,
                                    color = if (isSystemInDarkTheme()) Color(0xFF938F99) else Color(0xFF49454F),
                                    maxLines = 2,
                                    overflow = TextOverflow.Ellipsis,
                                    modifier = Modifier.height(34.dp)
                                )
                                Spacer(modifier = Modifier.height(10.dp))
                                
                                val goalPercent = if (goal.targetValue > 0f) goal.currentValue / goal.targetValue else 0f
                                Row(
                                    modifier = Modifier.fillMaxWidth(),
                                    horizontalArrangement = Arrangement.SpaceBetween
                                ) {
                                    Text(
                                        text = "${goal.currentValue.toInt()} / ${goal.targetValue.toInt()}",
                                        fontSize = 11.sp,
                                        fontWeight = FontWeight.Bold,
                                        color = if (isSystemInDarkTheme()) Color(0xFFD0BCFF) else Color(0xFF6750A4)
                                    )
                                    Text(
                                        text = "${(goalPercent * 100).toInt()}%",
                                        fontSize = 11.sp,
                                        fontWeight = FontWeight.Bold,
                                        color = if (isSystemInDarkTheme()) Color(0xFF938F99) else Color(0xFF49454F)
                                    )
                                }
                                Spacer(modifier = Modifier.height(4.dp))
                                LinearProgressIndicator(
                                    progress = { goalPercent },
                                    modifier = Modifier
                                        .fillMaxWidth()
                                        .height(6.dp)
                                        .clip(RoundedCornerShape(3.dp)),
                                    color = if (isSystemInDarkTheme()) Color(0xFFD0BCFF) else Color(0xFF6750A4),
                                    trackColor = if (isSystemInDarkTheme()) Color(0xFF211F26) else Color(0xFFE8DEF8)
                                )
                            }
                        }
                    }
                }
            }
        }

        // Today's Checklist Tasks Section
        item {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = "Yapılacak Görevler Planı 📝",
                    fontSize = 18.sp,
                    fontWeight = FontWeight.Bold,
                    color = MaterialTheme.colorScheme.onBackground
                )
                TextButton(onClick = { onNavigateToTab(2) }) {
                    Text("Tümünü Gör", color = EmeraldPrimary, fontWeight = FontWeight.Bold)
                }
            }

            if (todayPendingTasks.isEmpty()) {
                Surface(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(top = 8.dp),
                    shape = RoundedCornerShape(12.dp),
                    color = MaterialTheme.colorScheme.surface.copy(alpha = 0.5f),
                    border = BorderStroke(1.dp, MaterialTheme.colorScheme.surface)
                ) {
                    Column(
                        modifier = Modifier.padding(24.dp),
                        horizontalAlignment = Alignment.CenterHorizontally
                    ) {
                        Icon(
                            imageVector = Icons.Default.CheckCircle,
                            contentDescription = "Hepsi Bitti",
                            tint = EmeraldPrimary,
                            modifier = Modifier.size(36.dp)
                        )
                        Spacer(modifier = Modifier.height(8.dp))
                        Text(
                            text = "Harika! Yapılacak iş kalmadı.\nYeni bir görev planlayarak ritmini koru.",
                            fontSize = 13.sp,
                            color = TextSecondaryDark,
                            textAlign = TextAlign.Center
                        )
                    }
                }
            }
        }

        // Show standard quick task toggling in dashboard!
        items(todayPendingTasks.take(4)) { task ->
            Card(
                modifier = Modifier
                    .fillMaxWidth()
                    .testTag("dashboard_task_item_${task.id}")
                    .clickable { viewModel.toggleTaskCompletion(task) },
                shape = RoundedCornerShape(12.dp),
                colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface)
            ) {
                Row(
                    modifier = Modifier
                        .padding(14.dp)
                        .fillMaxWidth(),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    val checkboxTag = "checkbox_${task.id}"
                    Checkbox(
                        checked = task.isCompleted,
                        onCheckedChange = { viewModel.toggleTaskCompletion(task) },
                        colors = CheckboxDefaults.colors(checkedColor = EmeraldPrimary),
                        modifier = Modifier.testTag(checkboxTag)
                    )
                    Spacer(modifier = Modifier.width(8.dp))
                    Column(modifier = Modifier.weight(1f)) {
                        Text(
                            text = task.title,
                            fontSize = 15.sp,
                            fontWeight = FontWeight.Bold,
                            color = MaterialTheme.colorScheme.onSurface
                        )
                        if (task.description.isNotBlank()) {
                            Text(
                                text = task.description,
                                fontSize = 12.sp,
                                color = TextSecondaryDark,
                                maxLines = 1,
                                overflow = TextOverflow.Ellipsis
                            )
                        }
                    }
                    
                    val priorityColor = when (task.priority) {
                        "Yüksek" -> PriorityHigh
                        "Orta" -> PriorityMedium
                        else -> PriorityLow
                    }
                    
                    Text(
                        text = task.priority,
                        fontSize = 10.sp,
                        fontWeight = FontWeight.Black,
                        color = priorityColor,
                        modifier = Modifier
                            .border(1.dp, priorityColor.copy(alpha = 0.5f), RoundedCornerShape(4.dp))
                            .padding(horizontal = 6.dp, vertical = 2.dp)
                    )
                }
            }
        }

        // Daily Habits Block Quick Toggle
        item {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = "Günün Alışkanlıkları 🌱",
                    fontSize = 18.sp,
                    fontWeight = FontWeight.Bold,
                    color = MaterialTheme.colorScheme.onBackground
                )
                TextButton(onClick = { onNavigateToTab(3) }) {
                    Text("Tümünü Gör", color = EmeraldPrimary, fontWeight = FontWeight.Bold)
                }
            }

            if (habits.isEmpty()) {
                Surface(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(top = 8.dp),
                    shape = RoundedCornerShape(12.dp),
                    color = MaterialTheme.colorScheme.surface.copy(alpha = 0.5f),
                    border = BorderStroke(1.dp, MaterialTheme.colorScheme.surface)
                ) {
                    Text(
                        text = "Henüz bir alışkanlık oluşturulmamış.",
                        fontSize = 13.sp,
                        color = TextSecondaryDark,
                        textAlign = TextAlign.Center,
                        modifier = Modifier.padding(24.dp)
                    )
                }
            }
        }

        // Standard habit list displays directly
        items(habits.take(3)) { habit ->
            val sdf = SimpleDateFormat("yyyy-MM-dd", Locale.getDefault())
            val todayStr = sdf.format(Date())
            val datesList = habit.completedDates.split(",").map { it.trim() }
            val isCompletedToday = datesList.contains(todayStr)

            Card(
                modifier = Modifier
                    .fillMaxWidth()
                    .testTag("dashboard_habit_item_${habit.id}"),
                shape = RoundedCornerShape(12.dp),
                colors = CardDefaults.cardColors(
                    containerColor = if (isCompletedToday) {
                        EmeraldPrimary.copy(alpha = 0.08f)
                    } else {
                        MaterialTheme.colorScheme.surface
                    }
                ),
                border = if (isCompletedToday) BorderStroke(1.dp, EmeraldPrimary.copy(alpha = 0.3f)) else null
            ) {
                Row(
                    modifier = Modifier
                        .padding(14.dp)
                        .fillMaxWidth(),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    Column(modifier = Modifier.weight(1f)) {
                        Text(
                            text = habit.title,
                            fontSize = 15.sp,
                            fontWeight = FontWeight.Bold,
                            color = MaterialTheme.colorScheme.onSurface
                        )
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Text(
                                text = habit.category,
                                fontSize = 11.sp,
                                color = TextSecondaryDark
                            )
                            Spacer(modifier = Modifier.width(10.dp))
                            Icon(
                                imageVector = Icons.Default.Star,
                                contentDescription = "Seri",
                                tint = PriorityMedium,
                                modifier = Modifier.size(14.dp)
                            )
                            Spacer(modifier = Modifier.width(3.dp))
                            Text(
                                text = "${habit.streak} Gün Seri",
                                fontSize = 11.sp,
                                fontWeight = FontWeight.Bold,
                                color = PriorityMedium
                            )
                        }
                    }
                    
                    Button(
                        onClick = { viewModel.toggleHabitCompletion(habit) },
                        colors = ButtonDefaults.buttonColors(
                            containerColor = if (isCompletedToday) Color(0xFF334155) else EmeraldPrimary,
                            contentColor = if (isCompletedToday) TextPrimaryDark else DarkBg
                        ),
                        shape = RoundedCornerShape(8.dp),
                        contentPadding = PaddingValues(horizontal = 12.dp, vertical = 6.dp),
                        modifier = Modifier.testTag("toggle_habit_${habit.id}")
                    ) {
                        Text(
                            text = if (isCompletedToday) "Yapıldı ✓" else "Tamamla",
                            fontSize = 12.sp,
                            fontWeight = FontWeight.Bold
                        )
                    }
                }
            }
        }
    }
}

// ==================== VIEW 2: GOALSVIEW ====================
@Composable
fun GoalsView(
    goals: List<Goal>,
    viewModel: DashboardViewModel,
    onDeleteGoal: (Goal) -> Unit
) {
    val categories = listOf("Hepsi", "Kariyer", "Sağlık", "Kişisel Gelişim", "Finans")
    
    val filteredGoals = remember(goals, viewModel.goalCategoryFilter) {
        val filter = viewModel.goalCategoryFilter
        if (filter == null || filter == "Hepsi") {
            goals
        } else {
            goals.filter { it.category == filter }
        }
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .testTag("goals_view")
    ) {
        // Category filters horizontal row
        LazyRow(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 16.dp, vertical = 12.dp),
            horizontalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            items(categories) { category ->
                val isSelected = (viewModel.goalCategoryFilter ?: "Hepsi") == category
                FilterChip(
                    selected = isSelected,
                    onClick = {
                        viewModel.goalCategoryFilter = if (category == "Hepsi") null else category
                    },
                    label = { Text(category, fontWeight = FontWeight.Bold) },
                    colors = FilterChipDefaults.filterChipColors(
                        selectedContainerColor = EmeraldPrimary,
                        selectedLabelColor = DarkBg,
                        containerColor = MaterialTheme.colorScheme.surface,
                        labelColor = TextSecondaryDark
                    ),
                    modifier = Modifier.testTag("goal_chip_$category")
                )
            }
        }

        if (filteredGoals.isEmpty()) {
            Box(
                modifier = Modifier
                    .weight(1f)
                    .fillMaxWidth(),
                contentAlignment = Alignment.Center
            ) {
                Column(
                    horizontalAlignment = Alignment.CenterHorizontally,
                    modifier = Modifier.padding(32.dp)
                ) {
                    Icon(
                        imageVector = Icons.Default.Star,
                        contentDescription = "Hedef Yok",
                        tint = TextSecondaryDark,
                        modifier = Modifier.size(64.dp)
                    )
                    Spacer(modifier = Modifier.height(16.dp))
                    Text(
                        text = "Seçilen kategoride hedef bulunmamaktadır.\nYeni bir hedef belirleyip gelişime başla!",
                        fontSize = 15.sp,
                        color = TextSecondaryDark,
                        textAlign = TextAlign.Center
                    )
                }
            }
        } else {
            LazyColumn(
                modifier = Modifier
                    .weight(1f)
                    .fillMaxWidth(),
                contentPadding = PaddingValues(16.dp),
                verticalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                items(filteredGoals) { goal ->
                    Card(
                        modifier = Modifier
                            .fillMaxWidth()
                            .testTag("goal_item_${goal.id}"),
                        shape = RoundedCornerShape(14.dp),
                        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface)
                    ) {
                        Column(modifier = Modifier.padding(16.dp)) {
                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.SpaceBetween,
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Text(
                                    text = goal.category,
                                    fontSize = 11.sp,
                                    fontWeight = FontWeight.Bold,
                                    color = EmeraldPrimary,
                                    modifier = Modifier
                                        .background(EmeraldPrimary.copy(alpha = 0.12f), RoundedCornerShape(4.dp))
                                        .padding(horizontal = 8.dp, vertical = 4.dp)
                                )
                                Row(verticalAlignment = Alignment.CenterVertically) {
                                    val dateStr = SimpleDateFormat("dd MMM yyyy", Locale.getDefault()).format(Date(goal.targetDate))
                                    Text(
                                        text = dateStr,
                                        fontSize = 11.sp,
                                        color = TextSecondaryDark,
                                        fontWeight = FontWeight.Medium
                                    )
                                    Spacer(modifier = Modifier.width(10.dp))
                                    IconButton(
                                        onClick = { onDeleteGoal(goal) },
                                        modifier = Modifier
                                            .size(24.dp)
                                            .testTag("delete_goal_${goal.id}")
                                    ) {
                                        Icon(
                                            imageVector = Icons.Default.Delete,
                                            contentDescription = "Hedef Sil",
                                            tint = PriorityHigh,
                                            modifier = Modifier.size(18.dp)
                                        )
                                    }
                                }
                            }
                            
                            Spacer(modifier = Modifier.height(12.dp))
                            Text(
                                text = goal.title,
                                fontSize = 18.sp,
                                fontWeight = FontWeight.Bold,
                                color = MaterialTheme.colorScheme.onSurface
                            )
                            if (goal.description.isNotBlank()) {
                                Text(
                                    text = goal.description,
                                    fontSize = 13.sp,
                                    color = TextSecondaryDark,
                                    modifier = Modifier.padding(top = 4.dp)
                                )
                            }
                            
                            Spacer(modifier = Modifier.height(16.dp))
                            
                            // Meter Goals: progress incrementer slider! Satisfies highly polished touch ergonomics
                            val goalPercent = if (goal.targetValue > 0f) goal.currentValue / goal.targetValue else 0f
                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.SpaceBetween,
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Text(
                                    text = "İlerleme Durumu",
                                    fontSize = 13.sp,
                                    fontWeight = FontWeight.Bold,
                                    color = MaterialTheme.colorScheme.onSurface
                                )
                                Text(
                                    text = "${goal.currentValue.toInt()} / ${goal.targetValue.toInt()} (${(goalPercent * 100).toInt()}%)",
                                    fontSize = 13.sp,
                                    fontWeight = FontWeight.Bold,
                                    color = EmeraldPrimary
                                )
                            }
                            
                            Spacer(modifier = Modifier.height(8.dp))
                            
                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Button(
                                    onClick = { viewModel.updateGoalProgress(goal, goal.currentValue - 1f) },
                                    enabled = goal.currentValue > 0f,
                                    colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF334155)),
                                    contentPadding = PaddingValues(0.dp),
                                    modifier = Modifier
                                        .size(36.dp)
                                        .clip(CircleShape)
                                        .testTag("goal_dec_${goal.id}")
                                ) {
                                    Text("-", fontSize = 18.sp, fontWeight = FontWeight.Bold, color = TextPrimaryDark)
                                }
                                
                                Slider(
                                    value = goal.currentValue,
                                    onValueChange = { viewModel.updateGoalProgress(goal, it) },
                                    valueRange = 0f..(if (goal.targetValue > 0f) goal.targetValue else 100f),
                                    modifier = Modifier
                                        .weight(1f)
                                        .padding(horizontal = 12.dp)
                                        .testTag("goal_slider_${goal.id}"),
                                    colors = SliderDefaults.colors(
                                        activeTrackColor = EmeraldPrimary,
                                        thumbColor = MintSecondary
                                    )
                                )
                                
                                Button(
                                    onClick = { viewModel.updateGoalProgress(goal, goal.currentValue + 1f) },
                                    enabled = goal.currentValue < goal.targetValue,
                                    colors = ButtonDefaults.buttonColors(containerColor = EmeraldPrimary),
                                    contentPadding = PaddingValues(0.dp),
                                    modifier = Modifier
                                        .size(36.dp)
                                        .clip(CircleShape)
                                        .testTag("goal_inc_${goal.id}")
                                ) {
                                    Text("+", fontSize = 18.sp, fontWeight = FontWeight.Bold, color = DarkBg)
                                }
                            }
                        }
                    }
                }
            }
        }
    }
}

// ==================== VIEW 3: TASKSVIEW ====================
@Composable
fun TasksView(
    tasks: List<Task>,
    viewModel: DashboardViewModel,
    onDeleteTask: (Task) -> Unit
) {
    val priorities = listOf("Hepsi", "Yüksek", "Orta", "Düşük")
    val filteredTasks = remember(tasks, viewModel.taskPriorityFilter) {
        val filter = viewModel.taskPriorityFilter
        if (filter == null || filter == "Hepsi") {
            tasks
        } else {
            tasks.filter { it.priority == filter }
        }
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .testTag("tasks_view")
    ) {
        // Priority chips options
        LazyRow(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 16.dp, vertical = 12.dp),
            horizontalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            items(priorities) { priority ->
                val isSelected = (viewModel.taskPriorityFilter ?: "Hepsi") == priority
                FilterChip(
                    selected = isSelected,
                    onClick = {
                        viewModel.taskPriorityFilter = if (priority == "Hepsi") null else priority
                    },
                    label = { Text(priority, fontWeight = FontWeight.Bold) },
                    colors = FilterChipDefaults.filterChipColors(
                        selectedContainerColor = EmeraldPrimary,
                        selectedLabelColor = DarkBg,
                        containerColor = MaterialTheme.colorScheme.surface,
                        labelColor = TextSecondaryDark
                    ),
                    modifier = Modifier.testTag("task_chip_$priority")
                )
            }
        }

        if (filteredTasks.isEmpty()) {
            Box(
                modifier = Modifier
                    .weight(1f)
                    .fillMaxWidth(),
                contentAlignment = Alignment.Center
            ) {
                Column(
                    horizontalAlignment = Alignment.CenterHorizontally,
                    modifier = Modifier.padding(32.dp)
                ) {
                    Icon(
                        imageVector = Icons.Default.List,
                        contentDescription = "Görev Yok",
                        tint = TextSecondaryDark,
                        modifier = Modifier.size(64.dp)
                    )
                    Spacer(modifier = Modifier.height(16.dp))
                    Text(
                        text = "Seçilen öncelikte planlanmış görev yok.\nRitmini koru, yeni bir tane oluştur!",
                        fontSize = 15.sp,
                        color = TextSecondaryDark,
                        textAlign = TextAlign.Center
                    )
                }
            }
        } else {
            LazyColumn(
                modifier = Modifier
                    .weight(1f)
                    .fillMaxWidth(),
                contentPadding = PaddingValues(16.dp),
                verticalArrangement = Arrangement.spacedBy(10.dp)
            ) {
                items(filteredTasks) { task ->
                    Card(
                        modifier = Modifier
                            .fillMaxWidth()
                            .testTag("task_card_${task.id}")
                            .clickable { viewModel.toggleTaskCompletion(task) },
                        shape = RoundedCornerShape(12.dp),
                        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface)
                    ) {
                        Row(
                            modifier = Modifier
                                .padding(14.dp)
                                .fillMaxWidth(),
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Checkbox(
                                checked = task.isCompleted,
                                onCheckedChange = { viewModel.toggleTaskCompletion(task) },
                                colors = CheckboxDefaults.colors(checkedColor = EmeraldPrimary),
                                modifier = Modifier.testTag("checkbox_task_${task.id}")
                            )
                            Spacer(modifier = Modifier.width(10.dp))
                            Column(modifier = Modifier.weight(1f)) {
                                Text(
                                    text = task.title,
                                    fontSize = 16.sp,
                                    fontWeight = FontWeight.Bold,
                                    color = if (task.isCompleted) TextSecondaryDark else MaterialTheme.colorScheme.onSurface,
                                    maxLines = 1,
                                    overflow = TextOverflow.Ellipsis
                                )
                                if (task.description.isNotBlank()) {
                                    Text(
                                        text = task.description,
                                        fontSize = 13.sp,
                                        color = TextSecondaryDark,
                                        maxLines = 2,
                                        overflow = TextOverflow.Ellipsis
                                    )
                                }
                                Spacer(modifier = Modifier.height(4.dp))
                                val dateStr = SimpleDateFormat("dd MMM yyyy", Locale.getDefault()).format(Date(task.dueDate))
                                Text(
                                    text = "Son Tarih: $dateStr",
                                    fontSize = 11.sp,
                                    color = EmeraldPrimary,
                                    fontWeight = FontWeight.Bold
                                )
                            }
                            
                            val priorityColor = when (task.priority) {
                                "Yüksek" -> PriorityHigh
                                "Orta" -> PriorityMedium
                                else -> PriorityLow
                            }
                            
                            Column(
                                horizontalAlignment = Alignment.End,
                                verticalArrangement = Arrangement.spacedBy(8.dp)
                            ) {
                                IconButton(
                                    onClick = { onDeleteTask(task) },
                                    modifier = Modifier
                                        .size(24.dp)
                                        .testTag("delete_task_${task.id}")
                                ) {
                                    Icon(
                                        imageVector = Icons.Default.Delete,
                                        contentDescription = "Görev Sil",
                                        tint = PriorityHigh,
                                        modifier = Modifier.size(18.dp)
                                    )
                                }
                                Text(
                                    text = task.priority,
                                    fontSize = 10.sp,
                                    fontWeight = FontWeight.Black,
                                    color = priorityColor,
                                    modifier = Modifier
                                        .border(1.dp, priorityColor.copy(alpha = 0.5f), RoundedCornerShape(4.dp))
                                        .padding(horizontal = 6.dp, vertical = 2.dp)
                                )
                            }
                        }
                    }
                }
            }
        }
    }
}

// ==================== VIEW 4: HABITSVIEW ====================
@Composable
fun HabitsView(
    habits: List<Habit>,
    viewModel: DashboardViewModel,
    onDeleteHabit: (Habit) -> Unit
) {
    if (habits.isEmpty()) {
        Box(
            modifier = Modifier
                .fillMaxSize()
                .testTag("habits_view"),
            contentAlignment = Alignment.Center
        ) {
            Column(
                horizontalAlignment = Alignment.CenterHorizontally,
                modifier = Modifier.padding(32.dp)
            ) {
                Icon(
                    imageVector = Icons.Default.DateRange,
                    contentDescription = "Alışkanlık Yok",
                    tint = TextSecondaryDark,
                    modifier = Modifier.size(64.dp)
                )
                Spacer(modifier = Modifier.height(16.dp))
                Text(
                    text = "Henüz bir alışkanlık eklenmemiş.\nHer gün düzenli yapacağın aktiviteleri buraya ekle!",
                    fontSize = 15.sp,
                    color = TextSecondaryDark,
                    textAlign = TextAlign.Center
                )
            }
        }
    } else {
        LazyColumn(
            modifier = Modifier
                .fillMaxSize()
                .testTag("habits_view"),
            contentPadding = PaddingValues(16.dp),
            verticalArrangement = Arrangement.spacedBy(14.dp)
        ) {
            items(habits) { habit ->
                val sdf = SimpleDateFormat("yyyy-MM-dd", Locale.getDefault())
                val todayStr = sdf.format(Date())
                val datesList = habit.completedDates.split(",").map { it.trim() }.filter { it.isNotBlank() }
                val isCompletedToday = datesList.contains(todayStr)

                Card(
                    modifier = Modifier
                        .fillMaxWidth()
                        .testTag("habit_card_${habit.id}"),
                    shape = RoundedCornerShape(14.dp),
                    colors = CardDefaults.cardColors(
                        containerColor = if (isCompletedToday) {
                            EmeraldPrimary.copy(alpha = 0.08f)
                        } else {
                            MaterialTheme.colorScheme.surface
                        }
                    ),
                    border = if (isCompletedToday) BorderStroke(1.dp, EmeraldPrimary.copy(alpha = 0.3f)) else null
                ) {
                    Column(modifier = Modifier.padding(16.dp)) {
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Column {
                                Text(
                                    text = habit.title,
                                    fontSize = 18.sp,
                                    fontWeight = FontWeight.Bold,
                                    color = MaterialTheme.colorScheme.onSurface
                                )
                                Text(
                                    text = "Kategori: ${habit.category} • Frekans: ${habit.frequency}",
                                    fontSize = 12.sp,
                                    color = TextSecondaryDark,
                                    modifier = Modifier.padding(top = 2.dp)
                                )
                            }
                            
                            IconButton(
                                onClick = { onDeleteHabit(habit) },
                                modifier = Modifier
                                    .size(28.dp)
                                    .testTag("delete_habit_${habit.id}")
                            ) {
                                Icon(
                                    imageVector = Icons.Default.Delete,
                                    contentDescription = "Alışkanlık Sil",
                                    tint = PriorityHigh,
                                    modifier = Modifier.size(18.dp)
                                )
                            }
                        }

                        Spacer(modifier = Modifier.height(14.dp))
                        
                        // Last 7 days visual consistency matrix (Custom elegant drawing block)
                        Text(
                            text = "Son 7 Günlük Tutarlılık",
                            fontSize = 12.sp,
                            fontWeight = FontWeight.Bold,
                            color = MaterialTheme.colorScheme.onSurface,
                            modifier = Modifier.padding(bottom = 6.dp)
                        )
                        
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween
                        ) {
                            // Calculate last 7 days and draw visual indicator tiles!
                            val calendar = Calendar.getInstance()
                            val displayList = remember { mutableStateListOf<Pair<String, Boolean>>() }
                            displayList.clear()
                            
                            for (i in 6 downTo 0) {
                                val calCopy = Calendar.getInstance()
                                calCopy.add(Calendar.DAY_OF_YEAR, -i)
                                val dateStr = sdf.format(calCopy.time)
                                val dayName = SimpleDateFormat("E", Locale.getDefault()).format(calCopy.time)
                                val completed = datesList.contains(dateStr)
                                displayList.add(Pair(dayName, completed))
                            }

                            displayList.forEach { pair ->
                                Column(
                                    horizontalAlignment = Alignment.CenterHorizontally,
                                    verticalArrangement = Arrangement.spacedBy(4.dp)
                                ) {
                                    Text(
                                        text = pair.first,
                                        fontSize = 10.sp,
                                        color = TextSecondaryDark,
                                        fontWeight = FontWeight.Bold
                                    )
                                    Box(
                                        modifier = Modifier
                                            .size(28.dp)
                                            .clip(RoundedCornerShape(6.dp))
                                            .background(
                                                if (pair.second) EmeraldPrimary else Color(0xFF334155)
                                            )
                                            .border(
                                                1.dp, 
                                                if (pair.second) MintSecondary else Color.Transparent, 
                                                RoundedCornerShape(6.dp)
                                            ),
                                        contentAlignment = Alignment.Center
                                    ) {
                                        if (pair.second) {
                                            Icon(
                                                imageVector = Icons.Default.Check,
                                                contentDescription = "Ok",
                                                tint = DarkBg,
                                                modifier = Modifier.size(14.dp)
                                            )
                                        }
                                    }
                                }
                            }
                        }

                        Spacer(modifier = Modifier.height(16.dp))
                        Divider(color = MaterialTheme.colorScheme.background.copy(alpha = 0.4f))
                        Spacer(modifier = Modifier.height(14.dp))

                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Row(verticalAlignment = Alignment.CenterVertically) {
                                Icon(
                                    imageVector = Icons.Default.Star,
                                    contentDescription = "Ateş",
                                    tint = PriorityMedium,
                                    modifier = Modifier.size(20.dp)
                                )
                                Spacer(modifier = Modifier.width(6.dp))
                                Text(
                                    text = "${habit.streak} Günlük Kesintisiz Seri Başarısı",
                                    fontSize = 13.sp,
                                    fontWeight = FontWeight.Bold,
                                    color = PriorityMedium
                                )
                            }

                            Button(
                                onClick = { viewModel.toggleHabitCompletion(habit) },
                                colors = ButtonDefaults.buttonColors(
                                    containerColor = if (isCompletedToday) Color(0xFF334155) else EmeraldPrimary,
                                    contentColor = if (isCompletedToday) TextPrimaryDark else DarkBg
                                ),
                                shape = RoundedCornerShape(8.dp),
                                modifier = Modifier.testTag("action_habit_${habit.id}")
                            ) {
                                Text(
                                    text = if (isCompletedToday) "Yapıldı ✓" else "Bugün Tamamla",
                                    fontSize = 12.sp,
                                    fontWeight = FontWeight.Bold
                                )
                            }
                        }
                    }
                }
            }
        }
    }
}

// ==================== COMPONENT DIALOGS ====================

@Composable
fun AddGoalDialog(
    onDismiss: () -> Unit,
    onConfirm: (String, String, String, Long, Float) -> Unit
) {
    var title by remember { mutableStateOf("") }
    var desc by remember { mutableStateOf("") }
    var category by remember { mutableStateOf("Kariyer") }
    var targetValue by remember { mutableStateOf("10") }
    
    val categories = listOf("Kariyer", "Sağlık", "Kişisel Gelişim", "Finans", "Genel")

    Dialog(onDismissRequest = onDismiss) {
        Card(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp)
                .testTag("add_goal_dialog"),
            shape = RoundedCornerShape(16.dp),
            colors = CardDefaults.cardColors(containerColor = DarkSurface)
        ) {
            Column(
                modifier = Modifier
                    .padding(20.dp)
                    .fillMaxWidth(),
                verticalArrangement = Arrangement.spacedBy(14.dp)
            ) {
                Text(
                    text = "Yeni Hedef Belirle 🎯",
                    fontSize = 18.sp,
                    fontWeight = FontWeight.Black,
                    color = TextPrimaryDark
                )
                
                OutlinedTextField(
                    value = title,
                    onValueChange = { title = it },
                    label = { Text("Başlık", color = TextSecondaryDark) },
                    colors = OutlinedTextFieldDefaults.colors(
                        focusedBorderColor = EmeraldPrimary,
                        unfocusedBorderColor = Color(0xFF334155),
                        focusedLabelColor = EmeraldPrimary,
                        cursorColor = EmeraldPrimary
                    ),
                    modifier = Modifier.fillMaxWidth().testTag("add_goal_title")
                )

                OutlinedTextField(
                    value = desc,
                    onValueChange = { desc = it },
                    label = { Text("Açıklama (Opsiyonel)", color = TextSecondaryDark) },
                    colors = OutlinedTextFieldDefaults.colors(
                        focusedBorderColor = EmeraldPrimary,
                        unfocusedBorderColor = Color(0xFF334155),
                        focusedLabelColor = EmeraldPrimary,
                        cursorColor = EmeraldPrimary
                    ),
                    modifier = Modifier.fillMaxWidth()
                )

                // Category selector
                Column {
                    Text("Kategori", fontSize = 12.sp, fontWeight = FontWeight.Bold, color = TextSecondaryDark)
                    Spacer(modifier = Modifier.height(4.dp))
                    LazyRow(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                        items(categories) { cat ->
                            val isSel = category == cat
                            FilterChip(
                                selected = isSel,
                                onClick = { category = cat },
                                label = { Text(cat, fontSize = 11.sp, fontWeight = FontWeight.Bold) },
                                colors = FilterChipDefaults.filterChipColors(
                                    selectedContainerColor = EmeraldPrimary,
                                    selectedLabelColor = DarkBg,
                                    containerColor = Color(0xFF334155),
                                    labelColor = TextSecondaryDark
                                )
                            )
                        }
                    }
                }

                OutlinedTextField(
                    value = targetValue,
                    onValueChange = { targetValue = it },
                    label = { Text("Hedef Değer (Örn: 10 kitap, 30 idman)", color = TextSecondaryDark) },
                    colors = OutlinedTextFieldDefaults.colors(
                        focusedBorderColor = EmeraldPrimary,
                        unfocusedBorderColor = Color(0xFF334155),
                        focusedLabelColor = EmeraldPrimary,
                        cursorColor = EmeraldPrimary
                    ),
                    modifier = Modifier.fillMaxWidth()
                )

                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.End,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    TextButton(onClick = onDismiss) {
                        Text("Vazgeç", color = TextSecondaryDark)
                    }
                    Spacer(modifier = Modifier.width(8.dp))
                    Button(
                        onClick = {
                            if (title.isNotBlank()) {
                                val targetF = targetValue.toFloatOrNull() ?: 10f
                                onConfirm(title, desc, category, System.currentTimeMillis() + 30 * 24 * 60 * 60 * 1000L, targetF)
                            }
                        },
                        colors = ButtonDefaults.buttonColors(containerColor = EmeraldPrimary, contentColor = DarkBg),
                        modifier = Modifier.testTag("confirm_add_goal")
                    ) {
                        Text("Oluştur", fontWeight = FontWeight.Bold)
                    }
                }
            }
        }
    }
}

@Composable
fun AddTaskDialog(
    goals: List<Goal>,
    onDismiss: () -> Unit,
    onConfirm: (String, String, String, Long, Int?) -> Unit
) {
    var title by remember { mutableStateOf("") }
    var desc by remember { mutableStateOf("") }
    var priority by remember { mutableStateOf("Orta") }
    var linkedGoalId by remember { mutableStateOf<Int?>(null) }
    
    val priorities = listOf("Yüksek", "Orta", "Düşük")

    Dialog(onDismissRequest = onDismiss) {
        Card(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp)
                .testTag("add_task_dialog"),
            shape = RoundedCornerShape(16.dp),
            colors = CardDefaults.cardColors(containerColor = DarkSurface)
        ) {
            Column(
                modifier = Modifier
                    .padding(20.dp)
                    .fillMaxWidth(),
                verticalArrangement = Arrangement.spacedBy(14.dp)
            ) {
                Text(
                    text = "Yeni Görev Planla 📝",
                    fontSize = 18.sp,
                    fontWeight = FontWeight.Black,
                    color = TextPrimaryDark
                )
                
                OutlinedTextField(
                    value = title,
                    onValueChange = { title = it },
                    label = { Text("Başlık", color = TextSecondaryDark) },
                    colors = OutlinedTextFieldDefaults.colors(
                        focusedBorderColor = EmeraldPrimary,
                        unfocusedBorderColor = Color(0xFF334155),
                        focusedLabelColor = EmeraldPrimary,
                        cursorColor = EmeraldPrimary
                    ),
                    modifier = Modifier.fillMaxWidth().testTag("add_task_title")
                )

                OutlinedTextField(
                    value = desc,
                    onValueChange = { desc = it },
                    label = { Text("Açıklama (Opsiyonel)", color = TextSecondaryDark) },
                    colors = OutlinedTextFieldDefaults.colors(
                        focusedBorderColor = EmeraldPrimary,
                        unfocusedBorderColor = Color(0xFF334155),
                        focusedLabelColor = EmeraldPrimary,
                        cursorColor = EmeraldPrimary
                    ),
                    modifier = Modifier.fillMaxWidth()
                )

                // Priority switcher
                Column {
                    Text("Öncelik Derecesi", fontSize = 12.sp, fontWeight = FontWeight.Bold, color = TextSecondaryDark)
                    Spacer(modifier = Modifier.height(4.dp))
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        priorities.forEach { prio ->
                            val isSel = priority == prio
                            val color = when (prio) {
                                "Yüksek" -> PriorityHigh
                                "Orta" -> PriorityMedium
                                else -> PriorityLow
                            }
                            FilterChip(
                                selected = isSel,
                                onClick = { priority = prio },
                                label = { Text(prio, fontSize = 11.sp, fontWeight = FontWeight.Bold) },
                                colors = FilterChipDefaults.filterChipColors(
                                    selectedContainerColor = color,
                                    selectedLabelColor = if (prio == "Orta") DarkBg else TextPrimaryDark,
                                    containerColor = Color(0xFF334155),
                                    labelColor = TextSecondaryDark
                                )
                            )
                        }
                    }
                }

                // Linked Goal selector
                if (goals.isNotEmpty()) {
                    Column {
                        Text("Bir Hedefle İlişkilendir (Opsiyonel)", fontSize = 12.sp, fontWeight = FontWeight.Bold, color = TextSecondaryDark)
                        Spacer(modifier = Modifier.height(4.dp))
                        LazyRow(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                            item {
                                FilterChip(
                                    selected = linkedGoalId == null,
                                    onClick = { linkedGoalId = null },
                                    label = { Text("İlişkilendirme", fontSize = 11.sp) },
                                    colors = FilterChipDefaults.filterChipColors(
                                        selectedContainerColor = EmeraldPrimary,
                                        selectedLabelColor = DarkBg,
                                        containerColor = Color(0xFF334155)
                                    )
                                )
                            }
                            items(goals) { goal ->
                                val isSel = linkedGoalId == goal.id
                                FilterChip(
                                    selected = isSel,
                                    onClick = { linkedGoalId = goal.id },
                                    label = { Text(goal.title, fontSize = 11.sp, maxLines = 1) },
                                    colors = FilterChipDefaults.filterChipColors(
                                        selectedContainerColor = EmeraldPrimary,
                                        selectedLabelColor = DarkBg,
                                        containerColor = Color(0xFF334155)
                                    )
                                )
                            }
                        }
                    }
                }

                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.End,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    TextButton(onClick = onDismiss) {
                        Text("Vazgeç", color = TextSecondaryDark)
                    }
                    Spacer(modifier = Modifier.width(8.dp))
                    Button(
                        onClick = {
                            if (title.isNotBlank()) {
                                onConfirm(title, desc, priority, System.currentTimeMillis(), linkedGoalId)
                            }
                        },
                        colors = ButtonDefaults.buttonColors(containerColor = EmeraldPrimary, contentColor = DarkBg),
                        modifier = Modifier.testTag("confirm_add_task")
                    ) {
                        Text("Oluştur", fontWeight = FontWeight.Bold)
                    }
                }
            }
        }
    }
}

@Composable
fun AddHabitDialog(
    onDismiss: () -> Unit,
    onConfirm: (String, String, String) -> Unit
) {
    var title by remember { mutableStateOf("") }
    var category by remember { mutableStateOf("Sağlık") }
    var frequency by remember { mutableStateOf("Günlük") }
    
    val categories = listOf("Sağlık", "Zihin", "Öğrenme", "Genel")
    val frequencies = listOf("Günlük", "Haftalık")

    Dialog(onDismissRequest = onDismiss) {
        Card(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp)
                .testTag("add_habit_dialog"),
            shape = RoundedCornerShape(16.dp),
            colors = CardDefaults.cardColors(containerColor = DarkSurface)
        ) {
            Column(
                modifier = Modifier
                    .padding(20.dp)
                    .fillMaxWidth(),
                verticalArrangement = Arrangement.spacedBy(14.dp)
            ) {
                Text(
                    text = "Yeni Alışkanlık Ekle 🌱",
                    fontSize = 18.sp,
                    fontWeight = FontWeight.Black,
                    color = TextPrimaryDark
                )
                
                OutlinedTextField(
                    value = title,
                    onValueChange = { title = it },
                    label = { Text("Başlık (Örn: 2 Litre Su İç, Kitap Oku)", color = TextSecondaryDark) },
                    colors = OutlinedTextFieldDefaults.colors(
                        focusedBorderColor = EmeraldPrimary,
                        unfocusedBorderColor = Color(0xFF334155),
                        focusedLabelColor = EmeraldPrimary,
                        cursorColor = EmeraldPrimary
                    ),
                    modifier = Modifier.fillMaxWidth().testTag("add_habit_title")
                )

                // Category selector
                Column {
                    Text("Kategori", fontSize = 12.sp, fontWeight = FontWeight.Bold, color = TextSecondaryDark)
                    Spacer(modifier = Modifier.height(4.dp))
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(6.dp)
                    ) {
                        categories.forEach { cat ->
                            val isSel = category == cat
                            FilterChip(
                                selected = isSel,
                                onClick = { category = cat },
                                label = { Text(cat, fontSize = 11.sp, fontWeight = FontWeight.Bold) },
                                colors = FilterChipDefaults.filterChipColors(
                                    selectedContainerColor = EmeraldPrimary,
                                    selectedLabelColor = DarkBg,
                                    containerColor = Color(0xFF334155),
                                    labelColor = TextSecondaryDark
                                )
                            )
                        }
                    }
                }

                // Frequency selector
                Column {
                    Text("Sıklık", fontSize = 12.sp, fontWeight = FontWeight.Bold, color = TextSecondaryDark)
                    Spacer(modifier = Modifier.height(4.dp))
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        frequencies.forEach { freq ->
                            val isSel = frequency == freq
                            FilterChip(
                                selected = isSel,
                                onClick = { frequency = freq },
                                label = { Text(freq, fontSize = 11.sp, fontWeight = FontWeight.Bold) },
                                colors = FilterChipDefaults.filterChipColors(
                                    selectedContainerColor = EmeraldPrimary,
                                    selectedLabelColor = DarkBg,
                                    containerColor = Color(0xFF334155),
                                    labelColor = TextSecondaryDark
                                )
                            )
                        }
                    }
                }

                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.End,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    TextButton(onClick = onDismiss) {
                        Text("Vazgeç", color = TextSecondaryDark)
                    }
                    Spacer(modifier = Modifier.width(8.dp))
                    Button(
                        onClick = {
                            if (title.isNotBlank()) {
                                onConfirm(title, category, frequency)
                            }
                        },
                        colors = ButtonDefaults.buttonColors(containerColor = EmeraldPrimary, contentColor = DarkBg),
                        modifier = Modifier.testTag("confirm_add_habit")
                    ) {
                        Text("Oluştur", fontWeight = FontWeight.Bold)
                    }
                }
            }
        }
    }
}

@Composable
fun SyncBackupDialog(
    viewModel: DashboardViewModel,
    onDismiss: () -> Unit
) {
    var isSyncing by remember { mutableStateOf(false) }
    var syncSuccess by remember { mutableStateOf(false) }

    val dataJsonStr = remember { viewModel.formatDataAsJsonString() }

    Dialog(onDismissRequest = onDismiss) {
        Card(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp)
                .testTag("sync_backup_dialog"),
            shape = RoundedCornerShape(16.dp),
            colors = CardDefaults.cardColors(containerColor = DarkSurface)
        ) {
            Column(
                modifier = Modifier
                    .padding(20.dp)
                    .fillMaxWidth(),
                verticalArrangement = Arrangement.spacedBy(14.dp),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                Text(
                    text = "Bulut Senkronizasyonu ☁️",
                    fontSize = 18.sp,
                    fontWeight = FontWeight.Black,
                    color = TextPrimaryDark,
                    textAlign = TextAlign.Center
                )
                
                Text(
                    text = "Uygulama verileriniz lokal cihazınızda (Room DB) güvenle saklanır. Altta oluşturulan API Sync Payload'unu kullanarak verilerinizi yedekleyebilir veya senkronize edebilirsiniz.",
                    fontSize = 12.sp,
                    color = TextSecondaryDark,
                    textAlign = TextAlign.Center
                )

                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(130.dp)
                        .clip(RoundedCornerShape(8.dp))
                        .background(Color(0xFF0F172A))
                        .border(1.dp, Color(0xFF334155), RoundedCornerShape(8.dp))
                        .padding(8.dp)
                ) {
                    LazyColumn(modifier = Modifier.fillMaxSize()) {
                        item {
                            Text(
                                text = dataJsonStr,
                                fontSize = 10.sp,
                                fontFamily = FontFamily.Monospace,
                                color = MintSecondary
                            )
                        }
                    }
                }

                if (isSyncing) {
                    LinearProgressIndicator(
                        modifier = Modifier.fillMaxWidth().height(6.dp).clip(RoundedCornerShape(3.dp)),
                        color = EmeraldPrimary,
                        trackColor = Color(0xFF334155)
                    )
                    Text(
                        text = "Bulut sunucusuna senkronize ediliyor...",
                        fontSize = 12.sp,
                        fontWeight = FontWeight.Bold,
                        color = EmeraldPrimary
                    )
                } else if (syncSuccess) {
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.Center
                    ) {
                        Icon(
                            imageVector = Icons.Default.Check,
                            contentDescription = "Başarılı",
                            tint = EmeraldPrimary,
                            modifier = Modifier.size(20.dp)
                        )
                        Spacer(modifier = Modifier.width(6.dp))
                        Text(
                            text = "Senkronizasyon Başarılı!",
                            fontSize = 13.sp,
                            fontWeight = FontWeight.Bold,
                            color = EmeraldPrimary
                        )
                    }
                } else {
                    Button(
                        onClick = {
                            isSyncing = true
                            // Simulate net syncing speed
                            android.os.Handler(android.os.Looper.getMainLooper()).postDelayed({
                                isSyncing = false
                                syncSuccess = true
                            }, 1800)
                        },
                        colors = ButtonDefaults.buttonColors(containerColor = EmeraldPrimary, contentColor = DarkBg),
                        modifier = Modifier.fillMaxWidth().testTag("sync_action_button")
                    ) {
                        Text("Buluta Gönder & Senkronize Et", fontWeight = FontWeight.Bold)
                    }
                }

                TextButton(onClick = onDismiss, modifier = Modifier.fillMaxWidth()) {
                    Text("Kapat", color = TextSecondaryDark, fontWeight = FontWeight.Bold)
                }
            }
        }
    }
}
