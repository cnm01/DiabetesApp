package com.example.diabetesapp.view

import android.content.Context
import android.support.constraint.ConstraintLayout
import android.view.LayoutInflater
import android.view.View
import android.widget.TextView
import com.example.diabetesapp.R

class HintItem(context: Context, hint: String) : ConstraintLayout(context) {

    companion object {
        const val FREQUENT_HINT = "Improve your score by taking more frequent measurements throughout the day."
        const val ADD_HINT = "Add a measurement to get started"
        const val VERIFY_HINT = "Verify your email"
        const val RANGE_HINT = "Keep your blood glucose concentration within a safe range to improve your score"
    }

    private var textView: TextView? = null

    init {

        val inflater = context.getSystemService(Context.LAYOUT_INFLATER_SERVICE) as LayoutInflater
        val view = inflater.inflate(R.layout.hint_item_layout, this, true)

        textView = findViewById<View>(R.id.hint_text_view) as TextView
        textView!!.text = hint

    }

}