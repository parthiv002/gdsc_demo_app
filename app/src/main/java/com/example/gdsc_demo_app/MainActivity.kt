package com.example.gdsc_demo_app

import android.annotation.SuppressLint
import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.gdsc_demo_app.databinding.ActivityMainBinding
import com.google.android.material.floatingactionbutton.FloatingActionButton
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken

class MainActivity : AppCompatActivity() {

    private lateinit var adapter: SubjectAdapter
    private val subjects = mutableListOf<Subject>()
    private val sharedPreferences by lazy { getSharedPreferences("SubjectPrefs", Context.MODE_PRIVATE) }
    private lateinit var binding: ActivityMainBinding
    private lateinit var subjectViewModel: SubjectViewModel

    @SuppressLint("NotifyDataSetChanged")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        binding=ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)
        subjectViewModel=ViewModelProvider(this).get(SubjectViewModel::class.java)

        // Load saved subjects from SharedPreferences
        loadSubjects()

        // Initialize the adapter with the onSubjectsUpdated callback
        adapter = SubjectAdapter(subjects,subjectViewModel) { updatedSubjects ->
            saveSubjects(updatedSubjects)
        }

        // Set up RecyclerView
        findViewById<RecyclerView>(R.id.recyclerView).apply {
            layoutManager = LinearLayoutManager(this@MainActivity)
            adapter = this@MainActivity.adapter
        }

        // Handle floating action button click for adding subjects
        findViewById<FloatingActionButton>(R.id.fab).setOnClickListener {
            NewSubject().show(supportFragmentManager,"NewSubjectTag")
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
