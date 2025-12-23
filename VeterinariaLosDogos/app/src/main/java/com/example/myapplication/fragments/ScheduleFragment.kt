package com.example.myapplication.fragments

import android.Manifest
import android.content.pm.PackageManager
import android.location.Location
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.*
import androidx.core.app.ActivityCompat
import androidx.fragment.app.Fragment
import com.example.myapplication.R
import com.google.android.gms.location.FusedLocationProviderClient
import com.google.android.gms.location.LocationServices
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.FirebaseDatabase

class ScheduleFragment : Fragment() {
    private lateinit var fusedLocationClient: FusedLocationProviderClient
    private var lastLocation: Location? = null
    private val auth = FirebaseAuth.getInstance()
    private val db = FirebaseDatabase.getInstance().reference

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        val v = inflater.inflate(R.layout.fragment_schedule, container, false)

        fusedLocationClient = LocationServices.getFusedLocationProviderClient(requireActivity())

        val btnSave = v.findViewById<Button>(R.id.btnSave)
        btnSave.setOnClickListener {
            checkLocationPermissionAndSave()
        }

        return v
    }

    private fun checkLocationPermissionAndSave() {
        if (ActivityCompat.checkSelfPermission(requireContext(), Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            // Solicitar permiso si no se tiene
            requestPermissions(arrayOf(Manifest.permission.ACCESS_FINE_LOCATION), 1000)
            return
        }

        // Obtener ubicación actual
        fusedLocationClient.lastLocation.addOnSuccessListener { location ->
            lastLocation = location
            saveAppointmentWithLocation()
        }
    }

    private fun saveAppointmentWithLocation() {
        val uid = auth.currentUser?.uid ?: return
        val appointmentId = db.child("appointments").push().key ?: return

        val appointmentData = mutableMapOf<String, Any>(
            "userId" to uid,
            "date" to view?.findViewById<EditText>(R.id.etDate)?.text.toString(),
            "reason" to view?.findViewById<EditText>(R.id.etReason)?.text.toString(),
            "timestamp" to System.currentTimeMillis()
        )

        // Agregar coordenadas si el GPS las obtuvo
        lastLocation?.let {
            appointmentData["latitude"] = it.latitude
            appointmentData["longitude"] = it.longitude
        }

        db.child("appointments").child(appointmentId).setValue(appointmentData)
            .addOnSuccessListener {
                Toast.makeText(context, "Cita agendada con ubicación GPS", Toast.LENGTH_SHORT).show()
            }
    }
}