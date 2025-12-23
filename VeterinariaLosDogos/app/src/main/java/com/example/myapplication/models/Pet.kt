package com.example.myapplication.models

class Pet {
    // GETTERS Y SETTERS
    @JvmField
    var id: String? = null
    var ownerId: String? = null
    @JvmField
    var name: String? = null
    @JvmField
    var species: String? = null
    var breed: String? = null
    var birthdate: String? = null
    var imageUrl: String? = null // opcional si no lo usas

    constructor()

    // Constructor que tu fragmento necesita (6 parámetros)
    constructor(
        id: String?,
        ownerId: String?,
        name: String?,
        species: String?,
        breed: String?,
        birthdate: String?
    ) {
        this.id = id
        this.ownerId = ownerId
        this.name = name
        this.species = species
        this.breed = breed
        this.birthdate = birthdate
    }

    // Constructor opcional si alguna parte lo usa
    constructor(
        id: String?,
        ownerId: String?,
        name: String?,
        species: String?,
        breed: String?,
        birthdate: String?,
        imageUrl: String?
    ) {
        this.id = id
        this.ownerId = ownerId
        this.name = name
        this.species = species
        this.breed = breed
        this.birthdate = birthdate
        this.imageUrl = imageUrl
    }
}
