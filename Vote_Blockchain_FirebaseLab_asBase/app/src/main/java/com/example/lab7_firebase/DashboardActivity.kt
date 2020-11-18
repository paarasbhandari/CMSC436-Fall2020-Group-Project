package com.example.lab7_firebase

import android.content.Intent
import android.os.Bundle
import android.text.TextUtils
import android.util.Log
import android.view.View
import android.widget.*
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import com.google.firebase.database.*
import org.w3c.dom.Text
import java.lang.Exception
import java.util.*

class DashboardActivity : AppCompatActivity() {

    private lateinit var editTextName: EditText
    private lateinit var spinnerCountry: Spinner
    private lateinit var buttonAddAuthor: Button
    internal lateinit var listViewAuthors: ListView
    internal lateinit var authors: MutableList<Author>
    private lateinit var databaseAuthors: DatabaseReference
    private lateinit var uid: String

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_dashboard)

        //getting the reference of authors node
        databaseAuthors = FirebaseDatabase.getInstance().getReference("authors")

        editTextName = findViewById<View>(R.id.editTextName) as EditText
        spinnerCountry = findViewById<View>(R.id.spinnerCountry) as Spinner
        listViewAuthors = findViewById<View>(R.id.listViewAuthors) as ListView
        buttonAddAuthor = findViewById<View>(R.id.buttonAddAuthor) as Button

        authors = ArrayList()
        uid = intent.getStringExtra(USER_ID)!!

        buttonAddAuthor.setOnClickListener {
            addAuthor()
        }

        //attaching listener to ListView
        listViewAuthors.onItemClickListener = AdapterView.OnItemClickListener { adapterView, view, i, l ->
            //getting the selected artist
            val author = authors[i]

            //creating an intent
            val intent = Intent(applicationContext, AuthorActivity::class.java)

            Log.d(TAG, "Author information : \n" +
                    "\tID= ${author.authorId}" +
                    "\tName= ${author.authorName}" +
                    "\tCountry= ${author.authorCountry}")

            intent.putExtra(AUTHOR_ID, author.authorId)
            intent.putExtra(AUTHOR_NAME, author.authorName)
            intent.putExtra(USER_ID, USER_ID)
            startActivity(intent)
        }

        listViewAuthors.onItemLongClickListener = AdapterView.OnItemLongClickListener { adapterView, view, i, l ->
            val author = authors[i]
            showUpdateDeleteDialog(author.authorId, author.authorName)
            true
        }
    }

    private fun showUpdateDeleteDialog(authorId: String, authorName: String) {

        val dialogBuilder = AlertDialog.Builder(this)
        val inflater = layoutInflater
        val dialogView = inflater.inflate(R.layout.update_dialog, null)
        dialogBuilder.setView(dialogView)

        val editTextName = dialogView.findViewById<View>(R.id.editTextName) as EditText
        val spinnerCountry = dialogView.findViewById<View>(R.id.spinnerCountry) as Spinner
        val buttonUpdate = dialogView.findViewById<View>(R.id.buttonUpdateAuthor) as Button
        val buttonDelete = dialogView.findViewById<View>(R.id.buttonDeleteAuthor) as Button

        dialogBuilder.setTitle(authorName)
        val b = dialogBuilder.create()
        b.show()

        // TODO: Set update listener
        buttonUpdate.setOnClickListener {
            val newAuthorName = editTextName!!.text.toString().trim { it <= ' ' }
            val newAuthorCountry = spinnerCountry.selectedItem.toString()
            if (!TextUtils.isEmpty(newAuthorName)) {
                updateAuthor(authorId, uid, newAuthorName, newAuthorCountry)
                b.dismiss()
            }
        }

        // TODO: Set delete listener
        buttonDelete.setOnClickListener {
            deleteAuthor(authorId)
            b.dismiss()
        }
    }

    // TODO: Add an author
    private fun addAuthor() {

        Log.d(TAG, "Entered addAuthor()")

        //getting the values to save
        val authorName = editTextName.text.toString().trim { it <= ' ' }
        val authorCountry = spinnerCountry.selectedItem.toString()

        // is authorName provided?
        if (!TextUtils.isEmpty(authorName)) {
            // create a unique Primary Key for current Author using push().getKey()
            val id = databaseAuthors.child(uid).push().key.toString()

            // creating an Author Object for generated id
            val author = Author(id!!, authorName, authorCountry)

            // Saving the Author to given UID
            databaseAuthors.child(uid).child(id).setValue(author)  // ... do NOT use push()...

            editTextName.setText("")

            //displaying a success toast
            Toast.makeText(this, "Author Added", Toast.LENGTH_LONG).show()
        } else {
            //if the value is not given, display a toast
            Toast.makeText(this, "Please enter a name", Toast.LENGTH_LONG).show()
        }
    }

    // TODO: Update an author
    private fun updateAuthor(id: String, uid: String, name: String, country: String): Boolean {

        val author = Author(id, name, country)
        databaseAuthors.child(uid).child(id).setValue(author)

        Toast.makeText(applicationContext, "Author Updated", Toast.LENGTH_LONG).show()

        return true
    }

    // TODO: Delete an author - currently deleting all authors and titles
    private fun deleteAuthor(id: String): Boolean {

        val drAuthors = FirebaseDatabase
            .getInstance()
            .getReference("authors")
            .child(uid)
            .child(id)
        drAuthors.removeValue()

        val dataRefTitles = FirebaseDatabase
            .getInstance()
            .getReference("titles")
            .child(id) // just get author
        dataRefTitles.removeValue() // remove all titles for given author

        return true
    }

    override fun onStart() {
        super.onStart()

        databaseAuthors.addValueEventListener(object : ValueEventListener {
            override fun onDataChange(dataSnapshot: DataSnapshot) {

                Log.d(TAG, "Entered onDataChange")

                authors.clear()

                var author: Author? = null
                for (postSnapshot in dataSnapshot.child(uid).children) {
                    try {
                        author = postSnapshot.getValue(Author::class.java)
                        Log.d(TAG, "Try - Author?= $author")
                    } catch (e: Exception) {
                        Log.e(TAG, "Exception Caught - ${e.toString()}")
                    } finally {
                        Log.d(TAG, "Finally - Author?= $author")
                        authors.add(author!!)
                    }
                }

                val authorAdapter = AuthorList(this@DashboardActivity, authors)
                listViewAuthors.adapter = authorAdapter
            }

            override fun onCancelled(databaseError: DatabaseError) {

            }
        })
    }

    companion object {
        const val TAG = "Lab-Firebase"
        const val AUTHOR_NAME = "com.example.tesla.myhomelibrary.authorname"
        const val AUTHOR_ID = "com.example.tesla.myhomelibrary.authorid"
        const val USER_ID = "com.example.tesla.myhomelibrary.userid"
    }
}