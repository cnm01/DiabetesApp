package com.example.diabetesapp

import com.example.diabetesapp.model.Calculator
import com.example.diabetesapp.model.Measurement
import junit.framework.Assert
import org.junit.Test

class CalculatorUnitTest {

    // Upper Boundary Duration Tests:

        // First measurement inside range

    @Test
    fun basic_case_empty() {
        // No measurements

        val measurements = arrayListOf<Measurement>()

        val calculator = Calculator(measurements, true)
        Assert.assertEquals(0, calculator.duration)
    }

    @Test
    fun upper_1a() {
        // BGC starts inside desired range
        // Increases above range
        // Drops back down to within desired range

        var m1 = Measurement(5f, false, false, arrayListOf(),"", "" )
        m1.time = "0800"
        var m2 = Measurement(7f, false, false, arrayListOf(),"", "" )
        m2.time = "0900"
        var m3 = Measurement(5f, false, false, arrayListOf(),"", "" )
        m3.time = "1000"
        val measurements = arrayListOf(m1,m2,m3)

        val calculator = Calculator(measurements, true)
        Assert.assertEquals(100, calculator.duration)
    }

    @Test
    fun upper_1b() {
        // As for 1a
        // Additional measurement within range, after deviation

        var m1 = Measurement(5f, false, false, arrayListOf(),"", "" )
        m1.time = "0800"
        var m2 = Measurement(7f, false, false, arrayListOf(),"", "" )
        m2.time = "0900"
        var m3 = Measurement(5f, false, false, arrayListOf(),"", "" )
        m3.time = "1000"
        var m4 = Measurement(5f, false, false, arrayListOf(),"", "" )
        m4.time = "1100"
        val measurements = arrayListOf(m1,m2,m3,m4)

        val calculator = Calculator(measurements, true)
        Assert.assertEquals(100, calculator.duration)
    }

    @Test
    fun upper_1c() {
        // As for 1a
        // Additional measurement within range, before deviation

        var m1 = Measurement(5f, false, false, arrayListOf(),"", "" )
        m1.time = "0800"
        var m2 = Measurement(5f, false, false, arrayListOf(),"", "" )
        m2.time = "0900"
        var m3 = Measurement(7f, false, false, arrayListOf(),"", "" )
        m3.time = "1000"
        var m4 = Measurement(5f, false, false, arrayListOf(),"", "" )
        m4.time = "1100"
        val measurements = arrayListOf(m1,m2,m3,m4)

        val calculator = Calculator(measurements, true)
        Assert.assertEquals(100, calculator.duration)
    }

    @Test
    fun upper_2() {
    // Deviation starts and ends on upper boundary line (=6)

        var m1 = Measurement(6f, false, false, arrayListOf(),"", "" )
        m1.time = "0800"
        var m2 = Measurement(7f, false, false, arrayListOf(),"", "" )
        m2.time = "0900"
        var m3 = Measurement(6f, false, false, arrayListOf(),"", "" )
        m3.time = "1000"

        val measurements = arrayListOf(m1,m2,m3)

        val calculator = Calculator(measurements, true)
        Assert.assertEquals(200, calculator.duration)
    }

    @Test
    fun upper_3() {
    // Only 1 measurement given

        var m1 = Measurement(6f, false, false, arrayListOf(),"", "" )
        m1.time = "0800"
        val measurements = arrayListOf(m1)

        val calculator = Calculator(measurements, true)
        Assert.assertEquals(0, calculator.duration)
    }

    @Test
    fun upper_4() {
        // As for 1c
        // Last measurement is out of range

        var m1 = Measurement(5f, false, false, arrayListOf(),"", "" )
        m1.time = "0800"
        var m2 = Measurement(5f, false, false, arrayListOf(),"", "" )
        m2.time = "0900"
        var m3 = Measurement(7f, false, false, arrayListOf(),"", "" )
        m3.time = "1000"
        var m4 = Measurement(5f, false, false, arrayListOf(),"", "" )
        m4.time = "1100"
        var m5 = Measurement(7f, false, false, arrayListOf(),"", "" )
        m5.time = "1200"
        val measurements = arrayListOf(m1,m2,m3,m4,m5)

        val calculator = Calculator(measurements, true)
        Assert.assertEquals(150, calculator.duration)
    }

