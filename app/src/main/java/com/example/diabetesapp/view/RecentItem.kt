package com.example.diabetesapp.view

import android.content.Context
import android.support.constraint.ConstraintLayout
import android.view.LayoutInflater
import android.view.View
import android.widget.TextView
import com.example.diabetesapp.R

class RecentItem(context: Context, timeIn: String, bgcIn: String): ConstraintLayout(context) {


    private var time: String? = null
    private var bgc: String? = null

    // Elements
    private var timeTextView: TextView? = null
    private var bgcTextView: TextView? = null


    init {

        val inflater = context.getSystemService(Context.LAYOUT_INFLATER_SERVICE) as LayoutInflater
        val view = inflater.inflate(R.layout.recent_item_layout, this, true)

        time = timeIn
        bgc = bgcIn

        timeTextView = findViewById<View>(R.id.time_text_view) as TextView
        bgcTextView = findViewById<View>(R.id.bgc_text_view) as TextView

        timeTextView!!.text = time
        bgcTextView!!.text = bgc + " mmol/L"







    }




}