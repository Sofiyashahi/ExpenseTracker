package com.sofiyashahi.expensetracker.utils

import android.app.AlertDialog
import android.content.Context
import androidx.core.content.ContentProviderCompat.requireContext
import com.sofiyashahi.expensetracker.adapter.ExpenseListAdapter
import com.sofiyashahi.expensetracker.model.TransactionItem
import com.sofiyashahi.expensetracker.viewmodel.ExpenseViewModel
import com.sofiyashahi.expensetracker.viewmodel.IncomeViewModel

class DeleteTransactionDialog {
    companion object {
        fun showDeleteConfirmationDialog(
            item: TransactionItem,
            position : Int,
            context: Context,
            adapter: ExpenseListAdapter,
            expenseViewModel: ExpenseViewModel,
            incomeViewModel: IncomeViewModel
        ){
            AlertDialog.Builder(context)
                .setTitle("Delete Transaction")
                .setMessage("Are you sure you want to delete this transaction?")
                .setPositiveButton("Delete"){ _, _ ->
                    // Optimistically update UI
                    val mutableList = adapter.currentList.toMutableList()
                    mutableList.removeAt(position)
                    adapter.submitList(mutableList)

                    when (item) {
                        is TransactionItem.ExpenseItem -> expenseViewModel.delete(item.expense)
                        is TransactionItem.IncomeItem -> incomeViewModel.deleteIncome(item.income)
                    }
                }
                .setNegativeButton("Cancel") {_, _, ->
                    adapter.notifyItemChanged(position)
                }
                .setCancelable(false)
                .show()
        }
    }
}