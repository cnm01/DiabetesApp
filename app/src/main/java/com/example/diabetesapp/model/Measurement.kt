package com.example.diabetesapp.model

import java.time.LocalDateTime



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
        setTime()
        setTimeFormatted()


    }

    private fun setTime() {
        if(hour!! < 10) {
            if(minute!! < 10) {
                time = "0" + hour.toString() + "0" + minute.toString()
            }
            else {
                time = "0" + hour.toString() + minute.toString()
            }
        }
        else {
            if(minute!! < 10) {
                time = hour.toString() + "0" + minute.toString()
            }
            else {
                time = hour.toString() + minute.toString()
            }
        }

    }

    private fun setTimeFormatted() {
        if(hour!! < 10) {
            if(minute!! < 10) {
                timeFormatted = "0" + hour.toString() + ":" + "0" + minute.toString()
            }
            else {
                timeFormatted = "0" + hour.toString() + ":" + minute.toString()
            }
        }
        else {
            if(minute!! < 10) {
                timeFormatted = hour.toString() + ":" + "0" + minute.toString()
            }
            else {
                timeFormatted = hour.toString() + ":" + minute.toString()
            }
        }

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
