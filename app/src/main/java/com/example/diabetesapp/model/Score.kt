package com.example.diabetesapp.model

import java.time.LocalDateTime

class Score(var score: Int = 0) {

    var day: Int? = null
    var month: Int? = null
    var year: Int? = null
    var date: String? = null

    init {
        var dateTime = LocalDateTime.now()
        day = dateTime.dayOfMonth
        month = dateTime.monthValue
        year = dateTime.year
        date = day.toString() + "-" + month.toString() + "-" + year.toString()
    }

}