package com.example.gdsc_demo_app

import android.annotation.SuppressLint
import android.content.Context
import android.os.Bundle
import android.view.View
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.ItemTouchHelper
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.google.android.material.floatingactionbutton.FloatingActionButton
import com.google.android.material.snackbar.Snackbar
import com.google.firebase.FirebaseApp
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken

class MainActivity : AppCompatActivity() {

    private lateinit var adapter: SubjectAdapter
    private lateinit var dbReference: DatabaseReference
    private val subjects = mutableListOf<Subject>()

    @SuppressLint("NotifyDataSetChanged")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        FirebaseApp.initializeApp(this)
        setContentView(R.layout.activity_main)

        dbReference = FirebaseDatabase.getInstance().getReference("Subjects")

        // Initialize the adapter with the callback
        adapter = SubjectAdapter(subjects) { updatedSubjects ->
            saveSubjectsToFirebase(updatedSubjects)
        }

        // Set up RecyclerView
        findViewById<RecyclerView>(R.id.recyclerView).apply {
            layoutManager = LinearLayoutManager(this@MainActivity)
            adapter = this@MainActivity.adapter

            val itemTouchHelper = ItemTouchHelper(object : ItemTouchHelper.SimpleCallback(0, ItemTouchHelper.LEFT or ItemTouchHelper.RIGHT) {
                override fun onMove(recyclerView: RecyclerView, viewHolder: RecyclerView.ViewHolder, target: RecyclerView.ViewHolder): Boolean {
                    return false
                }

                override fun onSwiped(viewHolder: RecyclerView.ViewHolder, direction: Int) {
                    val position = viewHolder.adapterPosition
                    val removedSubject = subjects[position]

                    // Remove the item from the list
                    subjects.removeAt(position)
                    this@apply.adapter?.notifyItemRemoved(position)

                    // Persist the updated list
                    saveSubjectsToFirebase(subjects)

                    // Show Undo Snackbar
                    Snackbar.make(findViewById(R.id.recyclerView), "Subject removed", Snackbar.LENGTH_LONG)
                        .setAction("Undo") {
                            // Restore the removed item
                            subjects.add(position, removedSubject)
                            this@apply.adapter?.notifyItemInserted(position)
                            saveSubjectsToFirebase(subjects)
                        }.show()
                }
            })
            itemTouchHelper.attachToRecyclerView(this)
        }

        // Handle floating action button click for adding subjects
        findViewById<FloatingActionButton>(R.id.fab).setOnClickListener {
            NewSubject().show(supportFragmentManager, "NewSubjectFragment")
        }

        // Listen for the result from NewSubject fragment
        supportFragmentManager.setFragmentResultListener("requestKey", this) { _, bundle ->
            val subjectName = bundle.getString("subjectName")
            if (!subjectName.isNullOrEmpty()) {
                // Add the new subject to the list
                val newSubject = Subject(name = subjectName)
                subjects.add(newSubject)
                adapter.notifyDataSetChanged()

                // Save updated subjects to Firebase
                saveSubjectsToFirebase(subjects)
            }
        }

        supportFragmentManager.setFragmentResultListener("rKey", this) { _, bundle ->
            val newSubjectName = bundle.getString("newSubjectName")
            val newAttendClass = bundle.getInt("newAttendClass")
            val newTotalClass = bundle.getInt("newTotalClass")
            val position = bundle.getInt("position")

            if (!newSubjectName.isNullOrEmpty() && position >= 0) {
                // Update the subject data
                subjects[position].name = newSubjectName
                subjects[position].attendedClasses = newAttendClass
                subjects[position].totalClasses = newTotalClass

                // Notify adapter and persist changes
                adapter.notifyItemChanged(position)
                saveSubjectsToFirebase(subjects)
            }
        }

        // Fetch subjects from Firebase when the app starts
        fetchSubjectsFromFirebase()
    }

    private fun fetchSubjectsFromFirebase() {
        dbReference.addValueEventListener(object : ValueEventListener {
            @SuppressLint("NotifyDataSetChanged")
            override fun onDataChange(snapshot: DataSnapshot) {
                subjects.clear()
                for (subjectSnapshot in snapshot.children) {
                    val subject = subjectSnapshot.getValue(Subject::class.java)
                    if (subject != null) {
                        subjects.add(subject)
                    }
                }
                adapter.notifyDataSetChanged()
            }

            override fun onCancelled(error: DatabaseError) {
                // Handle error
            }
        })
    }

    private fun saveSubjectsToFirebase(updatedSubjects: List<Subject>) {
        dbReference.setValue(updatedSubjects).addOnCompleteListener { task ->
            if (!task.isSuccessful) {
                Toast.makeText(this,task.exception.toString(),Toast.LENGTH_LONG).show()
            }
        }
    }
}
