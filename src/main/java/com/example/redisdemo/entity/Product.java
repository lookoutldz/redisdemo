package com.example.redisdemo.entity;

import lombok.Data;

import java.math.BigDecimal;

@Data
public class Product extends BaseEntity {
    private String name;
    private Integer num;
    private BigDecimal price;
    private BigDecimal amount;
}
