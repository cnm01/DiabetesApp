package com.example.diabetesapp

import com.example.diabetesapp.model.Calculator
import junit.framework.Assert
import org.junit.Test

class ScoreUnitTest {




    @Test
    fun case_1() {
    // All measurements within safe range
    // Low number of measurements

        var m1 = com.example.diabetesapp.model.Measurement(5f, false, false, arrayListOf(), "", "")
        m1.time = "0800"
        var m2 = com.example.diabetesapp.model.Measurement(7f, false, false, arrayListOf(), "", "")
        m2.time = "0900"
        var m3 = com.example.diabetesapp.model.Measurement(5f, false, false, arrayListOf(), "", "")
        m3.time = "1000"
        val measurements = arrayListOf(m1,m2,m3)

        val calculator = Calculator(measurements, false)
        Assert.assertEquals(92, calculator.score)
    }

    @Test
    fun case_2() {
    // All measurements within safe range
    // High number of measurements

        var m1 = com.example.diabetesapp.model.Measurement(5f, false, false, arrayListOf(), "", "")
        m1.time = "0800"
        var m2 = com.example.diabetesapp.model.Measurement(7f, false, false, arrayListOf(), "", "")
        m2.time = "0900"
        var m3 = com.example.diabetesapp.model.Measurement(5f, false, false, arrayListOf(), "", "")
        m3.time = "1000"
        var m4 = com.example.diabetesapp.model.Measurement(5f, false, false, arrayListOf(), "", "")
        m4.time = "1100"
        var m5 = com.example.diabetesapp.model.Measurement(4f, false, false, arrayListOf(), "", "")
        m5.time = "1200"
        var m6 = com.example.diabetesapp.model.Measurement(5.5f, false, false, arrayListOf(), "", "")
        m6.time = "1300"
        var m7 = com.example.diabetesapp.model.Measurement(5f, false, false, arrayListOf(), "", "")
        m7.time = "1400"
        val measurements = arrayListOf(m1,m2,m3,m4,m5,m6,m7)

        val calculator = Calculator(measurements, false)
        Assert.assertEquals(100, calculator.score)
    }

    @Test
    fun case_3() {
    // Dataset with high deviation gives lower score than dataset with low deviation

        var m1 = com.example.diabetesapp.model.Measurement(5f, false, false, arrayListOf(), "", "")
        m1.time = "0800"
        var m2 = com.example.diabetesapp.model.Measurement(14f, false, false, arrayListOf(), "", "")
        m2.time = "0900"
        var m3 = com.example.diabetesapp.model.Measurement(5f, false, false, arrayListOf(), "", "")
        m3.time = "1000"
        var m4 = com.example.diabetesapp.model.Measurement(5f, false, false, arrayListOf(), "", "")
        m4.time = "1100"
        var m5 = com.example.diabetesapp.model.Measurement(4f, false, false, arrayListOf(), "", "")
        m5.time = "1200"
        var m6 = com.example.diabetesapp.model.Measurement(5.5f, false, false, arrayListOf(), "", "")
        m6.time = "1300"
        var m7 = com.example.diabetesapp.model.Measurement(5f, false, false, arrayListOf(), "", "")
        m7.time = "1400"
        val measurementsHigh = arrayListOf(m1,m2,m3,m4,m5,m6,m7)

        var b1 = com.example.diabetesapp.model.Measurement(5f, false, false, arrayListOf(), "", "")
        b1.time = "0800"
        var b2 = com.example.diabetesapp.model.Measurement(10f, false, false, arrayListOf(), "", "")
        b2.time = "0900"
        var b3 = com.example.diabetesapp.model.Measurement(5f, false, false, arrayListOf(), "", "")
        b3.time = "1000"
        var b4 = com.example.diabetesapp.model.Measurement(5f, false, false, arrayListOf(), "", "")
        b4.time = "1100"
        var b5 = com.example.diabetesapp.model.Measurement(4f, false, false, arrayListOf(), "", "")
        b5.time = "1200"
        var b6 = com.example.diabetesapp.model.Measurement(5.5f, false, false, arrayListOf(), "", "")
        b6.time = "1300"
        var b7 = com.example.diabetesapp.model.Measurement(5f, false, false, arrayListOf(), "", "")
        b7.time = "1400"
        val measurementsLow = arrayListOf(b1,b2,b3,b4,b5,b6,b7)

        val calculatorHigh = Calculator(measurementsHigh, false)
        val calculatorLow = Calculator(measurementsLow, false)

        println("High deviation score : " + calculatorHigh.score)
        println("Low deviation score : " + calculatorLow.score)
        Assert.assertTrue(calculatorHigh.score < calculatorLow.score)
    }

