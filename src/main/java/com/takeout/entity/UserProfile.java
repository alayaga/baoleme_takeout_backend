package com.takeout.entity;

import lombok.Data;

@Data
public class UserProfile {
    private String id;
    private String name;
    private String tags;
    private Integer historyOrdersCount;
    private String favoriteCategory;
}
