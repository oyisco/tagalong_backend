package com.tagalong.controller;


//import com.tagalong.config.JwtTokenUtil;

import com.tagalong.config.JwtTokenUtil;
import com.tagalong.dto.ApiResponse;
import com.tagalong.dto.JwtResponse;
import com.tagalong.dto.LoginRequest;
import com.tagalong.dto.SignUpRequest;
import com.tagalong.exception.AppException;
import com.tagalong.exception.BlogapiException;
import com.tagalong.model.repository.RoleRepository;
import com.tagalong.model.repository.UserRepository;
import com.tagalong.model.role.Role;
import com.tagalong.model.role.RoleName;
import com.tagalong.model.user.User;
//import com.tagalong.service.JwtUserDetailsService;
import com.tagalong.service.JwtUserDetailsService;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
//import org.springframework.security.authentication.AuthenticationManager;
//import org.springframework.security.authentication.BadCredentialsException;
//import org.springframework.security.authentication.DisabledException;
//import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
//import org.springframework.security.core.userdetails.UserDetails;
//import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.DisabledException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.support.ServletUriComponentsBuilder;

import javax.validation.Valid;
import java.net.URI;
import java.util.ArrayList;
import java.util.List;

@RestController
@RequestMapping("/api/auth")
@RequiredArgsConstructor
public class AuthController {
    private static final String USER_ROLE_NOT_SET = "User role not set";
    private final AuthenticationManager authenticationManager;
    private final UserRepository userRepository;
    private final RoleRepository roleRepository;
    private final JwtUserDetailsService userDetailsService;
    private final PasswordEncoder bcryptEncoder;
    private final JwtTokenUtil jwtTokenUtil;


    @RequestMapping(value = "/signin", method = RequestMethod.POST)
    public ResponseEntity<?> createAuthenticationToken(@RequestBody LoginRequest authenticationRequest) throws Exception {
        authenticate(authenticationRequest.getUsernameOrEmail(), authenticationRequest.getPassword());
        final UserDetails userDetails = userDetailsService
                .loadUserByUsername(authenticationRequest.getUsernameOrEmail());
        final String token = jwtTokenUtil.generateToken(userDetails);
        System.out.println("TOKEN " + token);

        return ResponseEntity.ok(new JwtResponse("Bearer " + token, authenticationRequest.getUsernameOrEmail()));
    }

    private void authenticate(String username, String password) throws Exception {
        try {
            authenticationManager.authenticate(new UsernamePasswordAuthenticationToken(username, password));
        } catch (DisabledException e) {
            throw new Exception("USER_DISABLED", e);
        } catch (BadCredentialsException e) {
            throw new Exception("INVALID_CREDENTIALS", e);
        }
    }

    @PostMapping("/signup")
    public ResponseEntity<ApiResponse> registerUser(@Valid @RequestBody SignUpRequest signUpRequest) {
        System.out.println("chake1");
        if (Boolean.TRUE.equals(userRepository.existsByEmail(signUpRequest.getEmail()))) {
            throw new BlogapiException(HttpStatus.BAD_REQUEST, "Email is already taken");
        }
        System.out.println("chake1");

        if (Boolean.TRUE.equals(userRepository.existsByPhone(signUpRequest.getPhone()))) {
            throw new BlogapiException(HttpStatus.BAD_REQUEST, "Phone number already exists on our records");
        }
        System.out.println("chake1");
//		SignUpRequest signup = new SignUpRequest();
        String firstName = signUpRequest.getFirstName().toLowerCase();

        String lastName = signUpRequest.getLastName().toLowerCase();

        String email = signUpRequest.getEmail().toLowerCase();

        //String password = bcryptEncoder.encode(signUpRequest.getPassword());
        String password = signUpRequest.getPassword();

        String phone = signUpRequest.getPhone().toLowerCase();

        Boolean verified = signUpRequest.getVerified();

        User user = new User();
//		firstName, lastName, email, password, phone, verified
        user.setFirstName(firstName);
        user.setLastName(lastName);
        user.setEmail(email);
        user.setPassword(password);
        user.setPhone(phone);
        user.setVerified(verified);

        System.out.println("chake1");
        List<Role> roles = new ArrayList<>();
        System.out.println("chake1");

            roles.add(roleRepository.findByName(RoleName.ROLE_USER)
                    .orElseThrow(() -> new AppException(USER_ROLE_NOT_SET)));
            //roles.add(roleRepository.findByName(RoleName.ROLE_ADMIN)
                 //   .orElseThrow(() -> new AppException(USER_ROLE_NOT_SET)));

           // roles.add(roleRepository.findByName(RoleName.ROLE_USER)
                   // .orElseThrow(() -> new AppException(USER_ROLE_NOT_SET)));

        System.out.println("chake1");
        user.setRoles(roles);
        System.out.println("chake1");
        User result = userRepository.save(user);
        System.out.println("chake1");
        URI location = ServletUriComponentsBuilder.fromCurrentContextPath().path("/api/users/{userId}")
                .buildAndExpand(result.getId()).toUri();
        System.out.println("chake1");
        return ResponseEntity.created(location).body(new ApiResponse(Boolean.TRUE, "User registered successfully"));
    }
}
