package com.example.gdsc_demo_app

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import com.example.gdsc_demo_app.databinding.FragmentNewSubjectBinding
import com.google.android.material.bottomsheet.BottomSheetDialogFragment

class NewSubject : BottomSheetDialogFragment() {

    private lateinit var binding: FragmentNewSubjectBinding

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        binding = FragmentNewSubjectBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        binding.saveSubjectBtn.setOnClickListener {
            val subjectName = binding.etSubjectName.text.toString().trim()
            if (subjectName.isNotEmpty()) {
                // Pass the data back to MainActivity using FragmentResult
                val result = Bundle().apply {
                    putString("subjectName", subjectName)
                }
                parentFragmentManager.setFragmentResult("requestKey", result)
                dismiss() // Close the bottom sheet
            } else {
                Toast.makeText(context, "Write the subject name!", Toast.LENGTH_LONG).show()
            }
        }
    }
}
