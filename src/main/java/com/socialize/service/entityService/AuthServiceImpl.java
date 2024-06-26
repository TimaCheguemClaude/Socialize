package com.socialize.service.entityService;

import com.socialize.auth.AuthenticationRequest;
import com.socialize.auth.AuthenticationResponse;
import com.socialize.auth.RegisterRequest;
import com.socialize.enums.UserAuthority;
import com.socialize.model.User;
import com.socialize.repository.UserRepository;
import com.socialize.security.jwt.JwtService;
import lombok.RequiredArgsConstructor;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;


@Service
@RequiredArgsConstructor
public class AuthServiceImpl implements AuthService {
    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;
    private final JwtService jwtService;
    private final AuthenticationManager authenticationManager;

    @Override
    public AuthenticationResponse signup(RegisterRequest request) {
        var user = User.builder()
                .name(request.getName())
                .username(request.getUsername())
                .email(request.getEmail())
                .password(passwordEncoder.encode(request.getPassword()))
                .authority(UserAuthority.USER)
                .build();
        userRepository.save(user);
        var jwtToken = jwtService.generateJwtToken(user);
        return AuthenticationResponse.builder()
                .token(jwtToken)
                .build();
    }

    @Override
    public AuthenticationResponse login(AuthenticationRequest request) {
        authenticationManager.authenticate(
                new UsernamePasswordAuthenticationToken(
                        request.getEmail(),
                        request.getPassword()
                )
        );
        var user = userRepository.findByEmail(request.getEmail())
                .orElseThrow(); //todo throw and handle an appropriate exception
        var jwtToken = jwtService.generateJwtToken(user);
        return AuthenticationResponse.builder()
                .token(jwtToken)
                .build();
    }
}