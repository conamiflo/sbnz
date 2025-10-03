package com.ftn.sbnz.model.dto.response;

import lombok.Value;

import lombok.Value;
import java.util.List;

@Value
public class UserResponseDTO {
    Long id;
    String username;
    String name;
    int age;
    String location;
    String gender;
    List<String> interests;
    String creatorType;
    int audienceSize;
}