package com.example.ui.screens

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.foundation.background
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
import com.example.data.model.ExpenseEntity
import com.example.data.model.ExpenseGroupEntity
import com.example.data.model.SettlementEntity
import com.example.splitzy.BalanceSummary
import com.example.splitzy.CategorySpend
import com.example.splitzy.DebtRelation
import com.example.splitzy.SplitzyCalculator
import com.example.ui.components.MetricStatCard
import com.example.ui.components.QuickActionButton
import com.example.ui.components.SectionHeader
import com.example.ui.components.StatusBadge
import com.example.ui.theme.*
import com.example.ui.viewmodel.CampusViewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SplitzyScreen(
    viewModel: CampusViewModel,
    modifier: Modifier = Modifier
) {
    var selectedTab by remember { mutableIntStateOf(0) }
    val tabs = listOf("Expenses", "Groups", "Settlements & Debt", "Student Wealth & SIPs", "AI Spending Insights")

    val expenses by viewModel.expenses.collectAsStateWithLifecycle()
    val groups by viewModel.expenseGroups.collectAsStateWithLifecycle()
    val settlements by viewModel.settlements.collectAsStateWithLifecycle()
    val balanceSummary by viewModel.balanceSummary.collectAsStateWithLifecycle()
    val debtRelations by viewModel.groupSettlements.collectAsStateWithLifecycle()
    val categorySpends by viewModel.categorySpends.collectAsStateWithLifecycle()
    val aiInsight by viewModel.aiExpenseInsight.collectAsStateWithLifecycle()
    val isAiLoading by viewModel.isAiLoading.collectAsStateWithLifecycle()
    val expenseForecast by viewModel.expenseForecast.collectAsStateWithLifecycle()

    var showAddExpenseDialog by remember { mutableStateOf(false) }
    var showSplitBillDialog by remember { mutableStateOf(false) }
    var showCreateGroupDialog by remember { mutableStateOf(false) }
    var showSettleDialog by remember { mutableStateOf(false) }

    Scaffold(
        topBar = {
            TopAppBar(
                title = {
                    Column {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Text(
                                text = "Splitzy",
                                style = MaterialTheme.typography.titleLarge.copy(
                                    fontWeight = FontWeight.ExtraBold,
                                    color = MaterialTheme.colorScheme.primary
                                )
                            )
                            Spacer(modifier = Modifier.width(6.dp))
                            Surface(
                                shape = RoundedCornerShape(8.dp),
                                color = Emerald500.copy(alpha = 0.15f)
                            ) {
                                Text(
                                    text = "Smart Split Engine",
                                    style = MaterialTheme.typography.labelSmall.copy(
                                        color = Emerald600,
                                        fontWeight = FontWeight.Bold
                                    ),
                                    modifier = Modifier.padding(horizontal = 6.dp, vertical = 2.dp)
                                )
                            }
                        }
                        Text(
                            text = "Manage your money & bill splits with friends",
                            style = MaterialTheme.typography.bodySmall.copy(
                                color = MaterialTheme.colorScheme.onSurfaceVariant
                            )
                        )
                    }
                },
                actions = {
                    IconButton(
                        onClick = { showAddExpenseDialog = true },
                        modifier = Modifier.testTag("splitzy_top_add_expense_button")
                    ) {
                        Icon(imageVector = Icons.Default.AddCircle, contentDescription = "Add Expense", tint = Indigo600)
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(containerColor = MaterialTheme.colorScheme.surface)
            )
        },
        floatingActionButton = {
            ExtendedFloatingActionButton(
                onClick = { showSplitBillDialog = true },
                icon = { Icon(Icons.Default.CallSplit, contentDescription = null) },
                text = { Text("Split Bill", fontWeight = FontWeight.Bold) },
                containerColor = Emerald600,
                contentColor = Color.White,
                modifier = Modifier.testTag("splitzy_fab_split_bill")
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

            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .background(
                        Brush.verticalGradient(
                            colors = listOf(
                                Indigo950,
                                Indigo800,
                                MaterialTheme.colorScheme.surface
                            )
                        )
                    )
                    .padding(16.dp)
            ) {
                Column {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Column {
                            Text(
                                text = "Total Shared Spending",
                                style = MaterialTheme.typography.labelMedium.copy(color = Indigo100)
                            )
                            Text(
                                text = "₹${balanceSummary.totalSpent.toInt()}",
                                style = MaterialTheme.typography.headlineMedium.copy(
                                    fontWeight = FontWeight.ExtraBold,
                                    color = Color.White
                                )
                            )
                        }

                        Surface(
                            shape = RoundedCornerShape(12.dp),
                            color = if (balanceSummary.netBalance >= 0) Emerald500.copy(alpha = 0.25f) else Rose500.copy(alpha = 0.25f)
                        ) {
                            Text(
                                text = if (balanceSummary.netBalance >= 0) "Net: +₹${balanceSummary.netBalance.toInt()}" else "Net: -₹${balanceSummary.youOwe.toInt()}",
                                style = MaterialTheme.typography.labelMedium.copy(
                                    fontWeight = FontWeight.Bold,
                                    color = if (balanceSummary.netBalance >= 0) Emerald300 else Rose300
                                ),
                                modifier = Modifier.padding(horizontal = 10.dp, vertical = 6.dp)
                            )
                        }
                    }

                    Spacer(modifier = Modifier.height(14.dp))


                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(10.dp)
                    ) {
                        Card(
                            shape = RoundedCornerShape(14.dp),
                            colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
                            modifier = Modifier.weight(1f)
                        ) {
                            Column(modifier = Modifier.padding(12.dp)) {
                                Text(
                                    text = "You are owed",
                                    style = MaterialTheme.typography.labelSmall,
                                    color = MaterialTheme.colorScheme.onSurfaceVariant
                                )
                                Text(
                                    text = "₹${balanceSummary.youGet.toInt()}",
                                    style = MaterialTheme.typography.titleLarge.copy(
                                        fontWeight = FontWeight.Bold,
                                        color = Emerald600
                                    )
                                )
                            }
                        }

                        Card(
                            shape = RoundedCornerShape(14.dp),
                            colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
                            modifier = Modifier.weight(1f)
                        ) {
                            Column(modifier = Modifier.padding(12.dp)) {
                                Text(
                                    text = "You owe others",
                                    style = MaterialTheme.typography.labelSmall,
                                    color = MaterialTheme.colorScheme.onSurfaceVariant
                                )
                                Text(
                                    text = "₹${balanceSummary.youOwe.toInt()}",
                                    style = MaterialTheme.typography.titleLarge.copy(
                                        fontWeight = FontWeight.Bold,
                                        color = Rose500
                                    )
                                )
                            }
                        }
                    }
                }
            }


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
                        modifier = Modifier.testTag("splitzy_tab_$index")
                    )
                }
            }


            when (selectedTab) {
                0 -> ExpensesListTab(
                    expenses = expenses,
                    onAddExpense = { showAddExpenseDialog = true },
                    onDeleteExpense = { viewModel.deleteExpense(it) }
                )
                1 -> GroupsListTab(
                    groups = groups,
                    onCreateGroup = { showCreateGroupDialog = true }
                )
                2 -> SettlementsAndDebtTab(
                    debtRelations = debtRelations,
                    settlements = settlements,
                    onSettleUp = { showSettleDialog = true }
                )
                3 -> StudentInvestmentGuideTab()
                4 -> AiFinanceInsightsTab(
                    categorySpends = categorySpends,
                    forecast = expenseForecast,
                    aiInsight = aiInsight,
                    isLoading = isAiLoading,
                    onRunAnalysis = { viewModel.runAiExpenseAnalysis() }
                )
            }
        }
    }


    if (showAddExpenseDialog) {
        AddExpenseModal(
            groups = groups,
            onDismiss = { showAddExpenseDialog = false },
            onAdd = { title, amt, cat, date, desc, paidBy, grpId, grpName, splitType, parts ->
                viewModel.addExpense(title, amt, cat, date, desc, paidBy, grpId, grpName, splitType, parts)
                showAddExpenseDialog = false
            }
        )
    }

    if (showSplitBillDialog) {
        SplitBillCalculatorModal(
            groups = groups,
            onDismiss = { showSplitBillDialog = false },
            onCalculateAndSave = { title, amt, cat, date, desc, paidBy, grpId, grpName, splitType, parts ->
                viewModel.addExpense(title, amt, cat, date, desc, paidBy, grpId, grpName, splitType, parts)
                showSplitBillDialog = false
            }
        )
    }

    if (showCreateGroupDialog) {
        CreateGroupModal(
            onDismiss = { showCreateGroupDialog = false },
            onAdd = { name, cat, members ->
                viewModel.addGroup(name, cat, members)
                showCreateGroupDialog = false
            }
        )
    }

    if (showSettleDialog) {
        RecordSettlementModal(
            groups = groups,
            onDismiss = { showSettleDialog = false },
            onSettle = { payer, receiver, amt, grp, note ->
                viewModel.recordSettlement(payer, receiver, amt, grp, note)
                showSettleDialog = false
            }
        )
    }
}



