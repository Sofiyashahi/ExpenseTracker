package com.sofiyashahi.expensetracker.viewmodel

import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.sofiyashahi.expensetracker.model.Income
import com.sofiyashahi.expensetracker.model.UiState
import com.sofiyashahi.expensetracker.repository.IncomeRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.onStart
import kotlinx.coroutines.launch

class IncomeViewModel(private val repository: IncomeRepository): ViewModel() {

    private val _incomeState = MutableStateFlow<UiState<List<Income>>>(UiState.Loading)
    val incomeState: StateFlow<UiState<List<Income>>> = _incomeState

    private val _totalIncome = MutableStateFlow(0.0)
    val totalIncome: StateFlow<Double> = _totalIncome

    init {
        fetchIncomes()
        fetchTotalIncome()
    }

    private fun fetchIncomes(){
        viewModelScope.launch {
            repository.getAllIncome()
                .onStart {
                    _incomeState.value = UiState.Loading
                }
                .catch { e-> _incomeState.value = UiState.Error(e.toString())  }
                .collect {list->
                    if (list.isEmpty()) {
                        _incomeState.value = UiState.Empty
                    } else {
                        _incomeState.value = UiState.Success(list)
                    }
                }


        }
    }

    private fun fetchTotalIncome() {
        viewModelScope.launch {
            repository.getTotalIncome().collect { totalIncome ->
                if(totalIncome != null) {
                    _totalIncome.value = totalIncome
                }
            }
        }
    }

    fun insertIncome(income: Income)  = viewModelScope.launch {
        try {
            repository.insertIncome(income)
        } catch (e: Exception) {
            _incomeState.value = UiState.Error("Failed to insert: ${e.message}")
        }
    }

    fun updateIncome(income: Income) = viewModelScope.launch {
        try {
            repository.updateIncome(income)
        } catch (e: Exception) {
            _incomeState.value = UiState.Error("Failed to update: ${e.message}")
        }
    }

    fun deleteIncome(income: Income) = viewModelScope.launch {
        try {
            repository.deleteIncome(income)
        } catch (e: Exception) {
            _incomeState.value = UiState.Error("Failed to delete: ${e.message}")
        }
    }


//    val allIncomes: StateFlow<List<Income>> = repository.getAllIncome().stateIn(viewModelScope, SharingStarted.Lazily, emptyList())
//    val totalIncome: StateFlow<Double> = repository.getTotalIncome().stateIn(viewModelScope, SharingStarted.Lazily, 0.0)
//
//    fun insertIncome(income: Income)  = viewModelScope.launch {
//        repository.insertIncome(income)
//    }
//
//    fun deleteIncome(income: Income)  = viewModelScope.launch {
//        repository.deleteIncome(income)
//    }
}