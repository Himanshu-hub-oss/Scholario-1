package com.example.data.model

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "lost_and_found")
data class LostFoundEntity(
    @PrimaryKey(autoGenerate = true) val id: Long = 0,
    val type: String,
    val itemName: String,
    val category: String = "Personal Item",
    val description: String,
    val location: String,
    val date: String,
    val contactInfo: String = "Campus ID verified (Chat via App)",
    val isResolved: Boolean = false,
    val isReported: Boolean = false,
    val reportReason: String? = null,
    val timestamp: Long = System.currentTimeMillis()
)

@Entity(tableName = "marketplace_items")
data class MarketplaceEntity(
    @PrimaryKey(autoGenerate = true) val id: Long = 0,
    val title: String,
    val price: Double,
    val category: String,
    val condition: String,
    val location: String,
    val description: String,
    val sellerName: String,
    val sellerContact: String = "Scholario Safe Connect",
    val isAvailable: Boolean = true,
    val isReported: Boolean = false,
    val timestamp: Long = System.currentTimeMillis()
)

@Entity(tableName = "career_items")
data class CareerEntity(
    @PrimaryKey(autoGenerate = true) val id: Long = 0,
    val type: String,
    val title: String,
    val companyOrRole: String,
    val stipendOrSalary: String,
    val location: String = "Hybrid / Remote",
    val deadline: String = "Open",
    val tags: String,
    val description: String,
    val applicationLink: String = "https://campus-careers.internal/apply"
)

@Entity(tableName = "notifications")
data class NotificationItemEntity(
    @PrimaryKey(autoGenerate = true) val id: Long = 0,
    val title: String,
    val message: String,
    val category: String,
    val isRead: Boolean = false,
    val timeAgo: String = "Just now",
    val timestamp: Long = System.currentTimeMillis()
)

@Entity(tableName = "campus_grievances")
data class CampusGrievanceEntity(
    @PrimaryKey(autoGenerate = true) val id: Long = 0,
    val title: String,
    val category: String,
    val location: String,
    val description: String,
    val photoEmoji: String = "🚰",
    val photoTag: String = "Water Cooler Dirty & Filter Clogged",
    val reportedByName: String = "Alex Rivera",
    val reportedByRoll: String = "STU-2024-8842",
    val reportedByPhone: String = "+91 9876543210",
    val department: String = "Computer Science & Engineering",
    val priority: String = "HIGH",
    val status: String = "PENDING",
    val hodRemarks: String? = null,
    val resolvedByHodName: String? = null,
    val resolvedDate: String? = null,
    val timestamp: Long = System.currentTimeMillis()
)

data class SecurityThreatIncident(
    val id: String,
    val threatType: String,
    val severity: String,
    val attackerIp: String,
    val attackerLocation: String,
    val deviceFingerprint: String,
    val attackVector: String,
    val timeAgo: String,
    val status: String,
    val isBlocked: Boolean = true
)
