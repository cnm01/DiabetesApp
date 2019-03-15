package com.example.diabetesapp

import android.os.Bundle
import android.support.v4.content.ContextCompat
import android.support.v7.app.AppCompatActivity
import android.util.Log
import android.view.View
import android.widget.*
import com.example.diabetesapp.model.Measurement
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore

class MeasurementViewActivity : AppCompatActivity() {

    // UI Elements
    private var timeTextView : TextView? = null
    private var dateTextView : TextView? = null
    private var bgcTextView : TextView? = null
    private var foodTextView : TextView? = null
    private var exerciseTextView : TextView? = null
    private var timeProgressBar : ProgressBar? = null
    private var bgcProgressBar : ProgressBar? = null

    // Symptom Checkboxes
    private var dizzynessCB : CheckBox? = null
    private var fatigueCB : CheckBox? = null
    private var visionCB : CheckBox? = null
    private var shakinessCB : CheckBox? = null
    private var paleCB : CheckBox? = null
    private var palpitationsCB : CheckBox? = null
    private var confusionCB : CheckBox? = null
    private var concentrateCB : CheckBox? = null
    private var thirstCB : CheckBox? = null
    private var urineCB : CheckBox? = null
    private var nauseaCB : CheckBox? = null
    private var abdoCB : CheckBox? = null
    private var breathingCB : CheckBox? = null
    private var headacheCB : CheckBox? = null

    private var medsTextView : TextView? = null
    private var notesTextView : TextView? = null

    private var deleteButton : Button? = null

    // Data
    private var mid : String? = null
    private var measurement : Measurement? = null

