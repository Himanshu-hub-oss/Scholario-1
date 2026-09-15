package com.example.data.model

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "expenses")
data class ExpenseEntity(
    @PrimaryKey(autoGenerate = true) val id: Long = 0,
    val title: String,
    val amount: Double,
    val category: String,
    val date: String,
    val description: String = "",
    val paidBy: String = "You",
    val groupId: String? = null,
    val groupName: String? = null,
    val splitType: String = "EQUAL",
    val participantsJson: String = "You",
    val isSettled: Boolean = false,
    val timestamp: Long = System.currentTimeMillis()
)

@Entity(tableName = "expense_groups")
data class ExpenseGroupEntity(
    @PrimaryKey val id: String,
    val name: String,
    val category: String,
    val membersJson: String,
    val iconName: String = "group",
    val totalExpense: Double = 0.0,
    val createdDate: String = "Aug 2026"
)

@Entity(tableName = "settlements")
data class SettlementEntity(
    @PrimaryKey(autoGenerate = true) val id: Long = 0,
    val payerName: String,
    val receiverName: String,
    val amount: Double,
    val groupName: String? = null,
    val note: String = "Settlement",
    val isCompleted: Boolean = false,
    val timestamp: Long = System.currentTimeMillis()
)
