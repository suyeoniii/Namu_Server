package com.example.demo.src;

import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/redis")
public class RedisController {
/*
    private final RedisTemplate<String, String> redisTemplate;

    public RedisController(RedisTemplate<String, String> redisTemplate) {
        this.redisTemplate = redisTemplate;
    }

    @ApiOperation(value = "redis key 등록")
    @PostMapping("")
    public ResponseEntity<?> addRedisKey(@ApiParam(value = "key", defaultValue = "") @RequestParam String key,
                                         @ApiParam(value = "value", defaultValue = "") @RequestParam String value) {
        ValueOperations<String, String> vop = redisTemplate.opsForValue();

        vop.set(key, value);
    }

    @ApiOperation(value = "redis value 조회")
    @GetMapping("/{key}")
    public ResponseEntity<?> getRedisKey(@ApiParam(value = "key", defaultValue = "") @PathVariable String key) {
        ValueOperations<String, String> vop = redisTemplate.opsForValue();

        return vop.get(key);
    }*/
}