        // First measurement outside range

    @Test
    fun upper_4a() {
    // First measurement is outside of range
    // Following measurements are inside of range

        var m1 = Measurement(7f, false, false, arrayListOf(),"", "" )
        m1.time = "0800"
        var m2 = Measurement(5f, false, false, arrayListOf(),"", "" )
        m2.time = "0900"
        var m3 = Measurement(5f, false, false, arrayListOf(),"", "" )
        m3.time = "1000"
        val measurements = arrayListOf(m1,m2,m3)

        val calculator = Calculator(measurements, true)
        Assert.assertEquals(50, calculator.duration)

    }

    @Test
    fun upper_4b() {
    // First measurement is outside of range
    // Second measurement inside range
    // Additional deviation
    // Ends within range

        var m1 = Measurement(7f, false, false, arrayListOf(),"", "" )
        m1.time = "0800"
        var m2 = Measurement(5f, false, false, arrayListOf(),"", "" )
        m2.time = "0900"
        var m3 = Measurement(7f, false, false, arrayListOf(),"", "" )
        m3.time = "1000"
        var m4 = Measurement(5f, false, false, arrayListOf(),"", "" )
        m4.time = "1100"
        val measurements = arrayListOf(m1,m2,m3,m4)

        val calculator = Calculator(measurements, true)
        Assert.assertEquals(150, calculator.duration)

    }

    @Test
    fun upper_4c() {
    // As for 4b except:
    // More measurements between deviations

        var m1 = Measurement(7f, false, false, arrayListOf(),"", "" )
        m1.time = "0800"
        var m2 = Measurement(5f, false, false, arrayListOf(),"", "" )
        m2.time = "0900"
        var m3 = Measurement(5f, false, false, arrayListOf(),"", "" )
        m3.time = "1000"
        var m4 = Measurement(5f, false, false, arrayListOf(),"", "" )
        m4.time = "1100"
        var m5 = Measurement(7f, false, false, arrayListOf(),"", "" )
        m5.time = "1200"
        var m6 = Measurement(5f, false, false, arrayListOf(),"", "" )
        m6.time = "1300"
        val measurements = arrayListOf(m1,m2,m3,m4,m5,m6)

        val calculator = Calculator(measurements, true)
        Assert.assertEquals(150, calculator.duration)

    }

    @Test
    fun upper_4d() {
    // As for 4a except:
    // Last measurement out of range

        var m1 = Measurement(7f, false, false, arrayListOf(),"", "" )
        m1.time = "0800"
        var m2 = Measurement(5f, false, false, arrayListOf(),"", "" )
        m2.time = "0900"
        var m3 = Measurement(5f, false, false, arrayListOf(),"", "" )
        m3.time = "1000"
        var m4 = Measurement(7f, false, false, arrayListOf(),"", "" )
        m4.time = "1100"
        val measurements = arrayListOf(m1,m2,m3,m4)

        val calculator = Calculator(measurements, true)
        Assert.assertEquals(100, calculator.duration)

    }

    @Test
    fun upper_4e() {
        // As for 4d except:
        // Additional deviation between first and last

        var m1 = Measurement(7f, false, false, arrayListOf(),"", "" )
        m1.time = "0800"
        var m2 = Measurement(5f, false, false, arrayListOf(),"", "" )
        m2.time = "0900"
        var m3 = Measurement(7f, false, false, arrayListOf(),"", "" )
        m3.time = "1000"
        var m4 = Measurement(5f, false, false, arrayListOf(),"", "" )
        m4.time = "1100"
        var m5 = Measurement(7f, false, false, arrayListOf(),"", "" )
        m5.time = "1200"
        val measurements = arrayListOf(m1,m2,m3,m4,m5)

        val calculator = Calculator(measurements, true)
        Assert.assertEquals(200, calculator.duration)

    }

    @Test
    fun upper_5() {
    // All within range

        var m1 = Measurement(5f, false, false, arrayListOf(),"", "" )
        m1.time = "0800"
        var m2 = Measurement(5f, false, false, arrayListOf(),"", "" )
        m2.time = "0900"
        var m3 = Measurement(5f, false, false, arrayListOf(),"", "" )
        m3.time = "1000"
        val measurements = arrayListOf(m1,m2,m3)

        val calculator = Calculator(measurements, true)
        Assert.assertEquals(0, calculator.duration)

    }

