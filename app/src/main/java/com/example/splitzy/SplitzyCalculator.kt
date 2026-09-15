package com.example.splitzy

import com.example.data.model.ExpenseEntity
import java.text.DecimalFormat
import kotlin.math.abs
import kotlin.math.min

data class BalanceSummary(
    val totalSpent: Double,
    val youOwe: Double,
    val youGet: Double,
    val netBalance: Double
)

data class DebtRelation(
    val debtor: String,
    val creditor: String,
    val amount: Double
)

data class CategorySpend(
    val category: String,
    val amount: Double,
    val percentage: Float
)

object SplitzyCalculator {
    private val df = DecimalFormat("#.##")


    fun parseParticipants(participantsJson: String): Map<String, Double> {
        val result = mutableMapOf<String, Double>()
        if (participantsJson.isBlank()) return result
        val parts = participantsJson.split(",")
        for (p in parts) {
            val tokens = p.split(":")
            if (tokens.size == 2) {
                val name = tokens[0].trim()
                val share = tokens[1].trim().toDoubleOrNull() ?: 0.0
                result[name] = share
            } else if (tokens.size == 1 && tokens[0].isNotBlank()) {
                result[tokens[0].trim()] = 0.0
            }
        }
        return result
    }


    fun computeEqualSplit(totalAmount: Double, participants: List<String>): String {
        if (participants.isEmpty()) return ""
        val sharePerPerson = totalAmount / participants.size
        return participants.joinToString(",") { name -> "$name:${String.format("%.2f", sharePerPerson)}" }
    }


    fun calculateUserBalances(expenses: List<ExpenseEntity>, currentUserName: String = "You"): BalanceSummary {
        var totalSpent = 0.0
        var netBalance = 0.0

        for (expense in expenses) {
            totalSpent += expense.amount
            val shares = parseParticipants(expense.participantsJson)

            val myShare = shares[currentUserName] ?: 0.0
            val didIPay = expense.paidBy.equals(currentUserName, ignoreCase = true)

            if (didIPay) {

                netBalance += (expense.amount - myShare)
            } else {

                netBalance -= myShare
            }
        }

        val youGet = if (netBalance > 0) netBalance else 0.0
        val youOwe = if (netBalance < 0) abs(netBalance) else 0.0

        return BalanceSummary(
            totalSpent = totalSpent,
            youOwe = youOwe,
            youGet = youGet,
            netBalance = netBalance
        )
    }


    fun computeGroupSettlements(expenses: List<ExpenseEntity>): List<DebtRelation> {
        val netBalances = mutableMapOf<String, Double>()

        for (expense in expenses) {
            val payer = expense.paidBy
            val shares = parseParticipants(expense.participantsJson)


            netBalances[payer] = (netBalances[payer] ?: 0.0) + expense.amount


            for ((consumer, share) in shares) {
                netBalances[consumer] = (netBalances[consumer] ?: 0.0) - share
            }
        }


        val debtors = mutableListOf<Pair<String, Double>>()
        val creditors = mutableListOf<Pair<String, Double>>()

        for ((person, balance) in netBalances) {
            val rounded = (balance * 100).toInt() / 100.0
            if (rounded < -0.01) {
                debtors.add(Pair(person, abs(rounded)))
            } else if (rounded > 0.01) {
                creditors.add(Pair(person, rounded))
            }
        }

        val settlements = mutableListOf<DebtRelation>()
        var dIndex = 0
        var cIndex = 0

        val mutDebtors = debtors.map { it.second }.toMutableList()
        val mutCreditors = creditors.map { it.second }.toMutableList()

        while (dIndex < debtors.size && cIndex < creditors.size) {
            val debtorName = debtors[dIndex].first
            val creditorName = creditors[cIndex].first

            val debtAmount = mutDebtors[dIndex]
            val creditAmount = mutCreditors[cIndex]

            val settleAmount = min(debtAmount, creditAmount)

            if (settleAmount > 0.01) {
                settlements.add(DebtRelation(debtorName, creditorName, settleAmount))
            }

            mutDebtors[dIndex] = debtAmount - settleAmount
            mutCreditors[cIndex] = creditAmount - settleAmount

            if (mutDebtors[dIndex] < 0.01) dIndex++
            if (mutCreditors[cIndex] < 0.01) cIndex++
        }

        return settlements
    }


    fun computeCategorySpends(expenses: List<ExpenseEntity>): List<CategorySpend> {
        val total = expenses.sumOf { it.amount }.coerceAtLeast(1.0)
        return expenses.groupBy { it.category }
            .map { (cat, list) ->
                val sum = list.sumOf { it.amount }
                CategorySpend(
                    category = cat,
                    amount = sum,
                    percentage = (sum / total).toFloat()
                )
            }
            .sortedByDescending { it.amount }
    }
}
