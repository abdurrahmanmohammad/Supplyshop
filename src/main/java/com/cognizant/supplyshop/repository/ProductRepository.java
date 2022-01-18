package com.cognizant.supplyshop.repository;


import com.cognizant.supplyshop.model.Product;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface ProductRepository extends JpaRepository<Product, Long> {
    boolean existsByName(String name);
    Optional<Product> findByIdAndDeleted(Long id, boolean deleted);
    List<Product> findByDeleted(boolean deleted);
}