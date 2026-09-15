package com.example.data.model

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "community_posts")
data class CommunityPostEntity(
    @PrimaryKey(autoGenerate = true) val id: Long = 0,
    val authorName: String,
    val authorRole: String = "Student",
    val title: String,
    val content: String,
    val groupTag: String,
    val likesCount: Int = 12,
    val commentsCount: Int = 4,
    val isLikedByMe: Boolean = false,
    val isReported: Boolean = false,
    val reportReason: String? = null,
    val timeAgo: String = "2 hours ago",
    val timestamp: Long = System.currentTimeMillis()
)

@Entity(tableName = "community_groups")
data class CommunityGroupEntity(
    @PrimaryKey val id: String,
    val name: String,
    val category: String,
    val memberCount: Int,
    val description: String,
    val isJoined: Boolean = false
)

@Entity(tableName = "campus_events")
data class CampusEventEntity(
    @PrimaryKey(autoGenerate = true) val id: Long = 0,
    val title: String,
    val clubName: String,
    val date: String,
    val time: String,
    val location: String,
    val category: String = "Technical",
    val attendeesCount: Int = 85,
    val isRegistered: Boolean = false,
    val description: String = ""
)
