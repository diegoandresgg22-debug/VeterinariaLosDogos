package com.example.myapplication.fragments

import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.LinearLayout
import android.widget.TextView
import androidx.fragment.app.Fragment
import coil.load
import coil.transform.CircleCropTransformation
import com.example.myapplication.R
import com.example.myapplication.models.DogBreed
import com.example.myapplication.models.Pet
import com.example.myapplication.network.ApiService
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.*
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory

class ProfileFragment : Fragment() {

    private lateinit var auth: FirebaseAuth
    private lateinit var db: DatabaseReference
    private lateinit var petsContainer: LinearLayout
    private lateinit var tvUserName: TextView
    private lateinit var tvUserEmail: TextView
    private lateinit var imgUserProfile: ImageView

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val view = inflater.inflate(R.layout.fragment_profile, container, false)

        // Inicializar Firebase
        auth = FirebaseAuth.getInstance()
        db = FirebaseDatabase.getInstance().reference

        // Referencias a la UI
        tvUserName = view.findViewById(R.id.tv_profile_name)
        tvUserEmail = view.findViewById(R.id.tv_profile_email)
        imgUserProfile = view.findViewById(R.id.img_user_profile)
        petsContainer = view.findViewById(R.id.pets_container)

        loadUserData()
        loadUserPets()
        fetchDogBreedsSuggestions()

        return view
    }

    // 1. Cargar datos del Usuario desde Firebase
    private fun loadUserData() {
        val userId = auth.currentUser?.uid ?: return
        db.child("Users").child(userId).addListenerForSingleValueEvent(object : ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                val name = snapshot.child("name").getValue(String::class.java)
                val email = snapshot.child("email").getValue(String::class.java)

                tvUserName.text = name ?: "Usuario"
                tvUserEmail.text = email ?: "Sin correo"

                // Aquí podrías cargar la foto de perfil del usuario si ya la tienes en Firebase
                // imgUserProfile.load(snapshot.child("imageUrl").value) { transformations(CircleCropTransformation()) }
            }
            override fun onCancelled(error: DatabaseError) {
                Log.e("Firebase", "Error al cargar usuario", error.toException())
            }
        })
    }

    // 2. Cargar lista de Mascotas con sus Fotos
    private fun loadUserPets() {
        val userId = auth.currentUser?.uid ?: return
        db.child("pets").orderByChild("ownerId").equalTo(userId)
            .addValueEventListener(object : ValueEventListener {
                override fun onDataChange(snapshot: DataSnapshot) {
                    petsContainer.removeAllViews() // Limpiar antes de recargar
                    for (postSnapshot in snapshot.children) {
                        val pet = postSnapshot.getValue(Pet::class.java)
                        if (pet != null) {
                            addPetToUI(pet)
                        }
                    }
                }
                override fun onCancelled(error: DatabaseError) {}
            })
    }

    private fun addPetToUI(pet: Pet) {
        val petView = layoutInflater.inflate(R.layout.item_pet_card, null)
        val nameTv = petView.findViewById<TextView>(R.id.tv_item_pet_name)
        val breedTv = petView.findViewById<TextView>(R.id.tv_item_pet_breed)
        val petImg = petView.findViewById<ImageView>(R.id.img_item_pet)

        nameTv.text = pet.name
        breedTv.text = pet.breed

        // Cargar imagen de la mascota usando la librería COIL
        if (!pet.imageUrl.isNullOrEmpty()) {
            petImg.load(pet.imageUrl) {
                crossfade(true)
                placeholder(R.drawable.ic_pet_placeholder) // Imagen mientras carga
                transformations(CircleCropTransformation())
            }
        }

        petsContainer.addView(petView)
    }

    // 3. Consumo de API REST con Retrofit (Requerimiento 2.2)
    private fun fetchDogBreedsSuggestions() {
        val retrofit = Retrofit.Builder()
            .baseUrl("https://api.thedogapi.com/")
            .addConverterFactory(GsonConverterFactory.create())
            .build()

        val service = retrofit.create(ApiService::class.java)
        service.getBreeds().enqueue(object : Callback<List<DogBreed>> {
            override fun onResponse(call: Call<List<DogBreed>>, response: Response<List<DogBreed>>) {
                if (response.isSuccessful) {
                    val breeds = response.body()?.take(3) // Mostrar solo 3 sugerencias
                    breeds?.forEach { breed ->
                        val suggestionTv = TextView(context)
                        suggestionTv.text = "💡 Sugerencia: Conoce al ${breed.name}"
                        suggestionTv.setPadding(10, 10, 10, 10)
                        petsContainer.addView(suggestionTv)
                    }
                }
            }
            override fun onFailure(call: Call<List<DogBreed>>, t: Throwable) {
                Log.e("API", "Error: ${t.message}")
            }
        })
    }
}