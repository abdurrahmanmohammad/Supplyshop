package com.cognizant.supplyshop.service;

import com.cognizant.supplyshop.model.CustomJWT;
import com.cognizant.supplyshop.model.Role;
import com.cognizant.supplyshop.model.User;
import com.cognizant.supplyshop.repository.UserRepository;
import com.cognizant.supplyshop.util.JwtUtils;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import javax.servlet.http.HttpServletRequest;
import java.util.Base64;
import java.util.Optional;

@Service
@RequiredArgsConstructor
public class AuthService {
    @Autowired private JwtUtils jwtUtils;
    @Autowired private ObjectMapper objectMapper;
    @Autowired private UserRepository userRepository;
    @Autowired private HttpServletRequest request;

    public boolean isAdmin() {
        User user = getCurrentUser();
        return user != null && user.getRole().equals(Role.ADMIN) && user.isActive();
    }

    public boolean isCurrentUser(Long id) {
        User currentUser = getCurrentUser();
        Optional<User> requestedUser = userRepository.findById(id);
        if(currentUser == null || !currentUser.isActive() || requestedUser.isEmpty()) return false;
        return currentUser.getEmail().equals(requestedUser.get().getEmail());
    }

    public User getCurrentUser() {
        String[] token = request.getHeader("Authorization").substring(7).split("\\.");
        String decodedPayload = new String(Base64.getDecoder().decode(token[1]));
        try {
            CustomJWT jwt = objectMapper.readValue(decodedPayload, CustomJWT.class);
            return userRepository.findByEmail(jwt.getSub());
        } catch (JsonProcessingException e) {
            return null;
        }
    }
}
