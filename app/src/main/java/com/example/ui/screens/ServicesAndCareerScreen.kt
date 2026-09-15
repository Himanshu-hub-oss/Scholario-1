package com.example.ui.screens

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
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.example.data.model.CampusGrievanceEntity
import com.example.data.model.CareerEntity
import com.example.data.model.LostFoundEntity
import com.example.data.model.MarketplaceEntity
import com.example.ui.components.StatusBadge
import com.example.ui.theme.*
import com.example.ui.viewmodel.CampusViewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ServicesAndCareerScreen(
    viewModel: CampusViewModel,
    onOpenResumeBuilder: () -> Unit = {},
    modifier: Modifier = Modifier
) {
    var selectedTab by remember { mutableIntStateOf(0) }
    val tabs = listOf("Campus Issues & HOD Desk", "Lost & Found", "Campus Marketplace", "Career & Internships")

    val grievances by viewModel.grievances.collectAsStateWithLifecycle()
    val lostFoundItems by viewModel.lostAndFoundItems.collectAsStateWithLifecycle()
    val marketplaceItems by viewModel.marketplaceItems.collectAsStateWithLifecycle()
    val careerItems by viewModel.careerItems.collectAsStateWithLifecycle()
    val aiCareerResult by viewModel.aiCareerResult.collectAsStateWithLifecycle()
    val isAiLoading by viewModel.isAiLoading.collectAsStateWithLifecycle()
    val currentUser by viewModel.currentUser.collectAsStateWithLifecycle()

    var showAddGrievanceDialog by remember { mutableStateOf(false) }
    var showAddLostFoundDialog by remember { mutableStateOf(false) }
    var showAddMarketDialog by remember { mutableStateOf(false) }
    var contactingSellerItem by remember { mutableStateOf<MarketplaceEntity?>(null) }
    var contactingLostItem by remember { mutableStateOf<LostFoundEntity?>(null) }
    var reportingLostId by remember { mutableStateOf<Long?>(null) }

    Scaffold(
        topBar = {
            TopAppBar(
                title = {
                    Column {
                        Text(
                            text = "Campus Services & Student Desk",
                            style = MaterialTheme.typography.titleLarge.copy(fontWeight = FontWeight.Bold)
                        )
                        Text(
                            text = "Report Campus Issues to HOD, Lost & Found, and Student Market",
                            style = MaterialTheme.typography.bodySmall.copy(color = MaterialTheme.colorScheme.onSurfaceVariant)
                        )
                    }
                },
                actions = {
                    when (selectedTab) {
                        0 -> IconButton(
                            onClick = { showAddGrievanceDialog = true },
                            modifier = Modifier.testTag("services_add_grievance_button")
                        ) {
                            Icon(imageVector = Icons.Default.AddPhotoAlternate, contentDescription = "Report Campus Issue", tint = Rose600)
                        }
                        1 -> IconButton(
                            onClick = { showAddLostFoundDialog = true },
                            modifier = Modifier.testTag("services_add_lost_found_button")
                        ) {
                            Icon(imageVector = Icons.Default.AddCircle, contentDescription = "Add Lost/Found", tint = Indigo600)
                        }
                        2 -> IconButton(
                            onClick = { showAddMarketDialog = true },
                            modifier = Modifier.testTag("services_add_marketplace_button")
                        ) {
                            Icon(imageVector = Icons.Default.AddShoppingCart, contentDescription = "Sell Item", tint = Emerald600)
                        }
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(containerColor = MaterialTheme.colorScheme.surface)
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
                containerColor = MaterialTheme.colorScheme.surface,
                contentColor = MaterialTheme.colorScheme.primary,
                edgePadding = 12.dp,
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
                        modifier = Modifier.testTag("services_tab_$index")
                    )
                }
            }

            when (selectedTab) {
                0 -> GrievancesPortalTab(
                    grievances = grievances,
                    onReportNew = { showAddGrievanceDialog = true }
                )
                1 -> LostAndFoundTab(
                    items = lostFoundItems,
                    onAddNew = { showAddLostFoundDialog = true },
                    onContact = { contactingLostItem = it },
                    onResolve = { viewModel.resolveLostFound(it) },
                    onReport = { reportingLostId = it }
                )
                2 -> MarketplaceTab(
                    items = marketplaceItems,
                    onAddNew = { showAddMarketDialog = true },
                    onContact = { contactingSellerItem = it }
                )
                3 -> CareerPortalTab(
                    careerItems = careerItems,
                    user = currentUser,
                    aiResult = aiCareerResult,
                    isLoading = isAiLoading,
                    onGetCareerAi = { deg, skills, interests ->
                        viewModel.runCareerGuidance(deg, skills, interests)
                    },
                    onOpenResumeBuilder = onOpenResumeBuilder
                )
            }
        }
    }


    if (showAddGrievanceDialog) {
        AddGrievanceModal(
            onDismiss = { showAddGrievanceDialog = false },
            onAdd = { title, cat, loc, desc, photoEmoji, photoTag, dept, priority ->
                viewModel.reportCampusGrievance(title, cat, loc, desc, photoEmoji, photoTag, dept, priority)
                showAddGrievanceDialog = false
            }
        )
    }


    if (showAddLostFoundDialog) {
        AddLostFoundModal(
            onDismiss = { showAddLostFoundDialog = false },
            onAdd = { type, name, cat, desc, loc, date, contact ->
                viewModel.addLostFound(type, name, cat, desc, loc, date, contact)
                showAddLostFoundDialog = false
            }
        )
    }


    if (showAddMarketDialog) {
        AddMarketplaceModal(
            onDismiss = { showAddMarketDialog = false },
            onAdd = { title, price, cat, condition, loc, desc ->
                viewModel.addMarketplaceItem(title, price, cat, condition, loc, desc)
                showAddMarketDialog = false
            }
        )
    }


    contactingSellerItem?.let { item ->
        AlertDialog(
            onDismissRequest = { contactingSellerItem = null },
            title = { Text("Connect with Seller", fontWeight = FontWeight.Bold) },
            text = {
                Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                    Text("Item: ${item.title}", fontWeight = FontWeight.SemiBold)
                    Text("Price: ₹${item.price.toInt()}", color = Emerald600, fontWeight = FontWeight.Bold)
                    Text("Seller: ${item.sellerName}")
                    Text("Pickup: ${item.location}")
                    Surface(
                        shape = RoundedCornerShape(8.dp),
                        color = Emerald50,
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        Text(
                            text = "🛡️ Scholario Safe Connect: In-app student chat and verified hostel pickup location protected.",
                            style = MaterialTheme.typography.bodySmall.copy(color = Emerald800),
                            modifier = Modifier.padding(10.dp)
                        )
                    }
                }
            },
            confirmButton = {
                Button(onClick = { contactingSellerItem = null }) { Text("Start Safe Chat") }
            },
            dismissButton = { TextButton(onClick = { contactingSellerItem = null }) { Text("Close") } }
        )
    }


    contactingLostItem?.let { item ->
        AlertDialog(
            onDismissRequest = { contactingLostItem = null },
            title = { Text("Contact ${if (item.type == "LOST") "Owner" else "Finder"}", fontWeight = FontWeight.Bold) },
            text = {
                Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                    Text("Item: ${item.itemName}", fontWeight = FontWeight.SemiBold)
                    Text("Location: ${item.location}")
                    Text("Info: ${item.contactInfo}")
                    Surface(
                        shape = RoundedCornerShape(8.dp),
                        color = Indigo50,
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        Text(
                            text = "Campus ID verified student profile. You can message via in-app safe chat or meet at Security Desk.",
                            style = MaterialTheme.typography.bodySmall.copy(color = Indigo800),
                            modifier = Modifier.padding(10.dp)
                        )
                    }
                }
            },
            confirmButton = {
                Button(onClick = { contactingLostItem = null }) { Text("Message via App") }
            },
            dismissButton = { TextButton(onClick = { contactingLostItem = null }) { Text("Close") } }
        )
    }


    reportingLostId?.let { id ->
        ReportItemModal(
            title = "Report Lost/Found Post",
            onDismiss = { reportingLostId = null },
            onConfirmReport = { reason ->
                viewModel.reportLostFound(id, reason)
                reportingLostId = null
            }
        )
    }
}



