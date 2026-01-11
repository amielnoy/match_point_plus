package com.matchpointplus.models;

import com.google.gson.annotations.SerializedName;
import java.util.List;

public class Match {
    @SerializedName("id")
    private String id;

    @SerializedName("name")
    private String name;

    @SerializedName("age")
    private int age;

    @SerializedName("location")
    private String location;

    @SerializedName("bio")
    private String bio;

    @SerializedName("interests")
    private List<String> interests;

    // Adjusted to match your specific DB column name
    @SerializedName("profilePicture") 
    private String profilePicture;
    
    @SerializedName("pictures")
    private List<String> pictures;

    @SerializedName("is_selected")
    private boolean isSelected;

    public Match(String id, String name, int age, String location, String bio, List<String> interests, String profilePicture, List<String> pictures) {
        this.id = id;
        this.name = name;
        this.age = age;
        this.location = location;
        this.bio = bio;
        this.interests = interests;
        this.profilePicture = profilePicture;
        this.pictures = pictures;
        this.isSelected = false;
    }

    // Getters
    public String getId() { return id; }
    public String getName() { return name; }
    public int getAge() { return age; }
    public String getLocation() { return location; }
    public String getBio() { return bio; }
    public List<String> getInterests() { return interests; }
    public String getProfilePicture() { return profilePicture; }
    public List<String> getPictures() { return pictures; }
    public boolean isSelected() { return isSelected; }

    public void setSelected(boolean selected) { isSelected = selected; }
}
