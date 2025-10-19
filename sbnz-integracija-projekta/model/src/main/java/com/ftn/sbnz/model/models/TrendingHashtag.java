package com.ftn.sbnz.model.models;

import lombok.Data;

import java.io.Serializable;

@Data
public class TrendingHashtag implements Serializable {

    private static final long serialVersionUID = 1L;
    private String tag;

    private Integer popularity;

    public TrendingHashtag(String tag, Integer popularity) {
        this.tag = tag;
        this.popularity = popularity;
    }

}