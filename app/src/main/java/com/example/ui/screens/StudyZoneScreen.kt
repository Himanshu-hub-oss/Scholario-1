package com.example.ui.screens

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
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
import com.example.data.model.ExamEntity
import com.example.data.model.QuizQuestionEntity
import com.example.data.model.StudyTaskEntity
import com.example.data.model.SubjectNoteEntity
import com.example.ui.components.SectionHeader
import com.example.ui.components.StatusBadge
import com.example.ui.theme.*
import com.example.ui.viewmodel.CampusViewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun StudyZoneScreen(
    viewModel: CampusViewModel,
    onOpenAiChat: () -> Unit,
    modifier: Modifier = Modifier
) {
    var selectedTab by remember { mutableIntStateOf(0) }
    val tabs = listOf("Planner & Tasks", "Notes & Docs", "Exams", "MCQ Quizzes", "AI Doubt Solver")

    val studyTasks by viewModel.studyTasks.collectAsStateWithLifecycle()
    val exams by viewModel.exams.collectAsStateWithLifecycle()
    val notes by viewModel.subjectNotes.collectAsStateWithLifecycle()
    val assignments by viewModel.assignments.collectAsStateWithLifecycle()
    val quizQuestions by viewModel.quizQuestions.collectAsStateWithLifecycle()
    val aiDoubtResult by viewModel.aiDoubtResult.collectAsStateWithLifecycle()
    val aiStudyPlanResult by viewModel.aiStudyPlanResult.collectAsStateWithLifecycle()
    val isAiLoading by viewModel.isAiLoading.collectAsStateWithLifecycle()
    val currentUser by viewModel.currentUser.collectAsStateWithLifecycle()
    val selectedAcademicYear by viewModel.selectedAcademicYear.collectAsStateWithLifecycle()

    var showAddTaskDialog by remember { mutableStateOf(false) }
    var showAddNoteDialog by remember { mutableStateOf(false) }
    var showAddExamDialog by remember { mutableStateOf(false) }
    var showStudyPlanDialog by remember { mutableStateOf(false) }
    var showChangeYearDialog by remember { mutableStateOf(false) }


    val activeYear = if (selectedAcademicYear != "All") {
        selectedAcademicYear
    } else {
        currentUser?.year?.take(8) ?: "3rd Year"
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = {
                    Column {
                        Text(
                            text = "Study Zone",
                            style = MaterialTheme.typography.titleLarge.copy(fontWeight = FontWeight.Bold)
                        )
                        Text(
                            text = "AI Study Assistant & Academic Hub",
                            style = MaterialTheme.typography.bodySmall.copy(color = MaterialTheme.colorScheme.onSurfaceVariant)
                        )
                    }
                },
                actions = {
                    IconButton(
                        onClick = onOpenAiChat,
                        modifier = Modifier.testTag("study_open_ai_button")
                    ) {
                        Icon(
                            imageVector = Icons.Default.AutoAwesome,
                            contentDescription = "Ask AI",
                            tint = Indigo600
                        )
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = MaterialTheme.colorScheme.surface
                )
            )
        },
        modifier = modifier
    ) { innerPadding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding)
                .background(MaterialTheme.colorScheme.background)
        ) {

            ScrollableTabRow(
                selectedTabIndex = selectedTab,
                edgePadding = 16.dp,
                containerColor = MaterialTheme.colorScheme.surface,
                contentColor = MaterialTheme.colorScheme.primary,
                divider = {}
            ) {
                tabs.forEachIndexed { index, title ->
                    Tab(
                        selected = selectedTab == index,
                        onClick = { selectedTab = index },
                        text = {
                            Text(
                                text = title,
                                style = MaterialTheme.typography.labelLarge.copy(
                                    fontWeight = if (selectedTab == index) FontWeight.Bold else FontWeight.Medium
                                )
                            )
                        },
                        modifier = Modifier.testTag("study_tab_$index")
                    )
                }
            }


            Surface(
                color = Indigo50.copy(alpha = 0.5f),
                modifier = Modifier.fillMaxWidth()
            ) {
                Column(modifier = Modifier.padding(horizontal = 16.dp, vertical = 6.dp)) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Text(
                                text = "🎓 Active Syllabus:",
                                style = MaterialTheme.typography.labelSmall.copy(fontWeight = FontWeight.Bold, color = Indigo900)
                            )
                            Spacer(Modifier.width(6.dp))
                            Surface(
                                shape = RoundedCornerShape(6.dp),
                                color = Indigo600
                            ) {
                                Text(
                                    text = if (selectedAcademicYear == "All") "All Years" else activeYear,
                                    style = MaterialTheme.typography.labelSmall.copy(fontWeight = FontWeight.Bold, color = Color.White),
                                    modifier = Modifier.padding(horizontal = 6.dp, vertical = 2.dp)
                                )
                            }
                        }

                        TextButton(
                            onClick = { showChangeYearDialog = true },
                            contentPadding = PaddingValues(horizontal = 6.dp, vertical = 2.dp)
                        ) {
                            Icon(imageVector = Icons.Default.Tune, contentDescription = null, modifier = Modifier.size(14.dp), tint = Indigo600)
                            Spacer(Modifier.width(4.dp))
                            Text("Set Profile Year", fontSize = 11.sp, color = Indigo600, fontWeight = FontWeight.Bold)
                        }
                    }


                    val yearChips = listOf("1st Year", "2nd Year", "3rd Year", "4th Year", "All Years")
                    LazyRow(
                        horizontalArrangement = Arrangement.spacedBy(6.dp),
                        modifier = Modifier.fillMaxWidth().padding(top = 2.dp, bottom = 4.dp)
                    ) {
                        items(yearChips) { yr ->
                            val isSelected = (yr == "All Years" && selectedAcademicYear == "All") ||
                                    (yr != "All Years" && selectedAcademicYear != "All" && activeYear.startsWith(yr.take(3)))
                            FilterChip(
                                selected = isSelected,
                                onClick = {
                                    if (yr == "All Years") viewModel.setSelectedAcademicYear("All")
                                    else viewModel.setSelectedAcademicYear(yr)
                                },
                                label = {
                                    Text(
                                        text = yr,
                                        fontSize = 11.sp,
                                        fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Normal
                                    )
                                },
                                shape = RoundedCornerShape(8.dp)
                            )
                        }
                    }
                }
            }

            Spacer(modifier = Modifier.height(4.dp))

            when (selectedTab) {
                0 -> StudyTasksAndPlannerTab(
                    tasks = studyTasks,
                    assignments = assignments,
                    aiStudyPlan = aiStudyPlanResult,
                    isLoading = isAiLoading,
                    onToggleTask = { viewModel.toggleTask(it) },
                    onDeleteTask = { viewModel.deleteTask(it) },
                    onAddTaskClick = { showAddTaskDialog = true },
                    onOpenPlanner = { showStudyPlanDialog = true }
                )
                1 -> SubjectNotesTab(
                    notes = notes,
                    activeYear = if (selectedAcademicYear == "All") "All" else activeYear,
                    onAddNoteClick = { showAddNoteDialog = true }
                )
                2 -> ExamScheduleTab(
                    exams = exams,
                    activeYear = if (selectedAcademicYear == "All") "All" else activeYear,
                    onAddExamClick = { showAddExamDialog = true }
                )
                3 -> McqQuizRunnerTab(
                    questions = quizQuestions
                )
                4 -> AiDoubtSolverTab(
                    aiResult = aiDoubtResult,
                    isLoading = isAiLoading,
                    onAskDoubt = { subj, q, diff -> viewModel.askDoubt(subj, q, diff) }
                )
            }
        }
    }


    if (showAddTaskDialog) {
        AddTaskModal(
            onDismiss = { showAddTaskDialog = false },
            onAdd = { title, subj, due, priority, mins ->
                viewModel.addTask(title, subj, due, priority, mins)
                showAddTaskDialog = false
            }
        )
    }


    if (showAddNoteDialog) {
        AddNoteModal(
            onDismiss = { showAddNoteDialog = false },
            onAdd = { subj, title, chap, summary, tags ->
                viewModel.addNote(subj, title, chap, summary, tags)
                showAddNoteDialog = false
            }
        )
    }


    if (showAddExamDialog) {
        AddExamModal(
            onDismiss = { showAddExamDialog = false },
            onAdd = { code, name, date, time, venue, syllabus ->
                viewModel.addExam(code, name, date, time, venue, syllabus)
                showAddExamDialog = false
            }
        )
    }


    if (showStudyPlanDialog) {
        GeneratePlanModal(
            isLoading = isAiLoading,
            onDismiss = { showStudyPlanDialog = false },
            onGenerate = { subjects, days, hours ->
                viewModel.generateStudyPlan(subjects, days, hours)
                showStudyPlanDialog = false
            }
        )
    }


    if (showChangeYearDialog) {
        ChangeYearModal(
            currentYear = currentUser?.year ?: "3rd Year",
            currentSemester = currentUser?.semester ?: "Semester 5",
            onDismiss = { showChangeYearDialog = false },
            onSave = { newYear, newSem ->
                viewModel.updateUserAcademicYear(newYear, newSem)
                showChangeYearDialog = false
            }
        )
    }
}



