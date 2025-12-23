package com.example.myapplication.repository

import com.example.myapplication.models.IoTCommand
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener

class IoTRepository {
    private val db: DatabaseReference = FirebaseDatabase.getInstance().reference

    fun listenToStatus(petId: String, listener: ValueEventListener) {
        db.child("iotData").child(petId).addValueEventListener(listener) [cite: 313]
    }

    fun sendCommand(petId: String, command: String) {
        val cmdId = db.child("iotCommands").child(petId).push().key ?: return
        val cmd = IoTCommand(command, System.currentTimeMillis())
        db.child("iotCommands").child(petId).child(cmdId).setValue(cmd) [cite: 314, 315]
    }
}