    @Test
    fun upper_6() {
    // All outside of range

        var m1 = Measurement(7f, false, false, arrayListOf(),"", "" )
        m1.time = "0800"
        var m2 = Measurement(7f, false, false, arrayListOf(),"", "" )
        m2.time = "0900"
        var m3 = Measurement(7f, false, false, arrayListOf(),"", "" )
        m3.time = "1000"
        val measurements = arrayListOf(m1,m2,m3)

        val calculator = Calculator(measurements, true)
        Assert.assertEquals(0, calculator.duration)

    }



    // Lower Boundary Duration Tests:
    // Symmetrical to upper boundary tests except using lower boundary

        // First measurement inside range

    @Test
    fun lower_1a() {
        // BGC starts inside desired range
        // Decreases below range
        // Increases back up to within desired range

        var m1 = Measurement(5f, false, false, arrayListOf(),"", "" )
        m1.time = "0800"
        var m2 = Measurement(3f, false, false, arrayListOf(),"", "" )
        m2.time = "0900"
        var m3 = Measurement(5f, false, false, arrayListOf(),"", "" )
        m3.time = "1000"
        val measurements = arrayListOf(m1,m2,m3)

        val calculator = Calculator(measurements, true)
        Assert.assertEquals(100, calculator.duration)
    }

    @Test
    fun lower_1b() {
        // As for 1a
        // Additional measurement within range, after deviation

        var m1 = Measurement(5f, false, false, arrayListOf(),"", "" )
        m1.time = "0800"
        var m2 = Measurement(3f, false, false, arrayListOf(),"", "" )
        m2.time = "0900"
        var m3 = Measurement(5f, false, false, arrayListOf(),"", "" )
        m3.time = "1000"
        var m4 = Measurement(5f, false, false, arrayListOf(),"", "" )
        m4.time = "1100"
        val measurements = arrayListOf(m1,m2,m3,m4)

        val calculator = Calculator(measurements, true)
        Assert.assertEquals(100, calculator.duration)
    }

    @Test
    fun lower_1c() {
        // As for 1a
        // Additional measurement within range, before deviation

        var m1 = Measurement(5f, false, false, arrayListOf(),"", "" )
        m1.time = "0800"
        var m2 = Measurement(5f, false, false, arrayListOf(),"", "" )
        m2.time = "0900"
        var m3 = Measurement(3f, false, false, arrayListOf(),"", "" )
        m3.time = "1000"
        var m4 = Measurement(5f, false, false, arrayListOf(),"", "" )
        m4.time = "1100"
        val measurements = arrayListOf(m1,m2,m3,m4)

        val calculator = Calculator(measurements, true)
        Assert.assertEquals(100, calculator.duration)
    }

    @Test
    fun lower_2() {
        // Deviation starts and ends on lower boundary line (=4)

        var m1 = Measurement(4f, false, false, arrayListOf(),"", "" )
        m1.time = "0800"
        var m2 = Measurement(3f, false, false, arrayListOf(),"", "" )
        m2.time = "0900"
        var m3 = Measurement(4f, false, false, arrayListOf(),"", "" )
        m3.time = "1000"

        val measurements = arrayListOf(m1,m2,m3)

        val calculator = Calculator(measurements, true)
        Assert.assertEquals(200, calculator.duration)
    }

    @Test
    fun lower_3() {
        // Only 1 measurement given on boundary line

        var m1 = Measurement(4f, false, false, arrayListOf(),"", "" )
        m1.time = "0800"
        val measurements = arrayListOf(m1)

        val calculator = Calculator(measurements, true)
        Assert.assertEquals(0, calculator.duration)
    }

    @Test
    fun lower_4() {
        // As for 1c
        // Last measurement is out of range

        var m1 = Measurement(5f, false, false, arrayListOf(),"", "" )
        m1.time = "0800"
        var m2 = Measurement(5f, false, false, arrayListOf(),"", "" )
        m2.time = "0900"
        var m3 = Measurement(3f, false, false, arrayListOf(),"", "" )
        m3.time = "1000"
        var m4 = Measurement(5f, false, false, arrayListOf(),"", "" )
        m4.time = "1100"
        var m5 = Measurement(3f, false, false, arrayListOf(),"", "" )
        m5.time = "1200"
        val measurements = arrayListOf(m1,m2,m3,m4,m5)

        val calculator = Calculator(measurements, true)
        Assert.assertEquals(150, calculator.duration)
    }

