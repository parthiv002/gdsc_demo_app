package com.example.gdsc_demo_app

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import com.example.gdsc_demo_app.databinding.FragmentEditBinding
import com.google.android.material.bottomsheet.BottomSheetDialogFragment

class EditFragment : BottomSheetDialogFragment() {

    private lateinit var binding:FragmentEditBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        binding=FragmentEditBinding.inflate(inflater,container,false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        // Retrieve the arguments passed from SubjectAdapter
        val subjectName = arguments?.getString("subjectName")
        val attendedClasses = arguments?.getInt("attendedClasses")
        val totalClasses = arguments?.getInt("totalClasses")

        // Set the existing values in the EditTexts
        binding.etEditSubjectName.setText(subjectName)
        binding.etEditAtClass.setText(attendedClasses?.toString())
        binding.etEditTotalClass.setText(totalClasses?.toString())

        binding.updateBtn.setOnClickListener {
            val newSubjectName = binding.etEditSubjectName.text.toString().trim()
            val newAttendClass = binding.etEditAtClass.text.toString().trim()
            val newTotalClass = binding.etEditTotalClass.text.toString().trim()

            if (newSubjectName.isNotEmpty() && newAttendClass.isNotEmpty() && newTotalClass.isNotEmpty()) {
                val result = Bundle().apply {
                    putString("newSubjectName", newSubjectName)
                    putInt("newAttendClass", newAttendClass.toInt())
                    putInt("newTotalClass", newTotalClass.toInt())
                    putInt("position", arguments?.getInt("position") ?: -1)
                }

                parentFragmentManager.setFragmentResult("rKey", result)
                dismiss()
            } else {
                Toast.makeText(context, "Please fill in all the details correctly!", Toast.LENGTH_LONG).show()
            }
        }
    }

}