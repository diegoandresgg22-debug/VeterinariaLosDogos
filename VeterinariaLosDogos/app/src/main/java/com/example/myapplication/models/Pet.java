package com.example.myapplication.models;

public class Pet {

    private String id;
    private String ownerId;
    private String name;
    private String species;
    private String breed;
    private String birthdate;
    private String imageUrl; // opcional si no lo usas

    public Pet() {
        // Constructor vacío requerido por Firebase
    }

    // Constructor que tu fragmento necesita (6 parámetros)
    public Pet(String id, String ownerId, String name, String species, String breed, String birthdate) {
        this.id = id;
        this.ownerId = ownerId;
        this.name = name;
        this.species = species;
        this.breed = breed;
        this.birthdate = birthdate;
    }

    // Constructor opcional si alguna parte lo usa
    public Pet(String id, String ownerId, String name, String species, String breed, String birthdate, String imageUrl) {
        this.id = id;
        this.ownerId = ownerId;
        this.name = name;
        this.species = species;
        this.breed = breed;
        this.birthdate = birthdate;
        this.imageUrl = imageUrl;
    }

    // GETTERS Y SETTERS

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getOwnerId() {
        return ownerId;
    }

    public void setOwnerId(String ownerId) {
        this.ownerId = ownerId;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getSpecies() {
        return species;
    }

    public void setSpecies(String species) {
        this.species = species;
    }

    public String getBreed() {
        return breed;
    }

    public void setBreed(String breed) {
        this.breed = breed;
    }

    public String getBirthdate() {
        return birthdate;
    }

    public void setBirthdate(String birthdate) {
        this.birthdate = birthdate;
    }

    public String getImageUrl() {
        return imageUrl;
    }

    public void setImageUrl(String imageUrl) {
        this.imageUrl = imageUrl;
    }
}
