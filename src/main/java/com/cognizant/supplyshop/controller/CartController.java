package com.cognizant.supplyshop.controller;

import com.cognizant.supplyshop.model.Cart;
import com.cognizant.supplyshop.repository.CartRepository;
import com.cognizant.supplyshop.repository.ProductRepository;
import com.cognizant.supplyshop.repository.UserRepository;
import com.cognizant.supplyshop.service.AuthService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Optional;

@RestController
@RequestMapping("/cart")
@CrossOrigin(origins = "http://localhost:3000")
public class CartController {
    @Autowired public CartRepository cartRepository;
    @Autowired private ProductRepository productRepository;
    @Autowired private UserRepository userRepository;
    @Autowired AuthService authService;

    @GetMapping
    public List<Cart> getCart() {
        return cartRepository.findAll();
    }

    @PostMapping
    public ResponseEntity<Cart> createCart(@RequestBody Cart cart) {
        /*
        if(cart.getUser() != null && cart.getProduct() != null
                && productRepository.existsById(cart.getProduct().get())
        && userRepository.existsById(cart.getUser().getId())){
            cart.setProduct(productRepository.findById((cart.getProduct().getId())));
            cart.setUser(userRepository.findById((cart.getUser().getId())));
        }

         */
        return ResponseEntity.status(HttpStatus.CREATED).body(cartRepository.save(cart));
    }

    @PatchMapping("/{id}")
    public ResponseEntity<Cart> updateCart(@PathVariable Long id, @RequestBody Cart patchCart) {
        return ResponseEntity.status(HttpStatus.NOT_FOUND).body(cartRepository.save(patchCart));
    }

    @DeleteMapping("/{id}")
    public void deleteCart(@PathVariable Long id) {
        cartRepository.deleteById(id);
    }

}