@Composable
fun ExpensesListTab(
    expenses: List<ExpenseEntity>,
    onAddExpense: () -> Unit,
    onDeleteExpense: (ExpenseEntity) -> Unit
) {
    var selectedCategory by remember { mutableStateOf("All") }
    val categories = listOf("All", "Food", "Bills", "Travel", "Education", "Entertainment", "Shopping")

    val filtered = if (selectedCategory == "All") expenses else expenses.filter { it.category == selectedCategory }

    LazyColumn(
        modifier = Modifier.fillMaxSize(),
        contentPadding = PaddingValues(horizontal = 16.dp, vertical = 12.dp),
        verticalArrangement = Arrangement.spacedBy(10.dp)
    ) {

        item {
            LazyRow(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                items(categories) { cat ->
                    FilterChip(
                        selected = selectedCategory == cat,
                        onClick = { selectedCategory = cat },
                        label = { Text(cat, fontSize = 12.sp) }
                    )
                }
            }
        }

        if (filtered.isEmpty()) {
            item {
                Card(
                    shape = RoundedCornerShape(16.dp),
                    colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Column(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(24.dp),
                        horizontalAlignment = Alignment.CenterHorizontally
                    ) {
                        Icon(imageVector = Icons.Default.ReceiptLong, contentDescription = null, tint = Indigo400, modifier = Modifier.size(48.dp))
                        Spacer(modifier = Modifier.height(8.dp))
                        Text("No expenses found", fontWeight = FontWeight.Bold)
                        Text("Add an expense to start tracking splits.", style = MaterialTheme.typography.bodySmall, color = MaterialTheme.colorScheme.onSurfaceVariant)
                    }
                }
            }
        } else {
            items(filtered) { exp ->
                Card(
                    shape = RoundedCornerShape(16.dp),
                    colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
                    elevation = CardDefaults.cardElevation(defaultElevation = 2.dp),
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Column(modifier = Modifier.padding(14.dp)) {
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Row(verticalAlignment = Alignment.CenterVertically) {
                                Box(
                                    modifier = Modifier
                                        .size(38.dp)
                                        .clip(CircleShape)
                                        .background(Indigo50),
                                    contentAlignment = Alignment.Center
                                ) {
                                    Icon(
                                        imageVector = when (exp.category) {
                                            "Food" -> Icons.Default.Restaurant
                                            "Bills" -> Icons.Default.Receipt
                                            "Travel" -> Icons.Default.DirectionsCar
                                            "Education" -> Icons.Default.School
                                            else -> Icons.Default.ShoppingBag
                                        },
                                        contentDescription = null,
                                        tint = Indigo600,
                                        modifier = Modifier.size(20.dp)
                                    )
                                }
                                Spacer(modifier = Modifier.width(10.dp))
                                Column {
                                    Text(
                                        text = exp.title,
                                        style = MaterialTheme.typography.titleSmall.copy(fontWeight = FontWeight.Bold),
                                        color = MaterialTheme.colorScheme.onSurface
                                    )
                                    Text(
                                        text = "${exp.groupName ?: "Personal"} • Paid by ${exp.paidBy}",
                                        style = MaterialTheme.typography.bodySmall.copy(fontSize = 11.sp),
                                        color = MaterialTheme.colorScheme.onSurfaceVariant
                                    )
                                }
                            }

                            Text(
                                text = "₹${exp.amount.toInt()}",
                                style = MaterialTheme.typography.titleMedium.copy(
                                    fontWeight = FontWeight.ExtraBold,
                                    color = MaterialTheme.colorScheme.primary
                                )
                            )
                        }

                        if (exp.description.isNotBlank()) {
                            Spacer(modifier = Modifier.height(4.dp))
                            Text(
                                text = exp.description,
                                style = MaterialTheme.typography.bodySmall.copy(fontSize = 11.sp),
                                color = MaterialTheme.colorScheme.onSurfaceVariant
                            )
                        }

                        Spacer(modifier = Modifier.height(8.dp))
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            StatusBadge(
                                text = "Split: ${exp.splitType}",
                                color = Cyan700,
                                bgColor = Cyan50
                            )
                            Text(
                                text = exp.date,
                                style = MaterialTheme.typography.labelSmall.copy(
                                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                                    fontSize = 11.sp
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
fun GroupsListTab(
    groups: List<ExpenseGroupEntity>,
    onCreateGroup: () -> Unit
) {
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
                Text(
                    text = "Expense Groups (${groups.size})",
                    style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold)
                )
                Button(
                    onClick = onCreateGroup,
                    shape = RoundedCornerShape(10.dp),
                    contentPadding = PaddingValues(horizontal = 12.dp, vertical = 6.dp),
                    modifier = Modifier.testTag("splitzy_create_group_button")
                ) {
                    Icon(imageVector = Icons.Default.Add, contentDescription = null, modifier = Modifier.size(16.dp))
                    Spacer(modifier = Modifier.width(4.dp))
                    Text("New Group", fontSize = 12.sp)
                }
            }
        }

        items(groups) { group ->
            val members = group.membersJson.split(",").map { it.trim() }
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
                                    .size(42.dp)
                                    .clip(RoundedCornerShape(12.dp))
                                    .background(Indigo600),
                                contentAlignment = Alignment.Center
                            ) {
                                Icon(
                                    imageVector = Icons.Default.Groups,
                                    contentDescription = null,
                                    tint = Color.White,
                                    modifier = Modifier.size(24.dp)
                                )
                            }
                            Spacer(modifier = Modifier.width(12.dp))
                            Column {
                                Text(
                                    text = group.name,
                                    style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold),
                                    color = MaterialTheme.colorScheme.onSurface
                                )
                                Text(
                                    text = "${group.category} • Created ${group.createdDate}",
                                    style = MaterialTheme.typography.bodySmall.copy(fontSize = 11.sp),
                                    color = MaterialTheme.colorScheme.onSurfaceVariant
                                )
                            }
                        }
                    }

                    Spacer(modifier = Modifier.height(12.dp))
                    Text(
                        text = "Members (${members.size}):",
                        style = MaterialTheme.typography.labelSmall.copy(
                            fontWeight = FontWeight.Bold,
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                    )
                    Spacer(modifier = Modifier.height(4.dp))
                    LazyRow(horizontalArrangement = Arrangement.spacedBy(6.dp)) {
                        items(members) { m ->
                            Surface(
                                shape = RoundedCornerShape(20.dp),
                                color = Indigo50
                            ) {
                                Text(
                                    text = m,
                                    style = MaterialTheme.typography.labelSmall.copy(
                                        color = Indigo700,
                                        fontWeight = FontWeight.SemiBold
                                    ),
                                    modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp)
                                )
                            }
                        }
                    }
                }
            }
        }
    }
}

