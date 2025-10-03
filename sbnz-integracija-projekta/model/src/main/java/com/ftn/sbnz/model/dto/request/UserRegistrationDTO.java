package com.ftn.sbnz.model.dto.request;

import lombok.Value;
import lombok.Value;
import java.util.List;

@Value
public class UserRegistrationDTO {

    String username;
    String password;
    String name;
    int age;
    String location;
    String gender;
    List<String> interests;
    String creatorType;
    int audienceSize;

}