package com.tagalong.controller;


//import com.tagalong.config.JwtTokenUtil;

import com.tagalong.config.JwtTokenUtil;
import com.tagalong.dto.*;
import com.tagalong.exception.AppException;
import com.tagalong.exception.BlogapiException;
import com.tagalong.model.repository.RoleRepository;
import com.tagalong.model.repository.UserRepository;
import com.tagalong.model.role.Role;
import com.tagalong.model.role.RoleName;
import com.tagalong.model.user.User;
//import com.tagalong.service.JwtUserDetailsService;
import com.tagalong.service.JwtUserDetailsService;
import io.jsonwebtoken.*;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
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

import javax.persistence.EntityNotFoundException;
import javax.validation.Valid;
import java.net.URI;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.function.Function;

@RestController
@RequestMapping("/api/auth")
@RequiredArgsConstructor
@Slf4j
public class AuthController {
    private static final String USER_ROLE_NOT_SET = "User role not set";
    private static final String SECRET = "PH3gdtPrssPH3gdtPrssPH3gdtPrssPH3gdtPrssPH3gdtPrssPH3gdtPrssPH3gdtPrssPH3gdtPrssPrssPH3gdtPrss";
    private final AuthenticationManager authenticationManager;
    private final UserRepository userRepository;
    private final RoleRepository roleRepository;
    private final JwtUserDetailsService userDetailsService;
    private final PasswordEncoder bcryptEncoder;
    private final JwtTokenUtil jwtTokenUtil;
    //private final TokenService tokenService;


    @RequestMapping(value = "/signin", method = RequestMethod.POST)
    public ResponseEntity<?> createAuthenticationToken(@RequestBody LoginRequest authenticationRequest) throws Exception {
        ResponseDTO responseDTO = new ResponseDTO();
        User userOptional = this.userRepository.findByEmail(authenticationRequest.getUsernameOrEmail()).orElseThrow(() -> new EntityNotFoundException("Could not find entity with id: "));
        String accountType = userOptional.getAccountType();
        String accountType1 = authenticationRequest.getAccountType();
        if (accountType.equalsIgnoreCase(accountType1)) {
            authenticate(authenticationRequest.getUsernameOrEmail(), authenticationRequest.getPassword());
            final UserDetails userDetails = userDetailsService
                    .loadUserByUsername(authenticationRequest.getUsernameOrEmail());
            final String token = jwtTokenUtil.generateToken(userDetails);
            System.out.println("TOKEN " + token);
            return ResponseEntity.ok(new JwtResponse("Bearer " + token, authenticationRequest.getUsernameOrEmail()));
        }
        if (!accountType.equalsIgnoreCase(accountType1)) {

            responseDTO.setMessage("this account to another does not match your account type ");
        }
        return ResponseEntity.ok(responseDTO);
//            else if (accountType.equalsIgnoreCase("user")) {
//                authenticate(authenticationRequest.getUsernameOrEmail(), authenticationRequest.getPassword());
//                final UserDetails userDetails = userDetailsService
//                        .loadUserByUsername(authenticationRequest.getUsernameOrEmail());
//                final String token = jwtTokenUtil.generateToken(userDetails);
//                System.out.println("TOKEN " + token);
//                return ResponseEntity.ok(new JwtResponse("Bearer " + token, authenticationRequest.getUsernameOrEmail()));
//            }


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

        String password = bcryptEncoder.encode(signUpRequest.getPassword());


        String phone = signUpRequest.getPhone().toLowerCase();

        Boolean verified = signUpRequest.getVerified();

        User user = new User();
//		firstName, lastName, email, password, phone, verified
        user.setAccountType(signUpRequest.getAccountType());
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


    //check if the token has expired
    @GetMapping("verify-jwt-token")
    private Boolean isTokenExpired(String token) {
        final Date expiration = getExpirationDateFromToken(token);
        return expiration.before(new Date());
    }

    //retrieve expiration date from jwt token
    public Date getExpirationDateFromToken(String token) {
        return getClaimFromToken(token, Claims::getExpiration);
    }

    public <T> T getClaimFromToken(String token, Function<Claims, T> claimsResolver) {
        final Claims claims = getAllClaimsFromToken(token);
        return claimsResolver.apply(claims);
    }

    private Claims getAllClaimsFromToken(String token) {
        return Jwts.parser().setSigningKey(SECRET).parseClaimsJws(token).getBody();
    }

//
//    public void deleteToken(String key) {
//        this.tokenService.delete(key);
//    }

//
//    @GetMapping("verify-jwt-token")
//    public boolean validateToken(@RequestParam String token) {
//        try {
//            Jwts.parser().setSigningKey(SECRET).parseClaimsJws(token);
//            return true;
//        } catch (MalformedJwtException ex) {
//            log.info("Invalid JWT token");
//        } catch (ExpiredJwtException ex) {
//            log.info("Expired JWT token");
//            //  httpServletRequest.setAttribute("expired",ex.getMessage());
//        } catch (UnsupportedJwtException ex) {
//            log.info("Unsupported JWT exception");
//        } catch (IllegalArgumentException ex) {
//            log.info("Jwt claims string is empty");
//        }
//        return false;
//    }

//    public boolean validateToken(String token) {
//        try {
//            Jwts.parser().setSigningKey(SECRET).parseClaimsJws(token);
//            return true;
//        } catch (MalformedJwtException ex) {
//            log.info("Invalid JWT token");
//        } catch (ExpiredJwtException ex) {
//            log.info("Expired JWT token");
//            //  httpServletRequest.setAttribute("expired",ex.getMessage());
//        } catch (UnsupportedJwtException ex) {
//            log.info("Unsupported JWT exception");
//        } catch (IllegalArgumentException ex) {
//            log.info("Jwt claims string is empty");
//        }
//        return false;
//    }


}
