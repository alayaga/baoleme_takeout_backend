package com.takeout.service;

import com.takeout.entity.Dish;
import java.util.List;

public interface DishService {
    List<Dish> findAll();
    Dish findById(String id);
    Dish create(Dish dish);
    Dish update(String id, Dish dish);
    void delete(String id);
}
