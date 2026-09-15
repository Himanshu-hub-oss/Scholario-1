package com.example.ui.screens

import androidx.compose.animation.*
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
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
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.example.data.model.QuizHistoryEntity
import com.example.data.model.QuizQuestionEntity
import com.example.ui.components.SectionHeader
import com.example.ui.graphify.GraphifyLineChart
import com.example.ui.graphify.GraphifyPoint
import com.example.ui.theme.*
import com.example.ui.viewmodel.CampusViewModel
import kotlinx.coroutines.delay

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun QuizScreen(
    viewModel: CampusViewModel,
    onBack: () -> Unit,
    modifier: Modifier = Modifier
) {
    val quizHistory by viewModel.quizHistory.collectAsStateWithLifecycle()
    val allQuestions by viewModel.quizQuestions.collectAsStateWithLifecycle()

    var isQuizActive by remember { mutableStateOf(false) }
    var selectedSubject by remember { mutableStateOf("Database Systems") }
    var selectedTopic by remember { mutableStateOf("BCNF & 3NF Normalization") }
    var selectedDifficulty by remember { mutableStateOf("Medium") }
    var currentQuestionIndex by remember { mutableIntStateOf(0) }
    var selectedOptionIndex by remember { mutableStateOf<Int?>(null) }
    var isAnswerSubmitted by remember { mutableStateOf(false) }
    var userScore by remember { mutableIntStateOf(0) }
    var isQuizFinished by remember { mutableStateOf(false) }
    var timeRemainingSec by remember { mutableIntStateOf(30) }

    val subjects = listOf("Database Systems", "Operating Systems", "Data Structures", "Computer Networks", "System Design")
    val difficulties = listOf("Easy", "Medium", "Hard")


    val activeQuizList = remember(allQuestions, selectedSubject, isQuizActive) {
        if (allQuestions.isNotEmpty()) allQuestions.take(5)
        else listOf(
            QuizQuestionEntity(
                subject = "Database Systems",
                topic = "Normalization",
                difficulty = "Medium",
                question = "In relational databases, which normal form strictly guarantees no transitive functional dependencies?",
                optionA = "1st Normal Form (1NF)",
                optionB = "2nd Normal Form (2NF)",
                optionC = "3rd Normal Form (3NF)",
                optionD = "Boyce-Codd Normal Form (BCNF)",
                correctIndex = 2,
                explanation = "3NF eliminates transitive dependencies where a non-prime attribute depends on another non-prime attribute."
            ),
            QuizQuestionEntity(
                subject = "Operating Systems",
                topic = "Deadlocks",
                difficulty = "Hard",
                question = "Which of the following is NOT one of Coffman's four conditions for deadlock?",
                optionA = "Mutual Exclusion",
                optionB = "Preemption allowed",
                optionC = "Hold and Wait",
                optionD = "Circular Wait",
                correctIndex = 1,
                explanation = "Deadlock requires NO PREEMPTION (resources cannot be forcibly taken away)."
            ),
            QuizQuestionEntity(
                subject = "Data Structures",
                topic = "Trees",
                difficulty = "Medium",
                question = "What is the maximum number of nodes in a binary tree of height 'h' (where root is height 0)?",
                optionA = "2^(h+1) - 1",
                optionB = "2^h - 1",
                optionC = "2h",
                optionD = "h^2",
                correctIndex = 0,
                explanation = "Sum of 2^i for i=0 to h gives 2^(h+1) - 1."
            )
        )
    }


    LaunchedEffect(isQuizActive, currentQuestionIndex, isAnswerSubmitted) {
        if (isQuizActive && !isAnswerSubmitted && !isQuizFinished) {
            timeRemainingSec = 30
            while (timeRemainingSec > 0 && !isAnswerSubmitted) {
                delay(1000)
                timeRemainingSec--
            }
            if (timeRemainingSec == 0 && !isAnswerSubmitted) {
                isAnswerSubmitted = true
            }
        }
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text(if (isQuizActive) "Campus Quiz Runner" else "Interactive Quizzes") },
                navigationIcon = {
                    IconButton(onClick = {
                        if (isQuizActive) {
                            isQuizActive = false
                            isQuizFinished = false
                        } else onBack()
                    }) {
                        Icon(Icons.Filled.ArrowBack, contentDescription = "Back")
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = MaterialTheme.colorScheme.surface,
                    titleContentColor = MaterialTheme.colorScheme.onSurface
                )
            )
        }
    ) { padding ->
        if (isQuizFinished) {

            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(padding)
                    .padding(24.dp),
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.Center
            ) {
                Box(
                    modifier = Modifier
                        .size(100.dp)
                        .clip(CircleShape)
                        .background(if (userScore >= activeQuizList.size / 2) Emerald100 else Amber100),
                    contentAlignment = Alignment.Center
                ) {
                    Text(
                        text = if (userScore >= activeQuizList.size / 2) "🏆" else "📚",
                        fontSize = 48.sp
                    )
                }

                Spacer(Modifier.height(16.dp))

                Text(
                    text = "Quiz Finished!",
                    style = MaterialTheme.typography.headlineMedium.copy(fontWeight = FontWeight.Bold)
                )

                Text(
                    text = "You scored $userScore out of ${activeQuizList.size} (${((userScore.toFloat() / activeQuizList.size) * 100).toInt()}%)",
                    style = MaterialTheme.typography.titleMedium,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )

                Spacer(Modifier.height(12.dp))

                Surface(
                    shape = RoundedCornerShape(12.dp),
                    color = Amber500.copy(alpha = 0.15f)
                ) {
                    Text(
                        text = "+${userScore * 10} XP Earned",
                        style = MaterialTheme.typography.labelLarge.copy(fontWeight = FontWeight.Bold),
                        color = Amber700,
                        modifier = Modifier.padding(horizontal = 16.dp, vertical = 8.dp)
                    )
                }

                Spacer(Modifier.height(32.dp))

                Button(
                    onClick = {
                        isQuizActive = false
                        isQuizFinished = false
                    },
                    shape = RoundedCornerShape(12.dp),
                    colors = ButtonDefaults.buttonColors(containerColor = Indigo600),
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Text("Back to Quizzes")
                }
            }
        } else if (isQuizActive && activeQuizList.isNotEmpty()) {
            val q = activeQuizList[currentQuestionIndex]
            val options = listOf(q.optionA, q.optionB, q.optionC, q.optionD)

            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(padding)
                    .padding(16.dp),
                verticalArrangement = Arrangement.SpaceBetween
            ) {
                Column {

                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text(
                            text = "Question ${currentQuestionIndex + 1}/${activeQuizList.size}",
                            style = MaterialTheme.typography.titleSmall.copy(fontWeight = FontWeight.Bold),
                            color = Indigo600
                        )

                        Surface(
                            shape = RoundedCornerShape(16.dp),
                            color = if (timeRemainingSec <= 5) Rose100 else Indigo50
                        ) {
                            Row(
                                modifier = Modifier.padding(horizontal = 10.dp, vertical = 4.dp),
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Icon(
                                    Icons.Outlined.Timer,
                                    contentDescription = null,
                                    tint = if (timeRemainingSec <= 5) Rose600 else Indigo600,
                                    modifier = Modifier.size(16.dp)
                                )
                                Spacer(Modifier.width(4.dp))
                                Text(
                                    text = "${timeRemainingSec}s",
                                    style = MaterialTheme.typography.labelMedium.copy(fontWeight = FontWeight.Bold),
                                    color = if (timeRemainingSec <= 5) Rose600 else Indigo600
                                )
                            }
                        }
                    }

                    Spacer(Modifier.height(10.dp))

                    LinearProgressIndicator(
                        progress = { (currentQuestionIndex + 1) / activeQuizList.size.toFloat() },
                        modifier = Modifier.fillMaxWidth().height(6.dp).clip(RoundedCornerShape(3.dp)),
                        color = Indigo600,
                        trackColor = Indigo50
                    )

                    Spacer(Modifier.height(20.dp))


                    ElevatedCard(
                        shape = RoundedCornerShape(16.dp),
                        colors = CardDefaults.elevatedCardColors(containerColor = MaterialTheme.colorScheme.surface),
                        elevation = CardDefaults.elevatedCardElevation(defaultElevation = 2.dp),
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        Column(modifier = Modifier.padding(16.dp)) {
                            Surface(
                                shape = RoundedCornerShape(6.dp),
                                color = Indigo50
                            ) {
                                Text(
                                    text = "${q.subject} • ${q.difficulty}",
                                    style = MaterialTheme.typography.labelSmall.copy(fontWeight = FontWeight.Bold),
                                    color = Indigo700,
                                    modifier = Modifier.padding(horizontal = 8.dp, vertical = 3.dp)
                                )
                            }

                            Spacer(Modifier.height(12.dp))

                            Text(
                                text = q.question,
                                style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.SemiBold),
                                color = MaterialTheme.colorScheme.onSurface
                            )
                        }
                    }

                    Spacer(Modifier.height(16.dp))


                    options.forEachIndexed { index, optionText ->
                        val isSelected = selectedOptionIndex == index
                        val isCorrect = index == q.correctIndex

                        val (cardBg, borderColor, textColor) = when {
                            isAnswerSubmitted && isCorrect -> Triple(Emerald50, Emerald500, Emerald900)
                            isAnswerSubmitted && isSelected && !isCorrect -> Triple(Rose50, Rose500, Rose900)
                            isSelected -> Triple(Indigo50, Indigo600, Indigo900)
                            else -> Triple(MaterialTheme.colorScheme.surface, MaterialTheme.colorScheme.outlineVariant, MaterialTheme.colorScheme.onSurface)
                        }

                        Surface(
                            shape = RoundedCornerShape(12.dp),
                            color = cardBg,
                            border = androidx.compose.foundation.BorderStroke(1.5.dp, borderColor),
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(vertical = 5.dp)
                                .clickable(enabled = !isAnswerSubmitted) {
                                    selectedOptionIndex = index
                                }
                        ) {
                            Row(
                                modifier = Modifier.padding(14.dp),
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Box(
                                    modifier = Modifier
                                        .size(28.dp)
                                        .clip(CircleShape)
                                        .background(if (isSelected || (isAnswerSubmitted && isCorrect)) borderColor else MaterialTheme.colorScheme.surfaceVariant),
                                    contentAlignment = Alignment.Center
                                ) {
                                    Text(
                                        text = ('A' + index).toString(),
                                        style = MaterialTheme.typography.labelSmall.copy(fontWeight = FontWeight.Bold),
                                        color = if (isSelected || (isAnswerSubmitted && isCorrect)) Color.White else MaterialTheme.colorScheme.onSurfaceVariant
                                    )
                                }

                                Spacer(Modifier.width(12.dp))

                                Text(
                                    text = optionText,
                                    style = MaterialTheme.typography.bodyMedium.copy(fontWeight = if (isSelected) FontWeight.SemiBold else FontWeight.Normal),
                                    color = textColor,
                                    modifier = Modifier.weight(1f)
                                )
                            }
                        }
                    }


                    if (isAnswerSubmitted) {
                        Spacer(Modifier.height(12.dp))
                        Surface(
                            shape = RoundedCornerShape(12.dp),
                            color = Indigo50,
                            modifier = Modifier.fillMaxWidth()
                        ) {
                            Column(modifier = Modifier.padding(12.dp)) {
                                Row(verticalAlignment = Alignment.CenterVertically) {
                                    Icon(Icons.Filled.Info, contentDescription = null, tint = Indigo600, modifier = Modifier.size(16.dp))
                                    Spacer(Modifier.width(6.dp))
                                    Text("Explanation", style = MaterialTheme.typography.labelMedium.copy(fontWeight = FontWeight.Bold), color = Indigo900)
                                }
                                Spacer(Modifier.height(4.dp))
                                Text(q.explanation, style = MaterialTheme.typography.bodySmall, color = Indigo800)
                            }
                        }
                    }
                }


                Column {
                    if (!isAnswerSubmitted) {
                        Button(
                            onClick = {
                                if (selectedOptionIndex != null) {
                                    isAnswerSubmitted = true
                                    if (selectedOptionIndex == q.correctIndex) {
                                        userScore++
                                    }
                                }
                            },
                            enabled = selectedOptionIndex != null,
                            shape = RoundedCornerShape(12.dp),
                            colors = ButtonDefaults.buttonColors(containerColor = Indigo600),
                            modifier = Modifier.fillMaxWidth()
                        ) {
                            Text("Submit Answer")
                        }
                    } else {
                        Button(
                            onClick = {
                                if (currentQuestionIndex < activeQuizList.size - 1) {
                                    currentQuestionIndex++
                                    selectedOptionIndex = null
                                    isAnswerSubmitted = false
                                } else {

                                    viewModel.submitQuizResult(
                                        subject = selectedSubject,
                                        topic = selectedTopic,
                                        score = userScore,
                                        totalQuestions = activeQuizList.size,
                                        difficulty = selectedDifficulty
                                    )
                                    isQuizFinished = true
                                }
                            },
                            shape = RoundedCornerShape(12.dp),
                            colors = ButtonDefaults.buttonColors(containerColor = Indigo600),
                            modifier = Modifier.fillMaxWidth()
                        ) {
                            Text(if (currentQuestionIndex < activeQuizList.size - 1) "Next Question" else "View Results")
                        }
                    }
                }
            }
        } else {

            LazyColumn(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(padding)
                    .padding(16.dp),
                verticalArrangement = Arrangement.spacedBy(16.dp)
            ) {
                item {
                    Text(
                        text = "Practice & Master Concepts",
                        style = MaterialTheme.typography.headlineSmall.copy(fontWeight = FontWeight.Bold)
                    )
                    Text(
                        text = "AI-generated questions tailored to your semester syllabus",
                        style = MaterialTheme.typography.bodyMedium,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                }


                item {
                    ElevatedCard(
                        shape = RoundedCornerShape(16.dp),
                        colors = CardDefaults.elevatedCardColors(containerColor = MaterialTheme.colorScheme.surface),
                        elevation = CardDefaults.elevatedCardElevation(defaultElevation = 2.dp),
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        Column(modifier = Modifier.padding(16.dp)) {
                            Text(
                                text = "1. Select Subject",
                                style = MaterialTheme.typography.titleSmall.copy(fontWeight = FontWeight.Bold)
                            )
                            Spacer(Modifier.height(8.dp))

                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.spacedBy(8.dp)
                            ) {
                                subjects.take(3).forEach { sub ->
                                    FilterChip(
                                        selected = selectedSubject == sub,
                                        onClick = { selectedSubject = sub },
                                        label = { Text(sub.split(" ").first()) },
                                        shape = RoundedCornerShape(10.dp)
                                    )
                                }
                            }

                            Spacer(Modifier.height(16.dp))

                            Text(
                                text = "2. Select Difficulty",
                                style = MaterialTheme.typography.titleSmall.copy(fontWeight = FontWeight.Bold)
                            )
                            Spacer(Modifier.height(8.dp))

                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.spacedBy(8.dp)
                            ) {
                                difficulties.forEach { diff ->
                                    FilterChip(
                                        selected = selectedDifficulty == diff,
                                        onClick = { selectedDifficulty = diff },
                                        label = { Text(diff) },
                                        shape = RoundedCornerShape(10.dp)
                                    )
                                }
                            }

                            Spacer(Modifier.height(20.dp))

                            Button(
                                onClick = {
                                    currentQuestionIndex = 0
                                    selectedOptionIndex = null
                                    isAnswerSubmitted = false
                                    userScore = 0
                                    isQuizActive = true
                                },
                                shape = RoundedCornerShape(12.dp),
                                colors = ButtonDefaults.buttonColors(containerColor = Indigo600),
                                modifier = Modifier.fillMaxWidth()
                            ) {
                                Icon(Icons.Filled.PlayArrow, contentDescription = null)
                                Spacer(Modifier.width(8.dp))
                                Text("Start 5-Question Quiz")
                            }
                        }
                    }
                }


                item {
                    ElevatedCard(
                        shape = RoundedCornerShape(16.dp),
                        colors = CardDefaults.elevatedCardColors(containerColor = MaterialTheme.colorScheme.surface),
                        elevation = CardDefaults.elevatedCardElevation(defaultElevation = 2.dp),
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        Column(modifier = Modifier.padding(16.dp)) {
                            Text(
                                text = "Graphify Quiz Performance Curve",
                                style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold)
                            )
                            Spacer(Modifier.height(10.dp))

                            val performancePoints = listOf(
                                GraphifyPoint("Test 1", 70f),
                                GraphifyPoint("Test 2", 80f),
                                GraphifyPoint("Test 3", 85f),
                                GraphifyPoint("Test 4", 90f),
                                GraphifyPoint("Latest", 95f)
                            )

                            GraphifyLineChart(
                                points = performancePoints,
                                lineColor = Emerald600,
                                fillColor = Emerald100
                            )
                        }
                    }
                }


                item {
                    SectionHeader(
                        title = "Recent Quiz History",
                        actionText = "Clear",
                        onActionClick = {}
                    )
                }

                items(quizHistory) { hist ->
                    ElevatedCard(
                        shape = RoundedCornerShape(12.dp),
                        colors = CardDefaults.elevatedCardColors(containerColor = MaterialTheme.colorScheme.surface),
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        Row(
                            modifier = Modifier.padding(14.dp),
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.SpaceBetween
                        ) {
                            Column {
                                Text(
                                    text = hist.subject,
                                    style = MaterialTheme.typography.titleSmall.copy(fontWeight = FontWeight.Bold)
                                )
                                Text(
                                    text = "${hist.topic} • ${hist.difficulty}",
                                    style = MaterialTheme.typography.bodySmall,
                                    color = MaterialTheme.colorScheme.onSurfaceVariant
                                )
                            }

                            Column(horizontalAlignment = Alignment.End) {
                                Text(
                                    text = "${hist.score}/${hist.totalQuestions} (${hist.percentage}%)",
                                    style = MaterialTheme.typography.titleSmall.copy(fontWeight = FontWeight.Bold),
                                    color = if (hist.percentage >= 80) Emerald600 else Amber600
                                )
                                Text(
                                    text = "+${hist.xpEarned} XP",
                                    style = MaterialTheme.typography.labelSmall,
                                    color = Amber600
                                )
                            }
                        }
                    }
                }
            }
        }
    }
}