@Composable
fun StudyTasksAndPlannerTab(
    tasks: List<StudyTaskEntity>,
    assignments: List<AssignmentEntity>,
    aiStudyPlan: String?,
    isLoading: Boolean,
    onToggleTask: (StudyTaskEntity) -> Unit,
    onDeleteTask: (Long) -> Unit,
    onAddTaskClick: () -> Unit,
    onOpenPlanner: () -> Unit
) {
    LazyColumn(
        modifier = Modifier.fillMaxSize(),
        contentPadding = PaddingValues(16.dp),
        verticalArrangement = Arrangement.spacedBy(12.dp)
    ) {

        item {
            Card(
                shape = RoundedCornerShape(18.dp),
                colors = CardDefaults.cardColors(containerColor = Indigo900),
                modifier = Modifier.fillMaxWidth()
            ) {
                Column(modifier = Modifier.padding(18.dp)) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Icon(
                                imageVector = Icons.Default.AutoAwesome,
                                contentDescription = null,
                                tint = Cyan400,
                                modifier = Modifier.size(24.dp)
                            )
                            Spacer(modifier = Modifier.width(8.dp))
                            Text(
                                text = "7-Day AI Exam Study Sprint",
                                style = MaterialTheme.typography.titleMedium.copy(
                                    fontWeight = FontWeight.Bold,
                                    color = Color.White
                                )
                            )
                        }
                    }
                    Spacer(modifier = Modifier.height(6.dp))
                    Text(
                        text = "Generate a customized high-yield revision timetable tailored to your subjects and daily study hours.",
                        style = MaterialTheme.typography.bodySmall.copy(color = Indigo100)
                    )
                    Spacer(modifier = Modifier.height(12.dp))
                    Button(
                        onClick = onOpenPlanner,
                        colors = ButtonDefaults.buttonColors(containerColor = Cyan500),
                        shape = RoundedCornerShape(12.dp),
                        modifier = Modifier.testTag("generate_study_plan_button")
                    ) {
                        Icon(
                            imageVector = Icons.Default.CalendarMonth,
                            contentDescription = null,
                            tint = Slate950,
                            modifier = Modifier.size(18.dp)
                        )
                        Spacer(modifier = Modifier.width(8.dp))
                        Text(
                            text = "Create AI Study Timetable",
                            color = Slate950,
                            fontWeight = FontWeight.Bold
                        )
                    }
                }
            }
        }


        if (!aiStudyPlan.isNullOrBlank()) {
            item {
                Card(
                    shape = RoundedCornerShape(16.dp),
                    colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
                    elevation = CardDefaults.cardElevation(defaultElevation = 2.dp),
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Column(modifier = Modifier.padding(16.dp)) {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Icon(
                                imageVector = Icons.Default.CheckCircle,
                                contentDescription = null,
                                tint = Emerald600,
                                modifier = Modifier.size(20.dp)
                            )
                            Spacer(modifier = Modifier.width(8.dp))
                            Text(
                                text = "Generated Plan Output",
                                style = MaterialTheme.typography.titleSmall.copy(fontWeight = FontWeight.Bold),
                                color = MaterialTheme.colorScheme.primary
                            )
                        }
                        Spacer(modifier = Modifier.height(8.dp))
                        Text(
                            text = aiStudyPlan,
                            style = MaterialTheme.typography.bodySmall.copy(
                                lineHeight = 20.sp,
                                color = MaterialTheme.colorScheme.onSurface
                            )
                        )
                    }
                }
            }
        }


        item {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = "Daily Tasks (${tasks.count { !it.isCompleted }} Pending)",
                    style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold),
                    color = MaterialTheme.colorScheme.onBackground
                )
                Button(
                    onClick = onAddTaskClick,
                    shape = RoundedCornerShape(10.dp),
                    contentPadding = PaddingValues(horizontal = 12.dp, vertical = 6.dp),
                    modifier = Modifier.testTag("study_add_task_button")
                ) {
                    Icon(imageVector = Icons.Default.Add, contentDescription = null, modifier = Modifier.size(16.dp))
                    Spacer(modifier = Modifier.width(4.dp))
                    Text("Add Task", fontSize = 12.sp)
                }
            }
        }

        items(tasks) { task ->
            Card(
                shape = RoundedCornerShape(14.dp),
                colors = CardDefaults.cardColors(
                    containerColor = if (task.isCompleted) MaterialTheme.colorScheme.surface.copy(alpha = 0.6f) else MaterialTheme.colorScheme.surface
                ),
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
                        onCheckedChange = { onToggleTask(task) }
                    )
                    Spacer(modifier = Modifier.width(8.dp))
                    Column(modifier = Modifier.weight(1f)) {
                        Text(
                            text = task.title,
                            style = MaterialTheme.typography.bodyMedium.copy(
                                fontWeight = FontWeight.SemiBold,
                                color = if (task.isCompleted) MaterialTheme.colorScheme.onSurfaceVariant else MaterialTheme.colorScheme.onSurface
                            )
                        )
                        Text(
                            text = "${task.subject} • Due: ${task.dueDate} • ${task.estimatedMinutes} mins",
                            style = MaterialTheme.typography.bodySmall.copy(
                                fontSize = 11.sp,
                                color = MaterialTheme.colorScheme.onSurfaceVariant
                            )
                        )
                    }
                    StatusBadge(
                        text = task.priority,
                        color = if (task.priority == "High") Rose500 else Amber600,
                        bgColor = if (task.priority == "High") Rose400.copy(alpha = 0.15f) else Amber400.copy(alpha = 0.15f)
                    )
                    IconButton(onClick = { onDeleteTask(task.id) }) {
                        Icon(
                            imageVector = Icons.Default.DeleteOutline,
                            contentDescription = "Delete",
                            tint = MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.6f),
                            modifier = Modifier.size(18.dp)
                        )
                    }
                }
            }
        }


        item {
            Spacer(modifier = Modifier.height(10.dp))
            Text(
                text = "Assignments & Lab Submissions",
                style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold),
                color = MaterialTheme.colorScheme.onBackground
            )
        }

        items(assignments) { assign ->
            Card(
                shape = RoundedCornerShape(14.dp),
                colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
                elevation = CardDefaults.cardElevation(defaultElevation = 1.dp),
                modifier = Modifier.fillMaxWidth()
            ) {
                Column(modifier = Modifier.padding(14.dp)) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        StatusBadge(
                            text = assign.status,
                            color = if (assign.status == "PENDING") Amber600 else Emerald600,
                            bgColor = if (assign.status == "PENDING") Amber400.copy(alpha = 0.15f) else Emerald400.copy(alpha = 0.15f)
                        )
                        Text(
                            text = "Max Marks: ${assign.maxMarks}",
                            style = MaterialTheme.typography.labelSmall.copy(color = MaterialTheme.colorScheme.onSurfaceVariant)
                        )
                    }
                    Spacer(modifier = Modifier.height(6.dp))
                    Text(
                        text = assign.title,
                        style = MaterialTheme.typography.titleSmall.copy(fontWeight = FontWeight.Bold),
                        color = MaterialTheme.colorScheme.onSurface
                    )
                    Text(
                        text = "Subject: ${assign.subject}",
                        style = MaterialTheme.typography.bodySmall.copy(fontSize = 12.sp),
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                    Text(
                        text = "⏰ Deadline: ${assign.deadline}",
                        style = MaterialTheme.typography.bodySmall.copy(
                            fontSize = 11.sp,
                            fontWeight = FontWeight.SemiBold,
                            color = if (assign.daysLeft <= 1) Rose500 else MaterialTheme.colorScheme.primary
                        )
                    )
                }
            }
        }
    }
}

