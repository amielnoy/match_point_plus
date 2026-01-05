package com.matchpointplus.models;

import com.google.gson.annotations.SerializedName;
import java.util.List;

public class User {
    private String id;
    private String name;
    private int age;
    private String location;
    private String bio;
    private List<String> interests;

    @SerializedName("profilePicture")
    private String profilePicture;
    
    private List<String> pictures;

    public User(String id, String name, int age, String location, String bio, List<String> interests, String profilePicture, List<String> pictures) {
        this.id = id;
        this.name = name;
        this.age = age;
        this.location = location;
        this.bio = bio;
        this.interests = interests;
        this.profilePicture = profilePicture;
        this.pictures = pictures;
    }

    // Getters and Setters
    public String getId() { return id; }
    public String getName() { return name; }
    public int getAge() { return age; }
    public String getLocation() { return location; }
    public String getBio() { return bio; }
    public List<String> getInterests() { return interests; }
    public String getProfilePicture() { return profilePicture; }
    public List<String> getPictures() { return pictures; }
}
