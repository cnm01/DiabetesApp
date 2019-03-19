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
import com.example.diabetesapp.model.User
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseAuthInvalidCredentialsException
import com.google.firebase.auth.FirebaseAuthUserCollisionException
import com.google.firebase.auth.FirebaseAuthWeakPasswordException
import com.google.firebase.firestore.FirebaseFirestore

class RegisterActivity : AppCompatActivity() {

    // UI Elements
    private var firstNameEditText: EditText? = null
    private var lastNameEditText: EditText? = null
    private var emailEditText: EditText? = null
    private var passwordEditText: EditText? = null
    private var registerButton: Button? = null
    private var progressBar: ProgressBar? = null

    // Firebase References
    private var database: FirebaseFirestore? = null
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

        database = FirebaseFirestore.getInstance()
        auth = FirebaseAuth.getInstance()

        registerButton!!.setOnClickListener{

            closeKeyboard()
            createNewAccount()
        }

    }

    private fun createNewAccount() {

        firstName = firstNameEditText!!.text.toString()
        lastName = lastNameEditText!!.text.toString()
        email = emailEditText!!.text.toString()
        password = passwordEditText!!.text.toString()

        // Validate user input (not empty)
        if(!TextUtils.isEmpty(firstName) && !TextUtils.isEmpty(lastName)
            && !TextUtils.isEmpty(email) && !TextUtils.isEmpty(password)) {

            progressBar?.visibility = View.VISIBLE

            // Create new FirebaseAuth user account
            auth!!
                .createUserWithEmailAndPassword(email!!, password!!)
                .addOnCompleteListener(this) { task ->

                    progressBar?.visibility = View.INVISIBLE

                    if(task.isSuccessful) {

                        Log.d("CreateAccountActivity", "createUserWithEmail:success")
                        val userId = auth!!.currentUser!!.uid

                        writeNewUser(userId, firstName!!, lastName!!, email!!)

                        Toast.makeText(this, "Registered", Toast.LENGTH_SHORT).show()

                        updateUserInfoAndUI()

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
                .addOnFailureListener { e ->
                    when (e) {
                        is FirebaseAuthWeakPasswordException -> Toast.makeText(this, "Password must be at least 6 characters", Toast.LENGTH_SHORT).show()
                        is FirebaseAuthUserCollisionException -> Toast.makeText(this, "Email already in use", Toast.LENGTH_SHORT).show()
                        is FirebaseAuthInvalidCredentialsException -> { Toast.makeText(this, "Invalid credentials", Toast.LENGTH_SHORT).show() }
                        else -> {
                            Log.w("Create new user", "ERROR", e)
                            progressBar?.visibility = View.INVISIBLE
                            Toast.makeText(this, "Account creation failed", Toast.LENGTH_SHORT).show()
                        }
                    }
                }
        }
        else {
            Toast.makeText(this, "Details can't be blank", Toast.LENGTH_SHORT).show()
            progressBar?.visibility = View.INVISIBLE
        }

    }

    private fun writeNewUser(uid: String, _firstName: String, _lastName: String, _email: String) {

        // Create new User and push data to Firestore
        val user = User(uid, _firstName, _lastName, _email)
        database!!.collection("Users").document(uid)
            .set(user)
            .addOnSuccessListener { Log.d("Write new user", "DocumentSnapshot successfully written!") }
            .addOnFailureListener { e -> Log.w("Write new user", "Error writing document", e) }
    }

    private fun updateUserInfoAndUI() {
        val intent = Intent(this, HomeActivity::class.java)
        startActivity(intent)
        finish()
    }

    private fun closeKeyboard() {
        try {
            val inputManager: InputMethodManager =getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager
            inputManager.hideSoftInputFromWindow(currentFocus.windowToken, InputMethodManager.SHOW_FORCED)
        } catch (e: IllegalStateException) {
            Log.d("Close Keyboard", "Keyboard already closed")
        }
    }

    override fun onBackPressed() {
        super.onBackPressed()
        val intent = Intent(this, LoginActivity::class.java)
        startActivity(intent)
        finish()
    }


}
