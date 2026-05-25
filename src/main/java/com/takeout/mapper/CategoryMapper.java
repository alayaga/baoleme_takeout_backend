package com.takeout.mapper;

import com.takeout.entity.Category;
import org.apache.ibatis.annotations.Param;
import java.util.List;

public interface CategoryMapper {
    List<Category> findAll();
    int insert(Category category);
    Category findById(@Param("id") String id);
}
