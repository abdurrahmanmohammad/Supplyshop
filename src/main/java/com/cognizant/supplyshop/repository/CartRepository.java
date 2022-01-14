package com.cognizant.supplyshop.repository;

import com.cognizant.supplyshop.model.Cart;
import com.cognizant.supplyshop.model.User;
import org.springframework.data.jpa.repository.JpaRepository;

public interface CartRepository extends JpaRepository<Cart, Long> {
    Cart findByUser(User user);
}