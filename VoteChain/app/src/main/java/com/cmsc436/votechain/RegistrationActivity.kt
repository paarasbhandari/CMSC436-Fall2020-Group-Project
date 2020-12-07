package com.cmsc436.votechain

import android.content.Intent
import android.os.Bundle
import android.view.Menu
import android.view.MenuItem
import android.widget.Button
import android.widget.EditText
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseAuthUserCollisionException
import com.google.firebase.auth.FirebaseAuthWeakPasswordException


private lateinit var auth: FirebaseAuth
private lateinit var emailEditText: EditText
private lateinit var passwordEditText: EditText
private lateinit var passwordCnfEditText: EditText
private lateinit var registerBtn: Button


class RegistrationActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(com.cmsc436.votechain.R.layout.activity_registration)

        auth = FirebaseAuth.getInstance()
        emailEditText = findViewById(com.cmsc436.votechain.R.id.email)
        passwordEditText = findViewById(com.cmsc436.votechain.R.id.password)
        passwordCnfEditText = findViewById(com.cmsc436.votechain.R.id.cnf_password)
        registerBtn = findViewById(com.cmsc436.votechain.R.id.register)
        registerBtn!!.setOnClickListener {register()}

    }

    // Handle Registering a new user to the Firebase
    private fun register() {

        val email: String = emailEditText?.text.toString()
        val password = passwordEditText?.text.toString()
        val cnfPassword = passwordCnfEditText?.text.toString()

        if (false == handleErrors(email, password,cnfPassword)) return

        // Create new user with email and password
        // handle firebase errors and output
        // specific toast messages
        auth.createUserWithEmailAndPassword(email, password)
            .addOnCompleteListener(this) { task ->
                if (task.isSuccessful) {
                    Toast.makeText(applicationContext, "Registration successful", Toast.LENGTH_LONG).show()
                    val intent = Intent(this@RegistrationActivity, CategoriesActivity::class.java)
                    startActivity(intent)
                } else {
                    var message = "An Unknown Exception \noccurred during registration"
                    try {
                        throw task.exception!!
                    } catch (e: FirebaseAuthWeakPasswordException) {
                        message = e.message.toString()
                    } catch (e: FirebaseAuthUserCollisionException) {
                        message = e.message.toString()
                    } finally {
                        Toast.makeText(baseContext, message,Toast.LENGTH_SHORT).show()
                    }
                }
            }
    }
    
    // Cleanly handle UI based registration errors 
    private fun handleErrors(email:String, password:String,cnfPassword:String) : Boolean {
        var output = true
        if (email.isEmpty()){
            Toast.makeText(applicationContext, "Please enter email...", Toast.LENGTH_LONG).show()
            output = false
        }
        if (password.isEmpty()){
            Toast.makeText(applicationContext, "Please enter password...", Toast.LENGTH_LONG).show()
            output = false
        }
        if (cnfPassword.isEmpty()){
            Toast.makeText(applicationContext, "Please confirm password...", Toast.LENGTH_LONG).show()
            output = false
        }
        if (!password.equals(cnfPassword)){
            Toast.makeText(
                applicationContext,
                "Passwords did not match! Please try again.",
                Toast.LENGTH_LONG
            ).show()
            output = false
        }
        return output
    }
}
