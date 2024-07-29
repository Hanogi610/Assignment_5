package com.example.assignment5.ui.adapter

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ArrayAdapter
import android.widget.TextView
import com.example.assignment5.data.model.Singer
import com.example.assignment5.R

class SingerSpinnerAdapter(context: Context, private val singers: List<Singer>) :
    ArrayAdapter<Singer>(context, 0, singers) {

    override fun getView(position: Int, convertView: View?, parent: ViewGroup): View {
        val view = convertView ?: LayoutInflater.from(context).inflate(
            R.layout.spinner_item_singer, parent, false
        )
        val singer = getItem(position)
        val textView = view.findViewById<TextView>(R.id.singer_name)
        textView.text = singer?.name
        return view
    }

    override fun getDropDownView(position: Int, convertView: View?, parent: ViewGroup): View {
        return getView(position, convertView, parent)
    }
}
