package com.example.ui.screens

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
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
import com.example.data.model.CampusGrievanceEntity
import com.example.data.model.UserEntity
import com.example.ui.components.SectionHeader
import com.example.ui.components.StatusBadge
import com.example.ui.theme.*
import com.example.ui.viewmodel.CampusViewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ProfileAndAdminScreen(
    viewModel: CampusViewModel,
    modifier: Modifier = Modifier
) {
    val currentUser by viewModel.currentUser.collectAsStateWithLifecycle()
    val allUsers by viewModel.allUsers.collectAsStateWithLifecycle()
    val grievances by viewModel.grievances.collectAsStateWithLifecycle()
    val reportedPosts by viewModel.reportedPosts.collectAsStateWithLifecycle()
    val reportedLostFound by viewModel.reportedLostFound.collectAsStateWithLifecycle()
    val reportedMarketplace by viewModel.reportedMarketplace.collectAsStateWithLifecycle()
    val isShieldActive by viewModel.isShieldActive.collectAsStateWithLifecycle()
    val securityIncidents by viewModel.securityIncidents.collectAsStateWithLifecycle()

    var showEditProfileDialog by remember { mutableStateOf(false) }
    var resolvingGrievance by remember { mutableStateOf<CampusGrievanceEntity?>(null) }

    Scaffold(
        topBar = {
            TopAppBar(
                title = {
                    Column {
                        Text(
                            text = "Student Profile & Admin",
                            style = MaterialTheme.typography.titleLarge.copy(fontWeight = FontWeight.Bold)
                        )
                        Text(
                            text = "Academic records, preferences, and role settings",
                            style = MaterialTheme.typography.bodySmall.copy(color = MaterialTheme.colorScheme.onSurfaceVariant)
                        )
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(containerColor = MaterialTheme.colorScheme.surface)
            )
        },
        modifier = modifier
    ) { innerPadding ->
        LazyColumn(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding)
                .background(MaterialTheme.colorScheme.background),
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
                    Column(modifier = Modifier.padding(18.dp)) {
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Box(
                                modifier = Modifier
                                    .size(64.dp)
                                    .clip(CircleShape)
                                    .background(
                                        Brush.linearGradient(listOf(Indigo600, Cyan500))
                                    ),
                                contentAlignment = Alignment.Center
                            ) {
                                Text(
                                    text = currentUser?.name?.take(1) ?: "A",
                                    style = MaterialTheme.typography.headlineMedium.copy(
                                        color = Color.White,
                                        fontWeight = FontWeight.ExtraBold
                                    )
                                )
                            }

                            Spacer(modifier = Modifier.width(14.dp))

                            Column(modifier = Modifier.weight(1f)) {
                                Row(verticalAlignment = Alignment.CenterVertically) {
                                    Text(
                                        text = currentUser?.name ?: "Alex Rivera",
                                        style = MaterialTheme.typography.titleLarge.copy(
                                            fontWeight = FontWeight.Bold,
                                            fontSize = 19.sp
                                        ),
                                        color = MaterialTheme.colorScheme.onSurface
                                    )
                                    Spacer(modifier = Modifier.width(6.dp))
                                    StatusBadge(
                                        text = currentUser?.role ?: "Student",
                                        color = Indigo600,
                                        bgColor = Indigo50
                                    )
                                }
                                Text(
                                    text = "📱 ${currentUser?.phone ?: "+91 9876543210"} • 🆔 ${currentUser?.studentId ?: "STU-2024-8842"}",
                                    style = MaterialTheme.typography.bodySmall.copy(color = MaterialTheme.colorScheme.onSurfaceVariant, fontSize = 11.sp)
                                )
                                Text(
                                    text = "🏛️ ${currentUser?.college ?: "Stanford Institute of Tech"} (${currentUser?.university ?: "Stanford University"})",
                                    style = MaterialTheme.typography.bodySmall.copy(
                                        fontSize = 11.sp,
                                        color = MaterialTheme.colorScheme.primary,
                                        fontWeight = FontWeight.Medium
                                    )
                                )
                                Text(
                                    text = "🎓 ${currentUser?.course} • ${currentUser?.year}",
                                    style = MaterialTheme.typography.bodySmall.copy(
                                        fontSize = 11.sp,
                                        color = MaterialTheme.colorScheme.onSurfaceVariant
                                    )
                                )
                            }
                        }

                        Spacer(modifier = Modifier.height(14.dp))


                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.spacedBy(10.dp)
                        ) {
                            Surface(
                                shape = RoundedCornerShape(12.dp),
                                color = Indigo50,
                                modifier = Modifier.weight(1f)
                            ) {
                                Column(modifier = Modifier.padding(10.dp)) {
                                    Text("Cumulative GPA", style = MaterialTheme.typography.labelSmall, color = Indigo700)
                                    Text(
                                        "${currentUser?.gpa ?: 3.85} / 4.0",
                                        style = MaterialTheme.typography.titleMedium.copy(
                                            fontWeight = FontWeight.Bold,
                                            color = Indigo900
                                        )
                                    )
                                }
                            }

                            Surface(
                                shape = RoundedCornerShape(12.dp),
                                color = Emerald50,
                                modifier = Modifier.weight(1f)
                            ) {
                                Column(modifier = Modifier.padding(10.dp)) {
                                    Text("Attendance", style = MaterialTheme.typography.labelSmall, color = Emerald700)
                                    Text(
                                        "${currentUser?.attendancePercent ?: 92}%",
                                        style = MaterialTheme.typography.titleMedium.copy(
                                            fontWeight = FontWeight.Bold,
                                            color = Emerald900
                                        )
                                    )
                                }
                            }

                            Surface(
                                shape = RoundedCornerShape(12.dp),
                                color = Amber50,
                                modifier = Modifier.weight(1f)
                            ) {
                                Column(modifier = Modifier.padding(10.dp)) {
                                    Text("Monthly Budget", style = MaterialTheme.typography.labelSmall, color = Amber800)
                                    Text(
                                        "₹${(currentUser?.monthlyBudget ?: 8500.0).toInt()}",
                                        style = MaterialTheme.typography.titleMedium.copy(
                                            fontWeight = FontWeight.Bold,
                                            color = Amber900
                                        )
                                    )
                                }
                            }
                        }

                        Spacer(modifier = Modifier.height(12.dp))

                        Text(
                            text = "Skills & Interests: ${currentUser?.skills ?: ""}",
                            style = MaterialTheme.typography.bodySmall.copy(
                                fontSize = 11.sp,
                                color = MaterialTheme.colorScheme.onSurfaceVariant
                            )
                        )

                        Spacer(modifier = Modifier.height(12.dp))

                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.spacedBy(8.dp),
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            OutlinedButton(
                                onClick = { showEditProfileDialog = true },
                                shape = RoundedCornerShape(10.dp),
                                contentPadding = PaddingValues(horizontal = 10.dp, vertical = 4.dp),
                                modifier = Modifier.weight(1f)
                            ) {
                                Icon(imageVector = Icons.Default.Edit, contentDescription = null, modifier = Modifier.size(14.dp))
                                Spacer(modifier = Modifier.width(4.dp))
                                Text("Edit Info", fontSize = 11.sp)
                            }

                            FilledTonalButton(
                                onClick = {
                                    val current = currentUser
                                    if (current != null) {
                                        val newRole = if (current.role == "Admin") "Student" else "Admin"
                                        viewModel.updateUserProfile(current.copy(role = newRole))
                                    }
                                },
                                shape = RoundedCornerShape(10.dp),
                                contentPadding = PaddingValues(horizontal = 10.dp, vertical = 4.dp),
                                modifier = Modifier.weight(1f).testTag("switch_role_button")
                            ) {
                                Icon(
                                    imageVector = if (currentUser?.role == "Admin") Icons.Default.AdminPanelSettings else Icons.Default.Person,
                                    contentDescription = null,
                                    modifier = Modifier.size(14.dp)
                                )
                                Spacer(modifier = Modifier.width(4.dp))
                                Text(
                                    text = if (currentUser?.role == "Admin") "Student Mode" else "Admin Mode",
                                    fontSize = 11.sp,
                                    fontWeight = FontWeight.Bold
                                )
                            }

                            Button(
                                onClick = { viewModel.logoutStudent() },
                                shape = RoundedCornerShape(10.dp),
                                colors = ButtonDefaults.buttonColors(containerColor = Rose600),
                                contentPadding = PaddingValues(horizontal = 10.dp, vertical = 4.dp),
                                modifier = Modifier.weight(1f).testTag("logout_switch_button")
                            ) {
                                Icon(Icons.Default.Logout, contentDescription = null, modifier = Modifier.size(14.dp))
                                Spacer(modifier = Modifier.width(4.dp))
                                Text("Log Out", fontSize = 11.sp, fontWeight = FontWeight.Bold)
                            }
                        }
                    }
                }
            }


            if (currentUser?.role == "Admin") {
                item {
                    SectionHeader(title = "Campus Admin & HOD Action Center")
                }


                item {
                    Card(
                        shape = RoundedCornerShape(16.dp),
                        colors = CardDefaults.cardColors(containerColor = Slate900),
                        modifier = Modifier.fillMaxWidth().testTag("hod_grievance_center_card")
                    ) {
                        Column(modifier = Modifier.padding(16.dp)) {
                            Row(verticalAlignment = Alignment.CenterVertically) {
                                Box(
                                    modifier = Modifier
                                        .size(36.dp)
                                        .clip(CircleShape)
                                        .background(Amber500.copy(alpha = 0.2f)),
                                    contentAlignment = Alignment.Center
                                ) {
                                    Icon(imageVector = Icons.Default.Engineering, contentDescription = null, tint = Amber400)
                                }
                                Spacer(modifier = Modifier.width(10.dp))
                                Column {
                                    Text(
                                        text = "🏛️ HOD Grievance Redressal Desk",
                                        style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold, color = Color.White)
                                    )
                                    Text(
                                        text = "Review dirty water coolers, broken fans/ACs, lab tickets & post solutions",
                                        style = MaterialTheme.typography.bodySmall.copy(color = Slate300, fontSize = 11.sp)
                                    )
                                }
                            }
                            Spacer(modifier = Modifier.height(10.dp))
                            val pendingGrievances = grievances.count { it.status != "RESOLVED" }
                            val resolvedGrievances = grievances.count { it.status == "RESOLVED" }
                            Text(
                                text = "Action Required: $pendingGrievances Pending / In Progress • $resolvedGrievances Solved",
                                style = MaterialTheme.typography.bodySmall.copy(color = Amber300, fontWeight = FontWeight.SemiBold)
                            )
                        }
                    }
                }


                if (grievances.isNotEmpty()) {
                    item {
                        Text(
                            text = "Student Grievance Reports & Photo Evidence (${grievances.size})",
                            style = MaterialTheme.typography.titleSmall.copy(fontWeight = FontWeight.Bold)
                        )
                    }

                    items(grievances, key = { "admin_grievance_${it.id}" }) { grievance ->
                        Card(
                            shape = RoundedCornerShape(14.dp),
                            colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
                            elevation = CardDefaults.cardElevation(defaultElevation = 2.dp),
                            modifier = Modifier.fillMaxWidth().testTag("hod_grievance_item_${grievance.id}")
                        ) {
                            Column(modifier = Modifier.padding(14.dp)) {
                                Row(
                                    modifier = Modifier.fillMaxWidth(),
                                    horizontalArrangement = Arrangement.SpaceBetween,
                                    verticalAlignment = Alignment.CenterVertically
                                ) {
                                    Row(verticalAlignment = Alignment.CenterVertically) {
                                        Text(grievance.photoEmoji, fontSize = 20.sp)
                                        Spacer(modifier = Modifier.width(6.dp))
                                        Text(
                                            text = grievance.category,
                                            style = MaterialTheme.typography.labelSmall.copy(fontWeight = FontWeight.Bold, color = Indigo600)
                                        )
                                    }
                                    StatusBadge(
                                        text = when (grievance.status) {
                                            "RESOLVED" -> "RESOLVED ✅"
                                            "IN_PROGRESS" -> "IN ACTION 🛠️"
                                            else -> "NEEDS HOD ⏳"
                                        },
                                        color = if (grievance.status == "RESOLVED") Emerald600 else Rose600,
                                        bgColor = if (grievance.status == "RESOLVED") Emerald50 else Rose50
                                    )
                                }

                                Spacer(modifier = Modifier.height(6.dp))
                                Text(grievance.title, fontWeight = FontWeight.Bold, fontSize = 15.sp)
                                Text(
                                    text = "📍 ${grievance.location} • Dept: ${grievance.department}",
                                    style = MaterialTheme.typography.bodySmall.copy(color = MaterialTheme.colorScheme.onSurfaceVariant, fontSize = 11.sp)
                                )
                                Spacer(modifier = Modifier.height(4.dp))
                                Text(grievance.description, style = MaterialTheme.typography.bodySmall)

                                Spacer(modifier = Modifier.height(6.dp))
                                Surface(
                                    shape = RoundedCornerShape(8.dp),
                                    color = Slate100,
                                    modifier = Modifier.fillMaxWidth()
                                ) {
                                    Row(modifier = Modifier.padding(6.dp), verticalAlignment = Alignment.CenterVertically) {
                                        Icon(Icons.Default.CameraAlt, contentDescription = null, tint = Indigo600, modifier = Modifier.size(16.dp))
                                        Spacer(modifier = Modifier.width(6.dp))
                                        Text(
                                            text = "Evidence: ${grievance.photoTag} (${grievance.priority} Priority)",
                                            style = MaterialTheme.typography.labelSmall.copy(fontSize = 10.sp, color = Slate800)
                                        )
                                    }
                                }

                                if (!grievance.hodRemarks.isNullOrBlank()) {
                                    Spacer(modifier = Modifier.height(6.dp))
                                    Text(
                                        text = "💬 HOD Note: ${grievance.hodRemarks}",
                                        style = MaterialTheme.typography.labelSmall.copy(color = Emerald800, fontWeight = FontWeight.SemiBold)
                                    )
                                }

                                Spacer(modifier = Modifier.height(10.dp))
                                Row(
                                    modifier = Modifier.fillMaxWidth(),
                                    horizontalArrangement = Arrangement.SpaceBetween,
                                    verticalAlignment = Alignment.CenterVertically
                                ) {
                                    Text(
                                        text = "By: ${grievance.reportedByName} (${grievance.reportedByRoll})",
                                        style = MaterialTheme.typography.labelSmall.copy(color = MaterialTheme.colorScheme.onSurfaceVariant, fontSize = 10.sp)
                                    )

                                    Row(horizontalArrangement = Arrangement.spacedBy(6.dp)) {
                                        if (grievance.status != "IN_PROGRESS" && grievance.status != "RESOLVED") {
                                            OutlinedButton(
                                                onClick = {
                                                    viewModel.updateGrievanceStatus(
                                                        id = grievance.id,
                                                        status = "IN_PROGRESS",
                                                        remarks = "Maintenance team dispatched for on-site inspection.",
                                                        hodName = "HOD ${currentUser?.name ?: "Dr. R. Sharma"}"
                                                    )
                                                },
                                                contentPadding = PaddingValues(horizontal = 8.dp, vertical = 2.dp)
                                            ) {
                                                Text("Dispatch 🛠️", fontSize = 11.sp)
                                            }
                                        }

                                        if (grievance.status != "RESOLVED") {
                                            Button(
                                                onClick = { resolvingGrievance = grievance },
                                                colors = ButtonDefaults.buttonColors(containerColor = Emerald600),
                                                contentPadding = PaddingValues(horizontal = 10.dp, vertical = 2.dp),
                                                modifier = Modifier.testTag("hod_resolve_btn_${grievance.id}")
                                            ) {
                                                Text("Resolve & Remarks ✅", fontSize = 11.sp, fontWeight = FontWeight.Bold)
                                            }
                                        } else {
                                            OutlinedButton(
                                                onClick = {
                                                    viewModel.updateGrievanceStatus(
                                                        id = grievance.id,
                                                        status = "UNDER_REVIEW",
                                                        remarks = "Re-opened for secondary inspection.",
                                                        hodName = "HOD ${currentUser?.name ?: "Dr. R. Sharma"}"
                                                    )
                                                },
                                                contentPadding = PaddingValues(horizontal = 8.dp, vertical = 2.dp)
                                            ) {
                                                Text("Re-open 🔄", fontSize = 11.sp)
                                            }
                                        }
                                    }
                                }
                            }
                        }
                    }
                }


                item {
                    Card(
                        shape = RoundedCornerShape(16.dp),
                        colors = CardDefaults.cardColors(containerColor = Rose900),
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        Column(modifier = Modifier.padding(16.dp)) {
                            Row(verticalAlignment = Alignment.CenterVertically) {
                                Icon(imageVector = Icons.Default.Shield, contentDescription = null, tint = Rose400)
                                Spacer(modifier = Modifier.width(8.dp))
                                Text(
                                    text = "Active Flagged Content Queue",
                                    style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold, color = Color.White)
                                )
                            }
                            Spacer(modifier = Modifier.height(8.dp))
                            Text(
                                text = "Pending Review: ${reportedPosts.size} Posts • ${reportedLostFound.size} Lost/Found • ${reportedMarketplace.size} Marketplace Items",
                                style = MaterialTheme.typography.bodySmall.copy(color = Rose100)
                            )
                        }
                    }
                }


                if (reportedPosts.isNotEmpty()) {
                    item {
                        Text(
                            text = "Reported Community Posts (${reportedPosts.size})",
                            style = MaterialTheme.typography.titleSmall.copy(fontWeight = FontWeight.Bold)
                        )
                    }

                    items(reportedPosts) { post ->
                        Card(
                            shape = RoundedCornerShape(14.dp),
                            colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
                            elevation = CardDefaults.cardElevation(defaultElevation = 2.dp),
                            modifier = Modifier.fillMaxWidth()
                        ) {
                            Column(modifier = Modifier.padding(14.dp)) {
                                Row(
                                    modifier = Modifier.fillMaxWidth(),
                                    horizontalArrangement = Arrangement.SpaceBetween
                                ) {
                                    StatusBadge(text = "FLAGGED: ${post.reportReason ?: "Spam"}", color = Rose600, bgColor = Rose50)
                                    Text("By: ${post.authorName}", style = MaterialTheme.typography.bodySmall)
                                }
                                Spacer(modifier = Modifier.height(4.dp))
                                Text(post.title, fontWeight = FontWeight.Bold)
                                Text(post.content, style = MaterialTheme.typography.bodySmall, maxLines = 2)

                                Spacer(modifier = Modifier.height(10.dp))
                                Row(
                                    modifier = Modifier.fillMaxWidth(),
                                    horizontalArrangement = Arrangement.End
                                ) {
                                    TextButton(onClick = { viewModel.dismissPostReport(post.id) }) {
                                        Text("Dismiss Report")
                                    }
                                    Spacer(modifier = Modifier.width(8.dp))
                                    Button(
                                        onClick = { viewModel.deletePost(post.id) },
                                        colors = ButtonDefaults.buttonColors(containerColor = Rose600)
                                    ) {
                                        Text("Delete Post")
                                    }
                                }
                            }
                        }
                    }
                }


                if (reportedLostFound.isNotEmpty()) {
                    item {
                        Text(
                            text = "Reported Lost & Found Items (${reportedLostFound.size})",
                            style = MaterialTheme.typography.titleSmall.copy(fontWeight = FontWeight.Bold)
                        )
                    }

                    items(reportedLostFound) { item ->
                        Card(
                            shape = RoundedCornerShape(14.dp),
                            colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
                            modifier = Modifier.fillMaxWidth()
                        ) {
                            Column(modifier = Modifier.padding(14.dp)) {
                                StatusBadge(text = "REPORTED: ${item.reportReason ?: "Spam"}", color = Rose600, bgColor = Rose50)
                                Spacer(modifier = Modifier.height(4.dp))
                                Text(item.itemName, fontWeight = FontWeight.Bold)
                                Text(item.description, style = MaterialTheme.typography.bodySmall)
                                Spacer(modifier = Modifier.height(8.dp))
                                Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.End) {
                                    Button(
                                        onClick = { viewModel.deleteLostFound(item.id) },
                                        colors = ButtonDefaults.buttonColors(containerColor = Rose600)
                                    ) {
                                        Text("Delete Item")
                                    }
                                }
                            }
                        }
                    }
                }
            }


            item {
                SectionHeader(
                    title = "Data Security & Intrusion Shield",
                    actionText = if (isShieldActive) "Shield Active" else "Shield Paused",
                    onActionClick = { viewModel.toggleSecurityShield() }
                )
                Card(
                    shape = RoundedCornerShape(18.dp),
                    colors = CardDefaults.cardColors(
                        containerColor = if (isShieldActive) Indigo900 else Slate900
                    ),
                    modifier = Modifier
                        .fillMaxWidth()
                        .testTag("security_shield_card")
                ) {
                    Column(modifier = Modifier.padding(16.dp)) {
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.SpaceBetween
                        ) {
                            Row(verticalAlignment = Alignment.CenterVertically) {
                                Box(
                                    modifier = Modifier
                                        .size(40.dp)
                                        .clip(CircleShape)
                                        .background(if (isShieldActive) Emerald500.copy(alpha = 0.2f) else Rose500.copy(alpha = 0.2f)),
                                    contentAlignment = Alignment.Center
                                ) {
                                    Icon(
                                        imageVector = if (isShieldActive) Icons.Default.Security else Icons.Default.GppBad,
                                        contentDescription = null,
                                        tint = if (isShieldActive) Emerald400 else Rose400,
                                        modifier = Modifier.size(24.dp)
                                    )
                                }
                                Spacer(modifier = Modifier.width(10.dp))
                                Column {
                                    Text(
                                        text = if (isShieldActive) "Campus Shield: Protected" else "Threat Defense: Idle",
                                        style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold, color = Color.White)
                                    )
                                    Text(
                                        text = "On-Device AES Sandbox • Zero Data Leakage",
                                        style = MaterialTheme.typography.labelSmall.copy(color = Indigo100)
                                    )
                                }
                            }

                            Switch(
                                checked = isShieldActive,
                                onCheckedChange = { viewModel.toggleSecurityShield() },
                                colors = SwitchDefaults.colors(
                                    checkedThumbColor = Color.White,
                                    checkedTrackColor = Emerald500
                                )
                            )
                        }

                        Spacer(modifier = Modifier.height(14.dp))


                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.spacedBy(8.dp)
                        ) {
                            Surface(
                                shape = RoundedCornerShape(10.dp),
                                color = Color.White.copy(alpha = 0.1f),
                                modifier = Modifier.weight(1f)
                            ) {
                                Column(modifier = Modifier.padding(8.dp)) {
                                    Text("Sandboxed Storage", color = Cyan400, fontSize = 10.sp, fontWeight = FontWeight.Bold)
                                    Text("SQLite Isolated", color = Color.White, fontSize = 12.sp, fontWeight = FontWeight.SemiBold)
                                }
                            }
                            Surface(
                                shape = RoundedCornerShape(10.dp),
                                color = Color.White.copy(alpha = 0.1f),
                                modifier = Modifier.weight(1f)
                            ) {
                                Column(modifier = Modifier.padding(8.dp)) {
                                    Text("Intrusion Alerts", color = Amber400, fontSize = 10.sp, fontWeight = FontWeight.Bold)
                                    Text("${securityIncidents.size} Blocked", color = Color.White, fontSize = 12.sp, fontWeight = FontWeight.SemiBold)
                                }
                            }
                            Surface(
                                shape = RoundedCornerShape(10.dp),
                                color = Color.White.copy(alpha = 0.1f),
                                modifier = Modifier.weight(1f)
                            ) {
                                Column(modifier = Modifier.padding(8.dp)) {
                                    Text("Tamper Detection", color = Emerald400, fontSize = 10.sp, fontWeight = FontWeight.Bold)
                                    Text("Real-Time", color = Color.White, fontSize = 12.sp, fontWeight = FontWeight.SemiBold)
                                }
                            }
                        }

                        Spacer(modifier = Modifier.height(12.dp))

                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.spacedBy(8.dp)
                        ) {
                            Button(
                                onClick = { viewModel.simulateThreatIntrusionTest() },
                                shape = RoundedCornerShape(10.dp),
                                colors = ButtonDefaults.buttonColors(containerColor = Cyan400),
                                contentPadding = PaddingValues(horizontal = 12.dp, vertical = 6.dp),
                                modifier = Modifier.weight(1f).testTag("simulate_security_threat_button")
                            ) {
                                Icon(Icons.Default.BugReport, contentDescription = null, tint = Slate950, modifier = Modifier.size(16.dp))
                                Spacer(modifier = Modifier.width(4.dp))
                                Text("Test Intrusion Alert", color = Slate950, fontWeight = FontWeight.Bold, fontSize = 11.sp)
                            }

                            OutlinedButton(
                                onClick = { viewModel.clearSecurityLogs() },
                                shape = RoundedCornerShape(10.dp),
                                border = androidx.compose.foundation.BorderStroke(1.dp, Color.White.copy(alpha = 0.4f)),
                                contentPadding = PaddingValues(horizontal = 12.dp, vertical = 6.dp),
                                modifier = Modifier.weight(1f)
                            ) {
                                Icon(Icons.Default.ClearAll, contentDescription = null, tint = Color.White, modifier = Modifier.size(16.dp))
                                Spacer(modifier = Modifier.width(4.dp))
                                Text("Clear Logs", color = Color.White, fontSize = 11.sp)
                            }
                        }
                    }
                }
            }


            if (securityIncidents.isNotEmpty()) {
                item {
                    Text(
                        text = "Intrusion & Threat Detection Log (${securityIncidents.size})",
                        style = MaterialTheme.typography.titleSmall.copy(fontWeight = FontWeight.Bold)
                    )
                }

                items(securityIncidents) { incident ->
                    Card(
                        shape = RoundedCornerShape(14.dp),
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
                                StatusBadge(
                                    text = "${incident.severity}: ${incident.threatType}",
                                    color = if (incident.severity == "CRITICAL") Rose600 else Amber700,
                                    bgColor = if (incident.severity == "CRITICAL") Rose50 else Amber50
                                )
                                Text(
                                    text = incident.timeAgo,
                                    style = MaterialTheme.typography.labelSmall.copy(color = MaterialTheme.colorScheme.onSurfaceVariant)
                                )
                            }

                            Spacer(modifier = Modifier.height(8.dp))

                            Row(verticalAlignment = Alignment.CenterVertically) {
                                Icon(Icons.Default.LocationOn, contentDescription = null, tint = Rose500, modifier = Modifier.size(16.dp))
                                Spacer(modifier = Modifier.width(4.dp))
                                Text(
                                    text = "Origin: ${incident.attackerLocation}",
                                    style = MaterialTheme.typography.bodySmall.copy(fontWeight = FontWeight.Bold)
                                )
                            }

                            Row(verticalAlignment = Alignment.CenterVertically) {
                                Icon(Icons.Default.Dns, contentDescription = null, tint = Indigo500, modifier = Modifier.size(16.dp))
                                Spacer(modifier = Modifier.width(4.dp))
                                Text(
                                    text = "Attacker IP: ${incident.attackerIp}",
                                    style = MaterialTheme.typography.bodySmall.copy(fontFamily = androidx.compose.ui.text.font.FontFamily.Monospace, fontSize = 11.sp)
                                )
                            }

                            Text(
                                text = "Device: ${incident.deviceFingerprint}",
                                style = MaterialTheme.typography.labelSmall.copy(color = MaterialTheme.colorScheme.onSurfaceVariant)
                            )
                            Text(
                                text = "Vector: ${incident.attackVector}",
                                style = MaterialTheme.typography.labelSmall.copy(color = MaterialTheme.colorScheme.onSurfaceVariant)
                            )

                            Spacer(modifier = Modifier.height(6.dp))

                            Surface(
                                shape = RoundedCornerShape(6.dp),
                                color = Emerald50,
                                modifier = Modifier.fillMaxWidth()
                            ) {
                                Row(
                                    modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp),
                                    verticalAlignment = Alignment.CenterVertically
                                ) {
                                    Icon(Icons.Default.CheckCircle, contentDescription = null, tint = Emerald700, modifier = Modifier.size(14.dp))
                                    Spacer(modifier = Modifier.width(4.dp))
                                    Text(
                                        text = incident.status,
                                        style = MaterialTheme.typography.labelSmall.copy(color = Emerald900, fontWeight = FontWeight.Bold)
                                    )
                                }
                            }
                        }
                    }
                }
            }


            item {
                SectionHeader(title = "App & System Architecture")
                Card(
                    shape = RoundedCornerShape(16.dp),
                    colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Column(modifier = Modifier.padding(16.dp)) {
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween
                        ) {
                            Text("Database Engine", style = MaterialTheme.typography.bodySmall)
                            Text("Room SQLite (Encrypted / Reactive Flow)", fontWeight = FontWeight.Bold, fontSize = 12.sp)
                        }
                        Divider(modifier = Modifier.padding(vertical = 8.dp), color = MaterialTheme.colorScheme.surfaceVariant)
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween
                        ) {
                            Text("Gemini AI Core", style = MaterialTheme.typography.bodySmall)
                            Text("gemini-3.5-flash (Connected)", color = Emerald600, fontWeight = FontWeight.Bold, fontSize = 12.sp)
                        }
                        Divider(modifier = Modifier.padding(vertical = 8.dp), color = MaterialTheme.colorScheme.surfaceVariant)
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween
                        ) {
                            Text("Splitzy Engine", style = MaterialTheme.typography.bodySmall)
                            Text("Equal / Custom / Percentage Debt Graph", fontWeight = FontWeight.Bold, fontSize = 12.sp)
                        }
                    }
                }
            }
        }
    }


    if (showEditProfileDialog) {
        val u = currentUser
        if (u != null) {
            EditProfileModal(
                user = u,
                onDismiss = { showEditProfileDialog = false },
                onSave = { updated ->
                    viewModel.updateUserProfile(updated)
                    showEditProfileDialog = false
                }
            )
        }
    }


    resolvingGrievance?.let { grievance ->
        HodResolutionModal(
            grievance = grievance,
            defaultHodName = "HOD ${currentUser?.name ?: "Dr. R. Sharma"}",
            onDismiss = { resolvingGrievance = null },
            onResolve = { remarks, hodName ->
                viewModel.resolveGrievance(grievance.id, remarks, hodName)
                resolvingGrievance = null
            }
        )
    }
}

