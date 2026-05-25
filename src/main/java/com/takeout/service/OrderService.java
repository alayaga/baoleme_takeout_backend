package com.takeout.service;

import com.takeout.entity.Order;
import java.util.List;
import java.util.Map;

public interface OrderService {
    List<Map<String, Object>> findAll();
    Order create(Map<String, Object> orderData);
    Order updateStatus(String orderId, String status);
}
