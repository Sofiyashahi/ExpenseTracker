package com.sofiyashahi.expensetracker.database

import androidx.lifecycle.LiveData
import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Update
import com.sofiyashahi.expensetracker.model.Income
import kotlinx.coroutines.flow.Flow

@Dao
interface IncomeDao {
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertIncome(income: Income)

    @Update
    suspend fun updateIncome(income: Income)

    @Delete
    suspend fun deleteIncome(income: Income)

    @Query("SELECT * FROM income_table ORDER BY timestamp DESC")
    fun getAllIncomes(): Flow<List<Income>>

    @Query("SELECT SUM(amount) FROM income_table")
    fun getTotalIncome(): Flow<Double>
}