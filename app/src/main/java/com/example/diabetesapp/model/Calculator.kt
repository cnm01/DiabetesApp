package com.example.diabetesapp.model

import kotlin.math.roundToInt

class Calculator(measurements : ArrayList<Measurement>) {

    var measurements : ArrayList<Measurement> = arrayListOf()

    var duration : Int = 0  // Duration of time spent outside of desired range
    var numHypos : Int = 0  // Num deviations above range
    var numHypers : Int = 0 // Num deviations below range
    var numDeviations : Int = 0 // Total num deviations outside range

    var percentageSafe : Int = 0    // Percentage of time spent inside of safe range
    var percentAbove : Int = 0  // Percentage of measurements above range
    var percentBelow : Int = 0  // Percentage of measurements below range
    var numMeasurements : Int = 0
    var numSymptoms : Int = 0

    var score : Int = 0

    init {
        this.measurements = measurements
        calcDurationUpper()
        calcDurationLower()
        calcPercentageSafe()
        calcPercentAbove()
        calcPercentBelow()
        setNumMeasurements()

        setScore()
    }

    private fun calcDurationUpper() {

        if(measurements.size < 2) { return }

        var cur = measurements[0]
        var next = measurements[1]
        var intercepts = arrayListOf<Float>()

        println("Created measurements")

        do {
            println("Executing loop")
            if(6 in cur.bloodGlucoseConc.toInt()..next.bloodGlucoseConc.toInt() || 6 in next.bloodGlucoseConc.toInt()..cur.bloodGlucoseConc.toInt() ) {
                println("Intercept in range")
                val m = (cur.bloodGlucoseConc-next.bloodGlucoseConc)/(cur.time!!.toInt() - next.time!!.toInt())
                val intercept = (6-cur.bloodGlucoseConc+m*cur.time!!.toInt())/m
                intercepts.add(intercept)
            }
            try {
                cur = next
                next = measurements[measurements.indexOf(next)+1]
            } catch (e : Exception) {
                break
            }

        } while (measurements.indexOf(next) < measurements.size)

        println(intercepts)


        var total = 0

        if(intercepts.isEmpty()) { return }

        // If first measurement is within range
        if(measurements[0].bloodGlucoseConc <= 6) {
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
        else {
            println("First m outside range")
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
        if(measurements.last().bloodGlucoseConc > 6) {
            println("Last m outside range")
            total += measurements.last().time!!.toInt() - intercepts.last().toInt()
        }


        println(total)
        duration += total

    }

    private fun calcDurationLower() {

        if(measurements.size < 2) { return }

        var cur = measurements[0]
        var next = measurements[1]
        var intercepts = arrayListOf<Float>()

        println("Created measurements")

        do {
            println("Executing loop")
            if(4 in cur.bloodGlucoseConc.toInt()..next.bloodGlucoseConc.toInt() || 4 in next.bloodGlucoseConc.toInt()..cur.bloodGlucoseConc.toInt() ) {
                println("Intercept in range")
                val m = (cur.bloodGlucoseConc-next.bloodGlucoseConc)/(cur.time!!.toInt() - next.time!!.toInt())
                val intercept = (4-cur.bloodGlucoseConc+m*cur.time!!.toInt())/m
                intercepts.add(intercept)
            }
            try {
                cur = next
                next = measurements[measurements.indexOf(next)+1]
            } catch (e : Exception) {
                break
            }

        } while (measurements.indexOf(next) < measurements.size)

        println(intercepts)


        var total = 0

        if(intercepts.isEmpty()) { return }

        // If first measurement is within range
        if(measurements[0].bloodGlucoseConc >= 4) {
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
        else {
            println("First m outside range")
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
        if(measurements.last().bloodGlucoseConc < 4) {
            println("Last m outside range")
            total += measurements.last().time!!.toInt() - intercepts.last().toInt()
        }


        println(total)
        duration += total

    }

    private fun calcPercentageSafe() {

        when {
            measurements.size == 0 -> { percentageSafe = 0 }
            measurements.size == 1 -> {
                if(measurements[0].bloodGlucoseConc in 4.0..6.0) {
                    percentageSafe = 100
                }
            }
            else -> {

                val totalDuration = measurements.last().time!!.toFloat() - measurements.first().time!!.toFloat()
                println("Duration outside safe range : " + duration)
                println("Total duration : " + totalDuration)
                percentageSafe = if (totalDuration > 0) {
                    100 - ((duration/totalDuration)*100).roundToInt()
                } else {
                    0
                }
                // If percentageSafe = 100 then either:
                // -> all measurements within safe range
                // -> all measurements outside of safe range
                if(percentageSafe == 100 && measurements[0].bloodGlucoseConc  !in 4.0..6.0) {
                    percentageSafe = 0
                }

            }
        }
    }

    private fun calcPercentAbove() {
        var num = 0
        for(m in measurements) {
            if(m.bloodGlucoseConc > 6f) {
                num++
            }
        }

        println("num_above : $num")
        println("Total size : " + measurements.size)

        if(measurements.size > 0 && num < measurements.size) {
            percentAbove = ((num.toFloat()/measurements.size.toFloat())*100f).toInt()
        }
    }

    private fun calcPercentBelow() {
        var num = 0
        for(m in measurements) {
            if(m.bloodGlucoseConc < 4f) {
                num++
            }
        }

        println("num_above : $num")
        println("Total size : " + measurements.size)

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

        score += ((percentageSafe.toFloat()/100) * 30).toInt()
        score += (((100-percentAbove.toFloat())/100) * 20).toInt()
        score += (((100-percentBelow.toFloat())/100) * 20).toInt()
        score += ((numMeasurements.toFloat() * 0.2) * 20).toInt()
        score += (1-((numSymptoms.toFloat() * 0.1)) * 10).toInt()

        // TODO refactor score algorithm based on specification

    }




}