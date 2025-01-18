package com.example.gdsc_demo_app

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.lifecycle.ViewModelProvider
import com.example.gdsc_demo_app.databinding.FragmentNewSubjectBinding
import com.google.android.material.bottomsheet.BottomSheetDialogFragment


class NewSubject : BottomSheetDialogFragment() {

    private lateinit var binding: FragmentNewSubjectBinding
    private lateinit var subjectViewModel:SubjectViewModel
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        binding= FragmentNewSubjectBinding.inflate(inflater,container,false)
        subjectViewModel=ViewModelProvider(this).get(SubjectViewModel::class.java)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        binding.saveSubjectBtn.setOnClickListener {
            if(binding.etSubjectName.text.toString().isNotEmpty()){
                val subjectName=binding.etSubjectName.text.toString().trim()
                subjectViewModel._subjectName.value=subjectName
                dismiss()
            }
            else{
                Toast.makeText(context,"Write the subject name!",Toast.LENGTH_LONG).show()
            }
        }
    }
}