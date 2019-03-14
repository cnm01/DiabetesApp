package com.example.diabetesapp.model

import kotlin.math.roundToInt

class Calculator(measurements : ArrayList<Measurement>) {

    // Safe Range
    val lowerLimit = 4f
    val upperLimit = 9f

    // List of measurements to calculate score for
    var measurements : ArrayList<Measurement> = arrayListOf()

    var duration : Int = 0                  // Duration of time spent outside of desired range
    var highestMeasurement : Float = 0f     // BGC of highest measurement
    var lowestMeasurement : Float = 0f      // BGC of lowest measurement

    var percentageSafe : Int = 0            // Percentage of time spent inside of safe range
    var percentAbove : Int = 0              // Percentage of measurements above range
    var percentBelow : Int = 0              // Percentage of measurements below range
    var numMeasurements : Int = 0           // Total number of measurements
    var numSymptoms : Int = 0               // Total number of symptoms out of all measurements
    var upperDifference : Float = 0f        // Difference between upperLimit and highest BGC
    var lowerDifference : Float = 0f        // Difference between lowerLimit and lowest BGC

    var score : Int = 0

    init {
        this.measurements = measurements
        calcDurationUpper()
        calcDurationLower()
        calcPercentageSafe()
        calcPercentAbove()
        calcPercentBelow()
        setNumMeasurements()
        setNumSymptoms()
        setHighest()
        setLowest()
        setUpperDifference()
        setLowerDifference()

        setScore()
    }

    private fun setHighest() {
    // Sets the BGC of the highest measurement

        if(measurements.size > 2) {
            var temp = measurements[0]

            for(m in measurements) {
                if(m.bloodGlucoseConc > temp.bloodGlucoseConc) {
                    temp = m
                }
            }
            highestMeasurement = temp.bloodGlucoseConc
        }
    }

    private fun setLowest() {
    // Sets the BGC of the lowest measurement

        if(measurements.size > 2) {
            var temp = measurements[0]

            for(m in measurements) {
                if(m.bloodGlucoseConc < temp.bloodGlucoseConc) {
                    temp = m
                }
            }
            lowestMeasurement = temp.bloodGlucoseConc
        }
    }

    private fun setUpperDifference() {
    // Sets the difference between the highest measurement BGC
    //  and the upperLimit

        upperDifference = if(highestMeasurement > upperLimit) {
            (highestMeasurement - upperDifference)
        }
        else {
            0f
        }
    }

    private fun setLowerDifference() {
    // Sets the difference between the lowest measurement BGC
    //  and the lowerLimit

        lowerDifference = if(lowestMeasurement < lowerLimit) {
            (lowerLimit - lowestMeasurement)
        }
        else {
            0f
        }
    }