@Composable
fun SettlementsAndDebtTab(
    debtRelations: List<DebtRelation>,
    settlements: List<SettlementEntity>,
    onSettleUp: () -> Unit
) {
    LazyColumn(
        modifier = Modifier.fillMaxSize(),
        contentPadding = PaddingValues(16.dp),
        verticalArrangement = Arrangement.spacedBy(14.dp)
    ) {

        item {
            Card(
                shape = RoundedCornerShape(16.dp),
                colors = CardDefaults.cardColors(containerColor = Cyan900),
                modifier = Modifier.fillMaxWidth()
            ) {
                Column(modifier = Modifier.padding(16.dp)) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Icon(imageVector = Icons.Default.Calculate, contentDescription = null, tint = Cyan400)
                        Spacer(modifier = Modifier.width(8.dp))
                        Text(
                            text = "Splitzy Debt Simplification Engine",
                            style = MaterialTheme.typography.titleSmall.copy(fontWeight = FontWeight.Bold, color = Color.White)
                        )
                    }
                    Spacer(modifier = Modifier.height(4.dp))
                    Text(
                        text = "Calculates minimal cash transactions required across all group members to settle every balance completely.",
                        style = MaterialTheme.typography.bodySmall.copy(color = Cyan100, fontSize = 12.sp)
                    )
                    Spacer(modifier = Modifier.height(10.dp))
                    Button(
                        onClick = onSettleUp,
                        colors = ButtonDefaults.buttonColors(containerColor = Cyan400),
                        shape = RoundedCornerShape(10.dp)
                    ) {
                        Text("Record a Settlement Payment", color = Slate950, fontWeight = FontWeight.Bold)
                    }
                }
            }
        }


        item {
            Text(
                text = "Who Owes Whom (Pending Balances)",
                style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold)
            )
        }

        if (debtRelations.isEmpty()) {
            item {
                Card(
                    shape = RoundedCornerShape(14.dp),
                    colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Row(
                        modifier = Modifier.padding(16.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Icon(imageVector = Icons.Default.CheckCircle, contentDescription = null, tint = Emerald500)
                        Spacer(modifier = Modifier.width(10.dp))
                        Text("All group expenses are completely settled! No outstanding balances.")
                    }
                }
            }
        } else {
            items(debtRelations) { rel ->
                Card(
                    shape = RoundedCornerShape(14.dp),
                    colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
                    elevation = CardDefaults.cardElevation(defaultElevation = 2.dp),
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(16.dp),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Text(
                                text = rel.debtor,
                                style = MaterialTheme.typography.titleSmall.copy(
                                    fontWeight = FontWeight.Bold,
                                    color = if (rel.debtor.equals("You", ignoreCase = true)) Rose500 else MaterialTheme.colorScheme.onSurface
                                )
                            )
                            Spacer(modifier = Modifier.width(6.dp))
                            Text(
                                text = "owes",
                                style = MaterialTheme.typography.bodySmall.copy(color = MaterialTheme.colorScheme.onSurfaceVariant)
                            )
                            Spacer(modifier = Modifier.width(6.dp))
                            Text(
                                text = rel.creditor,
                                style = MaterialTheme.typography.titleSmall.copy(
                                    fontWeight = FontWeight.Bold,
                                    color = if (rel.creditor.equals("You", ignoreCase = true)) Emerald600 else MaterialTheme.colorScheme.onSurface
                                )
                            )
                        }

                        Text(
                            text = "₹${rel.amount.toInt()}",
                            style = MaterialTheme.typography.titleMedium.copy(
                                fontWeight = FontWeight.ExtraBold,
                                color = Indigo600
                            )
                        )
                    }
                }
            }
        }


        item {
            Spacer(modifier = Modifier.height(6.dp))
            Text(
                text = "Settlement Payment Log",
                style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold)
            )
        }

        items(settlements) { s ->
            Card(
                shape = RoundedCornerShape(14.dp),
                colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
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
                        Icon(imageVector = Icons.Default.Check, contentDescription = null, tint = Emerald500)
                        Spacer(modifier = Modifier.width(10.dp))
                        Column {
                            Text(
                                text = "${s.payerName} paid ${s.receiverName}",
                                style = MaterialTheme.typography.bodyMedium.copy(fontWeight = FontWeight.SemiBold)
                            )
                            Text(
                                text = "${s.note ?: "Settlement"} • ${s.groupName ?: "General"}",
                                style = MaterialTheme.typography.bodySmall.copy(fontSize = 11.sp, color = MaterialTheme.colorScheme.onSurfaceVariant)
                            )
                        }
                    }
                    Text(
                        text = "₹${s.amount.toInt()}",
                        style = MaterialTheme.typography.titleSmall.copy(fontWeight = FontWeight.Bold, color = Emerald600)
                    )
                }
            }
        }
    }
}

