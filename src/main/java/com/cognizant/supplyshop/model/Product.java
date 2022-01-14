package com.cognizant.supplyshop.model;

import lombok.Data;

import javax.persistence.*;

@Entity
@Table
@Data
public class Product {
    @Id @GeneratedValue private Long id;
    private String name;
    private String brand;
    private String category;
    private double price;
    private int countInStock;
    private String imageCover;
    private String description;
}
