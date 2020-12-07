package com.cmsc436.votechain

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.Button
import android.widget.EditText
import android.widget.Toast
import com.google.firebase.auth.FirebaseAuth

private lateinit var auth: FirebaseAuth
private lateinit var emailEditText: EditText
private lateinit var passwordEditText: EditText
private lateinit var loginBtn: Button
private lateinit var registerBtn: Button

class LoginActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        auth = FirebaseAuth.getInstance()
        auth.addAuthStateListener {
            val user = auth.currentUser
            if (user!=null){
                val intent = Intent(this@LoginActivity, CategoriesActivity::class.java)
                startActivity(intent)
            } else {
                setContentView(R.layout.activity_login)

                emailEditText = findViewById(R.id.email)
                passwordEditText = findViewById(R.id.password)
                loginBtn = findViewById(R.id.login)
                loginBtn!!.setOnClickListener { logIn() }
                registerBtn = findViewById(R.id.register)
                registerBtn!!.setOnClickListener {
                    val intent = Intent(this@LoginActivity, RegistrationActivity::class.java)
                    startActivity(intent)
                }
            }
        }
    }

    // Handle User sign in with email and password
    private fun logIn() {
        val email: String = emailEditText?.text.toString()
        val password = passwordEditText?.text.toString()

        if (email.isEmpty()){
            Toast.makeText(applicationContext, "Please enter email...", Toast.LENGTH_LONG).show()
            return
        }

        if (password.isEmpty()){
            Toast.makeText(applicationContext, "Please enter password...", Toast.LENGTH_LONG).show()
            return
        }

        // Use firebase authentication to 
        // finish sign in process
        auth!!.signInWithEmailAndPassword(email, password)
            .addOnCompleteListener{task ->
                if (task.isSuccessful){
                    Toast.makeText(applicationContext, "Login successful", Toast.LENGTH_LONG).show()
                    val intent = Intent(this@LoginActivity, CategoriesActivity::class.java)
                    startActivity(Intent(intent))
                } else
                    Toast.makeText(applicationContext, "Login failed! Please try again later.", Toast.LENGTH_LONG).show()
            }
    }
}
