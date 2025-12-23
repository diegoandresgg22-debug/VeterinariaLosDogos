package com.example.myapplication.models

// Representa el objeto que devuelve la API de razas
data class DogBreed(
    val id: Int,
    val name: String,
    val temperament: String?,
    val life_span: String?
)