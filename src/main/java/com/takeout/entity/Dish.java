package com.takeout.entity;

import lombok.Data;
import java.math.BigDecimal;

@Data
public class Dish {
    private String id;
    private String name;
    private BigDecimal price;
    private String description;
    private String image;
    private String category;
    private Integer sales;
    private Integer stock;
    private String status;
}