    @Test
    fun case_4() {
    // All measurements within safe range
    // 5 symptoms present

        var m1 = com.example.diabetesapp.model.Measurement(5f, false, false, arrayListOf("Dizziness", "Fatigue"), "", "")
        m1.time = "0800"
        var m2 = com.example.diabetesapp.model.Measurement(7f, false, false, arrayListOf("Blurred Vision"), "", "")
        m2.time = "0900"
        var m3 = com.example.diabetesapp.model.Measurement(5f, false, false, arrayListOf("Thirstiness"), "", "")
        m3.time = "1000"
        var m4 = com.example.diabetesapp.model.Measurement(5f, false, false, arrayListOf(), "", "")
        m4.time = "1100"
        var m5 = com.example.diabetesapp.model.Measurement(4f, false, false, arrayListOf(), "", "")
        m5.time = "1200"
        var m6 = com.example.diabetesapp.model.Measurement(5.5f, false, false, arrayListOf(), "", "")
        m6.time = "1300"
        var m7 = com.example.diabetesapp.model.Measurement(5f, false, false, arrayListOf("Headache"), "", "")
        m7.time = "1400"
        val measurements = arrayListOf(m1,m2,m3,m4,m5,m6,m7)

        val calculator = Calculator(measurements, false)
        Assert.assertEquals(95, calculator.score)
    }

    @Test
    fun case_5() {
    // Dataset with bigger low deviation gives lower score than dataset with smaller low deviation

        var m1 = com.example.diabetesapp.model.Measurement(5f, false, false, arrayListOf(), "", "")
        m1.time = "0800"
        var m2 = com.example.diabetesapp.model.Measurement(0f, false, false, arrayListOf(), "", "")
        m2.time = "0900"
        var m3 = com.example.diabetesapp.model.Measurement(5f, false, false, arrayListOf(), "", "")
        m3.time = "1000"
        var m4 = com.example.diabetesapp.model.Measurement(5f, false, false, arrayListOf(), "", "")
        m4.time = "1100"
        var m5 = com.example.diabetesapp.model.Measurement(4f, false, false, arrayListOf(), "", "")
        m5.time = "1200"
        var m6 = com.example.diabetesapp.model.Measurement(5.5f, false, false, arrayListOf(), "", "")
        m6.time = "1300"
        var m7 = com.example.diabetesapp.model.Measurement(5f, false, false, arrayListOf(), "", "")
        m7.time = "1400"
        val measurementsHigh = arrayListOf(m1,m2,m3,m4,m5,m6,m7)

        var b1 = com.example.diabetesapp.model.Measurement(5f, false, false, arrayListOf(), "", "")
        b1.time = "0800"
        var b2 = com.example.diabetesapp.model.Measurement(3f, false, false, arrayListOf(), "", "")
        b2.time = "0900"
        var b3 = com.example.diabetesapp.model.Measurement(5f, false, false, arrayListOf(), "", "")
        b3.time = "1000"
        var b4 = com.example.diabetesapp.model.Measurement(5f, false, false, arrayListOf(), "", "")
        b4.time = "1100"
        var b5 = com.example.diabetesapp.model.Measurement(4f, false, false, arrayListOf(), "", "")
        b5.time = "1200"
        var b6 = com.example.diabetesapp.model.Measurement(5.5f, false, false, arrayListOf(), "", "")
        b6.time = "1300"
        var b7 = com.example.diabetesapp.model.Measurement(5f, false, false, arrayListOf(), "", "")
        b7.time = "1400"
        val measurementsLow = arrayListOf(b1,b2,b3,b4,b5,b6,b7)

        val calculatorHigh = Calculator(measurementsHigh, false)
        val calculatorLow = Calculator(measurementsLow, false)

        println("Big deviation score : " + calculatorHigh.score)
        println("Small deviation score : " + calculatorLow.score)
        Assert.assertTrue(calculatorHigh.score < calculatorLow.score)
    }