    // First measurement outside range

    @Test
    fun lower_4a() {
        // First measurement is outside of range
        // Following measurements are inside of range

        var m1 = Measurement(3f, false, false, arrayListOf(),"", "" )
        m1.time = "0800"
        var m2 = Measurement(5f, false, false, arrayListOf(),"", "" )
        m2.time = "0900"
        var m3 = Measurement(5f, false, false, arrayListOf(),"", "" )
        m3.time = "1000"
        val measurements = arrayListOf(m1,m2,m3)

        val calculator = Calculator(measurements, true)
        Assert.assertEquals(50, calculator.duration)

    }

    @Test
    fun lower_4b() {
        // First measurement is outside of range
        // Second measurement inside range
        // Additional deviation
        // Ends within range

        var m1 = Measurement(3f, false, false, arrayListOf(),"", "" )
        m1.time = "0800"
        var m2 = Measurement(5f, false, false, arrayListOf(),"", "" )
        m2.time = "0900"
        var m3 = Measurement(3f, false, false, arrayListOf(),"", "" )
        m3.time = "1000"
        var m4 = Measurement(5f, false, false, arrayListOf(),"", "" )
        m4.time = "1100"
        val measurements = arrayListOf(m1,m2,m3,m4)

        val calculator = Calculator(measurements, true)
        Assert.assertEquals(150, calculator.duration)

    }

    @Test
    fun lower_4c() {
        // As for 4b except:
        // More measurements between deviations

        var m1 = Measurement(3f, false, false, arrayListOf(),"", "" )
        m1.time = "0800"
        var m2 = Measurement(5f, false, false, arrayListOf(),"", "" )
        m2.time = "0900"
        var m3 = Measurement(5f, false, false, arrayListOf(),"", "" )
        m3.time = "1000"
        var m4 = Measurement(5f, false, false, arrayListOf(),"", "" )
        m4.time = "1100"
        var m5 = Measurement(3f, false, false, arrayListOf(),"", "" )
        m5.time = "1200"
        var m6 = Measurement(5f, false, false, arrayListOf(),"", "" )
        m6.time = "1300"
        val measurements = arrayListOf(m1,m2,m3,m4,m5,m6)

        val calculator = Calculator(measurements, true)
        Assert.assertEquals(150, calculator.duration)

    }

    @Test
    fun lower_4d() {
        // As for 4a except:
        // Last measurement out of range

        var m1 = Measurement(3f, false, false, arrayListOf(),"", "" )
        m1.time = "0800"
        var m2 = Measurement(5f, false, false, arrayListOf(),"", "" )
        m2.time = "0900"
        var m3 = Measurement(5f, false, false, arrayListOf(),"", "" )
        m3.time = "1000"
        var m4 = Measurement(3f, false, false, arrayListOf(),"", "" )
        m4.time = "1100"
        val measurements = arrayListOf(m1,m2,m3,m4)

        val calculator = Calculator(measurements, true)
        Assert.assertEquals(100, calculator.duration)

    }

    @Test
    fun lower_4e() {
        // As for 4d except:
        // Additional deviation between first and last

        var m1 = Measurement(3f, false, false, arrayListOf(),"", "" )
        m1.time = "0800"
        var m2 = Measurement(5f, false, false, arrayListOf(),"", "" )
        m2.time = "0900"
        var m3 = Measurement(3f, false, false, arrayListOf(),"", "" )
        m3.time = "1000"
        var m4 = Measurement(5f, false, false, arrayListOf(),"", "" )
        m4.time = "1100"
        var m5 = Measurement(3f, false, false, arrayListOf(),"", "" )
        m5.time = "1200"
        val measurements = arrayListOf(m1,m2,m3,m4,m5)

        val calculator = Calculator(measurements, true)
        Assert.assertEquals(200, calculator.duration)

    }

