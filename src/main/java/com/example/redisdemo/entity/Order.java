package com.example.redisdemo.entity;

import lombok.Data;

import java.math.BigDecimal;

@Data
public class Order extends BaseEntity {
    private Integer customerId;
    private Integer productId;
    private Integer num;
    private BigDecimal price;
    private BigDecimal amount;
}
