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
        const val RANGE_HINT = "Keep your blood glucose concentration within a safe range to improve your score."
        const val HIGH_HINT = "Keep your highest blood glucose concentration measurement within the safe range to ensure a good score."
        const val LOW_HINT = "Keep your lowest blood glucose concentration measurement within the safe range to ensure a good score."
        const val LOW_SCORE_HINT = "If your score is consistently low despite adequate management, seek guidance from a healthcare professional. It may be necessary to try a different treatment, medication, or make changes to your lifestyle."
        const val NUM_MEASUREMENTS_HINT = "A higher number of symptoms in a day indicates management or treatment is not optimal."
        const val PERCENT_SAFE_HINT = "A higher proportion of measurements within the safe range will result in a higher score."
        const val TIME_HINT = "To ensure a higher score, take your first measurement early on in the day, and your last measurement late in the day. Ensure you take lots of measurements in between."
        const val MEDS_HINT = "Use the 'Medications' section when adding a new measurement to record current medications and dosages for later viewing."
        const val NOTES_HINT = "Record any changes or abnormalities with the 'Notes' section when adding a new measurement."
        const val SICK_HINT = "Temporary illnesses such as colds/flu can impact your ability to maintain a healthy blood glucose concentration. Be sure to record any temporary illnesses when taking a new measurement, using the 'Notes' section."
        const val HYPO_HINT = "A blood glucose concentration below 4mmol/L may indicate hypoglycaemia. Seek medical advice."
        const val HYPER_HINT = "A blood glucose concentration above 8.5mmol/L may indicate hyperglycaemia. Seek medical advice."
        const val EMERGENCY_HINT = "Seek immediate medical attention if you are unable to control your blood glucose concentration to within a safe range."
    }

    private var textView: TextView? = null

    init {

        val inflater = context.getSystemService(Context.LAYOUT_INFLATER_SERVICE) as LayoutInflater
        val view = inflater.inflate(R.layout.hint_item_layout, this, true)

        textView = findViewById<View>(R.id.hint_text_view) as TextView
        textView!!.text = hint

    }

}