@Composable
fun SubjectNotesTab(
    notes: List<SubjectNoteEntity>,
    activeYear: String = "All",
    onAddNoteClick: () -> Unit
) {
    var selectedFilter by remember { mutableStateOf("All") }


    val yearFilteredNotes = if (activeYear == "All") {
        notes
    } else {
        val yrPrefix = activeYear.take(3)
        notes.filter { it.academicYear.startsWith(yrPrefix) || it.academicYear.contains(yrPrefix) }
    }


    val subjects = listOf("All") + yearFilteredNotes.map { it.subject }.distinct()
    val filteredNotes = if (selectedFilter == "All" || !subjects.contains(selectedFilter)) {
        yearFilteredNotes
    } else {
        yearFilteredNotes.filter { it.subject == selectedFilter }
    }

    LazyColumn(
        modifier = Modifier.fillMaxSize(),
        contentPadding = PaddingValues(16.dp),
        verticalArrangement = Arrangement.spacedBy(12.dp)
    ) {
        item {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Column {
                    Text(
                        text = "Previous Year Qs & Notes",
                        style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold)
                    )
                    Text(
                        text = if (activeYear == "All") "Showing all years" else "Filtered for $activeYear",
                        style = MaterialTheme.typography.labelSmall.copy(color = Indigo600, fontWeight = FontWeight.SemiBold)
                    )
                }
                Button(
                    onClick = onAddNoteClick,
                    shape = RoundedCornerShape(10.dp),
                    contentPadding = PaddingValues(horizontal = 12.dp, vertical = 6.dp),
                    modifier = Modifier.testTag("upload_notes_button")
                ) {
                    Icon(imageVector = Icons.Default.UploadFile, contentDescription = null, modifier = Modifier.size(16.dp))
                    Spacer(modifier = Modifier.width(4.dp))
                    Text("Upload Notes", fontSize = 12.sp)
                }
            }
        }


        item {
            LazyRow(
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                items(subjects) { subj ->
                    FilterChip(
                        selected = (selectedFilter == subj) || (selectedFilter !in subjects && subj == "All"),
                        onClick = { selectedFilter = subj },
                        label = { Text(subj, fontSize = 12.sp) }
                    )
                }
            }
        }

        if (filteredNotes.isEmpty()) {
            item {
                Card(
                    shape = RoundedCornerShape(16.dp),
                    colors = CardDefaults.cardColors(containerColor = Slate100),
                    modifier = Modifier.fillMaxWidth().padding(vertical = 12.dp)
                ) {
                    Column(
                        modifier = Modifier.padding(24.dp).fillMaxWidth(),
                        horizontalAlignment = Alignment.CenterHorizontally
                    ) {
                        Icon(imageVector = Icons.Default.MenuBook, contentDescription = null, tint = Slate500, modifier = Modifier.size(40.dp))
                        Spacer(modifier = Modifier.height(8.dp))
                        Text("No notes found for $activeYear", fontWeight = FontWeight.Bold, color = Slate800)
                        Text("Be the first to upload notes or switch year tab above!", fontSize = 12.sp, color = Slate600)
                    }
                }
            }
        }

        items(filteredNotes) { note ->
            Card(
                shape = RoundedCornerShape(16.dp),
                colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
                elevation = CardDefaults.cardElevation(defaultElevation = 2.dp),
                modifier = Modifier.fillMaxWidth()
            ) {
                Column(modifier = Modifier.padding(16.dp)) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Box(
                                modifier = Modifier
                                    .size(36.dp)
                                    .clip(RoundedCornerShape(8.dp))
                                    .background(Rose500.copy(alpha = 0.15f)),
                                contentAlignment = Alignment.Center
                            ) {
                                Icon(
                                    imageVector = Icons.Default.PictureAsPdf,
                                    contentDescription = null,
                                    tint = Rose500,
                                    modifier = Modifier.size(20.dp)
                                )
                            }
                            Spacer(modifier = Modifier.width(8.dp))
                            StatusBadge(
                                text = note.subject,
                                color = Indigo600,
                                bgColor = Indigo50
                            )
                            Spacer(modifier = Modifier.width(6.dp))
                            Surface(
                                shape = RoundedCornerShape(6.dp),
                                color = Amber50
                            ) {
                                Text(
                                    text = "${note.academicYear} • ${note.semester}",
                                    style = MaterialTheme.typography.labelSmall.copy(
                                        color = Amber800,
                                        fontWeight = FontWeight.Bold,
                                        fontSize = 10.sp
                                    ),
                                    modifier = Modifier.padding(horizontal = 6.dp, vertical = 2.dp)
                                )
                            }
                        }
                        Text(
                            text = "${note.downloadsCount} Downloads",
                            style = MaterialTheme.typography.labelSmall.copy(color = MaterialTheme.colorScheme.onSurfaceVariant)
                        )
                    }

                    Spacer(modifier = Modifier.height(10.dp))
                    Text(
                        text = note.title,
                        style = MaterialTheme.typography.titleSmall.copy(fontWeight = FontWeight.Bold),
                        color = MaterialTheme.colorScheme.onSurface
                    )
                    Text(
                        text = note.chapter,
                        style = MaterialTheme.typography.bodySmall.copy(fontSize = 12.sp, color = MaterialTheme.colorScheme.primary)
                    )
                    Spacer(modifier = Modifier.height(4.dp))
                    Text(
                        text = note.summary,
                        style = MaterialTheme.typography.bodySmall.copy(fontSize = 12.sp, color = MaterialTheme.colorScheme.onSurfaceVariant)
                    )

                    Spacer(modifier = Modifier.height(10.dp))
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text(
                            text = "🏷️ ${note.tags} • ${note.fileSize}",
                            style = MaterialTheme.typography.labelSmall.copy(color = MaterialTheme.colorScheme.onSurfaceVariant, fontSize = 11.sp)
                        )
                        FilledTonalButton(
                            onClick = {},
                            shape = RoundedCornerShape(8.dp),
                            contentPadding = PaddingValues(horizontal = 12.dp, vertical = 4.dp)
                        ) {
                            Icon(imageVector = Icons.Default.Download, contentDescription = null, modifier = Modifier.size(14.dp))
                            Spacer(modifier = Modifier.width(4.dp))
                            Text("Open PDF", fontSize = 11.sp)
                        }
                    }
                }
            }
        }
    }
}

