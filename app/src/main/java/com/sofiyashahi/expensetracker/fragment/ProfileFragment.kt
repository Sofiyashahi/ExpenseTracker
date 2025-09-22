package com.sofiyashahi.expensetracker.fragment

import android.app.Dialog
import android.icu.util.Currency
import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.Window
import androidx.appcompat.app.AppCompatActivity
import com.google.android.material.textfield.TextInputEditText
import com.mukesh.countrypicker.CountryPicker
import com.sofiyashahi.expensetracker.R
import com.sofiyashahi.expensetracker.SharedPreferenceManager
import com.sofiyashahi.expensetracker.activity.DashboardActivity
import com.sofiyashahi.expensetracker.databinding.FragmentProfileBinding

class ProfileFragment : Fragment() {

    private lateinit var binding: FragmentProfileBinding
    private var currency = ""
    private val TAG = "ProfileFragment"

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        binding = FragmentProfileBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        setUpView()
        editProfile()
    }

    private fun setUpView(){
        val name = SharedPreferenceManager.getString("name")
        binding.tvUserName.text = name
        val email = SharedPreferenceManager.getString("email")
        binding.tvUserEmail.text = email

        val currencyCode = SharedPreferenceManager.getString("currencyCode")
        binding.tvCurrency.text = "Currency: $currencyCode"

        updateCurrency()

    }

    private fun editProfile(){
        binding.btnEditProfile.setOnClickListener {
            val dialog = Dialog(requireContext())
            dialog.setCancelable(false)
            dialog.setContentView(R.layout.dialog_edit_profile)

            val name = SharedPreferenceManager.getString("name")
            dialog.findViewById<TextInputEditText>(R.id.etName).setText(name)
            val email = SharedPreferenceManager.getString("email")
            dialog.findViewById<TextInputEditText>(R.id.etEmail).setText(email)


            dialog.findViewById<View>(R.id.btSave).setOnClickListener {

                val editName = dialog.findViewById<TextInputEditText>(R.id.etName).text.toString()
                val editEmail = dialog.findViewById<TextInputEditText>(R.id.etEmail).text.toString()
                binding.tvUserName.text = editName
                binding.tvUserEmail.text = editEmail
                SharedPreferenceManager.putString("name", editName)
                SharedPreferenceManager.putString("email", editEmail)
                dialog.dismiss()
            }
            dialog.show()
        }

    }

    private fun updateCurrency(){
        binding.tvCurrency.setOnClickListener {
            val builder = CountryPicker.Builder().with(requireContext())
                .listener { country ->
                    Log.d(TAG, "onSelectCountry: ${country.currency}")
                    binding.tvCurrency.text = country.currency
                    val currencyCode = Currency.getInstance(country.currency)
                    val currencySymbol = currencyCode.symbol
                    Log.d(TAG, "currency symbol: $currencySymbol")
                    currency = currencySymbol
                    SharedPreferenceManager.putString("currency", currency)
                    SharedPreferenceManager.putString("currencyCode", country.currency)
                    binding.tvCurrency.text = "Currency: ${country.currency}"

                }

            val picker = builder.build()
            picker.showDialog(requireActivity() as AppCompatActivity)

        }
    }


}