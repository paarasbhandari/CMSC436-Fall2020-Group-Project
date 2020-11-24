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

    private fun register() {

        val email: String = emailEditText?.text.toString()
        val password = passwordEditText?.text.toString()
        val cnfPassword = passwordCnfEditText?.text.toString()

        if (email.isEmpty()){
            Toast.makeText(applicationContext, "Please enter email...", Toast.LENGTH_LONG).show()
            return
        }

        if (password.isEmpty()){
            Toast.makeText(applicationContext, "Please enter password...", Toast.LENGTH_LONG).show()
            return
        }

        if (cnfPassword.isEmpty()){
            Toast.makeText(applicationContext, "Please confirm password...", Toast.LENGTH_LONG).show()
            return
        }

        if (!password.equals(cnfPassword)){
            Toast.makeText(
                applicationContext,
                "Passwords did not match! Please try again.",
                Toast.LENGTH_LONG
            ).show()
            return
        }

        auth.createUserWithEmailAndPassword(email, password)
            .addOnCompleteListener(this) { task ->
                if (task.isSuccessful) {
                    Toast.makeText(applicationContext, "Registration successful", Toast.LENGTH_LONG).show()
                    val intent = Intent(this@RegistrationActivity, CategoriesActivity::class.java)
                    startActivity(intent)
                } else {
                    Toast.makeText(
                        baseContext, "Registration failed",
                        Toast.LENGTH_SHORT
                    ).show()
                }
            }
    }




}