    @Test
    fun case_6() {
    // Dataset with large proportion of measurements below range gives lower score
    //  than data set with smaller proportion of measurements below of range

        var m1 = com.example.diabetesapp.model.Measurement(5f, false, false, arrayListOf(), "", "")
        m1.time = "0800"
        var m2 = com.example.diabetesapp.model.Measurement(0f, false, false, arrayListOf(), "", "")
        m2.time = "0900"
        var m3 = com.example.diabetesapp.model.Measurement(1f, false, false, arrayListOf(), "", "")
        m3.time = "1000"
        var m4 = com.example.diabetesapp.model.Measurement(2f, false, false, arrayListOf(), "", "")
        m4.time = "1100"
        var m5 = com.example.diabetesapp.model.Measurement(3f, false, false, arrayListOf(), "", "")
        m5.time = "1200"
        var m6 = com.example.diabetesapp.model.Measurement(5.5f, false, false, arrayListOf(), "", "")
        m6.time = "1300"
        var m7 = com.example.diabetesapp.model.Measurement(5f, false, false, arrayListOf(), "", "")
        m7.time = "1400"
        val measurementsHigh = arrayListOf(m1,m2,m3,m4,m5,m6,m7)

        var b1 = com.example.diabetesapp.model.Measurement(5f, false, false, arrayListOf(), "", "")
        b1.time = "0800"
        var b2 = com.example.diabetesapp.model.Measurement(3f, false, false, arrayListOf(), "", "")
        b2.time = "0900"
        var b3 = com.example.diabetesapp.model.Measurement(5f, false, false, arrayListOf(), "", "")
        b3.time = "1000"
        var b4 = com.example.diabetesapp.model.Measurement(5f, false, false, arrayListOf(), "", "")
        b4.time = "1100"
        var b5 = com.example.diabetesapp.model.Measurement(4f, false, false, arrayListOf(), "", "")
        b5.time = "1200"
        var b6 = com.example.diabetesapp.model.Measurement(5.5f, false, false, arrayListOf(), "", "")
        b6.time = "1300"
        var b7 = com.example.diabetesapp.model.Measurement(5f, false, false, arrayListOf(), "", "")
        b7.time = "1400"
        val measurementsLow = arrayListOf(b1,b2,b3,b4,b5,b6,b7)

        val calculatorHigh = Calculator(measurementsHigh, false)
        val calculatorLow = Calculator(measurementsLow, false)

        println("Large proportion score : " + calculatorHigh.score)
        println("Small proportion score : " + calculatorLow.score)
        Assert.assertTrue(calculatorHigh.score < calculatorLow.score)

    }

    @Test
    fun case_7() {
        // Dataset with large proportion of measurements above range gives lower score
        //  than data set with smaller proportion of measurements above range

        var m1 = com.example.diabetesapp.model.Measurement(7f, false, false, arrayListOf(), "", "")
        m1.time = "0800"
        var m2 = com.example.diabetesapp.model.Measurement(10f, false, false, arrayListOf(), "", "")
        m2.time = "0900"
        var m3 = com.example.diabetesapp.model.Measurement(11f, false, false, arrayListOf(), "", "")
        m3.time = "1000"
        var m4 = com.example.diabetesapp.model.Measurement(12f, false, false, arrayListOf(), "", "")
        m4.time = "1100"
        var m5 = com.example.diabetesapp.model.Measurement(13f, false, false, arrayListOf(), "", "")
        m5.time = "1200"
        var m6 = com.example.diabetesapp.model.Measurement(8f, false, false, arrayListOf(), "", "")
        m6.time = "1300"
        var m7 = com.example.diabetesapp.model.Measurement(6f, false, false, arrayListOf(), "", "")
        m7.time = "1400"
        val measurementsHigh = arrayListOf(m1,m2,m3,m4,m5,m6,m7)

        var b1 = com.example.diabetesapp.model.Measurement(7f, false, false, arrayListOf(), "", "")
        b1.time = "0800"
        var b2 = com.example.diabetesapp.model.Measurement(10f, false, false, arrayListOf(), "", "")
        b2.time = "0900"
        var b3 = com.example.diabetesapp.model.Measurement(9f, false, false, arrayListOf(), "", "")
        b3.time = "1000"
        var b4 = com.example.diabetesapp.model.Measurement(9f, false, false, arrayListOf(), "", "")
        b4.time = "1100"
        var b5 = com.example.diabetesapp.model.Measurement(8f, false, false, arrayListOf(), "", "")
        b5.time = "1200"
        var b6 = com.example.diabetesapp.model.Measurement(7f, false, false, arrayListOf(), "", "")
        b6.time = "1300"
        var b7 = com.example.diabetesapp.model.Measurement(7f, false, false, arrayListOf(), "", "")
        b7.time = "1400"
        val measurementsLow = arrayListOf(b1,b2,b3,b4,b5,b6,b7)

        val calculatorHigh = Calculator(measurementsHigh, false)
        val calculatorLow = Calculator(measurementsLow, false)

        println("Large proportion score : " + calculatorHigh.score)
        println("Small proportion score : " + calculatorLow.score)
        Assert.assertTrue(calculatorHigh.score < calculatorLow.score)

    }




}