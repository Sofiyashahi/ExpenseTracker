package com.sofiyashahi.expensetracker.fragment

import android.app.Dialog
import android.content.Context
import android.graphics.Canvas
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.core.content.ContextCompat
import androidx.core.os.bundleOf
import androidx.core.view.isVisible
import androidx.fragment.app.Fragment
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.lifecycleScope
import androidx.lifecycle.repeatOnLifecycle
import androidx.recyclerview.widget.ItemTouchHelper
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.sofiyashahi.expensetracker.MyApplication
import com.sofiyashahi.expensetracker.R
import com.sofiyashahi.expensetracker.SharedPreferenceManager
import com.sofiyashahi.expensetracker.adapter.ExpenseListAdapter
import com.sofiyashahi.expensetracker.databinding.AddBalanceDialogBinding
import com.sofiyashahi.expensetracker.databinding.FragmentHomeBinding
import com.sofiyashahi.expensetracker.interfaces.OnTransactionClickListener
import com.sofiyashahi.expensetracker.interfaces.OnOpenWalletFragment
import com.sofiyashahi.expensetracker.model.Expense
import com.sofiyashahi.expensetracker.model.Income
import com.sofiyashahi.expensetracker.model.UiState
import com.sofiyashahi.expensetracker.utils.ManageTransaction
import com.sofiyashahi.expensetracker.viewmodel.ExpenseViewModel
import com.sofiyashahi.expensetracker.viewmodel.IncomeViewModel
import it.xabaras.android.recyclerview.swipedecorator.RecyclerViewSwipeDecorator
import kotlinx.coroutines.launch
import java.io.Serializable


class HomeFragment(private val context: Context, private val expenseViewModel: ExpenseViewModel,
                   private val incomeViewModel: IncomeViewModel, private val openWalletListener: OnOpenWalletFragment) :
    Fragment(), OnTransactionClickListener {

    private lateinit var binding: FragmentHomeBinding
    private lateinit var adapter: ExpenseListAdapter
    private var expenseFragment: ExpenseFragment? = null

    private var name = ""
    private var currency = ""
    private var currentExpenses: List<Expense> = emptyList()
    private var currentIncomes: List<Income> = emptyList()

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        binding = FragmentHomeBinding.inflate(layoutInflater, container, false)

        MyApplication.fragmentName = "Home"

        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        Log.d("HomeFragment", "onViewCreated: called")
        initView()
    }

    private fun initView(){

        expenseFragment = ExpenseFragment(expenseViewModel, incomeViewModel)

        name = SharedPreferenceManager.getString("name")
        currency = SharedPreferenceManager.getString("currency")

        binding.tvUserName.text = "Hello, $name"

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
                            Log.d("HomeFragment", "onViewCreated: ${currentExpenses.toString()}")
                            val transactionList =
                                ManageTransaction.setTransaction(currentExpenses, currentIncomes)
                            adapter.submitList(transactionList)
                        }
                    }
                }
            }
        }

        lifecycleScope.launch {
            repeatOnLifecycle(Lifecycle.State.STARTED){
                incomeViewModel.incomeState.collect {state->
                    when(state) {
                        is UiState.Empty -> {
                            binding.progressBar.isVisible = false
                            binding.tvNoTransaction.isVisible = true
                        }
                        is UiState.Error -> {
                            binding.progressBar.isVisible = false
                            Toast.makeText(context, state.message, Toast.LENGTH_LONG).show()
                        }
                        is UiState.Loading -> {
                            binding.progressBar.isVisible = true
                            binding.tvNoTransaction.isVisible = false
                        }
                        is UiState.Success -> {
                            binding.tvNoTransaction.isVisible = false
                            binding.progressBar.isVisible = false
                            currentIncomes = state.data
                            val transactionList = ManageTransaction.setTransaction(currentExpenses, currentIncomes)
                            adapter.submitList(transactionList)
                        }
                    }
                }
            }
        }


        addExpense()
        swipeToDelete()
        viewAll()
    }


    private fun addExpense(){

        binding.fabAdd.setOnClickListener{
            openFragment(expenseFragment!!)
        }
    }

    private fun openFragment(fragment: Fragment){
        binding.innerFragmentContainer.removeAllViews()
        val fragmentManager = activity?.supportFragmentManager
        val fragmentTransaction = fragmentManager?.beginTransaction()
        fragmentTransaction?.replace(R.id.inner_fragment_container, fragment)
        fragmentTransaction?.addToBackStack(null)
        fragmentTransaction?.commit()
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
                adapter?.deleteItem(viewHolder.adapterPosition)

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


    private fun viewAll(){
        binding.tvViewAll.setOnClickListener {
            openWalletListener.onOpen()
        }
    }

    override fun onExpenseDeleteClick(expense: Expense) {
        expenseViewModel.delete(expense)
    }

    override fun onIncomeDeleteClick(income: Income) {
        incomeViewModel.deleteIncome(income)
    }

    override fun onExpenseEditClick(expense: Expense) {
        val bundle = Bundle()
        bundle.putParcelable("expense", expense)
        expenseFragment?.arguments = bundle
        openFragment(expenseFragment!!)
    }

    override fun onIncomeEditClick(income: Income) {
        val bundle = Bundle()
        bundle.putParcelable("income", income)
        expenseFragment?.arguments = bundle
        openFragment(expenseFragment!!)
    }


}