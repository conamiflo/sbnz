package com.ftn.sbnz.model.models;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.ArrayList;
import java.util.List;

@Getter
@Setter
@AllArgsConstructor
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