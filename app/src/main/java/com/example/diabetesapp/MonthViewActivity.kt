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
import android.view.Menu
import android.view.MenuItem
import android.view.View
import android.widget.*
import com.example.diabetesapp.model.GraphFormatterMonth
import com.example.diabetesapp.model.Score
import com.example.diabetesapp.model.User
import com.example.diabetesapp.view.DayItem
import com.github.mikephil.charting.charts.LineChart
import com.github.mikephil.charting.components.XAxis
import com.github.mikephil.charting.data.Entry
import com.github.mikephil.charting.data.LineData
import com.github.mikephil.charting.data.LineDataSet
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import kotlinx.android.synthetic.main.activity_month_view.*
import kotlinx.android.synthetic.main.activity_new_measurement.*
import kotlinx.android.synthetic.main.app_bar_month_view.*
import kotlinx.android.synthetic.main.day_item_layout.view.*
import java.time.LocalDateTime

class MonthViewActivity : AppCompatActivity(), NavigationView.OnNavigationItemSelectedListener {

    // Score
    private var scoreText: TextView? = null

    // Data
    private val currentDate = LocalDateTime.now()
    private var month : Int? = null

    // GraphView
    private var graph: LineChart? = null
    private var graphHolder: ConstraintLayout? = null

    // Day Items
    private var measurementsLinearLayout: LinearLayout? = null
    private var days = mutableMapOf<Int, Score>()

    // UI Elements
    private var monthSpinner : Spinner? = null
    private var yearSpinner : Spinner? = null
    private var nextButton : Button? = null
    private var prevButton : Button? = null

    // Drawer elements
    private var nameTextView: TextView? = null
    private var emailTextView: TextView? = null
    private var headerView: View? = null

