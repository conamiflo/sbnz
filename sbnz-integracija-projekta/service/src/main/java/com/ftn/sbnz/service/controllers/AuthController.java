package com.ftn.sbnz.service.controllers;

import com.ftn.sbnz.model.dto.request.LoginRequestDTO;
import com.ftn.sbnz.model.dto.request.UserRegistrationDTO;
import com.ftn.sbnz.model.dto.response.LoginResponseDTO;
import com.ftn.sbnz.model.dto.response.UserResponseDTO;
import com.ftn.sbnz.service.services.interfaces.IAuthService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;


@RestController
@RequestMapping("/api/auth")
public class AuthController {

    private static IAuthService authService;
    public AuthController(IAuthService authService) {
        AuthController.authService = authService;
    }

    @PostMapping("/login")
    public ResponseEntity<LoginResponseDTO> login(@RequestBody LoginRequestDTO loginRequest) {
        return authService.login(loginRequest);
    }

    @PostMapping("/register")
    public ResponseEntity<UserResponseDTO> register(@RequestBody UserRegistrationDTO userRegistrationDTO) {
        return authService.register(userRegistrationDTO);
    }


}
