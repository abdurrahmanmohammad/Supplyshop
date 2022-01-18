package com.cognizant.supplyshop.controller;

import com.cognizant.supplyshop.model.Role;
import com.cognizant.supplyshop.model.User;
import com.cognizant.supplyshop.repository.UserRepository;
import com.cognizant.supplyshop.service.AuthService;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.crypto.bcrypt.BCrypt;
import org.springframework.web.bind.annotation.*;

import java.util.Map;
import java.util.Optional;


@RestController
@RequestMapping("/users")
@CrossOrigin(origins = "http://localhost:3000")
public class UserController {
    @Autowired private UserRepository userRepository;
    @Autowired private AuthService authService;

    @GetMapping
    public ResponseEntity<?> getUsers() {
        if (!authService.isAdmin()) return ResponseEntity.status(HttpStatus.UNAUTHORIZED).build(); // Authenticate admin
        return ResponseEntity.status(HttpStatus.OK).body(userRepository.findAll());
    }

    @GetMapping("/{id}")
    public ResponseEntity<?> getUser(@PathVariable Long id) {
        if (authService.isCurrentUser(id) || authService.isAdmin())
            return ResponseEntity.status(HttpStatus.OK).body(userRepository.findById(id));
        return ResponseEntity.status(HttpStatus.UNAUTHORIZED).build();
    }

    @GetMapping("/me")
    public ResponseEntity<?> getCurrentUser() {
        return ResponseEntity.ok(authService.getCurrentUser());
    }

    @PatchMapping("/updateMe")
    public ResponseEntity<?> updateCurrentUser(@RequestBody Map<String, String> fields) {
        User user = authService.getCurrentUser();
        if (user == null) return ResponseEntity.status(HttpStatus.UNAUTHORIZED).build();
        return updateUserFields(fields, user); // Update user fields
    }

    @PatchMapping("/{id}")
    public ResponseEntity<?> updateUser(@PathVariable Long id, @RequestBody Map<String, String> fields) {
        if (!authService.isAdmin()) return ResponseEntity.status(HttpStatus.UNAUTHORIZED).build();
        Optional<User> getUser = userRepository.findById(id);
        if (getUser.isEmpty()) return ResponseEntity.status(HttpStatus.NOT_FOUND).body(fields);
        User user = getUser.get();
        return updateUserFields(fields, user); // Update user fields
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<?> deleteUser(@PathVariable Long id) {
        if (!authService.isAdmin()) return ResponseEntity.status(HttpStatus.UNAUTHORIZED).build(); // Authenticate admin
        Optional<User> getUser = userRepository.findById(id);
        if(getUser.isEmpty()) return ResponseEntity.status(HttpStatus.NOT_FOUND).build();
        User user = getUser.get();
        user.setActive(false);
        userRepository.save(user);
        return ResponseEntity.status(HttpStatus.OK).build();
    }

    private ResponseEntity<?> updateUserFields(Map<String, String> fields, User user) {
        // Update user fields
        if (fields.containsKey("name")) user.setName(fields.get("name"));
        if (fields.containsKey("email")) user.setEmail(fields.get("email"));
        if (fields.containsKey("password")) user.setPassword(BCrypt.hashpw(fields.get("password"), BCrypt.gensalt(12)));
        if (fields.containsKey("active")) user.setActive(fields.get("active").equalsIgnoreCase("true"));
        if (fields.containsKey("role")) {
            Role role = fields.get("role").equalsIgnoreCase(Role.ADMIN.toString()) ? Role.ADMIN : Role.CUSTOMER;
            user.setRole(role);
        }
        return ResponseEntity.status(HttpStatus.OK).body(userRepository.save(user));
    }
}
