package com.example.diabetesapp

import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.support.v7.app.AppCompatActivity
import android.text.TextUtils
import android.util.Log
import android.view.View
import android.view.inputmethod.InputMethodManager
import android.widget.Button
import android.widget.EditText
import android.widget.ProgressBar
import android.widget.Toast
import com.google.firebase.auth.EmailAuthProvider
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore

class DeleteAccountActivity : AppCompatActivity() {

    // UI Elements
    private var emailEditText: EditText? = null
    private var passwordEditText: EditText? = null
    private var deleteButton: Button? = null
    private var progressBar: ProgressBar? = null

    // Firebase Ref
    private var auth: FirebaseAuth? = null
    private var database: FirebaseFirestore? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_delete_account)
        initialise()
    }

    private fun initialise() {
        emailEditText = findViewById<View>(R.id.email_input) as EditText
        passwordEditText = findViewById<View>(R.id.password_input) as EditText
        deleteButton = findViewById<View>(R.id.delete_button) as Button
        progressBar = findViewById<View>(R.id.progress_bar) as ProgressBar
        progressBar?.visibility = View.INVISIBLE

        auth = FirebaseAuth.getInstance()
        database = FirebaseFirestore.getInstance()

        deleteButton!!.setOnClickListener{
            closeKeyboard()
            deleteAccount()
        }
    }


    private fun deleteAccount() {
    // Deletes user accounts
    //  -> user must be recently authenticated for this to work

        val tag = "Delete User Account"
        val user = auth!!.currentUser
        val email = emailEditText!!.text.toString()
        val password = passwordEditText!!.text.toString()

        if(!TextUtils.isEmpty(email) && !TextUtils.isEmpty(password)) {
            progressBar?.visibility = View.VISIBLE
            val credential = EmailAuthProvider.getCredential(email, password)
            user!!.reauthenticate(credential)
                .addOnSuccessListener {
                    Log.d(tag, "Successfully re-authenticated user")
                    user.delete()
                        .addOnSuccessListener {
                            progressBar?.visibility = View.INVISIBLE
                            Log.d(tag, "Successfully deleted user")
                            Toast.makeText(this, "Account Deleted", Toast.LENGTH_SHORT).show()
                            val intent = Intent(this, LoginActivity::class.java)
                            startActivity(intent)
                            finish()
                        }
                        .addOnFailureListener { e ->
                            Log.w(tag, "Error deleting account", e)
                            progressBar?.visibility = View.INVISIBLE
                            Toast.makeText(this, "Unable to delete account", Toast.LENGTH_SHORT).show()
                        }
                }
                .addOnFailureListener { e ->
                    Log.w(tag, "Error re-authenticating user", e)
                    progressBar?.visibility = View.INVISIBLE
                    Toast.makeText(this, "Unable to authenticate account", Toast.LENGTH_SHORT).show()
                }
        }
        else {
            Toast.makeText(this, "Details cannot be blank", Toast.LENGTH_SHORT).show()
        }
    }

    private fun closeKeyboard() {
        val inputManager: InputMethodManager =getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager
        inputManager.hideSoftInputFromWindow(currentFocus.windowToken, InputMethodManager.SHOW_FORCED)
    }

    override fun onBackPressed() {
        super.onBackPressed()
        finish()
    }
}
