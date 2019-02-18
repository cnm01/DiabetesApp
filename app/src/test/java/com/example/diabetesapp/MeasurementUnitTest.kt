package com.example.diabetesapp

import com.example.diabetesapp.model.Measurement
import org.junit.Assert
import org.junit.Test
import java.time.LocalDateTime

class MeasurementUnitTest {

    private var measurement = Measurement(5.0f, false, false, arrayListOf("Headache"), "", "")

    @Test
    fun sample_addition_isCorrect() {
        Assert.assertEquals(4, 2 + 2)
    }

    @Test
    fun measurement_notNull() {
        Assert.assertNotNull(measurement)
    }

    @Test
    fun toString_prints() {
        println(measurement.toString())
    }

    @Test
    fun day_isCorrect() {
        val today = LocalDateTime.now().dayOfMonth
        Assert.assertEquals(today, measurement.day)
    }

    @Test
    fun month_isCorrect() {
        val today = LocalDateTime.now().monthValue
        Assert.assertEquals(today, measurement.month)
    }

    @Test
    fun year() {
        val today = LocalDateTime.now().year
        Assert.assertEquals(today, measurement.year)
    }
}