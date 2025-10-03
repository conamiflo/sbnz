package com.ftn.sbnz.service.services.interfaces;

import com.ftn.sbnz.model.dto.request.LoginRequestDTO;
import com.ftn.sbnz.model.dto.request.UserRegistrationDTO;
import com.ftn.sbnz.model.dto.response.LoginResponseDTO;
import com.ftn.sbnz.model.dto.response.UserResponseDTO;
import org.springframework.http.ResponseEntity;

public interface IAuthService {
    ResponseEntity<LoginResponseDTO> login(LoginRequestDTO loginRequest);
    ResponseEntity<UserResponseDTO> register(UserRegistrationDTO dto);
}