    @Test
    fun lower_5() {
    // All within range

        var m1 = Measurement(5f, false, false, arrayListOf(),"", "" )
        m1.time = "0800"
        var m2 = Measurement(5f, false, false, arrayListOf(),"", "" )
        m2.time = "0900"
        var m3 = Measurement(5f, false, false, arrayListOf(),"", "" )
        m3.time = "1000"
        val measurements = arrayListOf(m1,m2,m3)

        val calculator = Calculator(measurements, true)
        Assert.assertEquals(0, calculator.duration)

    }

    @Test
    fun lower_6() {
        // All outside of range

        var m1 = Measurement(3f, false, false, arrayListOf(),"", "" )
        m1.time = "0800"
        var m2 = Measurement(3f, false, false, arrayListOf(),"", "" )
        m2.time = "0900"
        var m3 = Measurement(3f, false, false, arrayListOf(),"", "" )
        m3.time = "1000"
        val measurements = arrayListOf(m1,m2,m3)

        val calculator = Calculator(measurements, true)
        Assert.assertEquals(0, calculator.duration)

    }


    // Upper+Lower Combined Tests:

    @Test
    fun combined_1a() {
    // Start within range
    // Deviation above range
    // Return to range
    // Deviation below range
    // End within range

        var m1 = Measurement(5f, false, false, arrayListOf(),"", "" )
        m1.time = "0800"
        var m2 = Measurement(7f, false, false, arrayListOf(),"", "" )
        m2.time = "0900"
        var m3 = Measurement(5f, false, false, arrayListOf(),"", "" )
        m3.time = "1000"
        var m4 = Measurement(3f, false, false, arrayListOf(),"", "" )
        m4.time = "1100"
        var m5 = Measurement(5f, false, false, arrayListOf(),"", "" )
        m5.time = "1200"
        val measurements = arrayListOf(m1,m2,m3,m4,m5)

        val calculator = Calculator(measurements, true)
        Assert.assertEquals(200, calculator.duration)

    }

    @Test
    fun combined_1b() {
        // Start above range
        // Return to range
        // Deviation below range
        // End within range

        var m1 = Measurement(7f, false, false, arrayListOf(),"", "" )
        m1.time = "0800"
        var m2 = Measurement(5f, false, false, arrayListOf(),"", "" )
        m2.time = "0900"
        var m3 = Measurement(3f, false, false, arrayListOf(),"", "" )
        m3.time = "1000"
        var m4 = Measurement(5f, false, false, arrayListOf(),"", "" )
        m4.time = "1100"
        val measurements = arrayListOf(m1,m2,m3,m4)

        val calculator = Calculator(measurements, true)
        Assert.assertEquals(150, calculator.duration)

    }

    @Test
    fun combined_1c() {
        // Start below range
        // Return to range
        // Deviation above range
        // End within range

        var m1 = Measurement(3f, false, false, arrayListOf(),"", "" )
        m1.time = "0800"
        var m2 = Measurement(5f, false, false, arrayListOf(),"", "" )
        m2.time = "0900"
        var m3 = Measurement(7f, false, false, arrayListOf(),"", "" )
        m3.time = "1000"
        var m4 = Measurement(5f, false, false, arrayListOf(),"", "" )
        m4.time = "1100"
        val measurements = arrayListOf(m1,m2,m3,m4)

        val calculator = Calculator(measurements, true)
        Assert.assertEquals(150, calculator.duration)

    }

    @Test
    fun combined_2() {
        // Start within range
        // Deviation above range
        // Deviation below range
        // End within range

        var m1 = Measurement(5f, false, false, arrayListOf(),"", "" )
        m1.time = "0800"
        var m2 = Measurement(7f, false, false, arrayListOf(),"", "" )
        m2.time = "0900"
        var m3 = Measurement(3f, false, false, arrayListOf(),"", "" )
        m3.time = "1000"
        var m4 = Measurement(5f, false, false, arrayListOf(),"", "" )
        m4.time = "1100"
        val measurements = arrayListOf(m1,m2,m3,m4)

        val calculator = Calculator(measurements, true)
        Assert.assertEquals(150, calculator.duration)

    }



    // Test PercentageSafe

