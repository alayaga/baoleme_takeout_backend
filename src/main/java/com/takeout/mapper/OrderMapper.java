package com.takeout.mapper;

import com.takeout.entity.Order;
import org.apache.ibatis.annotations.Param;
import java.util.List;

public interface OrderMapper {
    List<Order> findAll();
    Order findById(@Param("id") String id);
    int insert(Order order);
    int updateStatus(@Param("id") String id, @Param("status") String status);
}
