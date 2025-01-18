package com.example.gdsc_demo_app

import android.content.Intent
import android.os.Bundle
import android.widget.Button
import android.widget.EditText
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity

class AddSubjectActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_add_subject)

        findViewById<Button>(R.id.btnAddSubject).setOnClickListener {
            val subjectName = findViewById<EditText>(R.id.etSubjectName).text.toString().trim()
            if (subjectName.isNotEmpty()) {
                val intent = Intent().apply { putExtra("subjectName", subjectName) }
                setResult(RESULT_OK, intent)
                finish()
            } else {
                Toast.makeText(this, "Enter a valid subject name", Toast.LENGTH_SHORT).show()
            }
        }
    }
}

