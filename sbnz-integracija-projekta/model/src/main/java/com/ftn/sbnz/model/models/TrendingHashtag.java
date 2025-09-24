package com.ftn.sbnz.model.models;

import java.io.Serializable;

public class TrendingHashtag implements Serializable {

    private static final long serialVersionUID = 1L;
    private String tag;

    public TrendingHashtag(String tag) {
        this.tag = tag;
    }

    // Getters and Setters
    public String getTag() {
        return tag;
    }

    public void setTag(String tag) {
        this.tag = tag;
    }

    @Override
    public String toString() {
        return "TrendingHashtag{tag='" + tag + "'}";
    }
}