    private fun calcDurationUpper() {
    // Calculates the total time spent above the safe range
    //  and adds this to duration

        if(measurements.size < 2) { return }

        var cur = measurements[0]
        var next = measurements[1]
        val intercepts = arrayListOf<Float>()

        // Iterate through measurements to find all times where BGC crosses upperLimit
        do {
            //If current measurement and next measurement intercept upperLimit
            if(upperLimit.toInt() in cur.bloodGlucoseConc.toInt()..next.bloodGlucoseConc.toInt() || upperLimit.toInt() in next.bloodGlucoseConc.toInt()..cur.bloodGlucoseConc.toInt() ) {
                // Uses equation (y1 - y2) = m(x1 - x2)
                //  in the form: x1 = (y1 - y2 + mx2)/m
                val m = (cur.bloodGlucoseConc-next.bloodGlucoseConc)/(cur.time!!.toInt() - next.time!!.toInt())
                val intercept = (upperLimit.toInt()-cur.bloodGlucoseConc+m*cur.time!!.toInt())/m
                // Adds x value (time) of measurement to intercepts
                intercepts.add(intercept)
            }
            try {
                cur = next
                next = measurements[measurements.indexOf(next)+1]
            } catch (e : Exception) {
                break
            }

        } while (measurements.indexOf(next) < measurements.size)

        var total = 0

        if(intercepts.isEmpty()) { return }

        // If first measurement is inside range
        //  -> first intercept is BGC increasing past upperLimit
        if(measurements[0].bloodGlucoseConc <= upperLimit.toInt()) {
            for(m in intercepts) {
                val i = intercepts.indexOf(m)
                if (i % 2 == 0) {
                    if (i + 1 < intercepts.size) {
                        val time = intercepts[i + 1] - m
                        total += time.toInt()
                    }
                }
            }
        }
        // If first measurement is outside range
        //  -> first intercept is BGC decreasing below upperLimit
        else {
            total += intercepts[0].toInt() - measurements[0].time!!.toInt()

            for(m in intercepts) {
                val i = intercepts.indexOf(m)
                if (i % 2 == 1) {
                    if (i + 1 < intercepts.size) {
                        val time = intercepts[i + 1] - m
                        total += time.toInt()
                    }
                }
            }
        }
        // If last measurement is above upperLimit
        //  -> last intercept is BGC increasing above upperLimit
        if(measurements.last().bloodGlucoseConc > upperLimit.toInt()) {
            total += measurements.last().time!!.toInt() - intercepts.last().toInt()
        }

        duration += total

    }

    private fun calcDurationLower() {
    // Calculates the total time spent below the safe range
    //  and adds this to duration

        if(measurements.size < 2) { return }

        var cur = measurements[0]
        var next = measurements[1]
        val intercepts = arrayListOf<Float>()

        // Iterate through measurements to find all time where BGC crosses lowerLimit
        do {
            // If current measurement and next measurement intercept lowerLimit
            if(lowerLimit.toInt() in cur.bloodGlucoseConc.toInt()..next.bloodGlucoseConc.toInt() || lowerLimit.toInt() in next.bloodGlucoseConc.toInt()..cur.bloodGlucoseConc.toInt() ) {
                // Uses equation (y1 - y2) = m(x1 - x2)
                //  in the form x1 = (y1 - y2 + mx2)/m
                val m = (cur.bloodGlucoseConc-next.bloodGlucoseConc)/(cur.time!!.toInt() - next.time!!.toInt())
                val intercept = (lowerLimit.toInt()-cur.bloodGlucoseConc+m*cur.time!!.toInt())/m
                // Adds x value (time) of measurement to intercepts
                intercepts.add(intercept)
            }
            try {
                cur = next
                next = measurements[measurements.indexOf(next)+1]
            } catch (e : Exception) {
                break
            }
        } while (measurements.indexOf(next) < measurements.size)

        var total = 0

        if(intercepts.isEmpty()) { return }

        // If first measurement is inside range
        //  -> first intercept is BGC decreasing below lowerLimit
        if(measurements[0].bloodGlucoseConc >= lowerLimit.toInt()) {
            for(m in intercepts) {
                val i = intercepts.indexOf(m)
                if (i % 2 == 0) {
                    if (i + 1 < intercepts.size) {
                        val time = intercepts[i + 1] - m
                        total += time.toInt()
                    }
                }
            }
        }
        // If first measurement is outside range
        //  -> first intercept is BGC increasing above lowerLimit
        else {
            total += intercepts[0].toInt() - measurements[0].time!!.toInt()

            for(m in intercepts) {
                val i = intercepts.indexOf(m)
                if (i % 2 == 1) {
                    if (i + 1 < intercepts.size) {
                        val time = intercepts[i + 1] - m
                        total += time.toInt()
                    }
                }
            }
        }
        // If last measurement is below lowerLimit
        //  -> last intercept is BGC decreasing below lowerLimit
        if(measurements.last().bloodGlucoseConc < lowerLimit.toInt()) {
            println("Last m outside range")
            total += measurements.last().time!!.toInt() - intercepts.last().toInt()
        }

        duration += total

    }

