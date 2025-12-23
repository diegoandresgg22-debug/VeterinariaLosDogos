package com.example.myapplication

import android.app.DatePickerDialog
import android.app.ProgressDialog
import android.os.Bundle
import android.widget.*
import androidx.appcompat.app.AppCompatActivity
import com.example.myapplication.models.Pet
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.FirebaseDatabase
import java.text.SimpleDateFormat
import java.util.*

class RegisterPetActivity : AppCompatActivity() {
    private lateinit var etPetName: EditText
    private lateinit var etBreed: EditText
    private lateinit var etBirthdate: EditText
    private lateinit var spSpecies: Spinner
    private lateinit var btnSavePet: Button

    private val calendar = Calendar.getInstance()
    private val isoFormat = SimpleDateFormat("yyyy-MM-dd", Locale.getDefault())
    private val auth = FirebaseAuth.getInstance()
    private val db = FirebaseDatabase.getInstance().reference

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_register_pet) [cite: 171]

        etPetName = findViewById(R.id.etPetName)
        etBreed = findViewById(R.id.etBreed)
        etBirthdate = findViewById(R.id.etBirthdate)
        spSpecies = findViewById(R.id.spSpecies)
        btnSavePet = findViewById(R.id.btnSavePet)

        val adapter = ArrayAdapter.createFromResource(this, R.array.species_array, android.R.layout.simple_spinner_item)
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
        spSpecies.adapter = adapter [cite: 173]

        etBirthdate.setOnClickListener { showDatePicker() }
        btnSavePet.setOnClickListener { savePet() }
    }

    private fun showDatePicker() {
        DatePickerDialog(this, { _, year, month, day ->
            calendar.set(year, month, day)
            etBirthdate.setText(isoFormat.format(calendar.time))
        }, calendar.get(Calendar.YEAR), calendar.get(Calendar.MONTH), calendar.get(Calendar.DAY_OF_MONTH)).show()
    }

    private fun savePet() {
        val name = etPetName.text.toString().trim()
        val breed = etBreed.text.toString().trim()
        val species = spSpecies.selectedItem.toString()
        val date = etBirthdate.text.toString().trim()
        val uid = auth.currentUser?.uid

        if (name.isEmpty() || date.isEmpty() || uid == null) {
            Toast.makeText(this, "Datos incompletos", Toast.LENGTH_SHORT).show()
            return
        }

        val petId = db.child("pets").push().key ?: return
        val pet = Pet(petId, uid, name, species, breed, date) [cite: 184]

        db.child("pets").child(petId).setValue(pet).addOnCompleteListener {
            if (it.isSuccessful) {
                Toast.makeText(this, "Mascota guardada", Toast.LENGTH_SHORT).show()
                finish()
            }
        }
    }
}