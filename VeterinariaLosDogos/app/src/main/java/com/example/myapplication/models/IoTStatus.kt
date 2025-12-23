package com.example.myapplication.models

data class IoTStatus(
    var temperature: Double = 0.0,
    var heartRate: Int = 0,
    var activityLevel: String? = null,
    var isDoorOpen: Boolean = false,
    var isBuzzerActive: Boolean = false,
    var state: String? = null
)