@Composable
fun AiFinanceInsightsTab(
    categorySpends: List<CategorySpend>,
    forecast: com.example.ml.ExpenseForecast?,
    aiInsight: String?,
    isLoading: Boolean,
    onRunAnalysis: () -> Unit
) {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp)
            .verticalScroll(rememberScrollState()),
        verticalArrangement = Arrangement.spacedBy(14.dp)
    ) {

        Card(
            shape = RoundedCornerShape(18.dp),
            colors = CardDefaults.cardColors(containerColor = Indigo900),
            modifier = Modifier.fillMaxWidth()
        ) {
            Column(modifier = Modifier.padding(18.dp)) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Icon(imageVector = Icons.Default.AutoAwesome, contentDescription = null, tint = Emerald400)
                    Spacer(modifier = Modifier.width(8.dp))
                    Text(
                        text = "Splitzy AI Spending Intelligence",
                        style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold, color = Color.White)
                    )
                }
                Spacer(modifier = Modifier.height(4.dp))
                Text(
                    text = "Analyze your spending habits, highest expense drivers, ML end-of-month budget forecast, and practical student savings tips.",
                    style = MaterialTheme.typography.bodySmall.copy(color = Indigo100)
                )
                Spacer(modifier = Modifier.height(12.dp))
                Button(
                    onClick = onRunAnalysis,
                    colors = ButtonDefaults.buttonColors(containerColor = Emerald500),
                    shape = RoundedCornerShape(12.dp),
                    enabled = !isLoading,
                    modifier = Modifier.testTag("run_ai_expense_analysis_button")
                ) {
                    if (isLoading) {
                        CircularProgressIndicator(color = Color.White, modifier = Modifier.size(18.dp), strokeWidth = 2.dp)
                        Spacer(modifier = Modifier.width(8.dp))
                        Text("Analyzing Transactions...")
                    } else {
                        Icon(imageVector = Icons.Default.Analytics, contentDescription = null, tint = Slate950, modifier = Modifier.size(18.dp))
                        Spacer(modifier = Modifier.width(8.dp))
                        Text("Generate AI Spending Report", color = Slate950, fontWeight = FontWeight.Bold)
                    }
                }
            }
        }


        if (forecast != null) {
            Card(
                shape = RoundedCornerShape(16.dp),
                colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
                elevation = CardDefaults.cardElevation(defaultElevation = 2.dp),
                modifier = Modifier.fillMaxWidth()
            ) {
                Column(modifier = Modifier.padding(16.dp)) {
                    Text(
                        text = "📊 ML Projection & Burn Rate",
                        style = MaterialTheme.typography.titleSmall.copy(fontWeight = FontWeight.Bold),
                        color = MaterialTheme.colorScheme.primary
                    )
                    Spacer(modifier = Modifier.height(8.dp))
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween
                    ) {
                        Column {
                            Text("Estimated Month-End Total", style = MaterialTheme.typography.labelSmall, color = MaterialTheme.colorScheme.onSurfaceVariant)
                            Text("₹${forecast.predictedMonthTotal.toInt()}", style = MaterialTheme.typography.titleLarge.copy(fontWeight = FontWeight.Bold))
                        }
                        Column {
                            Text("Daily Burn Rate", style = MaterialTheme.typography.labelSmall, color = MaterialTheme.colorScheme.onSurfaceVariant)
                            Text("~₹${forecast.dailyBurnRate.toInt()} / day", style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold, color = Indigo600))
                        }
                    }
                }
            }
        }


        Card(
            shape = RoundedCornerShape(16.dp),
            colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
            elevation = CardDefaults.cardElevation(defaultElevation = 2.dp),
            modifier = Modifier.fillMaxWidth()
        ) {
            Column(modifier = Modifier.padding(16.dp)) {
                Text(
                    text = "Category Breakdown",
                    style = MaterialTheme.typography.titleSmall.copy(fontWeight = FontWeight.Bold)
                )
                Spacer(modifier = Modifier.height(10.dp))
                categorySpends.forEach { cs ->
                    Column(modifier = Modifier.padding(vertical = 4.dp)) {
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween
                        ) {
                            Text(cs.category, style = MaterialTheme.typography.bodyMedium.copy(fontWeight = FontWeight.Medium))
                            Text("₹${cs.amount.toInt()} (${(cs.percentage * 100).toInt()}%)", style = MaterialTheme.typography.bodySmall.copy(fontWeight = FontWeight.Bold))
                        }
                        Spacer(modifier = Modifier.height(4.dp))
                        LinearProgressIndicator(
                            progress = { cs.percentage },
                            modifier = Modifier
                                .fillMaxWidth()
                                .height(8.dp)
                                .clip(RoundedCornerShape(4.dp)),
                            color = when (cs.category) {
                                "Food" -> Emerald500
                                "Bills" -> Indigo600
                                "Travel" -> Cyan500
                                "Education" -> Amber500
                                else -> Rose500
                            },
                            trackColor = MaterialTheme.colorScheme.surfaceVariant
                        )
                    }
                }
            }
        }


        if (!aiInsight.isNullOrBlank()) {
            Card(
                shape = RoundedCornerShape(16.dp),
                colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
                elevation = CardDefaults.cardElevation(defaultElevation = 2.dp),
                modifier = Modifier.fillMaxWidth()
            ) {
                Column(modifier = Modifier.padding(16.dp)) {
                    Text(
                        text = "Campus AI Financial Advisory Report",
                        style = MaterialTheme.typography.titleSmall.copy(fontWeight = FontWeight.Bold, color = Emerald600)
                    )
                    Spacer(modifier = Modifier.height(8.dp))
                    Text(
                        text = aiInsight,
                        style = MaterialTheme.typography.bodySmall.copy(lineHeight = 20.sp, color = MaterialTheme.colorScheme.onSurface)
                    )
                }
            }
        }
    }
}



