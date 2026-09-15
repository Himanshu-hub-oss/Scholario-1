package com.example.ui.screens

import androidx.compose.animation.*
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.ui.theme.*
import com.example.ui.viewmodel.CampusViewModel
import kotlinx.coroutines.delay

@Composable
fun LoginAndOnboardingScreen(
    viewModel: CampusViewModel,
    onLoginSuccess: () -> Unit,
    modifier: Modifier = Modifier
) {

    var currentStep by remember { mutableIntStateOf(0) }


    var phoneNumber by remember { mutableStateOf("9876543210") }
    var enteredOtp by remember { mutableStateOf("") }
    var generatedOtp by remember { mutableStateOf<String?>(null) }
    var isOtpSent by remember { mutableStateOf(false) }
    var otpTimerSeconds by remember { mutableIntStateOf(0) }
    var errorMessage by remember { mutableStateOf<String?>(null) }


    var studentName by remember { mutableStateOf("Alex Rivera") }
    var collegeName by remember { mutableStateOf("Stanford Institute of Technology") }
    var universityName by remember { mutableStateOf("Stanford University") }
    var courseName by remember { mutableStateOf("B.Tech Computer Science & AI") }
    var studentId by remember { mutableStateOf("STU-2024-8842") }
    var yearSelected by remember { mutableStateOf("3rd Year (Junior)") }
    var semesterSelected by remember { mutableStateOf("Semester 6") }
    var emailAddress by remember { mutableStateOf("alex.rivera@campus.edu") }


    LaunchedEffect(otpTimerSeconds) {
        if (otpTimerSeconds > 0) {
            delay(1000L)
            otpTimerSeconds -= 1
        }
    }

    Box(
        modifier = modifier
            .fillMaxSize()
            .background(
                Brush.verticalGradient(
                    colors = listOf(
                        Indigo950,
                        Indigo900,
                        Slate900
                    )
                )
            )
            .statusBarsPadding()
            .navigationBarsPadding()
            .imePadding()
    ) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .verticalScroll(rememberScrollState())
                .padding(20.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center
        ) {

            Box(
                modifier = Modifier
                    .size(72.dp)
                    .clip(CircleShape)
                    .background(
                        Brush.linearGradient(listOf(Indigo500, Cyan400))
                    ),
                contentAlignment = Alignment.Center
            ) {
                Icon(
                    imageVector = Icons.Default.School,
                    contentDescription = "Campus Logo",
                    tint = Color.White,
                    modifier = Modifier.size(38.dp)
                )
            }

            Spacer(Modifier.height(14.dp))

            Text(
                text = "Scholario",
                style = MaterialTheme.typography.headlineMedium.copy(
                    fontWeight = FontWeight.ExtraBold,
                    color = Color.White,
                    letterSpacing = 1.sp
                )
            )

            Text(
                text = "The All-In-One Campus & Academic SuperApp",
                style = MaterialTheme.typography.bodyMedium.copy(
                    color = Indigo200,
                    textAlign = TextAlign.Center
                )
            )

            Spacer(Modifier.height(24.dp))


            Card(
                shape = RoundedCornerShape(24.dp),
                colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
                elevation = CardDefaults.cardElevation(defaultElevation = 8.dp),
                modifier = Modifier.fillMaxWidth()
            ) {
                Column(
                    modifier = Modifier.padding(24.dp),
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {

                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.Center,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Surface(
                            shape = CircleShape,
                            color = if (currentStep == 0) Indigo600 else Emerald600,
                            modifier = Modifier.size(28.dp)
                        ) {
                            Box(contentAlignment = Alignment.Center) {
                                Text(
                                    text = if (currentStep == 0) "1" else "✓",
                                    color = Color.White,
                                    fontSize = 12.sp,
                                    fontWeight = FontWeight.Bold
                                )
                            }
                        }

                        Box(
                            modifier = Modifier
                                .width(50.dp)
                                .height(3.dp)
                                .background(if (currentStep == 1) Indigo600 else Slate200)
                        )

                        Surface(
                            shape = CircleShape,
                            color = if (currentStep == 1) Indigo600 else Slate200,
                            modifier = Modifier.size(28.dp)
                        ) {
                            Box(contentAlignment = Alignment.Center) {
                                Text(
                                    text = "2",
                                    color = if (currentStep == 1) Color.White else Slate500,
                                    fontSize = 12.sp,
                                    fontWeight = FontWeight.Bold
                                )
                            }
                        }
                    }

                    Spacer(Modifier.height(18.dp))

                    AnimatedContent(
                        targetState = currentStep,
                        label = "auth_step_transition"
                    ) { step ->
                        if (step == 0) {

                            Column(horizontalAlignment = Alignment.CenterHorizontally) {
                                Text(
                                    text = "Student Phone Login",
                                    style = MaterialTheme.typography.titleLarge.copy(fontWeight = FontWeight.Bold),
                                    color = MaterialTheme.colorScheme.onSurface
                                )
                                Text(
                                    text = "Enter your mobile number to receive a 6-digit OTP verification code",
                                    style = MaterialTheme.typography.bodySmall.copy(
                                        color = MaterialTheme.colorScheme.onSurfaceVariant,
                                        textAlign = TextAlign.Center
                                    ),
                                    modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp)
                                )

                                Spacer(Modifier.height(18.dp))


                                Row(
                                    modifier = Modifier.fillMaxWidth(),
                                    verticalAlignment = Alignment.CenterVertically
                                ) {
                                    Surface(
                                        shape = RoundedCornerShape(12.dp),
                                        color = Slate100,
                                        modifier = Modifier.padding(end = 8.dp)
                                    ) {
                                        Text(
                                            text = "🇮🇳 +91",
                                            style = MaterialTheme.typography.bodyMedium.copy(fontWeight = FontWeight.Bold),
                                            modifier = Modifier.padding(horizontal = 12.dp, vertical = 15.dp)
                                        )
                                    }

                                    OutlinedTextField(
                                        value = phoneNumber,
                                        onValueChange = {
                                            if (it.length <= 10 && it.all { char -> char.isDigit() }) {
                                                phoneNumber = it
                                                errorMessage = null
                                            }
                                        },
                                        label = { Text("Phone Number") },
                                        placeholder = { Text("9876543210") },
                                        leadingIcon = {
                                            Icon(Icons.Default.Phone, contentDescription = null, tint = Indigo600)
                                        },
                                        keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                                        shape = RoundedCornerShape(14.dp),
                                        singleLine = true,
                                        modifier = Modifier
                                            .weight(1f)
                                            .testTag("login_phone_input")
                                    )
                                }

                                Spacer(Modifier.height(12.dp))

                                if (!isOtpSent) {
                                    Button(
                                        onClick = {
                                            if (phoneNumber.length < 10) {
                                                errorMessage = "Please enter a valid 10-digit mobile number."
                                                return@Button
                                            }
                                            val otp = ((100000..999999).random()).toString()
                                            generatedOtp = otp
                                            isOtpSent = true
                                            otpTimerSeconds = 30
                                            errorMessage = null
                                        },
                                        shape = RoundedCornerShape(14.dp),
                                        colors = ButtonDefaults.buttonColors(containerColor = Indigo600),
                                        modifier = Modifier
                                            .fillMaxWidth()
                                            .height(50.dp)
                                            .testTag("send_otp_button")
                                    ) {
                                        Icon(Icons.Default.Send, contentDescription = null, modifier = Modifier.size(18.dp))
                                        Spacer(Modifier.width(8.dp))
                                        Text("Send OTP Code", fontWeight = FontWeight.Bold)
                                    }
                                } else {

                                    Surface(
                                        shape = RoundedCornerShape(12.dp),
                                        color = Emerald50,
                                        border = androidx.compose.foundation.BorderStroke(1.dp, Emerald300),
                                        modifier = Modifier.fillMaxWidth()
                                    ) {
                                        Column(modifier = Modifier.padding(12.dp)) {
                                            Row(verticalAlignment = Alignment.CenterVertically) {
                                                Icon(Icons.Default.Sms, contentDescription = null, tint = Emerald700, modifier = Modifier.size(18.dp))
                                                Spacer(Modifier.width(6.dp))
                                                Text(
                                                    text = "SMS Received! Demo OTP: ${generatedOtp ?: "123456"}",
                                                    style = MaterialTheme.typography.bodySmall.copy(fontWeight = FontWeight.Bold, color = Emerald900)
                                                )
                                            }
                                            Spacer(Modifier.height(4.dp))
                                            Text(
                                                text = "Tap Auto-Fill below or type the 6-digit OTP code to verify.",
                                                style = MaterialTheme.typography.labelSmall.copy(color = Emerald800)
                                            )
                                        }
                                    }

                                    Spacer(Modifier.height(12.dp))


                                    OutlinedTextField(
                                        value = enteredOtp,
                                        onValueChange = {
                                            if (it.length <= 6 && it.all { char -> char.isDigit() }) {
                                                enteredOtp = it
                                                errorMessage = null
                                            }
                                        },
                                        label = { Text("Enter 6-Digit OTP") },
                                        placeholder = { Text("e.g. ${generatedOtp ?: "123456"}") },
                                        leadingIcon = {
                                            Icon(Icons.Default.Key, contentDescription = null, tint = Amber600)
                                        },
                                        trailingIcon = {
                                            TextButton(
                                                onClick = { enteredOtp = generatedOtp ?: "123456" }
                                            ) {
                                                Text("Auto-Fill", fontWeight = FontWeight.Bold, color = Indigo600)
                                            }
                                        },
                                        keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                                        shape = RoundedCornerShape(14.dp),
                                        singleLine = true,
                                        modifier = Modifier
                                            .fillMaxWidth()
                                            .testTag("otp_input")
                                    )

                                    Spacer(Modifier.height(8.dp))

                                    Row(
                                        modifier = Modifier.fillMaxWidth(),
                                        horizontalArrangement = Arrangement.SpaceBetween,
                                        verticalAlignment = Alignment.CenterVertically
                                    ) {
                                        Text(
                                            text = if (otpTimerSeconds > 0) "Resend in ${otpTimerSeconds}s" else "Didn't get OTP?",
                                            style = MaterialTheme.typography.labelSmall,
                                            color = Slate500
                                        )

                                        if (otpTimerSeconds == 0) {
                                            TextButton(
                                                onClick = {
                                                    val otp = ((100000..999999).random()).toString()
                                                    generatedOtp = otp
                                                    otpTimerSeconds = 30
                                                }
                                            ) {
                                                Text("Resend OTP", fontWeight = FontWeight.Bold, color = Indigo600)
                                            }
                                        }
                                    }

                                    Spacer(Modifier.height(14.dp))

                                    Button(
                                        onClick = {
                                            if (enteredOtp.length < 6) {
                                                errorMessage = "Please enter the complete 6-digit OTP."
                                                return@Button
                                            }
                                            if (enteredOtp == generatedOtp || enteredOtp == "123456") {

                                                currentStep = 1
                                            } else {
                                                errorMessage = "Invalid OTP code. Please use ${generatedOtp ?: "123456"}."
                                            }
                                        },
                                        shape = RoundedCornerShape(14.dp),
                                        colors = ButtonDefaults.buttonColors(containerColor = Indigo600),
                                        modifier = Modifier
                                            .fillMaxWidth()
                                            .height(50.dp)
                                            .testTag("verify_otp_button")
                                    ) {
                                        Icon(Icons.Default.CheckCircle, contentDescription = null, modifier = Modifier.size(18.dp))
                                        Spacer(Modifier.width(8.dp))
                                        Text("Verify OTP & Proceed", fontWeight = FontWeight.Bold)
                                    }
                                }

                                if (errorMessage != null) {
                                    Spacer(Modifier.height(8.dp))
                                    Text(
                                        text = errorMessage ?: "",
                                        color = Rose600,
                                        style = MaterialTheme.typography.labelSmall.copy(fontWeight = FontWeight.Bold)
                                    )
                                }

                                Spacer(Modifier.height(16.dp))
                                Divider(color = Slate200)
                                Spacer(Modifier.height(12.dp))


                                TextButton(
                                    onClick = {
                                        viewModel.loginDefaultStudent()
                                        onLoginSuccess()
                                    },
                                    modifier = Modifier.testTag("demo_login_skip_button")
                                ) {
                                    Text(
                                        text = "⚡ Quick Demo Login (Skip OTP with Pre-Seeded Scholar)",
                                        fontSize = 12.sp,
                                        fontWeight = FontWeight.SemiBold,
                                        color = Indigo600
                                    )
                                }
                            }
                        } else {

                            Column(
                                horizontalAlignment = Alignment.CenterHorizontally,
                                verticalArrangement = Arrangement.spacedBy(10.dp)
                            ) {
                                Text(
                                    text = "Campus & Academic Details",
                                    style = MaterialTheme.typography.titleLarge.copy(fontWeight = FontWeight.Bold),
                                    color = MaterialTheme.colorScheme.onSurface
                                )
                                Text(
                                    text = "Select your academic year to get personalized subjects, syllabus & study notes",
                                    style = MaterialTheme.typography.bodySmall.copy(
                                        color = MaterialTheme.colorScheme.onSurfaceVariant,
                                        textAlign = TextAlign.Center
                                    )
                                )

                                Spacer(Modifier.height(4.dp))


                                Column(modifier = Modifier.fillMaxWidth()) {
                                    Text(
                                        text = "🎓 Which Academic Year are you in?",
                                        style = MaterialTheme.typography.labelMedium.copy(fontWeight = FontWeight.Bold, color = Indigo700)
                                    )
                                    Spacer(Modifier.height(6.dp))
                                    val yearsList = listOf(
                                        "1st Year" to "Freshman (Sem 1-2)",
                                        "2nd Year" to "Sophomore (Sem 3-4)",
                                        "3rd Year" to "Junior (Sem 5-6)",
                                        "4th Year" to "Senior (Sem 7-8)"
                                    )
                                    Row(
                                        modifier = Modifier.fillMaxWidth(),
                                        horizontalArrangement = Arrangement.spacedBy(6.dp)
                                    ) {
                                        yearsList.forEach { (yrName, yrDesc) ->
                                            val isSelected = yearSelected.startsWith(yrName)
                                            Surface(
                                                shape = RoundedCornerShape(10.dp),
                                                color = if (isSelected) Indigo600 else Slate100,
                                                border = if (isSelected) null else androidx.compose.foundation.BorderStroke(1.dp, Slate200),
                                                modifier = Modifier
                                                    .weight(1f)
                                                    .clickable {
                                                        yearSelected = "$yrName ($yrDesc)"
                                                        semesterSelected = when (yrName) {
                                                            "1st Year" -> "Semester 1"
                                                            "2nd Year" -> "Semester 3"
                                                            "3rd Year" -> "Semester 5"
                                                            else -> "Semester 7"
                                                        }
                                                    }
                                            ) {
                                                Column(
                                                    modifier = Modifier.padding(vertical = 8.dp, horizontal = 4.dp),
                                                    horizontalAlignment = Alignment.CenterHorizontally
                                                ) {
                                                    Text(
                                                        text = yrName,
                                                        fontWeight = FontWeight.Bold,
                                                        fontSize = 12.sp,
                                                        color = if (isSelected) Color.White else Slate800
                                                    )
                                                    Text(
                                                        text = when (yrName) {
                                                            "1st Year" -> "Sem 1/2"
                                                            "2nd Year" -> "Sem 3/4"
                                                            "3rd Year" -> "Sem 5/6"
                                                            else -> "Sem 7/8"
                                                        },
                                                        fontSize = 9.sp,
                                                        color = if (isSelected) Indigo100 else Slate500
                                                    )
                                                }
                                            }
                                        }
                                    }
                                }

                                OutlinedTextField(
                                    value = studentName,
                                    onValueChange = { studentName = it },
                                    label = { Text("Full Name") },
                                    leadingIcon = { Icon(Icons.Default.Person, contentDescription = null, tint = Indigo600) },
                                    shape = RoundedCornerShape(12.dp),
                                    singleLine = true,
                                    modifier = Modifier
                                        .fillMaxWidth()
                                        .testTag("onboarding_name_input")
                                )

                                OutlinedTextField(
                                    value = collegeName,
                                    onValueChange = { collegeName = it },
                                    label = { Text("College / Campus Name") },
                                    leadingIcon = { Icon(Icons.Default.LocationCity, contentDescription = null, tint = Indigo600) },
                                    shape = RoundedCornerShape(12.dp),
                                    singleLine = true,
                                    modifier = Modifier
                                        .fillMaxWidth()
                                        .testTag("onboarding_college_input")
                                )

                                OutlinedTextField(
                                    value = universityName,
                                    onValueChange = { universityName = it },
                                    label = { Text("University / Board") },
                                    leadingIcon = { Icon(Icons.Default.AccountBalance, contentDescription = null, tint = Indigo600) },
                                    shape = RoundedCornerShape(12.dp),
                                    singleLine = true,
                                    modifier = Modifier
                                        .fillMaxWidth()
                                        .testTag("onboarding_university_input")
                                )

                                OutlinedTextField(
                                    value = courseName,
                                    onValueChange = { courseName = it },
                                    label = { Text("Course / Department") },
                                    leadingIcon = { Icon(Icons.Default.MenuBook, contentDescription = null, tint = Indigo600) },
                                    shape = RoundedCornerShape(12.dp),
                                    singleLine = true,
                                    modifier = Modifier
                                        .fillMaxWidth()
                                        .testTag("onboarding_course_input")
                                )

                                Row(
                                    modifier = Modifier.fillMaxWidth(),
                                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                                ) {
                                    OutlinedTextField(
                                        value = studentId,
                                        onValueChange = { studentId = it },
                                        label = { Text("Student ID / Roll") },
                                        shape = RoundedCornerShape(12.dp),
                                        singleLine = true,
                                        modifier = Modifier.weight(1f)
                                    )

                                    OutlinedTextField(
                                        value = semesterSelected,
                                        onValueChange = { semesterSelected = it },
                                        label = { Text("Semester") },
                                        shape = RoundedCornerShape(12.dp),
                                        singleLine = true,
                                        modifier = Modifier.weight(1f)
                                    )
                                }

                                OutlinedTextField(
                                    value = emailAddress,
                                    onValueChange = { emailAddress = it },
                                    label = { Text("College Email Address") },
                                    leadingIcon = { Icon(Icons.Default.Email, contentDescription = null, tint = Indigo600) },
                                    shape = RoundedCornerShape(12.dp),
                                    singleLine = true,
                                    modifier = Modifier.fillMaxWidth()
                                )

                                Spacer(Modifier.height(10.dp))

                                Button(
                                    onClick = {
                                        viewModel.registerAndLoginStudent(
                                            name = studentName.ifBlank { "Campus Scholar" },
                                            phone = "+91 $phoneNumber",
                                            email = emailAddress.ifBlank { "student@campus.edu" },
                                            college = collegeName.ifBlank { "National Institute of Technology" },
                                            university = universityName.ifBlank { "State Technical University" },
                                            course = courseName.ifBlank { "B.Tech Computer Science" },
                                            studentId = studentId.ifBlank { "STU-2024-1001" },
                                            year = yearSelected,
                                            semester = semesterSelected,
                                            onSuccess = {
                                                onLoginSuccess()
                                            }
                                        )
                                    },
                                    shape = RoundedCornerShape(14.dp),
                                    colors = ButtonDefaults.buttonColors(containerColor = Indigo600),
                                    modifier = Modifier
                                        .fillMaxWidth()
                                        .height(50.dp)
                                        .testTag("complete_onboarding_button")
                                ) {
                                    Icon(Icons.Default.RocketLaunch, contentDescription = null, modifier = Modifier.size(18.dp))
                                    Spacer(Modifier.width(8.dp))
                                    Text("Enter Campus Dashboard", fontWeight = FontWeight.Bold)
                                }

                                TextButton(
                                    onClick = { currentStep = 0 }
                                ) {
                                    Text("← Change Phone Number", color = Slate600)
                                }
                            }
                        }
                    }
                }
            }
        }
    }
}
