package com.example.diabetesapp.view

import android.content.Context
import android.graphics.Color
import android.graphics.drawable.GradientDrawable
import android.support.constraint.ConstraintLayout
import android.support.v4.content.ContextCompat
import android.view.LayoutInflater
import android.view.View
import android.widget.Button
import android.widget.TextView
import com.example.diabetesapp.R

class DayItem(context: Context, dayIn: String, monthIn: String, scoreIn : Int): ConstraintLayout(context) {


    private var day: String? = null
    private var month: String? = null
    private var score: Int? = null

    // Elements
    private var dayTextView: TextView? = null
    private var monthTextView: TextView? = null
    private var scoreButton: Button? = null


    init {

        val inflater = context.getSystemService(Context.LAYOUT_INFLATER_SERVICE) as LayoutInflater
        val view = inflater.inflate(R.layout.day_item_layout, this, true)

        day = dayIn
        month = monthIn
        score = scoreIn

        dayTextView = findViewById<View>(R.id.day_text_view) as TextView
        monthTextView = findViewById<View>(R.id.month_text_view) as TextView
        scoreButton = findViewById<View>(R.id.score_button) as Button

        dayTextView!!.text = day
        monthTextView!!.text = month

        setScore()


    }

    private fun setScore() {
        val drawable = scoreButton!!.background as GradientDrawable
        when {
            (score == -1) -> {
                drawable.setStroke(1, ContextCompat.getColor(context, R.color.colorPrimary))
                scoreButton!!.text = "-"
                return
            }
            (score!! >= 70) -> {
                drawable.setStroke(1, ContextCompat.getColor(context, R.color.scoreGood))
            }
            (score!! >= 30) -> {
                drawable.setStroke(1, Color.YELLOW)
            }
            else -> {
                drawable.setStroke(1, Color.RED)
            }
        }
        scoreButton!!.text = "$score"
    }


}