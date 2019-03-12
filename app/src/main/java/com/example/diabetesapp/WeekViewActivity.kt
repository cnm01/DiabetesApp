package com.example.diabetesapp

import android.content.Intent
import android.graphics.Color
import android.graphics.drawable.GradientDrawable
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
import com.example.diabetesapp.model.GraphFormatterWeek
import com.example.diabetesapp.model.Score
import com.example.diabetesapp.model.User
import com.github.mikephil.charting.charts.LineChart
import com.github.mikephil.charting.components.XAxis
import com.github.mikephil.charting.data.Entry
import com.github.mikephil.charting.data.LineData
import com.github.mikephil.charting.data.LineDataSet
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import kotlinx.android.synthetic.main.activity_week_view.*
import kotlinx.android.synthetic.main.app_bar_week_view.*
import java.time.LocalDateTime

class WeekViewActivity : AppCompatActivity(), NavigationView.OnNavigationItemSelectedListener {

    // Score
    private var scoreText: TextView? = null
    private var total : Int = 0
    private var counter : Int = 0

    // Data
    private var currentDate : LocalDateTime? = null
    private var selectedDate : LocalDateTime? = null
    private var day: Int? = null
    private var month: Int? = null
    private var year: Int? = null

    // GraphView
    private var graph: LineChart? = null
    private var graphHolder: ConstraintLayout? = null

    // UI Elements
    private var daySpinner : Spinner? = null
    private var monthSpinner : Spinner? = null
    private var yearSpinner : Spinner? = null
    private var nextButton : Button? = null
    private var prevButton : Button? = null

    // Date strings
    private var mondayDate : TextView? = null
    private var tuesdayDate : TextView? = null
    private var wednesdayDate : TextView? = null
    private var thursdayDate : TextView? = null
    private var fridayDate : TextView? = null
    private var saturdayDate : TextView? = null
    private var sundayDate : TextView? = null

    // Score labels
    private var mondayScore : Button? = null
    private var tuesdayScore : Button? = null
    private var wednesdayScore : Button? = null
    private var thursdayScore : Button? = null
    private var fridayScore : Button? = null
    private var saturdayScore : Button? = null
    private var sundayScore : Button? = null
    private var scoreButtons : ArrayList<Button>? = null
    private val entries = ArrayList<Entry>()

    // Drawer elements
    private var nameTextView: TextView? = null
    private var emailTextView: TextView? = null
    private var headerView: View? = null

