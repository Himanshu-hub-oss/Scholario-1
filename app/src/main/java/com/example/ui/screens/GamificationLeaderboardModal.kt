package com.example.ui.screens

import androidx.compose.animation.*
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
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.example.data.model.BadgeEntity
import com.example.data.model.LeaderboardUser
import com.example.ui.components.SectionHeader
import com.example.ui.graphify.GraphifyCircularGauge
import com.example.ui.theme.*
import com.example.ui.viewmodel.CampusViewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun GamificationLeaderboardModal(
    viewModel: CampusViewModel,
    onDismiss: () -> Unit,
    modifier: Modifier = Modifier
) {
    val currentUser by viewModel.currentUser.collectAsStateWithLifecycle()
    val campusScore by viewModel.campusScore.collectAsStateWithLifecycle()
    val userXp by viewModel.userXp.collectAsStateWithLifecycle()
    val userLevel by viewModel.userLevel.collectAsStateWithLifecycle()
    val badges by viewModel.badges.collectAsStateWithLifecycle()

    var selectedTab by remember { mutableIntStateOf(0) }
    var selectedLeaderboardCategory by remember { mutableStateOf("Study") }
    var hideFromLeaderboard by remember { mutableStateOf(false) }

    val leaderboardCategories = listOf("Study", "Quiz", "Savings", "Community")

    val demoLeaderboard = listOf(
        LeaderboardUser(1, "Aarav Sharma", "IIT Delhi", 96, 1420, 8, "Study", "A"),
        LeaderboardUser(2, "Priya Patel", "BITS Pilani", 94, 1310, 7, "Study", "P"),
        LeaderboardUser(3, "Alex Rivera (You)", currentUser?.college ?: "Stanford", campusScore, userXp, userLevel, "Study", "A", isCurrentUser = true),
        LeaderboardUser(4, "Rohan Verma", "NIT Surathkal", 88, 1150, 6, "Study", "R"),
        LeaderboardUser(5, "Sneha Sen", "DTU", 85, 980, 5, "Study", "S"),
        LeaderboardUser(6, "Karan Malhotra", "Manipal Institute", 82, 890, 4, "Study", "K")
    )

    ModalBottomSheet(
        onDismissRequest = onDismiss,
        containerColor = MaterialTheme.colorScheme.surface,
        modifier = modifier.testTag("gamification_leaderboard_modal")
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .fillMaxHeight(0.9f)
                .padding(horizontal = 20.dp, vertical = 8.dp)
        ) {

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = "Campus Score & Gamification",
                    style = MaterialTheme.typography.titleLarge.copy(fontWeight = FontWeight.Bold)
                )
                IconButton(onClick = onDismiss) {
                    Icon(Icons.Filled.Close, contentDescription = "Close")
                }
            }

            Spacer(Modifier.height(8.dp))


            TabRow(
                selectedTabIndex = selectedTab,
                containerColor = MaterialTheme.colorScheme.surface,
                contentColor = Indigo600
            ) {
                Tab(
                    selected = selectedTab == 0,
                    onClick = { selectedTab = 0 },
                    text = { Text("Score & Badges") },
                    icon = { Icon(Icons.Filled.Stars, contentDescription = null, modifier = Modifier.size(18.dp)) }
                )
                Tab(
                    selected = selectedTab == 1,
                    onClick = { selectedTab = 1 },
                    text = { Text("Leaderboard") },
                    icon = { Icon(Icons.Filled.Leaderboard, contentDescription = null, modifier = Modifier.size(18.dp)) }
                )
            }

            Spacer(Modifier.height(16.dp))

            if (selectedTab == 0) {

                LazyColumn(
                    modifier = Modifier.fillMaxSize(),
                    verticalArrangement = Arrangement.spacedBy(16.dp)
                ) {

                    item {
                        ElevatedCard(
                            shape = RoundedCornerShape(18.dp),
                            colors = CardDefaults.elevatedCardColors(containerColor = Indigo50),
                            modifier = Modifier.fillMaxWidth()
                        ) {
                            Column(modifier = Modifier.padding(20.dp)) {
                                Row(
                                    modifier = Modifier.fillMaxWidth(),
                                    verticalAlignment = Alignment.CenterVertically,
                                    horizontalArrangement = Arrangement.SpaceBetween
                                ) {
                                    Column(modifier = Modifier.weight(1f)) {
                                        Text(
                                            text = "CAMPUS SCORE",
                                            style = MaterialTheme.typography.labelMedium.copy(fontWeight = FontWeight.Bold),
                                            color = Indigo700
                                        )
                                        Text(
                                            text = "Student Index: $campusScore/100",
                                            style = MaterialTheme.typography.headlineSmall.copy(fontWeight = FontWeight.Bold),
                                            color = Indigo900
                                        )
                                        Spacer(Modifier.height(4.dp))
                                        Text(
                                            text = "Level $userLevel • $userXp Total XP",
                                            style = MaterialTheme.typography.bodyMedium,
                                            color = Indigo800
                                        )
                                    }

                                    GraphifyCircularGauge(
                                        score = campusScore,
                                        title = "Campus Score",
                                        color = Indigo600,
                                        size = 90.dp
                                    )
                                }

                                Spacer(Modifier.height(16.dp))
                                Divider(color = Indigo200)
                                Spacer(Modifier.height(12.dp))


                                val factors = listOf(
                                    Triple("Study", 80, Indigo600),
                                    Triple("Attendance", currentUser?.attendancePercent ?: 90, Emerald600),
                                    Triple("Budget", 70, Amber600),
                                    Triple("Skills", 85, Sky600),
                                    Triple("Activities", 82, Rose600)
                                )

                                Column(verticalArrangement = Arrangement.spacedBy(6.dp)) {
                                    factors.forEach { (name, pct, col) ->
                                        Row(
                                            modifier = Modifier.fillMaxWidth(),
                                            verticalAlignment = Alignment.CenterVertically,
                                            horizontalArrangement = Arrangement.SpaceBetween
                                        ) {
                                            Text(name, style = MaterialTheme.typography.labelSmall, color = Indigo900)
                                            Row(
                                                verticalAlignment = Alignment.CenterVertically,
                                                modifier = Modifier.width(180.dp)
                                            ) {
                                                LinearProgressIndicator(
                                                    progress = { pct / 100f },
                                                    modifier = Modifier.weight(1f).height(6.dp).clip(RoundedCornerShape(3.dp)),
                                                    color = col,
                                                    trackColor = Indigo200
                                                )
                                                Spacer(Modifier.width(8.dp))
                                                Text("$pct%", style = MaterialTheme.typography.labelSmall.copy(fontWeight = FontWeight.Bold), color = Indigo900)
                                            }
                                        }
                                    }
                                }
                            }
                        }
                    }


                    item {
                        SectionHeader(
                            title = "Achievement Badges (${badges.count { it.isUnlocked }}/${badges.size})",
                            actionText = "Unlock Next",
                            onActionClick = {}
                        )
                    }


                    items(badges) { badge ->
                        BadgeItemCard(
                            badge = badge,
                            onUnlock = { viewModel.unlockBadge(badge.id) }
                        )
                    }
                }
            } else {

                LazyColumn(
                    modifier = Modifier.fillMaxSize(),
                    verticalArrangement = Arrangement.spacedBy(14.dp)
                ) {

                    item {
                        LazyRow(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                            items(leaderboardCategories) { cat ->
                                FilterChip(
                                    selected = selectedLeaderboardCategory == cat,
                                    onClick = { selectedLeaderboardCategory = cat },
                                    label = { Text(cat) },
                                    shape = RoundedCornerShape(10.dp)
                                )
                            }
                        }
                    }


                    item {
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.SpaceBetween
                        ) {
                            Text(
                                text = "Hide me from public leaderboard",
                                style = MaterialTheme.typography.bodySmall,
                                color = MaterialTheme.colorScheme.onSurfaceVariant
                            )
                            Switch(
                                checked = hideFromLeaderboard,
                                onCheckedChange = { hideFromLeaderboard = it }
                            )
                        }
                    }


                    items(demoLeaderboard) { user ->
                        if (!hideFromLeaderboard || !user.isCurrentUser) {
                            LeaderboardUserRow(user = user)
                        }
                    }
                }
            }
        }
    }
}

