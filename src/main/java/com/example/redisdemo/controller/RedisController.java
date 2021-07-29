package com.example.redisdemo.controller;

import org.springframework.web.bind.annotation.*;
import redis.clients.jedis.Jedis;

@RestController
@RequestMapping("/redis")
public class RedisController {

    private static final String host = "lookoutldz.top";
    private static final int port = 6379;
    private static final String auth = "redis123456";


    @GetMapping("/{key}")
    public Object getEntry(@PathVariable("key") String key) {
        try (Jedis jedis = new Jedis(host, port)) {
            jedis.auth(auth);
            return jedis.get(key);
        }
    }

    @PostMapping("/{key}")
    public Object setEntry(@PathVariable("key") String key, @RequestBody String value) {
        try (Jedis jedis = new Jedis(host, port)) {
            jedis.auth(auth);
            return jedis.setnx(key, value);
        }
    }
}
