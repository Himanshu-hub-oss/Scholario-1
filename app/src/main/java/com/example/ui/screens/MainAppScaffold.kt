package com.example.ui.screens

import androidx.compose.animation.*
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material.icons.outlined.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.ui.theme.*
import com.example.ui.viewmodel.CampusViewModel

sealed class Screen(val index: Int, val title: String, val selectedIcon: ImageVector, val unselectedIcon: ImageVector) {
    object Home : Screen(0, "Home", Icons.Filled.Home, Icons.Outlined.Home)
    object Study : Screen(1, "Study", Icons.Filled.AutoStories, Icons.Outlined.AutoStories)
    object Splitzy : Screen(2, "Splitzy", Icons.Filled.AccountBalanceWallet, Icons.Outlined.AccountBalanceWallet)
    object Activity : Screen(3, "Activity", Icons.Filled.Timeline, Icons.Outlined.Timeline)
    object Community : Screen(4, "Community", Icons.Filled.Forum, Icons.Outlined.Forum)
    object Services : Screen(5, "Services", Icons.Filled.Work, Icons.Outlined.WorkOutline)
    object Profile : Screen(6, "Profile", Icons.Filled.Person, Icons.Outlined.PersonOutline)
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun MainAppScaffold(
    viewModel: CampusViewModel = viewModel(),
    modifier: Modifier = Modifier
) {
    var currentScreenIndex by remember { mutableIntStateOf(0) }
    var showAiChatSheet by remember { mutableStateOf(false) }
    var showNotificationsModal by remember { mutableStateOf(false) }
    var showGamificationModal by remember { mutableStateOf(false) }
    var showQuizRunnerScreen by remember { mutableStateOf(false) }
    var showResumeBuilderScreen by remember { mutableStateOf(false) }


    var showAddExpenseModal by remember { mutableStateOf(false) }
    var showSplitBillModal by remember { mutableStateOf(false) }
    var showAddTaskModal by remember { mutableStateOf(false) }
    var showAddNoteModal by remember { mutableStateOf(false) }
    var showCreateGroupModal by remember { mutableStateOf(false) }

    val expenseGroups by viewModel.expenseGroups.collectAsStateWithLifecycle()
    val isUserLoggedIn by viewModel.isUserLoggedIn.collectAsStateWithLifecycle()

    val navItems = listOf(
        Screen.Home,
        Screen.Study,
        Screen.Splitzy,
        Screen.Activity,
        Screen.Community,
        Screen.Services,
        Screen.Profile
    )

    if (!isUserLoggedIn) {
        LoginAndOnboardingScreen(
            viewModel = viewModel,
            onLoginSuccess = { currentScreenIndex = 0 }
        )
    } else if (showQuizRunnerScreen) {
        QuizScreen(
            viewModel = viewModel,
            onBack = { showQuizRunnerScreen = false }
        )
    } else if (showResumeBuilderScreen) {
        ResumeBuilderScreen(
            viewModel = viewModel,
            onBack = { showResumeBuilderScreen = false }
        )
    } else {
        Scaffold(
            bottomBar = {
                NavigationBar(
                    containerColor = MaterialTheme.colorScheme.surface,
                    tonalElevation = 6.dp,
                    modifier = Modifier.testTag("main_bottom_navigation")
                ) {
                    navItems.forEach { screen ->
                        val isSelected = currentScreenIndex == screen.index
                        NavigationBarItem(
                            selected = isSelected,
                            onClick = { currentScreenIndex = screen.index },
                            icon = {
                                Icon(
                                    imageVector = if (isSelected) screen.selectedIcon else screen.unselectedIcon,
                                    contentDescription = screen.title
                                )
                            },
                            label = { Text(screen.title, maxLines = 1) },
                            colors = NavigationBarItemDefaults.colors(
                                selectedIconColor = Indigo600,
                                selectedTextColor = Indigo600,
                                indicatorColor = Indigo50
                            ),
                            modifier = Modifier.testTag("nav_item_${screen.title.lowercase()}")
                        )
                    }
                }
            },
            floatingActionButton = {

                FloatingActionButton(
                    onClick = { showAiChatSheet = true },
                    containerColor = Indigo600,
                    contentColor = Color.White,
                    shape = CircleShape,
                    modifier = Modifier
                        .padding(bottom = 12.dp)
                        .testTag("global_campus_ai_fab")
                ) {
                    Icon(
                        imageVector = Icons.Default.AutoAwesome,
                        contentDescription = "Campus AI Assistant",
                        modifier = Modifier.size(24.dp)
                    )
                }
            },
            modifier = modifier
        ) { innerPadding ->
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(innerPadding)
            ) {
            Crossfade(
                targetState = currentScreenIndex,
                animationSpec = androidx.compose.animation.core.tween(durationMillis = 150),
                label = "screen_crossfade"
            ) { targetIndex ->
                when (targetIndex) {
                    0 -> HomeScreen(
                        viewModel = viewModel,
                        onNavigateToStudy = { currentScreenIndex = 1 },
                        onNavigateToSplitzy = { currentScreenIndex = 2 },
                        onNavigateToCommunity = { currentScreenIndex = 4 },
                        onOpenAiChat = { showAiChatSheet = true },
                        onOpenNotifications = { showNotificationsModal = true },
                        onQuickAddExpense = { showAddExpenseModal = true },
                        onQuickSplitBill = { showSplitBillModal = true },
                        onQuickAddTask = { showAddTaskModal = true },
                        onQuickAddNote = { showAddNoteModal = true },
                        onQuickCreateGroup = { showCreateGroupModal = true },
                        onOpenQuizRunner = { showQuizRunnerScreen = true },
                        onOpenResumeBuilder = { showResumeBuilderScreen = true },
                        onOpenGamification = { showGamificationModal = true },
                        onNavigateToGrievances = { currentScreenIndex = 5 }
                    )
                    1 -> StudyZoneScreen(
                        viewModel = viewModel,
                        onOpenAiChat = { showAiChatSheet = true }
                    )
                    2 -> SplitzyScreen(
                        viewModel = viewModel
                    )
                    3 -> ActivityScreen(
                        viewModel = viewModel,
                        onOpenAiChat = { showAiChatSheet = true }
                    )
                    4 -> CommunityScreen(
                        viewModel = viewModel
                    )
                    5 -> ServicesAndCareerScreen(
                        viewModel = viewModel,
                        onOpenResumeBuilder = { showResumeBuilderScreen = true }
                    )
                    6 -> ProfileAndAdminScreen(
                        viewModel = viewModel
                    )
                }
            }
            }
        }
    }


