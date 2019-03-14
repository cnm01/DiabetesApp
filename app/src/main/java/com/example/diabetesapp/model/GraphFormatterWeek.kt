package com.example.diabetesapp.model

import com.github.mikephil.charting.components.AxisBase
import com.github.mikephil.charting.formatter.IAxisValueFormatter

class GraphFormatterWeek : IAxisValueFormatter {

    override fun getFormattedValue(value: Float, axis: AxisBase?): String {
        val intVal = value.toInt()

        return when (intVal) {
            0 -> { "Mon" }
            1 -> { "Tues" }
            2 -> { "Weds" }
            3 -> { "Thurs" }
            4 -> { "Fri" }
            5 -> { "Sat" }
            6 -> { "Sun" }
            else -> { "-" }
        }
    }
}