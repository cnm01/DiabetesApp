package com.example.diabetesapp

import android.content.Intent
import android.graphics.Color
import android.os.Bundle
import android.support.constraint.ConstraintLayout
import android.support.design.widget.NavigationView
import android.support.v4.content.ContextCompat
import android.support.v4.view.GravityCompat
import android.support.v7.app.ActionBarDrawerToggle
import android.support.v7.app.AppCompatActivity
import android.util.DisplayMetrics
import android.util.Log
import android.view.Gravity
import android.view.Menu
import android.view.MenuItem
import android.view.View
import android.widget.LinearLayout
import android.widget.ProgressBar
import android.widget.TextView
import android.widget.Toast
import com.example.diabetesapp.model.*
import com.example.diabetesapp.view.HintItem
import com.example.diabetesapp.view.RecentItem
import com.github.mikephil.charting.charts.LineChart
import com.github.mikephil.charting.components.LimitLine
import com.github.mikephil.charting.components.XAxis
import com.github.mikephil.charting.data.Entry
import com.github.mikephil.charting.data.LineData
import com.github.mikephil.charting.data.LineDataSet
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.FirebaseFirestoreSettings
import com.google.firebase.firestore.Query
import kotlinx.android.synthetic.main.activity_home.*
import kotlinx.android.synthetic.main.app_bar_home.*
import kotlinx.android.synthetic.main.content_home.*
import kotlinx.android.synthetic.main.recent_item_layout.view.*
import java.time.LocalDateTime

class HomeActivity : AppCompatActivity(), NavigationView.OnNavigationItemSelectedListener {
// Acts as main activity for app : dashboard

    // Score
    private var scoreText: TextView? = null

    // Measurements
    private var measurementsArray = arrayListOf<Measurement>()

    // GraphView
    private var graph: LineChart? = null
    private var graphHolder: ConstraintLayout? = null

    // RecentItems
    private var recentLinearLayout: LinearLayout? = null

    // Hints
    private var hintsLinearLayout: LinearLayout? = null

    // Drawer elements
    private var nameTextView: TextView? = null
    private var emailTextView: TextView? = null
    private var headerView: View? = null

