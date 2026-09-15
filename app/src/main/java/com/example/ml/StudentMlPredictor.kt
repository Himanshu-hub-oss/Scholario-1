package com.example.ml

import com.example.data.model.AssignmentEntity
import com.example.data.model.ExpenseEntity
import com.example.data.model.QuizQuestionEntity
import com.example.data.model.StudyTaskEntity
import com.example.data.model.UserEntity

enum class AcademicRiskLevel {
    LOW_RISK,
    MEDIUM_RISK,
    HIGH_RISK
}

data class AcademicPerformancePrediction(
    val riskLevel: AcademicRiskLevel,
    val readinessScorePercent: Int,
    val predictedGpaRange: String,
    val strengths: List<String>,
    val riskFactors: List<String>,
    val actionableGuidance: String
)

data class ExpenseForecast(
    val predictedMonthTotal: Double,
    val dailyBurnRate: Double,
    val budgetExceedRiskPercent: Int,
    val highestCategory: String,
    val projectedSavings: Double
)


interface PerformancePredictorEngine {
    fun predictAcademicRisk(
        user: UserEntity,
        tasks: List<StudyTaskEntity>,
        assignments: List<AssignmentEntity>
    ): AcademicPerformancePrediction

    fun forecastExpenses(
        expenses: List<ExpenseEntity>,
        monthlyBudget: Double
    ): ExpenseForecast
}

object StudentMlPredictor : PerformancePredictorEngine {

    override fun predictAcademicRisk(
        user: UserEntity,
        tasks: List<StudyTaskEntity>,
        assignments: List<AssignmentEntity>
    ): AcademicPerformancePrediction {
        val attendance = user.attendancePercent
        val currentGpa = user.gpa

        val totalTasks = tasks.size.coerceAtLeast(1)
        val completedTasks = tasks.count { it.isCompleted }
        val taskCompletionRate = (completedTasks.toDouble() / totalTasks) * 100

        val submittedAssignments = assignments.count { it.status != "PENDING" }
        val assignmentRate = (submittedAssignments.toDouble() / assignments.size.coerceAtLeast(1)) * 100



        val gpaNorm = (currentGpa / 4.0) * 100
        val compositeScore = (attendance * 0.30) + (taskCompletionRate * 0.25) + (assignmentRate * 0.25) + (gpaNorm * 0.20)

        val riskLevel = when {
            compositeScore >= 78.0 -> AcademicRiskLevel.LOW_RISK
            compositeScore >= 60.0 -> AcademicRiskLevel.MEDIUM_RISK
            else -> AcademicRiskLevel.HIGH_RISK
        }

        val predictedGpa = when (riskLevel) {
            AcademicRiskLevel.LOW_RISK -> "3.75 - 3.95"
            AcademicRiskLevel.MEDIUM_RISK -> "3.20 - 3.65"
            AcademicRiskLevel.HIGH_RISK -> "2.60 - 3.10"
        }

        val strengths = mutableListOf<String>()
        val riskFactors = mutableListOf<String>()

        if (attendance >= 85) strengths.add("Solid classroom attendance ($attendance%)")
        else riskFactors.add("Attendance near cutoff ($attendance%)")

        if (taskCompletionRate >= 60) strengths.add("Active study schedule follow-through")
        else riskFactors.add("Multiple pending study tasks")

        if (assignmentRate >= 60) strengths.add("On-time lab and project submissions")
        else riskFactors.add("Pending lab assignments due soon")

        val guidance = when (riskLevel) {
            AcademicRiskLevel.LOW_RISK -> "Exemplary momentum! Focus on mock test speed and active revision for upcoming DBMS & Algorithms finals."
            AcademicRiskLevel.MEDIUM_RISK -> "Maintain consistency: Complete remaining pending assignments and dedicate 45 mins daily to high-priority topics."
            AcademicRiskLevel.HIGH_RISK -> "Urgent intervention recommended: Attend remedial doubt sessions and use Campus AI 7-day study planner."
        }

        return AcademicPerformancePrediction(
            riskLevel = riskLevel,
            readinessScorePercent = compositeScore.toInt().coerceIn(10, 99),
            predictedGpaRange = predictedGpa,
            strengths = strengths,
            riskFactors = riskFactors,
            actionableGuidance = guidance
        )
    }

    override fun forecastExpenses(
        expenses: List<ExpenseEntity>,
        monthlyBudget: Double
    ): ExpenseForecast {
        val totalSpent = expenses.sumOf { it.amount }

        val avgDaily = (totalSpent / 25.0).coerceAtLeast(50.0)
        val projectedTotal = totalSpent + (avgDaily * 6.0)

        val budgetRisk = if (projectedTotal > monthlyBudget) {
            (((projectedTotal - monthlyBudget) / monthlyBudget) * 100).toInt().coerceIn(1, 100)
        } else {
            0
        }

        val topCategory = expenses.groupBy { it.category }
            .maxByOrNull { entry -> entry.value.sumOf { it.amount } }?.key ?: "Food"

        val savingsOpportunity = (expenses.filter { it.category in listOf("Entertainment", "Shopping", "Food") }
            .sumOf { it.amount } * 0.15)

        return ExpenseForecast(
            predictedMonthTotal = projectedTotal,
            dailyBurnRate = avgDaily,
            budgetExceedRiskPercent = budgetRisk,
            highestCategory = topCategory,
            projectedSavings = savingsOpportunity
        )
    }
}
