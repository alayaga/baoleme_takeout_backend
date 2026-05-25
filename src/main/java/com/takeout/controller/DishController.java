package com.takeout.controller;

import com.takeout.entity.Dish;
import com.takeout.service.DishService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/dishes")
public class DishController {

    @Autowired
    private DishService dishService;

    @GetMapping
    public List<Dish> getAll() {
        return dishService.findAll();
    }

    @PostMapping
    public Dish create(@RequestBody Dish dish) {
        return dishService.create(dish);
    }

    @PutMapping("/{id}")
    public Dish update(@PathVariable String id, @RequestBody Dish dish) {
        return dishService.update(id, dish);
    }

    @DeleteMapping("/{id}")
    public Map<String, String> delete(@PathVariable String id) {
        dishService.delete(id);
        Map<String, String> result = new HashMap<>();
        result.put("message", "删除成功");
        return result;
    }
}