    private fun calcPercentageSafe() {
    // Calculates the percentage of time that is spent within the safe range

        when {
            measurements.size == 0 -> { percentageSafe = 0 }
            measurements.size == 1 -> {
                if(measurements[0].bloodGlucoseConc in lowerLimit..upperLimit) {
                    percentageSafe = 100
                }
            }
            else -> {

                val totalDuration = measurements.last().time!!.toFloat() - measurements.first().time!!.toFloat()
                percentageSafe = if (totalDuration > 0) {
                    100 - ((duration/totalDuration)*100).roundToInt()
                } else {
                    0
                }
                // If percentageSafe = 100 then either:
                // -> all measurements within safe range
                // -> all measurements outside of safe range
                // Check if all are outside or outside range
                if(percentageSafe == 100 && measurements[0].bloodGlucoseConc  !in lowerLimit..upperLimit) {
                    percentageSafe = 0
                }

            }
        }
    }

    private fun calcPercentAbove() {
    // Calculates the percentage of measurements that are above range

        var num = 0
        for(m in measurements) {
            if(m.bloodGlucoseConc > upperLimit) {
                num++
            }
        }

        if(measurements.size > 0 && num < measurements.size) {
            percentAbove = ((num.toFloat()/measurements.size.toFloat())*100f).toInt()
        }
    }

    private fun calcPercentBelow() {
    // Calculates the percentage of measurements that are below range

        var num = 0
        for(m in measurements) {
            if(m.bloodGlucoseConc < lowerLimit) {
                num++
            }
        }

        if(measurements.size > 0 && num < measurements.size) {
            percentBelow = ((num.toFloat()/measurements.size.toFloat())*100f).toInt()
        }
    }

    private fun setNumMeasurements() {
        numMeasurements = measurements.size
    }

    private fun setNumSymptoms() {
        var num = 0
        for(m in measurements) {
            for(s in m.symptoms) {
                num++
            }
        }
        numSymptoms = num
    }

    private fun setScore() {
    // Calculates and sets a value for score

        val percentageSafeMetric = (percentageSafe.toFloat()/100)
        val percentAboveMetric = ((100-percentAbove.toFloat())/100)
        val percentBelowMetric = ((100-percentBelow.toFloat())/100)

        var numMeasurementsMetric = (numMeasurements.toFloat() * 0.2)
        numMeasurementsMetric = when {
            (numMeasurementsMetric > 1) -> { 1.0 }
            (numMeasurementsMetric < 0) -> { 0.0 }
            else -> { numMeasurementsMetric }
        }
        var numSymptomsMetric = 1-((numSymptoms.toFloat() * 0.1))
        numSymptomsMetric = when {
            (numSymptomsMetric > 1) -> { 1.0 }
            (numSymptomsMetric < 0) -> { 0.0 }
            else -> { numSymptomsMetric }
        }

        var upperDifferenceMetric = (1-(upperDifference/10))
        upperDifferenceMetric = when {
            (upperDifferenceMetric > 1) -> {1f}
            (upperDifferenceMetric < 0) -> {0f}
            else -> {upperDifferenceMetric}
        }

        var lowerDifferenceMetric = (1-(lowerDifference/10))
        lowerDifferenceMetric = when {
            (lowerDifferenceMetric > 1) -> {1f}
            (lowerDifferenceMetric < 0) -> {0f}
            else -> {lowerDifferenceMetric}
        }

        // Each metric is a value between 0..1
        // Each metric is weighted with a value /100 as below
        score += (percentageSafeMetric * 30).roundToInt()
        score += (percentAboveMetric * 10).roundToInt()
        score += (percentBelowMetric * 10).roundToInt()
        score += (numMeasurementsMetric * 20).roundToInt()
        score += (numSymptomsMetric * 10).roundToInt()
        score += (upperDifferenceMetric * 10).roundToInt()
        score += (lowerDifferenceMetric * 10).roundToInt()


        if(score > 100) {
            score = 100
        }
        else if(score < 0) {
            score = 0
        }


    }






}