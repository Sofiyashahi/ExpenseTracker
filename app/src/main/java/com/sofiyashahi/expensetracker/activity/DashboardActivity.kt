package com.sofiyashahi.expensetracker.activity

import android.content.SharedPreferences
import android.os.Bundle
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import com.sofiyashahi.expensetracker.R
import com.sofiyashahi.expensetracker.SharedPreferenceManager
import com.sofiyashahi.expensetracker.database.ExpenseDatabase
import com.sofiyashahi.expensetracker.database.IncomeDatabase
import com.sofiyashahi.expensetracker.databinding.ActivityDashboardBinding
import com.sofiyashahi.expensetracker.fragment.ChartFragment
import com.sofiyashahi.expensetracker.fragment.HomeFragment
import com.sofiyashahi.expensetracker.fragment.ProfileFragment
import com.sofiyashahi.expensetracker.fragment.WalletFragment
import com.sofiyashahi.expensetracker.interfaces.OnOpenWalletFragment
import com.sofiyashahi.expensetracker.repository.ExpenseRepository
import com.sofiyashahi.expensetracker.repository.IncomeRepository
import com.sofiyashahi.expensetracker.viewmodel.ExpenseViewModel
import com.sofiyashahi.expensetracker.viewmodel.ExpenseViewModelFactory
import com.sofiyashahi.expensetracker.viewmodel.IncomeViewModel
import com.sofiyashahi.expensetracker.viewmodel.IncomeViewModelFactory

class DashboardActivity : AppCompatActivity(), OnOpenWalletFragment {

    private lateinit var binding: ActivityDashboardBinding
    private var sharedPreference: SharedPreferences? = null
    private var name = ""
    private var currency = ""
    private var homeFragment: HomeFragment? = null
    private var walletFragment: WalletFragment? = null
    private var chartFragment: ChartFragment? = null

    private val viewmodel: ExpenseViewModel by viewModels {
        val expenseDao = ExpenseDatabase.getDatabase(applicationContext).expenseDao()
        val repository = ExpenseRepository(expenseDao)
        ExpenseViewModelFactory(repository)
    }

    private val incomeViewModel: IncomeViewModel by viewModels {
        val incomeDao = IncomeDatabase.getIncomeDB(applicationContext).incomeDao()
        val repository = IncomeRepository(incomeDao)
        IncomeViewModelFactory(repository)
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
//        enableEdgeToEdge()
        binding = ActivityDashboardBinding.inflate(layoutInflater)
        setContentView(binding.root)

        sharedPreference = getSharedPreferences("BASIC_INFO", MODE_PRIVATE)


        homeFragment = HomeFragment(this, viewmodel, incomeViewModel, this)
        walletFragment = WalletFragment(this, viewmodel, incomeViewModel)
        chartFragment = ChartFragment(this, viewmodel)

        replaceFragment(homeFragment!!)
        initView()

    }

    private fun initView(){
        name = SharedPreferenceManager.getString("name")
        currency = SharedPreferenceManager.getString("currency")

        navigationBar()
    }

    private fun navigationBar(){
        binding.bottomNavigation.setOnItemSelectedListener {
            when(it.itemId){
                R.id.home -> replaceFragment(homeFragment!!)
                R.id.wallet -> replaceFragment(walletFragment!!)
                R.id.chart -> replaceFragment(chartFragment!!)
                R.id.profile -> replaceFragment(ProfileFragment())
                else -> {}
            }
            true
        }
    }

    private fun replaceFragment(fragment: Fragment){
        binding.fragmentContainer.removeAllViews()
        val fragmentManager = supportFragmentManager
        val fragmentTransaction = fragmentManager.beginTransaction()
        fragmentTransaction.replace(R.id.fragment_container, fragment)
        fragmentTransaction.addToBackStack(null)
        fragmentTransaction.commit()
    }

    override fun onOpen() {
        binding.bottomNavigation.selectedItemId = R.id.wallet
    }
}