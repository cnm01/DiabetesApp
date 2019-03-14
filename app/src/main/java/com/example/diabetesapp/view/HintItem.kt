package com.example.diabetesapp.view

import android.content.Context
import android.support.constraint.ConstraintLayout
import android.view.LayoutInflater
import android.view.View
import android.widget.TextView
import com.example.diabetesapp.R

class HintItem(context: Context, hint: String) : ConstraintLayout(context) {

    companion object {
        const val FREQUENT_HINT =  R.string.frequent_hint
        const val ADD_HINT = R.string.add_hint
        const val VERIFY_HINT = R.string.verify_hint
        const val RANGE_HINT = R.string.range_hint
        const val HIGH_HINT = R.string.high_hint
        const val LOW_HINT = R.string.low_hint
        const val LOW_SCORE_HINT = R.string.low_score_hint
        const val NUM_MEASUREMENTS_HINT = R.string.num_measurements_hint
        const val PERCENT_SAFE_HINT = R.string.percent_safe_hint
        const val TIME_HINT = R.string.time_hint
        const val MEDS_HINT = R.string.meds_hint
        const val NOTES_HINT = R.string.notes_hint
        const val SICK_HINT = R.string.sick_hint
        const val HYPO_HINT = R.string.hypo_hint
        const val HYPER_HINT = R.string.hyper_hint
        const val EMERGENCY_HINT = R.string.emergency_hint
        const val GOOD_HINT = R.string.good_hint
        const val CONTINUE_HINT = R.string.continue_hint
    }

    private var textView: TextView? = null

    init {

        val inflater = context.getSystemService(Context.LAYOUT_INFLATER_SERVICE) as LayoutInflater
        val view = inflater.inflate(R.layout.hint_item_layout, this, true)

        textView = findViewById<View>(R.id.hint_text_view) as TextView
        textView!!.text = hint

    }

}