    // Firebase References
    private var auth: FirebaseAuth? = null
    private var database: FirebaseFirestore? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_week_view)
        setSupportActionBar(toolbar_week)

        val toggle = ActionBarDrawerToggle(
            this, drawer_layout_week, toolbar_week, R.string.navigation_drawer_open, R.string.navigation_drawer_close
        )
        drawer_layout_week.addDrawerListener(toggle)
        toggle.syncState()

        nav_view_week.setNavigationItemSelectedListener(this)

        initialise()
    }

    private fun initialise() {
        headerView = nav_view_week.getHeaderView(0)
        nameTextView = headerView!!.findViewById<View>(R.id.name_text_view) as TextView
        emailTextView = headerView!!.findViewById<View>(R.id.email_text_view) as TextView
        auth = FirebaseAuth.getInstance()
        database = FirebaseFirestore.getInstance()
        var headerIMG = headerView!!.findViewById<View>(R.id.header_layout_week) as LinearLayout
        // TODO refactor multiple navigation drawers (one for each activity) into one shared one
        // Sets Navigation Drawer Header background image
        headerIMG.setBackgroundResource(R.drawable.header2)
        graph = findViewById<View>(R.id.graphView) as LineChart
        graphHolder = findViewById<View>(R.id.graph_holder) as ConstraintLayout
        daySpinner = findViewById<View>(R.id.day_spinner) as Spinner
        monthSpinner = findViewById<View>(R.id.month_spinner) as Spinner
        yearSpinner = findViewById<View>(R.id.year_spinner) as Spinner
        nextButton = findViewById<View>(R.id.next_button4) as Button
        prevButton = findViewById<View>(R.id.prev_button4) as Button
        scoreText = findViewById<View>(R.id.score_text) as TextView
        initDateStrings()
        initDateScores()
        currentDate = LocalDateTime.now()
        selectedDate = LocalDateTime.now()
        refreshDate()
        initDaySpinner()
        initMonthSpinner()
        initYearSpinner()
        initButtons()
        scoreButtons = arrayListOf<Button>(
            mondayScore!!,
            tuesdayScore!!,
            wednesdayScore!!,
            thursdayScore!!,
            fridayScore!!,
            saturdayScore!!,
            sundayScore!!
        )


    }

    private fun refreshDate() {
    // Should be called whenever the currentDate is changed

        day = selectedDate!!.dayOfMonth
        month = selectedDate!!.monthValue
        year = selectedDate!!.year
    }

    private fun inflateDatesAndScores() {


        val cur = selectedDate!!.dayOfWeek.value -1

        val dateTextViews = arrayListOf<TextView>(
            mondayDate!!,
            tuesdayDate!!,
            wednesdayDate!!,
            thursdayDate!!,
            fridayDate!!,
            saturdayDate!!,
            sundayDate!!
        )

        // Evaluates days in the week up to and including selectedDate

        var datesBefore = arrayListOf<LocalDateTime>()
        for (i in 0..cur) {
            datesBefore.add(selectedDate!!.minusDays((cur - i).toLong()))
        }

        total = 0
        counter = 0
        entries.clear()


        for (i in 0..cur) {
            dateTextViews[i].text =
                getString(R.string.date_format, datesBefore[i].dayOfMonth, datesBefore[i].monthValue, datesBefore[i].year)

            val dateFormatted = "${datesBefore[i].dayOfMonth}-${datesBefore[i].monthValue}-${datesBefore[i].year}"
            val userId = auth!!.currentUser!!.uid
            // Query -> Score from day
            val query = database!!
                .collection("Users")
                .document(userId)
                .collection("Scores")
                .whereEqualTo("date", dateFormatted)
            query.get()
                .addOnSuccessListener { querySnapshot ->
                    val itemsArray = querySnapshot.documents
                    if(datesBefore[i].isAfter(currentDate)) {
                        setScoreAndColor(this!!.scoreButtons!![i], -1)
                    }
                    else {
                        // If no score for day
                        if (itemsArray.size == 0) {
                            setScoreAndColor(this!!.scoreButtons!![i], 0)
                            entries.add(Entry(i.toFloat(), 0f))
                            counter++
                            // Day == Monday
                            if ((cur + 1) == 0) {
                                setScore(0)
                            } else {
                                var avg = total / counter
                                Log.d("Avg score : ", avg.toString())
                                setScore(avg)
                            }
                        }
                        else {
                            val score = itemsArray[0].toObject(Score::class.java)
                            setScoreAndColor(this!!.scoreButtons!![i], score!!.score)
                            entries.add(Entry(i.toFloat(), score!!.score.toFloat()))
                            total += score.score
                            counter++
                            Log.d("Day val : ", i.toString())
                            Log.d("Day score :", score.score.toString())
                            Log.d("Total score : ", total.toString())
                            // Day == Monday
                            if ((cur + 1) == 0) {
                                setScore(score!!.score)
                            } else {
                                var avg = total / counter
                                Log.d("Avg score : ", avg.toString())
                                setScore(avg)
                            }
                        }
                    }
                }
                .addOnFailureListener { e ->
                    Toast.makeText(this, "Error fetching score", Toast.LENGTH_LONG).show()
                    Log.e("Fetch scores", "FAILURE", e)
                }
        }


        // Evaluates days in the week after the selectedDate

        var datesAfter = mutableMapOf<Int, LocalDateTime>()
        for (i in (cur+1)..6) {
            datesAfter[i] = (selectedDate!!.plusDays((i - (cur)).toLong()))
        }

        Log.d("days after : ", datesAfter.toString())


        for (i in (cur+1)..6) {
            dateTextViews[i].text =
                getString(R.string.date_format, datesAfter[i]!!.dayOfMonth, datesAfter[i]!!.monthValue, datesAfter[i]!!.year)

        val dateFormatted = "${datesAfter[i]!!.dayOfMonth}-${datesAfter[i]!!.monthValue}-${datesAfter[i]!!.year}"
        val userId = auth!!.currentUser!!.uid
        // Query -> Score from day
        val query = database!!
            .collection("Users")
            .document(userId)
            .collection("Scores")
            .whereEqualTo("date", dateFormatted)
        query.get()
            .addOnSuccessListener { querySnapshot ->
                val itemsArray = querySnapshot.documents
//                Log.d("Logging date : ", datesAfter[i].toString())
//                Log.d("Current Date : ", currentDate.toString())
//                Log.d("Logging date is after current date : ", datesAfter[i]!!.isAfter(currentDate).toString())
                if(datesAfter[i]!!.isAfter(currentDate)) {
                    Log.d("AFTER DATE", "EXECUTING")
                    Log.d("ENTRIES : ", entries.toString())
                    setScoreAndColor(this!!.scoreButtons!![i], -1)
                    inflateGraphView()
                }
                else {
                    // If no score for day
                    if (itemsArray.size == 0) {
                        setScoreAndColor(this!!.scoreButtons!![i], 0)
                        entries.add(Entry(i.toFloat(), 0f))
                        counter++
                        var avg = total / counter
                        Log.d("Avg score : ", avg.toString())
                        setScore(avg)
                    }
                    // If score
                    else {
                        val score = itemsArray[0].toObject(Score::class.java)
                        setScoreAndColor(this!!.scoreButtons!![i], score!!.score)
                        entries.add(Entry(i.toFloat(), score!!.score.toFloat()))
                        total += score.score
                        counter ++
                        Log.d("Day val : ", i.toString())
                        Log.d("Day score :", score.score.toString())
                        Log.d("Total score : ", total.toString())
                        var avg = total / counter
                        Log.d("Avg score : ", avg.toString())
                        setScore(avg)
                    }
                }
            }
            .addOnFailureListener { e ->
                Toast.makeText(this, "Error fetching score", Toast.LENGTH_LONG).show()
                Log.e("Fetch scores", "FAILURE", e)
            }
        }

    }

    private fun setScoreAndColor(button : Button, score : Int) {
        val drawable = button.background as GradientDrawable
        when {
            score == -1 -> {
                drawable.setStroke(2, ContextCompat.getColor(this, R.color.colorPrimary))
                button.text = "-"
                return

            }
            score >= 70 -> {
                drawable.setStroke(2, ContextCompat.getColor(this, R.color.scoreGood))
            }
            score >= 30 -> {
                drawable.setStroke(2, Color.YELLOW)
            }
            else -> {
                drawable.setStroke(2, Color.RED)
            }
        }
        button!!.text = "$score"
        inflateGraphView()
    }

    private fun initDaySpinner() {

        // TODO set correct number of days in each month

        val days = ArrayList<Int>()
        for(i in 1..31) {
            days.add(i)
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

                selectedDate = LocalDateTime.of(year!!, month!!, selectedDay, 7, 0, 0)
                refreshDate()
                inflateDatesAndScores()
//                inflateGraphView()
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

                selectedDate = LocalDateTime.of(year!!, selectedMonth, day!!, 7, 0, 0)
                refreshDate()

                inflateDatesAndScores()
//                inflateGraphView()
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

    override fun onResume() {
        super.onResume()
        setNavDrawerDetails()
        inflateDatesAndScores()
//        inflateGraphView()
    }

    private fun initDateStrings() {
        mondayDate = findViewById<View>(R.id.monday_date) as TextView
        tuesdayDate = findViewById<View>(R.id.tuesday_date) as TextView
        wednesdayDate = findViewById<View>(R.id.wednesday_date) as TextView
        thursdayDate = findViewById<View>(R.id.thursday_date) as TextView
        fridayDate = findViewById<View>(R.id.friday_date) as TextView
        saturdayDate = findViewById<View>(R.id.saturday_date) as TextView
        sundayDate = findViewById<View>(R.id.sunday_date) as TextView
    }

    private fun initDateScores() {
        mondayScore = findViewById<View>(R.id.monday_score) as Button
        tuesdayScore = findViewById<View>(R.id.tuesday_score) as Button
        wednesdayScore = findViewById<View>(R.id.wednesday_score) as Button
        thursdayScore = findViewById<View>(R.id.thursday_score) as Button
        fridayScore = findViewById<View>(R.id.friday_score) as Button
        saturdayScore = findViewById<View>(R.id.saturday_score) as Button
        sundayScore = findViewById<View>(R.id.sunday_score) as Button

    }

    private fun initButtons() {
        nextButton!!.setOnClickListener {
            selectedDate = selectedDate!!.plusDays(7)
            refreshDate()
            daySpinner!!.setSelection(day!!-1)
            monthSpinner!!.setSelection(month!!-1)
            inflateDatesAndScores()
//            inflateGraphView()
        }
        prevButton!!.setOnClickListener {
            selectedDate = selectedDate!!.minusDays(7)
            refreshDate()
            daySpinner!!.setSelection(day!!-1)
            monthSpinner!!.setSelection(month!!-1)
            inflateDatesAndScores()
//            inflateGraphView()
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
        if (drawer_layout_week.isDrawerOpen(GravityCompat.START)) {
            drawer_layout_week.closeDrawer(GravityCompat.START)
        } else {
            super.onBackPressed()
        }
    }

    override fun onCreateOptionsMenu(menu: Menu): Boolean {
        // Inflate the menu; this adds items to the action bar if it is present.
        menuInflater.inflate(R.menu.week_view, menu)
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
        drawer_layout_week.closeDrawer(GravityCompat.START)
        return true
    }

    // Converts DP to PX for setMargin of RecentItems
    private fun Int.dpToPx(displayMetrics: DisplayMetrics): Int = (this * displayMetrics.density).toInt()

    private fun inflateGraphView() {

        try {
            graphHolder!!.removeAllViews()

            val progressBar = ProgressBar(this)
            val layoutParams = ConstraintLayout.LayoutParams(
                LinearLayout.LayoutParams.MATCH_PARENT,
                LinearLayout.LayoutParams.WRAP_CONTENT
            )
            layoutParams.setMargins(0, 8.dpToPx(this.resources.displayMetrics), 0, 0)
            progressBar.layoutParams = layoutParams
            graphHolder!!.addView(progressBar)

//            for (i in 0..6) {
//                try {
//                    entries.add(Entry(i.toFloat(), this!!.scoreButtons!![i].text.toString().toFloat()))
//                } catch (e : java.lang.Exception) {
//                    Log.w("Convert string to float : ", "EXCEPTION", e)
//                }
//            }

            entries.sortBy { entry -> entry.x }

            val dataSet = LineDataSet(entries, "Weekly Score")
            dataSet.mode = LineDataSet.Mode.LINEAR
            dataSet.setDrawValues(false)
            dataSet.setDrawFilled(true)
            dataSet.lineWidth = 2f
            dataSet.lineWidth = 2f
            dataSet.fillColor = Color.WHITE
            dataSet.circleRadius = 4f
            dataSet.setCircleColor(Color.WHITE)
            dataSet.color = Color.WHITE
            dataSet.isHighlightEnabled = false
            val lineData = LineData(dataSet)
            graph!!.data = lineData
            graph!!.setGridBackgroundColor(Color.RED)
            graph!!.axisRight.isEnabled = false
            graph!!.xAxis.setDrawGridLines(false)
            graph!!.xAxis.textSize = 10f
            graph!!.xAxis.valueFormatter = GraphFormatterWeek()
            graph!!.xAxis.position = XAxis.XAxisPosition.BOTTOM
            graph!!.xAxis.axisMinimum = 0f
            graph!!.xAxis.axisMaximum = 6f
            graph!!.xAxis.granularity = 1f
            graph!!.axisLeft.axisMinimum = 0f
            graph!!.axisLeft.axisMaximum = 100f
            graph!!.axisLeft.granularity = 1f
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

        } catch (e : Exception) {

            Log.w("POPULATE GRAPH", "EXCEPTION", e)
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

}
