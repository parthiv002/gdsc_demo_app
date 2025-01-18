package com.example.gdsc_demo_app

import android.annotation.SuppressLint
import android.content.Context
import android.content.Intent
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.google.android.material.floatingactionbutton.FloatingActionButton
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
        }

        // Handle floating action button click for adding subjects
        findViewById<FloatingActionButton>(R.id.fab).setOnClickListener {
            val intent = Intent(this, AddSubjectActivity::class.java)
            startActivityForResult(intent, ADD_SUBJECT_REQUEST_CODE)
        }
    }

    @SuppressLint("NotifyDataSetChanged")
    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (requestCode == ADD_SUBJECT_REQUEST_CODE && resultCode == RESULT_OK) {
            val subjectName = data?.getStringExtra("subjectName")
            if (!subjectName.isNullOrEmpty()) {
                // Add the new subject to the list
                subjects.add(Subject(name = subjectName))
                adapter.notifyDataSetChanged()

                // Save updated subjects to SharedPreferences
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

    companion object {
        private const val ADD_SUBJECT_REQUEST_CODE = 1
    }
}