@Composable
fun LostAndFoundTab(
    items: List<LostFoundEntity>,
    onAddNew: () -> Unit,
    onContact: (LostFoundEntity) -> Unit,
    onResolve: (Long) -> Unit,
    onReport: (Long) -> Unit
) {
    var typeFilter by remember { mutableStateOf("ALL") }
    val filtered = when (typeFilter) {
        "LOST" -> items.filter { it.type == "LOST" }
        "FOUND" -> items.filter { it.type == "FOUND" }
        else -> items
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
                Row(horizontalArrangement = Arrangement.spacedBy(6.dp)) {
                    listOf("ALL", "LOST", "FOUND").forEach { t ->
                        FilterChip(
                            selected = typeFilter == t,
                            onClick = { typeFilter = t },
                            label = { Text(t, fontSize = 11.sp) }
                        )
                    }
                }

                Button(
                    onClick = onAddNew,
                    shape = RoundedCornerShape(10.dp),
                    contentPadding = PaddingValues(horizontal = 12.dp, vertical = 6.dp)
                ) {
                    Icon(imageVector = Icons.Default.Add, contentDescription = null, modifier = Modifier.size(16.dp))
                    Spacer(modifier = Modifier.width(4.dp))
                    Text("Report Item", fontSize = 12.sp)
                }
            }
        }

        items(filtered) { item ->
            val isLost = item.type == "LOST"
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
                                text = if (isLost) "LOST ITEM" else "FOUND ITEM",
                                color = if (isLost) Rose500 else Emerald600,
                                bgColor = if (isLost) Rose400.copy(alpha = 0.15f) else Emerald400.copy(alpha = 0.15f)
                            )
                            Spacer(modifier = Modifier.width(8.dp))
                            StatusBadge(
                                text = item.category,
                                color = Indigo600,
                                bgColor = Indigo50
                            )
                        }

                        if (!item.isResolved) {
                            IconButton(onClick = { onReport(item.id) }) {
                                Icon(
                                    imageVector = Icons.Default.Flag,
                                    contentDescription = "Report",
                                    tint = MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.6f),
                                    modifier = Modifier.size(18.dp)
                                )
                            }
                        } else {
                            StatusBadge(text = "Resolved ✓", color = Emerald600, bgColor = Emerald50)
                        }
                    }

                    Spacer(modifier = Modifier.height(8.dp))
                    Text(
                        text = item.itemName,
                        style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold),
                        color = MaterialTheme.colorScheme.onSurface
                    )
                    Spacer(modifier = Modifier.height(2.dp))
                    Text(
                        text = "📍 Location: ${item.location} • 📅 Date: ${item.date}",
                        style = MaterialTheme.typography.bodySmall.copy(fontSize = 12.sp, color = MaterialTheme.colorScheme.onSurfaceVariant)
                    )
                    Spacer(modifier = Modifier.height(6.dp))
                    Text(
                        text = item.description,
                        style = MaterialTheme.typography.bodySmall.copy(fontSize = 12.sp, color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.88f))
                    )

                    Spacer(modifier = Modifier.height(12.dp))
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text(
                            text = "🛡️ Verified Student Post",
                            style = MaterialTheme.typography.labelSmall.copy(color = MaterialTheme.colorScheme.onSurfaceVariant, fontSize = 11.sp)
                        )

                        Row {
                            if (!item.isResolved) {
                                TextButton(onClick = { onResolve(item.id) }) {
                                    Text("Mark Resolved", fontSize = 11.sp)
                                }
                                Spacer(modifier = Modifier.width(6.dp))
                            }
                            Button(
                                onClick = { onContact(item) },
                                shape = RoundedCornerShape(10.dp),
                                contentPadding = PaddingValues(horizontal = 12.dp, vertical = 6.dp)
                            ) {
                                Text(if (isLost) "Contact Owner" else "Claim Item", fontSize = 12.sp, fontWeight = FontWeight.Bold)
                            }
                        }
                    }
                }
            }
        }
    }
}

