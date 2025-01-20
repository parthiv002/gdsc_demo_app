package com.example.gdsc_demo_app

import android.annotation.SuppressLint
import android.content.Context
import android.os.Bundle
import android.view.View
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.ItemTouchHelper
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.google.android.material.floatingactionbutton.FloatingActionButton
import com.google.android.material.snackbar.Snackbar
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken

class MainActivity : AppCompatActivity() {

    private lateinit var adapter: SubjectAdapter
    private val subjects = mutableListOf<Subject>()
    private val sharedPreferences by lazy { getSharedPreferences("SubjectPrefs", Context.MODE_PRIVATE) }

    @SuppressLint("NotifyDataSetChanged")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        // Load saved subjects from SharedPreferences
        loadSubjects()

        // Initialize the adapter with the onSubjectsUpdated callback
        adapter = SubjectAdapter(subjects) { updatedSubjects ->
            saveSubjects(updatedSubjects)
        }

        // Set up RecyclerView
        findViewById<RecyclerView>(R.id.recyclerView).apply {
            layoutManager = LinearLayoutManager(this@MainActivity)
            adapter = this@MainActivity.adapter

            val itemTouchHelper=ItemTouchHelper(object:ItemTouchHelper.SimpleCallback(0,ItemTouchHelper.LEFT or ItemTouchHelper.RIGHT){
                override fun onMove(
                    recyclerView: RecyclerView,
                    viewHolder: RecyclerView.ViewHolder,
                    target: RecyclerView.ViewHolder
                ): Boolean {
                    return false
                }

                override fun onSwiped(viewHolder: RecyclerView.ViewHolder, direction: Int) {
                    val position = viewHolder.adapterPosition
                    val removedSubject = subjects[position]

                    // Remove the item from the list
                    subjects.removeAt(position)
                    this@apply.adapter?.notifyItemRemoved(position)

                    // Persist the updated list
                    saveSubjects(subjects)

                    // Show Undo Snackbar
                    Snackbar.make(findViewById(R.id.recyclerView), "Subject removed", Snackbar.LENGTH_LONG)
                        .setAction("Undo") {
                            // Restore the removed item
                            subjects.add(position, removedSubject)
                            this@apply.adapter?.notifyItemInserted(position)
                            saveSubjects(subjects)
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
                subjects.add(Subject(name = subjectName))
                adapter.notifyDataSetChanged()

                // Save updated subjects to SharedPreferences
                saveSubjects(subjects)
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
                saveSubjects(subjects)
            }
        }

    }

    private fun loadSubjects() {
        val json = sharedPreferences.getString("subjects", null)
        if (!json.isNullOrEmpty()) {
            val type = object : TypeToken<List<Subject>>() {}.type
            val savedSubjects: List<Subject> = Gson().fromJson(json, type)
            subjects.addAll(savedSubjects)
        }
    }

    private fun saveSubjects(updatedSubjects: List<Subject>) {
        val json = Gson().toJson(updatedSubjects)
        sharedPreferences.edit().putString("subjects", json).apply()
    }
}
