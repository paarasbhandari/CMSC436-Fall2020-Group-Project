package com.cmsc436.votechain

import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.view.Menu
import android.view.MenuItem
import android.view.View
import android.widget.*
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.FragmentActivity
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener
import java.security.MessageDigest


private lateinit var auth: FirebaseAuth
internal lateinit var listViewCategories: ListView
private var beverageVoteValue: String ?= null
private var tvShowVoteValue: String ?= null
private var superheroVoteValue: String ?= null
private var submitBtn: Button?= null
private var database: FirebaseDatabase? = null


class CategoriesActivity : AppCompatActivity() {


    companion object {

        private const val REQUEST_CODE = 1
        private const val CATEGORY_KEY = "CATEGORY_KEY"
        private const val VOTE_KEY = "VOTE_KEY"
        private const val VOTE_CATEGORY = "VOTE_CATEGORY"
        private const val VOTE_VALUE = "VOTE_VALUE"
        private const val TAG = "Votechain-CategoriesActivity"
        private const val BLANK_VOTE = ""
        const val SUPER_HERO_VOTE_KEY = "SUPERHERO_VOTE_VALUE"
        const val BEVERAGE_VOTE_KEY = "BEVERAGE_VOTE_VALUE"
        const val TV_SHOW_VOTE_KEY = "TV_SHOW_VOTE_VALUE"
        private const val HASH_KEY = "HASH_KEY"
        private const val IS_GENESIS_KEY  = "IS_GENESIS"
        private const val IS_GENESIS_TRUE  = "TRUE"
        private const val IS_GENESIS_FALSE  = "FALSE"
        private const val PREV_HASH_KEY  = "PREV_HASH"

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
            intent.putExtra(SUPER_HERO_VOTE_KEY, superheroVoteValue)
            intent.putExtra(TV_SHOW_VOTE_KEY, tvShowVoteValue)
            intent.putExtra(BEVERAGE_VOTE_KEY, beverageVoteValue)
            startActivityForResult(intent, REQUEST_CODE)
        }

        auth = FirebaseAuth.getInstance()
        auth.addAuthStateListener {
            val user = auth.currentUser
            if (user==null){
                val signOutIntent = Intent(this@CategoriesActivity, LoginActivity::class.java)
                startActivity(signOutIntent)
            }
        }

        database = FirebaseDatabase.getInstance()