@Composable
fun MarketplaceTab(
    items: List<MarketplaceEntity>,
    onAddNew: () -> Unit,
    onContact: (MarketplaceEntity) -> Unit
) {
    var selectedCategory by remember { mutableStateOf("All") }
    val categories = listOf("All", "Books", "Calculators", "Cycles", "Electronics", "Furniture", "Other")

    val filtered = if (selectedCategory == "All") items else items.filter { it.category == selectedCategory }

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
                    text = "Student-to-Student Marketplace",
                    style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold)
                )
                Button(
                    onClick = onAddNew,
                    colors = ButtonDefaults.buttonColors(containerColor = Emerald600),
                    shape = RoundedCornerShape(10.dp),
                    contentPadding = PaddingValues(horizontal = 12.dp, vertical = 6.dp)
                ) {
                    Icon(imageVector = Icons.Default.Add, contentDescription = null, modifier = Modifier.size(16.dp))
                    Spacer(modifier = Modifier.width(4.dp))
                    Text("Sell Item", fontSize = 12.sp, fontWeight = FontWeight.Bold)
                }
            }
        }


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

        items(filtered) { item ->
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
                                text = item.category,
                                color = Indigo600,
                                bgColor = Indigo50
                            )
                            Spacer(modifier = Modifier.width(6.dp))
                            StatusBadge(
                                text = item.condition,
                                color = Emerald600,
                                bgColor = Emerald50
                            )
                        }

                        Text(
                            text = "₹${item.price.toInt()}",
                            style = MaterialTheme.typography.titleLarge.copy(
                                fontWeight = FontWeight.ExtraBold,
                                color = Emerald600
                            )
                        )
                    }

                    Spacer(modifier = Modifier.height(8.dp))
                    Text(
                        text = item.title,
                        style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold),
                        color = MaterialTheme.colorScheme.onSurface
                    )
                    Text(
                        text = "📍 Location: ${item.location} • Seller: ${item.sellerName}",
                        style = MaterialTheme.typography.bodySmall.copy(fontSize = 11.sp, color = MaterialTheme.colorScheme.onSurfaceVariant)
                    )
                    Spacer(modifier = Modifier.height(4.dp))
                    Text(
                        text = item.description,
                        style = MaterialTheme.typography.bodySmall.copy(fontSize = 12.sp, color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.88f))
                    )

                    Spacer(modifier = Modifier.height(12.dp))
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text(
                            text = "🛡️ Safe Student Connect",
                            style = MaterialTheme.typography.labelSmall.copy(color = MaterialTheme.colorScheme.onSurfaceVariant, fontSize = 11.sp)
                        )

                        Button(
                            onClick = { onContact(item) },
                            colors = ButtonDefaults.buttonColors(containerColor = Emerald600),
                            shape = RoundedCornerShape(10.dp),
                            contentPadding = PaddingValues(horizontal = 14.dp, vertical = 6.dp)
                        ) {
                            Icon(imageVector = Icons.Default.Chat, contentDescription = null, modifier = Modifier.size(14.dp))
                            Spacer(modifier = Modifier.width(6.dp))
                            Text("Contact Seller", fontSize = 12.sp, fontWeight = FontWeight.Bold)
                        }
                    }
                }
            }
        }
    }
}

