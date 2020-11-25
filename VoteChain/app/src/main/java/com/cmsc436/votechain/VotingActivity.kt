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
var radioButton: RadioButton? = null
var textView: TextView? = null
var votingList: Array<String>? = null


class VotingActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_voting)

        //radioGroup = findViewById(R.id.listViewVotes)
        radioGroup = findViewById<View>(R.id.listViewVotes) as RadioGroup


        val submit: Button = findViewById(R.id.submit)

        val category = intent.getStringExtra("CATEGORY")
        if (category.equals("Superhero")){
            votingList = arrayOf("Batman", "Superman", "Spiderman", "Wolverine")
            var radioBtn1 = radioGroup?.findViewById<RadioButton>(R.id.Vote1)
            radioBtn1?.text = "Batman"
            var radioBtn2 = radioGroup?.findViewById<RadioButton>(R.id.Vote2)
            radioBtn2?.text = "Superman"
            var radioBtn3 = radioGroup?.findViewById<RadioButton>(R.id.Vote3)
            radioBtn3?.text = "Spiderman"
        }


        val counter = 0

        radioGroup!!.setOnCheckedChangeListener(RadioGroup.OnCheckedChangeListener { group, checkedId ->
            // This will get the radiobutton that has changed in its check state
            val checkedRadioButton = group.findViewById<View>(checkedId) as RadioButton
            // This puts the value (true/false) into the variable
            val isChecked = checkedRadioButton.isChecked
            // If the radiobutton that has changed in check state is now checked...
            if (isChecked) {
                var voteValue = checkedRadioButton.text

            }

        })

//        submit.setOnClickListener(View.OnClickListener { i ->
//            val vote = votingList[counter]
//            val vote_value = counter
//            val radioId = radioGroup!!.checkedRadioButtonId
//            radioButton = findViewById(radioId)
//            textView!!.text = "Your choice: " + radioButton!!.getText()
//            val intent = Intent(applicationContext, VotingActivity::class.java).putExtra(
//                "VOTE_CATEGORY",
//                vote
//            ).putExtra("VOTE_VALUE", vote_value)
//            startActivityForResult(intent, 1)
//        })


        /** mVotingRadioGroup.onItemClickListener = AdapterView.OnItemClickListener { adapterView, view, i, l ->
        val vote = votingList[i]
        val vote_value = i
        //creating an intent
        val intent = Intent(applicationContext, VotingActivity::class.java).putExtra("VOTE_CATEGORY", vote).putExtra("VOTE_VALUE", vote_value)
        intent.putExtra("CATEGORY", vote)
        startActivityForResult(intent, 1)
        }
         **/
    }

    fun checkButton(v: View?) {
        val radioId = radioGroup!!.checkedRadioButtonId
        radioButton = findViewById(radioId)
        Toast.makeText(
            this, "Selected Radio Button: " + radioButton!!.getText(),
            Toast.LENGTH_SHORT
        ).show()
    }
}