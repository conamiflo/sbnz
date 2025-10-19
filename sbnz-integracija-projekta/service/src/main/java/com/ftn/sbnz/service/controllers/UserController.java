package com.ftn.sbnz.service.controllers;

import com.ftn.sbnz.model.dto.response.UserResponseDTO;
import com.ftn.sbnz.service.services.interfaces.IUserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.server.ResponseStatusException;

import javax.persistence.EntityNotFoundException;

@RestController
@RequestMapping("/api/users")
public class UserController {

    private final IUserService userService;

    @Autowired
    public UserController(IUserService userService) {
        this.userService = userService;
    }


    @GetMapping("/me")
    public ResponseEntity<UserResponseDTO> getCurrentUserProfile() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();

        if (authentication == null || !authentication.isAuthenticated() || "anonymousUser".equals(authentication.getPrincipal())) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).build();
        }

        String username = authentication.getName();

        try {
            UserResponseDTO userDto = userService.getUserDtoByUsername(username);
            return ResponseEntity.ok(userDto);
        } catch (EntityNotFoundException e) {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, "Authenticated user not found in database", e);
        } catch (Exception e) {
            throw new ResponseStatusException(HttpStatus.INTERNAL_SERVER_ERROR, "Error retrieving user profile", e);
        }
    }

    @GetMapping("/{username}")
    public ResponseEntity<UserResponseDTO> getUserProfileByUsername(@PathVariable String username) {
        try {
            UserResponseDTO userDto = userService.getUserDtoByUsername(username);
            return ResponseEntity.ok(userDto);
        } catch (EntityNotFoundException e) {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, "User not found with username: " + username, e);
        } catch (Exception e) {
            throw new ResponseStatusException(HttpStatus.INTERNAL_SERVER_ERROR, "Error retrieving user profile", e);
        }
    }

}