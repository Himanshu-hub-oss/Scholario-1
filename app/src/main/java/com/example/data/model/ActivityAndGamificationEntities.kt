package com.example.data.model

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "activities")
data class ActivityEntity(
    @PrimaryKey(autoGenerate = true) val id: Long = 0,
    val iconName: String = "check_circle",
    val title: String,
    val description: String,
    val category: String,
    val xpEarned: Int = 10,
    val timeAgo: String = "Just now",
    val timestamp: Long = System.currentTimeMillis()
)

@Entity(tableName = "badges")
data class BadgeEntity(
    @PrimaryKey val id: String,
    val title: String,
    val description: String,
    val icon: String,
    val category: String,
    val isUnlocked: Boolean = false,
    val unlockedDate: String? = null,
    val xpValue: Int = 50
)

@Entity(tableName = "quiz_history")
data class QuizHistoryEntity(
    @PrimaryKey(autoGenerate = true) val id: Long = 0,
    val subject: String,
    val topic: String,
    val score: Int,
    val totalQuestions: Int,
    val percentage: Int,
    val difficulty: String,
    val xpEarned: Int,
    val date: String,
    val timestamp: Long = System.currentTimeMillis()
)

@Entity(tableName = "resumes")
data class ResumeEntity(
    @PrimaryKey val id: String = "default_resume",
    val fullName: String = "Alex Rivera",
    val email: String = "alex.rivera@campus.edu",
    val phone: String = "+1 (555) 234-5678",
    val location: String = "Stanford, CA",
    val summary: String = "Aspiring Software Engineer & AI Researcher with strong proficiency in Kotlin, Jetpack Compose, Python, and scalable distributed architectures. Passionate about building impactful mobile products and AI-assisted tooling.",
    val educationJson: String = "B.Tech in Computer Science & AI (GPA: 3.85/4.0) - Stanford Institute of Technology (2023-2027)",
    val skillsJson: String = "Kotlin, Jetpack Compose, Room SQLite, Python, PyTorch, Gemini API, Git, System Design, REST APIs",
    val projectsJson: String = "Scholario Super App: Multi-module student productivity suite featuring AI study tutor, Splitzy bill manager, and Graphify visualization engine.\nML Exam Risk Predictor: Random-Forest based academic risk forecasting microservice with 94% precision.",
    val experienceJson: String = "Undergraduate AI Research Assistant - Stanford AI Lab (Jan 2025 - Present): Built evaluation benchmarks for educational LLM fine-tuning pipelines.\nOpen Source Contributor - Kotlin Foundation (2024): Contributed optimizations to Compose Canvas UI rendering components.",
    val certificationsJson: String = "Google Associate Android Developer (AAD), AWS Certified Cloud Practitioner",
    val selectedTemplate: String = "MODERN",
    val lastUpdated: String = "Aug 2026"
)

data class LeaderboardUser(
    val rank: Int,
    val name: String,
    val college: String,
    val score: Int,
    val xp: Int,
    val level: Int,
    val category: String,
    val avatarLetter: String,
    val isCurrentUser: Boolean = false
)

data class CampusRewardItem(
    val id: String,
    val name: String,
    val description: String,
    val category: String,
    val tokenCost: Int,
    val emoji: String,
    val originalPriceTag: String,
    val stockLeft: Int,
    val isRedeemed: Boolean = false,
    val couponCode: String? = null
)

