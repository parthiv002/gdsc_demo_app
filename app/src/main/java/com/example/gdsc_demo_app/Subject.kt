package com.example.gdsc_demo_app

data class Subject(
    var name: String,
    var totalClasses: Int = 0,
    var attendedClasses: Int = 0,
    var attendancePercentage: Float = 0f
)