@Composable
fun AddExpenseModal(
    groups: List<ExpenseGroupEntity>,
    onDismiss: () -> Unit,
    onAdd: (String, Double, String, String, String, String, String?, String?, String, String) -> Unit
) {
    var title by remember { mutableStateOf("") }
    var amount by remember { mutableStateOf("") }
    var category by remember { mutableStateOf("Food") }
    var paidBy by remember { mutableStateOf("You") }
    var selectedGroup by remember { mutableStateOf<ExpenseGroupEntity?>(null) }
    var description by remember { mutableStateOf("") }

    val categories = listOf("Food", "Bills", "Travel", "Education", "Entertainment", "Shopping")

    AlertDialog(
        onDismissRequest = onDismiss,
        title = { Text("Add Expense", fontWeight = FontWeight.Bold) },
        text = {
            Column(
                modifier = Modifier.verticalScroll(rememberScrollState()),
                verticalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                OutlinedTextField(value = title, onValueChange = { title = it }, label = { Text("Expense Title (e.g. Pizza treat)") }, modifier = Modifier.fillMaxWidth())
                OutlinedTextField(value = amount, onValueChange = { amount = it }, label = { Text("Total Amount (₹)") }, modifier = Modifier.fillMaxWidth())

                Text("Category:", style = MaterialTheme.typography.labelSmall)
                LazyRow(horizontalArrangement = Arrangement.spacedBy(6.dp)) {
                    items(categories) { cat ->
                        FilterChip(
                            selected = category == cat,
                            onClick = { category = cat },
                            label = { Text(cat, fontSize = 11.sp) }
                        )
                    }
                }

                OutlinedTextField(value = paidBy, onValueChange = { paidBy = it }, label = { Text("Paid By") }, modifier = Modifier.fillMaxWidth())

                Text("Group:", style = MaterialTheme.typography.labelSmall)
                LazyRow(horizontalArrangement = Arrangement.spacedBy(6.dp)) {
                    item {
                        FilterChip(
                            selected = selectedGroup == null,
                            onClick = { selectedGroup = null },
                            label = { Text("Personal / No Group", fontSize = 11.sp) }
                        )
                    }
                    items(groups) { g ->
                        FilterChip(
                            selected = selectedGroup?.id == g.id,
                            onClick = { selectedGroup = g },
                            label = { Text(g.name, fontSize = 11.sp) }
                        )
                    }
                }

                OutlinedTextField(value = description, onValueChange = { description = it }, label = { Text("Notes / Description") }, modifier = Modifier.fillMaxWidth())
            }
        },
        confirmButton = {
            Button(
                onClick = {
                    val amtVal = amount.toDoubleOrNull() ?: 0.0
                    if (title.isNotBlank() && amtVal > 0) {
                        val members = selectedGroup?.membersJson?.split(",")?.map { it.trim() } ?: listOf("You")
                        val parts = SplitzyCalculator.computeEqualSplit(amtVal, members)
                        onAdd(
                            title, amtVal, category, "2026-08-26", description, paidBy,
                            selectedGroup?.id, selectedGroup?.name ?: "Personal", "EQUAL", parts
                        )
                    }
                },
                enabled = title.isNotBlank() && (amount.toDoubleOrNull() ?: 0.0) > 0
            ) { Text("Save Expense") }
        },
        dismissButton = { TextButton(onClick = onDismiss) { Text("Cancel") } }
    )
}

@Composable
fun SplitBillCalculatorModal(
    groups: List<ExpenseGroupEntity>,
    onDismiss: () -> Unit,
    onCalculateAndSave: (String, Double, String, String, String, String, String?, String?, String, String) -> Unit
) {
    var title by remember { mutableStateOf("") }
    var amountText by remember { mutableStateOf("") }
    var category by remember { mutableStateOf("Food") }
    var splitMode by remember { mutableStateOf("EQUAL") }
    var membersInput by remember { mutableStateOf("You, Rahul, Priya, Amit") }
    var customValues by remember { mutableStateOf("") }

    val amount = amountText.toDoubleOrNull() ?: 0.0
    val members = membersInput.split(",").map { it.trim() }.filter { it.isNotBlank() }

    AlertDialog(
        onDismissRequest = onDismiss,
        title = { Text("Split Bill Calculator", fontWeight = FontWeight.Bold) },
        text = {
            Column(
                modifier = Modifier.verticalScroll(rememberScrollState()),
                verticalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                OutlinedTextField(value = title, onValueChange = { title = it }, label = { Text("Bill Title (e.g. Dinner & Drinks)") }, modifier = Modifier.fillMaxWidth())
                OutlinedTextField(value = amountText, onValueChange = { amountText = it }, label = { Text("Total Bill Amount (₹)") }, modifier = Modifier.fillMaxWidth())
                OutlinedTextField(value = membersInput, onValueChange = { membersInput = it }, label = { Text("Participants (comma-separated)") }, modifier = Modifier.fillMaxWidth())

                Text("Split Mode:", style = MaterialTheme.typography.labelSmall)
                Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                    listOf("EQUAL", "CUSTOM", "PERCENTAGE").forEach { m ->
                        FilterChip(
                            selected = splitMode == m,
                            onClick = { splitMode = m },
                            label = { Text(m, fontSize = 11.sp) }
                        )
                    }
                }

                if (splitMode == "EQUAL" && amount > 0 && members.isNotEmpty()) {
                    Surface(
                        shape = RoundedCornerShape(10.dp),
                        color = Indigo50,
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        Text(
                            text = "Each person pays: ₹${String.format("%.2f", amount / members.size)}",
                            style = MaterialTheme.typography.labelMedium.copy(fontWeight = FontWeight.Bold, color = Indigo800),
                            modifier = Modifier.padding(10.dp)
                        )
                    }
                } else if (splitMode != "EQUAL") {
                    OutlinedTextField(
                        value = customValues,
                        onValueChange = { customValues = it },
                        label = { Text(if (splitMode == "CUSTOM") "Custom amounts e.g. 300, 400, 200, 300" else "Percentages e.g. 25, 35, 20, 20") },
                        modifier = Modifier.fillMaxWidth()
                    )
                }
            }
        },
        confirmButton = {
            Button(
                onClick = {
                    if (title.isNotBlank() && amount > 0 && members.isNotEmpty()) {
                        val parts = if (splitMode == "EQUAL") {
                            SplitzyCalculator.computeEqualSplit(amount, members)
                        } else {
                            val vals = customValues.split(",").map { it.trim().toDoubleOrNull() ?: 0.0 }
                            members.mapIndexed { idx, name ->
                                val share = if (splitMode == "PERCENTAGE") {
                                    val pct = vals.getOrElse(idx) { 100.0 / members.size }
                                    (pct / 100.0) * amount
                                } else {
                                    vals.getOrElse(idx) { amount / members.size }
                                }
                                "$name:${String.format("%.2f", share)}"
                            }.joinToString(",")
                        }

                        onCalculateAndSave(
                            title, amount, category, "2026-08-26", "Splitzy Smart Split",
                            "You", null, "Shared Bill", splitMode, parts
                        )
                    }
                },
                enabled = title.isNotBlank() && amount > 0
            ) { Text("Save & Apply Split") }
        },
        dismissButton = { TextButton(onClick = onDismiss) { Text("Cancel") } }
    )
}

@Composable
fun CreateGroupModal(
    onDismiss: () -> Unit,
    onAdd: (String, String, String) -> Unit
) {
    var name by remember { mutableStateOf("") }
    var category by remember { mutableStateOf("Flat & Utilities") }
    var members by remember { mutableStateOf("You, Rahul, Priya, Amit") }

    AlertDialog(
        onDismissRequest = onDismiss,
        title = { Text("Create Expense Group", fontWeight = FontWeight.Bold) },
        text = {
            Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                OutlinedTextField(value = name, onValueChange = { name = it }, label = { Text("Group Name (e.g. Room 304)") }, modifier = Modifier.fillMaxWidth())
                OutlinedTextField(value = category, onValueChange = { category = it }, label = { Text("Category (e.g. Travel, Flat, Project)") }, modifier = Modifier.fillMaxWidth())
                OutlinedTextField(value = members, onValueChange = { members = it }, label = { Text("Members (comma-separated)") }, modifier = Modifier.fillMaxWidth())
            }
        },
        confirmButton = {
            Button(
                onClick = {
                    if (name.isNotBlank()) onAdd(name, category, members)
                },
                enabled = name.isNotBlank()
            ) { Text("Create Group") }
        },
        dismissButton = { TextButton(onClick = onDismiss) { Text("Cancel") } }
    )
}

