package com.sofiyashahi.expensetracker

import android.content.Context
import android.content.SharedPreferences

object SharedPreferenceManager {

    private const val PREF_NAME = "BASIC_PREF"
    private lateinit var sharedPreferences: SharedPreferences

    // Initialize the SharedPreferences instance
    fun init(context: Context) {
        sharedPreferences = context.getSharedPreferences(PREF_NAME, Context.MODE_PRIVATE)
    }

    // Save a string value
    fun putString(key: String, value: String) {
        sharedPreferences.edit().putString(key, value).apply()
    }

    // Retrieve a string value
    fun getString(key: String, defaultValue: String = ""): String {
        return sharedPreferences.getString(key, defaultValue) ?: defaultValue
    }

    // Save an integer value
    fun putInt(key: String, value: Int) {
        sharedPreferences.edit().putInt(key, value).apply()
    }

    // Retrieve an integer value
    fun getInt(key: String, defaultValue: Int = 0): Int {
        return sharedPreferences.getInt(key, defaultValue)
    }

    fun putBoolean(key: String, value: Boolean) {
        sharedPreferences.edit().putBoolean(key, value).apply()
    }

    fun getBoolean(key: String, defaultValue: Boolean = false): Boolean {
        return sharedPreferences.getBoolean(key, defaultValue)
    }

    // Clear all preferences
    fun clear() {
        sharedPreferences.edit().clear().apply()
    }

    // Remove a specific key
    fun remove(key: String) {
        sharedPreferences.edit().remove(key).apply()
    }
}