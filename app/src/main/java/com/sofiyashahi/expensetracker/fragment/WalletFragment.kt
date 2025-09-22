package com.sofiyashahi.expensetracker.fragment

import android.content.Context
import android.graphics.Canvas
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.core.content.ContextCompat
import androidx.core.view.isVisible
import androidx.fragment.app.Fragment
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.flowWithLifecycle
import androidx.lifecycle.lifecycleScope
import androidx.lifecycle.repeatOnLifecycle
import androidx.recyclerview.widget.ItemTouchHelper
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.sofiyashahi.expensetracker.MyApplication
import com.sofiyashahi.expensetracker.R
import com.sofiyashahi.expensetracker.SharedPreferenceManager
import com.sofiyashahi.expensetracker.adapter.ExpenseListAdapter
import com.sofiyashahi.expensetracker.databinding.FragmentWalletBinding
import com.sofiyashahi.expensetracker.interfaces.OnTransactionClickListener
import com.sofiyashahi.expensetracker.model.Expense
import com.sofiyashahi.expensetracker.model.Income
import com.sofiyashahi.expensetracker.model.UiState
import com.sofiyashahi.expensetracker.utils.ManageTransaction
import com.sofiyashahi.expensetracker.utils.MonthYearPickerDialog
import com.sofiyashahi.expensetracker.viewmodel.ExpenseViewModel
import com.sofiyashahi.expensetracker.viewmodel.IncomeViewModel
import it.xabaras.android.recyclerview.swipedecorator.RecyclerViewSwipeDecorator
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch


