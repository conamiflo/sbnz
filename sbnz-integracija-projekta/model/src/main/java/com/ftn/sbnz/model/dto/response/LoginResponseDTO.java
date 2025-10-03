package com.ftn.sbnz.model.dto.response;

import lombok.Data;

@Data
public class LoginResponseDTO {
    private String accessToken;
    private String username;

    public LoginResponseDTO(String accessToken, String username) {
        this.accessToken = accessToken;
        this.username = username;
    }

}