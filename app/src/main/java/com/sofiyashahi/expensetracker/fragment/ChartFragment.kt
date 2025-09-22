package com.sofiyashahi.expensetracker.fragment

import android.content.Context
import android.graphics.Color
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.lifecycleScope
import androidx.lifecycle.repeatOnLifecycle
import com.github.mikephil.charting.animation.Easing
import com.github.mikephil.charting.charts.PieChart
import com.github.mikephil.charting.data.PieData
import com.github.mikephil.charting.data.PieDataSet
import com.github.mikephil.charting.data.PieEntry
import com.github.mikephil.charting.formatter.PercentFormatter
import com.sofiyashahi.expensetracker.R
import com.sofiyashahi.expensetracker.databinding.FragmentChartBinding
import com.sofiyashahi.expensetracker.model.Expense
import com.sofiyashahi.expensetracker.model.UiState
import com.sofiyashahi.expensetracker.viewmodel.ExpenseViewModel
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.launch


class ChartFragment(private val context: Context, private val expenseViewModel: ExpenseViewModel) : Fragment() {

    private lateinit var binding: FragmentChartBinding
    private lateinit var piechart: PieChart

    private val categoryMap = mapOf(
        R.drawable.ic_fastfood to "Food",
        R.drawable.ic_clothes to "Shopping",
        R.drawable.ic_transportation to "Transportation",
        R.drawable.ic_grocery to "Groceries",
        R.drawable.ic_investment to "Investment",
        R.drawable.ic_electronics to "Electronics",
        R.drawable.ic_lifestyle to "Self Care",
        R.drawable.ic_medication to "Medication",
        R.drawable.ic_movie to "Entertainment",
        R.drawable.ic_education to "Education",
        R.drawable.ic_utility to "Bills & Utilities"
    )

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        binding = FragmentChartBinding.inflate(inflater, container, false)
        piechart = binding.pieChart

        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        setUpView()
    }

    private fun setUpView(){

        lifecycleScope.launch {
            repeatOnLifecycle(Lifecycle.State.STARTED) {
                expenseViewModel.expenseState.collect{state->
                    when(state){
                        is UiState.Loading -> {
                            piechart.clear()
                            piechart.centerText = "Loading..."
                            piechart.setCenterTextSize(14f)
                        }
                        is UiState.Success-> {
                            setUpPieChart(state.data)
                        }
                        is UiState.Empty -> {
                            piechart.clear()
                            piechart.centerText = "No Expenses Available"
                            piechart.setCenterTextSize(14f)
                        }
                        is UiState.Error -> {
                            piechart.clear()
                            piechart.centerText = "Error Loading Data"
                            piechart.setCenterTextSize(14f)
                        }

                    }
                }
            }
        }
    }

    private fun setUpPieChart(expenses: List<Expense>) {
        val categoryTotals = expenses.groupBy { it.pic }
            .mapValues { entry-> entry.value.sumOf { it.amount } }

        val pieEntries = ArrayList<PieEntry>()
        val colors = ArrayList<Int>()

        val colorList = listOf(
            Color.parseColor("#FF6F61"), // Red-ish
            Color.parseColor("#6B8E23"), // Olive
            Color.parseColor("#1E90FF"), // Blue
            Color.parseColor("#FFD700"), // Yellow
            Color.parseColor("#FF69B4"), // Pink
            Color.parseColor("#40E0D0"), // Teal
            Color.parseColor("#8A2BE2"), // Purple
            Color.parseColor("#FF8C00")  // Orange
        )

        var colorIndex = 0
        for ((categoryPic, total) in categoryTotals) {
            val categoryTitle = categoryMap[categoryPic] ?: "Unknown"
            pieEntries.add(PieEntry(total.toFloat(), categoryTitle))
            colors.add(colorList[colorIndex % colorList.size])
            colorIndex++
        }

        val pieDataSet = PieDataSet(pieEntries, "Expense Categories")
        pieDataSet.colors = colors
        pieDataSet.sliceSpace = 3f
        pieDataSet.valueTextSize = 14f
        pieDataSet.valueTextColor = Color.WHITE

        val pieData = PieData(pieDataSet)
        pieData.setValueFormatter(PercentFormatter())
        piechart.data = pieData

        // Chart appearance
        piechart.setUsePercentValues(true)
        piechart.description.isEnabled = false
        piechart.isDrawHoleEnabled = true
        piechart.setHoleColor(Color.TRANSPARENT)
        piechart.setTransparentCircleAlpha(110)
        piechart.setEntryLabelColor(Color.BLACK)
        piechart.setEntryLabelTextSize(12f)
        piechart.centerText = "Expenses by Category"
        piechart.setCenterTextSize(16f)
        piechart.animateY(1000, Easing.EaseInOutQuad)

        piechart.invalidate()
    }


}