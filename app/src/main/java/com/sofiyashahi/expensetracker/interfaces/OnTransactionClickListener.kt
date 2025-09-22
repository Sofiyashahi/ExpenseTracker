package com.sofiyashahi.expensetracker.interfaces

import com.sofiyashahi.expensetracker.model.Expense
import com.sofiyashahi.expensetracker.model.Income

interface OnTransactionClickListener {

    fun onExpenseDeleteClick(expense: Expense)

    fun onIncomeDeleteClick(income: Income)

    fun onExpenseEditClick(expense: Expense)

    fun onIncomeEditClick(income: Income)
}