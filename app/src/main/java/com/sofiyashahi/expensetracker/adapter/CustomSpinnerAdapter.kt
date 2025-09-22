package com.sofiyashahi.expensetracker.adapter

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ArrayAdapter
import android.widget.ImageView
import android.widget.TextView
import com.sofiyashahi.expensetracker.R
import com.sofiyashahi.expensetracker.model.CustomSpinnerModel

class CustomSpinnerAdapter(context: Context, private val items: ArrayList<CustomSpinnerModel>) :
    ArrayAdapter<CustomSpinnerModel>(context, 0, items) {

    override fun getView(position: Int, convertView: View?, parent: ViewGroup): View {
        var itemView = convertView

        if(itemView == null){
            itemView = LayoutInflater.from(context).inflate(R.layout.custom_spinner_layout, parent, false)
        }

        val item = items[position]
        val spinnerImage: ImageView = itemView?.findViewById(R.id.ivIcon)!!
        val spinnerText: TextView = itemView.findViewById(R.id.tvSpinnerText)!!

        spinnerImage.setImageResource(item.icon)
        spinnerText.text = item.text

        return itemView
    }

    override fun getDropDownView(position: Int, convertView: View?, parent: ViewGroup): View {
        var itemView = convertView
        if(itemView == null){
            itemView = LayoutInflater.from(context).inflate(R.layout.spinner_dropdown_layout, parent, false)
        }

        val item = items[position]
        val spinnerImage: ImageView = itemView?.findViewById(R.id.ivDropDownIcon)!!
        val spinnerText: TextView = itemView.findViewById(R.id.tvDropDownText)!!

        spinnerImage.setImageResource(item.icon)
        spinnerText.text = item.text

        return itemView
    }

    private fun customView(position: Int, convertView: View?, parent: ViewGroup): View {
        var itemView = convertView
        if(itemView == null){
            itemView = LayoutInflater.from(context).inflate(R.layout.custom_spinner_layout, parent, false)
        }

        val item = items[position]
        val spinnerImage: ImageView = itemView?.findViewById(R.id.ivIcon)!!
        val spinnerText: TextView = itemView.findViewById(R.id.tvSpinnerText)!!

        spinnerImage.setImageResource(item.icon)
        spinnerText.text = item.text

        return itemView!!
    }
}