@Composable
fun BadgeItemCard(
    badge: BadgeEntity,
    onUnlock: () -> Unit,
    modifier: Modifier = Modifier
) {
    ElevatedCard(
        shape = RoundedCornerShape(14.dp),
        colors = CardDefaults.elevatedCardColors(
            containerColor = if (badge.isUnlocked) MaterialTheme.colorScheme.surface else MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.5f)
        ),
        modifier = modifier.fillMaxWidth()
    ) {
        Row(
            modifier = Modifier.padding(14.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Box(
                modifier = Modifier
                    .size(48.dp)
                    .clip(CircleShape)
                    .background(if (badge.isUnlocked) Amber100 else Slate200),
                contentAlignment = Alignment.Center
            ) {
                Text(
                    text = badge.icon,
                    fontSize = 24.sp
                )
            }

            Spacer(Modifier.width(14.dp))

            Column(modifier = Modifier.weight(1f)) {
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    Text(
                        text = badge.title,
                        style = MaterialTheme.typography.titleSmall.copy(fontWeight = FontWeight.Bold),
                        color = if (badge.isUnlocked) MaterialTheme.colorScheme.onSurface else Slate500
                    )
                    if (badge.isUnlocked) {
                        Surface(shape = RoundedCornerShape(6.dp), color = Emerald50) {
                            Text("Unlocked", style = MaterialTheme.typography.labelSmall.copy(fontSize = 10.sp), color = Emerald700, modifier = Modifier.padding(horizontal = 6.dp, vertical = 2.dp))
                        }
                    }
                }

                Spacer(Modifier.height(2.dp))

                Text(
                    text = badge.description,
                    style = MaterialTheme.typography.bodySmall,
                    color = if (badge.isUnlocked) MaterialTheme.colorScheme.onSurfaceVariant else Slate500
                )
            }

            Surface(shape = RoundedCornerShape(8.dp), color = Amber500.copy(alpha = 0.15f)) {
                Text(
                    text = "+${badge.xpValue} XP",
                    style = MaterialTheme.typography.labelSmall.copy(fontWeight = FontWeight.Bold),
                    color = Amber700,
                    modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp)
                )
            }
        }
    }
}

