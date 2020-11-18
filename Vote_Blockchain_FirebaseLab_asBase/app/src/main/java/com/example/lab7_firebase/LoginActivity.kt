package com.example.lab7_firebase

import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.Button
import android.widget.EditText
import android.widget.ProgressBar
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.FirebaseDatabase

class LoginActivity : AppCompatActivity() {
    // private var mDatabaseReference: DatabaseReference? = null
    // private var mDatabase: FirebaseDatabase? = null
    private var userEmail: EditText? = null
    private var userPassword: EditText? = null
    private var loginBtn: Button? = null
    private var progressBar: ProgressBar? = null

    private var mAuth: FirebaseAuth? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_login)

        // mDatabase = FirebaseDatabase.getInstance()
        // mDatabaseReference = mDatabase!!.reference.child("Users")
        mAuth = FirebaseAuth.getInstance()

        userEmail = findViewById(R.id.email)
        userPassword = findViewById(R.id.password)
        loginBtn = findViewById(R.id.login)
        progressBar = findViewById(R.id.progressBar)

        loginBtn!!.setOnClickListener { loginUserAccount() }
    }

    // TODO: Allow the user to log into their account
    // If the email and password are not empty, try to log in
    // If the login is successful, store info into intent and launch DashboardActivity
    private fun loginUserAccount() {

        Log.d(TAG, "Entered loginUserAccount()")

        val email = userEmail!!.text.toString()
        val pass = userPassword!!.text.toString()

        // separating the two checks so Toast messages can be accurately displayed to user
        if (email.isEmpty()) {
            Toast.makeText(applicationContext, "Please enter email..", Toast.LENGTH_LONG).show()
            return // is this safe? could these lines be skipped?
        }
        if (pass.isEmpty()) {
            Toast.makeText(applicationContext, "Please enter password!", Toast.LENGTH_LONG).show()
            return
        }

        Log.d(TAG, "Email= $email | Password= $pass")

        mAuth!!.signInWithEmailAndPassword(email, pass)
            .addOnCompleteListener { task ->
                progressBar!!.visibility = View.GONE
                if (task.isSuccessful) {
                    Toast.makeText(applicationContext, "Login successful, launching Dashboard", Toast.LENGTH_SHORT).show()
                    startActivity(
                        (Intent(this@LoginActivity, DashboardActivity::class.java)
                        .putExtra(USER_ID, mAuth!!.currentUser?.uid)
                        .putExtra(USER_EMAIL, email))
                    )
                } else {
                    Toast.makeText(applicationContext, "Login failed! Please try harder..", Toast.LENGTH_LONG).show()
                }
            }
    }

    companion object {
        const val USER_EMAIL = "com.example.tesla.myhomelibrary.useremail"
        const val USER_ID = "com.example.tesla.myhomelibrary.userid"

        const val TAG = "Lab7-Firebase-LoginActivity"
    }
}