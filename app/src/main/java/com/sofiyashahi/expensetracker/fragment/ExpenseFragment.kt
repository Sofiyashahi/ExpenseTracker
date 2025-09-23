package com.sofiyashahi.expensetracker.fragment

import android.os.Bundle
import android.text.Editable
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.AdapterView
import android.widget.LinearLayout
import android.widget.Toast
import androidx.fragment.app.DialogFragment
import androidx.recyclerview.widget.ItemTouchHelper
import androidx.recyclerview.widget.RecyclerView
import com.google.android.material.datepicker.MaterialDatePicker
import com.google.android.material.floatingactionbutton.FloatingActionButton
import com.sofiyashahi.expensetracker.MyApplication
import com.sofiyashahi.expensetracker.R
import com.sofiyashahi.expensetracker.adapter.CustomSpinnerAdapter
import com.sofiyashahi.expensetracker.databinding.FragmentExpenseBinding
import com.sofiyashahi.expensetracker.model.CustomSpinnerModel
import com.sofiyashahi.expensetracker.model.Expense
import com.sofiyashahi.expensetracker.model.Income
import com.sofiyashahi.expensetracker.utils.ManageTransaction
import com.sofiyashahi.expensetracker.viewmodel.ExpenseViewModel
import com.sofiyashahi.expensetracker.viewmodel.IncomeViewModel
import java.text.SimpleDateFormat
import java.util.Calendar
import java.util.Date
import java.util.Locale
import java.util.TimeZone

class ExpenseFragment(private val viewModel: ExpenseViewModel, private val incomeViewModel: IncomeViewModel) : Fragment() {

    private lateinit var binding: FragmentExpenseBinding
    private var customSpinnerList: ArrayList<CustomSpinnerModel>? = null
    private lateinit var customSpinnerAdapter: CustomSpinnerAdapter
    private var categoryPic: Int = 0
    private var selectedDateMillis: Long = System.currentTimeMillis()


    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        binding = FragmentExpenseBinding.inflate(layoutInflater, container, false)

        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        val fab = requireActivity().findViewById<FloatingActionButton>(R.id.fab_add)
        fab.hide()

        initView()
        setSpinnerAdapter()

