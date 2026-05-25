package com.takeout.mapper;

import com.takeout.entity.OrderItem;
import org.apache.ibatis.annotations.Param;
import java.util.List;

public interface OrderItemMapper {
    int insert(OrderItem item);
    int batchInsert(@Param("list") List<OrderItem> items);
    List<OrderItem> findByOrderId(@Param("orderId") String orderId);
}
