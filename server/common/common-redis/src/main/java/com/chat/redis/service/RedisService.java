package com.chat.redis.service;

import com.alibaba.fastjson2.JSON;
import jakarta.annotation.Resource;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.core.ValueOperations;
import org.springframework.stereotype.Component;
import org.springframework.util.CollectionUtils;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.Map;
import java.util.concurrent.TimeUnit;

// redis服务类
@Component
public class RedisService {
    @Resource
    public RedisTemplate redisTemplate;

    // 判断key是否存在
    public Boolean isKey(String key) {
        return redisTemplate.hasKey(key);
    }

    // 设置过期时间
    public boolean expire(final String key, final long timeout) { // 无单位
        return expire(key, timeout, TimeUnit.SECONDS);
    }

    public boolean expire(final String key, final long timeout, final TimeUnit unit) { // 指定单位
        return expire(key, timeout, unit);
    }

    // 获取剩余有效时间
    public Long getExpire(final String key, final TimeUnit unit) {
        return redisTemplate.getExpire(key, unit);
    }

    // 删除一个对象
    public boolean delete(final String key) {
        return redisTemplate.delete(key);
    }

    // 设置一个KV
    public <T> void set(final String key, final T value) {
        redisTemplate.opsForValue().set(key, value);
    }

    // 设置一个KV并带有过期时间
    public <T> void set(final String key, final T value, final Long timeout, final TimeUnit timeUnit) {
        redisTemplate.opsForValue().set(key, value, timeout, timeUnit);
    }

    // 获取一个对象
    public <T> T get(final String key, Class<T> clazz) {
        ValueOperations<String, T> operation = redisTemplate.opsForValue();
        T t = operation.get(key);
        if (t instanceof String) {
            return t;
        }

        // 返回反序列化后的原始数据
        return JSON.parseObject(String.valueOf(t), clazz);
    }

    // 获取list类型对象的size
    public Long getListSize(final String key) {
        return redisTemplate.opsForList().size(key);
    }

    // 获取list对象中指定范围数据
    public <T> List<T> getListByRange(final String key, long start, long end, Class<T> clazz) {
        List range = redisTemplate.opsForList().range(key, start, end);
        if (CollectionUtils.isEmpty(range)) {
            return null;
        }

        // 返回反序列化后的原始数据
        return JSON.parseArray(JSON.toJSONString(range), clazz);
    }

    // 尾插list
    public <T> Long rightPushAll(final String key, Collection<T> list) {
        return redisTemplate.opsForList().rightPushAll(key, list);
    }

    // 头插list
    public <T> Long leftPushForList(final String key, T value) {
        return redisTemplate.opsForList().leftPush(key, value);
    }

    // 删除list中的指定值
    public <T> Long removeForList(final String key, T value) {
        return redisTemplate.opsForList().remove(key, 1L, value);
    }

    // 获取key中指定hash的值
    public <T> T getMapValue(final String key, final String hKey, Class<T> clazz) {
        Object cacheMapValue = redisTemplate.opsForHash().get(key, hKey);
        if (cacheMapValue != null) {
            return JSON.parseObject(String.valueOf(cacheMapValue), clazz);
        }
        return null;
    }

    // 获取key中多个hash的值
    public <T> List<T> getMultiMapValue(final String key, final
    Collection<String> hKeys, Class<T> clazz) {
        List list = redisTemplate.opsForHash().multiGet(key, hKeys);

        List<T> result = new ArrayList<>();
        for (Object item : list) {
            result.add(JSON.parseObject(JSON.toJSONString(item), clazz));
        }

        return result;
    }

    // 往key中的hash插入值(修改)
    public <T> void setMapValue(final String key, final String hKey, final T value) {
        redisTemplate.opsForHash().put(key, hKey, value);
    }

    // 往key中插入hash
    public <K, T> void setMap(final String key, final Map<K, T> dataMap) {
        if (dataMap != null) {
            redisTemplate.opsForHash().putAll(key, dataMap);
        }
    }

    // 删除key中的hash
    public Long deleteMapValue(final String key, final String hKey) {
        return redisTemplate.opsForHash().delete(key, hKey);
    }
}
