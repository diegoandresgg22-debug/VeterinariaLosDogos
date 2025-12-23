package com.example.myapplication.fragments

import android.app.Activity
import android.app.DatePickerDialog
import android.content.Intent
import android.graphics.Bitmap
import android.os.Bundle
import android.provider.MediaStore
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.*
import androidx.activity.result.contract.ActivityResultContracts
import androidx.fragment.app.Fragment
import com.example.myapplication.R
import com.example.myapplication.models.Pet
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.storage.FirebaseStorage
import java.io.ByteArrayOutputStream
import java.text.SimpleDateFormat
import java.util.*

class RegisterPetFragment : Fragment() {

    private lateinit var imgPet: ImageView
    private lateinit var etPetName: EditText
    private lateinit var etBreed: EditText
    private lateinit var etBirthdate: EditText
    private lateinit var spSpecies: Spinner
    private var imageBitmap: Bitmap? = null

    private val calendar = Calendar.getInstance()
    private val isoFormat = SimpleDateFormat("yyyy-MM-dd", Locale.getDefault())
    private val auth = FirebaseAuth.getInstance()
    private val db = FirebaseDatabase.getInstance().reference
    private val storage = FirebaseStorage.getInstance().reference

    // Lanzador para la cámara (Requisito Hardware 2.4.1)
    private val cameraLauncher = registerForActivityResult(ActivityResultContracts.StartActivityForResult()) { result ->
        if (result.resultCode == Activity.RESULT_OK) {
            val bitmap = result.data?.extras?.get("data") as Bitmap [cite: 549]
            imgPet.setImageBitmap(bitmap) [cite: 550]
            imageBitmap = bitmap [cite: 550]
        }
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        val v = inflater.inflate(R.layout.fragment_register_pet, container, false) [cite: 551]

        // Inicializar vistas
        imgPet = v.findViewById(R.id.imgPetPreview) [cite: 551, 638]
        etPetName = v.findViewById(R.id.etPetName) [cite: 571, 639]
        etBreed = v.findViewById(R.id.etBreed) [cite: 571]
        etBirthdate = v.findViewById(R.id.etBirthdate) [cite: 571]
        spSpecies = v.findViewById(R.id.spSpecies) [cite: 571]
        val btnCamera = v.findViewById<Button>(R.id.btnCamera) [cite: 551, 639]
        val btnSave = v.findViewById<Button>(R.id.btnSavePet) [cite: 551, 639]

        // Spinner de especies
        val adapter = ArrayAdapter.createFromResource(requireContext(), R.array.species_array, android.R.layout.simple_spinner_item) [cite: 571]
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item) [cite: 571]
        spSpecies.adapter = adapter [cite: 571]

        // Listeners
        etBirthdate.setOnClickListener { showDatePicker() } [cite: 571]
        btnCamera.setOnClickListener { openCamera() } [cite: 551]
        btnSave.setOnClickListener { validateAndUpload() } [cite: 552]

        return v
    }

    private fun showDatePicker() {
        DatePickerDialog(requireContext(), { _, year, month, day ->
            calendar.set(year, month, day)
            etBirthdate.setText(isoFormat.format(calendar.time))
        }, calendar.get(Calendar.YEAR), calendar.get(Calendar.MONTH), calendar.get(Calendar.DAY_OF_MONTH)).show() [cite: 572]
    }

    private fun openCamera() {
        val takePictureIntent = Intent(MediaStore.ACTION_IMAGE_CAPTURE) [cite: 551]
        cameraLauncher.launch(takePictureIntent) [cite: 551]
    }

    private fun validateAndUpload() {
        val name = etPetName.text.toString().trim() [cite: 572]
        val breed = etBreed.text.toString().trim() [cite: 573]
        val species = spSpecies.selectedItem.toString() [cite: 573]
        val date = etBirthdate.text.toString().trim() [cite: 573]
        val uid = auth.currentUser?.uid [cite: 573]

        if (name.isEmpty() || date.isEmpty() || uid == null) {
            Toast.makeText(context, "Por favor completa los campos obligatorios", Toast.LENGTH_SHORT).show() [cite: 574]
            return
        }

        val petId = db.child("pets").push().key ?: return [cite: 574]

        if (imageBitmap != null) {
            uploadImage(petId, uid, name, species, breed, date)
        } else {
            savePetToDatabase(petId, uid, name, species, breed, date, "")
        }
    }

    private fun uploadImage(petId: String, uid: String, name: String, species: String, breed: String, date: String) {
        val baos = ByteArrayOutputStream() [cite: 553]
        imageBitmap!!.compress(Bitmap.CompressFormat.JPEG, 100, baos) [cite: 553]
        val data = baos.toByteArray() [cite: 553]

        val fileRef = storage.child("pets/$petId.jpg") [cite: 553]
        fileRef.putBytes(data).addOnSuccessListener {
            fileRef.downloadUrl.addOnSuccessListener { uri ->
                savePetToDatabase(petId, uid, name, species, breed, date, uri.toString()) [cite: 554]
            }
        }.addOnFailureListener {
            Toast.makeText(context, "Error al subir imagen: ${it.message}", Toast.LENGTH_SHORT).show()
        }
    }

    private fun savePetToDatabase(id: String, ownerId: String, name: String, species: String, breed: String, date: String, url: String) {
        val pet = Pet(id, ownerId, name, species, breed, date, url) [cite: 574, 592, 593]
        db.child("pets").child(id).setValue(pet).addOnSuccessListener { [cite: 574]
            Toast.makeText(context, "Mascota registrada exitosamente", Toast.LENGTH_SHORT).show() [cite: 555, 575]
            // Limpiar campos o navegar
        }
    }
}