@Composable
fun CareerPortalTab(
    careerItems: List<CareerEntity>,
    user: com.example.data.model.UserEntity?,
    aiResult: String?,
    isLoading: Boolean,
    onGetCareerAi: (String, String, String) -> Unit,
    onOpenResumeBuilder: () -> Unit = {}
) {
    var selectedType by remember { mutableStateOf("ALL") }

    val filtered = when (selectedType) {
        "INTERNSHIP" -> careerItems.filter { it.type == "INTERNSHIP" }
        "JOB" -> careerItems.filter { it.type == "JOB" }
        "ROADMAP" -> careerItems.filter { it.type == "ROADMAP" }
        else -> careerItems
    }

    LazyColumn(
        modifier = Modifier.fillMaxSize(),
        contentPadding = PaddingValues(16.dp),
        verticalArrangement = Arrangement.spacedBy(14.dp)
    ) {

        item {
            Card(
                shape = RoundedCornerShape(18.dp),
                colors = CardDefaults.cardColors(containerColor = Indigo900),
                modifier = Modifier.fillMaxWidth()
            ) {
                Column(modifier = Modifier.padding(18.dp)) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Icon(imageVector = Icons.Default.AutoAwesome, contentDescription = null, tint = Cyan400)
                        Spacer(modifier = Modifier.width(8.dp))
                        Text(
                            text = "AI Career & Resume Strategist",
                            style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold, color = Color.White)
                        )
                    }
                    Spacer(modifier = Modifier.height(4.dp))
                    Text(
                        text = "Get customized placement tracks, skill gap analysis, and resume enhancement tips for your degree (${user?.course ?: "B.Tech"}).",
                        style = MaterialTheme.typography.bodySmall.copy(color = Indigo100)
                    )
                    Spacer(modifier = Modifier.height(12.dp))
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        Button(
                            onClick = {
                                onGetCareerAi(
                                    user?.course ?: "B.Tech Computer Science & AI",
                                    user?.skills ?: "Kotlin, Python, ML, System Design",
                                    "Mobile Architecture, AI Engineering, Distributed Systems"
                                )
                            },
                            colors = ButtonDefaults.buttonColors(containerColor = Cyan400),
                            shape = RoundedCornerShape(12.dp),
                            enabled = !isLoading,
                            modifier = Modifier
                                .weight(1f)
                                .testTag("career_get_ai_guidance_button")
                        ) {
                            if (isLoading) {
                                CircularProgressIndicator(color = Slate950, modifier = Modifier.size(18.dp), strokeWidth = 2.dp)
                                Spacer(modifier = Modifier.width(6.dp))
                                Text("Analyzing...", color = Slate950, fontSize = 12.sp)
                            } else {
                                Icon(imageVector = Icons.Default.WorkHistory, contentDescription = null, tint = Slate950, modifier = Modifier.size(16.dp))
                                Spacer(modifier = Modifier.width(6.dp))
                                Text("AI Career Tips", color = Slate950, fontWeight = FontWeight.Bold, fontSize = 12.sp)
                            }
                        }

                        OutlinedButton(
                            onClick = onOpenResumeBuilder,
                            shape = RoundedCornerShape(12.dp),
                            colors = ButtonDefaults.outlinedButtonColors(contentColor = Color.White),
                            border = androidx.compose.foundation.BorderStroke(1.dp, Cyan400),
                            modifier = Modifier
                                .weight(1f)
                                .testTag("career_open_resume_builder_button")
                        ) {
                            Icon(imageVector = Icons.Default.Description, contentDescription = null, tint = Cyan400, modifier = Modifier.size(16.dp))
                            Spacer(modifier = Modifier.width(6.dp))
                            Text("Resume Builder", color = Color.White, fontWeight = FontWeight.Bold, fontSize = 12.sp)
                        }
                    }
                }
            }
        }


        if (!aiResult.isNullOrBlank()) {
            item {
                Card(
                    shape = RoundedCornerShape(16.dp),
                    colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
                    elevation = CardDefaults.cardElevation(defaultElevation = 2.dp),
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Column(modifier = Modifier.padding(16.dp)) {
                        Text(
                            text = "🎯 AI Career Recommendation Report",
                            style = MaterialTheme.typography.titleSmall.copy(fontWeight = FontWeight.Bold, color = MaterialTheme.colorScheme.primary)
                        )
                        Spacer(modifier = Modifier.height(8.dp))
                        Text(
                            text = aiResult,
                            style = MaterialTheme.typography.bodySmall.copy(lineHeight = 20.sp, color = MaterialTheme.colorScheme.onSurface)
                        )
                    }
                }
            }
        }


        item {
            Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                listOf("ALL", "INTERNSHIP", "JOB", "ROADMAP").forEach { t ->
                    FilterChip(
                        selected = selectedType == t,
                        onClick = { selectedType = t },
                        label = { Text(t, fontSize = 11.sp) }
                    )
                }
            }
        }


        items(filtered) { item ->
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
                            text = item.type,
                            color = if (item.type == "INTERNSHIP") Cyan700 else Indigo700,
                            bgColor = if (item.type == "INTERNSHIP") Cyan50 else Indigo50
                        )
                        Text(
                            text = "Deadline: ${item.deadline}",
                            style = MaterialTheme.typography.labelSmall.copy(
                                color = MaterialTheme.colorScheme.onSurfaceVariant,
                                fontSize = 11.sp
                            )
                        )
                    }

                    Spacer(modifier = Modifier.height(8.dp))
                    Text(
                        text = item.title,
                        style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold),
                        color = MaterialTheme.colorScheme.onSurface
                    )
                    Text(
                        text = "${item.companyOrRole} • 📍 ${item.location}",
                        style = MaterialTheme.typography.bodySmall.copy(fontSize = 12.sp, color = MaterialTheme.colorScheme.primary, fontWeight = FontWeight.SemiBold)
                    )
                    Text(
                        text = "💰 Compensation: ${item.stipendOrSalary}",
                        style = MaterialTheme.typography.bodySmall.copy(fontSize = 11.sp, color = Emerald600, fontWeight = FontWeight.Bold)
                    )

                    Spacer(modifier = Modifier.height(6.dp))
                    Text(
                        text = item.description,
                        style = MaterialTheme.typography.bodySmall.copy(fontSize = 12.sp, color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.85f))
                    )

                    Spacer(modifier = Modifier.height(8.dp))
                    Text(
                        text = "🏷️ Required Skills: ${item.tags}",
                        style = MaterialTheme.typography.labelSmall.copy(color = MaterialTheme.colorScheme.onSurfaceVariant, fontSize = 11.sp)
                    )

                    Spacer(modifier = Modifier.height(12.dp))
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.End
                    ) {
                        Button(
                            onClick = {},
                            shape = RoundedCornerShape(10.dp),
                            contentPadding = PaddingValues(horizontal = 16.dp, vertical = 6.dp)
                        ) {
                            Text("Apply Now →", fontSize = 12.sp, fontWeight = FontWeight.Bold)
                        }
                    }
                }
            }
        }
    }
}