    if (showAiChatSheet) {
        CampusAiChatSheet(
            viewModel = viewModel,
            onDismiss = { showAiChatSheet = false }
        )
    }


    if (showGamificationModal) {
        GamificationLeaderboardModal(
            viewModel = viewModel,
            onDismiss = { showGamificationModal = false }
        )
    }


    if (showNotificationsModal) {
        NotificationsModal(
            viewModel = viewModel,
            onDismiss = { showNotificationsModal = false }
        )
    }


    if (showAddExpenseModal) {
        AddExpenseModal(
            groups = expenseGroups,
            onDismiss = { showAddExpenseModal = false },
            onAdd = { title, amt, cat, date, desc, paidBy, grpId, grpName, splitType, parts ->
                viewModel.addExpense(title, amt, cat, date, desc, paidBy, grpId, grpName, splitType, parts)
                showAddExpenseModal = false
            }
        )
    }


    if (showSplitBillModal) {
        SplitBillCalculatorModal(
            groups = expenseGroups,
            onDismiss = { showSplitBillModal = false },
            onCalculateAndSave = { title, amt, cat, date, desc, paidBy, grpId, grpName, splitType, parts ->
                viewModel.addExpense(title, amt, cat, date, desc, paidBy, grpId, grpName, splitType, parts)
                showSplitBillModal = false
            }
        )
    }


    if (showAddTaskModal) {
        AddTaskModal(
            onDismiss = { showAddTaskModal = false },
            onAdd = { title, subj, due, priority, mins ->
                viewModel.addTask(title, subj, due, priority, mins)
                showAddTaskModal = false
            }
        )
    }


    if (showAddNoteModal) {
        AddNoteModal(
            onDismiss = { showAddNoteModal = false },
            onAdd = { subj, title, chap, summary, tags ->
                viewModel.addNote(subj, title, chap, summary, tags)
                showAddNoteModal = false
            }
        )
    }


    if (showCreateGroupModal) {
        CreateGroupModal(
            onDismiss = { showCreateGroupModal = false },
            onAdd = { name, cat, members ->
                viewModel.addGroup(name, cat, members)
                showCreateGroupModal = false
            }
        )
    }
}