    // Firebase References
    private var auth: FirebaseAuth? = null
    private var database: FirebaseFirestore? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_home)
        setSupportActionBar(toolbar_home)

        new_measurement_floating_button.setOnClickListener {
            val intent = Intent(this, NewMeasurementActivity::class.java)
            startActivity(intent)
        }

        val toggle = ActionBarDrawerToggle(
            this, drawer_layout_home, toolbar_home, R.string.navigation_drawer_open, R.string.navigation_drawer_close
        )
        drawer_layout_home.addDrawerListener(toggle)
        toggle.syncState()

        nav_view_home.setNavigationItemSelectedListener(this)

        initialise()

    }

    private fun initDateSetting() {
    // Treats java.util.date objects as com.google.firebase.Timestamp objects
    // (Solves warning given in Logcat)
    // -> java.util.date behaviour will change in firestore and your app may break
    // Refactor usages as such:
    //        // Old:
    //        val date = snapshot.getDate("created_at")
    //        // New:
    //        val timestamp = snapshot.getTimestamp("created_at")
    //        val date = timestamp.toDate()
        val firestore = FirebaseFirestore.getInstance()
        val settings = FirebaseFirestoreSettings.Builder()
            .setTimestampsInSnapshotsEnabled(true)
            .build()
        firestore.firestoreSettings = settings
    }

    private fun initialise() {

        initDateSetting()

        headerView = nav_view_home.getHeaderView(0)
        nameTextView = headerView!!.findViewById<View>(R.id.name_text_view) as TextView
        emailTextView = headerView!!.findViewById<View>(R.id.email_text_view) as TextView
        val headerIMG = headerView!!.findViewById<View>(R.id.header_layout) as LinearLayout
        // Sets Navigation Drawer Header background image
        headerIMG.setBackgroundResource(R.drawable.wallpaper2)
        auth = FirebaseAuth.getInstance()
        database = FirebaseFirestore.getInstance()
        recentLinearLayout = findViewById<View>(R.id.recentLinearLayout) as LinearLayout
        graph = findViewById<View>(R.id.graphView) as LineChart
        hintsLinearLayout = findViewById<View>(R.id.hintsLinearLayout) as LinearLayout
        graphHolder = findViewById<View>(R.id.graph_holder) as ConstraintLayout
        scoreText = findViewById<View>(R.id.score_text) as TextView
    }

    private fun setNavDrawerDetails() {
    // Sets name and email in Nav Drawer Header

        val uid = auth!!.currentUser!!.uid
        val userRef = database!!.collection("Users").document(uid)
        userRef.get()
            .addOnSuccessListener { documentSnapshot ->
                val user = documentSnapshot.toObject(User::class.java)
                val name = user!!.firstName + " " + user.lastName
                val email = user.email
                nameTextView!!.text = name
                emailTextView!!.text = email
                Log.d("Fetch drawer details", "User details successfully obtained and written to drawer")
            }
            .addOnFailureListener{
                e -> Log.w("Fetch drawer details", "Error fetching user details for nav drawer", e)
            }
    }

    override fun onBackPressed() {
        if (drawer_layout_home.isDrawerOpen(GravityCompat.START)) {
            drawer_layout_home.closeDrawer(GravityCompat.START)
        } else {
            super.onBackPressed()
        }
    }

    override fun onCreateOptionsMenu(menu: Menu): Boolean {
        // Inflate the menu; this adds items to the action bar if it is present.
        menuInflater.inflate(R.menu.home, menu)
        return true
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        return when (item.itemId) {
            R.id.action_settings -> true
            else -> super.onOptionsItemSelected(item)
        }
    }

    override fun onNavigationItemSelected(item: MenuItem): Boolean {
        // Handle navigation view item clicks here.
        when (item.itemId) {
            R.id.nav_dashboard -> {
            }
            R.id.nav_day_view -> {
                val intent = Intent(this, DayViewActivity::class.java)
                intent.flags = Intent.FLAG_ACTIVITY_REORDER_TO_FRONT
                startActivity(intent)
            }
            R.id.nav_week_view-> {
                val intent = Intent(this, WeekViewActivity::class.java)
                intent.flags = Intent.FLAG_ACTIVITY_REORDER_TO_FRONT
                startActivity(intent)
            }
            R.id.nav_month_view-> {
                val intent = Intent(this, MonthViewActivity::class.java)
                intent.flags = Intent.FLAG_ACTIVITY_REORDER_TO_FRONT
                startActivity(intent)
            }

            R.id.nav_account -> {
                val intent = Intent(this, AccountActivity::class.java)
                intent.flags = Intent.FLAG_ACTIVITY_REORDER_TO_FRONT
                startActivity(intent)
            }
            R.id.nav_sign_out -> {
                auth?.signOut()
                val intent = Intent(this, LoginActivity::class.java)
                startActivity(intent)
                finish()
            }
        }
        drawer_layout_home.closeDrawer(GravityCompat.START)
        return true
    }

    override fun onResume() {
        super.onResume()
        setNavDrawerDetails()
        recentLinearLayout!!.removeAllViews()
        graphHolder!!.removeAllViews()
        inflateRecentItems()
        inflateGraphView()
        hintsLinearLayout!!.removeAllViews()
        calcScore()
    }

    // Converts DP to PX for setMargin of RecentItems
    private fun Int.dpToPx(displayMetrics: DisplayMetrics): Int = (this * displayMetrics.density).toInt()

    private fun insertRecentItem(time: String, bgc: String, mid : String) {
    // Creates a RecentItem and adds it to view

        // Creates Layout Params for bottom margin of RecentItem
        var layoutParams = ConstraintLayout.LayoutParams(ConstraintLayout.LayoutParams.MATCH_PARENT, ConstraintLayout.LayoutParams.WRAP_CONTENT)
        layoutParams.setMargins(0,0,0, 4.dpToPx(this.resources.displayMetrics))

        // Creates new RecentItem and adds to view
        val newItem = RecentItem(this, time, bgc)
        newItem.recent_item_container.setOnClickListener {
            val intent = Intent(this, MeasurementViewActivity::class.java)
            intent.putExtra("mid", mid)
            startActivity(intent)
            Log.d("Measurement Item Clicked :", "DETECTED")
        }
        newItem.layoutParams = layoutParams

        recentLinearLayout!!.addView(newItem)
    }


    private fun inflateRecentItems() {
    // Fetches measurements and inflates them to view

        // Display ProgressBar while waiting for RecentItems to inflate
        val progressBar = ProgressBar(this)
        recentLinearLayout!!.addView(progressBar)

        val userId = auth!!.currentUser!!.uid

        // Gets current date in format dd-mm-yyyy for query
        var dateTime = LocalDateTime.now()
        val day = dateTime.dayOfMonth
        val month = dateTime.monthValue
        val year = dateTime.year
        val date = "$day-$month-$year"

        // Query -> Measurements from current day
        val query = database!!
                            .collection("Users")
                            .document(userId)
                            .collection("Measurements")
                            .whereEqualTo("date", date)
                            .orderBy("time", Query.Direction.DESCENDING)
        query.get()
            .addOnSuccessListener {querySnapshot ->
                val itemsArray = querySnapshot.documents

                // If no Measurements from current day
                // -> Display "No recent measurements" TextView
                if(itemsArray.size == 0) {
                    recentLinearLayout!!.removeView(progressBar)
                    val emptyTextView = TextView(this)
                    emptyTextView.text = getString(R.string.no_recent_measurements)
                    emptyTextView.setTextColor(ContextCompat.getColor(this, R.color.textDark))
                    emptyTextView.gravity = Gravity.CENTER_HORIZONTAL

                    var layoutParams = LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.WRAP_CONTENT)
                    layoutParams.setMargins(0, 20.dpToPx(this.resources.displayMetrics),0, 0)
                    emptyTextView.layoutParams = layoutParams

                    see_more_label.visibility = View.INVISIBLE
                    recentLinearLayout!!.removeAllViews()
                    recentLinearLayout!!.addView(emptyTextView)
                }
                // If measurements from current day -> display them
                else {
                    // For each measurement from current day
                    // -> insert measurement into RecentItems container
                    itemsArray.forEach { documentSnapshot ->
                        val item = documentSnapshot.toObject(Measurement::class.java)
                        val time = item!!.timeFormatted
                        val bgc = item.bloodGlucoseConc.toString()

                        recentLinearLayout!!.removeView(progressBar)
                        Log.d("ID : ", documentSnapshot.id)
                        insertRecentItem(time!!, bgc, documentSnapshot.id)
                    }
                }
            }
            .addOnFailureListener { e ->
                Log.w("Fetch recent items", "Error fetching recent items", e)
                Toast.makeText(this, "Error fetching recent items", Toast.LENGTH_LONG).show()
            }
    }

    // TODO add progress bars to week and month views

    private fun inflateGraphView() {
    // Fetches measurements and adds graph of them to view

        // Display progress bar while waiting for measurements to load
        val progressBar = ProgressBar(this)
        var layoutParams = ConstraintLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.WRAP_CONTENT)
        layoutParams.setMargins(0, 8.dpToPx(this.resources.displayMetrics),0, 0)
        progressBar.layoutParams = layoutParams
        graphHolder!!.addView(progressBar)

        val entries = ArrayList<Entry>()

        val userId = auth!!.currentUser!!.uid

        // Gets current date in format dd-mm-yyyy for query
        var dateTime = LocalDateTime.now()
        val day = dateTime.dayOfMonth
        val month = dateTime.monthValue
        val year = dateTime.year
        val date = "$day-$month-$year"

        // Query -> Measurements from current day
        val query = database!!
            .collection("Users")
            .document(userId)
            .collection("Measurements")
            .whereEqualTo("date", date)
            .orderBy("time", Query.Direction.ASCENDING)

        query.get()
            .addOnSuccessListener {querySnapshot ->
                val itemsArray = querySnapshot.documents

                // If no Measurements from current day
                // -> Display "No recent measurements" TextView
                if(itemsArray.size == 0) {
                    graphHolder!!.removeView(graph!!)
                    val addMeasurementsTextView = TextView(this)
                    addMeasurementsTextView.text = getString(R.string.add_measurement)
                    addMeasurementsTextView.setTextColor(ContextCompat.getColor(this, R.color.textDark))
                    addMeasurementsTextView.gravity = Gravity.CENTER
                    addMeasurementsTextView.height = 300

                    var layoutParams1 = ConstraintLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.WRAP_CONTENT)
                    layoutParams1.setMargins(0, 50.dpToPx(this.resources.displayMetrics),0, 50.dpToPx(this.resources.displayMetrics))
                    addMeasurementsTextView.layoutParams = layoutParams1


                    graphHolder!!.removeView(progressBar)
                    graphHolder!!.addView(addMeasurementsTextView)

                }
                // Else -> display recent measurements from current day
                else {
                    // For each measurement from current day
                    // -> insert measurement into RecentItems widget
                    itemsArray.forEach { documentSnapshot ->
                        val item = documentSnapshot.toObject(Measurement::class.java)
                        val time = item!!.time!!.toFloat()

                        val bgc = item.bloodGlucoseConc

                        Log.d("ENTRY", "VALUE ##########")
                        Log.d(time.toString(), bgc.toString())
                        entries.add(Entry(time, bgc))
                        Log.d("ADD ENTRIES", "COMPLETE")
                    }

                    // Creates graph
                    val dataSet = LineDataSet(entries, "Blood Glucose Concentration")
                    dataSet.mode = LineDataSet.Mode.CUBIC_BEZIER
                    dataSet.setDrawValues(false)
                    dataSet.setDrawFilled(true)
                    dataSet.lineWidth = 2f
                    dataSet.fillColor = Color.WHITE
                    dataSet.circleRadius = 4f
                    dataSet.setCircleColor(Color.WHITE)
                    dataSet.color = Color.WHITE
                    dataSet.isHighlightEnabled = false
                    val lineData = LineData(dataSet)

                    var lowLimit = LimitLine(4f)
                    var highLimit = LimitLine(9f)
                    highLimit.lineColor = Color.RED
                    highLimit.lineWidth = 0.3f
                    highLimit.enableDashedLine(30f, 10f, 30f)
                    lowLimit.lineColor = Color.RED
                    lowLimit.lineWidth = 0.3f
                    lowLimit.enableDashedLine(30f, 10f, 30f)
                    var lowerLimit = LimitLine(4f)
                    lowerLimit.lineColor = Color.RED
                    lowerLimit.lineWidth = 0.3f
                    lowerLimit.enableDashedLine(30f, 10f, 30f)

                    graph!!.axisLeft.addLimitLine(highLimit)
                    graph!!.axisLeft.addLimitLine(lowLimit)
                    graph!!.axisRight.setDrawLimitLinesBehindData(false)

                    graph!!.data = lineData
                    graph!!.setGridBackgroundColor(Color.RED)
                    graph!!.axisRight.isEnabled = false
                    graph!!.xAxis.setDrawGridLines(false)
                    graph!!.xAxis.textSize = 10f
                    graph!!.xAxis.valueFormatter = GraphFormatterDay()
                    graph!!.xAxis.granularity = 100f
                    graph!!.xAxis.position = XAxis.XAxisPosition.BOTTOM
                    graph!!.legend.isEnabled = false
                    graph!!.description.isEnabled = false
                    graph!!.axisLeft.setDrawGridLines(false)
                    graph!!.invalidate()

                    graphHolder!!.removeView(progressBar)
                    graphHolder!!.addView(graph!!)
                    Log.d("POPULATE GRAPH", "COMPLETE")
                }
            }
            .addOnFailureListener { e ->
                Log.w("Fetch recent items", "Error fetching Graph items", e)
                graphHolder!!.removeView(graph!!)
                val tv = TextView(this)
                tv.text = getString(R.string.error)
                tv.setTextColor(ContextCompat.getColor(this, R.color.textDark))
                tv.gravity = Gravity.CENTER
                tv.height = 300

                var layoutParams2 = ConstraintLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.WRAP_CONTENT)
                layoutParams2.setMargins(0, 50.dpToPx(this.resources.displayMetrics),0, 50.dpToPx(this.resources.displayMetrics))
                tv.layoutParams = layoutParams2

                graphHolder!!.addView(tv)
            }
    }

    private fun calcScore() {
    // Fetches measurements for current day and uses them to set score
    // Updates score in firestore model
    // Fetches measurements and assigns them to field: measurementsArray

        var measurements = ArrayList<Measurement>()

        val userId = auth!!.currentUser!!.uid

        // Gets current date in format dd-mm-yyyy for query
        var dateTime = LocalDateTime.now()
        val day = dateTime.dayOfMonth
        val month = dateTime.monthValue
        val year = dateTime.year
        val date = day.toString() + "-" + month.toString() + "-" + year.toString()

        // Query -> Measurements from current day
        val query = database!!
            .collection("Users")
            .document(userId)
            .collection("Measurements")
            .whereEqualTo("date", date)
            .orderBy("time", Query.Direction.ASCENDING)

        query.get()
            .addOnSuccessListener { querySnapshot ->
                val itemsArray = querySnapshot.documents

                // If no Measurements from current day
                // -> Display "No recent measurements" TextView
                if (itemsArray.size == 0) {
                    // Upload score object to firestore
                    // Set score text equal to that score
                    setScore(0)
                }
                // Else -> get measurements from current day
                else {
                    // For each measurement from current day
                    itemsArray.forEach { documentSnapshot ->
                        val item = documentSnapshot.toObject(Measurement::class.java)
                        measurements.add(item!!)
                        measurementsArray.add(item!!)
                    }
                    val calculator = Calculator(measurements)
                    setScore(calculator.score)
                    inflateHints()
                }
            }
    }

    private fun setScore(scoreVal: Int) {
    // Sets score on view, updates user model with new score

        when {
            scoreVal >= 70 -> {
                scoreText!!.setTextColor(ContextCompat.getColor(this, R.color.scoreGood))
            }
            scoreVal >= 30 -> {
                scoreText!!.setTextColor(Color.YELLOW)
            }
            else -> {
                scoreText!!.setTextColor(Color.RED)
            }
        }
        scoreText!!.text = scoreVal.toString()

        val score = Score(scoreVal)

        val userId = auth!!.currentUser!!.uid

        // Gets current date in format dd-mm-yyyy for query
        var dateTime = LocalDateTime.now()
        val day = dateTime.dayOfMonth
        val month = dateTime.monthValue
        val year = dateTime.year
        val date = "$day-$month-$year"

        // Query -> Score from current day
        val query = database!!
            .collection("Users")
            .document(userId)
            .collection("Scores")
            .whereEqualTo("date", date)

        query.get()
            .addOnSuccessListener { querySnapshot ->
                val itemsArray = querySnapshot.documents

                // Delete all previous scores for current day
                if (itemsArray.size > 0) {
                    for(doc in itemsArray) {
                        doc.reference.delete()
                    }
                }
                // Set score for current day in firestore model
                database!!.collection("Users")
                    .document(userId)
                    .collection("Scores")
                    .add(score)
                    .addOnSuccessListener {
                        Log.d("Add score", "Score written successfully")
                    }
                    .addOnFailureListener {
                        Log.w("Add score", "Failed to write new measurement object to Firestore")
                    }
            }
    }

    private fun inflateHints() {
        // TODO implement functionality for deciding and inflating hints
        // Inserts appropriate hint based on context
        // -> If no measurements on current day yet - ADD_HINT
        // -> Regular usage - FREQUENT_HINT
        // -> If account is unverified - VERIFY_HINT
        // -> If deviation outside healthy range of BGC - RANGE_HINT

        hintsLinearLayout!!.removeAllViews()

        if(measurementsArray.size == 0) {
            insertHintItem(getString(HintItem.ADD_HINT))
        }

        if(!auth!!.currentUser!!.isEmailVerified) {
            insertHintItem(getString(HintItem.VERIFY_HINT))
        }

        if(measurementsArray.size > 2 && Math.random() > 0.7) {
            insertHintItem(getString(HintItem.FREQUENT_HINT))
        }

        var aboveUpper = false
        var belowLower = false
        val calc = Calculator(measurementsArray)
        for(m in measurementsArray) {
            if(m.bloodGlucoseConc > calc.upperLimit) {
                aboveUpper = true
            }
            else if(m.bloodGlucoseConc < calc.lowerLimit) {
                belowLower = true
            }
        }
        if(aboveUpper && Math.random() > 0.7) {
            insertHintItem(getString(HintItem.RANGE_HINT))
            if(Math.random() > 0.5) {
                insertHintItem(getString(HintItem.HIGH_HINT))
            }
        }
        if(belowLower && Math.random() > 0.7) {
            insertHintItem(getString(HintItem.RANGE_HINT))
            if(Math.random() > 0.5) {
                insertHintItem(getString(HintItem.LOW_HINT))
            }
        }

        try {
            if(scoreText!!.text.toString().toInt() in 1..30 && Math.random() > 0.5) {
                insertHintItem(getString(HintItem.LOW_SCORE_HINT))
            }
        } catch (e : Exception) {
            Log.w("Assign hint item score", "FAILED", e)
        }

        var numSymptoms = 0
        for(m in measurementsArray) {
            for(symptom in m.symptoms) {
                numSymptoms++
            }
        }

        if(numSymptoms > 3 && Math.random() > 0.7) {
            insertHintItem(getString(HintItem.NUM_MEASUREMENTS_HINT))
        }

        if(calc.percentageSafe > 50 && Math.random() > 0.7) {
            insertHintItem(getString(HintItem.PERCENT_SAFE_HINT))
        }

        if(measurementsArray.size > 1) {
            val duration = measurementsArray.last().time!!.toInt() - measurementsArray.first().time!!.toInt()
            if(duration < 100 && Math.random() > 0.7) {
                insertHintItem(getString(HintItem.TIME_HINT))
            }
        }

        if(Math.random() > 0.8) {
            if(Math.random() > 0.5) {
                insertHintItem(getString(HintItem.MEDS_HINT))
            }
            else {
                insertHintItem(getString(HintItem.NOTES_HINT))
            }
        }

        if(Math.random() > 0.7) {
            insertHintItem(getString(HintItem.SICK_HINT))
        }

        if(measurementsArray.size > 1) {
            if(measurementsArray.last().bloodGlucoseConc < 4 && Math.random() > 0.7) {
                insertHintItem(getString(HintItem.HYPO_HINT))
                if(measurementsArray.takeLast(2).last().bloodGlucoseConc < 4) {
                    insertHintItem(getString(HintItem.EMERGENCY_HINT))
                }
            }
            else if(measurementsArray.last().bloodGlucoseConc > 8.5 && Math.random() > 0.7) {
                insertHintItem(getString(HintItem.HYPER_HINT))
                if(measurementsArray.takeLast(2).last().bloodGlucoseConc > 8.5) {
                    insertHintItem(getString(HintItem.EMERGENCY_HINT))
                }
            }
        }

        if(hintsLinearLayout!!.childCount == 0) {
            if(scoreText!!.text.toString().toInt() > 60) {
                insertHintItem(getString(HintItem.GOOD_HINT))
            }
            else {
                insertHintItem(getString(HintItem.CONTINUE_HINT))
            }
        }



    }

    private fun insertHintItem(hint: String) {

        // Creates Layout Params for bottom margin of RecentItem
        var layoutParams = LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.WRAP_CONTENT)
        layoutParams.setMargins(0,0,0, 4.dpToPx(this.resources.displayMetrics))

        // Creates new RecentItem and adds to view
        val newItem = HintItem(this, hint)
        newItem.layoutParams = layoutParams
        hintsLinearLayout!!.addView(newItem)
    }


}
