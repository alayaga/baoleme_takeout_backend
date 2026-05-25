package com.takeout.entity;

import lombok.Data;
import java.math.BigDecimal;

@Data
public class OrderItem {
    private Long id;
    private String orderId;
    private String dishId;
    private String dishName;
    private BigDecimal price;
    private Integer quantity;
}
