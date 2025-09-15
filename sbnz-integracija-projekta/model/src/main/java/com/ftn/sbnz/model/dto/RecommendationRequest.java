package com.ftn.sbnz.model.dto;

import java.util.List;

import com.ftn.sbnz.model.models.Post;
import com.ftn.sbnz.model.models.User;

public class RecommendationRequest {
    
    private List<User> users;
    private List<Post> posts;
    
    public RecommendationRequest() {}
    
    public RecommendationRequest(List<User> users, List<Post> posts) {
        this.users = users;
        this.posts = posts;
    }
    
    // Getters and Setters
    public List<User> getUsers() {
        return users;
    }
    
    public void setUsers(List<User> users) {
        this.users = users;
    }
    
    public List<Post> getPosts() {
        return posts;
    }
    
    public void setPosts(List<Post> posts) {
        this.posts = posts;
    }
    
    @Override
    public String toString() {
        return "RecommendationRequest{" +
                "users=" + (users != null ? users.size() : 0) + " users" +
                ", posts=" + (posts != null ? posts.size() : 0) + " posts" +
                '}';
    }
}