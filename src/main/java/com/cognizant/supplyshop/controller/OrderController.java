package com.cognizant.supplyshop.controller;

import com.cognizant.supplyshop.model.Order;
import com.cognizant.supplyshop.model.OrderItem;
import com.cognizant.supplyshop.model.Product;
import com.cognizant.supplyshop.repository.OrderRepository;
import com.cognizant.supplyshop.repository.ProductRepository;
import com.cognizant.supplyshop.service.AuthService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.util.ReflectionUtils;
import org.springframework.web.bind.annotation.*;

import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Optional;

@RestController
@RequestMapping("/orders")
@CrossOrigin(origins = "http://localhost:3000")
public class OrderController {
    @Autowired private OrderRepository orderRepository;
    @Autowired private ProductRepository productRepository;
    @Autowired private AuthService authService;

    @GetMapping
    public ResponseEntity<?> getOrders() {
        if (!authService.isAdmin()) return ResponseEntity.status(HttpStatus.UNAUTHORIZED).build(); // Authenticate admin
        return ResponseEntity.status(HttpStatus.OK).body(orderRepository.findAll());
    }

    @GetMapping("/{id}")
    public ResponseEntity<Order> getOrder(@PathVariable Long id) {
        Optional<Order> getOrder = orderRepository.findById(id);
        if (getOrder.isEmpty()) return ResponseEntity.status(HttpStatus.NOT_FOUND).build();
        Order order = getOrder.get();
        if (!authService.isAdmin()) return ResponseEntity.status(HttpStatus.UNAUTHORIZED).build(); // Authenticate admin
        return ResponseEntity.status(HttpStatus.OK).body(order);

    }

    @GetMapping("/mine")
    public ResponseEntity<List<Order>> getMyOrders() {
        return ResponseEntity.status(HttpStatus.OK).body(orderRepository.findByUser(authService.getCurrentUser()));
    }

    @GetMapping("mine/{id}")
    public ResponseEntity<Order> getMyOrder(@PathVariable Long id) {
        Optional<Order> getOrder = orderRepository.findById(id);
        if (getOrder.isEmpty()) return ResponseEntity.status(HttpStatus.NOT_FOUND).build();
        Order order = getOrder.get();
        if (!authService.getCurrentUser().getEmail().equals(order.getUser().getEmail()))
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).build();
        return ResponseEntity.status(HttpStatus.OK).body(order);
    }

    @PostMapping
    public ResponseEntity<Order> createOrder(@RequestBody Order order) {
        //if (!authService.isAdmin()) return ResponseEntity.status(HttpStatus.UNAUTHORIZED).build(); // Authenticate admin
        // Update counts of products in inventory
        List<Product> products = new ArrayList<>();
        for (OrderItem orderItem : order.getOrderItems()) {
            Optional<Product> getProduct = productRepository.findById(orderItem.getProduct().getId());
            if (getProduct.isEmpty()) return ResponseEntity.status(HttpStatus.BAD_REQUEST).build();
            Product product = getProduct.get();
            int countInStock = product.getCountInStock() - orderItem.getQuantity();
            if (countInStock < 0)
                return ResponseEntity.status(HttpStatus.BAD_REQUEST).build(); // Invalid request: ordered items exceed stock
            product.setCountInStock(countInStock);
            products.add(product); // Add updated product to list
        }
        order.setUser(authService.getCurrentUser());
        productRepository.saveAll(products);
        return ResponseEntity.status(HttpStatus.CREATED).body(orderRepository.save(order));
    }

    @PatchMapping("/{id}")
    public ResponseEntity<?> updateOrder(@PathVariable Long id, @RequestBody Map<Object, Object> fields) {
        if (!authService.isAdmin()) return ResponseEntity.status(HttpStatus.UNAUTHORIZED).build(); // Authenticate admin
        Optional<Order> getOrder = orderRepository.findById(id);
        if (getOrder.isEmpty()) return ResponseEntity.status(HttpStatus.NOT_FOUND).body(fields);
        Order order = getOrder.get();
        fields.forEach((key, value) -> {
            Field field = ReflectionUtils.findField(Order.class, key.toString());
            field.setAccessible(true);
            ReflectionUtils.setField(field, order, value);
        });
        return ResponseEntity.status(HttpStatus.NOT_FOUND).body(orderRepository.save(order));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<?> deleteOrder(@PathVariable Long id) {
        if (!authService.isAdmin()) return ResponseEntity.status(HttpStatus.UNAUTHORIZED).build(); // Authenticate admin
        orderRepository.deleteById(id);
        return ResponseEntity.status(HttpStatus.OK).build();
    }

    @PutMapping("pay/{id}")
    public ResponseEntity<?> payOrder(@PathVariable Long id) {
        Optional<Order> getOrder = orderRepository.findById(id);
        if (getOrder.isEmpty()) ResponseEntity.status(HttpStatus.NOT_FOUND).build();
        Order order = getOrder.get();
        if (authService.getCurrentUser().getId().equals(order.getId()) || !authService.isAdmin())
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).build(); // Authenticate customer or admin
        order.setPaid(true);
        orderRepository.save(order);
        return ResponseEntity.status(HttpStatus.OK).build();
    }

    @PutMapping("deliver/{id}")
    public ResponseEntity<?> deliverOrder(@PathVariable Long id) {
        if (!authService.isAdmin()) return ResponseEntity.status(HttpStatus.UNAUTHORIZED).build(); // Authenticate admin
        Optional<Order> getOrder = orderRepository.findById(id);
        if (getOrder.isEmpty()) ResponseEntity.status(HttpStatus.NOT_FOUND).build();
        Order order = getOrder.get();
        order.setDelivered(true);
        orderRepository.save(order);
        return ResponseEntity.status(HttpStatus.OK).build();
    }
}
