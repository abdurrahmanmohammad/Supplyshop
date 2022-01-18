package com.cognizant.supplyshop.controller;


import com.cognizant.supplyshop.model.Role;
import com.cognizant.supplyshop.model.User;
import com.cognizant.supplyshop.repository.UserRepository;
import com.cognizant.supplyshop.util.JwtUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.crypto.bcrypt.BCrypt;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.Locale;
import java.util.Map;

@RestController
@RequestMapping("/auth")
@CrossOrigin(origins = "http://localhost:3000")
public class AuthController {
    @Autowired private UserRepository userRepository;
    @Autowired private JwtUtils jwtUtils;

    @PostMapping("/login")
    public ResponseEntity<?> loginUser(@RequestBody User user) {
        Map<String, String> userInfo = new HashMap<>();
        User foundUser = userRepository.findByEmailAndActive(user.getEmail(), true); // Attempt to retrieve user from DB (if user is created)
        if(foundUser == null || !BCrypt.checkpw(user.getPassword(), foundUser.getPassword())) // If user not exist or invalid password
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body("Please use correct email or password");
        String createdToken = jwtUtils.generateToken(user); // Authenticate user by creating jwt
        if(createdToken.equals("")) return ResponseEntity.status(HttpStatus.CONFLICT).build();
        userInfo.put("email", foundUser.getEmail());
        userInfo.put("name", foundUser.getName());
        userInfo.put("role", foundUser.getRole().toString().toLowerCase());
        userInfo.put("token", "Bearer " + createdToken);
        //return ResponseEntity.ok(new AuthenticationResponse("Bearer " + createdToken));
        return ResponseEntity.ok(userInfo);
    }

    @PostMapping("/signup")
    public ResponseEntity<?> signupUser(@RequestBody User user){
        Map<String, String> userInfo = new HashMap<>();
        if (userRepository.existsByEmail(user.getEmail())) // If user already exists
            return ResponseEntity.status(HttpStatus.CONFLICT).build();
        user.setPassword(BCrypt.hashpw(user.getPassword(), BCrypt.gensalt(12))); // Hash password
        user.setRole(Role.CUSTOMER); // Give user customer access/role
        user.setActive(true); // Activate user
        userRepository.save(user); // Create user in db
        String createdToken = jwtUtils.generateToken(user); // Create jwt
        if(createdToken.equals("")) return ResponseEntity.status(HttpStatus.CONFLICT).build();
        userInfo.put("email", user.getEmail());
        userInfo.put("name", user.getName());
        userInfo.put("role", user.getRole().toString().toLowerCase());
        userInfo.put("token", "Bearer " + createdToken);
//        return ResponseEntity.ok(new AuthenticationResponse("Bearer " + createdToken));
        return ResponseEntity.ok(userInfo);
    }
}
