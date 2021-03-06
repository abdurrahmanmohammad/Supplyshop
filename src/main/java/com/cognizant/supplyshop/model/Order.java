package com.cognizant.supplyshop.model;

import lombok.Data;
import org.hibernate.annotations.CreationTimestamp;

import javax.persistence.*;
import java.sql.Date;
import java.util.Set;

@Entity
@Table(name = "orders")
@Data
public class Order {
    @Id @GeneratedValue private Long id;
    @OneToOne private User user;
    @Embedded private Address shippingAddress;
    private String paymentMethod;
    private double itemsPrice, shippingPrice, taxPrice, totalPrice;
    @ElementCollection
    @CollectionTable(name = "order_item", joinColumns = @JoinColumn(name = "owner_id"))
    private Set<OrderItem> orderItems;
    private boolean isPaid, isDelivered;
    @CreationTimestamp private Date created_at;
}