    // Firebase References
    private var auth: FirebaseAuth? = null
    private var database: FirebaseFirestore? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_month_view)
        setSupportActionBar(toolbar_month)

        val toggle = ActionBarDrawerToggle(
            this, drawer_layout_month, toolbar_month, R.string.navigation_drawer_open, R.string.navigation_drawer_close
        )
        drawer_layout_month.addDrawerListener(toggle)
        toggle.syncState()

        nav_view_month.setNavigationItemSelectedListener(this)

        initialise()
    }

    private fun initialise() {
        headerView = nav_view_month.getHeaderView(0)
        nameTextView = headerView!!.findViewById<View>(R.id.name_text_view) as TextView
        emailTextView = headerView!!.findViewById<View>(R.id.email_text_view) as TextView
        auth = FirebaseAuth.getInstance()
        database = FirebaseFirestore.getInstance()
        var headerIMG = headerView!!.findViewById<View>(R.id.header_layout_month) as LinearLayout
        // Sets Navigation Drawer Header background image
        headerIMG.setBackgroundResource(R.drawable.wallpaper2)

        scoreText = findViewById<View>(R.id.score_text) as TextView
        graph = findViewById<View>(R.id.graphView) as LineChart
        graphHolder = findViewById<View>(R.id.graph_holder) as ConstraintLayout
        measurementsLinearLayout = findViewById<View>(R.id.recentLinearLayout) as LinearLayout
        monthSpinner = findViewById<View>(R.id.month_spinner) as Spinner
        yearSpinner = findViewById<View>(R.id.year_spinner) as Spinner
        nextButton = findViewById<View>(R.id.next_button4) as Button
        prevButton = findViewById<View>(R.id.prev_button4) as Button
        month = currentDate.monthValue

        initMonthSpinner()
        initYearSpinner()

//        initGraph()

        initButtons()
    }

    private fun fetchDays() {

        days.clear()

        val userId = auth!!.currentUser!!.uid

        // Query -> Score from current day
        val query = database!!
            .collection("Users")
            .document(userId)
            .collection("Scores")
            .whereEqualTo("month", month)
        query.get()
            .addOnSuccessListener {querySnapshot ->
                val itemsArray = querySnapshot.documents
                for(document in itemsArray) {
                    val dayItem = document.toObject(Score::class.java)
                    days[dayItem!!.day!!.toInt()] = dayItem
                }
                Log.d("Fetch_score_items : ", "SUCCESS")



                inflateDays()
                inflateGraphView()

            }

            .addOnFailureListener { e ->
                Log.w("Fetch score items", "Error fetching score items", e)
                Toast.makeText(this, "Error fetching score items", Toast.LENGTH_LONG).show()
            }






    }

    private fun inflateDays() {

        measurementsLinearLayout!!.removeAllViews()

        val longMonths = arrayListOf(1,3,5,7,8,10,12)
        val shortMonths = arrayListOf(4,6,9,11)

        val lengthOfMonth = when (month) {
            in longMonths -> {
                31
            }
            in shortMonths -> {
                30
            }
            else -> {
                28
            }
        }

        var total = 0
        var ctr = 0

        for(i in 1..lengthOfMonth) {

            Log.d("Evaluating day : ", i.toString())
            val dayItemDate = LocalDateTime.of(2019, month!!, i, 7, 0)

            if(dayItemDate.isAfter(currentDate)) {
                Log.d("Day is after date :", "TRUE")
                insertDayItem(i.toString(), month!!, -1, dayItemDate)
            }
            else {
                Log.d("Day is after date :", "FALSE")
                if(days.containsKey(i)) {
                    // Add score item for day
                    Log.d("Score exists : ", "TRUE")
                    insertDayItem(i.toString(), month!!, days[i]!!.score, dayItemDate)
                    total += days[i]!!.score
                    ctr++
                }
                else {
                    // Add zero (0) item
                    Log.d("Score exists : ", "FALSE")
                    insertDayItem(i.toString(), month!!, 0, dayItemDate)
                    ctr++
                }
            }
        }

        if(ctr > 0) {
            val avg = total/ctr
            setScore(avg)
        }
        else {
            setScore(0)
        }


    }

    private fun insertDayItem(day : String, month : Int, score : Int, date : LocalDateTime) {
        Log.d("Inserting day : ", day)

        val monthName = when (month) {
            (1) -> {"January"}
            (2) -> {"February"}
            (3) -> {"March"}
            (4) -> {"April"}
            (5) -> {"May"}
            (6) -> {"June"}
            (7) -> {"July"}
            (8) -> {"August"}
            (9) -> {"September"}
            (10) -> {"October"}
            (11) -> {"November"}
            (12) -> {"December"}
            else -> {"---"}
        }

        // Creates Layout Params for bottom margin of RecentItem
        var layoutParams = LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.WRAP_CONTENT)
        layoutParams.setMargins(0,0,0, 4.dpToPx(this.resources.displayMetrics))

        // Create new DayItem
        val newItem = DayItem(this, day, monthName, score)
        newItem.day_item.setOnClickListener {
            val intent = Intent(this, DayViewActivity::class.java)
            intent.putExtra("date", date)
            startActivity(intent)
        }
        newItem.layoutParams = layoutParams
        measurementsLinearLayout!!.addView(newItem)


    }


    // Converts DP to PX for setMargin of RecentItems
    private fun Int.dpToPx(displayMetrics: DisplayMetrics): Int = (this * displayMetrics.density).toInt()

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
            }

            override fun onItemSelected(parent: AdapterView<*>?, view: View?, position: Int, id: Long) {
                val item = monthSpinnerAdapter.getItem(position)
                val spinnerStr = item.toString()
                val selectedMonth = spinnerStr.toInt()
                month = selectedMonth

                fetchDays()
//                inflateGraphView()
//                inflateMeasurementItems()
//                Log.d("Calling inflate measurements : ", "from MonthSpinner")
//                inflateScore()

            }

        }


    }

    private fun inflateGraphView() {

        val entries = arrayListOf<Entry>()

        val longMonths = arrayListOf(1,3,5,7,8,10,12)
        val shortMonths = arrayListOf(4,6,9,11)

        val lengthOfMonth = when (month) {
            in longMonths -> {
                31
            }
            in shortMonths -> {
                30
            }
            else -> {
                28
            }
        }

        for(i in 1..lengthOfMonth) {

            val dayItemDate = LocalDateTime.of(2019, month!!, i, 7, 0)

            if(dayItemDate.isAfter(currentDate)) {
            }
            else {
                if(days.containsKey(i)) {
                    // Add score item for day
                    entries.add(Entry(i.toFloat(), days[i]!!.score.toFloat()))
                }
                else {
                    // Add zero (0) item
                    entries.add(Entry(i.toFloat(), 0f))
                }
            }
        }

        entries.sortBy { entry -> entry.x }

        val dataSet = LineDataSet(entries, "Monthly Score")
        dataSet.mode = LineDataSet.Mode.HORIZONTAL_BEZIER
        dataSet.setDrawValues(false)
        dataSet.setDrawFilled(true)
        dataSet.lineWidth = 2f
        dataSet.lineWidth = 2f
        dataSet.fillColor = Color.WHITE
        dataSet.circleRadius = 2f
        dataSet.setCircleColor(Color.WHITE)
        dataSet.color = Color.WHITE
        dataSet.isHighlightEnabled = false
        val lineData = LineData(dataSet)
        graph!!.data = lineData
        graph!!.setGridBackgroundColor(Color.RED)
        graph!!.axisRight.isEnabled = false
        graph!!.xAxis.setDrawGridLines(false)
        graph!!.xAxis.textSize = 10f
        graph!!.xAxis.valueFormatter = GraphFormatterMonth()
        graph!!.xAxis.position = XAxis.XAxisPosition.BOTTOM
        graph!!.xAxis.axisMinimum = 0f
        graph!!.xAxis.axisMaximum = lengthOfMonth.toFloat()
        graph!!.xAxis.granularity = 1f
        graph!!.axisLeft.axisMinimum = 0f
        graph!!.axisLeft.axisMaximum = 100f
        graph!!.axisLeft.granularity = 1f
        graph!!.legend.isEnabled = false
        graph!!.description.isEnabled = false
        graph!!.axisLeft.setDrawGridLines(false)
        if(month!! == currentDate.monthValue) {
            graph!!.setVisibleXRangeMaximum(currentDate.dayOfMonth.toFloat())
            graph!!.setVisibleXRangeMaximum(32f)
        }
        else {
            graph!!.fitScreen()
        }
        graph!!.invalidate()

        graphHolder!!.removeView(progressBar)
        if(graph!!.parent != graphHolder) {
            graphHolder!!.removeAllViews()
            graphHolder!!.addView(graph!!)
        }
        Log.d("POPULATE GRAPH", "COMPLETE")

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
            }

            override fun onItemSelected(parent: AdapterView<*>?, view: View?, position: Int, id: Long) {
                val item = yearSpinnerAdapter.getItem(position)
                var spinnerStr = item.toString()

            }

        }

        yearSpinner!!.setSelection(0)
    }

    private fun initButtons() {
        nextButton!!.setOnClickListener {
            val new = month!! + 1

            if(new <= 12) {
                month = new
                monthSpinner!!.setSelection(month!!-1)
            }
        }
        prevButton!!.setOnClickListener {
            val new = month!! - 1

            if(new >= 1) {
                month = new
                monthSpinner!!.setSelection(month!!-1)
            }
        }
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

    override fun onResume() {
        super.onResume()
        setNavDrawerDetails()

        monthSpinner!!.setSelection(month!!-1)
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

    override fun onBackPressed() {
        if (drawer_layout_month.isDrawerOpen(GravityCompat.START)) {
            drawer_layout_month.closeDrawer(GravityCompat.START)
        } else {
            super.onBackPressed()
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
        drawer_layout_month.closeDrawer(GravityCompat.START)
        return true
    }

    override fun onCreateOptionsMenu(menu: Menu): Boolean {
        // Inflate the menu; this adds items to the action bar if it is present.
        menuInflater.inflate(R.menu.month_view, menu)
        return true
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        when (item.itemId) {
            R.id.action_settings -> return true
            else -> return super.onOptionsItemSelected(item)
        }
    }
}
