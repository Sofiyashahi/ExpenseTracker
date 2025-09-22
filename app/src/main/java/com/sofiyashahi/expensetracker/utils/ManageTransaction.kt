package com.sofiyashahi.expensetracker.utils

import android.util.Log
import com.sofiyashahi.expensetracker.model.Expense
import com.sofiyashahi.expensetracker.model.Income
import com.sofiyashahi.expensetracker.model.TransactionItem
import java.util.Calendar

class ManageTransaction {

    companion object {
        private val transactions = mutableListOf<TransactionItem>()

        fun setTransaction(newExpense: List<Expense>, newIncome: List<Income>): MutableList<TransactionItem> {
            transactions.clear()

            val combinedList = mutableListOf<TransactionItem>()
            combinedList.addAll(newExpense.map { TransactionItem.ExpenseItem(it) })
            combinedList.addAll(newIncome.map { TransactionItem.IncomeItem(it) })

            transactions.addAll(combinedList.sortedByDescending { it.timestamp })

            return transactions
        }

        fun filterByMonthYear(month: Int, year: Int): List<TransactionItem> {
            val calendar = Calendar.getInstance()

            val filteredList = transactions.filter { item ->
                val itemTimestamp = item.timestamp
                calendar.timeInMillis = itemTimestamp
                val itemMonth = calendar.get(Calendar.MONTH) + 1   // Calendar.MONTH is 0-based
                val itemYear = calendar.get(Calendar.YEAR)
                Log.d("FilterCheck", "Item: → month=$itemMonth, year=$itemYear}")

                itemMonth == month && itemYear == year
            }

            return filteredList
        }
    }

}