    // Firebase References
    private var auth: FirebaseAuth? = null
    private var database: FirebaseFirestore? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_measurement_view)

        val b = intent.extras

        if(b != null) {
            mid = b.getString("mid")
            Log.d("SET MID :", "SUCCESSFUL")
            Log.d("MID :", mid)
        }
        else {
            Log.e("Create MeasurementViewActivity :: ", "FAILED :: no measurement supplied")
            Toast.makeText(this, "Error creating MeasurementView, no measurement supplied", Toast.LENGTH_LONG).show()
            finish()
        }

        initialise()
    }

    private fun initialise() {
        timeTextView = findViewById<View>(R.id.time_text_view) as TextView
        dateTextView = findViewById<View>(R.id.date_text_view) as TextView
        bgcTextView = findViewById<View>(R.id.bgc_text_view) as TextView
        foodTextView = findViewById<View>(R.id.food_text_view) as TextView
        exerciseTextView = findViewById<View>(R.id.exercise_text_view) as TextView
        timeProgressBar = findViewById<View>(R.id.time_progress_bar) as ProgressBar
        bgcProgressBar = findViewById<View>(R.id.bgc_progress_bar) as ProgressBar

        initCheckBoxes()

        medsTextView = findViewById<View>(R.id.medications_text_view) as TextView
        notesTextView = findViewById<View>(R.id.notes_text_view) as TextView

        deleteButton = findViewById<View>(R.id.delete_button) as Button
        deleteButton!!.setOnClickListener {

            val userId = auth!!.currentUser!!.uid

            val query = database!!
                .collection("Users")
                .document(userId)
                .collection("Measurements")
                .document(mid!!)
            query.delete()
                .addOnSuccessListener {
                    Log.d("Delete measurement", "SUCCESSFUL")
                    Toast.makeText(this, "Successfully deleted", Toast.LENGTH_LONG).show()
                    finish()
                }
                .addOnFailureListener { e ->
                    Log.w("Delete measurement", "Error deleting measurement", e)
                    Toast.makeText(this, "Error deleting measurement", Toast.LENGTH_LONG).show()
                    finish()
                }
        }

        auth = FirebaseAuth.getInstance()
        database = FirebaseFirestore.getInstance()

        fetchMeasurement()
    }

    private fun initCheckBoxes() {
        dizzynessCB = findViewById<View>(R.id.dizziness_checkbox) as CheckBox
        fatigueCB = findViewById<View>(R.id.fatigue_checkbox) as CheckBox
        visionCB = findViewById<View>(R.id.blurred_vision_checkbox) as CheckBox
        shakinessCB = findViewById<View>(R.id.shakiness_checkbox) as CheckBox
        paleCB = findViewById<View>(R.id.pale_skin_checkbox) as CheckBox
        palpitationsCB = findViewById<View>(R.id.palpitations_checkbox) as CheckBox
        confusionCB = findViewById<View>(R.id.confusion_checkbox) as CheckBox
        concentrateCB = findViewById<View>(R.id.concentration_checkbox) as CheckBox
        thirstCB = findViewById<View>(R.id.thirstiness_checkbox) as CheckBox
        urineCB = findViewById<View>(R.id.frequent_urination_checkbox) as CheckBox
        nauseaCB = findViewById<View>(R.id.nausea_checkbox) as CheckBox
        abdoCB = findViewById<View>(R.id.abdominal_pain_checkbox) as CheckBox
        breathingCB = findViewById<View>(R.id.rapid_breathing_checkbox) as CheckBox
        headacheCB = findViewById<View>(R.id.headache_checkbox) as CheckBox
    }

    private fun fetchMeasurement() {

        val userId = auth!!.currentUser!!.uid

        val query = database!!
            .collection("Users")
            .document(userId)
            .collection("Measurements")
            .document(mid!!)
        query.get()
            .addOnSuccessListener {querySnapshot ->
                Log.d("Fetch measurement", "SUCCESSFUL")
                measurement = querySnapshot.toObject(Measurement::class.java)
                setDetails()
            }
            .addOnFailureListener { e ->
                Log.w("Fetch measurement", "Error fetching measurement", e)
                Toast.makeText(this, "Error fetching measurement", Toast.LENGTH_LONG).show()
                finish()
            }

    }

    private fun setDetails() {
        timeProgressBar!!.visibility = View.INVISIBLE

        timeTextView!!.text = measurement!!.timeFormatted
        dateTextView!!.text = getString(R.string.date_format, measurement!!.day, measurement!!.month, measurement!!.year)
        setBGC()
        if(measurement!!.recentFood) {
            foodTextView!!.text = getString(R.string.recent_food)
        }
        else {
            foodTextView!!.text = getString(R.string.no_recent_food)
        }
        exerciseTextView!!.text = if(measurement!!.recentExercise) { "Recent physical activity" }
                                  else {"No recent physical activity"}
        setCheckBoxes()
        medsTextView!!.text = measurement!!.medications
        notesTextView!!.text = measurement!!.notes

    }

    private fun setBGC() {
        bgcProgressBar!!.visibility = View.INVISIBLE
        bgcTextView!!.text = getString(R.string.unit, measurement!!.bloodGlucoseConc.toString())
        if(measurement!!.bloodGlucoseConc > 6 || measurement!!.bloodGlucoseConc < 4) {
            bgcTextView!!.setTextColor(ContextCompat.getColor(this, R.color.scoreBad))
        }
        else {
            bgcTextView!!.setTextColor(ContextCompat.getColor(this, R.color.scoreGood))
        }
    }

    private fun setCheckBoxes() {
        if(measurement!!.symptoms.contains("Dizziness")) { dizzynessCB!!.isChecked = true }
        if(measurement!!.symptoms.contains("Fatigue")) { fatigueCB!!.isChecked = true }
        if(measurement!!.symptoms.contains("Blurred Vision")) { visionCB!!.isChecked = true }
        if(measurement!!.symptoms.contains("Shakiness")) { shakinessCB!!.isChecked = true }
        if(measurement!!.symptoms.contains("Pale Skin")) { paleCB!!.isChecked = true }
        if(measurement!!.symptoms.contains("Palpitations")) { palpitationsCB!!.isChecked = true }
        if(measurement!!.symptoms.contains("Confusion")) { confusionCB!!.isChecked = true }
        if(measurement!!.symptoms.contains("Difficulty Concentrating")) { concentrateCB!!.isChecked = true }
        if(measurement!!.symptoms.contains("Thirstiness")) { thirstCB!!.isChecked = true }
        if(measurement!!.symptoms.contains("Frequent Urination")) { urineCB!!.isChecked = true }
        if(measurement!!.symptoms.contains("Nausea")) { nauseaCB!!.isChecked = true }
        if(measurement!!.symptoms.contains("Abdominal Pain")) { abdoCB!!.isChecked = true }
        if(measurement!!.symptoms.contains("Rapid Breathing")) { breathingCB!!.isChecked = true }
        if(measurement!!.symptoms.contains("Headache")) { headacheCB!!.isChecked = true }

        dizzynessCB!!.isEnabled = false
        fatigueCB!!.isEnabled = false
        visionCB!!.isEnabled = false
        shakinessCB!!.isEnabled = false
        paleCB!!.isEnabled = false
        palpitationsCB!!.isEnabled = false
        confusionCB!!.isEnabled = false
        concentrateCB!!.isEnabled = false
        thirstCB!!.isEnabled = false
        urineCB!!.isEnabled = false
        nauseaCB!!.isEnabled = false
        abdoCB!!.isEnabled = false
        breathingCB!!.isEnabled = false
        headacheCB!!.isEnabled = false
    }


}
