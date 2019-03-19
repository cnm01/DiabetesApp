package com.example.diabetesapp

import android.content.Context
import android.os.Bundle
import android.support.v7.app.AppCompatActivity
import android.util.Log
import android.view.View
import android.view.inputmethod.InputMethodManager
import android.widget.Button
import android.widget.EditText
import android.widget.ProgressBar
import android.widget.Toast
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseAuthEmailException

class ForgotPasswordActivity : AppCompatActivity() {

    // UI Elements
    private var emailEditText : EditText? = null
    private var progressBar : ProgressBar? = null
    private var submitButton : Button? = null

    // Firebase References
    private var auth: FirebaseAuth? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_forgot_password)

        init()
    }

    private fun init() {
        emailEditText = findViewById<View>(R.id.email_input2) as EditText
        progressBar = findViewById<View>(R.id.progress_bar2) as ProgressBar
        submitButton = findViewById<View>(R.id.submit_email_button) as Button
        progressBar!!.visibility = View.INVISIBLE
        auth = FirebaseAuth.getInstance()

        submitButton!!.setOnClickListener {
            submit()
        }
    }

    private fun submit() {
        submitButton!!.visibility = View.VISIBLE

        val email = emailEditText!!.text.toString()

        auth!!.sendPasswordResetEmail(email)
            .addOnSuccessListener {
                closeKeyboard()
                Log.d("Send password reset email : ", "SUCCESS")
                progressBar!!.visibility = View.INVISIBLE
                Toast.makeText(this, "Password reset email sent", Toast.LENGTH_SHORT).show()
                finish()
            }
            .addOnFailureListener { e ->
                if(e is FirebaseAuthEmailException) {
                    Toast.makeText(this, "No account associated with email", Toast.LENGTH_SHORT).show()
                }
                else {
                    Toast.makeText(this, "Unable to send password reset email", Toast.LENGTH_SHORT).show()
                }
                Log.e("Send password reset email : ", "FAILURE")
            }
    }

    private fun closeKeyboard() {
        try {
            val inputManager: InputMethodManager =getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager
            inputManager.hideSoftInputFromWindow(currentFocus.windowToken, InputMethodManager.SHOW_FORCED)
        } catch (e: IllegalStateException) {
            Log.d("Close Keyboard", "Keyboard already closed")
        }
    }


}
