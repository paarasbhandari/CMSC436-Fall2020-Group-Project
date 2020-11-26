package com.cmsc436.votechain

import android.content.Intent
import android.os.Bundle
import android.view.View
import android.widget.Button
import android.widget.RadioButton
import android.widget.RadioGroup
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity


//private var mVotingRadioGroup: RadioGroup? = null   //The RadioGroup variable
//private var mVoteButton: RadioButton? = null        //The Button user selects
var radioGroup: RadioGroup? = null
var radioBtn1: RadioButton? = null
var radioBtn2: RadioButton? = null
var radioBtn3: RadioButton? = null
var radioBtn4: RadioButton? = null
var voteValue: String? = null
var votingList: Array<String>? = null


class VotingActivity : AppCompatActivity() {

    companion object {

        private const val RESULT_CODE = 1
        private const val VOTE_VALUE = "VOTE_VALUE"
        private const val VOTE_CATEGORY = "VOTE_CATEGORY"
        private const val TAG = "Votechain-CategoriesActivity"

    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_voting)



        radioGroup = findViewById<View>(R.id.listViewVotes) as RadioGroup
        radioBtn1 = findViewById<RadioButton>(R.id.Vote1)
        radioBtn2 = findViewById<RadioButton>(R.id.Vote2)
        radioBtn3 = findViewById<RadioButton>(R.id.Vote3)
        radioBtn4 = findViewById<RadioButton>(R.id.Vote4)
        var radioBtnList = arrayOf(radioBtn1, radioBtn2, radioBtn3, radioBtn4)

        val category = intent.getStringExtra("CATEGORY")

        var oldVoteValue = ""

        if (category.equals("TV Show")){
            votingList = arrayOf("Breaking Bad", "Friends", "Narcos", "Game of Thrones")
            oldVoteValue = intent.getStringExtra("TV_SHOW_VOTE_VALUE").toString()
        } else if (category.equals("Superhero")){
            votingList = arrayOf("Spiderman", "Batman", "Superman", "Iron Man")
            oldVoteValue = intent.getStringExtra("SUPERHERO_VOTE_VALUE").toString()
        } else if (category.equals("Beverage")){
            votingList = arrayOf("Tea", "Beer", "Coffee", "Wine")
            oldVoteValue = intent.getStringExtra("BEVERAGE_VOTE_VALUE").toString()
        }

        if (oldVoteValue != null && !oldVoteValue.isEmpty() && !oldVoteValue.equals("null")){
            radioBtnList.get(getIndex(votingList!!, oldVoteValue))?.isChecked = true
        }


        setOptionsText()

        radioGroup!!.setOnCheckedChangeListener(RadioGroup.OnCheckedChangeListener { group, checkedId ->
            // This will get the radiobutton that has changed in its check state
            val checkedRadioButton = group.findViewById<View>(checkedId) as RadioButton
            // This puts the value (true/false) into the variable
            val isChecked = checkedRadioButton.isChecked
            // If the radiobutton that has changed in check state is now checked...
            if (isChecked) {
                voteValue = checkedRadioButton.text as String
            }
        })

        val submit: Button = findViewById(R.id.submit)

        submit.setOnClickListener(View.OnClickListener { i ->
            val returnIntent = Intent()
            returnIntent.putExtra(VOTE_CATEGORY, category)
            returnIntent.putExtra(VOTE_VALUE, voteValue)
            setResult(RESULT_CODE, returnIntent)
            finish()
        })


    }

    private fun setOptionsText(){
        radioBtn1?.text = votingList?.get(0)
        radioBtn2?.text = votingList?.get(1)
        radioBtn3?.text = votingList?.get(2)
        radioBtn4?.text = votingList?.get(3)
    }

    private fun getIndex(list: Array<String>, a: String): Int{
        for (x in (0..3)){
            if (list.get(x).equals(a)){
                return x
            }
        }
        return -1
    }


}