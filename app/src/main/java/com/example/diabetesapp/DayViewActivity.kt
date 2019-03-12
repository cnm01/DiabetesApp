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
import android.widget.*
import com.example.diabetesapp.model.GraphFormatterDay
import com.example.diabetesapp.model.Measurement
import com.example.diabetesapp.model.Score
import com.example.diabetesapp.model.User
import com.example.diabetesapp.view.RecentItem
import com.github.mikephil.charting.charts.LineChart
import com.github.mikephil.charting.components.LimitLine
import com.github.mikephil.charting.components.XAxis
import com.github.mikephil.charting.data.Entry
import com.github.mikephil.charting.data.LineData
import com.github.mikephil.charting.data.LineDataSet
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.Query
import kotlinx.android.synthetic.main.activity_day_view.*
import kotlinx.android.synthetic.main.app_bar_day_view.*
import kotlinx.android.synthetic.main.content_home.*
import java.time.LocalDateTime

class DayViewActivity : AppCompatActivity(), NavigationView.OnNavigationItemSelectedListener {

    // Data
    private var day: Int? = null
    private var month: Int? = null
    private var year: Int? = null

    // Drawer elements
    private var nameTextView: TextView? = null
    private var emailTextView: TextView? = null
    private var headerView: View? = null

    // Score
    private var scoreText: TextView? = null

    // GraphView
    private var graph: LineChart? = null
    private var graphHolder: ConstraintLayout? = null

    // Measurement Items
    private var measurementsLinearLayout: LinearLayout? = null

    // UI Elements
    private var daySpinner : Spinner? = null
    private var monthSpinner : Spinner? = null
    private var yearSpinner : Spinner? = null
    private var nextButton : Button? = null
    private var prevButton : Button? = null

    // Firebase References
    private var auth: FirebaseAuth? = null
    private var database: FirebaseFirestore? = null



    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_day_view)
        setSupportActionBar(toolbar_day)

        val toggle = ActionBarDrawerToggle(
            this, drawer_layout_day, toolbar_day, R.string.navigation_drawer_open, R.string.navigation_drawer_close
        )
        drawer_layout_day.addDrawerListener(toggle)
        toggle.syncState()

        nav_view_day.setNavigationItemSelectedListener(this)

        initialise()
    }

    private fun initialise() {
        initDate()
        headerView = nav_view_day.getHeaderView(0)
        nameTextView = headerView!!.findViewById<View>(R.id.name_text_view) as TextView
        emailTextView = headerView!!.findViewById<View>(R.id.email_text_view) as TextView
        auth = FirebaseAuth.getInstance()
        database = FirebaseFirestore.getInstance()
        val headerIMG = headerView!!.findViewById<View>(R.id.header_layout_day) as LinearLayout
        // TODO refactor multiple navigation drawers (one for each activity) into one shared one
        // Sets Navigation Drawer Header background image
        headerIMG.setBackgroundResource(R.drawable.header2)

        daySpinner = findViewById<View>(R.id.day_spinner) as Spinner
        monthSpinner = findViewById<View>(R.id.month_spinner) as Spinner
        yearSpinner = findViewById<View>(R.id.year_spinner) as Spinner
        nextButton = findViewById<View>(R.id.next_button) as Button
        prevButton = findViewById<View>(R.id.prev_button) as Button
        initDaySpinner()
        initMonthSpinner()
        initYearSpinner()
        graph = findViewById<View>(R.id.graphView) as LineChart
        graphHolder = findViewById<View>(R.id.graph_holder) as ConstraintLayout
        measurementsLinearLayout = findViewById<View>(R.id.recentLinearLayout) as LinearLayout
        initGraph()
        initMeasurements()
        initButtons()
        scoreText = findViewById<View>(R.id.score_text) as TextView
        setScore(0)




    }

    private fun initButtons() {
        nextButton!!.setOnClickListener {
            val current = LocalDateTime.of(year!!, month!!, day!!, 7, 0)
            val new = current.plusDays(1)
            day = new.dayOfMonth
            month = new.monthValue

            daySpinner!!.setSelection(day!!-1)
            monthSpinner!!.setSelection(month!!-1)
        }
        prevButton!!.setOnClickListener {
            val current = LocalDateTime.of(year!!, month!!, day!!, 7, 0)
            val new = current.minusDays(1)
            day = new.dayOfMonth
            month = new.monthValue

            daySpinner!!.setSelection(day!!-1)
            monthSpinner!!.setSelection(month!!-1)
        }

    }

    private fun initDate() {

        val dateTime = LocalDateTime.now()
        day = dateTime.dayOfMonth
        month = dateTime.monthValue
        year = dateTime.year

    }

    private fun setScore(scoreVal: Int) {
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
    }

    private fun initGraph() {
        graphHolder!!.removeView(graph!!)
        val addMeasurementsTextView = TextView(this)
        addMeasurementsTextView.text = "No measurements"
        addMeasurementsTextView.setTextColor(ContextCompat.getColor(this, R.color.textDark))
        addMeasurementsTextView.gravity = Gravity.CENTER
        addMeasurementsTextView.height = 300

        val layoutParams1 = ConstraintLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.WRAP_CONTENT)
        layoutParams1.setMargins(0, 50.dpToPx(this.resources.displayMetrics),0, 50.dpToPx(this.resources.displayMetrics))
        addMeasurementsTextView.layoutParams = layoutParams1


