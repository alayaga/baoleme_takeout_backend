package com.takeout.controller;

import com.takeout.entity.Category;
import com.takeout.service.CategoryService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api")
public class CategoryController {

    @Autowired
    private CategoryService categoryService;

    @GetMapping("/categories")
    public List<Category> getAll() {
        return categoryService.findAll();
    }

    @PostMapping("/categories")
    public Category create(@RequestBody Category category) {
        return categoryService.create(category);
    }
}