@Composable
fun HodResolutionModal(
    grievance: CampusGrievanceEntity,
    defaultHodName: String,
    onDismiss: () -> Unit,
    onResolve: (String, String) -> Unit
) {
    var remarks by remember {
        mutableStateOf(
            when (grievance.category) {
                "Water & Sanitation" -> "Water cooler tank thoroughly drained, sanitized with chlorine solution, and new dual RO sediment filter installed by campus maintenance."
                "Electricity & AC" -> "AC compressor serviced, gas refilled, and thermostat temperature recalibrated to 23°C."
                "Classroom & Furniture" -> "Damaged bench/desk replaced with new ergonomic furniture from central stores."
                "Lab & Equipment" -> "Lab technician inspected hardware, replaced faulty peripheral cable and verified test bench."
                else -> "Issue inspected on-site by estate maintenance team and verified fully resolved."
            }
        )
    }
    var hodName by remember { mutableStateOf(defaultHodName) }

    AlertDialog(
        onDismissRequest = onDismiss,
        title = {
            Row(verticalAlignment = Alignment.CenterVertically) {
                Icon(Icons.Default.Verified, contentDescription = null, tint = Emerald600)
                Spacer(modifier = Modifier.width(8.dp))
                Text("HOD Grievance Redressal", fontWeight = FontWeight.Bold)
            }
        },
        text = {
            Column(
                modifier = Modifier.verticalScroll(rememberScrollState()),
                verticalArrangement = Arrangement.spacedBy(10.dp)
            ) {
                Surface(
                    shape = RoundedCornerShape(10.dp),
                    color = Slate100,
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Column(modifier = Modifier.padding(10.dp)) {
                        Text(
                            text = "Ticket #${grievance.id}: ${grievance.title}",
                            style = MaterialTheme.typography.bodySmall.copy(fontWeight = FontWeight.Bold)
                        )
                        Text(
                            text = "📍 ${grievance.location} • By ${grievance.reportedByName} (${grievance.reportedByRoll})",
                            style = MaterialTheme.typography.bodySmall.copy(color = MaterialTheme.colorScheme.onSurfaceVariant, fontSize = 11.sp)
                        )
                        Spacer(modifier = Modifier.height(4.dp))
                        Text(
                            text = "📷 Evidence: ${grievance.photoEmoji} ${grievance.photoTag}",
                            style = MaterialTheme.typography.labelSmall.copy(color = Indigo700, fontWeight = FontWeight.SemiBold)
                        )
                    }
                }

                OutlinedTextField(
                    value = hodName,
                    onValueChange = { hodName = it },
                    label = { Text("HOD / Official Authority Name") },
                    modifier = Modifier.fillMaxWidth()
                )

                OutlinedTextField(
                    value = remarks,
                    onValueChange = { remarks = it },
                    label = { Text("Resolution Actions Taken (HOD Remarks)") },
                    placeholder = { Text("Describe the maintenance fix or resolution applied...") },
                    minLines = 3,
                    modifier = Modifier.fillMaxWidth()
                )
            }
        },
        confirmButton = {
            Button(
                onClick = {
                    if (remarks.isNotBlank()) {
                        onResolve(remarks, hodName.ifBlank { "HOD Department" })
                    }
                },
                enabled = remarks.isNotBlank(),
                colors = ButtonDefaults.buttonColors(containerColor = Emerald600)
            ) {
                Text("Mark as Solved ✅", fontWeight = FontWeight.Bold)
            }
        },
        dismissButton = { TextButton(onClick = onDismiss) { Text("Cancel") } }
    )
}

