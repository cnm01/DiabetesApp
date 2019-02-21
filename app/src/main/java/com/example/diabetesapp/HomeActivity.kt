package com.example.diabetesapp

import android.content.Intent
import android.opengl.Visibility
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
import com.example.diabetesapp.view.RecentItem
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.Query
import kotlinx.android.synthetic.main.activity_home.*
import kotlinx.android.synthetic.main.app_bar_home.*
import kotlinx.android.synthetic.main.content_home.*
import java.time.LocalDateTime

class HomeActivity : AppCompatActivity(), NavigationView.OnNavigationItemSelectedListener {

    // RecentItems
    private var recentLinearLayout: LinearLayout? = null


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
        setNavDrawerDetails()
        recentLinearLayout = findViewById<View>(R.id.recentLinearLayout) as LinearLayout
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
                        val time = item!!.hour.toString() + ":" + item!!.minute.toString()
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

}
