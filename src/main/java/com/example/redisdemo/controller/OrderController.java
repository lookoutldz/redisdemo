package com.example.redisdemo.controller;

import com.example.redisdemo.entity.Order;
import com.example.redisdemo.entity.Product;
import org.springframework.web.bind.annotation.*;
import redis.clients.jedis.Jedis;

import java.time.LocalDateTime;
import java.util.UUID;

@RestController
@RequestMapping("/order")
public class OrderController {

    private static final String host = "lookoutldz.top";
    private static final int port = 6379;
    private static final String auth = "redis123456";
    private static final String STOCK_KEY = "stock";
    private static final long expireSeconds = 5;

    private final ThreadLocal<UUID> threadLocal = ThreadLocal.withInitial(UUID::randomUUID);

    private static final String LOCK_KEY = "lock";

    @PostMapping("/v1/{customerId}")
    public Object submitOrder(@PathVariable("customerId") Integer customerId, @RequestBody Product product) {
        try (Jedis jedis = new Jedis(host, port)) {
            jedis.auth(auth);

            return operation(jedis, customerId, product);

        } catch (Exception e) {
            System.out.println(e.getMessage());
            return e.getMessage();
        }
    }

    @PostMapping("/v2/{customerId}")
    public Object submitOrder2(@PathVariable("customerId") Integer customerId, @RequestBody Product product) {
        try (Jedis jedis = new Jedis(host, port)) {
            jedis.auth(auth);

            // 拿锁后拿库存，效验
            UUID uuid = threadLocal.get();
            long i = 0;
            long hasLocked = jedis.setnx(LOCK_KEY, uuid.toString());
            jedis.expire(LOCK_KEY, expireSeconds);

            while (hasLocked == 0) {
                Thread.sleep(10);
                hasLocked = jedis.setnx(LOCK_KEY, uuid.toString());
                if (hasLocked == 1) {
                    Order order = operation(jedis, customerId, product);
                    jedis.del(LOCK_KEY);
                    return order;
                }
                i++;
                if (i > 10000) {
                    break;
                }
            }

            return null;

        } catch (Exception e) {
            System.out.println(e.getMessage());
            return e.getMessage();
        }
    }

    private synchronized Order operation(Jedis jedis, Integer customerId, Product product) {



        int stock = Integer.parseInt(jedis.get(STOCK_KEY));
        if (stock < 1) {
            throw new RuntimeException("商品已抢光~");
        }
        // 减库存，生成订单，写库存
        --stock;
        Order order = new Order();
        order.setCustomerId(customerId);
        order.setProductId(product.getId());
        order.setPrice(product.getPrice());
        order.setAmount(product.getAmount());
        order.setNum(product.getNum());
        order.setCreateTime(LocalDateTime.now());
        order.setUpdateTime(LocalDateTime.now());
        // todo persist order ...
        jedis.set(STOCK_KEY, String.valueOf(stock));



        return order;
    }
}
