package com.ftn.sbnz.model.models;

import java.util.ArrayList;
import java.util.List;

public class User {
    private String id;
    private String name;
    private int age;
    private String location;
    private String gender;
    private List<String> interests;
    private String creatorType; 
    private int audienceSize;
    
    public User() {
        this.interests = new ArrayList<>();
    }
    
    public User(String id, String name, int age, String location, String gender, String creatorType, int audienceSize) {
        this.id = id;
        this.name = name;
        this.age = age;
        this.location = location;
        this.gender = gender;
        this.creatorType = creatorType;
        this.audienceSize = audienceSize;
        this.interests = new ArrayList<>();
    }
    
    // Getters and Setters
    public String getId() { return id; }
    public void setId(String id) { this.id = id; }
    
    public String getName() { return name; }
    public void setName(String name) { this.name = name; }
    
    public int getAge() { return age; }
    public void setAge(int age) { this.age = age; }
    
    public String getLocation() { return location; }
    public void setLocation(String location) { this.location = location; }
    
    public String getGender() { return gender; }
    public void setGender(String gender) { this.gender = gender; }
    
    public List<String> getInterests() { return interests; }
    public void setInterests(List<String> interests) { this.interests = interests; }
    
    public String getCreatorType() { return creatorType; }
    public void setCreatorType(String creatorType) { this.creatorType = creatorType; }
    
    public int getAudienceSize() { return audienceSize; }
    public void setAudienceSize(int audienceSize) { this.audienceSize = audienceSize; }
    
    public void addInterest(String interest) {
        this.interests.add(interest);
    }
    
    @Override
    public String toString() {
        return "User{" +
                "id='" + id + '\'' +
                ", name='" + name + '\'' +
                ", age=" + age +
                ", location='" + location + '\'' +
                ", interests=" + interests +
                ", creatorType='" + creatorType + '\'' +
                ", audienceSize=" + audienceSize +
                '}';
    }
}