package com.sofiyashahi.expensetracker.database

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import com.sofiyashahi.expensetracker.model.Income

@Database(entities = [Income::class], version = 3, exportSchema = false)
abstract class IncomeDatabase: RoomDatabase() {

    abstract fun incomeDao(): IncomeDao

    companion object {
        @Volatile
        private var INSTANCE: IncomeDatabase? = null

        fun getIncomeDB(context: Context): IncomeDatabase {
            return INSTANCE ?: synchronized(this) {
                val instance = Room.databaseBuilder(
                    context.applicationContext,
                    IncomeDatabase::class.java,
                    "income_database"
                ).fallbackToDestructiveMigration().build()
                INSTANCE = instance
                instance
            }
        }
    }
}