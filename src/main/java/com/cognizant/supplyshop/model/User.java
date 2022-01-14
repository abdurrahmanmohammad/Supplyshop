package com.cognizant.supplyshop.model;

import lombok.Data;

import javax.persistence.*;

@Entity
@Table
@Data
public class User {
    @Id @GeneratedValue private Long id;
    private String name;
    private String email;
    private String password;
    private Role role;
    private boolean active;
}