package com.petadoption.model;

import java.sql.Timestamp;

public class Pet {
    private int id;
    private String name;
    private String species;
    private String breed;
    private int age;
    private String description;
    private String ownerContact;
    private String imageUrl;
    private Integer userId;
    private int likesCount;
    private boolean likedByUser;
    private Timestamp createdAt;

    public Pet() {}

    public Pet(String name, String species, String breed, int age, String description, String ownerContact, String imageUrl, Integer userId) {
        this.name = name;
        this.species = species;
        this.breed = breed;
        this.age = age;
        this.description = description;
        this.ownerContact = ownerContact;
        this.imageUrl = imageUrl;
        this.userId = userId;
    }

    // Getters and Setters
    public int getId() { return id; }
    public void setId(int id) { this.id = id; }

    public String getName() { return name; }
    public void setName(String name) { this.name = name; }

    public String getSpecies() { return species; }
    public void setSpecies(String species) { this.species = species; }

    public String getBreed() { return breed; }
    public void setBreed(String breed) { this.breed = breed; }

    public int getAge() { return age; }
    public void setAge(int age) { this.age = age; }

    public String getDescription() { return description; }
    public void setDescription(String description) { this.description = description; }

    public String getOwnerContact() { return ownerContact; }
    public void setOwnerContact(String ownerContact) { this.ownerContact = ownerContact; }

    public String getImageUrl() { return imageUrl; }
    public void setImageUrl(String imageUrl) { this.imageUrl = imageUrl; }

    public Integer getUserId() { return userId; }
    public void setUserId(Integer userId) { this.userId = userId; }

    public int getLikesCount() { return likesCount; }
    public void setLikesCount(int likesCount) { this.likesCount = likesCount; }

    public boolean isLikedByUser() { return likedByUser; }
    public void setLikedByUser(boolean likedByUser) { this.likedByUser = likedByUser; }

    public Timestamp getCreatedAt() { return createdAt; }
    public void setCreatedAt(Timestamp createdAt) { this.createdAt = createdAt; }
}
