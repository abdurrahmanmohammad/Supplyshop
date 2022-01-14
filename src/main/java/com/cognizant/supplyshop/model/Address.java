package com.cognizant.supplyshop.model;

import lombok.Data;

import javax.persistence.Embeddable;

@Embeddable
@Data
public class Address {
    String address, city, province;
    int postalCode;
}
