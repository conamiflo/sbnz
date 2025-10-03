package com.ftn.sbnz.service.services;

import com.ftn.sbnz.model.dto.request.UserRegistrationDTO;
import com.ftn.sbnz.model.dto.response.UserResponseDTO;
import com.ftn.sbnz.model.models.User;
import com.ftn.sbnz.service.repositories.UserRepository;
import com.ftn.sbnz.service.services.interfaces.IUserService;
import lombok.AllArgsConstructor;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.Collections;
import java.util.Optional;

@Service
@AllArgsConstructor
public class UserService implements IUserService, UserDetailsService {

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;

    @Override
    public UserResponseDTO registerUser(UserRegistrationDTO registrationDTO) {
        userRepository.findByUsername(registrationDTO.getUsername()).ifPresent(u -> {
            throw new IllegalStateException("Username already exists.");
        });

        User newUser = new User();
        newUser.setUsername(registrationDTO.getUsername());
        newUser.setName(registrationDTO.getName());
        newUser.setAge(registrationDTO.getAge());
        newUser.setLocation(registrationDTO.getLocation());
        newUser.setGender(registrationDTO.getGender());
        newUser.setInterests(registrationDTO.getInterests());
        newUser.setCreatorType(registrationDTO.getCreatorType());
        newUser.setAudienceSize(registrationDTO.getAudienceSize());

        newUser.setPassword(passwordEncoder.encode(registrationDTO.getPassword()));
        User savedUser = userRepository.save(newUser);

        return new UserResponseDTO(
                savedUser.getId(),
                savedUser.getUsername(),
                savedUser.getName(),
                savedUser.getAge(),
                savedUser.getLocation(),
                savedUser.getGender(),
                savedUser.getInterests(),
                savedUser.getCreatorType(),
                savedUser.getAudienceSize()
        );
    }

    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        User user = userRepository.findByUsername(username)
                .orElseThrow(() -> new UsernameNotFoundException("User not found with username: " + username));

        return new org.springframework.security.core.userdetails.User(
                user.getUsername(),
                user.getPassword(),
                Collections.singletonList(new SimpleGrantedAuthority("ROLE_" + "USER"))
        );
    }
}