package com.takeout.service.impl;

import com.takeout.entity.Order;
import com.takeout.entity.OrderItem;
import com.takeout.mapper.DishMapper;
import com.takeout.mapper.OrderItemMapper;
import com.takeout.mapper.OrderMapper;
import com.takeout.service.OrderService;
import com.takeout.websocket.BaolemeWebSocketServer;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.time.Instant;
import java.util.*;

@Service
public class OrderServiceImpl implements OrderService {

    @Autowired
    private OrderMapper orderMapper;
    @Autowired
    private OrderItemMapper orderItemMapper;
    @Autowired
    private DishMapper dishMapper;

    @Override
    public List<Map<String, Object>> findAll() {
        List<Order> orders = orderMapper.findAll();
        List<Map<String, Object>> result = new ArrayList<>();
        for (Order order : orders) {
            result.add(buildOrderResponse(order));
        }
        return result;
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public Order create(Map<String, Object> orderData) {
        Order order = new Order();
        String orderId = "ord_" + System.currentTimeMillis();
        order.setId(orderId);
        order.setUserId((String) orderData.get("userId"));
        order.setTotalPrice(new BigDecimal(orderData.get("totalPrice").toString()));
        order.setStatus("pending");
        order.setCreatedAt(Instant.now().toString());
        order.setAddress((String) orderData.get("address"));
        order.setPhone((String) orderData.get("phone"));
        order.setNote(orderData.get("note") != null ? (String) orderData.get("note") : "");

        @SuppressWarnings("unchecked")
        List<Map<String, Object>> items = (List<Map<String, Object>>) orderData.get("items");
        List<OrderItem> orderItems = new ArrayList<>();
        for (Map<String, Object> itemData : items) {
            @SuppressWarnings("unchecked")
            Map<String, Object> dishData = (Map<String, Object>) itemData.get("dish");
            String dishId = (String) dishData.get("id");
            int quantity = ((Number) itemData.get("quantity")).intValue();

            int affected = dishMapper.updateStockAndSales(dishId, quantity);
            if (affected == 0) {
                throw new RuntimeException("菜品库存不足: " + dishData.get("name"));
            }

            OrderItem oi = new OrderItem();
            oi.setOrderId(orderId);
            oi.setDishId(dishId);
            oi.setDishName((String) dishData.get("name"));
            oi.setPrice(new BigDecimal(dishData.get("price").toString()));
            oi.setQuantity(quantity);
            orderItems.add(oi);
        }

        orderMapper.insert(order);
        orderItemMapper.batchInsert(orderItems);

        BaolemeWebSocketServer.broadcastToRole("merchant",
                "{\"type\":\"NEW_ORDER_PLACED\",\"payload\":{\"orderId\":\"" + orderId + "\"}}");

        order.setItems(orderItems);
        return order;
    }

    @Override
    public Order updateStatus(String orderId, String status) {
        orderMapper.updateStatus(orderId, status);
        Order order = orderMapper.findById(orderId);
        BaolemeWebSocketServer.sendToUser("client", order.getUserId(),
                "{\"type\":\"ORDER_STATUS_UPDATED\",\"payload\":{\"orderId\":\""
                + orderId + "\",\"status\":\"" + status + "\"}}");
        return order;
    }

    private Map<String, Object> buildOrderResponse(Order order) {
        Map<String, Object> map = new LinkedHashMap<>();
        map.put("id", order.getId());
        map.put("userId", order.getUserId());
        List<OrderItem> items = orderItemMapper.findByOrderId(order.getId());
        List<Map<String, Object>> itemList = new ArrayList<>();
        for (OrderItem item : items) {
            Map<String, Object> itemMap = new HashMap<>();
            Map<String, Object> dishMap = new HashMap<>();
            dishMap.put("id", item.getDishId());
            dishMap.put("name", item.getDishName());
            dishMap.put("price", item.getPrice());
            itemMap.put("dish", dishMap);
            itemMap.put("quantity", item.getQuantity());
            itemList.add(itemMap);
        }
        map.put("items", itemList);
        map.put("totalPrice", order.getTotalPrice());
        map.put("status", order.getStatus());
        map.put("createdAt", order.getCreatedAt());
        map.put("address", order.getAddress());
        map.put("phone", order.getPhone());
        map.put("note", order.getNote());
        return map;
    }
}