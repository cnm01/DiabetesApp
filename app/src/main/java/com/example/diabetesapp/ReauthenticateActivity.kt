package com.example.diabetesapp

import android.content.Intent
import android.os.Bundle
import android.support.v7.app.AppCompatActivity
import android.text.TextUtils
import android.util.Log
import android.view.View
import android.widget.Button
import android.widget.EditText
import android.widget.ProgressBar
import android.widget.Toast
import com.google.firebase.auth.EmailAuthProvider
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore

class ReauthenticateActivity : AppCompatActivity() {

    // UI Elements
    private var emailEditText: EditText? = null
    private var passwordEditText : EditText? = null
    private var progressBar : ProgressBar? = null
    private var loginButton: Button? = null

    // Firebase Ref
    private var auth: FirebaseAuth? = null
    private var database: FirebaseFirestore? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_reauthenticate)
        init()
    }

    private fun init() {
        emailEditText = findViewById<View>(R.id.email_input) as EditText
        passwordEditText = findViewById<View>(R.id.password_input) as EditText
        progressBar = findViewById<View>(R.id.progress_bar) as ProgressBar
        loginButton = findViewById<View>(R.id.login_button) as Button
        auth = FirebaseAuth.getInstance()
        database = FirebaseFirestore.getInstance()
        progressBar!!.visibility = View.INVISIBLE

        loginButton!!.setOnClickListener {
            reauthenticate()
        }
    }

    private fun reauthenticate() {
        val TAG = "ReauthenticateActivity"
        val user = auth!!.currentUser
        val email = emailEditText!!.text.toString()
        val password = passwordEditText!!.text.toString()

        if(!TextUtils.isEmpty(email) && !TextUtils.isEmpty(password)) {
            progressBar?.visibility = View.VISIBLE
            val credential = EmailAuthProvider.getCredential(email, password)
            user!!.reauthenticate(credential)
                .addOnSuccessListener {
                    Log.d(TAG, "Successfully re-authenticated user")
                    val intent = Intent(this, ChangeDetailsActivity::class.java)
                    startActivity(intent)
                    finish()

                }
                .addOnFailureListener { e ->
                    Log.w(TAG, "Error re-authenticating user", e)
                    progressBar?.visibility = View.INVISIBLE
                    Toast.makeText(this, "Unable to authenticate account", Toast.LENGTH_SHORT).show()
                }
        }
        else {
            Toast.makeText(this, "Details cannot be blank", Toast.LENGTH_SHORT).show()
        }
    }
}
