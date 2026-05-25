package com.takeout.mapper;

import com.takeout.entity.Dish;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Update;
import java.util.List;

public interface DishMapper {
    List<Dish> findAll();
    Dish findById(@Param("id") String id);
    int insert(Dish dish);
    int update(Dish dish);
    int deleteById(@Param("id") String id);

    @Update("UPDATE dish SET stock = stock - #{qty}, sales = sales + #{qty} " +
            "WHERE id = #{dishId} AND stock >= #{qty}")
    int updateStockAndSales(@Param("dishId") String dishId, @Param("qty") int qty);
}
