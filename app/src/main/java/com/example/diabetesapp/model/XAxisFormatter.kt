package com.example.diabetesapp.model

import com.github.mikephil.charting.components.AxisBase
import com.github.mikephil.charting.formatter.IAxisValueFormatter

class XAxisFormatter() : IAxisValueFormatter {

    override fun getFormattedValue(value: Float, axis: AxisBase?): String {
        val intVal = value.toInt()
        val str = intVal.toString()

        return when {
            str.length == 2 -> str[0] + str[1].toString() + ":" + "00"
            str.length == 3 -> "0" + str[0] + ":" + str[1] + str[2]
            str.length == 4 -> str[0] + str[1].toString() + ":" + str[2] + str[3]
            else -> str
        }

    }


}