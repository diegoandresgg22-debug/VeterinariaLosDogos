package com.example.myapplication.models

class User {
    // Getters & Setters (Firebase necesita ambos)
    var id: String? = null,
    var name: String? = null,
    var email: String? = null,
    var phone: String? = null

    constructor()

    constructor(name: String?, email: String?) {
        this.name = name
        this.email = email
    }
}