@Composable
fun ExamScheduleTab(
    exams: List<ExamEntity>,
    activeYear: String = "All",
    onAddExamClick: () -> Unit
) {

    val filteredExams = if (activeYear == "All") {
        exams
    } else {
        val yrPrefix = activeYear.take(3)
        exams.filter { it.academicYear.startsWith(yrPrefix) || it.academicYear.contains(yrPrefix) }
    }

    LazyColumn(
        modifier = Modifier.fillMaxSize(),
        contentPadding = PaddingValues(16.dp),
        verticalArrangement = Arrangement.spacedBy(14.dp)
    ) {
        item {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Column {
                    Text(
                        text = "Mid & End-Semester Timetable",
                        style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold)
                    )
                    Text(
                        text = if (activeYear == "All") "All scheduled exams" else "Exams for $activeYear",
                        style = MaterialTheme.typography.labelSmall.copy(color = Indigo600, fontWeight = FontWeight.SemiBold)
                    )
                }
                Button(
                    onClick = onAddExamClick,
                    shape = RoundedCornerShape(10.dp),
                    contentPadding = PaddingValues(horizontal = 12.dp, vertical = 6.dp)
                ) {
                    Icon(imageVector = Icons.Default.Add, contentDescription = null, modifier = Modifier.size(16.dp))
                    Spacer(modifier = Modifier.width(4.dp))
                    Text("Add Exam", fontSize = 12.sp)
                }
            }
        }

        if (filteredExams.isEmpty()) {
            item {
                Card(
                    shape = RoundedCornerShape(16.dp),
                    colors = CardDefaults.cardColors(containerColor = Slate100),
                    modifier = Modifier.fillMaxWidth().padding(vertical = 12.dp)
                ) {
                    Column(
                        modifier = Modifier.padding(24.dp).fillMaxWidth(),
                        horizontalAlignment = Alignment.CenterHorizontally
                    ) {
                        Icon(imageVector = Icons.Default.EventAvailable, contentDescription = null, tint = Slate500, modifier = Modifier.size(40.dp))
                        Spacer(modifier = Modifier.height(8.dp))
                        Text("No exams scheduled for $activeYear", fontWeight = FontWeight.Bold, color = Slate800)
                        Text("You are all caught up!", fontSize = 12.sp, color = Slate600)
                    }
                }
            }
        }

        items(filteredExams) { exam ->
            Card(
                shape = RoundedCornerShape(16.dp),
                colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
                elevation = CardDefaults.cardElevation(defaultElevation = 2.dp),
                modifier = Modifier.fillMaxWidth()
            ) {
                Column(modifier = Modifier.padding(16.dp)) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            StatusBadge(
                                text = exam.subjectCode,
                                color = Indigo600,
                                bgColor = Indigo50
                            )
                            Spacer(modifier = Modifier.width(6.dp))
                            Surface(
                                shape = RoundedCornerShape(6.dp),
                                color = Indigo50
                            ) {
                                Text(
                                    text = "${exam.academicYear} • ${exam.semester}",
                                    style = MaterialTheme.typography.labelSmall.copy(
                                        color = Indigo700,
                                        fontWeight = FontWeight.Bold,
                                        fontSize = 10.sp
                                    ),
                                    modifier = Modifier.padding(horizontal = 6.dp, vertical = 2.dp)
                                )
                            }
                        }
                        StatusBadge(
                            text = "${exam.daysRemaining} Days Left",
                            color = Rose500,
                            bgColor = Rose400.copy(alpha = 0.15f)
                        )
                    }

                    Spacer(modifier = Modifier.height(8.dp))
                    Text(
                        text = exam.subjectName,
                        style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold),
                        color = MaterialTheme.colorScheme.onSurface
                    )
                    Text(
                        text = "📅 Date: ${exam.date} • ⏰ ${exam.time}",
                        style = MaterialTheme.typography.bodySmall.copy(
                            fontSize = 12.sp,
                            fontWeight = FontWeight.SemiBold,
                            color = Slate800
                        )
                    )
                    Text(
                        text = "📍 Venue: ${exam.venue}",
                        style = MaterialTheme.typography.bodySmall.copy(
                            fontSize = 12.sp,
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                    )

                    Spacer(modifier = Modifier.height(10.dp))
                    Surface(
                        color = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.5f),
                        shape = RoundedCornerShape(10.dp),
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        Column(modifier = Modifier.padding(10.dp)) {
                            Text(
                                text = "📖 Syllabus Overview:",
                                style = MaterialTheme.typography.labelSmall.copy(
                                    fontWeight = FontWeight.Bold,
                                    color = MaterialTheme.colorScheme.primary
                                )
                            )
                            Spacer(modifier = Modifier.height(2.dp))
                            Text(
                                text = exam.syllabus,
                                style = MaterialTheme.typography.bodySmall.copy(
                                    fontSize = 11.sp,
                                    color = MaterialTheme.colorScheme.onSurface
                                )
                            )
                        }
                    }
                }
            }
        }
    }
}

