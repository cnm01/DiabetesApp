package com.example.diabetesapp

import android.content.Intent
import android.os.Bundle
import android.support.v7.app.AppCompatActivity
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseUser
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.FirebaseFirestoreSettings

class SplashActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_splash)

        // Treats java.util.date objects as com.google.firebase.Timestamp objects
        // (Solves warning given in Logcat)
        // -> java.util.date behaviour will change in firestore and your app may break
        // Refactor usages as such:
        //        // Old:
        //        val date = snapshot.getDate("created_at")
        //        // New:
        //        val timestamp = snapshot.getTimestamp("created_at")
        //        val date = timestamp.toDate()
        val firestore = FirebaseFirestore.getInstance()
        val settings = FirebaseFirestoreSettings.Builder()
            .setTimestampsInSnapshotsEnabled(true)
            .build()
        firestore.firestoreSettings = settings



        var user: FirebaseUser? = FirebaseAuth.getInstance().currentUser

        if(user != null){
            val intent = Intent(this, HomeActivity::class.java)
            startActivity(intent)
            finish()
        }
        else {
            val intent = Intent(this, LoginActivity::class.java)
            startActivity(intent)
            finish()
        }


    }
}
