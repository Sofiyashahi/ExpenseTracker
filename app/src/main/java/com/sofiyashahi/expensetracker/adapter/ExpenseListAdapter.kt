package com.sofiyashahi.expensetracker.adapter

import android.view.LayoutInflater
import android.view.ViewGroup
import android.widget.PopupMenu
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.sofiyashahi.expensetracker.MyApplication
import com.sofiyashahi.expensetracker.R
import com.sofiyashahi.expensetracker.SharedPreferenceManager
import com.sofiyashahi.expensetracker.databinding.ExpenseItemBinding
import com.sofiyashahi.expensetracker.fragment.toFormattedDate
import com.sofiyashahi.expensetracker.interfaces.OnTransactionClickListener
import com.sofiyashahi.expensetracker.model.TransactionItem

class ExpenseListAdapter(private val transactionClickListener: OnTransactionClickListener) :
    ListAdapter<TransactionItem, ExpenseListAdapter.ViewHolder>(TransactionDiffCallback()) {

    inner class ViewHolder(val binding: ExpenseItemBinding) : RecyclerView.ViewHolder(binding.root)

    override fun onCreateViewHolder(
        parent: ViewGroup,
        viewType: Int
    ): ViewHolder {
        val binding = ExpenseItemBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return ViewHolder(binding)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {

        val item = getItem(position)
        val context = holder.itemView.context
        val currency = SharedPreferenceManager.getString("currency")

        //Popup menu edit
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

                    else -> false
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

//        val size = displayedTransactions.size
        val size = super.getItemCount()
        return if (size <= 8) size
        else {
            if (MyApplication.fragmentName == "Home") 8
            else size
        }
    }

}

// DiffUtil Callback
class TransactionDiffCallback : DiffUtil.ItemCallback<TransactionItem>() {
    override fun areItemsTheSame(oldItem: TransactionItem, newItem: TransactionItem): Boolean {
        return when {
            oldItem is TransactionItem.ExpenseItem && newItem is TransactionItem.ExpenseItem ->
                oldItem.expense.id == newItem.expense.id
            oldItem is TransactionItem.IncomeItem && newItem is TransactionItem.IncomeItem ->
                oldItem.income.id == newItem.income.id
            else -> false
        }
    }

    override fun areContentsTheSame(oldItem: TransactionItem, newItem: TransactionItem): Boolean {
        return oldItem == newItem
    }
}