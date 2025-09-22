package com.sofiyashahi.expensetracker.repository

import android.util.Log
import androidx.lifecycle.LiveData
import com.sofiyashahi.expensetracker.database.ExpenseDao
import com.sofiyashahi.expensetracker.model.Expense
import kotlinx.coroutines.flow.Flow

class ExpenseRepository(private val expenseDao: ExpenseDao) {

    fun getAllExpenses(): Flow<List<Expense>> = expenseDao.getAllExpenses()
    fun getTotalExpenses(): Flow<Double?> = expenseDao.getTotalExpenses()

    suspend fun insert(expense: Expense) {
        expenseDao.insert(expense)
    }

    suspend fun update(expense: Expense) {
        Log.d("ExpenseRepository", "Expense updated: $expense")
        expenseDao.update(expense)
    }

    suspend fun delete(expense: Expense) {
        expenseDao.delete(expense)
    }

}