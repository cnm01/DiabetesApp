package com.example.diabetesapp

import android.content.Intent
import android.graphics.Color
import android.os.Bundle
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
import com.example.diabetesapp.model.Measurement
import com.example.diabetesapp.model.User
import com.example.diabetesapp.model.XAxisFormatter
import com.example.diabetesapp.view.RecentItem
import com.github.mikephil.charting.charts.LineChart
import com.github.mikephil.charting.components.XAxis
import com.github.mikephil.charting.data.Entry
import com.github.mikephil.charting.data.LineData
import com.github.mikephil.charting.data.LineDataSet
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.Query
import kotlinx.android.synthetic.main.activity_home.*
import kotlinx.android.synthetic.main.app_bar_home.*
import kotlinx.android.synthetic.main.content_home.*
import java.time.LocalDateTime
import java.util.*

class HomeActivity : AppCompatActivity(), NavigationView.OnNavigationItemSelectedListener {

    // GraphView
    private var graph: LineChart? = null

    // RecentItems
    private var recentLinearLayout: LinearLayout? = null

    // Hints
    private var hintsLinearLineChart: LinearLayout? = null


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

//        inflateRecentItems()




    }

    private fun initialise() {
        headerView = nav_view_home.getHeaderView(0)
        nameTextView = headerView!!.findViewById<View>(R.id.name_text_view) as TextView
        emailTextView = headerView!!.findViewById<View>(R.id.email_text_view) as TextView
        var headerIMG = headerView!!.findViewById<View>(R.id.header_layout) as LinearLayout
        // TODO refactor multiple navigation drawers (one for each activity) into one shared one
        // Sets Navigation Drawer Header background image
        headerIMG.setBackgroundResource(R.drawable.header2)
        auth = FirebaseAuth.getInstance()
        database = FirebaseFirestore.getInstance()
        recentLinearLayout = findViewById<View>(R.id.recentLinearLayout) as LinearLayout
        graph = findViewById<View>(R.id.graphView) as LineChart
        hintsLinearLineChart = findViewById<View>(R.id.hintsLinearLayout) as LinearLayout
    }

    private fun setNavDrawerDetails() {
        val uid = auth!!.currentUser!!.uid
        val userRef = database!!.collection("Users").document(uid)
        userRef.get()
            .addOnSuccessListener { documentSnapshot ->
                val user = documentSnapshot.toObject(User::class.java)
                val name = user!!.firstName + " " + user.lastName
                val email = user!!.email
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
        when (item.itemId) {
            R.id.action_settings -> return true
            else -> return super.onOptionsItemSelected(item)
        }
    }

    override fun onNavigationItemSelected(item: MenuItem): Boolean {
        // Handle navigation view item clicks here.
        when (item.itemId) {
            R.id.nav_dashboard -> {

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

        drawer_layout_home.closeDrawer(GravityCompat.START)
        return true
    }

    override fun onResume() {
        super.onResume()
        setNavDrawerDetails()
        recentLinearLayout!!.removeAllViews()
        see_more_label.visibility = View.VISIBLE
        inflateRecentItems()
        inflateGraphView()
    }

    // Converts DP to PX for setMargin of RecentItems
    private fun Int.dpToPx(displayMetrics: DisplayMetrics): Int = (this * displayMetrics.density).toInt()



    private fun insertRecentItem(time: String, bgc: String) {

        // Creates Layout Params for bottom margin of RecentItem
        var layoutParams = LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.WRAP_CONTENT)
        layoutParams.setMargins(0,0,0, 4.dpToPx(this.resources.displayMetrics))

        // Creates new RecentItem and adds to view
        val newItem = RecentItem(this, time, bgc)
        newItem.layoutParams = layoutParams
        recentLinearLayout!!.addView(newItem)
    }

    private fun inflateRecentItems() {

        // Display ProgressBar while waiting for RecentItems to inflate
        val progressBar = ProgressBar(this)
        recentLinearLayout!!.addView(progressBar)

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
                            .orderBy("time", Query.Direction.DESCENDING)
//                            .limit(2)
        query.get()
            .addOnSuccessListener {querySnapshot ->
                val itemsArray = querySnapshot.documents

                // If no Measurements from current day
                // -> Display "No recent measurements" TextView
                if(itemsArray.size == 0) {
                    recentLinearLayout!!.removeView(progressBar)
                    val emptyTextView = TextView(this)
                    emptyTextView.text = "No recent measurements"
                    emptyTextView.setTextColor(ContextCompat.getColor(this, R.color.textDark))
                    emptyTextView.gravity = Gravity.CENTER_HORIZONTAL

                    var layoutParams = LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.WRAP_CONTENT)
                    layoutParams.setMargins(0, 20.dpToPx(this.resources.displayMetrics),0, 0)
                    emptyTextView.layoutParams = layoutParams

                    see_more_label.visibility = View.INVISIBLE
                    recentLinearLayout!!.addView(emptyTextView)
                }
                // Else -> display recent measurements from current day
                else {
                    // For each measurement from current day
                    // -> insert measurement into RecentItems widget
                    itemsArray.forEach { documentSnapshot ->
                        val item = documentSnapshot.toObject(Measurement::class.java)
                        val time = item!!.timeFormatted
                        val bgc = item!!.bloodGlucoseConc.toString()

                        recentLinearLayout!!.removeView(progressBar)
                        insertRecentItem(time!!, bgc!!)
                    }
                }
            }
            .addOnFailureListener { e ->
                Log.w("Fetch recent items", "Error fetching recent items", e)
                Toast.makeText(this, "Error fetching recent items", Toast.LENGTH_LONG).show()

            }

    }



    private fun inflateGraphView() {

        val entries = ArrayList<Entry>()

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
            .addOnSuccessListener {querySnapshot ->
                val itemsArray = querySnapshot.documents

                // If no Measurements from current day
                // -> Display "No recent measurements" TextView
                if(itemsArray.size == 0) {

                    Toast.makeText(this, "No Graph items", Toast.LENGTH_LONG).show()

                }
                // Else -> display recent measurements from current day
                else {
                    // For each measurement from current day
                    // -> insert measurement into RecentItems widget
                    itemsArray.forEach { documentSnapshot ->
                        val item = documentSnapshot.toObject(Measurement::class.java)
                        val time = item!!.time!!.toFloat()

                        val bgc = item!!.bloodGlucoseConc
                        // TODO find a way to evenly space x axis of graph
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
                    dataSet.circleRadius = 5f
                    dataSet.setCircleColor(Color.WHITE)
                    dataSet.color = Color.WHITE
                    val lineData = LineData(dataSet)
                    graph!!.data = lineData
                    graph!!.setNoDataText("No data available")
                    graph!!.setGridBackgroundColor(Color.RED)
//                    graph!!.setVisibleXRangeMaximum(5f)
                    graph!!.axisRight.isEnabled = false
                    graph!!.xAxis.setDrawGridLines(false)
                    graph!!.xAxis.textSize = 10f
                    graph!!.xAxis.valueFormatter = XAxisFormatter()
//                    graph!!.xAxis.axisLineWidth = 2f
//                    graph!!.xAxis.axisLineColor = Color.BLACK
//                    graph!!.xAxis.setDrawAxisLine(false)
//                    graph!!.axisLeft.setDrawAxisLine(false)
//                    graph!!.axisRight.setDrawGridLines(false)
                    graph!!.xAxis.granularity = 100f
                    graph!!.xAxis.position = XAxis.XAxisPosition.BOTTOM
                    graph!!.legend.isEnabled = false
                    graph!!.description.isEnabled = false
                    graph!!.invalidate()
                    graph!!.axisLeft.setDrawGridLines(false)
                    Log.d("POPULATE GRAPH", "COMPLETE")
                }
            }
            .addOnFailureListener { e ->
                Log.w("Fetch recent items", "Error fetching Graph items", e)
                Toast.makeText(this, "Error fetching Graph items", Toast.LENGTH_LONG).show()

            }

    }

    // TODO create method for setting score
    // TODO create method for setting score color based on score

    // TODO create method for inflating hints



}