//        graphHolder!!.addView(addMeasurementsTextView)
    }

    private fun initMeasurements() {
        val emptyTextView = TextView(this)
        emptyTextView.text = "No measurements"
        emptyTextView.setTextColor(ContextCompat.getColor(this, R.color.textDark))
        emptyTextView.gravity = Gravity.CENTER_HORIZONTAL

        val layoutParams = LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.WRAP_CONTENT)
        layoutParams.setMargins(0, 20.dpToPx(this.resources.displayMetrics),0, 0)
        emptyTextView.layoutParams = layoutParams

        see_more_label.visibility = View.INVISIBLE
//        recentLinearLayout!!.addView(emptyTextView)
    }

    // Converts DP to PX for setMargin of RecentItems
    private fun Int.dpToPx(displayMetrics: DisplayMetrics): Int = (this * displayMetrics.density).toInt()

    private fun initDaySpinner() {

        val longMonths = arrayListOf(1,3,5,7,8,10,12)
        val shortMonths = arrayListOf(4,6,9,11)

        // TODO implement year switching capability

        val days = ArrayList<Int>()

        when (month) {
            in longMonths -> {
                for(i in 1..31) {
                    days.add(i)
                }
            }
            in shortMonths -> {
                for(i in 1..30) {
                    days.add(i)
                }
            }
            //Month is Feb =(2)
            else -> {
                for(i in 1..28) {
                    days.add(i)
                }
            }
        }

        val daySpinnerAdapter = ArrayAdapter(this, android.R.layout.simple_spinner_item, days)
        daySpinnerAdapter.setDropDownViewResource((android.R.layout.simple_dropdown_item_1line))
        daySpinner!!.adapter = daySpinnerAdapter

        daySpinner!!.onItemSelectedListener = object : AdapterView.OnItemSelectedListener {

            override fun onNothingSelected(parent: AdapterView<*>?) {
                TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
            }

            override fun onItemSelected(parent: AdapterView<*>?, view: View?, position: Int, id: Long) {
                val item = daySpinnerAdapter.getItem(position)
                val spinnerStr = item.toString()
                val selectedDay = spinnerStr.toInt()
                day = selectedDay

                inflateGraphView()
                inflateMeasurementItems()
                Log.d("Calling inflate measurements : ", "from DaySpinner")
                inflateScore()
            }

        }

        daySpinner!!.setSelection(day!!-1)
    }

    private fun initMonthSpinner() {

        val months = ArrayList<Int>()
        for(i in 1..12) {
            months.add(i)
        }

        val monthSpinnerAdapter = ArrayAdapter(this, android.R.layout.simple_spinner_item, months)
        monthSpinnerAdapter.setDropDownViewResource((android.R.layout.simple_dropdown_item_1line))
        monthSpinner!!.adapter = monthSpinnerAdapter

        monthSpinner!!.onItemSelectedListener = object : AdapterView.OnItemSelectedListener {

            override fun onNothingSelected(parent: AdapterView<*>?) {
                TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
            }

            override fun onItemSelected(parent: AdapterView<*>?, view: View?, position: Int, id: Long) {
                val item = monthSpinnerAdapter.getItem(position)
                val spinnerStr = item.toString()
                val selectedMonth = spinnerStr.toInt()
                month = selectedMonth

                initDaySpinner()

                inflateGraphView()
                inflateMeasurementItems()
                Log.d("Calling inflate measurements : ", "from MonthSpinner")
                inflateScore()

            }

        }

        monthSpinner!!.setSelection(month!!-1)
    }

    private fun initYearSpinner() {

        val years = ArrayList<Int>()
        for(i in 2019..2019) {
            years.add(i)
        }

        val yearSpinnerAdapter = ArrayAdapter(this, android.R.layout.simple_spinner_item, years)
        yearSpinnerAdapter.setDropDownViewResource((android.R.layout.simple_dropdown_item_1line))
        yearSpinner!!.adapter = yearSpinnerAdapter

        yearSpinner!!.onItemSelectedListener = object : AdapterView.OnItemSelectedListener {

            override fun onNothingSelected(parent: AdapterView<*>?) {
                TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
            }

            override fun onItemSelected(parent: AdapterView<*>?, view: View?, position: Int, id: Long) {
                val item = yearSpinnerAdapter.getItem(position)
                var spinnerStr = item.toString()

            }

        }

        yearSpinner!!.setSelection(0)
    }

    private fun setNavDrawerDetails() {
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

    private fun inflateGraphView() {

        graphHolder!!.removeAllViews()

        val progressBar = ProgressBar(this)
        val layoutParams = ConstraintLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.WRAP_CONTENT)
        layoutParams.setMargins(0, 8.dpToPx(this.resources.displayMetrics),0, 0)
        progressBar.layoutParams = layoutParams
        graphHolder!!.addView(progressBar)

        val entries = ArrayList<Entry>()

        val userId = auth!!.currentUser!!.uid

        val date = day.toString() + "-" + month.toString() + "-" + year.toString()

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

                // If no Measurements from selected date
                // -> Display "No measurements" TextView
                if(itemsArray.size == 0) {
                    graphHolder!!.removeView(graph!!)
                    val addMeasurementsTextView = TextView(this)
                    addMeasurementsTextView.text = "No measurements for selected date"
                    addMeasurementsTextView.setTextColor(ContextCompat.getColor(this, R.color.textDark))
                    addMeasurementsTextView.gravity = Gravity.CENTER
                    addMeasurementsTextView.height = 300

                    val layoutParams1 = ConstraintLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.WRAP_CONTENT)
                    layoutParams1.setMargins(0, 50.dpToPx(this.resources.displayMetrics),0, 50.dpToPx(this.resources.displayMetrics))
                    addMeasurementsTextView.layoutParams = layoutParams1


                    graphHolder!!.removeView(progressBar)
                    graphHolder!!.addView(addMeasurementsTextView)

                }
                // Else -> display measurements from selected day
                else {
                    // For each measurement from selected day
                    // -> insert measurement into MeasurementItems widget
                    itemsArray.forEach { documentSnapshot ->
                        val item = documentSnapshot.toObject(Measurement::class.java)
                        val time = item!!.time!!.toFloat()

                        val bgc = item.bloodGlucoseConc

                        Log.d("ENTRY", "VALUE ##########")
                        Log.d(time.toString(), bgc.toString())
                        entries.add(Entry(time, bgc))
                        Log.d("ADD ENTRIES", "COMPLETE")
                    }
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

                    val postPrandialLimit = LimitLine(9f)
                    postPrandialLimit.lineColor = Color.RED
                    postPrandialLimit.lineWidth = 0.3f
                    postPrandialLimit.enableDashedLine(30f, 10f, 30f)
                    val prePrandialLimit = LimitLine(6f)
                    prePrandialLimit.lineColor = Color.RED
                    prePrandialLimit.lineWidth = 0.3f
                    prePrandialLimit.enableDashedLine(30f, 10f, 30f)
                    val lowerLimit = LimitLine(4f)
                    lowerLimit.lineColor = Color.RED
                    lowerLimit.lineWidth = 0.3f
                    lowerLimit.enableDashedLine(30f, 10f, 30f)

                    graph!!.axisLeft.addLimitLine(postPrandialLimit)
                    graph!!.axisLeft.addLimitLine(prePrandialLimit)
                    graph!!.axisLeft.addLimitLine(lowerLimit)
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

                    if(graph!!.parent != graphHolder) {
                        graphHolder!!.removeAllViews()
                        graphHolder!!.addView(graph!!)
                    }

                    Log.d("POPULATE GRAPH", "COMPLETE")
                }
            }
            .addOnFailureListener { e ->
                Log.w("Fetch measurement items", "Error fetching measurement items", e)
                graphHolder!!.removeView(graph!!)
                val tv = TextView(this)
                tv.text = "Error"
                tv.setTextColor(ContextCompat.getColor(this, R.color.textDark))
                tv.gravity = Gravity.CENTER
                tv.height = 300

                val layoutParams2 = ConstraintLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.WRAP_CONTENT)
                layoutParams2.setMargins(0, 50.dpToPx(this.resources.displayMetrics),0, 50.dpToPx(this.resources.displayMetrics))
                tv.layoutParams = layoutParams2

                graphHolder!!.addView(tv)
            }

    }


    private fun insertMeasurementItem(time: String, bgc: String) {

        // Creates Layout Params for bottom margin of RecentItem
        var layoutParams = LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.WRAP_CONTENT)
        layoutParams.setMargins(0,0,0, 4.dpToPx(this.resources.displayMetrics))

        // Creates new RecentItem and adds to view
        val newItem = RecentItem(this, time, bgc)
        newItem.layoutParams = layoutParams
        measurementsLinearLayout!!.addView(newItem)
    }

    private fun inflateMeasurementItems() {

        measurementsLinearLayout!!.removeAllViews()
        recentLinearLayout.removeAllViews()

        // Display ProgressBar while waiting for RecentItems to inflate
        val progressBar = ProgressBar(this)
        measurementsLinearLayout!!.addView(progressBar)

        val userId = auth!!.currentUser!!.uid

        val date = "$day-$month-$year"

        // Query -> Measurements from current day
        val query = database!!
            .collection("Users")
            .document(userId)
            .collection("Measurements")
            .whereEqualTo("date", date)
            .orderBy("time", Query.Direction.DESCENDING)
//                            .limit(2)
        query.get()
            .addOnSuccessListener {querySnapshot ->
                val itemsArray = querySnapshot.documents

                // If no Measurements from current day
                // -> Display "No recent measurements" TextView
                if(itemsArray.size == 0) {
                    measurementsLinearLayout!!.removeView(progressBar)
                    val emptyTextView = TextView(this)
                    emptyTextView.text = "No measurements for selected date"
                    emptyTextView.setTextColor(ContextCompat.getColor(this, R.color.textDark))
                    emptyTextView.gravity = Gravity.CENTER_HORIZONTAL

                    val layoutParams = LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.WRAP_CONTENT)
                    layoutParams.setMargins(0, 20.dpToPx(this.resources.displayMetrics),0, 0)
                    emptyTextView.layoutParams = layoutParams

                    see_more_label.visibility = View.INVISIBLE
                    measurementsLinearLayout!!.removeAllViews()
                    measurementsLinearLayout!!.addView(emptyTextView)
                }
                // If measurements from current day -> display them
                else {
                    // For each measurement from current day
                    // -> insert measurement into RecentItems widget
                    measurementsLinearLayout!!.removeView(progressBar)
                    measurementsLinearLayout!!.removeAllViews()
                    itemsArray.forEach { documentSnapshot ->
                        val item = documentSnapshot.toObject(Measurement::class.java)
                        val time = item!!.timeFormatted
                        val bgc = item.bloodGlucoseConc.toString()


                        insertMeasurementItem(time!!, bgc)
                    }
                }
            }
            .addOnFailureListener { e ->
                Log.w("Fetch measurement items", "Error fetching measurement items", e)
                Toast.makeText(this, "Error fetching measurement items", Toast.LENGTH_LONG).show()

            }

    }

    override fun onResume() {
        super.onResume()
        setNavDrawerDetails()
        inflateGraphView()
        inflateMeasurementItems()
        Log.d("Calling inflate measurements : ", "from onResume")
        inflateScore()

    }

    private fun inflateScore() {

        val userId = auth!!.currentUser!!.uid

        val date = "$day-$month-$year"

        // Query -> Score from current day
        val query = database!!
            .collection("Users")
            .document(userId)
            .collection("Scores")
            .whereEqualTo("date", date)
        query.get()
            .addOnSuccessListener {querySnapshot ->
                Log.d("Fetch score", "SUCCESSFUL")
                val itemsArray = querySnapshot.documents

                // If no score from current day
                // -> Display Score = 0
                if(itemsArray.size == 0) {
                    setScore(0)
                }
                // If score for selected date
                else {
                    val scoreItem = itemsArray[0].toObject(Score::class.java)
                    setScore(scoreItem!!.score)
                }
            }
            .addOnFailureListener { e ->
                Log.w("Fetch score", "Error fetching score", e)
                Toast.makeText(this, "Error fetching score", Toast.LENGTH_LONG).show()

            }

    }

    override fun onBackPressed() {
        if (drawer_layout_day.isDrawerOpen(GravityCompat.START)) {
            drawer_layout_day.closeDrawer(GravityCompat.START)
        } else {
            super.onBackPressed()
        }
    }

    override fun onCreateOptionsMenu(menu: Menu): Boolean {
        // Inflate the menu; this adds items to the action bar if it is present.
        menuInflater.inflate(R.menu.day_view, menu)
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
                val intent = Intent(this, HomeActivity::class.java)
                intent.flags = Intent.FLAG_ACTIVITY_REORDER_TO_FRONT
                startActivity(intent)
                finish()
            }
            R.id.nav_day_view -> {
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
            R.id.nav_settings -> {
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
        drawer_layout_day.closeDrawer(GravityCompat.START)
        return true
    }
}
