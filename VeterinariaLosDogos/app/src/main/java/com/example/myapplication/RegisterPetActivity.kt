package com.example.myapplication.fragments

import android.app.Activity
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
import com.google.firebase.storage.FirebaseDatabase
import com.google.firebase.storage.FirebaseStorage
import java.io.ByteArrayOutputStream
import java.util.*

class RegisterPetFragment : Fragment() {
    private lateinit var imgPet: ImageView
    private var imageBitmap: Bitmap? = null
    private val storage = FirebaseStorage.getInstance().reference
    private val db = FirebaseDatabase.getInstance().reference
    private val auth = FirebaseAuth.getInstance()

    // Lanzador para la cámara
    private val cameraLauncher = registerForActivityResult(ActivityResultContracts.StartActivityForResult()) { result ->
        if (result.resultCode == Activity.RESULT_OK) {
            val bitmap = result.data?.extras?.get("data") as Bitmap
            imgPet.setImageBitmap(bitmap)
            imageBitmap = bitmap
        }
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        val v = inflater.inflate(R.layout.fragment_register_pet, container, false)

        imgPet = v.findViewById(R.id.imgPetPreview) // Debes agregar este ImageView a tu XML
        val btnCamera = v.findViewById<Button>(R.id.btnCamera)
        val btnSave = v.findViewById<Button>(R.id.btnSavePet)

        btnCamera.setOnClickListener {
            val takePictureIntent = Intent(MediaStore.ACTION_IMAGE_CAPTURE)
            cameraLauncher.launch(takePictureIntent)
        }

        btnSave.setOnClickListener { uploadImageAndSavePet(v) }

        return v
    }

    private fun uploadImageAndSavePet(v: View) {
        val name = v.findViewById<EditText>(R.id.etPetName).text.toString()
        val uid = auth.currentUser?.uid ?: return
        val petId = db.child("pets").push().key ?: return

        if (imageBitmap != null) {
            val baos = ByteArrayOutputStream()
            imageBitmap!!.compress(Bitmap.CompressFormat.JPEG, 100, baos)
            val data = baos.toByteArray()

            // Subir a Firebase Storage
            val fileRef = storage.child("pets/$petId.jpg")
            fileRef.putBytes(data).addOnSuccessListener {
                fileRef.downloadUrl.addOnSuccessListener { uri ->
                    saveToDatabase(petId, uid, name, uri.toString())
                }
            }
        } else {
            saveToDatabase(petId, uid, name, "")
        }
    }

    private fun saveToDatabase(id: String, owner: String, name: String, url: String) {
        val pet = Pet(id, owner, name, imageUrl = url)
        db.child("pets").child(id).setValue(pet).addOnSuccessListener {
            Toast.makeText(context, "Mascota guardada con foto", Toast.LENGTH_SHORT).show()
        }
    }
}