@Composable
fun RecordSettlementModal(
    groups: List<ExpenseGroupEntity>,
    onDismiss: () -> Unit,
    onSettle: (String, String, Double, String?, String) -> Unit
) {
    var payer by remember { mutableStateOf("Rahul") }
    var receiver by remember { mutableStateOf("You") }
    var amount by remember { mutableStateOf("300") }
    var note by remember { mutableStateOf("Settled via UPI") }

    AlertDialog(
        onDismissRequest = onDismiss,
        title = { Text("Record Settlement Payment", fontWeight = FontWeight.Bold) },
        text = {
            Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                OutlinedTextField(value = payer, onValueChange = { payer = it }, label = { Text("Who Paid?") }, modifier = Modifier.fillMaxWidth())
                OutlinedTextField(value = receiver, onValueChange = { receiver = it }, label = { Text("Who was Paid?") }, modifier = Modifier.fillMaxWidth())
                OutlinedTextField(value = amount, onValueChange = { amount = it }, label = { Text("Amount Paid (₹)") }, modifier = Modifier.fillMaxWidth())
                OutlinedTextField(value = note, onValueChange = { note = it }, label = { Text("Note / Transaction Reference") }, modifier = Modifier.fillMaxWidth())
            }
        },
        confirmButton = {
            Button(
                onClick = {
                    val amt = amount.toDoubleOrNull() ?: 0.0
                    if (payer.isNotBlank() && receiver.isNotBlank() && amt > 0) {
                        onSettle(payer, receiver, amt, "Room 304 Flatmates", note)
                    }
                },
                enabled = (amount.toDoubleOrNull() ?: 0.0) > 0
            ) { Text("Record Payment") }
        },
        dismissButton = { TextButton(onClick = onDismiss) { Text("Cancel") } }
    )
}

