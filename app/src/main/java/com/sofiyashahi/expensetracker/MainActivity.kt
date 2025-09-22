package com.sofiyashahi.expensetracker

import android.content.Intent
import android.icu.util.Currency
import android.os.Bundle
import android.util.Log
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.mukesh.countrypicker.CountryPicker
import com.sofiyashahi.expensetracker.activity.DashboardActivity
import com.sofiyashahi.expensetracker.databinding.ActivityMainBinding

class MainActivity : AppCompatActivity() {

    private lateinit var binding: ActivityMainBinding
    private val TAG = "MainActivity"
    private var name = ""
    private var currency = ""

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
//        enableEdgeToEdge()
        setContentView(binding.root)

        setUpView()


    }

    override fun onResume() {
        super.onResume()

        if(!SharedPreferenceManager.getBoolean("prevStarted", false)){
            SharedPreferenceManager.putBoolean("prevStarted", true)
        } else {
            Intent(this, DashboardActivity::class.java).also {
                startActivity(it)
            }
        }
    }

    private fun setUpView(){

        binding.layoutCountry.setOnClickListener {
            selectCurrency()
        }

        binding.btGetStart.setOnClickListener{
            name = binding.etName.text.toString()
            SharedPreferenceManager.putString("name", name)
            SharedPreferenceManager.putString("currency", currency)

            if (binding.etName.text.isNullOrEmpty()){
                Toast.makeText(this, "Please enter your name", Toast.LENGTH_SHORT).show()
            } else if (binding.tvCurrency.text.isNullOrEmpty()){
                Toast.makeText(this, "Please select your currency", Toast.LENGTH_SHORT).show()
            } else {
                Intent(this, DashboardActivity::class.java).also {
                    Log.d(TAG, "setUpView: name-> $name")
                    startActivity(it)
                }
            }
        }
    }

    private fun selectCurrency(){
        val builder = CountryPicker.Builder().with(this)
            .listener { country ->
                Log.d(TAG, "onSelectCountry: ${country.currency}")
                SharedPreferenceManager.putString("currencyCode", country.currency)
                binding.tvCurrency.text = country.currency
                val currencyCode = Currency.getInstance(country.currency)
                val currencySymbol = currencyCode.symbol
                Log.d(TAG, "currency symbol: $currencySymbol")
                currency = currencySymbol
            }

        val picker = builder.build()
        picker.showDialog(this)
    }
}