@Composable
fun McqQuizRunnerTab(
    questions: List<QuizQuestionEntity>
) {
    var currentIndex by remember { mutableIntStateOf(0) }
    var selectedOption by remember { mutableStateOf<Int?>(null) }
    var showExplanation by remember { mutableStateOf(false) }
    var score by remember { mutableIntStateOf(0) }

    if (questions.isEmpty()) {
        Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
            Text("No quiz questions loaded.")
        }
        return
    }

    val currentQ = questions[currentIndex.coerceIn(0, questions.size - 1)]

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp)
            .verticalScroll(rememberScrollState())
    ) {

        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            StatusBadge(
                text = "${currentQ.subject} (${currentQ.difficulty})",
                color = Indigo600,
                bgColor = Indigo50
            )
            Text(
                text = "Question ${currentIndex + 1}/${questions.size} • Score: $score",
                style = MaterialTheme.typography.labelMedium.copy(fontWeight = FontWeight.Bold)
            )
        }

        Spacer(modifier = Modifier.height(14.dp))


        Card(
            shape = RoundedCornerShape(18.dp),
            colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
            elevation = CardDefaults.cardElevation(defaultElevation = 2.dp),
            modifier = Modifier.fillMaxWidth()
        ) {
            Column(modifier = Modifier.padding(18.dp)) {
                Text(
                    text = currentQ.question,
                    style = MaterialTheme.typography.titleMedium.copy(
                        fontWeight = FontWeight.Bold,
                        fontSize = 16.sp
                    ),
                    color = MaterialTheme.colorScheme.onSurface
                )
            }
        }

        Spacer(modifier = Modifier.height(14.dp))


        val options = listOf(currentQ.optionA, currentQ.optionB, currentQ.optionC, currentQ.optionD)

        options.forEachIndexed { optIndex, optText ->
            val isSelected = selectedOption == optIndex
            val isCorrect = optIndex == currentQ.correctIndex

            val borderColor = when {
                showExplanation && isCorrect -> Emerald500
                showExplanation && isSelected && !isCorrect -> Rose500
                isSelected -> Indigo600
                else -> MaterialTheme.colorScheme.outline
            }

            val bgColor = when {
                showExplanation && isCorrect -> Emerald500.copy(alpha = 0.12f)
                showExplanation && isSelected && !isCorrect -> Rose500.copy(alpha = 0.12f)
                isSelected -> Indigo50
                else -> MaterialTheme.colorScheme.surface
            }

            Card(
                shape = RoundedCornerShape(14.dp),
                colors = CardDefaults.cardColors(containerColor = bgColor),
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(vertical = 4.dp)
                    .border(1.5.dp, borderColor, RoundedCornerShape(14.dp))
                    .clickable(enabled = !showExplanation) {
                        selectedOption = optIndex
                        showExplanation = true
                        if (optIndex == currentQ.correctIndex) {
                            score += 1
                        }
                    }
                    .testTag("quiz_option_$optIndex")
            ) {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(14.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    val label = when (optIndex) {
                        0 -> "A"; 1 -> "B"; 2 -> "C"; else -> "D"
                    }
                    Box(
                        modifier = Modifier
                            .size(30.dp)
                            .clip(CircleShape)
                            .background(borderColor.copy(alpha = 0.2f)),
                        contentAlignment = Alignment.Center
                    ) {
                        Text(
                            text = label,
                            style = MaterialTheme.typography.labelMedium.copy(
                                fontWeight = FontWeight.Bold,
                                color = borderColor
                            )
                        )
                    }
                    Spacer(modifier = Modifier.width(12.dp))
                    Text(
                        text = optText,
                        style = MaterialTheme.typography.bodyMedium.copy(fontWeight = FontWeight.Medium),
                        color = MaterialTheme.colorScheme.onSurface
                    )
                }
            }
        }


        if (showExplanation) {
            Spacer(modifier = Modifier.height(14.dp))
            Card(
                shape = RoundedCornerShape(14.dp),
                colors = CardDefaults.cardColors(
                    containerColor = if (selectedOption == currentQ.correctIndex)
                        Emerald500.copy(alpha = 0.08f)
                    else Rose500.copy(alpha = 0.08f)
                ),
                modifier = Modifier.fillMaxWidth()
            ) {
                Column(modifier = Modifier.padding(14.dp)) {
                    Text(
                        text = if (selectedOption == currentQ.correctIndex) "🎉 Correct!" else "❌ Incorrect",
                        style = MaterialTheme.typography.titleSmall.copy(
                            fontWeight = FontWeight.Bold,
                            color = if (selectedOption == currentQ.correctIndex) Emerald600 else Rose600
                        )
                    )
                    Spacer(modifier = Modifier.height(4.dp))
                    Text(
                        text = currentQ.explanation,
                        style = MaterialTheme.typography.bodySmall.copy(fontSize = 12.sp),
                        color = MaterialTheme.colorScheme.onSurface
                    )
                }
            }

            Spacer(modifier = Modifier.height(12.dp))
            Button(
                onClick = {
                    if (currentIndex < questions.size - 1) {
                        currentIndex += 1
                        selectedOption = null
                        showExplanation = false
                    } else {

                        currentIndex = 0
                        selectedOption = null
                        showExplanation = false
                        score = 0
                    }
                },
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(12.dp)
            ) {
                Text(if (currentIndex < questions.size - 1) "Next Question →" else "Restart Quiz 🔄")
            }
        }
    }
}