@Composable
fun EditProfileModal(
    user: UserEntity,
    onDismiss: () -> Unit,
    onSave: (UserEntity) -> Unit
) {
    var name by remember { mutableStateOf(user.name) }
    var phone by remember { mutableStateOf(user.phone) }
    var college by remember { mutableStateOf(user.college) }
    var university by remember { mutableStateOf(user.university) }
    var studentId by remember { mutableStateOf(user.studentId) }
    var course by remember { mutableStateOf(user.course) }
    var gpaText by remember { mutableStateOf(user.gpa.toString()) }
    var attendanceText by remember { mutableStateOf(user.attendancePercent.toString()) }
    var budgetText by remember { mutableStateOf(user.monthlyBudget.toInt().toString()) }
    var skills by remember { mutableStateOf(user.skills) }

    AlertDialog(
        onDismissRequest = onDismiss,
        title = { Text("Edit Student & Campus Info", fontWeight = FontWeight.Bold) },
        text = {
            Column(
                modifier = Modifier.verticalScroll(rememberScrollState()),
                verticalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                OutlinedTextField(value = name, onValueChange = { name = it }, label = { Text("Full Name") }, modifier = Modifier.fillMaxWidth())
                OutlinedTextField(value = phone, onValueChange = { phone = it }, label = { Text("Mobile / Phone Number") }, modifier = Modifier.fillMaxWidth())
                OutlinedTextField(value = college, onValueChange = { college = it }, label = { Text("College / Campus") }, modifier = Modifier.fillMaxWidth())
                OutlinedTextField(value = university, onValueChange = { university = it }, label = { Text("University / Board") }, modifier = Modifier.fillMaxWidth())
                OutlinedTextField(value = studentId, onValueChange = { studentId = it }, label = { Text("Student ID / Roll No") }, modifier = Modifier.fillMaxWidth())
                OutlinedTextField(value = course, onValueChange = { course = it }, label = { Text("Course / Department") }, modifier = Modifier.fillMaxWidth())
                OutlinedTextField(value = gpaText, onValueChange = { gpaText = it }, label = { Text("GPA (e.g. 3.85)") }, modifier = Modifier.fillMaxWidth())
                OutlinedTextField(value = attendanceText, onValueChange = { attendanceText = it }, label = { Text("Attendance % (e.g. 92)") }, modifier = Modifier.fillMaxWidth())
                OutlinedTextField(value = budgetText, onValueChange = { budgetText = it }, label = { Text("Monthly Budget (₹)") }, modifier = Modifier.fillMaxWidth())
                OutlinedTextField(value = skills, onValueChange = { skills = it }, label = { Text("Skills & Technologies") }, modifier = Modifier.fillMaxWidth())
            }
        },
        confirmButton = {
            Button(
                onClick = {
                    onSave(
                        user.copy(
                            name = name,
                            phone = phone,
                            college = college,
                            university = university,
                            studentId = studentId,
                            course = course,
                            gpa = gpaText.toDoubleOrNull() ?: user.gpa,
                            attendancePercent = attendanceText.toIntOrNull() ?: user.attendancePercent,
                            monthlyBudget = budgetText.toDoubleOrNull() ?: user.monthlyBudget,
                            skills = skills
                        )
                    )
                }
            ) { Text("Save Changes") }
        },
        dismissButton = { TextButton(onClick = onDismiss) { Text("Cancel") } }
    )
}
