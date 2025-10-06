package com.ftn.sbnz.model.dto.request;

import lombok.Data;
import java.util.List;

@Data
public class PostCreateDTO {
    private String content;
    private String contentType;
    private String category;
    private List<String> hashtags;
}