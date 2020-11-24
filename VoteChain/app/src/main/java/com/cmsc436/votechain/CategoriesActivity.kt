package com.cmsc436.votechain

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.Menu
import android.view.MenuItem
import com.google.firebase.auth.FirebaseAuth

private lateinit var auth: FirebaseAuth

class CategoriesActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_categories)
        auth = FirebaseAuth.getInstance()
        auth.addAuthStateListener {
            val user = auth.currentUser
            if (user==null){
                val intent = Intent(this@CategoriesActivity, LoginActivity::class.java)
                startActivity(intent)
            }
        }
    }

    override fun onCreateOptionsMenu(menu: Menu?): Boolean {
        val inflater = menuInflater
        inflater.inflate(R.menu.main_menu, menu)
        return true
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        return when (item.itemId) {
            R.id.item1 -> {
                auth.signOut()
                true
            }
            else -> super.onOptionsItemSelected(item)
        }
    }


}