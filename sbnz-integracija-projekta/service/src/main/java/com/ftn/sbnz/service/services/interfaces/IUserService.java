package com.ftn.sbnz.service.services.interfaces;

import com.ftn.sbnz.model.dto.request.UserRegistrationDTO;
import com.ftn.sbnz.model.dto.response.UserResponseDTO;
import org.springframework.security.core.userdetails.UserDetails;

import javax.persistence.EntityNotFoundException;

public interface IUserService {

    UserResponseDTO registerUser(UserRegistrationDTO registrationDTO);
    UserDetails loadUserByUsername(String username);
    UserResponseDTO getUserDtoByUsername(String username) throws EntityNotFoundException;

}
