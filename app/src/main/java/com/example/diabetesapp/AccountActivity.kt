package com.example.diabetesapp

import android.content.Intent
import android.os.Bundle
import android.support.design.widget.NavigationView
import android.support.v4.view.GravityCompat
import android.support.v7.app.ActionBarDrawerToggle
import android.support.v7.app.AppCompatActivity
import android.text.method.ScrollingMovementMethod
import android.util.Log
import android.view.Menu
import android.view.MenuItem
import android.view.View
import android.widget.Button
import android.widget.LinearLayout
import android.widget.TextView
import android.widget.Toast
import com.example.diabetesapp.model.User
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import kotlinx.android.synthetic.main.activity_account.*
import kotlinx.android.synthetic.main.app_bar_account.*

class AccountActivity : AppCompatActivity(), NavigationView.OnNavigationItemSelectedListener {

    // Nav Drawer Header
    private var headerNameTextView: TextView? = null
    private var headerEmailTextView: TextView? = null
    private var headerView: View? = null

    // UI elements
    private var firstNameTextView: TextView? = null
    private var lastNameTextView: TextView? = null
    private var emailTextView: TextView? = null
    private var verificationTextView: TextView? = null
    private var changeDetailsButton: Button? = null
    private var deleteAccountButton: Button? = null
    private var signOutButton: Button? = null
    private var verifyButton: Button? = null

    // Firebase References
    private var auth: FirebaseAuth? = null
    private var database: FirebaseFirestore? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_account)
        setSupportActionBar(toolbar_account)

        val toggle = ActionBarDrawerToggle(
            this, drawer_layout_account, toolbar_account, R.string.navigation_drawer_open, R.string.navigation_drawer_close
        )
        drawer_layout_account.addDrawerListener(toggle)
        toggle.syncState()

        nav_view_account.setNavigationItemSelectedListener(this)

        initialise()
    }

    private fun initialise() {
        headerView = nav_view_account.getHeaderView(0)
        headerNameTextView = headerView!!.findViewById<View>(R.id.name_text_view) as TextView
        headerEmailTextView = headerView!!.findViewById<View>(R.id.email_text_view) as TextView
        val headerIMG = headerView!!.findViewById<View>(R.id.header_layout) as LinearLayout
        // Sets Navigation Drawer Header background image
        headerIMG.setBackgroundResource(R.drawable.header2)

        firstNameTextView = findViewById<View>(R.id.first_name_text) as TextView
        firstNameTextView!!.movementMethod = ScrollingMovementMethod()
        lastNameTextView = findViewById<View>(R.id.last_name_text) as TextView
        lastNameTextView!!.movementMethod = ScrollingMovementMethod()
        emailTextView = findViewById<View>(R.id.email_text) as TextView
        emailTextView!!.movementMethod = ScrollingMovementMethod()
        verificationTextView = findViewById<View>(R.id.verification_text) as TextView
        verificationTextView!!.movementMethod = ScrollingMovementMethod()
        changeDetailsButton = findViewById<View>(R.id.change_details_button) as Button
        deleteAccountButton = findViewById<View>(R.id.delete_account_button) as Button
        signOutButton = findViewById<View>(R.id.sign_out_button) as Button
        verifyButton = findViewById<View>(R.id.verify_button) as Button

        auth = FirebaseAuth.getInstance()
        database = FirebaseFirestore.getInstance()


        // Opens authentication for change account details on button click
        changeDetailsButton!!.setOnClickListener{
            val intent = Intent(this, ReauthenticateActivity::class.java)
            startActivity(intent)
        }

        // Opens authentication for account deletion on button click
        deleteAccountButton!!.setOnClickListener{
            val intent = Intent(this, DeleteAccountActivity::class.java)
            startActivity(intent)
        }

        // Signs out and opens login page on button click
        signOutButton!!.setOnClickListener{
            auth!!.signOut()
            Log.d("Logging out", "Successfully logged out")
            val intent = Intent(this, LoginActivity::class.java)
            startActivity(intent)
            finish()
        }

        // Sends email verification on button click
        verifyButton!!.setOnClickListener {
            auth!!.currentUser!!.sendEmailVerification()
                .addOnSuccessListener {
                    Toast.makeText(this, "Verification email sent", Toast.LENGTH_LONG).show()
                    Log.d("Send verify email", "SUCCESS")
                }
                .addOnFailureListener {
                    Toast.makeText(this, "Failed to send verification email", Toast.LENGTH_LONG).show()
                    Log.w("Send verify email", "FAILED")
                }
        }
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
                headerNameTextView!!.text = name
                headerEmailTextView!!.text = email
                Log.d("Fetch drawer details", "User details successfully obtained and written to drawer")
            }
            .addOnFailureListener{
                    e -> Log.w("Fetch drawer details", "Error fetching user details for nav drawer", e)
            }
    }

    private fun setAccountDetails() {
    // Sets firstName, lastName, email, and verification

        val tag = "Set account details"
        val uid = auth!!.currentUser!!.uid
        val userRef = database!!.collection("Users").document(uid)
        userRef.get()
            .addOnSuccessListener { documentSnapshot ->
                val user = documentSnapshot.toObject(User::class.java)
                val firstName = user!!.firstName
                val lastName = user.lastName
                val email = user.email
                firstNameTextView!!.text = firstName
                lastNameTextView!!.text = lastName
                emailTextView!!.text = email
                setVerification()
                Log.d(tag, "Account details successfully obtained")
            }
            .addOnFailureListener{
                    e -> Log.w(tag, "Error fetching account details setAccountDetails()", e)
            }
    }

    private fun setVerification() {
    // Sets the verification status

        auth!!.currentUser?.getIdToken(true)!!
            .addOnSuccessListener {
                auth!!.currentUser!!.reload()
                Log.d("Refresh token for verification status :", "SUCCESSFUL")
            }
            .addOnFailureListener {
                Log.e("Refresh token for verifcation status :", "FAILED")
            }

        val user = auth!!.currentUser!!
        if(user.isEmailVerified) {
            verificationTextView!!.text = getString(R.string.verified)
            verifyButton!!.isEnabled = false
            verifyButton!!.visibility = View.INVISIBLE
        }
        else {
            verificationTextView!!.text = getString(R.string.unverified)
        }
    }

    override fun onBackPressed() {
        if (drawer_layout_account.isDrawerOpen(GravityCompat.START)) {
            drawer_layout_account.closeDrawer(GravityCompat.START)
        } else {
            super.onBackPressed()
            finish()
        }
    }

    override fun onCreateOptionsMenu(menu: Menu): Boolean {
        // Inflate the menu; this adds items to the action bar if it is present.
        return false
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

            }
            R.id.nav_sign_out -> {
                auth?.signOut()
                val intent = Intent(this, LoginActivity::class.java)
                startActivity(intent)
                finish()
            }
        }

        drawer_layout_account.closeDrawer(GravityCompat.START)
        return true
    }

    override fun onResume() {
        super.onResume()
        setNavDrawerDetails()
        setAccountDetails()
    }
}
