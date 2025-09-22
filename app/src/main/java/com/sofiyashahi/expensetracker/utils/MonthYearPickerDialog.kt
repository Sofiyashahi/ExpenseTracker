package com.sofiyashahi.expensetracker.utils

import android.app.Dialog
import android.content.Context
import com.sofiyashahi.expensetracker.R
import com.sofiyashahi.expensetracker.databinding.DialogMonthYearPickerBinding
import java.util.Calendar

class MonthYearPickerDialog(context: Context, private val listener: (month: Int, year: Int) -> Unit
    ): Dialog(context)  {

        private var binding: DialogMonthYearPickerBinding = DialogMonthYearPickerBinding.inflate(layoutInflater)

    init {
        setContentView(binding.root)

            val numberPickerMonth = binding.pickerMonth
            val numberPickerYear = binding.pickerYear
            val btnOk = binding.btnOk
            val btnCancel = binding.btnCancel

            val currentYear = Calendar.getInstance().get(Calendar.YEAR)

            numberPickerMonth.minValue = 1
            numberPickerMonth.maxValue = 12
            numberPickerMonth.displayedValues = arrayOf(
                "Jan", "Feb", "Mar", "Apr", "May", "Jun",
                "Jul", "Aug", "Sep", "Oct", "Nov", "Dec"
            )

            numberPickerYear.minValue = 2000
            numberPickerYear.maxValue = currentYear + 10
            numberPickerYear.value = currentYear

            btnOk.setOnClickListener {
                listener(numberPickerMonth.value, numberPickerYear.value)
                dismiss()
            }

            btnCancel.setOnClickListener {
                dismiss()
            }


        }
}