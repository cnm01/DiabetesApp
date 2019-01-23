package com.example.diabetesapp

import android.content.Intent
import android.os.Bundle
import android.support.v7.app.AppCompatActivity
import android.view.View
import android.widget.Button
import com.google.firebase.auth.FirebaseAuth

class MainActivity : AppCompatActivity() {

    private var signOutButton: Button? = null

    // Firebase References
    private var auth: FirebaseAuth? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        initialise()


    }

    private fun initialise() {
        signOutButton = findViewById<View>(R.id.sign_out_button) as Button
        auth = FirebaseAuth.getInstance()

        signOutButton?.setOnClickListener{
            auth?.signOut()

            val intent = Intent(this, SplashActivity::class.java)
//            intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP)
//            intent.putExtra("EXIT", true)
            startActivity(intent)
            finish()
        }
    }
}
