package com.example.myapplication.models

data class Appointment(
    var id: String? = null,
    var petId: String? = null,
    var ownerUid: String? = null,
    var date: String? = null,
    var reason: String? = null
)