package com.sofiyashahi.expensetracker.repository

import androidx.lifecycle.LiveData
import com.sofiyashahi.expensetracker.database.IncomeDao
import com.sofiyashahi.expensetracker.model.Income
import kotlinx.coroutines.flow.Flow

class IncomeRepository(private val incomeDao: IncomeDao) {

    fun getAllIncome(): Flow<List<Income>> = incomeDao.getAllIncomes()
    fun getTotalIncome(): Flow<Double?> = incomeDao.getTotalIncome()

    suspend fun insertIncome(income: Income) {
        incomeDao.insertIncome(income)
    }

    suspend fun updateIncome(income: Income){
        incomeDao.updateIncome(income)
    }

    suspend fun deleteIncome(income: Income) {
        incomeDao.deleteIncome(income)
    }
}