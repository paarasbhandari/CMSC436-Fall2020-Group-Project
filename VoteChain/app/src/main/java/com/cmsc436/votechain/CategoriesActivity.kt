package com.cmsc436.votechain

import android.content.Intent
import android.os.Bundle
import android.util.Log
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


    companion object {

        private const val REQUEST_CODE = 1
        private const val CATEGORY_KEY = "CATEGORY_KEY"
        private const val VOTE_KEY = "VOTE_KEY"
        private const val VOTE_CATEGORY = "VOTE_CATEGORY"
        private const val VOTE_VALUE = "VOTE_VALUE"
        private const val TAG = "Votechain-CategoriesActivity"

    }

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
            startActivityForResult(intent, REQUEST_CODE)

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

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        Log.d(TAG, "Entered onActivityResult")
        super.onActivityResult(requestCode, resultCode, data)

        if (requestCode == REQUEST_CODE) {

            val cat = data!!.getStringExtra(VOTE_CATEGORY)
            val vote = data.getStringExtra(VOTE_VALUE)
            Log.d(TAG, "$cat\n$vote\n")

            if (resultCode == RESULT_OK) {

            }
        }
    }

}