        submitBtn = findViewById<Button>(R.id.submit_vote)
        submitBtn?.setOnClickListener(View.OnClickListener { i ->

            if (tvShowVoteValue == null) {
                Toast.makeText(applicationContext, "Please vote for TV Show", Toast.LENGTH_LONG)
                    .show()
            } else if (superheroVoteValue == null) {
                Toast.makeText(applicationContext, "Please vote for Superhero", Toast.LENGTH_LONG)
                    .show()
            } else if (beverageVoteValue == null) {
                Toast.makeText(applicationContext, "Please vote for Beverage", Toast.LENGTH_LONG)
                    .show()
            } else {

                val builder = AlertDialog.Builder(this@CategoriesActivity)
                builder.setMessage("Please confirm your votes. \n\n" + getCnfVotesString())
                    .setCancelable(false)
                    .setPositiveButton("Confirm") { dialog, id ->
                        // Delete selected note from database
                        Log.i(TAG, "Votes Confirmed")

                        val builder = AlertDialog.Builder(this@CategoriesActivity)
                        builder.setMessage("You can only vote once. \nAre you voting for the first time?")
                            .setCancelable(false)
                            .setPositiveButton("Yes") { dialog, id ->
                                // Delete selected note from database
                                Log.i(TAG, "Voting for first time confirmed")
                                Toast.makeText(
                                    applicationContext,
                                    "Submitting your vote. Please wait!",
                                    Toast.LENGTH_SHORT
                                ).show()

                                submitVotes()

                            }
                            .setNegativeButton("No") { dialog, id ->
                                Log.i(TAG, "Not voting for the first time")
                            }
                        val alert = builder.create()
                        alert.show()

                    }
                    .setNegativeButton("Cancel") { dialog, id ->
                        Log.i(TAG, "Cancelled")
                    }
                val alert = builder.create()
                alert.show()

            }
        })
    }

    override fun onCreateOptionsMenu(menu: Menu?): Boolean {
        val inflater = menuInflater
        inflater.inflate(R.menu.main_menu, menu)
        return true
    }

    private fun getVoteString(): String{
        val user = auth.currentUser
        if(user!=null){
            val uid = user.uid
            val s = uid+TV_SHOW_VOTE_KEY+tvShowVoteValue+SUPER_HERO_VOTE_KEY+superheroVoteValue+BEVERAGE_VOTE_KEY+beverageVoteValue
            return s
        }
        return ""
    }

    public fun submitVotes(){
        val ref = database?.getReference("blocks")

        ref?.addListenerForSingleValueEvent(object : ValueEventListener {
            override fun onDataChange(dataSnapshot: DataSnapshot) {
                // This method is called once with the initial value and again
                // whenever data at this location is updated.

                val value = dataSnapshot.getValue()

                val voteStr = getVoteString()
                val hash = getSHA256Hash(voteStr)

                val hashMap: HashMap<String, String> = HashMap<String, String>()
                if (voteStr != "") {

                    superheroVoteValue?.let { hashMap[SUPER_HERO_VOTE_KEY] = it }
                    beverageVoteValue?.let { hashMap[BEVERAGE_VOTE_KEY] = it }
                    tvShowVoteValue?.let { hashMap[TV_SHOW_VOTE_KEY] = it }
                    hashMap[HASH_KEY] = hash

                } else {
                    Toast.makeText(applicationContext, "Vote String is Empty", Toast.LENGTH_LONG)
                        .show()
                }

                var isGenesis = false
                if (value == null) {
                    // no blocks currently, add genesis block
                    hashMap.put(IS_GENESIS_KEY, IS_GENESIS_TRUE)
                    isGenesis = true
                } else {
                    hashMap.put(IS_GENESIS_KEY, IS_GENESIS_FALSE)
                }

                val latestHashRef = database?.getReference("latest_hash")
                latestHashRef?.addListenerForSingleValueEvent(object : ValueEventListener {
                    override fun onDataChange(dataSnapshot: DataSnapshot) {
                        // This method is called once with the initial value and again
                        // whenever data at this location is updated.
                        val value = dataSnapshot.getValue()
                        if (value != null) {
                            val hashStr = value as String
                            hashMap.put(PREV_HASH_KEY, hashStr)
                        }
                        val uid = auth.currentUser?.uid
                        if (uid != null) {

                            // adding new block
                            val ref = database?.getReference("blocks")?.child(uid)
                            ref?.setValue(hashMap)


                            Toast.makeText(
                                applicationContext,
                                "Vote Submitted. Verifying... \nPlease wait!",
                                Toast.LENGTH_LONG
                            ).show()


                            // verify chain

                            database?.getReference("blocks")
                                ?.addListenerForSingleValueEvent(object :
                                    ValueEventListener {
                                    override fun onDataChange(dataSnapshot: DataSnapshot) {
                                        // This method is called once with the initial value and again
                                        // whenever data at this location is updated.
                                        val chain =
                                            dataSnapshot.getValue() as HashMap<String, String>
                                        Log.i(TAG, chain.size.toString())

                                        if (chain.size == 1) {
                                            // only 1 block exists in the list
                                            latestHashRef?.addListenerForSingleValueEvent(object :
                                                ValueEventListener {
                                                override fun onDataChange(dataSnapshot: DataSnapshot) {
                                                    // This method is called once with the initial value and again
                                                    // whenever data at this location is updated.
                                                    val value = dataSnapshot.getValue()
                                                    if (value != null) {
                                                        fraudDetected()
                                                    } else {
                                                        latestHashRef.setValue(hash)
                                                        Toast.makeText(
                                                            applicationContext,
                                                            "Vote Verified! Thank you.",
                                                            Toast.LENGTH_LONG
                                                        ).show()
                                                    }
                                                }

                                                override fun onCancelled(error: DatabaseError) {
                                                }
                                            })
                                        } else {
                                            Log.i(TAG, "VoteChain is longer than 1")
                                            latestHashRef.setValue(hash)
                                            latestHashRef?.addListenerForSingleValueEvent(object :
                                                ValueEventListener {
                                                override fun onDataChange(dataSnapshot: DataSnapshot) {
                                                    // This method is called once with the initial value and again
                                                    // whenever data at this location is updated.
                                                    val value = dataSnapshot.getValue()
                                                    if (value != null) {
                                                        val prevHashStr = value as String

                                                        val ref = database?.getReference("blocks")
                                                        ref?.addListenerForSingleValueEvent(object :
                                                            ValueEventListener {
                                                            override fun onDataChange(dataSnapshot: DataSnapshot) {
                                                                // This method is called once with the initial value and again
                                                                // whenever data at this location is updated.

                                                                val map = dataSnapshot.getValue() as HashMap<String, HashMap<String, String>>
                                                                Log.i(TAG, value.toString())
                                                                val keySet = map.keys

                                                                var currHash = prevHashStr
                                                                var reachedGenesis = false
                                                                while(true){
                                                                    var foundHash = false
                                                                    map.forEach { (key, value) ->
                                                                        Log.i(TAG,"Hello")
                                                                        if(map.get(key)?.get(HASH_KEY)==currHash ){
                                                                            if(map.get(key)?.get(
                                                                                    IS_GENESIS_KEY)=="TRUE"){
                                                                                reachedGenesis = true
                                                                            }
                                                                            if (map.get(key)?.get(
                                                                                    IS_GENESIS_KEY)=="FALSE" && map.get(key)?.get(HASH_KEY) != map.get(key)?.get(PREV_HASH_KEY)){
                                                                                foundHash = true
                                                                                currHash = map.get(key)?.get(PREV_HASH_KEY)
                                                                                    .toString()
                                                                            }
                                                                        }
                                                                    }
                                                                    if(reachedGenesis){
                                                                        Toast.makeText(
                                                                            applicationContext,
                                                                            "Vote Verified! Thank you.",
                                                                            Toast.LENGTH_LONG
                                                                        ).show()
                                                                        break
                                                                    }
                                                                    if (!foundHash){
                                                                        fraudDetected()
                                                                        break
                                                                    }
                                                                }

                                                            }

                                                            override fun onCancelled(error: DatabaseError) {

                                                            }
                                                        })


                                                    } else {
                                                        Toast.makeText(
                                                            applicationContext,
                                                            "Error 1",
                                                            Toast.LENGTH_LONG
                                                        )
                                                            .show()
                                                    }
                                                }

                                                override fun onCancelled(error: DatabaseError) {
                                                }
                                            })
                                        }
                                    }

                                    override fun onCancelled(error: DatabaseError) {
                                    }
                                })

                        } else {
                            Toast.makeText(applicationContext, "UID is null", Toast.LENGTH_LONG)
                                .show()
                        }
                    }

                    override fun onCancelled(error: DatabaseError) {
                    }
                })

            }

            override fun onCancelled(error: DatabaseError) {
                // Failed to read value
                Log.i(TAG, "Failed to read value.", error.toException())
            }
        })

    }

    private fun destroyVoteChain(){
        val blocksRef = database?.getReference("blocks")
        blocksRef?.removeValue()
        val latestHashRef = database?.getReference("latest_hash")
        latestHashRef?.removeValue()
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        return when (item.itemId) {
            R.id.item1 -> {
                signOut()
                true
            }
            else -> super.onOptionsItemSelected(item)
        }
    }

    private fun fraudDetected(){
        val builder =
            AlertDialog.Builder(this@CategoriesActivity)
        builder.setMessage("VOTING FRAUD DETECTED. \nDESTROYING VOTE CHAIN")
            .setCancelable(false)
            .setPositiveButton("Ok") { dialog, id ->
                destroyVoteChain()
            }
        val alert = builder.create()
        alert.show()
    }

    private fun signOut(){
        resetVotes()
        auth.signOut()
    }

    override fun onBackPressed() {
        //disabling back button
        Toast.makeText(applicationContext, "Click Options Menu to Log Out.", Toast.LENGTH_LONG).show()
    }

    private fun getSHA256Hash(data: String): String {
        val result: String = ""
        try {
            val digest: MessageDigest = MessageDigest.getInstance("SHA-256")
            val hash: ByteArray = digest.digest(data.toByteArray(charset("UTF-8")))
            return hash.toHexString() // make it printable
        } catch (ex: Exception) {
            ex.printStackTrace()
        }
        return result
    }

    fun ByteArray.toHexString() : String {
        return this.joinToString("") {
            java.lang.String.format("%02x", it)
        }
    }


    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        Log.d(TAG, "Entered onActivityResult")

        if (requestCode == REQUEST_CODE) {
            if (resultCode == RESULT_OK) {
                super.onActivityResult(requestCode, resultCode, data)
                val category = data!!.getStringExtra(VOTE_CATEGORY)
                val voteValue = data.getStringExtra(VOTE_VALUE)

                if (voteValue != null && voteValue?.isNotEmpty()) {
                    when (category) {
                        "TV Show" -> tvShowVoteValue = voteValue
                        "Superhero" -> superheroVoteValue = voteValue
                        "Beverage" -> beverageVoteValue = voteValue
                    }
                }
            }
        }
    }

    private fun getCnfVotesString(): String{
        val s = "TV Show: " + tvShowVoteValue + "\n" + "Superhero: " + superheroVoteValue + "\n" +  "Beverage: " + beverageVoteValue
        return s
    }

    private fun resetVotes(){
        tvShowVoteValue = null
        superheroVoteValue = null
        beverageVoteValue = null
    }

}