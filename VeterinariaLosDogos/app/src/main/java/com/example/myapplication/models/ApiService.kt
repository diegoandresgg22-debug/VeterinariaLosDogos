package com.example.myapplication.network

import com.example.myapplication.models.DogBreed
import retrofit2.Call
import retrofit2.http.GET

interface ApiService {
    @GET("v1/breeds")
    fun getBreeds(): Call<List<DogBreed>>
}