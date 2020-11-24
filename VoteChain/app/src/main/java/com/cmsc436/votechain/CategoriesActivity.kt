package com.cmsc436.votechain

import android.content.Intent
import android.os.Bundle
import android.view.Menu
import android.view.MenuItem
import android.view.View
import android.widget.AdapterView
import android.widget.ArrayAdapter
import android.widget.ListView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.google.firebase.auth.FirebaseAuth

private lateinit var auth: FirebaseAuth
internal lateinit var listViewCategories: ListView


class CategoriesActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_categories)

        listViewCategories = findViewById<View>(R.id.listViewCategories) as ListView
        val categoryList = arrayOf("TV Show", "Superhero", "Beverage")
        val adapter = ArrayAdapter(this, android.R.layout.simple_list_item_1, categoryList)
        listViewCategories.adapter = adapter

        listViewCategories.onItemClickListener = AdapterView.OnItemClickListener { adapterView, view, i, l ->
            //getting the selected artist
            val category = categoryList[i]

            //creating an intent
            val intent = Intent(applicationContext, VotingActivity::class.java)
            intent.putExtra("CATEGORY", category)
            startActivityForResult(intent, 1)


        }

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

    override fun onBackPressed() {
        //disabling back button
        Toast.makeText(applicationContext, "Click Options Menu to Log Out.", Toast.LENGTH_LONG).show()
    }

}