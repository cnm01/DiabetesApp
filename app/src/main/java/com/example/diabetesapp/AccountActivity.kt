package com.example.diabetesapp

import android.content.Intent
import android.os.Bundle
import android.support.design.widget.NavigationView
import android.support.v4.view.GravityCompat
import android.support.v7.app.ActionBarDrawerToggle
import android.support.v7.app.AppCompatActivity
import android.util.Log
import android.view.Menu
import android.view.MenuItem
import android.view.View
import android.widget.TextView
import com.example.diabetesapp.model.User
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import kotlinx.android.synthetic.main.activity_account.*
import kotlinx.android.synthetic.main.app_bar_account.*

class AccountActivity : AppCompatActivity(), NavigationView.OnNavigationItemSelectedListener {

    // TODO create view of user details
    // TODO implement log out button
    // TODO implement delete account button
    // TODO implement change details

    // UI elements
    private var nameTextView: TextView? = null
    private var emailTextView: TextView? = null
    private var headerView: View? = null

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
        nameTextView = headerView!!.findViewById<View>(R.id.name_text_view) as TextView
        emailTextView = headerView!!.findViewById<View>(R.id.email_text_view) as TextView
        auth = FirebaseAuth.getInstance()
        database = FirebaseFirestore.getInstance()
        setNavDrawerDetails()
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
        if (drawer_layout_account.isDrawerOpen(GravityCompat.START)) {
            drawer_layout_account.closeDrawer(GravityCompat.START)
        } else {
            super.onBackPressed()
        }
    }

    override fun onCreateOptionsMenu(menu: Menu): Boolean {
        // Inflate the menu; this adds items to the action bar if it is present.
        menuInflater.inflate(R.menu.account, menu)
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
            }
            R.id.nav_settings -> {

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
}
