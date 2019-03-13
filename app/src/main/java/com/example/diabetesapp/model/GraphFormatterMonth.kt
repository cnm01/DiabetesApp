package com.example.diabetesapp.model

import com.github.mikephil.charting.components.AxisBase
import com.github.mikephil.charting.formatter.IAxisValueFormatter

class GraphFormatterMonth : IAxisValueFormatter {

    override fun getFormattedValue(value: Float, axis: AxisBase?): String {
        val intVal = value.toInt()

        return intVal.toString()


    }


}