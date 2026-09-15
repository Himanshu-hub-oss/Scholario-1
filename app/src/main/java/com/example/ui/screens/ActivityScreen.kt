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
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.example.data.model.ActivityEntity
import com.example.ui.components.SectionHeader
import com.example.ui.graphify.GraphifyBarChart
import com.example.ui.graphify.GraphifyBarData
import com.example.ui.graphify.GraphifyDonutChart
import com.example.ui.graphify.GraphifySliceData
import com.example.ui.theme.*
import com.example.ui.viewmodel.CampusViewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ActivityScreen(
    viewModel: CampusViewModel,
    onOpenAiChat: () -> Unit,
    modifier: Modifier = Modifier
) {
    val activities by viewModel.activities.collectAsStateWithLifecycle()
    val userXp by viewModel.userXp.collectAsStateWithLifecycle()
    val userLevel by viewModel.userLevel.collectAsStateWithLifecycle()
    val userTokens by viewModel.userTokens.collectAsStateWithLifecycle()
    val rewardsCatalog by viewModel.rewardsCatalog.collectAsStateWithLifecycle()

    var activeTab by remember { mutableStateOf(0) }
    var searchQuery by remember { mutableStateOf("") }
    var selectedCategory by remember { mutableStateOf("All") }
    var showAnalyticsSection by remember { mutableStateOf(true) }
    var redemptionDialogReward by remember { mutableStateOf<com.example.data.model.CampusRewardItem?>(null) }
    var redemptionSuccessCode by remember { mutableStateOf<String?>(null) }
    var redemptionErrorMessage by remember { mutableStateOf<String?>(null) }

    val categories = listOf("All", "Study", "Money", "Community", "Campus", "Career", "Achievements")


    val filteredActivities = remember(activities, searchQuery, selectedCategory) {
        activities.filter { act ->
            val matchesCat = if (selectedCategory == "All") true else act.category.equals(selectedCategory, ignoreCase = true)
            val matchesQuery = if (searchQuery.isBlank()) true else {
                act.title.contains(searchQuery, ignoreCase = true) || act.description.contains(searchQuery, ignoreCase = true)
            }
            matchesCat && matchesQuery
        }
    }


    val categoryCounts = remember(activities) {
        activities.groupBy { it.category }
            .mapValues { it.value.size }
    }

    val mostActiveCategory = remember(categoryCounts) {
        categoryCounts.maxByOrNull { it.value }?.key ?: "Study"
    }

    val dailyActivityData = listOf(
        GraphifyBarData("Mon", 4f, Indigo500),
        GraphifyBarData("Tue", 7f, Indigo600),
        GraphifyBarData("Wed", 5f, Emerald500),
        GraphifyBarData("Thu", 9f, Indigo700),
        GraphifyBarData("Fri", 6f, Amber500),
        GraphifyBarData("Sat", 8f, Indigo600),
        GraphifyBarData("Sun", (activities.size.toFloat().coerceAtLeast(3f)), Indigo600)
    )

    val categorySlices = remember(categoryCounts) {
        val colors = listOf(Indigo600, Emerald500, Amber500, Rose500, Sky500, Slate700)
        categoryCounts.entries.mapIndexed { idx, entry ->
            GraphifySliceData(
                label = entry.key,
                value = entry.value.toFloat(),
                color = colors[idx % colors.size]
            )
        }
    }

    LazyColumn(
        modifier = modifier
            .fillMaxSize()
            .background(MaterialTheme.colorScheme.background)
            .testTag("activity_screen_list"),
        contentPadding = PaddingValues(horizontal = 16.dp, vertical = 16.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {

        item {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Column {
                    Text(
                        text = "Activity Hub",
                        style = MaterialTheme.typography.headlineSmall.copy(fontWeight = FontWeight.Bold),
                        color = MaterialTheme.colorScheme.onBackground
                    )
                    Text(
                        text = "Track your live campus achievements & logs",
                        style = MaterialTheme.typography.bodyMedium,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                }


                Row(
                    horizontalArrangement = Arrangement.spacedBy(6.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Surface(
                        shape = RoundedCornerShape(20.dp),
                        color = Amber500.copy(alpha = 0.15f),
                        border = androidx.compose.foundation.BorderStroke(1.dp, Amber500.copy(alpha = 0.4f))
                    ) {
                        Row(
                            verticalAlignment = Alignment.CenterVertically,
                            modifier = Modifier.padding(horizontal = 10.dp, vertical = 6.dp)
                        ) {
                            Text(
                                text = "⚡ $userXp XP",
                                style = MaterialTheme.typography.labelMedium.copy(fontWeight = FontWeight.Bold),
                                color = Amber600
                            )
                        }
                    }

                    Surface(
                        shape = RoundedCornerShape(20.dp),
                        color = Emerald500.copy(alpha = 0.15f),
                        border = androidx.compose.foundation.BorderStroke(1.dp, Emerald500.copy(alpha = 0.4f)),
                        modifier = Modifier.clickable { activeTab = 1 }.testTag("token_balance_pill")
                    ) {
                        Row(
                            verticalAlignment = Alignment.CenterVertically,
                            modifier = Modifier.padding(horizontal = 10.dp, vertical = 6.dp)
                        ) {
                            Text(
                                text = "🪙 $userTokens",
                                style = MaterialTheme.typography.labelMedium.copy(fontWeight = FontWeight.Bold),
                                color = Emerald700
                            )
                        }
                    }
                }
            }
        }


        item {
            TabRow(
                selectedTabIndex = activeTab,
                containerColor = MaterialTheme.colorScheme.surface,
                contentColor = Indigo600,
                modifier = Modifier
                    .fillMaxWidth()
                    .clip(RoundedCornerShape(14.dp))
            ) {
                Tab(
                    selected = activeTab == 0,
                    onClick = { activeTab = 0 },
                    text = {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Icon(Icons.Default.History, contentDescription = null, modifier = Modifier.size(16.dp))
                            Spacer(Modifier.width(6.dp))
                            Text("Activity Feed", fontWeight = FontWeight.Bold)
                        }
                    },
                    modifier = Modifier.testTag("tab_activity_feed")
                )
                Tab(
                    selected = activeTab == 1,
                    onClick = { activeTab = 1 },
                    text = {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Icon(Icons.Default.CardGiftcard, contentDescription = null, modifier = Modifier.size(16.dp))
                            Spacer(Modifier.width(6.dp))
                            Text("🎁 Rewards Bazaar", fontWeight = FontWeight.Bold)
                        }
                    },
                    modifier = Modifier.testTag("tab_rewards_bazaar")
                )
            }
        }

        if (activeTab == 1) {

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
                            Column {
                                Text(
                                    text = "Your Token Balance",
                                    style = MaterialTheme.typography.labelMedium,
                                    color = Indigo200
                                )
                                Text(
                                    text = "🪙 $userTokens Tokens",
                                    style = MaterialTheme.typography.headlineMedium.copy(fontWeight = FontWeight.ExtraBold, color = Color.White)
                                )
                            }

                            Button(
                                onClick = {
                                    viewModel.awardActivityTokens(500, "Consistent Daily App Usage, Study Focus & Quizzes")
                                },
                                shape = RoundedCornerShape(12.dp),
                                colors = ButtonDefaults.buttonColors(containerColor = Amber400),
                                contentPadding = PaddingValues(horizontal = 12.dp, vertical = 8.dp),
                                modifier = Modifier.testTag("earn_more_tokens_button")
                            ) {
                                Text("+500 Tokens", color = Slate950, fontWeight = FontWeight.Bold, fontSize = 12.sp)
                            }
                        }

                        Spacer(Modifier.height(10.dp))
                        Text(
                            text = "💡 Earn tokens steadily: +250 for completing study goals, +500 for clearing weekly quizzes, +1,000 for maintaining 85%+ attendance streak!",
                            style = MaterialTheme.typography.bodySmall,
                            color = Indigo100
                        )
                    }
                }
            }

            item {
                Text(
                    text = "Redeem Exclusive Campus Merch & Swag",
                    style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold),
                    color = MaterialTheme.colorScheme.onBackground
                )
            }

            items(rewardsCatalog) { reward ->
                Card(
                    shape = RoundedCornerShape(16.dp),
                    colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
                    elevation = CardDefaults.cardElevation(defaultElevation = 2.dp),
                    modifier = Modifier.fillMaxWidth().testTag("reward_card_${reward.id}")
                ) {
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(16.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Box(
                            modifier = Modifier
                                .size(64.dp)
                                .clip(RoundedCornerShape(14.dp))
                                .background(Indigo50),
                            contentAlignment = Alignment.Center
                        ) {
                            Text(text = reward.emoji, fontSize = 32.sp)
                        }

                        Spacer(Modifier.width(14.dp))

                        Column(modifier = Modifier.weight(1f)) {
                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.SpaceBetween,
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Text(
                                    text = reward.name,
                                    style = MaterialTheme.typography.titleSmall.copy(fontWeight = FontWeight.Bold),
                                    color = MaterialTheme.colorScheme.onSurface,
                                    maxLines = 1
                                )
                            }

                            Text(
                                text = reward.description,
                                style = MaterialTheme.typography.bodySmall,
                                color = MaterialTheme.colorScheme.onSurfaceVariant,
                                maxLines = 2
                            )

                            Spacer(Modifier.height(8.dp))

                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.SpaceBetween,
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Row(verticalAlignment = Alignment.CenterVertically) {
                                    Surface(
                                        shape = RoundedCornerShape(6.dp),
                                        color = Emerald50
                                    ) {
                                        Text(
                                            text = "🪙 ${reward.tokenCost} Tokens",
                                            style = MaterialTheme.typography.labelSmall.copy(fontWeight = FontWeight.Bold),
                                            color = Emerald700,
                                            modifier = Modifier.padding(horizontal = 6.dp, vertical = 3.dp)
                                        )
                                    }
                                    Spacer(Modifier.width(6.dp))
                                    Text(
                                        text = reward.originalPriceTag,
                                        style = MaterialTheme.typography.labelSmall.copy(
                                            color = Slate400,
                                            textDecoration = androidx.compose.ui.text.style.TextDecoration.LineThrough
                                        )
                                    )
                                }

                                if (reward.isRedeemed) {
                                    Surface(
                                        shape = RoundedCornerShape(8.dp),
                                        color = Indigo50
                                    ) {
                                        Text(
                                            text = "Code: ${reward.couponCode}",
                                            style = MaterialTheme.typography.labelSmall.copy(fontWeight = FontWeight.Bold, color = Indigo700),
                                            modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp)
                                        )
                                    }
                                } else {
                                    Button(
                                        onClick = {
                                            viewModel.redeemReward(
                                                reward = reward,
                                                onSuccess = { code ->
                                                    redemptionSuccessCode = code
                                                    redemptionDialogReward = reward
                                                },
                                                onError = { err ->
                                                    redemptionErrorMessage = err
                                                }
                                            )
                                        },
                                        shape = RoundedCornerShape(10.dp),
                                        colors = ButtonDefaults.buttonColors(
                                            containerColor = if (userTokens >= reward.tokenCost) Indigo600 else Slate400
                                        ),
                                        contentPadding = PaddingValues(horizontal = 14.dp, vertical = 6.dp),
                                        modifier = Modifier.testTag("redeem_button_${reward.id}")
                                    ) {
                                        Text(
                                            text = if (userTokens >= reward.tokenCost) "Redeem" else "Locked",
                                            fontWeight = FontWeight.Bold,
                                            fontSize = 12.sp
                                        )
                                    }
                                }
                            }
                        }
                    }
                }
            }
        } else {


            item {
                OutlinedTextField(
                    value = searchQuery,
                    onValueChange = { searchQuery = it },
                    modifier = Modifier
                        .fillMaxWidth()
                        .testTag("activity_search_input"),
                    placeholder = { Text("Search logs (e.g. DBMS, Splitzy, Quiz)...") },
                    leadingIcon = {
                        Icon(Icons.Filled.Search, contentDescription = "Search", tint = Indigo600)
                    },
                    trailingIcon = {
                        if (searchQuery.isNotEmpty()) {
                            IconButton(onClick = { searchQuery = "" }) {
                                Icon(Icons.Filled.Close, contentDescription = "Clear")
                            }
                        }
                    },
                    shape = RoundedCornerShape(14.dp),
                    singleLine = true
                )
            }



        item {
            LazyRow(
                horizontalArrangement = Arrangement.spacedBy(8.dp),
                modifier = Modifier.fillMaxWidth()
            ) {
                items(categories) { cat ->
                    val isSelected = selectedCategory.equals(cat, ignoreCase = true)
                    FilterChip(
                        selected = isSelected,
                        onClick = { selectedCategory = cat },
                        label = { Text(cat) },
                        leadingIcon = {
                            if (isSelected) {
                                Icon(Icons.Filled.Check, contentDescription = null, modifier = Modifier.size(16.dp))
                            }
                        },
                        shape = RoundedCornerShape(12.dp),
                        colors = FilterChipDefaults.filterChipColors(
                            selectedContainerColor = Indigo600,
                            selectedLabelColor = Color.White,
                            selectedLeadingIconColor = Color.White
                        ),
                        modifier = Modifier.testTag("filter_chip_$cat")
                    )
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
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Icon(Icons.Filled.Analytics, contentDescription = null, tint = Indigo600)
                            Spacer(Modifier.width(8.dp))
                            Text(
                                text = "Graphify Activity Analytics",
                                style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold)
                            )
                        }

                        IconButton(onClick = { showAnalyticsSection = !showAnalyticsSection }) {
                            Icon(
                                imageVector = if (showAnalyticsSection) Icons.Filled.KeyboardArrowUp else Icons.Filled.KeyboardArrowDown,
                                contentDescription = "Toggle"
                            )
                        }
                    }

                    AnimatedVisibility(visible = showAnalyticsSection) {
                        Column {
                            Spacer(Modifier.height(12.dp))


                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.spacedBy(10.dp)
                            ) {
                                Surface(
                                    modifier = Modifier.weight(1f),
                                    shape = RoundedCornerShape(12.dp),
                                    color = Indigo50
                                ) {
                                    Row(
                                        modifier = Modifier.padding(12.dp),
                                        verticalAlignment = Alignment.CenterVertically
                                    ) {
                                        Text("🔥", fontSize = 24.sp)
                                        Spacer(Modifier.width(8.dp))
                                        Column {
                                            Text("7-Day Streak", style = MaterialTheme.typography.labelMedium.copy(fontWeight = FontWeight.Bold), color = Indigo900)
                                            Text("Keep it up!", style = MaterialTheme.typography.labelSmall, color = Indigo700)
                                        }
                                    }
                                }

                                Surface(
                                    modifier = Modifier.weight(1f),
                                    shape = RoundedCornerShape(12.dp),
                                    color = Emerald50
                                ) {
                                    Row(
                                        modifier = Modifier.padding(12.dp),
                                        verticalAlignment = Alignment.CenterVertically
                                    ) {
                                        Text("📈", fontSize = 24.sp)
                                        Spacer(Modifier.width(8.dp))
                                        Column {
                                            Text("Top: $mostActiveCategory", style = MaterialTheme.typography.labelMedium.copy(fontWeight = FontWeight.Bold), color = Emerald900)
                                            Text("Most Active", style = MaterialTheme.typography.labelSmall, color = Emerald700)
                                        }
                                    }
                                }
                            }

                            Spacer(Modifier.height(16.dp))
                            Text(
                                text = "Daily Activity Distribution",
                                style = MaterialTheme.typography.labelMedium.copy(fontWeight = FontWeight.Bold),
                                color = MaterialTheme.colorScheme.onSurfaceVariant
                            )
                            Spacer(Modifier.height(8.dp))

                            GraphifyBarChart(
                                data = dailyActivityData,
                                height = 130.dp
                            )
                        }
                    }
                }
            }
        }


        item {
            SectionHeader(
                title = "Timeline Logs (${filteredActivities.size})",
                actionText = "Log Action",
                onActionClick = {
                    viewModel.logActivity(
                        iconName = "check_circle",
                        title = "Study Session Checked In",
                        description = "Completed a 45-min Pomodoro block on Computer Networks.",
                        category = "Study",
                        xpEarned = 25
                    )
                }
            )
        }

        if (filteredActivities.isEmpty()) {
            item {
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(vertical = 40.dp),
                    contentAlignment = Alignment.Center
                ) {
                    Column(horizontalAlignment = Alignment.CenterHorizontally) {
                        Icon(
                            Icons.Outlined.History,
                            contentDescription = null,
                            tint = Slate400,
                            modifier = Modifier.size(48.dp)
                        )
                        Spacer(Modifier.height(12.dp))
                        Text(
                            text = "No activities found in this filter",
                            style = MaterialTheme.typography.bodyMedium,
                            color = Slate600
                        )
                    }
                }
            }
        } else {
            items(filteredActivities, key = { it.id }) { act ->
                ActivityItemRow(
                    activity = act,
                    onDelete = { viewModel.deleteActivity(act.id) }
                )
            }
        }
        }
    }



    if (redemptionSuccessCode != null && redemptionDialogReward != null) {
        AlertDialog(
            onDismissRequest = {
                redemptionSuccessCode = null
                redemptionDialogReward = null
            },
            icon = {
                Text(text = redemptionDialogReward?.emoji ?: "🎁", fontSize = 42.sp)
            },
            title = {
                Text(
                    text = "Claim Your ${redemptionDialogReward?.name}!",
                    style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold),
                    textAlign = androidx.compose.ui.text.style.TextAlign.Center
                )
            },
            text = {
                Column(
                    horizontalAlignment = Alignment.CenterHorizontally,
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Text(
                        text = "Your reward has been reserved! Show this digital voucher code at the Campus Merch Desk or Store:",
                        style = MaterialTheme.typography.bodyMedium,
                        textAlign = androidx.compose.ui.text.style.TextAlign.Center,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                    Spacer(Modifier.height(14.dp))
                    Surface(
                        shape = RoundedCornerShape(12.dp),
                        color = Indigo50,
                        border = androidx.compose.foundation.BorderStroke(1.dp, Indigo200)
                    ) {
                        Text(
                            text = redemptionSuccessCode ?: "",
                            style = MaterialTheme.typography.titleLarge.copy(
                                fontWeight = FontWeight.ExtraBold,
                                color = Indigo700,
                                letterSpacing = 2.sp
                            ),
                            modifier = Modifier.padding(horizontal = 16.dp, vertical = 10.dp)
                        )
                    }
                    Spacer(Modifier.height(10.dp))
                    Text(
                        text = "Tokens deducted: ${redemptionDialogReward?.tokenCost} 🪙",
                        style = MaterialTheme.typography.labelMedium.copy(color = Slate500)
                    )
                }
            },
            confirmButton = {
                Button(
                    onClick = {
                        redemptionSuccessCode = null
                        redemptionDialogReward = null
                    },
                    shape = RoundedCornerShape(10.dp),
                    colors = ButtonDefaults.buttonColors(containerColor = Indigo600)
                ) {
                    Text("Got it, Thanks!")
                }
            }
        )
    }


    if (redemptionErrorMessage != null) {
        AlertDialog(
            onDismissRequest = { redemptionErrorMessage = null },
            icon = {
                Icon(Icons.Default.ErrorOutline, contentDescription = null, tint = Rose500, modifier = Modifier.size(36.dp))
            },
            title = {
                Text("Not Enough Tokens", fontWeight = FontWeight.Bold)
            },
            text = {
                Text(
                    text = redemptionErrorMessage ?: "",
                    style = MaterialTheme.typography.bodyMedium
                )
            },
            confirmButton = {
                Button(
                    onClick = { redemptionErrorMessage = null },
                    colors = ButtonDefaults.buttonColors(containerColor = Indigo600)
                ) {
                    Text("OK")
                }
            }
        )
    }
}


