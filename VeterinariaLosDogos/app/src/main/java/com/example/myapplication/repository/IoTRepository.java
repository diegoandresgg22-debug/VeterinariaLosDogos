package com.example.myapplication.repository;

import androidx.annotation.NonNull;

import com.example.myapplication.models.IoTStatus;
import com.example.myapplication.models.IoTCommand;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

public class IoTRepository {

    private final DatabaseReference db;

    public IoTRepository() {
        db = FirebaseDatabase.getInstance().getReference();
    }

    public void listenToStatus(String petId, ValueEventListener listener) {
        db.child("iotData").child(petId).addValueEventListener(listener);
    }

    public void sendCommand(String petId, String command) {
        String cmdId = db.child("iotCommands").child(petId).push().getKey();
        if (cmdId == null) return;

        IoTCommand cmd = new IoTCommand(command, System.currentTimeMillis());
        db.child("iotCommands").child(petId).child(cmdId).setValue(cmd);
    }
}
