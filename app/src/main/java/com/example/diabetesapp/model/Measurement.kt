package com.example.diabetesapp.model

import java.time.LocalDateTime
import java.util.*

class Measurement(
    var bloodGlucoseConc: Float,
    var recentFood: Boolean,
    var recentExercise: Boolean,
    var symptoms: ArrayList<String>,
    var medications: String,
    var notes: String) {

    // Date
    var day: Int? = null
    var month: Int? = null
    var year: Int? = null
    var date: String? = null

    // Time
    var hour: Int? = null
    var minute: Int? = null
    var time: String? = null

    init {
        var dateTime = LocalDateTime.now()

        day = dateTime.dayOfMonth
        month = dateTime.monthValue
        year = dateTime.year
        date = day.toString() + "-" + month.toString() + "-" + year.toString()

        hour = dateTime.hour
        minute = dateTime.minute
        time = hour.toString() + minute.toString()


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