@Composable
fun AiDoubtSolverTab(
    aiResult: String?,
    isLoading: Boolean,
    onAskDoubt: (String, String, String) -> Unit
) {
    var subject by remember { mutableStateOf("DBMS") }
    var questionText by remember { mutableStateOf("") }
    var difficulty by remember { mutableStateOf("Medium") }

    val subjects = listOf("DBMS", "Algorithms", "Operating Systems", "Computer Networks", "Machine Learning")
    val difficulties = listOf("Easy", "Medium", "Hard")

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp)
            .verticalScroll(rememberScrollState()),
        verticalArrangement = Arrangement.spacedBy(12.dp)
    ) {
        Text(
            text = "AI Doubt Solver & Concept Explainer",
            style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold)
        )
        Text(
            text = "Ask any syllabus doubt, proof, or numerical problem. Select difficulty for tailored explanations.",
            style = MaterialTheme.typography.bodySmall.copy(color = MaterialTheme.colorScheme.onSurfaceVariant)
        )


        Text(text = "Select Subject:", style = MaterialTheme.typography.labelMedium.copy(fontWeight = FontWeight.SemiBold))
        LazyRow(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
            items(subjects) { s ->
                FilterChip(
                    selected = subject == s,
                    onClick = { subject = s },
                    label = { Text(s, fontSize = 12.sp) }
                )
            }
        }


        Text(text = "Explanation Depth:", style = MaterialTheme.typography.labelMedium.copy(fontWeight = FontWeight.SemiBold))
        Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
            difficulties.forEach { d ->
                FilterChip(
                    selected = difficulty == d,
                    onClick = { difficulty = d },
                    label = { Text(d, fontSize = 12.sp) }
                )
            }
        }


        OutlinedTextField(
            value = questionText,
            onValueChange = { questionText = it },
            label = { Text("What concept or question would you like explained?") },
            placeholder = { Text("e.g. Explain DBMS Normalization 1NF to BCNF with student database example.") },
            minLines = 3,
            maxLines = 6,
            modifier = Modifier
                .fillMaxWidth()
                .testTag("ai_doubt_input_field")
        )

        Button(
            onClick = {
                if (questionText.isNotBlank()) {
                    onAskDoubt(subject, questionText, difficulty)
                }
            },
            enabled = questionText.isNotBlank() && !isLoading,
            modifier = Modifier
                .fillMaxWidth()
                .testTag("ai_doubt_solve_button"),
            shape = RoundedCornerShape(12.dp)
        ) {
            if (isLoading) {
                CircularProgressIndicator(color = Color.White, modifier = Modifier.size(20.dp), strokeWidth = 2.dp)
                Spacer(modifier = Modifier.width(8.dp))
                Text("Analyzing with Campus AI...")
            } else {
                Icon(imageVector = Icons.Default.AutoAwesome, contentDescription = null, modifier = Modifier.size(18.dp))
                Spacer(modifier = Modifier.width(8.dp))
                Text("Solve with Campus AI", fontWeight = FontWeight.Bold)
            }
        }


        Text(text = "Try asking:", style = MaterialTheme.typography.labelSmall.copy(color = MaterialTheme.colorScheme.onSurfaceVariant))
        LazyRow(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
            val presets = listOf(
                "Explain DBMS normalization in simple language.",
                "Explain Dijkstra vs Bellman-Ford step by step.",
                "How does Banker's deadlock algorithm work?",
                "What is TCP 3-way handshake with diagram?"
            )
            items(presets) { preset ->
                SuggestionChip(
                    onClick = {
                        questionText = preset
                        onAskDoubt(subject, preset, difficulty)
                    },
                    label = { Text(preset, fontSize = 11.sp, maxLines = 1) }
                )
            }
        }


        if (!aiResult.isNullOrBlank()) {
            Spacer(modifier = Modifier.height(8.dp))
            Card(
                shape = RoundedCornerShape(16.dp),
                colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
                elevation = CardDefaults.cardElevation(defaultElevation = 2.dp),
                modifier = Modifier.fillMaxWidth()
            ) {
                Column(modifier = Modifier.padding(16.dp)) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Icon(
                                imageVector = Icons.Default.CheckCircle,
                                contentDescription = null,
                                tint = Emerald600,
                                modifier = Modifier.size(20.dp)
                            )
                            Spacer(modifier = Modifier.width(8.dp))
                            Text(
                                text = "Campus AI Explanation ($difficulty)",
                                style = MaterialTheme.typography.titleSmall.copy(fontWeight = FontWeight.Bold),
                                color = MaterialTheme.colorScheme.primary
                            )
                        }
                    }
                    Spacer(modifier = Modifier.height(10.dp))
                    Text(
                        text = aiResult,
                        style = MaterialTheme.typography.bodySmall.copy(
                            lineHeight = 20.sp,
                            color = MaterialTheme.colorScheme.onSurface
                        )
                    )
                }
            }
        }
    }
}



