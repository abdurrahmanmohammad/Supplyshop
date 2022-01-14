package com.cognizant.supplyshop.repository;


import com.cognizant.supplyshop.model.Order;
import com.cognizant.supplyshop.model.User;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface OrderRepository extends JpaRepository<Order, Long> {
    List<Order> findByUser(User user);
}
