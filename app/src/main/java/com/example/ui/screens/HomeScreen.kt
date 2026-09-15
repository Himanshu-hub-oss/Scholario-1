package com.example.ui.screens

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material.icons.outlined.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.example.data.model.AssignmentEntity
import com.example.data.model.CampusEventEntity
import com.example.data.model.ExamEntity
import com.example.data.model.ExpenseEntity
import com.example.data.model.StudyTaskEntity
import com.example.ml.AcademicRiskLevel
import com.example.ui.components.MetricStatCard
import com.example.ui.components.QuickActionButton
import com.example.ui.components.SectionHeader
import com.example.ui.components.StatusBadge
import com.example.ui.theme.*
import com.example.ui.viewmodel.CampusViewModel
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

import com.example.ui.graphify.GraphifyCircularGauge

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun HomeScreen(
    viewModel: CampusViewModel,
    onNavigateToStudy: () -> Unit,
    onNavigateToSplitzy: () -> Unit,
    onNavigateToCommunity: () -> Unit,
    onOpenAiChat: () -> Unit,
    onOpenNotifications: () -> Unit,
    onQuickAddExpense: () -> Unit,
    onQuickSplitBill: () -> Unit,
    onQuickAddTask: () -> Unit,
    onQuickAddNote: () -> Unit,
    onQuickCreateGroup: () -> Unit,
    onOpenQuizRunner: () -> Unit = {},
    onOpenResumeBuilder: () -> Unit = {},
    onOpenGamification: () -> Unit = {},
    onNavigateToGrievances: () -> Unit = {},
    modifier: Modifier = Modifier
) {
    val currentUser by viewModel.currentUser.collectAsStateWithLifecycle()
    val expenses by viewModel.expenses.collectAsStateWithLifecycle()
    val studyTasks by viewModel.studyTasks.collectAsStateWithLifecycle()
    val exams by viewModel.exams.collectAsStateWithLifecycle()
    val assignments by viewModel.assignments.collectAsStateWithLifecycle()
    val campusEvents by viewModel.campusEvents.collectAsStateWithLifecycle()
    val academicRisk by viewModel.academicRiskPrediction.collectAsStateWithLifecycle()
    val balanceSummary by viewModel.balanceSummary.collectAsStateWithLifecycle()
    val notifications by viewModel.notifications.collectAsStateWithLifecycle()
    val campusScore by viewModel.campusScore.collectAsStateWithLifecycle()
    val userXp by viewModel.userXp.collectAsStateWithLifecycle()
    val userLevel by viewModel.userLevel.collectAsStateWithLifecycle()

    val unreadNotifsCount = remember(notifications) { notifications.count { !it.isRead } }
    val todayFormatted = remember {
        SimpleDateFormat("EEEE, dd MMMM", Locale.getDefault()).format(Date())
    }

    val upcomingExam = remember(exams) { exams.firstOrNull { !it.isCompleted } }
    val pendingTasks = remember(studyTasks) { studyTasks.filter { !it.isCompleted }.take(4) }
    val pendingAssignments = remember(assignments) { assignments.filter { it.status == "PENDING" }.take(3) }
    val recentExpenses = remember(expenses) { expenses.take(3) }

    LazyColumn(
        modifier = modifier
            .fillMaxSize()
            .background(MaterialTheme.colorScheme.background),
        contentPadding = PaddingValues(bottom = 96.dp)
    ) {

        item {
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .background(
                        Brush.verticalGradient(
                            colors = listOf(
                                Indigo900,
                                Indigo700,
                                MaterialTheme.colorScheme.background
                            )
                        )
                    )
                    .padding(horizontal = 20.dp, vertical = 20.dp)
            ) {
                Column {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Column {
                            Text(
                                text = "Hi, ${currentUser?.name?.split(" ")?.firstOrNull() ?: "Student"} 👋",
                                style = MaterialTheme.typography.headlineMedium.copy(
                                    fontWeight = FontWeight.ExtraBold,
                                    fontSize = 26.sp,
                                    color = Color.White
                                )
                            )
                            Spacer(modifier = Modifier.height(2.dp))
                            Text(
                                text = "Ready to make today productive?",
                                style = MaterialTheme.typography.bodyMedium.copy(
                                    color = Indigo100,
                                    fontSize = 14.sp
                                )
                            )
                            Text(
                                text = todayFormatted,
                                style = MaterialTheme.typography.labelSmall.copy(
                                    color = Cyan400,
                                    fontWeight = FontWeight.SemiBold
                                )
                            )
                        }


                        Row(verticalAlignment = Alignment.CenterVertically) {

                            Surface(
                                shape = RoundedCornerShape(12.dp),
                                color = Color.White.copy(alpha = 0.15f),
                                modifier = Modifier.padding(end = 8.dp)
                            ) {
                                Text(
                                    text = currentUser?.role ?: "Student",
                                    style = MaterialTheme.typography.labelSmall.copy(
                                        color = Color.White,
                                        fontWeight = FontWeight.Bold
                                    ),
                                    modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp)
                                )
                            }


                            IconButton(
                                onClick = onOpenNotifications,
                                modifier = Modifier
                                    .size(44.dp)
                                    .clip(CircleShape)
                                    .background(Color.White.copy(alpha = 0.2f))
                                    .testTag("home_notification_button")
                            ) {
                                BadgedBox(badge = {
                                    if (unreadNotifsCount > 0) {
                                        Badge(containerColor = Rose500) {
                                            Text("$unreadNotifsCount")
                                        }
                                    }
                                }) {
                                    Icon(
                                        imageVector = Icons.Default.Notifications,
                                        contentDescription = "Notifications",
                                        tint = Color.White
                                    )
                                }
                            }
                        }
                    }

                    Spacer(modifier = Modifier.height(16.dp))


                    Surface(
                        shape = RoundedCornerShape(18.dp),
                        color = Color.White.copy(alpha = 0.12f),
                        border = androidx.compose.foundation.BorderStroke(1.dp, Color.White.copy(alpha = 0.2f)),
                        modifier = Modifier
                            .fillMaxWidth()
                            .clickable { onOpenGamification() }
                            .testTag("home_campus_score_card")
                    ) {
                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(14.dp),
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.SpaceBetween
                        ) {
                            Column(modifier = Modifier.weight(1f)) {
                                Row(verticalAlignment = Alignment.CenterVertically) {
                                    Text(
                                        text = "⚡ Level $userLevel Scholar",
                                        style = MaterialTheme.typography.labelMedium.copy(fontWeight = FontWeight.Bold),
                                        color = Amber400
                                    )
                                    Spacer(Modifier.width(6.dp))
                                    Text(
                                        text = "• $userXp XP",
                                        style = MaterialTheme.typography.labelSmall,
                                        color = Indigo100
                                    )
                                }
                                Spacer(Modifier.height(2.dp))
                                Text(
                                    text = "Campus Score Index",
                                    style = MaterialTheme.typography.titleMedium.copy(
                                        fontWeight = FontWeight.Bold,
                                        color = Color.White
                                    )
                                )
                                Text(
                                    text = "Tap to view leaderboard & achievement badges",
                                    style = MaterialTheme.typography.labelSmall,
                                    color = Indigo100
                                )
                            }

                            GraphifyCircularGauge(
                                score = campusScore,
                                title = "Score",
                                color = Cyan400,
                                size = 68.dp
                            )
                        }
                    }

                    Spacer(modifier = Modifier.height(14.dp))


                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(12.dp)
                    ) {

                        Card(
                            shape = RoundedCornerShape(16.dp),
                            colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
                            elevation = CardDefaults.cardElevation(defaultElevation = 3.dp),
                            modifier = Modifier
                                .weight(1f)
                                .clickable { onNavigateToStudy() }
                                .testTag("home_study_progress_card")
                        ) {
                            Column(modifier = Modifier.padding(14.dp)) {
                                Row(
                                    modifier = Modifier.fillMaxWidth(),
                                    horizontalArrangement = Arrangement.SpaceBetween,
                                    verticalAlignment = Alignment.CenterVertically
                                ) {
                                    Text(
                                        text = "Study Score",
                                        style = MaterialTheme.typography.labelMedium,
                                        color = MaterialTheme.colorScheme.onSurfaceVariant
                                    )
                                    Icon(
                                        imageVector = Icons.Default.AutoStories,
                                        contentDescription = null,
                                        tint = Indigo600,
                                        modifier = Modifier.size(18.dp)
                                    )
                                }
                                Spacer(modifier = Modifier.height(6.dp))
                                Text(
                                    text = "${academicRisk?.readinessScorePercent ?: 88}%",
                                    style = MaterialTheme.typography.headlineSmall.copy(
                                        fontWeight = FontWeight.Bold,
                                        color = Indigo600
                                    )
                                )
                                Spacer(modifier = Modifier.height(4.dp))
                                LinearProgressIndicator(
                                    progress = { ((academicRisk?.readinessScorePercent ?: 88) / 100f) },
                                    modifier = Modifier
                                        .fillMaxWidth()
                                        .height(6.dp)
                                        .clip(RoundedCornerShape(3.dp)),
                                    color = Indigo600,
                                    trackColor = Indigo100
                                )
                                Spacer(modifier = Modifier.height(4.dp))
                                Text(
                                    text = "Attendance: ${currentUser?.attendancePercent ?: 92}%",
                                    style = MaterialTheme.typography.labelSmall.copy(
                                        color = MaterialTheme.colorScheme.onSurfaceVariant,
                                        fontSize = 11.sp
                                    )
                                )
                            }
                        }


                        Card(
                            shape = RoundedCornerShape(16.dp),
                            colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
                            elevation = CardDefaults.cardElevation(defaultElevation = 3.dp),
                            modifier = Modifier
                                .weight(1f)
                                .clickable { onNavigateToSplitzy() }
                                .testTag("home_splitzy_spending_card")
                        ) {
                            Column(modifier = Modifier.padding(14.dp)) {
                                Row(
                                    modifier = Modifier.fillMaxWidth(),
                                    horizontalArrangement = Arrangement.SpaceBetween,
                                    verticalAlignment = Alignment.CenterVertically
                                ) {
                                    Text(
                                        text = "Splitzy Spend",
                                        style = MaterialTheme.typography.labelMedium,
                                        color = MaterialTheme.colorScheme.onSurfaceVariant
                                    )
                                    Icon(
                                        imageVector = Icons.Default.AccountBalanceWallet,
                                        contentDescription = null,
                                        tint = Emerald600,
                                        modifier = Modifier.size(18.dp)
                                    )
                                }
                                Spacer(modifier = Modifier.height(6.dp))
                                Text(
                                    text = "₹${balanceSummary.totalSpent.toInt()}",
                                    style = MaterialTheme.typography.headlineSmall.copy(
                                        fontWeight = FontWeight.Bold,
                                        color = Emerald600
                                    )
                                )
                                Spacer(modifier = Modifier.height(4.dp))
                                Row(
                                    modifier = Modifier.fillMaxWidth(),
                                    horizontalArrangement = Arrangement.SpaceBetween
                                ) {
                                    Text(
                                        text = "Owed: ₹${balanceSummary.youOwe.toInt()}",
                                        style = MaterialTheme.typography.labelSmall.copy(
                                            color = Rose500,
                                            fontWeight = FontWeight.SemiBold
                                        )
                                    )
                                    Text(
                                        text = "Get: ₹${balanceSummary.youGet.toInt()}",
                                        style = MaterialTheme.typography.labelSmall.copy(
                                            color = Emerald600,
                                            fontWeight = FontWeight.SemiBold
                                        )
                                    )
                                }
                            }
                        }
                    }
                }
            }
        }


        if (upcomingExam != null) {
            item {
                Card(
                    shape = RoundedCornerShape(18.dp),
                    colors = CardDefaults.cardColors(
                        containerColor = Amber500.copy(alpha = 0.12f)
                    ),
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 16.dp, vertical = 6.dp)
                        .clickable { onNavigateToStudy() }
                        .testTag("home_upcoming_exam_banner")
                ) {
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(16.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Box(
                            modifier = Modifier
                                .size(50.dp)
                                .clip(RoundedCornerShape(14.dp))
                                .background(Amber500),
                            contentAlignment = Alignment.Center
                        ) {
                            Column(horizontalAlignment = Alignment.CenterHorizontally) {
                                Text(
                                    text = "${upcomingExam.daysRemaining}",
                                    style = MaterialTheme.typography.titleLarge.copy(
                                        fontWeight = FontWeight.Bold,
                                        color = Color.White,
                                        fontSize = 18.sp
                                    )
                                )
                                Text(
                                    text = "DAYS",
                                    style = MaterialTheme.typography.labelSmall.copy(
                                        color = Color.White.copy(alpha = 0.9f),
                                        fontSize = 9.sp,
                                        fontWeight = FontWeight.ExtraBold
                                    )
                                )
                            }
                        }

                        Spacer(modifier = Modifier.width(14.dp))

                        Column(modifier = Modifier.weight(1f)) {
                            Row(verticalAlignment = Alignment.CenterVertically) {
                                StatusBadge(
                                    text = upcomingExam.subjectCode,
                                    color = Amber600,
                                    bgColor = Amber400.copy(alpha = 0.25f)
                                )
                                Spacer(modifier = Modifier.width(6.dp))
                                Text(
                                    text = "Upcoming Exam",
                                    style = MaterialTheme.typography.labelSmall.copy(
                                        color = MaterialTheme.colorScheme.onSurfaceVariant
                                    )
                                )
                            }
                            Spacer(modifier = Modifier.height(2.dp))
                            Text(
                                text = upcomingExam.subjectName,
                                style = MaterialTheme.typography.titleMedium.copy(
                                    fontWeight = FontWeight.Bold,
                                    fontSize = 15.sp
                                ),
                                color = MaterialTheme.colorScheme.onSurface,
                                maxLines = 1
                            )
                            Text(
                                text = "📍 ${upcomingExam.venue} • ⏰ ${upcomingExam.time}",
                                style = MaterialTheme.typography.bodySmall.copy(
                                    fontSize = 12.sp,
                                    color = MaterialTheme.colorScheme.onSurfaceVariant
                                ),
                                maxLines = 1
                            )
                        }

                        Icon(
                            imageVector = Icons.Default.ArrowForward,
                            contentDescription = "View Exam Details",
                            tint = Amber600,
                            modifier = Modifier.size(16.dp)
                        )
                    }
                }
            }
        }


        item {
            SectionHeader(title = "Quick Actions Hub")
            LazyRow(
                contentPadding = PaddingValues(horizontal = 16.dp),
                horizontalArrangement = Arrangement.spacedBy(10.dp),
                modifier = Modifier.fillMaxWidth()
            ) {
                item {
                    QuickActionButton(
                        icon = Icons.Default.AddCard,
                        label = "Add Expense",
                        color = Indigo600,
                        onClick = onQuickAddExpense
                    )
                }
                item {
                    QuickActionButton(
                        icon = Icons.Default.CallSplit,
                        label = "Split Bill",
                        color = Cyan600,
                        onClick = onQuickSplitBill
                    )
                }
                item {
                    QuickActionButton(
                        icon = Icons.Default.AutoAwesome,
                        label = "Ask AI",
                        color = Emerald600,
                        onClick = onOpenAiChat
                    )
                }
                item {
                    QuickActionButton(
                        icon = Icons.Default.Quiz,
                        label = "Take Quiz",
                        color = Indigo700,
                        onClick = onOpenQuizRunner
                    )
                }
                item {
                    QuickActionButton(
                        icon = Icons.Default.ReportProblem,
                        label = "Report Issue",
                        color = Rose600,
                        onClick = onNavigateToGrievances
                    )
                }
                item {
                    QuickActionButton(
                        icon = Icons.Default.WorkHistory,
                        label = "Resume Builder",
                        color = Indigo600,
                        onClick = onOpenResumeBuilder
                    )
                }
                item {
                    QuickActionButton(
                        icon = Icons.Default.Leaderboard,
                        label = "Leaderboard",
                        color = Amber500,
                        onClick = onOpenGamification
                    )
                }
                item {
                    QuickActionButton(
                        icon = Icons.Default.CheckCircleOutline,
                        label = "Add Task",
                        color = Indigo500,
                        onClick = onQuickAddTask
                    )
                }
                item {
                    QuickActionButton(
                        icon = Icons.Default.NoteAdd,
                        label = "Add Note",
                        color = Sky600,
                        onClick = onQuickAddNote
                    )
                }
                item {
                    QuickActionButton(
                        icon = Icons.Default.GroupAdd,
                        label = "Create Group",
                        color = Emerald700,
                        onClick = onQuickCreateGroup
                    )
                }
            }
        }


        item {
            SectionHeader(
                title = "Today's Study Plan & Tasks",
                actionText = "Study Zone",
                onActionClick = onNavigateToStudy
            )

            if (pendingTasks.isEmpty() && pendingAssignments.isEmpty()) {
                Card(
                    shape = RoundedCornerShape(16.dp),
                    colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 16.dp)
                ) {
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(16.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Icon(
                            imageVector = Icons.Default.DoneAll,
                            contentDescription = null,
                            tint = Emerald500,
                            modifier = Modifier.size(28.dp)
                        )
                        Spacer(modifier = Modifier.width(12.dp))
                        Text(
                            text = "All study tasks complete for today! Keep it up.",
                            style = MaterialTheme.typography.bodyMedium,
                            color = MaterialTheme.colorScheme.onSurface
                        )
                    }
                }
            } else {
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 16.dp),
                    verticalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    pendingTasks.forEach { task ->
                        Card(
                            shape = RoundedCornerShape(14.dp),
                            colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
                            elevation = CardDefaults.cardElevation(defaultElevation = 1.dp),
                            modifier = Modifier.fillMaxWidth()
                        ) {
                            Row(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .padding(12.dp),
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Checkbox(
                                    checked = task.isCompleted,
                                    onCheckedChange = { viewModel.toggleTask(task) },
                                    modifier = Modifier.testTag("task_checkbox_${task.id}")
                                )
                                Spacer(modifier = Modifier.width(8.dp))
                                Column(modifier = Modifier.weight(1f)) {
                                    Text(
                                        text = task.title,
                                        style = MaterialTheme.typography.bodyMedium.copy(
                                            fontWeight = FontWeight.SemiBold
                                        ),
                                        color = MaterialTheme.colorScheme.onSurface
                                    )
                                    Row(verticalAlignment = Alignment.CenterVertically) {
                                        Text(
                                            text = "${task.subject} • ⏰ ${task.dueDate}",
                                            style = MaterialTheme.typography.bodySmall.copy(fontSize = 11.sp),
                                            color = MaterialTheme.colorScheme.onSurfaceVariant
                                        )
                                    }
                                }
                                StatusBadge(
                                    text = task.priority,
                                    color = if (task.priority == "High") Rose500 else Amber600,
                                    bgColor = if (task.priority == "High") Rose400.copy(alpha = 0.15f) else Amber400.copy(alpha = 0.15f)
                                )
                            }
                        }
                    }
                }
            }
        }


        if (academicRisk != null) {
            item {
                Spacer(modifier = Modifier.height(8.dp))
                SectionHeader(title = "AI Academic Performance Engine")
                Card(
                    shape = RoundedCornerShape(16.dp),
                    colors = CardDefaults.cardColors(
                        containerColor = if (academicRisk?.riskLevel == AcademicRiskLevel.LOW_RISK)
                            Emerald500.copy(alpha = 0.08f)
                        else Amber500.copy(alpha = 0.08f)
                    ),
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 16.dp)
                ) {
                    Column(modifier = Modifier.padding(16.dp)) {
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Row(verticalAlignment = Alignment.CenterVertically) {
                                Icon(
                                    imageVector = Icons.Default.Psychology,
                                    contentDescription = null,
                                    tint = if (academicRisk?.riskLevel == AcademicRiskLevel.LOW_RISK) Emerald600 else Amber600,
                                    modifier = Modifier.size(24.dp)
                                )
                                Spacer(modifier = Modifier.width(8.dp))
                                Text(
                                    text = "Status: ${if (academicRisk?.riskLevel == AcademicRiskLevel.LOW_RISK) "Optimal Momentum (Low Risk)" else "Action Needed"}",
                                    style = MaterialTheme.typography.titleSmall.copy(fontWeight = FontWeight.Bold),
                                    color = MaterialTheme.colorScheme.onSurface
                                )
                            }
                            Text(
                                text = "Est. GPA: ${academicRisk?.predictedGpaRange}",
                                style = MaterialTheme.typography.labelMedium.copy(
                                    fontWeight = FontWeight.Bold,
                                    color = Indigo600
                                )
                            )
                        }

                        Spacer(modifier = Modifier.height(6.dp))
                        Text(
                            text = academicRisk?.actionableGuidance ?: "",
                            style = MaterialTheme.typography.bodySmall.copy(fontSize = 12.sp),
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                    }
                }
            }
        }


        item {
            Spacer(modifier = Modifier.height(8.dp))
            SectionHeader(
                title = "Recent Expenses (Splitzy)",
                actionText = "All Expenses",
                onActionClick = onNavigateToSplitzy
            )

            if (recentExpenses.isEmpty()) {
                Text(
                    text = "No expenses recorded yet. Tap '+ Add Expense' to start tracking.",
                    style = MaterialTheme.typography.bodyMedium,
                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                    modifier = Modifier.padding(horizontal = 16.dp)
                )
            } else {
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 16.dp),
                    verticalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    recentExpenses.forEach { exp ->
                        Card(
                            shape = RoundedCornerShape(14.dp),
                            colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
                            elevation = CardDefaults.cardElevation(defaultElevation = 1.dp),
                            modifier = Modifier.fillMaxWidth()
                        ) {
                            Row(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .padding(14.dp),
                                horizontalArrangement = Arrangement.SpaceBetween,
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Row(verticalAlignment = Alignment.CenterVertically) {
                                    Box(
                                        modifier = Modifier
                                            .size(40.dp)
                                            .clip(CircleShape)
                                            .background(Indigo100),
                                        contentAlignment = Alignment.Center
                                    ) {
                                        Icon(
                                            imageVector = when (exp.category) {
                                                "Food" -> Icons.Default.Restaurant
                                                "Travel" -> Icons.Default.DirectionsCar
                                                "Bills" -> Icons.Default.Receipt
                                                "Education" -> Icons.Default.School
                                                else -> Icons.Default.ShoppingBag
                                            },
                                            contentDescription = null,
                                            tint = Indigo600,
                                            modifier = Modifier.size(20.dp)
                                        )
                                    }
                                    Spacer(modifier = Modifier.width(12.dp))
                                    Column {
                                        Text(
                                            text = exp.title,
                                            style = MaterialTheme.typography.bodyMedium.copy(fontWeight = FontWeight.SemiBold),
                                            color = MaterialTheme.colorScheme.onSurface
                                        )
                                        Text(
                                            text = "${exp.category} • Paid by ${exp.paidBy} • ${exp.date}",
                                            style = MaterialTheme.typography.bodySmall.copy(fontSize = 11.sp),
                                            color = MaterialTheme.colorScheme.onSurfaceVariant
                                        )
                                    }
                                }
                                Text(
                                    text = "₹${exp.amount.toInt()}",
                                    style = MaterialTheme.typography.titleMedium.copy(
                                        fontWeight = FontWeight.Bold,
                                        color = MaterialTheme.colorScheme.onSurface
                                    )
                                )
                            }
                        }
                    }
                }
            }
        }


        item {
            Spacer(modifier = Modifier.height(10.dp))
            SectionHeader(
                title = "Upcoming Campus Events",
                actionText = "Community",
                onActionClick = onNavigateToCommunity
            )

            LazyRow(
                modifier = Modifier.fillMaxWidth(),
                contentPadding = PaddingValues(horizontal = 16.dp),
                horizontalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                items(campusEvents, key = { it.id }) { event ->
                    Card(
                        shape = RoundedCornerShape(16.dp),
                        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
                        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp),
                        modifier = Modifier
                            .width(260.dp)
                            .clickable { onNavigateToCommunity() }
                    ) {
                        Column(modifier = Modifier.padding(14.dp)) {
                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.SpaceBetween,
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                StatusBadge(
                                    text = event.category,
                                    color = Indigo600,
                                    bgColor = Indigo50
                                )
                                Text(
                                    text = if (event.isRegistered) "Registered ✓" else "${event.attendeesCount} Going",
                                    style = MaterialTheme.typography.labelSmall.copy(
                                        color = if (event.isRegistered) Emerald600 else MaterialTheme.colorScheme.onSurfaceVariant,
                                        fontWeight = FontWeight.Bold
                                    )
                                )
                            }
                            Spacer(modifier = Modifier.height(8.dp))
                            Text(
                                text = event.title,
                                style = MaterialTheme.typography.titleSmall.copy(fontWeight = FontWeight.Bold),
                                color = MaterialTheme.colorScheme.onSurface,
                                maxLines = 2
                            )
                            Spacer(modifier = Modifier.height(4.dp))
                            Text(
                                text = "By ${event.clubName}",
                                style = MaterialTheme.typography.bodySmall.copy(fontSize = 11.sp),
                                color = MaterialTheme.colorScheme.onSurfaceVariant
                            )
                            Text(
                                text = "📅 ${event.date} • ${event.time}",
                                style = MaterialTheme.typography.bodySmall.copy(
                                    fontSize = 11.sp,
                                    color = MaterialTheme.colorScheme.primary,
                                    fontWeight = FontWeight.Medium
                                )
                            )
                        }
                    }
                }
            }
        }
    }
}