@Composable
fun AddTaskModal(
    onDismiss: () -> Unit,
    onAdd: (String, String, String, String, Int) -> Unit
) {
    var title by remember { mutableStateOf("") }
    var subject by remember { mutableStateOf("DBMS") }
    var dueDate by remember { mutableStateOf("Tomorrow, 6:00 PM") }
    var priority by remember { mutableStateOf("Medium") }
    var mins by remember { mutableStateOf("45") }

    AlertDialog(
        onDismissRequest = onDismiss,
        title = { Text("Add Study Task", fontWeight = FontWeight.Bold) },
        text = {
            Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                OutlinedTextField(
                    value = title,
                    onValueChange = { title = it },
                    label = { Text("Task Title") },
                    placeholder = { Text("e.g. Revise Chapter 3 BCNF proofs") },
                    modifier = Modifier.fillMaxWidth()
                )
                OutlinedTextField(
                    value = subject,
                    onValueChange = { subject = it },
                    label = { Text("Subject") },
                    modifier = Modifier.fillMaxWidth()
                )
                OutlinedTextField(
                    value = dueDate,
                    onValueChange = { dueDate = it },
                    label = { Text("Due Date / Time") },
                    modifier = Modifier.fillMaxWidth()
                )
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    listOf("High", "Medium", "Low").forEach { p ->
                        FilterChip(
                            selected = priority == p,
                            onClick = { priority = p },
                            label = { Text(p) }
                        )
                    }
                }
            }
        },
        confirmButton = {
            Button(
                onClick = {
                    if (title.isNotBlank()) {
                        onAdd(title, subject, dueDate, priority, mins.toIntOrNull() ?: 45)
                    }
                },
                enabled = title.isNotBlank()
            ) {
                Text("Add Task")
            }
        },
        dismissButton = {
            TextButton(onClick = onDismiss) { Text("Cancel") }
        }
    )
}

@Composable
fun AddNoteModal(
    onDismiss: () -> Unit,
    onAdd: (String, String, String, String, String) -> Unit
) {
    var title by remember { mutableStateOf("") }
    var subject by remember { mutableStateOf("DBMS") }
    var chapter by remember { mutableStateOf("Unit 2") }
    var summary by remember { mutableStateOf("") }
    var tags by remember { mutableStateOf("PYQ, Formula Sheet") }

    AlertDialog(
        onDismissRequest = onDismiss,
        title = { Text("Upload Study Notes / Doc", fontWeight = FontWeight.Bold) },
        text = {
            Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                OutlinedTextField(value = title, onValueChange = { title = it }, label = { Text("Document Title") }, modifier = Modifier.fillMaxWidth())
                OutlinedTextField(value = subject, onValueChange = { subject = it }, label = { Text("Subject") }, modifier = Modifier.fillMaxWidth())
                OutlinedTextField(value = chapter, onValueChange = { chapter = it }, label = { Text("Chapter / Unit") }, modifier = Modifier.fillMaxWidth())
                OutlinedTextField(value = summary, onValueChange = { summary = it }, label = { Text("Summary / Highlights") }, minLines = 2, modifier = Modifier.fillMaxWidth())
                OutlinedTextField(value = tags, onValueChange = { tags = it }, label = { Text("Tags (comma-separated)") }, modifier = Modifier.fillMaxWidth())
            }
        },
        confirmButton = {
            Button(
                onClick = {
                    if (title.isNotBlank()) {
                        onAdd(subject, title, chapter, summary, tags)
                    }
                },
                enabled = title.isNotBlank()
            ) { Text("Upload") }
        },
        dismissButton = { TextButton(onClick = onDismiss) { Text("Cancel") } }
    )
}

