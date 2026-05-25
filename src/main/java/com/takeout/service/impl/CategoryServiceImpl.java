package com.takeout.service.impl;

import com.takeout.entity.Category;
import com.takeout.mapper.CategoryMapper;
import com.takeout.service.CategoryService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.UUID;

@Service
public class CategoryServiceImpl implements CategoryService {

    @Autowired
    private CategoryMapper categoryMapper;

    @Override
    public List<Category> findAll() {
        return categoryMapper.findAll();
    }

    @Override
    public Category create(Category category) {
        if (category.getId() == null || category.getId().isEmpty()) {
            category.setId(UUID.randomUUID().toString().substring(0, 8));
        }
        if (category.getIcon() == null) {
            category.setIcon("Utensils");
        }
        categoryMapper.insert(category);
        return category;
    }
}
