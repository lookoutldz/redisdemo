package com.example.redisdemo.controller;

import com.example.redisdemo.entity.Order;
import com.example.redisdemo.entity.Product;
import org.springframework.web.bind.annotation.*;
import redis.clients.jedis.Jedis;

import java.time.LocalDateTime;

@RestController
@RequestMapping("/order")
public class OrderController {

    private static final String host = "lookoutldz.top";
    private static final int port = 6379;
    private static final String auth = "redis123456";

    @PostMapping("/{customerId}")
    public Object submitOrder(@PathVariable("customerId") Integer customerId, @RequestBody Product product) {
        try (Jedis jedis = new Jedis(host, port)) {
            jedis.auth(auth);

            Integer stock = Integer.valueOf(jedis.get("stock"));
            if (stock < 1) {
                throw new RuntimeException("商品已抢光~");
            }
            stock--;

            Order order = new Order();
            order.setCustomerId(customerId);
            order.setProductId(product.getId());
            order.setPrice(product.getPrice());
            order.setAmount(product.getAmount());
            order.setNum(product.getNum());
            order.setCreateTime(LocalDateTime.now());
            order.setUpdateTime(LocalDateTime.now());
            // todo persist order

            return order;

        } catch (Exception e) {
            System.out.println(e.getMessage());
            return e.getMessage();
        }
    }
}
