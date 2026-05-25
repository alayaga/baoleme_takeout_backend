package com.takeout.controller;

import com.takeout.entity.Order;
import com.takeout.service.OrderService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/orders")
public class OrderController {

    @Autowired
    private OrderService orderService;

    @GetMapping
    public List<Map<String, Object>> getAll() {
        return orderService.findAll();
    }

    @PostMapping
    public ResponseEntity<?> create(@RequestBody Map<String, Object> orderData) {
        try {
            Order order = orderService.create(orderData);
            return ResponseEntity.ok(order);
        } catch (RuntimeException e) {
            Map<String, String> err = new HashMap<>();
            err.put("message", e.getMessage());
            return ResponseEntity.badRequest().body(err);
        }
    }

    @PutMapping("/{orderId}/status")
    public Order updateStatus(@PathVariable String orderId, @RequestBody Map<String, String> body) {
        return orderService.updateStatus(orderId, body.get("status"));
    }
}
