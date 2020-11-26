package com.cmsc436.votechain

import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.view.Menu
import android.view.MenuItem
import android.view.View
import android.widget.*
import androidx.appcompat.app.AppCompatActivity
import com.google.firebase.auth.FirebaseAuth

private lateinit var auth: FirebaseAuth
internal lateinit var listViewCategories: ListView
private var beverageVoteValue: String ?= null
private var tvShowVoteValue: String ?= null
private var superheroVoteValue: String ?= null
private var submitBtn: Button?= null


class CategoriesActivity : AppCompatActivity() {


    companion object {

        private const val REQUEST_CODE = 1
        private const val CATEGORY_KEY = "CATEGORY_KEY"
        private const val VOTE_KEY = "VOTE_KEY"
        private const val VOTE_CATEGORY = "VOTE_CATEGORY"
        private const val VOTE_VALUE = "VOTE_VALUE"
        private const val TAG = "Votechain-CategoriesActivity"
        private const val BLANK_VOTE = ""

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
            intent.putExtra("SUPERHERO_VOTE_VALUE", superheroVoteValue)
            intent.putExtra("TV_SHOW_VOTE_VALUE", tvShowVoteValue)
            intent.putExtra("BEVERAGE_VOTE_VALUE", beverageVoteValue)
            startActivityForResult(intent, REQUEST_CODE)

        }

        auth = FirebaseAuth.getInstance()
        auth.addAuthStateListener {
            val user = auth.currentUser
            if (user==null){
                resetVotes()
                val intent = Intent(this@CategoriesActivity, LoginActivity::class.java)
                startActivity(intent)
            }
        }

        submitBtn = findViewById<Button>(R.id.submit_vote)
        submitBtn?.setOnClickListener(View.OnClickListener { i ->

            if (tvShowVoteValue == null){
                Toast.makeText(applicationContext, "Please vote for TV Show", Toast.LENGTH_LONG).show()
            }  else if (superheroVoteValue == null){
                Toast.makeText(applicationContext, "Please vote for Superhero", Toast.LENGTH_LONG).show()
            } else if (beverageVoteValue == null){
                Toast.makeText(applicationContext, "Please vote for Beverage", Toast.LENGTH_LONG).show()
            } else {
                Toast.makeText(applicationContext, "SUBMITTING VOTES", Toast.LENGTH_LONG).show()

            }

        })



    }

    override fun onCreateOptionsMenu(menu: Menu?): Boolean {
        val inflater = menuInflater
        inflater.inflate(R.menu.main_menu, menu)
        return true
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        return when (item.itemId) {
            R.id.item1 -> {
                resetVotes()
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

        if (requestCode == resultCode) {
            super.onActivityResult(requestCode, resultCode, data)
            val category = data!!.getStringExtra(VOTE_CATEGORY)
            val voteValue = data.getStringExtra(VOTE_VALUE)
            if (voteValue != null && !voteValue?.isEmpty()){
                if (category.equals("TV Show")){
                    tvShowVoteValue = voteValue
                } else if (category.equals("Superhero")){
                    superheroVoteValue = voteValue
                } else if (category.equals("Beverage")){
                    beverageVoteValue = voteValue
                }
            }
        }
    }

    private fun resetVotes(){
        tvShowVoteValue = null
        superheroVoteValue = null
        beverageVoteValue = null
    }

}