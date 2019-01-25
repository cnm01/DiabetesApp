package com.example.diabetesapp

import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.support.v7.app.AppCompatActivity
import android.text.TextUtils
import android.util.Log
import android.view.View
import android.view.inputmethod.InputMethodManager
import android.widget.*
import com.google.firebase.auth.FirebaseAuth

class LoginActivity : AppCompatActivity() {

    // UI Elements
    private var emailEditText: EditText? = null
    private var passwordEditText: EditText? = null
    private var forgotPasswordTextView: TextView? = null
    private var loginButton: Button? = null
    private var registerButton: Button? = null
    private var progressBar: ProgressBar? = null


    // Firebase References
    private var auth: FirebaseAuth? = null

    // Values
    private var email: String? = null
    private var password: String? = null


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_login)

        initialise()

    }

    private fun initialise() {


        emailEditText = findViewById<View>(R.id.email_input) as EditText
        passwordEditText = findViewById<View>(R.id.password_input) as EditText
        forgotPasswordTextView = findViewById<View>(R.id.forgot_password_text) as TextView
        loginButton = findViewById<View>(R.id.login_button) as Button
        registerButton = findViewById<View>(R.id.register_button) as Button
        progressBar = findViewById<View>(R.id.progress_bar) as ProgressBar
        progressBar?.visibility = View.INVISIBLE

        auth = FirebaseAuth.getInstance()

        registerButton!!.setOnClickListener{
            val intent = Intent(this, RegisterActivity::class.java)
            startActivity(intent)
            finish()
        }

        loginButton!!.setOnClickListener{
            // Close keyboard
            val inputManager: InputMethodManager =getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager
            inputManager.hideSoftInputFromWindow(currentFocus.windowToken, InputMethodManager.SHOW_FORCED)
            login()
        }
    }

    private fun login() {
        email = emailEditText?.text.toString()
        password = passwordEditText?.text.toString()

        // Validate User Input (not empty)
        if(!TextUtils.isEmpty(email) && !TextUtils.isEmpty(password)) {

            progressBar?.visibility = View.VISIBLE

            // Login FirebaseAuth user account
            auth!!
                .signInWithEmailAndPassword(email!!, password!!)
                .addOnCompleteListener(this) {
                    task -> progressBar?.visibility = View.INVISIBLE

                    if(task.isSuccessful) {
                        Log.d("Logging in user", "signInWithEmail:success")
                        updateUI()
                    }
                    else {
                        Log.e("Logging in user", "signInWithEmail:failure", task.exception)
                        Toast.makeText(this, "Authentication failed", Toast.LENGTH_SHORT).show()
                    }
                }

        }
        else {
            Toast.makeText(this, "Details can't be blank", Toast.LENGTH_SHORT).show()
        }

    }

    private fun updateUI() {
        val intent = Intent(this, HomeActivity::class.java)
        startActivity(intent)
        finish()
    }
}