@Composable
fun AddLostFoundModal(
    onDismiss: () -> Unit,
    onAdd: (String, String, String, String, String, String, String) -> Unit
) {
    var type by remember { mutableStateOf("LOST") }
    var itemName by remember { mutableStateOf("") }
    var category by remember { mutableStateOf("Electronics") }
    var location by remember { mutableStateOf("Central Library") }
    var description by remember { mutableStateOf("") }
    var contact by remember { mutableStateOf("Campus ID verified student") }

    AlertDialog(
        onDismissRequest = onDismiss,
        title = { Text("Report Lost or Found Item", fontWeight = FontWeight.Bold) },
        text = {
            Column(
                modifier = Modifier.verticalScroll(rememberScrollState()),
                verticalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                    listOf("LOST", "FOUND").forEach { t ->
                        FilterChip(
                            selected = type == t,
                            onClick = { type = t },
                            label = { Text(t) }
                        )
                    }
                }
                OutlinedTextField(value = itemName, onValueChange = { itemName = it }, label = { Text("Item Name (e.g. Sony Headphones)") }, modifier = Modifier.fillMaxWidth())
                OutlinedTextField(value = category, onValueChange = { category = it }, label = { Text("Category") }, modifier = Modifier.fillMaxWidth())
                OutlinedTextField(value = location, onValueChange = { location = it }, label = { Text("Location where Lost/Found") }, modifier = Modifier.fillMaxWidth())
                OutlinedTextField(value = description, onValueChange = { description = it }, label = { Text("Identifying Details / Description") }, minLines = 3, modifier = Modifier.fillMaxWidth())
            }
        },
        confirmButton = {
            Button(
                onClick = {
                    if (itemName.isNotBlank()) {
                        onAdd(type, itemName, category, description, location, "2026-08-26", contact)
                    }
                },
                enabled = itemName.isNotBlank()
            ) { Text("Publish Item") }
        },
        dismissButton = { TextButton(onClick = onDismiss) { Text("Cancel") } }
    )
}

@Composable
fun AddMarketplaceModal(
    onDismiss: () -> Unit,
    onAdd: (String, Double, String, String, String, String) -> Unit
) {
    var title by remember { mutableStateOf("") }
    var price by remember { mutableStateOf("") }
    var category by remember { mutableStateOf("Books") }
    var condition by remember { mutableStateOf("Like New") }
    var location by remember { mutableStateOf("Hostel Block 3") }
    var description by remember { mutableStateOf("") }

    AlertDialog(
        onDismissRequest = onDismiss,
        title = { Text("List Item on Marketplace", fontWeight = FontWeight.Bold) },
        text = {
            Column(
                modifier = Modifier.verticalScroll(rememberScrollState()),
                verticalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                OutlinedTextField(value = title, onValueChange = { title = it }, label = { Text("Title (e.g. Casio Calculator)") }, modifier = Modifier.fillMaxWidth())
                OutlinedTextField(value = price, onValueChange = { price = it }, label = { Text("Price (₹)") }, modifier = Modifier.fillMaxWidth())
                OutlinedTextField(value = category, onValueChange = { category = it }, label = { Text("Category (Books, Cycles, Electronics)") }, modifier = Modifier.fillMaxWidth())
                OutlinedTextField(value = condition, onValueChange = { condition = it }, label = { Text("Condition (Brand New, Like New, Good)") }, modifier = Modifier.fillMaxWidth())
                OutlinedTextField(value = location, onValueChange = { location = it }, label = { Text("Pickup Location on Campus") }, modifier = Modifier.fillMaxWidth())
                OutlinedTextField(value = description, onValueChange = { description = it }, label = { Text("Description & Notes") }, minLines = 2, modifier = Modifier.fillMaxWidth())
            }
        },
        confirmButton = {
            Button(
                onClick = {
                    val p = price.toDoubleOrNull() ?: 0.0
                    if (title.isNotBlank() && p > 0) {
                        onAdd(title, p, category, condition, location, description)
                    }
                },
                enabled = title.isNotBlank() && (price.toDoubleOrNull() ?: 0.0) > 0
            ) { Text("List for Sale") }
        },
        dismissButton = { TextButton(onClick = onDismiss) { Text("Cancel") } }
    )
}