        arguments?.let { bundle ->
            if(MyApplication.editExpense) {
                bundle.getParcelable<Expense>("expense")?.let { expense ->
                    editExpense(expense)
                }
            } else {
                bundle.getParcelable<Income>("income")?.let { income ->
                    editIncome(income)
                }
            }
        }

    }

    private fun setSpinnerAdapter(){
        customSpinnerList = ArrayList()
        customSpinnerList?.add(CustomSpinnerModel("Groceries", R.drawable.ic_grocery))
        customSpinnerList?.add(CustomSpinnerModel("Shopping", R.drawable.ic_clothes))
        customSpinnerList?.add(CustomSpinnerModel("Food", R.drawable.ic_fastfood))
        customSpinnerList?.add(CustomSpinnerModel("Transportation", R.drawable.ic_transportation))
        customSpinnerList?.add(CustomSpinnerModel("Investment", R.drawable.ic_investment))
        customSpinnerList?.add(CustomSpinnerModel("Electronics", R.drawable.ic_electronics))
        customSpinnerList?.add(CustomSpinnerModel("Self Care", R.drawable.ic_lifestyle))
        customSpinnerList?.add(CustomSpinnerModel("Medication", R.drawable.ic_medication))
        customSpinnerList?.add(CustomSpinnerModel("Entertainment", R.drawable.ic_movie))
        customSpinnerList?.add(CustomSpinnerModel("Education", R.drawable.ic_education))
        customSpinnerList?.add(CustomSpinnerModel("Bills & Utilities", R.drawable.ic_utility))

        customSpinnerAdapter = CustomSpinnerAdapter(requireContext(), customSpinnerList!!)
        binding.categorySpinner.adapter = customSpinnerAdapter

        binding.categorySpinner.onItemSelectedListener = object : AdapterView.OnItemSelectedListener{
            override fun onItemSelected(adapeterView: AdapterView<*>?, p1: View?, p2: Int, p3: Long) {

                binding.categorySpinner.dropDownWidth = 500

                val items: CustomSpinnerModel = adapeterView?.selectedItem as CustomSpinnerModel
//                Toast.makeText(context, "items-> ${items.text}", Toast.LENGTH_SHORT).show()
                categoryPic = items.icon
            }

            override fun onNothingSelected(p0: AdapterView<*>?) {

            }

        }

    }

    private fun initView(){

        updateDateEditText()

        if(binding.radioExpense.isChecked){
            MyApplication.isExpense = true
        }

        binding.radioIncome.setOnCheckedChangeListener{ _, isChecked ->
            if(isChecked) {
                binding.cardCategory.visibility = View.GONE
                MyApplication.isExpense = false
            }
            else {
                binding.cardCategory.visibility = View.VISIBLE
                MyApplication.isExpense = true
            }
        }

        changeDate()


        binding.btAdd.setOnClickListener {
            val title = binding.etTitle.text.toString()
            val amount = binding.etAmount.text.toString()


            if(title.isNotEmpty() && amount.isNotEmpty()){
                Log.d("InsertExpense", "Date in millis-> $selectedDateMillis}, date=${Date(selectedDateMillis)}")

                if(MyApplication.isExpense){
                    val expense = Expense(title = title, amount = amount.toDouble(), pic = categoryPic, timestamp = selectedDateMillis)
                    viewModel.insert(expense)
                } else {
                    val income = Income(title = title, amount = amount.toDouble(), timestamp = selectedDateMillis)
                    incomeViewModel.insertIncome(income)
                }

                binding.etTitle.setText("")
                binding.etAmount.setText("")
                binding.etDate.setText("")
                binding.etDescription.setText("")
            }

            closeFragment()

        }

        binding.btClose.setOnClickListener {
            closeFragment()
        }

    }

    private fun editExpense(expense: Expense) {
        editTransaction(
            isExpense = true,
            titleText = expense.title,
            amountValue = expense.amount,
            timestamp = expense.timestamp,
            categoryPic = expense.pic,
            expense = expense
        )
    }

    private fun editIncome(income: Income) {
        editTransaction(
            isExpense = false,
            titleText = income.title,
            amountValue = income.amount,
            timestamp = income.timestamp,
            income = income
        )
    }

    private fun editTransaction(
        expense: Expense? = null,
        income: Income? = null,
        isExpense: Boolean,
        titleText: String,
        amountValue: Double,
        timestamp: Long,
        categoryPic: Int? = null
    ){
        selectedDateMillis = timestamp

        if(!isExpense) {
            binding.radioIncome.isChecked = true
            binding.radioExpense.isChecked = false
            binding.cardCategory.visibility = View.GONE
        } else {
            binding.radioExpense.isChecked = true
            binding.radioIncome.isChecked = false
            binding.cardCategory.visibility = View.VISIBLE
        }

        binding.etTitle.setText(titleText)
        binding.etAmount.setText(amountValue.toString())
        binding.etDate.setText(timestamp.toFormattedDate())
        if(isExpense && expense != null) {
            val pic = expense.pic
            val position = customSpinnerList?.indexOfFirst { it.icon == pic }
            if (position != null) {
                binding.categorySpinner.setSelection(position)
            }
        }

        binding.btAdd.text = "Update"

        binding.btAdd.setOnClickListener {
            val title = binding.etTitle.text.toString()
            val amount = binding.etAmount.text.toString()

            if(title.isNotEmpty() && amount.isNotEmpty()){
                Log.d("UpdateTransaction", "Date in millis-> $selectedDateMillis}, date=${Date(selectedDateMillis)}")

                if (isExpense && expense != null) {
                    // Update Expense
                    val updatedExpense = expense.copy(
                        title = title,
                        amount = amount.toDouble(),
                        pic = categoryPic ?: expense.pic,
                        timestamp = selectedDateMillis
                    )
                    viewModel.update(updatedExpense)
                } else if (!isExpense && income != null) {
                    // Update Income
                    val updatedIncome = income.copy(
                        title = title,
                        amount = amount.toDouble(),
                        timestamp = selectedDateMillis
                    )
                    incomeViewModel.updateIncome(updatedIncome)
                }


                binding.etTitle.setText("")
                binding.etAmount.setText("")
                binding.etDate.setText("")
                binding.etDescription.setText("")
            }

            closeFragment()
        }

    }

    private fun changeDate() {
        binding.DateIL.setEndIconOnClickListener {
            val datePicker = MaterialDatePicker.Builder.datePicker()
                .setTitleText("Select Date")
                .setSelection(selectedDateMillis)
                .build()

            datePicker.show(requireActivity().supportFragmentManager, "DatePicker")

            datePicker.addOnPositiveButtonClickListener { selection ->

//                // Normalize selection to local timezone
//                val calendar = Calendar.getInstance(TimeZone.getTimeZone("UTC"))
//                calendar.timeInMillis = selection

                selectedDateMillis = selection

                updateDateEditText()
            }

        }

    }

    private fun updateDateEditText(){
        val sdf = SimpleDateFormat("EEE, MMM d, yyyy", Locale.getDefault())
        val display = sdf.format(Date(selectedDateMillis))
        binding.DateIL.editText?.setText(display)
    }

    private fun closeFragment(){
        requireActivity().finish()
    }

}

//Extension function for long to formatted date form
fun Long.toFormattedDate(): String {
    val sdf = SimpleDateFormat("EEE, MMM d, yyyy", Locale.getDefault())
    return sdf.format(Date(this))
}