package com.cognizant.supplyshop.model;

import lombok.Data;

import javax.persistence.Embeddable;
import javax.persistence.JoinColumn;
import javax.persistence.OneToOne;

@Embeddable
@Data
public class OrderItem {
    @OneToOne
    @JoinColumn(name = "product")
    private Product product;
    private int quantity, price;
    private String name;
}
