package com.ftn.sbnz.service.services.interfaces;

import com.ftn.sbnz.model.dto.request.UserRegistrationDTO;
import com.ftn.sbnz.model.dto.response.UserResponseDTO;
import org.springframework.security.core.userdetails.UserDetails;

public interface IUserService {

    UserResponseDTO registerUser(UserRegistrationDTO registrationDTO);
    UserDetails loadUserByUsername(String username);

}