    @Test
    fun percentageSafe_isCorrect_1() {
    // Deviation in middle

        var m1 = Measurement(5f, false, false, arrayListOf(),"", "" )
        m1.time = "0800"
        var m2 = Measurement(7f, false, false, arrayListOf(),"", "" )
        m2.time = "0900"
        var m3 = Measurement(5f, false, false, arrayListOf(),"", "" )
        m3.time = "1000"
        var m4 = Measurement(5f, false, false, arrayListOf(),"", "" )
        m4.time = "1100"
        val measurements = arrayListOf(m1,m2,m3,m4)

        val calculator = Calculator(measurements, true)
        Assert.assertEquals(67, calculator.percentageSafe)
    }

    @Test
    fun percentageSafe_isCorrect_2() {
    // Starts out of range
    // Deviation in middle

        var m1 = Measurement(7f, false, false, arrayListOf(),"", "" )
        m1.time = "0800"
        var m2 = Measurement(5f, false, false, arrayListOf(),"", "" )
        m2.time = "0900"
        var m3 = Measurement(7f, false, false, arrayListOf(),"", "" )
        m3.time = "1000"
        var m4 = Measurement(5f, false, false, arrayListOf(),"", "" )
        m4.time = "1100"
        val measurements = arrayListOf(m1,m2,m3,m4)

        val calculator = Calculator(measurements, true)
        Assert.assertEquals(50, calculator.percentageSafe)
    }

    @Test
    fun percentageSafe_isCorrect_3() {
    // All measurements within range

        var m1 = Measurement(5f, false, false, arrayListOf(),"", "" )
        m1.time = "0800"
        var m2 = Measurement(5f, false, false, arrayListOf(),"", "" )
        m2.time = "0900"
        var m3 = Measurement(5f, false, false, arrayListOf(),"", "" )
        m3.time = "1000"
        var m4 = Measurement(5f, false, false, arrayListOf(),"", "" )
        m4.time = "1100"
        val measurements = arrayListOf(m1,m2,m3,m4)

        val calculator = Calculator(measurements, true)
        Assert.assertEquals(100, calculator.percentageSafe)
    }

    @Test
    fun percentageSafe_isCorrect_4() {
    // All measurements above range

        var m1 = Measurement(7f, false, false, arrayListOf(),"", "" )
        m1.time = "0800"
        var m2 = Measurement(7f, false, false, arrayListOf(),"", "" )
        m2.time = "0900"
        var m3 = Measurement(7f, false, false, arrayListOf(),"", "" )
        m3.time = "1000"
        var m4 = Measurement(7f, false, false, arrayListOf(),"", "" )
        m4.time = "1100"
        val measurements = arrayListOf(m1,m2,m3,m4)

        val calculator = Calculator(measurements, true)
        Assert.assertEquals(0, calculator.percentageSafe)
    }

    @Test
    fun percentageSafe_isCorrect_5() {
        // All measurements below range

        var m1 = Measurement(3f, false, false, arrayListOf(),"", "" )
        m1.time = "0800"
        var m2 = Measurement(3f, false, false, arrayListOf(),"", "" )
        m2.time = "0900"
        var m3 = Measurement(3f, false, false, arrayListOf(),"", "" )
        m3.time = "1000"
        var m4 = Measurement(3f, false, false, arrayListOf(),"", "" )
        m4.time = "1100"
        val measurements = arrayListOf(m1,m2,m3,m4)

        val calculator = Calculator(measurements, true)
        Assert.assertEquals(0, calculator.percentageSafe)
    }



    // Test PercentAbove

    @Test
    fun percentAbove_isCorrect_1() {
    // One measurements above range

        var m1 = Measurement(5f, false, false, arrayListOf(),"", "" )
        m1.time = "0800"
        var m2 = Measurement(5f, false, false, arrayListOf(),"", "" )
        m2.time = "0900"
        var m3 = Measurement(7f, false, false, arrayListOf(),"", "" )
        m3.time = "1000"
        var m4 = Measurement(5f, false, false, arrayListOf(),"", "" )
        m4.time = "1100"
        val measurements = arrayListOf(m1,m2,m3,m4)

        val calculator = Calculator(measurements, true)
        Assert.assertEquals(25, calculator.percentAbove)
    }

    @Test
    fun percentAbove_empty_isCorrect() {

        val measurements = arrayListOf<Measurement>()
        val calculator = Calculator(measurements, true)
        Assert.assertEquals(0, calculator.percentAbove)
    }

