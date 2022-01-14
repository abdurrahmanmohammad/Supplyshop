package com.cognizant.supplyshop.model;

import lombok.Data;

@Data
public class CustomJWT {
    String sub;
    Long exp, iat;
}
