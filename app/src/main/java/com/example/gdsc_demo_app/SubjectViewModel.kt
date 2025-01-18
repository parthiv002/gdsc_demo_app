package com.example.gdsc_demo_app

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel

class SubjectViewModel:ViewModel() {

    var _subjectName=MutableLiveData<String>()

}