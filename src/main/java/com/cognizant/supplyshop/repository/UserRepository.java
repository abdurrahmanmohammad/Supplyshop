package com.cognizant.supplyshop.repository;


import com.cognizant.supplyshop.model.User;
import org.springframework.data.jpa.repository.JpaRepository;

public interface UserRepository extends JpaRepository<User, Long> {
    boolean existsByEmail(String email);
    User findByEmail(String email);
    User findByEmailAndActive(String email, boolean active);
}