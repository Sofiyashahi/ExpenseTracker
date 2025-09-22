package com.sofiyashahi.expensetracker.viewmodel

import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.sofiyashahi.expensetracker.model.Expense
import com.sofiyashahi.expensetracker.model.UiState
import com.sofiyashahi.expensetracker.repository.ExpenseRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.onStart
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch

class ExpenseViewModel(private val repository: ExpenseRepository): ViewModel() {

    private val _expenseState = MutableStateFlow<UiState<List<Expense>>>(UiState.Loading)
    val expenseState: StateFlow<UiState<List<Expense>>> = _expenseState

    private val _totalExpenses = MutableStateFlow(0.0)
    val totalExpenses: StateFlow<Double> = _totalExpenses

    init {
        fetchExpenses()
        fetchTotalExpense()
    }

    private fun fetchExpenses() {
        viewModelScope.launch {
            repository.getAllExpenses()
                .onStart {
                    _expenseState.value = UiState.Loading
                }
                .catch { e-> _expenseState.value = UiState.Error(e.toString()) }
                .collect { list ->
                    if(list.isEmpty()) {
                        _expenseState.value = UiState.Empty
                    } else {
                        _expenseState.value = UiState.Success(list)
                    }
                }
        }
    }
    private fun fetchTotalExpense(){
        viewModelScope.launch {
            repository.getTotalExpenses().collect { totalExpense ->
                if (totalExpense != null) {
                    _totalExpenses.value = totalExpense
                }
            }
        }
    }

    fun insert(expense: Expense) = viewModelScope.launch {
        try {
            repository.insert(expense)
        } catch (e: Exception) {
            _expenseState.value = UiState.Error("Failed to insert: ${e.message}")
        }
    }

    fun update(expense: Expense) = viewModelScope.launch {
        try {
            repository.update(expense)
            Log.d("ExpenseViewModel", "Expense updated: $expense")
        } catch (e: Exception){
            _expenseState.value = UiState.Error("Failed to update: ${e.message}")
        }
    }

    fun delete(expense: Expense) = viewModelScope.launch {
        try {
            repository.delete(expense)
        } catch (e: Exception) {
            _expenseState.value = UiState.Error("Failed to delete: ${e.message}")
        }
    }

    fun addBalance(balance: Int) {
        Log.d("ExpenseViewModel", "addBalance: $balance")
    }
}