@Composable
fun GrievancesPortalTab(
    grievances: List<CampusGrievanceEntity>,
    onReportNew: () -> Unit
) {
    var selectedFilter by remember { mutableStateOf("ALL") }
    val filters = listOf("ALL", "PENDING", "IN_PROGRESS", "RESOLVED", "Water & Sanitation", "Electricity & AC", "Lab & Tech")

    val filteredList = remember(grievances, selectedFilter) {
        when (selectedFilter) {
            "ALL" -> grievances
            "PENDING" -> grievances.filter { it.status == "PENDING" }
            "IN_PROGRESS" -> grievances.filter { it.status == "IN_PROGRESS" || it.status == "UNDER_REVIEW" }
            "RESOLVED" -> grievances.filter { it.status == "RESOLVED" }
            else -> grievances.filter { it.category.contains(selectedFilter, ignoreCase = true) }
        }
    }

    val pendingCount = remember(grievances) { grievances.count { it.status == "PENDING" } }
    val inProgressCount = remember(grievances) { grievances.count { it.status == "IN_PROGRESS" || it.status == "UNDER_REVIEW" } }
    val resolvedCount = remember(grievances) { grievances.count { it.status == "RESOLVED" } }

    LazyColumn(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp),
        verticalArrangement = Arrangement.spacedBy(14.dp)
    ) {

        item {
            Card(
                shape = RoundedCornerShape(18.dp),
                colors = CardDefaults.cardColors(containerColor = Slate900),
                modifier = Modifier.fillMaxWidth()
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
                                    .size(42.dp)
                                    .clip(CircleShape)
                                    .background(Rose500.copy(alpha = 0.2f)),
                                contentAlignment = Alignment.Center
                            ) {
                                Icon(
                                    imageVector = Icons.Default.ReportProblem,
                                    contentDescription = null,
                                    tint = Rose400,
                                    modifier = Modifier.size(24.dp)
                                )
                            }
                            Spacer(modifier = Modifier.width(12.dp))
                            Column {
                                Text(
                                    text = "Campus Issue Redressal Desk",
                                    style = MaterialTheme.typography.titleMedium.copy(
                                        color = Color.White,
                                        fontWeight = FontWeight.Bold
                                    )
                                )
                                Text(
                                    text = "Report dirty water coolers, broken AC, lab defects to HOD",
                                    style = MaterialTheme.typography.bodySmall.copy(color = Slate300, fontSize = 11.sp)
                                )
                            }
                        }
                    }

                    Spacer(modifier = Modifier.height(14.dp))


                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        Surface(
                            shape = RoundedCornerShape(10.dp),
                            color = Rose950,
                            modifier = Modifier.weight(1f)
                        ) {
                            Column(modifier = Modifier.padding(10.dp), horizontalAlignment = Alignment.CenterHorizontally) {
                                Text("$pendingCount", style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold, color = Rose400))
                                Text("Pending HOD", style = MaterialTheme.typography.labelSmall.copy(color = Rose200, fontSize = 10.sp))
                            }
                        }
                        Surface(
                            shape = RoundedCornerShape(10.dp),
                            color = Amber900.copy(alpha = 0.4f),
                            modifier = Modifier.weight(1f)
                        ) {
                            Column(modifier = Modifier.padding(10.dp), horizontalAlignment = Alignment.CenterHorizontally) {
                                Text("$inProgressCount", style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold, color = Amber400))
                                Text("In Action", style = MaterialTheme.typography.labelSmall.copy(color = Amber100, fontSize = 10.sp))
                            }
                        }
                        Surface(
                            shape = RoundedCornerShape(10.dp),
                            color = Emerald900.copy(alpha = 0.4f),
                            modifier = Modifier.weight(1f)
                        ) {
                            Column(modifier = Modifier.padding(10.dp), horizontalAlignment = Alignment.CenterHorizontally) {
                                Text("$resolvedCount", style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold, color = Emerald400))
                                Text("Solved ✅", style = MaterialTheme.typography.labelSmall.copy(color = Emerald100, fontSize = 10.sp))
                            }
                        }
                    }

                    Spacer(modifier = Modifier.height(14.dp))

                    Button(
                        onClick = onReportNew,
                        colors = ButtonDefaults.buttonColors(containerColor = Rose600),
                        shape = RoundedCornerShape(12.dp),
                        modifier = Modifier
                            .fillMaxWidth()
                            .testTag("report_campus_problem_banner_btn")
                    ) {
                        Icon(Icons.Default.AddPhotoAlternate, contentDescription = null, modifier = Modifier.size(18.dp))
                        Spacer(modifier = Modifier.width(8.dp))
                        Text("Report Campus Problem + Attach Photo", fontWeight = FontWeight.Bold)
                    }
                }
            }
        }


        item {
            LazyRow(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                items(filters) { filter ->
                    FilterChip(
                        selected = selectedFilter == filter,
                        onClick = { selectedFilter = filter },
                        label = {
                            Text(
                                when (filter) {
                                    "ALL" -> "All Issues (${grievances.size})"
                                    "PENDING" -> "Pending ($pendingCount)"
                                    "IN_PROGRESS" -> "In Action ($inProgressCount)"
                                    "RESOLVED" -> "Solved ($resolvedCount)"
                                    else -> filter
                                },
                                fontSize = 12.sp
                            )
                        }
                    )
                }
            }
        }

        if (filteredList.isEmpty()) {
            item {
                Card(
                    shape = RoundedCornerShape(14.dp),
                    colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Column(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(24.dp),
                        horizontalAlignment = Alignment.CenterHorizontally
                    ) {
                        Icon(
                            imageVector = Icons.Default.CheckCircle,
                            contentDescription = null,
                            tint = Emerald500,
                            modifier = Modifier.size(48.dp)
                        )
                        Spacer(modifier = Modifier.height(8.dp))
                        Text("No Complaints in this view", fontWeight = FontWeight.Bold)
                        Text(
                            "Great news! All reported campus issues in this section have been resolved or none filed.",
                            style = MaterialTheme.typography.bodySmall.copy(color = MaterialTheme.colorScheme.onSurfaceVariant),
                            textAlign = androidx.compose.ui.text.style.TextAlign.Center
                        )
                    }
                }
            }
        } else {
            items(filteredList, key = { it.id }) { item ->
                GrievanceItemCard(item = item)
            }
        }
    }
}

