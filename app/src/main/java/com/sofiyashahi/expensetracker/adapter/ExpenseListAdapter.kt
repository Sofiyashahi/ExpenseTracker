package com.sofiyashahi.expensetracker.adapter

import android.content.Context
import android.icu.text.DecimalFormat
import android.util.Log
import android.view.LayoutInflater
import android.view.Menu
import android.view.ViewGroup
import android.widget.PopupMenu
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.sofiyashahi.expensetracker.MyApplication
import com.sofiyashahi.expensetracker.R
import com.sofiyashahi.expensetracker.SharedPreferenceManager
import com.sofiyashahi.expensetracker.databinding.ExpenseItemBinding
import com.sofiyashahi.expensetracker.fragment.toFormattedDate
import com.sofiyashahi.expensetracker.interfaces.OnTransactionClickListener
import com.sofiyashahi.expensetracker.model.Expense
import com.sofiyashahi.expensetracker.model.Income
import com.sofiyashahi.expensetracker.model.TransactionItem
import java.util.Calendar

class ExpenseListAdapter(private val transactionClickListener: OnTransactionClickListener) :
    RecyclerView.Adapter<ExpenseListAdapter.ViewHolder>() {

    class ViewHolder(val binding: ExpenseItemBinding) : RecyclerView.ViewHolder(binding.root)

    lateinit var context: Context
    var formatter: DecimalFormat? = null

    private val displayedTransactions = mutableListOf<TransactionItem>()

    fun submitList(transactions: MutableList<TransactionItem>) {
        displayedTransactions.clear()
        displayedTransactions.addAll(transactions)
        notifyDataSetChanged()
    }

    fun deleteItem(position: Int) {
        if (position < displayedTransactions.size) {
            val item = displayedTransactions[position]  // Get the item at position

            when (item) {
                is TransactionItem.ExpenseItem -> {
                    transactionClickListener.onExpenseDeleteClick(item.expense)  // Notify click listener
                }

                is TransactionItem.IncomeItem -> {
                    transactionClickListener.onIncomeDeleteClick(item.income)  // Notify click listener
                }
            }

            displayedTransactions.removeAt(position)  // Remove from local list
            notifyItemRemoved(position)  // Notify RecyclerView
        }
    }

    override fun onCreateViewHolder(
        parent: ViewGroup,
        viewType: Int
    ): ViewHolder {
        context = parent.context
        formatter = DecimalFormat("###,###,###.##")
        val binding = ExpenseItemBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return ViewHolder(binding)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {

        val item = displayedTransactions[position]
        val currency = SharedPreferenceManager.getString("currency")
        //for context menu edit
        holder.binding.root.setOnLongClickListener { view ->
            val popup = PopupMenu(context, view)
            popup.menuInflater.inflate(R.menu.expense_edit_menu, popup.menu)

            popup.setOnMenuItemClickListener { menuItem ->
                when (menuItem.itemId) {
                    R.id.menu_edit -> {
                        if (item is TransactionItem.ExpenseItem) {
                            transactionClickListener.onExpenseEditClick(item.expense)
                        } else if (item is TransactionItem.IncomeItem) {
                            transactionClickListener.onIncomeEditClick(item.income)
                        }
                        true
                    }

                    else -> {
                        Log.d("ExpenseList", "onBindViewHolder: Item not valid")
                        false
                    }
                }
            }

            popup.show()
            true
        }

        when (item) {
            is TransactionItem.ExpenseItem -> {
                val expense = item.expense
                holder.binding.tvTitle.text = expense.title
                holder.binding.tvTime.text = expense.timestamp.toFormattedDate()
                holder.binding.tvPrice.text = "- $currency${expense.amount}"
                holder.binding.tvPrice.setTextColor(
                    ContextCompat.getColor(
                        context,
                        R.color.red
                    )
                )

                Glide.with(context)
                    .load(expense.pic)
                    .into(holder.binding.img)

            }

            is TransactionItem.IncomeItem -> {
                val income = item.income
                holder.binding.tvTitle.text = income.title
                holder.binding.tvTime.text = income.timestamp.toFormattedDate()
                holder.binding.tvPrice.text = "+ $currency${income.amount}"
                holder.binding.tvPrice.setTextColor(
                    ContextCompat.getColor(
                        context,
                        R.color.green
                    )
                )

                Glide.with(context)
                    .load(R.drawable.money)
                    .into(holder.binding.img)
            }

        }

    }

    override fun getItemCount(): Int {

        val size = displayedTransactions.size
        return if (size <= 8) size
        else {
            if (MyApplication.fragmentName == "Home") 8
            else size
        }
    }

}