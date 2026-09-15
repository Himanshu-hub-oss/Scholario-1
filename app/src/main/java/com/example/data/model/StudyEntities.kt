package com.example.data.model

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "study_tasks")
data class StudyTaskEntity(
    @PrimaryKey(autoGenerate = true) val id: Long = 0,
    val title: String,
    val subject: String,
    val academicYear: String = "3rd Year",
    val dueDate: String,
    val priority: String = "Medium",
    val isCompleted: Boolean = false,
    val estimatedMinutes: Int = 45,
    val timestamp: Long = System.currentTimeMillis()
)

@Entity(tableName = "exams")
data class ExamEntity(
    @PrimaryKey(autoGenerate = true) val id: Long = 0,
    val subjectCode: String,
    val subjectName: String,
    val academicYear: String = "3rd Year",
    val semester: String = "Semester 6",
    val date: String,
    val time: String,
    val venue: String,
    val syllabus: String,
    val daysRemaining: Int = 7,
    val isCompleted: Boolean = false
)

@Entity(tableName = "subject_notes")
data class SubjectNoteEntity(
    @PrimaryKey(autoGenerate = true) val id: Long = 0,
    val subject: String,
    val title: String,
    val chapter: String,
    val academicYear: String = "3rd Year",
    val semester: String = "Semester 5 & 6",
    val fileSize: String = "2.4 MB",
    val fileType: String = "PDF",
    val summary: String,
    val tags: String = "PYQ, Formula Sheet, Important",
    val downloadUrl: String = "",
    val downloadsCount: Int = 142,
    val isBookmarked: Boolean = false
)

@Entity(tableName = "assignments")
data class AssignmentEntity(
    @PrimaryKey(autoGenerate = true) val id: Long = 0,
    val title: String,
    val subject: String,
    val deadline: String,
    val status: String = "PENDING",
    val maxMarks: Int = 100,
    val score: Int? = null,
    val instructions: String = "",
    val daysLeft: Int = 3
)

@Entity(tableName = "quiz_questions")
data class QuizQuestionEntity(
    @PrimaryKey(autoGenerate = true) val id: Long = 0,
    val subject: String,
    val topic: String,
    val difficulty: String = "Medium",
    val question: String,
    val optionA: String,
    val optionB: String,
    val optionC: String,
    val optionD: String,
    val correctIndex: Int,
    val explanation: String
)
