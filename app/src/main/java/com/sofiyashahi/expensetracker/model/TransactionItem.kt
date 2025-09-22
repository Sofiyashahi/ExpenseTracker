package com.sofiyashahi.expensetracker.model

sealed class TransactionItem {
    abstract val timestamp: Long
    data class ExpenseItem(val expense: Expense): TransactionItem() {
        override val timestamp: Long = expense.timestamp
    }
    data class IncomeItem(val income: Income): TransactionItem() {
        override val timestamp: Long = income.timestamp
    }
}