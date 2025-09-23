package com.sofiyashahi.expensetracker

import android.app.Application

class MyApplication : Application() {

    override fun onCreate() {
        super.onCreate()
        SharedPreferenceManager.init(this)
    }

    companion object {
        var fragmentName = ""
        var isExpense = true
        var editExpense = true
    }
}