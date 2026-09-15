package com.example.ui.screens

import androidx.compose.animation.*
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
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
import com.example.data.model.ResumeEntity
import com.example.ui.components.SectionHeader
import com.example.ui.theme.*
import com.example.ui.viewmodel.CampusViewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ResumeBuilderScreen(
    viewModel: CampusViewModel,
    onBack: () -> Unit,
    modifier: Modifier = Modifier
) {
    val initialResume by viewModel.userResume.collectAsStateWithLifecycle()
    val isAiLoading by viewModel.isAiLoading.collectAsStateWithLifecycle()
    val aiResumeCritique by viewModel.aiResumeCritique.collectAsStateWithLifecycle()

    var fullName by remember { mutableStateOf(initialResume?.fullName ?: "Alex Rivera") }
    var email by remember { mutableStateOf(initialResume?.email ?: "alex.rivera@campus.edu") }
    var phone by remember { mutableStateOf(initialResume?.phone ?: "+1 (555) 234-5678") }
    var location by remember { mutableStateOf(initialResume?.location ?: "Stanford, CA") }
    var summary by remember { mutableStateOf(initialResume?.summary ?: "Aspiring Software Engineer & AI Researcher.") }
    var education by remember { mutableStateOf(initialResume?.educationJson ?: "B.Tech in Computer Science & AI (GPA: 3.85/4.0) - Stanford (2023-2027)") }
    var skills by remember { mutableStateOf(initialResume?.skillsJson ?: "Kotlin, Jetpack Compose, Room SQLite, Python, PyTorch, Gemini API, Git") }
    var projects by remember { mutableStateOf(initialResume?.projectsJson ?: "Scholario Super App: Multi-module student productivity suite with Splitzy & Graphify.") }
    var experience by remember { mutableStateOf(initialResume?.experienceJson ?: "Undergraduate AI Research Assistant - Stanford AI Lab (2025 - Present)") }
    var certifications by remember { mutableStateOf(initialResume?.certificationsJson ?: "Google Associate Android Developer (AAD)") }

    var selectedTemplate by remember { mutableStateOf("MODERN") }
    var isPreviewMode by remember { mutableStateOf(false) }
    var showExportSnackbar by remember { mutableStateOf(false) }

    val templates = listOf(
        "MODERN" to "Modern Tech",
        "CLASSIC" to "Classic Academic",
        "MINIMALIST" to "Minimalist Slate"
    )

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text(if (isPreviewMode) "Resume Preview & Export" else "Resume Builder") },
                navigationIcon = {
                    IconButton(onClick = {
                        if (isPreviewMode) isPreviewMode = false else onBack()
                    }) {
                        Icon(Icons.Filled.ArrowBack, contentDescription = "Back")
                    }
                },
                actions = {
                    if (!isPreviewMode) {
                        TextButton(
                            onClick = {
                                val updated = ResumeEntity(
                                    fullName = fullName,
                                    email = email,
                                    phone = phone,
                                    location = location,
                                    summary = summary,
                                    educationJson = education,
                                    skillsJson = skills,
                                    projectsJson = projects,
                                    experienceJson = experience,
                                    certificationsJson = certifications,
                                    selectedTemplate = selectedTemplate
                                )
                                viewModel.saveResume(updated)
                                isPreviewMode = true
                            }
                        ) {
                            Text("Preview", color = Indigo600, fontWeight = FontWeight.Bold)
                        }
                    }
                }
            )
        },
        snackbarHost = {
            if (showExportSnackbar) {
                Snackbar(
                    action = {
                        TextButton(onClick = { showExportSnackbar = false }) {
                            Text("OK", color = Color.White)
                        }
                    },
                    modifier = Modifier.padding(16.dp)
                ) {
                    Text("Resume formatted and ready for ATS export!")
                }
            }
        }
    ) { padding ->
        if (isPreviewMode) {

            LazyColumn(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(padding)
                    .background(MaterialTheme.colorScheme.background)
                    .padding(16.dp),
                verticalArrangement = Arrangement.spacedBy(16.dp)
            ) {

                item {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                            templates.forEach { (key, label) ->
                                FilterChip(
                                    selected = selectedTemplate == key,
                                    onClick = { selectedTemplate = key },
                                    label = { Text(label) },
                                    shape = RoundedCornerShape(10.dp)
                                )
                            }
                        }

                        Button(
                            onClick = { showExportSnackbar = true },
                            colors = ButtonDefaults.buttonColors(containerColor = Indigo600),
                            shape = RoundedCornerShape(10.dp)
                        ) {
                            Icon(Icons.Filled.Download, contentDescription = null, modifier = Modifier.size(16.dp))
                            Spacer(Modifier.width(6.dp))
                            Text("Export PDF")
                        }
                    }
                }


                item {
                    ElevatedCard(
                        shape = RoundedCornerShape(14.dp),
                        colors = CardDefaults.elevatedCardColors(containerColor = Indigo50),
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        Column(modifier = Modifier.padding(16.dp)) {
                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.SpaceBetween,
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Row(verticalAlignment = Alignment.CenterVertically) {
                                    Icon(Icons.Filled.AutoAwesome, contentDescription = null, tint = Indigo600)
                                    Spacer(Modifier.width(8.dp))
                                    Text(
                                        text = "Campus AI ATS Auditor",
                                        style = MaterialTheme.typography.titleSmall.copy(fontWeight = FontWeight.Bold),
                                        color = Indigo900
                                    )
                                }

                                Button(
                                    onClick = {
                                        val cur = ResumeEntity(
                                            fullName = fullName,
                                            email = email,
                                            phone = phone,
                                            location = location,
                                            summary = summary,
                                            educationJson = education,
                                            skillsJson = skills,
                                            projectsJson = projects,
                                            experienceJson = experience,
                                            certificationsJson = certifications,
                                            selectedTemplate = selectedTemplate
                                        )
                                        viewModel.auditResumeWithAi(cur)
                                    },
                                    colors = ButtonDefaults.buttonColors(containerColor = Indigo600),
                                    shape = RoundedCornerShape(8.dp),
                                    contentPadding = PaddingValues(horizontal = 12.dp, vertical = 6.dp)
                                ) {
                                    Text("Audit Score")
                                }
                            }

                            if (isAiLoading) {
                                Spacer(Modifier.height(12.dp))
                                LinearProgressIndicator(modifier = Modifier.fillMaxWidth(), color = Indigo600)
                            } else if (!aiResumeCritique.isNullOrBlank()) {
                                Spacer(Modifier.height(12.dp))
                                Text(
                                    text = aiResumeCritique ?: "",
                                    style = MaterialTheme.typography.bodySmall,
                                    color = Indigo900
                                )
                            }
                        }
                    }
                }


                item {
                    val docBorderColor = when (selectedTemplate) {
                        "MODERN" -> Indigo600
                        "CLASSIC" -> Slate700
                        else -> Slate400
                    }

                    ElevatedCard(
                        shape = RoundedCornerShape(8.dp),
                        colors = CardDefaults.elevatedCardColors(containerColor = Color.White),
                        elevation = CardDefaults.elevatedCardElevation(defaultElevation = 4.dp),
                        modifier = Modifier
                            .fillMaxWidth()
                            .border(1.dp, Slate200, RoundedCornerShape(8.dp))
                    ) {
                        Column(modifier = Modifier.padding(20.dp)) {

                            Text(
                                text = fullName.uppercase(),
                                style = MaterialTheme.typography.headlineSmall.copy(fontWeight = FontWeight.Bold),
                                color = docBorderColor
                            )
                            Text(
                                text = "$email  •  $phone  •  $location",
                                style = MaterialTheme.typography.labelSmall,
                                color = Slate600
                            )

                            Divider(modifier = Modifier.padding(vertical = 12.dp), color = docBorderColor, thickness = 2.dp)


                            Text("PROFESSIONAL SUMMARY", style = MaterialTheme.typography.labelMedium.copy(fontWeight = FontWeight.Bold), color = docBorderColor)
                            Spacer(Modifier.height(4.dp))
                            Text(summary, style = MaterialTheme.typography.bodySmall, color = Slate800)

                            Spacer(Modifier.height(14.dp))


                            Text("EDUCATION", style = MaterialTheme.typography.labelMedium.copy(fontWeight = FontWeight.Bold), color = docBorderColor)
                            Spacer(Modifier.height(4.dp))
                            Text(education, style = MaterialTheme.typography.bodySmall, color = Slate800)

                            Spacer(Modifier.height(14.dp))


                            Text("TECHNICAL SKILLS", style = MaterialTheme.typography.labelMedium.copy(fontWeight = FontWeight.Bold), color = docBorderColor)
                            Spacer(Modifier.height(4.dp))
                            Text(skills, style = MaterialTheme.typography.bodySmall, color = Slate800)

                            Spacer(Modifier.height(14.dp))


                            Text("FEATURED PROJECTS", style = MaterialTheme.typography.labelMedium.copy(fontWeight = FontWeight.Bold), color = docBorderColor)
                            Spacer(Modifier.height(4.dp))
                            Text(projects, style = MaterialTheme.typography.bodySmall, color = Slate800)

                            Spacer(Modifier.height(14.dp))


                            Text("EXPERIENCE & LEADERSHIP", style = MaterialTheme.typography.labelMedium.copy(fontWeight = FontWeight.Bold), color = docBorderColor)
                            Spacer(Modifier.height(4.dp))
                            Text(experience, style = MaterialTheme.typography.bodySmall, color = Slate800)

                            Spacer(Modifier.height(14.dp))


                            Text("CERTIFICATIONS", style = MaterialTheme.typography.labelMedium.copy(fontWeight = FontWeight.Bold), color = docBorderColor)
                            Spacer(Modifier.height(4.dp))
                            Text(certifications, style = MaterialTheme.typography.bodySmall, color = Slate800)
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
                verticalArrangement = Arrangement.spacedBy(14.dp)
            ) {
                item {
                    Text(
                        text = "Build Your Professional Resume",
                        style = MaterialTheme.typography.headlineSmall.copy(fontWeight = FontWeight.Bold)
                    )
                    Text(
                        text = "Fill in your details to create an ATS-friendly tech resume in seconds",
                        style = MaterialTheme.typography.bodyMedium,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                }

                item {
                    OutlinedTextField(
                        value = fullName,
                        onValueChange = { fullName = it },
                        label = { Text("Full Name") },
                        modifier = Modifier.fillMaxWidth(),
                        shape = RoundedCornerShape(12.dp)
                    )
                }

                item {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(10.dp)
                    ) {
                        OutlinedTextField(
                            value = email,
                            onValueChange = { email = it },
                            label = { Text("Email") },
                            modifier = Modifier.weight(1f),
                            shape = RoundedCornerShape(12.dp)
                        )
                        OutlinedTextField(
                            value = phone,
                            onValueChange = { phone = it },
                            label = { Text("Phone") },
                            modifier = Modifier.weight(1f),
                            shape = RoundedCornerShape(12.dp)
                        )
                    }
                }

                item {
                    OutlinedTextField(
                        value = summary,
                        onValueChange = { summary = it },
                        label = { Text("Professional Summary") },
                        modifier = Modifier.fillMaxWidth(),
                        minLines = 3,
                        shape = RoundedCornerShape(12.dp)
                    )
                }

                item {
                    OutlinedTextField(
                        value = education,
                        onValueChange = { education = it },
                        label = { Text("Education (Degree, College, GPA, Year)") },
                        modifier = Modifier.fillMaxWidth(),
                        minLines = 2,
                        shape = RoundedCornerShape(12.dp)
                    )
                }

                item {
                    OutlinedTextField(
                        value = skills,
                        onValueChange = { skills = it },
                        label = { Text("Skills (Comma separated)") },
                        modifier = Modifier.fillMaxWidth(),
                        minLines = 2,
                        shape = RoundedCornerShape(12.dp)
                    )
                }

                item {
                    OutlinedTextField(
                        value = projects,
                        onValueChange = { projects = it },
                        label = { Text("Featured Projects") },
                        modifier = Modifier.fillMaxWidth(),
                        minLines = 3,
                        shape = RoundedCornerShape(12.dp)
                    )
                }

                item {
                    OutlinedTextField(
                        value = experience,
                        onValueChange = { experience = it },
                        label = { Text("Experience / Research / Leadership") },
                        modifier = Modifier.fillMaxWidth(),
                        minLines = 3,
                        shape = RoundedCornerShape(12.dp)
                    )
                }

                item {
                    OutlinedTextField(
                        value = certifications,
                        onValueChange = { certifications = it },
                        label = { Text("Certifications & Achievements") },
                        modifier = Modifier.fillMaxWidth(),
                        minLines = 2,
                        shape = RoundedCornerShape(12.dp)
                    )
                }

                item {
                    Button(
                        onClick = {
                            val updated = ResumeEntity(
                                fullName = fullName,
                                email = email,
                                phone = phone,
                                location = location,
                                summary = summary,
                                educationJson = education,
                                skillsJson = skills,
                                projectsJson = projects,
                                experienceJson = experience,
                                certificationsJson = certifications,
                                selectedTemplate = selectedTemplate
                            )
                            viewModel.saveResume(updated)
                            isPreviewMode = true
                        },
                        shape = RoundedCornerShape(12.dp),
                        colors = ButtonDefaults.buttonColors(containerColor = Indigo600),
                        modifier = Modifier.fillMaxWidth().height(50.dp)
                    ) {
                        Icon(Icons.Filled.Visibility, contentDescription = null)
                        Spacer(Modifier.width(8.dp))
                        Text("Save & Preview Resume")
                    }
                }
            }
        }
    }
}