@Composable
fun GrievanceItemCard(item: CampusGrievanceEntity) {
    val statusColor = when (item.status) {
        "RESOLVED" -> Emerald600
        "IN_PROGRESS", "UNDER_REVIEW" -> Amber600
        else -> Rose600
    }
    val statusBg = when (item.status) {
        "RESOLVED" -> Emerald50
        "IN_PROGRESS", "UNDER_REVIEW" -> Amber50
        else -> Rose50
    }

    Card(
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp),
        modifier = Modifier
            .fillMaxWidth()
            .testTag("grievance_card_${item.id}")
    ) {
        Column(modifier = Modifier.padding(16.dp)) {

            Row(
                modifier = Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Text(
                        text = item.photoEmoji,
                        fontSize = 24.sp,
                        modifier = Modifier.padding(end = 8.dp)
                    )
                    Column {
                        Text(
                            text = item.category,
                            style = MaterialTheme.typography.labelSmall.copy(
                                color = Indigo600,
                                fontWeight = FontWeight.Bold
                            )
                        )
                        Text(
                            text = "📍 ${item.location}",
                            style = MaterialTheme.typography.bodySmall.copy(
                                color = MaterialTheme.colorScheme.onSurfaceVariant,
                                fontSize = 11.sp
                            )
                        )
                    }
                }

                StatusBadge(
                    text = when (item.status) {
                        "RESOLVED" -> "RESOLVED ✅"
                        "IN_PROGRESS" -> "IN PROGRESS 🛠️"
                        "UNDER_REVIEW" -> "UNDER REVIEW 🔍"
                        else -> "PENDING HOD ⏳"
                    },
                    color = statusColor,
                    bgColor = statusBg
                )
            }

            Spacer(modifier = Modifier.height(10.dp))


            Text(
                text = item.title,
                style = MaterialTheme.typography.titleMedium.copy(
                    fontWeight = FontWeight.Bold,
                    fontSize = 16.sp
                )
            )
            Spacer(modifier = Modifier.height(4.dp))
            Text(
                text = item.description,
                style = MaterialTheme.typography.bodyMedium.copy(
                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                    lineHeight = 20.sp
                )
            )

            Spacer(modifier = Modifier.height(12.dp))


            Surface(
                shape = RoundedCornerShape(12.dp),
                color = Slate100,
                border = androidx.compose.foundation.BorderStroke(1.dp, Slate200),
                modifier = Modifier.fillMaxWidth()
            ) {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(10.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Box(
                        modifier = Modifier
                            .size(36.dp)
                            .clip(RoundedCornerShape(8.dp))
                            .background(Indigo100),
                        contentAlignment = Alignment.Center
                    ) {
                        Icon(
                            imageVector = Icons.Default.CameraAlt,
                            contentDescription = null,
                            tint = Indigo700,
                            modifier = Modifier.size(20.dp)
                        )
                    }
                    Spacer(modifier = Modifier.width(10.dp))
                    Column(modifier = Modifier.weight(1f)) {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Text(
                                text = "📷 Photo Evidence Attached",
                                style = MaterialTheme.typography.labelSmall.copy(fontWeight = FontWeight.Bold, color = Indigo900)
                            )
                            Spacer(modifier = Modifier.width(6.dp))
                            Surface(
                                shape = RoundedCornerShape(4.dp),
                                color = Rose100
                            ) {
                                Text(
                                    text = item.priority,
                                    style = MaterialTheme.typography.labelSmall.copy(color = Rose700, fontSize = 9.sp, fontWeight = FontWeight.Bold),
                                    modifier = Modifier.padding(horizontal = 4.dp, vertical = 2.dp)
                                )
                            }
                        }
                        Text(
                            text = item.photoTag,
                            style = MaterialTheme.typography.bodySmall.copy(color = Slate700, fontSize = 11.sp)
                        )
                    }
                }
            }

            Spacer(modifier = Modifier.height(10.dp))


            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = "👤 ${item.reportedByName} (${item.reportedByRoll}) • ${item.department}",
                    style = MaterialTheme.typography.bodySmall.copy(
                        color = MaterialTheme.colorScheme.onSurfaceVariant,
                        fontSize = 11.sp
                    )
                )
            }


            if (!item.hodRemarks.isNullOrBlank() || item.status == "RESOLVED") {
                Spacer(modifier = Modifier.height(10.dp))
                Surface(
                    shape = RoundedCornerShape(12.dp),
                    color = if (item.status == "RESOLVED") Emerald50 else Amber50,
                    border = androidx.compose.foundation.BorderStroke(
                        1.dp,
                        if (item.status == "RESOLVED") Emerald300 else Amber300
                    ),
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Column(modifier = Modifier.padding(12.dp)) {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Icon(
                                imageVector = if (item.status == "RESOLVED") Icons.Default.Verified else Icons.Default.Engineering,
                                contentDescription = null,
                                tint = if (item.status == "RESOLVED") Emerald700 else Amber700,
                                modifier = Modifier.size(16.dp)
                            )
                            Spacer(modifier = Modifier.width(6.dp))
                            Text(
                                text = if (item.status == "RESOLVED") "HOD Redressal Resolution" else "HOD Action in Progress",
                                style = MaterialTheme.typography.labelMedium.copy(
                                    fontWeight = FontWeight.Bold,
                                    color = if (item.status == "RESOLVED") Emerald900 else Amber900
                                )
                            )
                        }
                        Spacer(modifier = Modifier.height(4.dp))
                        Text(
                            text = item.hodRemarks ?: "Maintenance engineer deployed for inspection.",
                            style = MaterialTheme.typography.bodySmall.copy(
                                color = if (item.status == "RESOLVED") Emerald950 else Amber950,
                                fontSize = 12.sp
                            )
                        )
                        if (!item.resolvedByHodName.isNullOrBlank()) {
                            Spacer(modifier = Modifier.height(4.dp))
                            Text(
                                text = "— Verified by ${item.resolvedByHodName} (${item.resolvedDate ?: "Today"})",
                                style = MaterialTheme.typography.labelSmall.copy(
                                    color = if (item.status == "RESOLVED") Emerald800 else Amber800,
                                    fontSize = 10.sp,
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

@Composable
fun AddGrievanceModal(
    onDismiss: () -> Unit,
    onAdd: (String, String, String, String, String, String, String, String) -> Unit
) {
    var title by remember { mutableStateOf("") }
    var category by remember { mutableStateOf("Water & Sanitation") }
    var department by remember { mutableStateOf("Computer Science & Engineering") }
    var location by remember { mutableStateOf("") }
    var description by remember { mutableStateOf("") }
    var priority by remember { mutableStateOf("HIGH") }


    val photoOptions = listOf(
        Triple("🚰", "Water Cooler Dirty / Algae Contamination", "Water & Sanitation"),
        Triple("❄️", "AC Compressor Defect / Blowing Warm Air", "Electricity & AC"),
        Triple("📽️", "Projector / Display Cable Flickering", "Classroom & Tech"),
        Triple("⚡", "Exposed Electric Switchboard / Sparking", "Electricity & AC"),
        Triple("🪑", "Broken Bench / Damaged Desk In Class", "Classroom & Furniture"),
        Triple("🔬", "Lab Equipment / Multimeter / PC Malfunction", "Lab & Equipment"),
        Triple("🚽", "Restroom Cleanliness & Hygiene Issue", "Water & Sanitation"),
        Triple("📶", "Wi-Fi Access Point Offline / No Signal", "Campus Wi-Fi / IT")
    )
    var selectedPhotoIndex by remember { mutableIntStateOf(0) }

    val categories = listOf(
        "Water & Sanitation",
        "Electricity & AC",
        "Lab & Equipment",
        "Classroom & Furniture",
        "Campus Wi-Fi / IT",
        "Mess & Canteen",
        "Hostel & Safety"
    )

    val departments = listOf(
        "Computer Science & Engineering",
        "Electronics & Communication",
        "Mechanical Engineering",
        "Civil Engineering",
        "Electrical Engineering",
        "Central Administration & Campus Maintenance",
        "Hostel & Student Welfare"
    )

    AlertDialog(
        onDismissRequest = onDismiss,
        title = {
            Row(verticalAlignment = Alignment.CenterVertically) {
                Icon(Icons.Default.ReportProblem, contentDescription = null, tint = Rose600)
                Spacer(modifier = Modifier.width(8.dp))
                Text("Report Campus Problem to HOD", fontWeight = FontWeight.Bold)
            }
        },
        text = {
            Column(
                modifier = Modifier.verticalScroll(rememberScrollState()),
                verticalArrangement = Arrangement.spacedBy(10.dp)
            ) {
                Text(
                    text = "Submit issues regarding dirty water coolers, broken fans/ACs, lab computers, or hygiene directly to department HODs.",
                    style = MaterialTheme.typography.bodySmall.copy(color = MaterialTheme.colorScheme.onSurfaceVariant)
                )

                OutlinedTextField(
                    value = title,
                    onValueChange = { title = it },
                    label = { Text("Problem Title (e.g. Water Cooler Dirty in Block B)") },
                    placeholder = { Text("e.g. Water cooler water is foul smelling") },
                    modifier = Modifier.fillMaxWidth()
                )


                Text("Category", style = MaterialTheme.typography.labelMedium.copy(fontWeight = FontWeight.Bold))
                LazyRow(horizontalArrangement = Arrangement.spacedBy(6.dp)) {
                    items(categories) { cat ->
                        FilterChip(
                            selected = category == cat,
                            onClick = { category = cat },
                            label = { Text(cat, fontSize = 11.sp) }
                        )
                    }
                }


                Text("Responsible Department / Cell", style = MaterialTheme.typography.labelMedium.copy(fontWeight = FontWeight.Bold))
                LazyRow(horizontalArrangement = Arrangement.spacedBy(6.dp)) {
                    items(departments) { dept ->
                        FilterChip(
                            selected = department == dept,
                            onClick = { department = dept },
                            label = { Text(dept.take(20) + if (dept.length > 20) "..." else "", fontSize = 11.sp) }
                        )
                    }
                }

                OutlinedTextField(
                    value = location,
                    onValueChange = { location = it },
                    label = { Text("Exact Campus Location / Floor") },
                    placeholder = { Text("e.g. Block B, 2nd Floor near CS Lab 3") },
                    modifier = Modifier.fillMaxWidth()
                )

                OutlinedTextField(
                    value = description,
                    onValueChange = { description = it },
                    label = { Text("Detailed Description of Problem") },
                    placeholder = { Text("Describe what is wrong so the maintenance staff and HOD can address it quickly...") },
                    minLines = 3,
                    modifier = Modifier.fillMaxWidth()
                )


                Text("Attach Problem Photo Evidence (Select Snapshot)", style = MaterialTheme.typography.labelMedium.copy(fontWeight = FontWeight.Bold))
                Column(verticalArrangement = Arrangement.spacedBy(6.dp)) {
                    photoOptions.forEachIndexed { index, (emoji, tag, _) ->
                        Surface(
                            shape = RoundedCornerShape(10.dp),
                            color = if (selectedPhotoIndex == index) Indigo50 else Slate50,
                            border = androidx.compose.foundation.BorderStroke(
                                1.dp,
                                if (selectedPhotoIndex == index) Indigo600 else Slate200
                            ),
                            modifier = Modifier
                                .fillMaxWidth()
                                .clickable {
                                    selectedPhotoIndex = index
                                    if (title.isBlank()) {
                                        title = tag
                                    }
                                }
                        ) {
                            Row(
                                modifier = Modifier.padding(8.dp),
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                RadioButton(
                                    selected = selectedPhotoIndex == index,
                                    onClick = { selectedPhotoIndex = index }
                                )
                                Text(emoji, fontSize = 20.sp)
                                Spacer(modifier = Modifier.width(8.dp))
                                Column {
                                    Text(tag, fontSize = 12.sp, fontWeight = FontWeight.SemiBold)
                                    Text("📷 Camera captured & geotagged", fontSize = 10.sp, color = MaterialTheme.colorScheme.onSurfaceVariant)
                                }
                            }
                        }
                    }
                }


                Text("Urgency Level", style = MaterialTheme.typography.labelMedium.copy(fontWeight = FontWeight.Bold))
                Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                    listOf("LOW", "MEDIUM", "HIGH", "URGENT").forEach { p ->
                        FilterChip(
                            selected = priority == p,
                            onClick = { priority = p },
                            label = { Text(p, fontSize = 11.sp, fontWeight = FontWeight.Bold) }
                        )
                    }
                }
            }
        },
        confirmButton = {
            Button(
                onClick = {
                    if (title.isNotBlank() && location.isNotBlank()) {
                        val selPhoto = photoOptions[selectedPhotoIndex]
                        onAdd(
                            title,
                            category,
                            location,
                            description.ifBlank { "Immediate campus maintenance requested for $title." },
                            selPhoto.first,
                            selPhoto.second,
                            department,
                            priority
                        )
                    }
                },
                enabled = title.isNotBlank() && location.isNotBlank(),
                colors = ButtonDefaults.buttonColors(containerColor = Rose600)
            ) {
                Text("Submit to HOD 🚀", fontWeight = FontWeight.Bold)
            }
        },
        dismissButton = { TextButton(onClick = onDismiss) { Text("Cancel") } }
    )
}

