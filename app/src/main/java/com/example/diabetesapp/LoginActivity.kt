package com.example.diabetesapp

import android.content.Intent
import android.os.Bundle
import android.support.v7.app.AppCompatActivity
import android.view.View
import android.widget.Button
import android.widget.EditText
import android.widget.TextView

class LoginActivity : AppCompatActivity() {

    // UI Elements
    private var emailEditText: EditText? = null
    private var passwordEditText: EditText? = null
    private var forgotPasswordTextView: TextView? = null
    private var loginButton: Button? = null
    private var registerButton: Button? = null


    // Firebase References


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

        registerButton!!.setOnClickListener{
            val intent = Intent(this, RegisterActivity::class.java)
            startActivity(intent)
        }
    }
}