@Composable
fun LeaderboardUserRow(
    user: LeaderboardUser,
    modifier: Modifier = Modifier
) {
    val rankBadgeColor = when (user.rank) {
        1 -> Amber500
        2 -> Slate400
        3 -> Amber700
        else -> Indigo600
    }

    ElevatedCard(
        shape = RoundedCornerShape(14.dp),
        colors = CardDefaults.elevatedCardColors(
            containerColor = if (user.isCurrentUser) Indigo50 else MaterialTheme.colorScheme.surface
        ),
        elevation = CardDefaults.elevatedCardElevation(defaultElevation = if (user.isCurrentUser) 3.dp else 1.dp),
        modifier = modifier.fillMaxWidth()
    ) {
        Row(
            modifier = Modifier.padding(14.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {

            Box(
                modifier = Modifier
                    .size(32.dp)
                    .clip(CircleShape)
                    .background(rankBadgeColor.copy(alpha = 0.2f)),
                contentAlignment = Alignment.Center
            ) {
                Text(
                    text = "#${user.rank}",
                    style = MaterialTheme.typography.labelMedium.copy(fontWeight = FontWeight.Bold),
                    color = rankBadgeColor
                )
            }

            Spacer(Modifier.width(12.dp))


            Column(modifier = Modifier.weight(1f)) {
                Text(
                    text = user.name,
                    style = MaterialTheme.typography.titleSmall.copy(fontWeight = FontWeight.Bold),
                    color = if (user.isCurrentUser) Indigo900 else MaterialTheme.colorScheme.onSurface
                )
                Text(
                    text = "${user.college} • Lvl ${user.level}",
                    style = MaterialTheme.typography.bodySmall,
                    color = Slate600
                )
            }


            Column(horizontalAlignment = Alignment.End) {
                Text(
                    text = "${user.score} pts",
                    style = MaterialTheme.typography.titleSmall.copy(fontWeight = FontWeight.Bold),
                    color = Indigo600
                )
                Text(
                    text = "${user.xp} XP",
                    style = MaterialTheme.typography.labelSmall,
                    color = Slate500
                )
            }
        }
    }
}
