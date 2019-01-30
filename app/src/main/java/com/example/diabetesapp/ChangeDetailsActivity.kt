package com.example.diabetesapp

import android.content.Context
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
import com.google.firebase.firestore.FirebaseFirestore


class ChangeDetailsActivity : AppCompatActivity() {

    // UI Elements

    private var firstNameEditText: EditText? = null
    private var lastNameEditText: EditText? = null
    private var emailEditText: EditText? = null
    private var passwordEditText: EditText? = null
    private var updateButton: Button? = null
    private var progressBar: ProgressBar? = null

    // Firebase Ref

    private var auth: FirebaseAuth? = null
    private var database: FirebaseFirestore? = null


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_change_details)

        initialise()
    }

    private fun initialise() {
        firstNameEditText = findViewById<View>(R.id.first_name_input) as EditText
        lastNameEditText = findViewById<View>(R.id.last_name_input) as EditText
        emailEditText = findViewById<View>(R.id.email_input) as EditText
        passwordEditText = findViewById<View>(R.id.password_input) as EditText
        updateButton = findViewById<View>(R.id.update_button) as Button
        progressBar = findViewById<View>(R.id.progress_bar) as ProgressBar
        progressBar?.visibility = View.INVISIBLE

        auth = FirebaseAuth.getInstance()
        database = FirebaseFirestore.getInstance()

        fillTextFields()

        updateButton!!.setOnClickListener {
            updateDetails()
            finish()
        }


    }

    private fun fillTextFields() {
        val TAG = "Fill account details"
        val uid = auth!!.currentUser!!.uid
        val userRef = database!!.collection("Users").document(uid)
        userRef.get()
            .addOnSuccessListener { documentSnapshot ->
                val user = documentSnapshot.toObject(User::class.java)
                val firstName = user!!.firstName
                val lastName = user!!.lastName
                val email = user!!.email
                firstNameEditText!!.append(firstName)
                lastNameEditText!!.append(lastName)
                emailEditText!!.append(email)
                Log.d(TAG, "UpdateDetails text fields filled with account details")
            }
            .addOnFailureListener { e ->
                Log.w(TAG, "Error auto filling UpdateDetails fields", e)
            }
    }


    private fun updateDetails() {

        val firstName = firstNameEditText!!.text.toString()
        val lastName = lastNameEditText!!.text.toString()
        val email = emailEditText!!.text.toString()
        val password = passwordEditText!!.text.toString()

        // Validate user input (not empty)
        if (!TextUtils.isEmpty(firstName) && !TextUtils.isEmpty(lastName)
            && !TextUtils.isEmpty(email)) {

            progressBar?.visibility = View.VISIBLE

            val userId = auth!!.currentUser!!.uid
            val user = auth!!.currentUser

            user!!.updateEmail(email)
                .addOnSuccessListener { Log.d("Update email", "Email successfully updated") }
                .addOnFailureListener { e -> Log.w("Update email", "Error updating email", e) }

            updateUserModel(userId, firstName!!, lastName!!, email!!)

            if(!TextUtils.isEmpty(password)) {
                user!!.updatePassword(password)
                    .addOnSuccessListener { Log.d("Update password", "Password successfully updated") }
                    .addOnFailureListener { e -> Log.w("Update password", "Error updating password", e) }
            }

            progressBar?.visibility = View.INVISIBLE
            Toast.makeText(this, "Successfully updated", Toast.LENGTH_SHORT).show()

        }
        else {
            Toast.makeText(this, "Details can't be blank", Toast.LENGTH_SHORT).show()
        }


    }

    private fun updateUserModel(uid: String, _firstName: String, _lastName: String, _email: String) {

        val user = User(uid, _firstName, _lastName, _email)
        database!!.collection("Users").document(uid)
            .set(user)
            .addOnSuccessListener { Log.d("Update user details", "DocumentSnapshot successfully written!") }
            .addOnFailureListener { e -> Log.w("Update user details", "Error writing document", e) }
    }


}