@Composable
fun StudentInvestmentGuideTab() {
    var monthlyInvestment by remember { mutableFloatStateOf(500f) }
    var expectedReturnRate by remember { mutableFloatStateOf(13f) }
    var investmentYears by remember { mutableFloatStateOf(5f) }



    val monthlyRate = (expectedReturnRate / 100f) / 12f
    val totalMonths = (investmentYears * 12).toInt()
    val totalInvested = monthlyInvestment * totalMonths
    val futureValue = if (monthlyRate > 0) {
        val compoundFactor = Math.pow((1.0 + monthlyRate), totalMonths.toDouble())
        (monthlyInvestment * ((compoundFactor - 1.0) / monthlyRate) * (1.0 + monthlyRate)).toFloat()
    } else {
        totalInvested
    }
    val wealthGained = (futureValue - totalInvested).coerceAtLeast(0f)

    LazyColumn(
        modifier = Modifier.fillMaxSize(),
        contentPadding = PaddingValues(16.dp),
        verticalArrangement = Arrangement.spacedBy(14.dp)
    ) {

        item {
            Card(
                shape = RoundedCornerShape(20.dp),
                colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
                elevation = CardDefaults.cardElevation(defaultElevation = 2.dp),
                modifier = Modifier.fillMaxWidth()
            ) {
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .background(
                            Brush.linearGradient(
                                listOf(Indigo900, Slate900)
                            )
                        )
                        .padding(18.dp)
                ) {
                    Column {
                        Row(
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.SpaceBetween,
                            modifier = Modifier.fillMaxWidth()
                        ) {
                            Surface(
                                shape = RoundedCornerShape(8.dp),
                                color = Amber400.copy(alpha = 0.25f)
                            ) {
                                Text(
                                    text = "💡 Financial Literacy Hub",
                                    style = MaterialTheme.typography.labelSmall.copy(
                                        color = Amber300,
                                        fontWeight = FontWeight.Bold
                                    ),
                                    modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp)
                                )
                            }
                            Text(
                                text = "Age 18-24 Special",
                                style = MaterialTheme.typography.labelSmall.copy(color = Indigo200)
                            )
                        }
                        Spacer(modifier = Modifier.height(10.dp))
                        Text(
                            text = "Smart Student Wealth & Investment Guide",
                            style = MaterialTheme.typography.titleLarge.copy(
                                fontWeight = FontWeight.ExtraBold,
                                color = Color.White
                            )
                        )
                        Spacer(modifier = Modifier.height(4.dp))
                        Text(
                            text = "Learn where to invest your pocket money, build wealth before graduation, and avoid student financial traps.",
                            style = MaterialTheme.typography.bodySmall.copy(color = Indigo100, fontSize = 12.sp)
                        )
                    }
                }
            }
        }


        item {
            Card(
                shape = RoundedCornerShape(18.dp),
                colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
                elevation = CardDefaults.cardElevation(defaultElevation = 2.dp),
                modifier = Modifier.fillMaxWidth()
            ) {
                Column(modifier = Modifier.padding(16.dp)) {
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.SpaceBetween,
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Icon(
                                imageVector = Icons.Default.TrendingUp,
                                contentDescription = null,
                                tint = Emerald600,
                                modifier = Modifier.size(20.dp)
                            )
                            Spacer(modifier = Modifier.width(8.dp))
                            Text(
                                text = "Student SIP & Wealth Simulator",
                                style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold)
                            )
                        }
                        Surface(
                            shape = RoundedCornerShape(6.dp),
                            color = Emerald500.copy(alpha = 0.15f)
                        ) {
                            Text(
                                text = "Compound Power ⚡",
                                style = MaterialTheme.typography.labelSmall.copy(color = Emerald600, fontWeight = FontWeight.Bold),
                                modifier = Modifier.padding(horizontal = 6.dp, vertical = 2.dp)
                            )
                        }
                    }

                    Spacer(modifier = Modifier.height(12.dp))


                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween
                    ) {
                        Text(
                            text = "Monthly Pocket Investment:",
                            style = MaterialTheme.typography.bodySmall.copy(color = MaterialTheme.colorScheme.onSurfaceVariant)
                        )
                        Text(
                            text = "₹${monthlyInvestment.toInt()}/month",
                            style = MaterialTheme.typography.bodySmall.copy(fontWeight = FontWeight.Bold, color = Indigo600)
                        )
                    }
                    Slider(
                        value = monthlyInvestment,
                        onValueChange = { monthlyInvestment = it },
                        valueRange = 100f..5000f,
                        steps = 48,
                        colors = SliderDefaults.colors(thumbColor = Indigo600, activeTrackColor = Indigo600)
                    )


                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween
                    ) {
                        Text(
                            text = "Expected Annual Return (%):",
                            style = MaterialTheme.typography.bodySmall.copy(color = MaterialTheme.colorScheme.onSurfaceVariant)
                        )
                        Text(
                            text = "${expectedReturnRate.toInt()}% p.a. (Nifty 50 avg)",
                            style = MaterialTheme.typography.bodySmall.copy(fontWeight = FontWeight.Bold, color = Emerald600)
                        )
                    }
                    Slider(
                        value = expectedReturnRate,
                        onValueChange = { expectedReturnRate = it },
                        valueRange = 6f..20f,
                        steps = 13,
                        colors = SliderDefaults.colors(thumbColor = Emerald600, activeTrackColor = Emerald600)
                    )


                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween
                    ) {
                        Text(
                            text = "Investment Time Horizon:",
                            style = MaterialTheme.typography.bodySmall.copy(color = MaterialTheme.colorScheme.onSurfaceVariant)
                        )
                        Text(
                            text = "${investmentYears.toInt()} Years",
                            style = MaterialTheme.typography.bodySmall.copy(fontWeight = FontWeight.Bold, color = Indigo600)
                        )
                    }
                    Slider(
                        value = investmentYears,
                        onValueChange = { investmentYears = it },
                        valueRange = 1f..15f,
                        steps = 13,
                        colors = SliderDefaults.colors(thumbColor = Indigo600, activeTrackColor = Indigo600)
                    )

                    Spacer(modifier = Modifier.height(10.dp))


                    Surface(
                        shape = RoundedCornerShape(14.dp),
                        color = Slate100,
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        Column(modifier = Modifier.padding(14.dp)) {
                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.SpaceBetween
                            ) {
                                Column {
                                    Text(
                                        text = "Total Invested",
                                        style = MaterialTheme.typography.labelSmall.copy(color = Slate600)
                                    )
                                    Text(
                                        text = "₹${totalInvested.toInt()}",
                                        style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold, color = Slate900)
                                    )
                                }
                                Column(horizontalAlignment = Alignment.End) {
                                    Text(
                                        text = "Est. Wealth Gain",
                                        style = MaterialTheme.typography.labelSmall.copy(color = Emerald700)
                                    )
                                    Text(
                                        text = "+₹${wealthGained.toInt()}",
                                        style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold, color = Emerald600)
                                    )
                                }
                            }

                            Spacer(modifier = Modifier.height(8.dp))
                            HorizontalDivider(color = Slate200)
                            Spacer(modifier = Modifier.height(8.dp))

                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.SpaceBetween,
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Text(
                                    text = "Total Maturity Value:",
                                    style = MaterialTheme.typography.bodyMedium.copy(fontWeight = FontWeight.Bold)
                                )
                                Text(
                                    text = "₹${futureValue.toInt()}",
                                    style = MaterialTheme.typography.headlineSmall.copy(fontWeight = FontWeight.ExtraBold, color = Indigo700)
                                )
                            }
                        }
                    }
                }
            }
        }


        item {
            Text(
                text = "📊 Where Should Students Invest?",
                style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold)
            )
        }


        item {
            InvestmentAssetCard(
                title = "1. Nifty 50 Index Mutual Funds (SIP)",
                category = "Most Recommended for Students",
                categoryColor = Emerald600,
                categoryBg = Emerald50,
                startingFrom = "₹100 / month",
                expectedReturns = "12% - 14% p.a. (Historical)",
                riskLevel = "Moderate • High Long-term Reward",
                description = "Invests automatically across India's top 50 blue-chip companies (Reliance, TCS, HDFC, Infosys). You own the entire Indian economy with zero stock-picking stress.",
                bestApps = "Groww, Zerodha Coin, INDmoney, Kuvera (Choose Direct-Growth Plans)"
            )
        }


        item {
            InvestmentAssetCard(
                title = "2. 24K Digital Gold & SGBs",
                category = "Inflation Hedge",
                categoryColor = Amber700,
                categoryBg = Amber50,
                startingFrom = "₹10 micro-buys",
                expectedReturns = "10% - 12% p.a.",
                riskLevel = "Low to Medium",
                description = "Buy 99.9% 24 Karat pure digital gold without paying 15-20% making charges or locker fees. Safely hedges against inflation and currency depreciation.",
                bestApps = "Jar App, Paytm Gold, PhonePe Gold, Zerodha (for SGBs)"
            )
        }


        item {
            InvestmentAssetCard(
                title = "3. Student Recurring Deposit (RD / Sweep-In)",
                category = "100% Guaranteed Safe",
                categoryColor = Cyan700,
                categoryBg = Cyan50,
                startingFrom = "₹500 / month",
                expectedReturns = "7.0% - 7.5% p.a. Guaranteed",
                riskLevel = "Zero Risk • Fixed Returns",
                description = "Auto-debits a fixed amount every month from your student bank account. Ideal for building your Semester Exam Fee fund or Laptop upgrade fund.",
                bestApps = "SBI YONO, HDFC Mobile, Kotak 811, Fi Money"
            )
        }


        item {
            InvestmentAssetCard(
                title = "4. Micro-Stocks in Monopoly Companies",
                category = "High Growth (Requires Research)",
                categoryColor = Indigo700,
                categoryBg = Indigo50,
                startingFrom = "₹500+ per share",
                expectedReturns = "15%+ p.a.",
                riskLevel = "High • Avoid Intraday & F&O",
                description = "Buy fractional or single shares of companies you personally use every day (Tata Motors, ITC, Infosys, CDSL). Never trade speculative intraday options.",
                bestApps = "Groww, Zerodha Kite, Angel One"
            )
        }


        item {
            Card(
                shape = RoundedCornerShape(18.dp),
                colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
                elevation = CardDefaults.cardElevation(defaultElevation = 2.dp),
                modifier = Modifier.fillMaxWidth()
            ) {
                Column(modifier = Modifier.padding(16.dp)) {
                    Text(
                        text = "💰 The 50-30-20 Student Budget Rule",
                        style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold)
                    )
                    Spacer(modifier = Modifier.height(6.dp))
                    Text(
                        text = "Divide whatever pocket money or stipend you receive into 3 simple buckets:",
                        style = MaterialTheme.typography.bodySmall.copy(color = MaterialTheme.colorScheme.onSurfaceVariant)
                    )
                    Spacer(modifier = Modifier.height(12.dp))

                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        BudgetBucketItem(
                            percent = "50%",
                            title = "Needs",
                            desc = "Hostel Mess, Books, Bus pass, Recharge",
                            color = Indigo600,
                            modifier = Modifier.weight(1f)
                        )
                        BudgetBucketItem(
                            percent = "30%",
                            title = "Wants",
                            desc = "Canteen food, Movies, Outings, Games",
                            color = Amber600,
                            modifier = Modifier.weight(1f)
                        )
                        BudgetBucketItem(
                            percent = "20%",
                            title = "Invest",
                            desc = "Index Fund SIP, Digital Gold, Emergency",
                            color = Emerald600,
                            modifier = Modifier.weight(1f)
                        )
                    }
                }
            }
        }


        item {
            Card(
                shape = RoundedCornerShape(18.dp),
                colors = CardDefaults.cardColors(containerColor = Rose50.copy(alpha = 0.7f)),
                border = androidx.compose.foundation.BorderStroke(1.dp, Rose200),
                modifier = Modifier.fillMaxWidth()
            ) {
                Column(modifier = Modifier.padding(16.dp)) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Icon(
                            imageVector = Icons.Default.Warning,
                            contentDescription = null,
                            tint = Rose600,
                            modifier = Modifier.size(20.dp)
                        )
                        Spacer(modifier = Modifier.width(8.dp))
                        Text(
                            text = "🚫 Top 5 Financial Traps to Avoid in College",
                            style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold, color = Rose800)
                        )
                    }
                    Spacer(modifier = Modifier.height(10.dp))
                    val traps = listOf(
                        "1. Instant Loan / BNPL Apps" to "Charging 36% to 48% hidden penalties that destroy your CIBIL score before graduation.",
                        "2. Telegram 'Stock & Crypto VIP' Groups" to "99% are scam pump-and-dump channels targeting innocent college students.",
                        "3. Options (F&O) & Intraday Trading" to "SEBI reports 93% of retail traders lose money in F&O. Focus on long-term SIPs instead.",
                        "4. Peer Pressure Spending" to "Spending money you don't have to impress people you don't even like.",
                        "5. Keeping 100% Cash Idle in Bank" to "Normal savings accounts give 2.5% while inflation is 6%. Your money is silently losing purchasing power."
                    )
                    traps.forEach { (trapTitle, trapDesc) ->
                        Column(modifier = Modifier.padding(vertical = 4.dp)) {
                            Text(
                                text = trapTitle,
                                style = MaterialTheme.typography.bodySmall.copy(fontWeight = FontWeight.Bold, color = Rose900)
                            )
                            Text(
                                text = trapDesc,
                                style = MaterialTheme.typography.bodySmall.copy(fontSize = 11.sp, color = Slate700)
                            )
                        }
                    }
                }
            }
        }


        item {
            Card(
                shape = RoundedCornerShape(18.dp),
                colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
                elevation = CardDefaults.cardElevation(defaultElevation = 2.dp),
                modifier = Modifier.fillMaxWidth()
            ) {
                Column(modifier = Modifier.padding(16.dp)) {
                    Text(
                        text = "📋 4-Step Checklist to Start at 18+",
                        style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold)
                    )
                    Spacer(modifier = Modifier.height(10.dp))
                    val steps = listOf(
                        "Step 1: Get a PAN Card" to "Apply online on NSDL/UTI portal for ₹107. Takes 7-10 days to arrive.",
                        "Step 2: Zero-Balance Student Bank Account" to "Open a free digital account in SBI, Kotak 811, Fi, or Jupiter.",
                        "Step 3: Free e-KYC on a Direct MF Platform" to "Upload PAN + Aadhaar on Groww or Zerodha Coin in 5 minutes.",
                        "Step 4: Start a ₹500 Auto-SIP" to "Pick a Nifty 50 Direct Index Fund & let compound interest work silently!"
                    )
                    steps.forEachIndexed { idx, (sTitle, sDesc) ->
                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(vertical = 6.dp),
                            verticalAlignment = Alignment.Top
                        ) {
                            Box(
                                modifier = Modifier
                                    .size(24.dp)
                                    .clip(CircleShape)
                                    .background(Indigo600),
                                contentAlignment = Alignment.Center
                            ) {
                                Text(
                                    text = "${idx + 1}",
                                    color = Color.White,
                                    fontSize = 12.sp,
                                    fontWeight = FontWeight.Bold
                                )
                            }
                            Spacer(modifier = Modifier.width(10.dp))
                            Column(modifier = Modifier.weight(1f)) {
                                Text(
                                    text = sTitle,
                                    style = MaterialTheme.typography.bodySmall.copy(fontWeight = FontWeight.Bold)
                                )
                                Text(
                                    text = sDesc,
                                    style = MaterialTheme.typography.bodySmall.copy(fontSize = 11.sp, color = MaterialTheme.colorScheme.onSurfaceVariant)
                                )
                            }
                        }
                    }
                }
            }
        }
    }
}

