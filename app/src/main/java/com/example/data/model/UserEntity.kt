package com.example.data.model

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "users")
data class UserEntity(
    @PrimaryKey val id: String = "user_default",
    val name: String = "Alex Rivera",
    val email: String = "alex.rivera@campus.edu",
    val phone: String = "+91 9876543210",
    val college: String = "Stanford Institute of Technology",
    val university: String = "Stanford University",
    val studentId: String = "STU-2024-8842",
    val course: String = "B.Tech Computer Science & AI",
    val year: String = "3rd Year (Junior)",
    val semester: String = "Semester 6",
    val gpa: Double = 3.85,
    val attendancePercent: Int = 92,
    val skills: String = "Kotlin, Jetpack Compose, Python, Machine Learning, UI/UX, System Design",
    val avatarRes: String = "avatar_alex",
    val role: String = "Student",
    val monthlyBudget: Double = 8000.0,
    val joinedDate: String = "Aug 2024",
    val isLoggedIn: Boolean = true
)
