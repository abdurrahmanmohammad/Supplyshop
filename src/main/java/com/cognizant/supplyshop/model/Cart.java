package com.cognizant.supplyshop.model;

import lombok.Data;

import javax.persistence.*;
import java.util.Set;

@Entity
@Table
@Data
public class Cart {
    @Id @GeneratedValue private Long id;
    @OneToOne private User user;
    @ElementCollection
    @CollectionTable(name = "order_item", joinColumns = @JoinColumn(name = "owner_id"))
    private Set<CartItem> cartItem;
}