@Composable
fun InvestmentAssetCard(
    title: String,
    category: String,
    categoryColor: Color,
    categoryBg: Color,
    startingFrom: String,
    expectedReturns: String,
    riskLevel: String,
    description: String,
    bestApps: String
) {
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
                StatusBadge(
                    text = category,
                    color = categoryColor,
                    bgColor = categoryBg
                )
                Text(
                    text = "Min: $startingFrom",
                    style = MaterialTheme.typography.labelSmall.copy(fontWeight = FontWeight.Bold, color = Indigo700)
                )
            }
            Spacer(modifier = Modifier.height(8.dp))
            Text(
                text = title,
                style = MaterialTheme.typography.titleSmall.copy(fontWeight = FontWeight.Bold)
            )
            Spacer(modifier = Modifier.height(4.dp))
            Text(
                text = description,
                style = MaterialTheme.typography.bodySmall.copy(fontSize = 12.sp, color = MaterialTheme.colorScheme.onSurfaceVariant)
            )

            Spacer(modifier = Modifier.height(8.dp))
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Column {
                    Text(
                        text = "Expected Returns:",
                        style = MaterialTheme.typography.labelSmall.copy(color = MaterialTheme.colorScheme.onSurfaceVariant, fontSize = 10.sp)
                    )
                    Text(
                        text = expectedReturns,
                        style = MaterialTheme.typography.labelSmall.copy(fontWeight = FontWeight.Bold, color = Emerald600)
                    )
                }
                Column(horizontalAlignment = Alignment.End) {
                    Text(
                        text = "Risk Level:",
                        style = MaterialTheme.typography.labelSmall.copy(color = MaterialTheme.colorScheme.onSurfaceVariant, fontSize = 10.sp)
                    )
                    Text(
                        text = riskLevel,
                        style = MaterialTheme.typography.labelSmall.copy(fontWeight = FontWeight.SemiBold, color = Slate700)
                    )
                }
            }

            Spacer(modifier = Modifier.height(8.dp))
            Surface(
                shape = RoundedCornerShape(8.dp),
                color = Slate100,
                modifier = Modifier.fillMaxWidth()
            ) {
                Text(
                    text = "📲 Recommended: $bestApps",
                    style = MaterialTheme.typography.labelSmall.copy(fontSize = 10.sp, color = Slate700),
                    modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp)
                )
            }
        }
    }
}

@Composable
fun BudgetBucketItem(
    percent: String,
    title: String,
    desc: String,
    color: Color,
    modifier: Modifier = Modifier
) {
    Surface(
        shape = RoundedCornerShape(12.dp),
        color = color.copy(alpha = 0.1f),
        border = androidx.compose.foundation.BorderStroke(1.dp, color.copy(alpha = 0.2f)),
        modifier = modifier
    ) {
        Column(
            modifier = Modifier.padding(10.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Text(
                text = percent,
                style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.ExtraBold, color = color)
            )
            Text(
                text = title,
                style = MaterialTheme.typography.labelSmall.copy(fontWeight = FontWeight.Bold, color = color)
            )
            Spacer(modifier = Modifier.height(4.dp))
            Text(
                text = desc,
                style = MaterialTheme.typography.labelSmall.copy(fontSize = 9.sp, color = Slate600, textAlign = androidx.compose.ui.text.style.TextAlign.Center),
                maxLines = 2
            )
        }
    }
}
