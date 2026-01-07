package com.matchpointplus.models;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;
import java.util.List;

public class User {
    @SerializedName("id")
    private String id;

    @SerializedName("email")
    private String email;

    @SerializedName("password")
    private String password;

    @SerializedName("profile_picture")
    private String profilePicture;

    @SerializedName("pictures")
    private List<String> pictures;

    public User(String email, String password) {
        this.email = email;
        this.password = password;
    }

    public String getId() { return id; }
    public String getEmail() { return email; }
    public String getPassword() { return password; }
    public String getProfilePicture() { return profilePicture; }
    public List<String> getPictures() { return pictures; }

    public void setProfilePicture(String profilePicture) {
        this.profilePicture = profilePicture;
    }
}
