package com.example.diabetesapp

import android.content.Context
import android.os.Bundle
import android.support.v7.app.AppCompatActivity
import android.support.v7.widget.SwitchCompat
import android.util.Log
import android.view.View
import android.view.inputmethod.InputMethodManager
import android.widget.*
import com.example.diabetesapp.model.Measurement
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore

class NewMeasurementActivity : AppCompatActivity() {

    private val bloodGlucoseValues = ArrayList<String>()

    // UI Elements
    private var bloodGlucoseSpinner: Spinner? = null
    private var foodConsumedSwitch: SwitchCompat? = null
    private var exerciseSwitch: SwitchCompat? = null
    private var progressBar: ProgressBar? = null

    // Symptom Checkboxes
    private var dizzinesCheckbox: CheckBox? = null
    private var fatigueCheckBox: CheckBox? = null
    private var blurredVisionCheckbox: CheckBox? = null
    private var shakinessCheckbox: CheckBox? = null
    private var paleSkinCheckBox: CheckBox? = null
    private var palpitationsCheckBox: CheckBox? = null
    private var confusionCheckBox: CheckBox? = null
    private var concentratingCheckBox: CheckBox? = null
    private var thirstCheckBox: CheckBox? = null
    private var urinationCheckBox: CheckBox? = null
    private var nauseaCheckBox: CheckBox? = null
    private var abdominalCheckBox: CheckBox? = null
    private var breathingCheckBox: CheckBox? = null
    private var headacheCheckBox: CheckBox? = null

    private var medicationsEditText: EditText? = null
    private var notesEditText: EditText? = null
    private var submitButton: Button? = null

    // Firebase References
    private var auth: FirebaseAuth? = null
    private var database: FirebaseFirestore? = null