@Composable
fun ActivityItemRow(
    activity: ActivityEntity,
    onDelete: () -> Unit,
    modifier: Modifier = Modifier
) {
    val (icon, tintBg, tintFg) = when (activity.category.lowercase()) {
        "study" -> Triple(Icons.Filled.AutoStories, Indigo50, Indigo600)
        "money" -> Triple(Icons.Filled.AccountBalanceWallet, Emerald50, Emerald600)
        "community" -> Triple(Icons.Filled.Forum, Sky50, Sky600)
        "campus" -> Triple(Icons.Filled.School, Amber50, Amber600)
        "career" -> Triple(Icons.Filled.Work, Rose50, Rose600)
        "achievements" -> Triple(Icons.Filled.EmojiEvents, Amber50, Amber600)
        else -> Triple(Icons.Filled.CheckCircle, Indigo50, Indigo600)
    }

    ElevatedCard(
        shape = RoundedCornerShape(14.dp),
        colors = CardDefaults.elevatedCardColors(containerColor = MaterialTheme.colorScheme.surface),
        elevation = CardDefaults.elevatedCardElevation(defaultElevation = 1.dp),
        modifier = modifier.fillMaxWidth()
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(14.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Box(
                modifier = Modifier
                    .size(44.dp)
                    .clip(CircleShape)
                    .background(tintBg),
                contentAlignment = Alignment.Center
            ) {
                Icon(
                    imageVector = icon,
                    contentDescription = null,
                    tint = tintFg,
                    modifier = Modifier.size(22.dp)
                )
            }

            Spacer(Modifier.width(14.dp))

            Column(modifier = Modifier.weight(1f)) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        text = activity.title,
                        style = MaterialTheme.typography.titleSmall.copy(fontWeight = FontWeight.Bold),
                        color = MaterialTheme.colorScheme.onSurface,
                        maxLines = 1
                    )
                    Text(
                        text = activity.timeAgo,
                        style = MaterialTheme.typography.labelSmall,
                        color = Slate500
                    )
                }

                Spacer(Modifier.height(3.dp))

                Text(
                    text = activity.description,
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                    maxLines = 2
                )

                Spacer(Modifier.height(6.dp))

                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    Surface(
                        shape = RoundedCornerShape(6.dp),
                        color = tintBg
                    ) {
                        Text(
                            text = activity.category,
                            style = MaterialTheme.typography.labelSmall.copy(fontWeight = FontWeight.SemiBold, fontSize = 10.sp),
                            color = tintFg,
                            modifier = Modifier.padding(horizontal = 6.dp, vertical = 2.dp)
                        )
                    }

                    Surface(
                        shape = RoundedCornerShape(6.dp),
                        color = Amber500.copy(alpha = 0.12f)
                    ) {
                        Text(
                            text = "+${activity.xpEarned} XP",
                            style = MaterialTheme.typography.labelSmall.copy(fontWeight = FontWeight.Bold, fontSize = 10.sp),
                            color = Amber600,
                            modifier = Modifier.padding(horizontal = 6.dp, vertical = 2.dp)
                        )
                    }
                }
            }

            IconButton(onClick = onDelete) {
                Icon(
                    Icons.Outlined.Delete,
                    contentDescription = "Delete",
                    tint = Slate400,
                    modifier = Modifier.size(18.dp)
                )
            }
        }
    }
}
