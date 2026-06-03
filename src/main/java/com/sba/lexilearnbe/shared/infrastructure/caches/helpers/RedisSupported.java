package com.sba.lexilearnbe.shared.infrastructure.caches.helpers;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.stereotype.Component;

import java.util.Set;
import java.util.concurrent.TimeUnit;

@Slf4j
@Component
@RequiredArgsConstructor
public class RedisSupported {

    private final StringRedisTemplate redisTemplate; // ← đổi thành StringRedisTemplate

    // ── String operations ──────────────────────────────────

    public void set(String key, String value, long ttlSeconds) {
        redisTemplate.opsForValue().set(key, value, ttlSeconds, TimeUnit.SECONDS);
    }

    public String get(String key) {
        return redisTemplate.opsForValue().get(key); // trả thẳng String, không cần cast
    }

    // ── Set operations ─────────────────────────────────────

    public void setSet(String key, Set<String> values, long ttlSeconds) {
        redisTemplate.delete(key);
        if (!values.isEmpty()) {
            redisTemplate.opsForSet().add(key, values.toArray(new String[0]));
            redisTemplate.expire(key, ttlSeconds, TimeUnit.SECONDS);
        }
    }

    public Set<String> getSet(String key) { // ← trả Set<String> thay vì Set<Object>
        Set<String> members = redisTemplate.opsForSet().members(key);
        if (members == null || members.isEmpty()) return null;
        return members;
    }

    // ── Counter operations ─────────────────────────────────

    public long increment(String key, long ttlSeconds) {
        Long count = redisTemplate.opsForValue().increment(key);
        if (count != null && count == 1) {
            redisTemplate.expire(key, ttlSeconds, TimeUnit.SECONDS);
        }
        return count != null ? count : 0L;
    }

    public long getCounter(String key) {
        String value = redisTemplate.opsForValue().get(key);
        if (value == null) return 0L;
        return Long.parseLong(value);
    }

    // ── Common operations ──────────────────────────────────

    public void delete(String key) {
        redisTemplate.delete(key);
    }

    public boolean exists(String key) {
        return Boolean.TRUE.equals(redisTemplate.hasKey(key));
    }

    public long getTtl(String key) {
        Long ttl = redisTemplate.getExpire(key, TimeUnit.SECONDS);
        return ttl != null ? ttl : -2L;
    }

    public void extendTtl(String key, long ttlSeconds) {
        redisTemplate.expire(key, ttlSeconds, TimeUnit.SECONDS);
    }
}
