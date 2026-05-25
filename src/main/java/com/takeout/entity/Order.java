package com.takeout.entity;

import lombok.Data;
import java.math.BigDecimal;
import java.util.List;

@Data
public class Order {
    private String id;
    private String userId;
    private BigDecimal totalPrice;
    private String status;
    private String createdAt;
    private String address;
    private String phone;
    private String note;
    private List<OrderItem> items;
}