    @Test
    fun percentAbove_none_isCorrect() {

        var m1 = Measurement(5f, false, false, arrayListOf(),"", "" )
        m1.time = "0800"
        var m2 = Measurement(5f, false, false, arrayListOf(),"", "" )
        m2.time = "0900"
        var m3 = Measurement(5f, false, false, arrayListOf(),"", "" )
        m3.time = "1000"
        var m4 = Measurement(5f, false, false, arrayListOf(),"", "" )
        m4.time = "1100"

        val measurements = arrayListOf<Measurement>(m1,m2,m3,m4)
        val calculator = Calculator(measurements, true)
        Assert.assertEquals(0, calculator.percentAbove)
    }

    @Test
    fun percentBelow_isCorrect() {
        var m1 = Measurement(5f, false, false, arrayListOf(),"", "" )
        m1.time = "0800"
        var m2 = Measurement(5f, false, false, arrayListOf(),"", "" )
        m2.time = "0900"
        var m3 = Measurement(3f, false, false, arrayListOf(),"", "" )
        m3.time = "1000"
        var m4 = Measurement(5f, false, false, arrayListOf(),"", "" )
        m4.time = "1100"
        val measurements = arrayListOf(m1,m2,m3,m4)

        val calculator = Calculator(measurements, true)
        Assert.assertEquals(25, calculator.percentBelow)
    }

    @Test
    fun percentBelow_empty_isCorrect() {

        val measurements = arrayListOf<Measurement>()
        val calculator = Calculator(measurements, true)
        Assert.assertEquals(0, calculator.percentBelow)
    }

    @Test
    fun percentBelow_none_isCorrect() {

        var m1 = Measurement(5f, false, false, arrayListOf(),"", "" )
        m1.time = "0800"
        var m2 = Measurement(5f, false, false, arrayListOf(),"", "" )
        m2.time = "0900"
        var m3 = Measurement(5f, false, false, arrayListOf(),"", "" )
        m3.time = "1000"
        var m4 = Measurement(5f, false, false, arrayListOf(),"", "" )
        m4.time = "1100"

        val measurements = arrayListOf<Measurement>(m1,m2,m3,m4)
        val calculator = Calculator(measurements, true)
        Assert.assertEquals(0, calculator.percentBelow)
    }


    // Test Score

    @Test
    fun score_Works() {

        var m1 = Measurement(5f, false, false, arrayListOf(),"", "" )
        m1.time = "0800"
        var m2 = Measurement(7f, false, false, arrayListOf(),"", "" )
        m2.time = "0900"
        var m3 = Measurement(5f, false, false, arrayListOf(),"", "" )
        m3.time = "1000"
        var m4 = Measurement(5f, false, false, arrayListOf(),"", "" )
        m4.time = "1100"

        val measurements = arrayListOf<Measurement>(m1,m2,m3,m4)
        val calculator = Calculator(measurements, true)
        println("Score : " + calculator.score)
    }

    // Test Highest

    @Test
    fun highest_isCorrect() {

        var m1 = Measurement(5f, false, false, arrayListOf(),"", "" )
        m1.time = "0800"
        var m2 = Measurement(7f, false, false, arrayListOf(),"", "" )
        m2.time = "0900"
        var m3 = Measurement(5f, false, false, arrayListOf(),"", "" )
        m3.time = "1000"
        var m4 = Measurement(5f, false, false, arrayListOf(),"", "" )
        m4.time = "1100"

        val measurements = arrayListOf<Measurement>(m1,m2,m3,m4)
        val calculator = Calculator(measurements, true)
        Assert.assertEquals(7f, calculator.highestMeasurement)
    }

    @Test
    fun lowest_isCorrect() {

        var m1 = Measurement(5f, false, false, arrayListOf(),"", "" )
        m1.time = "0800"
        var m2 = Measurement(7f, false, false, arrayListOf(),"", "" )
        m2.time = "0900"
        var m3 = Measurement(5f, false, false, arrayListOf(),"", "" )
        m3.time = "1000"
        var m4 = Measurement(5f, false, false, arrayListOf(),"", "" )
        m4.time = "1100"

        val measurements = arrayListOf<Measurement>(m1,m2,m3,m4)
        val calculator = Calculator(measurements, true)
        Assert.assertEquals(5f, calculator.lowestMeasurement)
    }



}