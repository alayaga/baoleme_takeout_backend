package com.takeout.service.impl;

import com.takeout.entity.Dish;
import com.takeout.mapper.DishMapper;
import com.takeout.service.DishService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.UUID;

@Service
public class DishServiceImpl implements DishService {

    @Autowired
    private DishMapper dishMapper;

    @Override
    public List<Dish> findAll() {
        return dishMapper.findAll();
    }

    @Override
    public Dish findById(String id) {
        return dishMapper.findById(id);
    }

    @Override
    public Dish create(Dish dish) {
        if (dish.getId() == null || dish.getId().isEmpty()) {
            dish.setId("d_" + UUID.randomUUID().toString().substring(0, 8));
        }
        if (dish.getSales() == null) dish.setSales(0);
        if (dish.getStock() == null) dish.setStock(99);
        if (dish.getStatus() == null) dish.setStatus("active");
        dishMapper.insert(dish);
        return dish;
    }

    @Override
    public Dish update(String id, Dish dish) {
        dish.setId(id);
        dishMapper.update(dish);
        return dishMapper.findById(id);
    }

    @Override
    public void delete(String id) {
        dishMapper.deleteById(id);
    }
}
