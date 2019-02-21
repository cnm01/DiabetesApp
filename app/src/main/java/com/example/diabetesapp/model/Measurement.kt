package com.example.diabetesapp.model

import java.time.LocalDateTime
import java.util.*
import kotlin.collections.ArrayList

class Measurement(
    var bloodGlucoseConc: Float = 0f,
    var recentFood: Boolean = false,
    var recentExercise: Boolean = false,
    var symptoms: ArrayList<String> = ArrayList(),
    var medications: String = "",
    var notes: String = "") {

    // Date
    var day: Int? = null
    var month: Int? = null
    var year: Int? = null
    var date: String? = null    // dd-mm-yyyy

    // Time
    var hour: Int? = null
    var minute: Int? = null
    var time: String? = null    // hhmm
    var timeFormatted: String? = null   //hh:mm

    init {
        var dateTime = LocalDateTime.now()

        day = dateTime.dayOfMonth
        month = dateTime.monthValue
        year = dateTime.year
        date = day.toString() + "-" + month.toString() + "-" + year.toString()

        hour = dateTime.hour
        minute = dateTime.minute
        time = hour.toString() + minute.toString()
        timeFormatted = hour.toString() + ":" + minute.toString()


    }

    override fun toString(): String {
        return "[BGC: " + bloodGlucoseConc + "\n" +
                "recentFood: " + recentFood + "\n" +
                "recentExercise: " + recentExercise + "\n" +
                "symptoms: " + symptoms.toString() + "\n" +
                "medications: " + medications + "\n" +
                "notes: " + notes + "\n" +
                "date: " + date + "\n" +
                "time: " + time + " ]"

    }

}
