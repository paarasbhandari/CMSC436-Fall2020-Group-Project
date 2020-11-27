package com.cmsc436.votechain

import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.Button
import android.widget.RadioButton
import android.widget.RadioGroup
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.children
import androidx.core.view.get

var radioGroup: RadioGroup? = null
var submit: Button? = null
var votingList: Array<String>? = null
var category: String? = null
var oldVote: String? = null

class VotingActivity : AppCompatActivity() {

    companion object {
        private const val TAG = "Votechain-VotingActivity"
        private const val VOTE_VALUE = "VOTE_VALUE"
        private const val VOTE_CATEGORY = "VOTE_CATEGORY"
        /*
        private val tvshowVotingList = arrayOf("Breaking Bad", "Friends", "Narcos", "Game of Thrones")
        private val superheroVotingList = arrayOf("Spiderman", "Batman", "Superman", "Iron Man")
        */
        private val beverageVotingList = arrayOf("Tea", "Beer", "Coffee", "Wine")
        private val superheroVotingList: Array<String> =
            arrayOf("Batman", "Superman", "Spiderman", "Wolverine")
        private val tvshowVotingList : Array<String> =
            arrayOf("South Park", "Grey's Anatomy", "Game of Thrones",
                "It's Always Sunny in Philadelphia",
                "Umbrella Academy")
/*        private val beverageVotingList: Array<String> =
            arrayOf("Coke", "Pepsi", "Dr. Pepper", "Sprite",
                "Mountain Dew", "Mr. Pibb", "Canada Dry",
                "Mountain Lightning", "Mellow Yellow",
                "Fanta Orange", "Coke Cherry")*/

    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_voting)

        radioGroup = findViewById<View>(R.id.listViewVotes) as RadioGroup
        submit = findViewById(R.id.submit)

        category = intent.getStringExtra("CATEGORY")

        submit!!.setOnClickListener(View.OnClickListener {
            Log.d(TAG, "Entered setOnClickListener")
            val voteId = radioGroup!!.checkedRadioButtonId // get id of checked button - id is generated dynamically
            val voteString = findViewById<RadioButton>(voteId).text

            Toast.makeText(
                this,
                "Submit Clicked : $voteString", Toast.LENGTH_LONG
            ).show()

            val returnIntent = Intent()
                .putExtra(VOTE_CATEGORY, category)
                .putExtra(VOTE_VALUE, voteString)
            setResult(RESULT_OK, returnIntent)
            finish()
        })

        setupCategory() // make radio buttons for each category

    }

    private fun setupCategory() {
        // get category to display along with old vote
        when (category) {
            "TV Show" -> {
                votingList = tvshowVotingList
                oldVote = intent.getStringExtra(CategoriesActivity.TV_SHOW_VOTE_KEY)
            }
            "Superhero" -> {
                votingList = superheroVotingList
                oldVote = intent.getStringExtra(CategoriesActivity.SUPER_HERO_VOTE_KEY)
            }
            "Beverage" -> {
                votingList = beverageVotingList
                oldVote = intent.getStringExtra(CategoriesActivity.BEVERAGE_VOTE_KEY)
            }
            null -> return
        }

        // create radio buttons
        for (i in 0 until votingList!!.count()) {
            val radioButton = RadioButton(this)
            radioButton.text = votingList!![i] // set text of radio button array index value
            radioGroup!!.addView(radioButton)
        }

        // set old vote
        if (oldVote != null)
            (radioGroup!!.getChildAt(votingList!!.indexOf(oldVote)) as RadioButton).isChecked = true
    }
}