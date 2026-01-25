package com.matchpointplus.models;

import com.google.gson.annotations.SerializedName;
import java.util.List;

/**
 * Modern User model with safety annotations
 */
public class User {
    @SerializedName("id")
    private String id;

    @SerializedName("email")
    private String email;

    @SerializedName("password")
    private String password;

    @SerializedName("name")
    private String name;

    @SerializedName("age")
    private Integer age;

    @SerializedName("location")
    private String location;

    @SerializedName("bio")
    private String bio;

    @SerializedName("profile_picture")
    private String profilePicture;

    public User(String email, String password) {
        this.email = email;
        this.password = password;
    }

    // Standard Getters & Setters
    public String getId() { return id; }
    public void setId(String id) { this.id = id; }
    
    public String getEmail() { return email; }
    public void setEmail(String email) { this.email = email; }

    public String getPassword() { return password; }
    public void setPassword(String password) { this.password = password; }

    public String getName() { return name; }
    public void setName(String name) { this.name = name; }

    public Integer getAge() { return age; }
    public void setAge(Integer age) { this.age = age; }

    public String getProfilePicture() { return profilePicture; }
    public void setProfilePicture(String profilePicture) { this.profilePicture = profilePicture; }
}