    // Data Values
    private var bloodGlucoseConc: Float? = null
    private var foodRecentlyConsumed: Boolean? = false
    private var recentExercise: Boolean? = false
    private var symptoms = arrayListOf<String>()
    private var medications: String? = null
    private var notes: String? = null









    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_new_measurement)

        initialise()

    }


    private fun initialise() {
        populateBloodGlucoseValue()
        bloodGlucoseSpinner = findViewById<View>(R.id.blood_glucose_spinner) as Spinner
        initBloodSpinner()
        foodConsumedSwitch = findViewById<View>(R.id.food_switch) as SwitchCompat
        exerciseSwitch = findViewById<View>(R.id.exercise_switch) as SwitchCompat
        initCheckboxes()
        medicationsEditText = findViewById<View>(R.id.medications_edit_text) as EditText
        notesEditText = findViewById<View>(R.id.notes_edit_text) as EditText
        submitButton = findViewById<View>(R.id.submit_button)as Button
        progressBar = findViewById<View>(R.id.progressBar) as ProgressBar
        auth = FirebaseAuth.getInstance()
        database = FirebaseFirestore.getInstance()
        initSubmitButtonListener()
        initFoodSwitch()
        initExerciseSwitch()


    }

    private fun populateBloodGlucoseValue() {
        val unit = " mmol/L"
        var concentration = 0.0f
        while(concentration <= 50.1f) {
            bloodGlucoseValues += "%.1f".format(concentration)  + unit
            concentration += 0.1f
        }
    }

    private fun initBloodSpinner() {
        val bloodGlucoseAdapter = ArrayAdapter(this, android.R.layout.simple_spinner_item, bloodGlucoseValues)
        bloodGlucoseAdapter.setDropDownViewResource((android.R.layout.simple_dropdown_item_1line))
        bloodGlucoseSpinner!!.adapter = bloodGlucoseAdapter

        bloodGlucoseSpinner!!.onItemSelectedListener = object : AdapterView.OnItemSelectedListener {

            override fun onNothingSelected(parent: AdapterView<*>?) {
            }

            override fun onItemSelected(parent: AdapterView<*>?, view: View?, position: Int, id: Long) {
                val item = bloodGlucoseAdapter.getItem(position)
                var spinnerStr = item.toString()
                spinnerStr = spinnerStr.replace(" mmol/L", "")
                bloodGlucoseConc = spinnerStr.toFloat()
            }

        }
        bloodGlucoseSpinner!!.setSelection(50)
    }

    private fun initCheckboxes() {
        dizzinesCheckbox = findViewById<View>(R.id.dizziness_checkbox) as CheckBox
        fatigueCheckBox = findViewById<View>(R.id.fatigue_checkbox) as CheckBox
        blurredVisionCheckbox = findViewById<View>(R.id.blurred_vision_checkbox) as CheckBox
        shakinessCheckbox = findViewById<View>(R.id.shakiness_checkbox) as CheckBox
        paleSkinCheckBox = findViewById<View>(R.id.pale_skin_checkbox) as CheckBox
        palpitationsCheckBox = findViewById<View>(R.id.palpitations_checkbox) as CheckBox
        confusionCheckBox = findViewById<View>(R.id.confusion_checkbox) as CheckBox
        concentratingCheckBox = findViewById<View>(R.id.concentration_checkbox) as CheckBox
        thirstCheckBox = findViewById<View>(R.id.thirstiness_checkbox) as CheckBox
        urinationCheckBox = findViewById<View>(R.id.frequent_urination_checkbox) as CheckBox
        nauseaCheckBox = findViewById<View>(R.id.nausea_checkbox) as CheckBox
        abdominalCheckBox = findViewById<View>(R.id.abdominal_pain_checkbox) as CheckBox
        breathingCheckBox = findViewById<View>(R.id.rapid_breathing_checkbox) as CheckBox
        headacheCheckBox = findViewById<View>(R.id.headache_checkbox) as CheckBox
    }

    private fun initFoodSwitch() {
        foodConsumedSwitch!!.setOnCheckedChangeListener{ _, isChecked ->
            if (isChecked) {
                foodRecentlyConsumed = true
            }

        }
    }

    private fun initExerciseSwitch() {
        exerciseSwitch!!.setOnCheckedChangeListener{ _, isChecked ->
            if (isChecked) {
                recentExercise = true
            }

        }
    }

    private fun assignSymptoms() {
        symptoms.clear()
        if(dizzinesCheckbox!!.isChecked) {symptoms.add("Dizziness")}
        if(fatigueCheckBox!!.isChecked) {symptoms.add("Fatigue")}
        if(blurredVisionCheckbox!!.isChecked) {symptoms.add("Blurred Vision")}
        if(shakinessCheckbox!!.isChecked) {symptoms.add("Shakiness")}
        if(paleSkinCheckBox!!.isChecked) {symptoms.add("Pale Skin")}
        if(palpitationsCheckBox!!.isChecked) {symptoms.add("Palpitations")}
        if(confusionCheckBox!!.isChecked) {symptoms.add("Confusion")}
        if(concentratingCheckBox!!.isChecked) {symptoms.add("Difficulty Concentrating")}
        if(thirstCheckBox!!.isChecked) {symptoms.add("Thirstiness")}
        if(urinationCheckBox!!.isChecked) {symptoms.add("Frequent Urination")}
        if(nauseaCheckBox!!.isChecked) {symptoms.add("Nausea")}
        if(abdominalCheckBox!!.isChecked) {symptoms.add("Abdominal Pain")}
        if(breathingCheckBox!!.isChecked) {symptoms.add("Rapid Breathing")}
        if(headacheCheckBox!!.isChecked) {symptoms.add("Headache")}
    }

    private fun assignMedications() {
        medications = medicationsEditText!!.text.toString()
    }
    private fun assignNotes() {
        notes = notesEditText!!.text.toString()
    }

    private fun initSubmitButtonListener() {

        submitButton!!.setOnClickListener {

            closeKeyboard()
            progressBar?.visibility = View.VISIBLE

            assignSymptoms()
            assignMedications()
            assignNotes()

            var measurement = Measurement(bloodGlucoseConc!!,
                foodRecentlyConsumed!!,
                recentExercise!!,
                symptoms,
                medications!!,
                notes!!)

            val userId = auth!!.currentUser!!.uid
            database!!.collection("Users")
                        .document(userId)
                            .collection("Measurements")
                                .add(measurement)
                                    .addOnSuccessListener {
                                        Log.d("Add Measurement", "Measurement written successfully")
                                        Toast.makeText(this, "Success", Toast.LENGTH_LONG).show()
                                        progressBar?.visibility = View.INVISIBLE
                                        finish()

                                    }
                                    .addOnFailureListener {
                                        Log.w("Add Measurement", "Failed to write new measurement object to Firestore")
                                        Toast.makeText(this, "Submit Unsuccessful", Toast.LENGTH_LONG).show()
                                    }










        }
    }

    private fun closeKeyboard() {
        try {
            val inputManager: InputMethodManager =getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager
            inputManager.hideSoftInputFromWindow(currentFocus.windowToken, InputMethodManager.SHOW_FORCED)
        } catch (e: IllegalStateException) {
            Log.d("Close Keyboard", "Keyboard already closed")
        }
    }


}

