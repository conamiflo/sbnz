package com.ftn.sbnz.service.services;

import com.ftn.sbnz.model.dto.request.LoginRequestDTO;
import com.ftn.sbnz.model.dto.request.UserRegistrationDTO;
import com.ftn.sbnz.model.dto.response.LoginResponseDTO;
import com.ftn.sbnz.model.dto.response.UserResponseDTO;
import com.ftn.sbnz.service.services.interfaces.IAuthService;
import com.ftn.sbnz.service.services.interfaces.IUserService;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.oauth2.jwt.JwtClaimsSet;
import org.springframework.security.oauth2.jwt.JwtEncoder;
import org.springframework.security.oauth2.jwt.JwtEncoderParameters;
import org.springframework.stereotype.Service;
import com.ftn.sbnz.model.models.User;
import com.ftn.sbnz.service.repositories.UserRepository;
import org.kie.api.runtime.KieSession;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Qualifier;


import java.time.Instant;
import java.time.temporal.ChronoUnit;
import java.util.stream.Collectors;

@Service
public class AuthService implements IAuthService {

    private final AuthenticationManager authenticationManager;
    private final JwtEncoder jwtEncoder;
    private final IUserService userService;
    // ✅ ДОДАТЕ ЗАВИСНОСТИ
    private final UserRepository userRepository;
    private final KieSession cepKsession;
    private static final Logger log = LoggerFactory.getLogger(AuthService.class);


    @Value("${app.jwt.expiration}")
    private long jwtExpirySeconds;

    // ✅ АЖУРИРАН КОНСТРУКТОР
    public AuthService(AuthenticationManager authenticationManager,
                       JwtEncoder jwtEncoder,
                       IUserService userService,
                       UserRepository userRepository,
                       @Qualifier("cepKsession") KieSession cepKsession
    ) {
        this.authenticationManager = authenticationManager;
        this.jwtEncoder = jwtEncoder;
        this.userService = userService;
        // ✅ ДОДЕЛИ ЗАВИСНОСТИ
        this.userRepository = userRepository;
        this.cepKsession = cepKsession;
    }

    @Override
    public ResponseEntity<LoginResponseDTO> login(LoginRequestDTO loginRequest) {
        Authentication authentication = authenticationManager.authenticate(
                new UsernamePasswordAuthenticationToken(loginRequest.getUsername(), loginRequest.getPassword())
        );
        String username = authentication.getName();

        Instant now = Instant.now();
        JwtClaimsSet claims = JwtClaimsSet.builder()
                .issuer("self")
                .issuedAt(now)
                .expiresAt(now.plus(jwtExpirySeconds, ChronoUnit.SECONDS))
                .subject(username)
                .claim("scope", authentication.getAuthorities().stream()
                        .map(GrantedAuthority::getAuthority)
                        .collect(Collectors.joining(" ")))
                .build();
        String token = jwtEncoder.encode(JwtEncoderParameters.from(claims)).getTokenValue();

        try {
            User loggedInUser = userRepository.findByUsername(username).orElse(null);
            if (loggedInUser != null) {
                cepKsession.insert(loggedInUser);
                log.info("User {} inserted into CEP session upon login.", username);
            } else {
                log.warn("User {} authenticated but not found in repository for CEP session.", username);
            }
        } catch (Exception e) {
            log.error("!!! Error inserting User into CEP session during login for {} !!!", username, e);
        }

        return ResponseEntity.ok(new LoginResponseDTO(token, username));
    }

    @Override
    public ResponseEntity<UserResponseDTO> register(UserRegistrationDTO dto) {
        UserResponseDTO user = userService.registerUser(dto);
        return new ResponseEntity<>(user, HttpStatus.CREATED);
    }
}

