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
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.FirebaseDatabase


class RegisterActivity : AppCompatActivity() {

    // UI Elements
    private var firstNameEditText: EditText? = null
    private var lastNameEditText: EditText? = null
    private var emailEditText: EditText? = null
    private var passwordEditText: EditText? = null
    private var registerButton: Button? = null
    private var progressBar: ProgressBar? = null

    // Firebase References
    private var databaseRef: DatabaseReference? = null
    private var database: FirebaseDatabase? = null
    private var auth: FirebaseAuth? = null

    // Values
    private var firstName: String? = null
    private var lastName: String? = null
    private var email: String? = null
    private var password: String? = null


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_register)



        initialise()

    }

    private fun initialise() {

        firstNameEditText = findViewById<View>(R.id.first_name_input) as EditText
        lastNameEditText = findViewById<View>(R.id.last_name_input) as EditText
        emailEditText = findViewById<View>(R.id.email_input) as EditText
        passwordEditText = findViewById<View>(R.id.password_input) as EditText
        registerButton = findViewById<View>(R.id.register_button) as Button
        progressBar = findViewById<View>(R.id.progress_bar) as ProgressBar
        progressBar?.visibility = View.INVISIBLE

        database = FirebaseDatabase.getInstance()
        databaseRef = database!!.reference!!.child("Users")
        auth = FirebaseAuth.getInstance()

        registerButton!!.setOnClickListener{
            createNewAccount()
        }

    }

    private fun createNewAccount() {

        firstName = firstNameEditText?.text.toString()
        lastName = lastNameEditText?.text.toString()
        email = emailEditText?.text.toString()
        password = passwordEditText?.text.toString()

        // Validate user input

        if(!TextUtils.isEmpty(firstName) && !TextUtils.isEmpty(lastName)
            && !TextUtils.isEmpty(email) && !TextUtils.isEmpty(password)) {

            progressBar?.visibility = View.VISIBLE

            auth!!
                .createUserWithEmailAndPassword(email!!, password!!)
                .addOnCompleteListener(this) {
                        task -> progressBar?.visibility = View.INVISIBLE

                        if(task.isSuccessful) {

                            Log.d("CreateAccountActivity", "createUserWithEmail:success")
                            val userID = auth!!.currentUser!!.uid
//                            verifyEmail()
                            val currentUserDB = databaseRef!!.child(userID)
                            currentUserDB.child("firstName").setValue(firstName)
                            currentUserDB.child("lastName").setValue(lastName)
                            updateUserInfoAndUI()
                        }
                }
                }
        else {
            Toast.makeText(this, "Details can't be blank", Toast.LENGTH_SHORT).show()
        }

    }

    private fun updateUserInfoAndUI() {
        val intent = Intent(this, MainActivity::class.java)
        startActivity(intent)
        finish()
    }


}
