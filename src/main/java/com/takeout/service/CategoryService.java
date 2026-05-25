package com.takeout.service;

import com.takeout.entity.Category;
import java.util.List;

public interface CategoryService {
    List<Category> findAll();
    Category create(Category category);
}