@Composable
fun AddExamModal(
    onDismiss: () -> Unit,
    onAdd: (String, String, String, String, String, String) -> Unit
) {
    var code by remember { mutableStateOf("CS605") }
    var name by remember { mutableStateOf("") }
    var date by remember { mutableStateOf("2026-09-12") }
    var time by remember { mutableStateOf("09:30 AM - 12:30 PM") }
    var venue by remember { mutableStateOf("Hall B4") }
    var syllabus by remember { mutableStateOf("Units 1 to 5") }

    AlertDialog(
        onDismissRequest = onDismiss,
        title = { Text("Add Exam Schedule", fontWeight = FontWeight.Bold) },
        text = {
            Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                OutlinedTextField(value = code, onValueChange = { code = it }, label = { Text("Subject Code") }, modifier = Modifier.fillMaxWidth())
                OutlinedTextField(value = name, onValueChange = { name = it }, label = { Text("Subject Name") }, modifier = Modifier.fillMaxWidth())
                OutlinedTextField(value = date, onValueChange = { date = it }, label = { Text("Date (YYYY-MM-DD)") }, modifier = Modifier.fillMaxWidth())
                OutlinedTextField(value = time, onValueChange = { time = it }, label = { Text("Time Range") }, modifier = Modifier.fillMaxWidth())
                OutlinedTextField(value = venue, onValueChange = { venue = it }, label = { Text("Venue / Hall") }, modifier = Modifier.fillMaxWidth())
                OutlinedTextField(value = syllabus, onValueChange = { syllabus = it }, label = { Text("Syllabus Details") }, minLines = 2, modifier = Modifier.fillMaxWidth())
            }
        },
        confirmButton = {
            Button(
                onClick = {
                    if (name.isNotBlank()) {
                        onAdd(code, name, date, time, venue, syllabus)
                    }
                },
                enabled = name.isNotBlank()
            ) { Text("Save Exam") }
        },
        dismissButton = { TextButton(onClick = onDismiss) { Text("Cancel") } }
    )
}

@Composable
fun GeneratePlanModal(
    isLoading: Boolean,
    onDismiss: () -> Unit,
    onGenerate: (String, Int, Int) -> Unit
) {
    var subjects by remember { mutableStateOf("DBMS, Algorithms, OS, Computer Networks") }
    var days by remember { mutableStateOf("7") }
    var hours by remember { mutableStateOf("4") }

    AlertDialog(
        onDismissRequest = onDismiss,
        title = { Text("Create AI Study Timetable", fontWeight = FontWeight.Bold) },
        text = {
            Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                Text("Configure your exam sprint parameters:", style = MaterialTheme.typography.bodySmall)
                OutlinedTextField(
                    value = subjects,
                    onValueChange = { subjects = it },
                    label = { Text("Subjects to Revise") },
                    modifier = Modifier.fillMaxWidth()
                )
                OutlinedTextField(
                    value = days,
                    onValueChange = { days = it },
                    label = { Text("Number of Days (e.g. 7)") },
                    modifier = Modifier.fillMaxWidth()
                )
                OutlinedTextField(
                    value = hours,
                    onValueChange = { hours = it },
                    label = { Text("Available Study Hours / Day") },
                    modifier = Modifier.fillMaxWidth()
                )
            }
        },
        confirmButton = {
            Button(
                onClick = {
                    onGenerate(subjects, days.toIntOrNull() ?: 7, hours.toIntOrNull() ?: 4)
                },
                enabled = !isLoading
            ) {
                Text("Generate Plan")
            }
        },
        dismissButton = { TextButton(onClick = onDismiss) { Text("Cancel") } }
    )
}

@Composable
fun ChangeYearModal(
    currentYear: String,
    currentSemester: String,
    onDismiss: () -> Unit,
    onSave: (String, String) -> Unit
) {
    val yearOptions = listOf(
        "1st Year (Freshman)",
        "2nd Year (Sophomore)",
        "3rd Year (Junior)",
        "4th Year (Senior)"
    )
    val semOptions = listOf(
        "Semester 1", "Semester 2", "Semester 3", "Semester 4",
        "Semester 5", "Semester 6", "Semester 7", "Semester 8"
    )

    var selectedYear by remember {
        mutableStateOf(yearOptions.firstOrNull { it.startsWith(currentYear.take(3)) } ?: yearOptions[2])
    }
    var selectedSemester by remember {
        mutableStateOf(semOptions.firstOrNull { it.equals(currentSemester, ignoreCase = true) } ?: semOptions[4])
    }

    AlertDialog(
        onDismissRequest = onDismiss,
        title = {
            Row(verticalAlignment = Alignment.CenterVertically) {
                Icon(imageVector = Icons.Default.School, contentDescription = null, tint = Indigo600)
                Spacer(modifier = Modifier.width(8.dp))
                Text("Select Academic Year & Sem", fontWeight = FontWeight.Bold)
            }
        },
        text = {
            Column(verticalArrangement = Arrangement.spacedBy(12.dp)) {
                Text(
                    text = "Setting your current academic stage tailors notes, exam timetables, syllabus questions, and AI doubt solvers to your specific curriculum.",
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )

                Text(
                    text = "Academic Year:",
                    style = MaterialTheme.typography.labelMedium.copy(fontWeight = FontWeight.Bold)
                )

                yearOptions.forEach { yr ->
                    val isSelected = selectedYear == yr
                    Surface(
                        shape = RoundedCornerShape(10.dp),
                        color = if (isSelected) Indigo50 else MaterialTheme.colorScheme.surface,
                        border = androidx.compose.foundation.BorderStroke(
                            1.dp,
                            if (isSelected) Indigo600 else Slate300
                        ),
                        modifier = Modifier
                            .fillMaxWidth()
                            .clickable { selectedYear = yr }
                    ) {
                        Row(
                            modifier = Modifier.padding(horizontal = 12.dp, vertical = 8.dp),
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            RadioButton(
                                selected = isSelected,
                                onClick = { selectedYear = yr },
                                colors = RadioButtonDefaults.colors(selectedColor = Indigo600)
                            )
                            Spacer(modifier = Modifier.width(6.dp))
                            Text(
                                text = yr,
                                style = MaterialTheme.typography.bodyMedium.copy(
                                    fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Normal,
                                    color = if (isSelected) Indigo900 else MaterialTheme.colorScheme.onSurface
                                )
                            )
                        }
                    }
                }

                Text(
                    text = "Active Semester:",
                    style = MaterialTheme.typography.labelMedium.copy(fontWeight = FontWeight.Bold)
                )

                LazyRow(horizontalArrangement = Arrangement.spacedBy(6.dp)) {
                    items(semOptions) { sem ->
                        FilterChip(
                            selected = selectedSemester == sem,
                            onClick = { selectedSemester = sem },
                            label = { Text(sem, fontSize = 11.sp) }
                        )
                    }
                }
            }
        },
        confirmButton = {
            Button(
                onClick = {
                    onSave(selectedYear, selectedSemester)
                }
            ) {
                Text("Save & Apply Syllabus")
            }
        },
        dismissButton = { TextButton(onClick = onDismiss) { Text("Cancel") } }
    )
}