class WalletFragment(private val context: Context, private val expenseViewModel: ExpenseViewModel, private val incomeViewModel: IncomeViewModel) : Fragment(),
    OnTransactionClickListener {

    private lateinit var binding: FragmentWalletBinding
    private lateinit var adapter: ExpenseListAdapter
    private var currency = ""
    private var totalExpense: Double? = 0.0
    private var totalIncome : Double? = null

    private var currentExpenses: List<Expense> = emptyList()
    private var currentIncomes: List<Income> = emptyList()

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        binding = FragmentWalletBinding.inflate(layoutInflater, container, false)

        MyApplication.fragmentName = "Wallet"

        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        setUpAdapter()
        setUpView()
        filterList()
        refreshList()

    }

    private fun setUpAdapter(){
        adapter = ExpenseListAdapter(this)
        binding.rvExpenseList.layoutManager = LinearLayoutManager(context)
        binding.rvExpenseList.adapter = adapter


        lifecycleScope.launch {
            repeatOnLifecycle(Lifecycle.State.STARTED) {
                expenseViewModel.expenseState.collect { state ->
                    when (state) {
                        is UiState.Empty -> {
                            binding.progressBar.isVisible = false
                            binding.tvNoTransaction.isVisible = true
                        }

                        is UiState.Error -> {
                            binding.progressBar.isVisible = false
                            Toast.makeText(context, state.message, Toast.LENGTH_LONG).show()
                        }

                        is UiState.Loading -> binding.progressBar.isVisible = true
                        is UiState.Success -> {
                            binding.tvNoTransaction.isVisible = false
                            binding.progressBar.isVisible = false
                            currentExpenses = state.data
                            val transactionList =
                                ManageTransaction.setTransaction(currentExpenses, currentIncomes)
                            adapter.submitList(transactionList)
                        }
                    }
                }
            }
        }

            lifecycleScope.launch {

                repeatOnLifecycle(Lifecycle.State.STARTED) {
                    incomeViewModel.incomeState.collect { state ->
                        when (state) {
                            is UiState.Empty -> {
                                binding.progressBar.isVisible = false
//                                binding.tvNoTransaction.isVisible = true
                            }

                            is UiState.Error -> {
                                binding.progressBar.isVisible = false
                                Toast.makeText(context, state.message, Toast.LENGTH_LONG).show()
                            }

                            is UiState.Loading -> binding.progressBar.isVisible = true
                            is UiState.Success -> {
//                                binding.tvNoTransaction.isVisible = false
                                binding.progressBar.isVisible = false
                                currentIncomes = state.data
                                val transactionList = ManageTransaction.setTransaction(
                                    currentExpenses,
                                    currentIncomes
                                )
                                adapter.submitList(transactionList)
                            }
                        }
                    }
                }
            }

        swipeToDelete()

    }

    private fun setUpView(){
        currency = SharedPreferenceManager.getString("currency")

        lifecycleScope.launch {
            repeatOnLifecycle(Lifecycle.State.STARTED) {
                incomeViewModel.totalIncome.collect {
                    totalIncome = it
                    Log.d("WalletFragment", "totalIncome: $totalIncome")
                    binding.tvTotalIncome.text = "$currency $it"
                }
            }
        }

        lifecycleScope.launch {
            repeatOnLifecycle(Lifecycle.State.STARTED) {
                expenseViewModel.totalExpenses.collect{
                    totalExpense = it
                    binding.tvTotalExpense.text = "$currency $it"
                }
            }
        }

        CoroutineScope(Dispatchers.Main).launch {
            delay(100)
            Log.d("WalletFragment", "setUpView: $totalExpense + $totalIncome")
            val savings = totalIncome?.minus(totalExpense!!) ?: 0.0
            binding.tvSaving.text = "$currency $savings"
        }

    }

    private fun filterList(){

        binding.btSortBy.setOnClickListener {
            MonthYearPickerDialog(requireContext()){month, year ->
                Log.d("FilterCheck", "In Picker: → month=$month, year=$year}")

                val filterList = ManageTransaction.filterByMonthYear(month, year)
                adapter.submitList(filterList as MutableList)
            }.show()
        }
    }

    private fun refreshList(){
        binding.btRefresh.setOnClickListener {
            val transactionList = ManageTransaction.setTransaction(currentExpenses, currentIncomes)
            adapter.submitList(transactionList)
        }
    }
    private fun swipeToDelete(){
        val itemTouchHelper = ItemTouchHelper(object : ItemTouchHelper.SimpleCallback(
            0, ItemTouchHelper.LEFT or ItemTouchHelper.RIGHT){
            override fun onMove(
                recyclerView: RecyclerView,
                viewHolder: RecyclerView.ViewHolder,
                target: RecyclerView.ViewHolder
            ): Boolean {
                return false
            }

            override fun onSwiped(viewHolder: RecyclerView.ViewHolder, direction: Int) {
                adapter.deleteItem(viewHolder.adapterPosition)
            }

            override fun onChildDraw(
                c: Canvas,
                recyclerView: RecyclerView,
                viewHolder: RecyclerView.ViewHolder,
                dX: Float,
                dY: Float,
                actionState: Int,
                isCurrentlyActive: Boolean
            ) {

                RecyclerViewSwipeDecorator.Builder(
                    c,
                    recyclerView,
                    viewHolder,
                    dX,
                    dY,
                    actionState,
                    isCurrentlyActive
                )
                    .addBackgroundColor(
                        ContextCompat.getColor(
                            requireContext(),
                            R.color.red
                        )
                    )
                    .addActionIcon(R.drawable.ic_delete)
                    .create()
                    .decorate()

                super.onChildDraw(
                    c,
                    recyclerView,
                    viewHolder,
                    dX,
                    dY,
                    actionState,
                    isCurrentlyActive
                )
            }

        })

        itemTouchHelper.attachToRecyclerView(binding.rvExpenseList)

    }

    override fun onExpenseDeleteClick(expense: Expense) {
        expenseViewModel.delete(expense)
    }

    override fun onIncomeDeleteClick(income: Income) {
        incomeViewModel.deleteIncome(income)
    }

    override fun onExpenseEditClick(expense: Expense) {

    }

    override fun onIncomeEditClick(income: Income) {

    }
}