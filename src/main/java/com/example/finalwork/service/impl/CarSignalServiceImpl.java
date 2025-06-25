package com.example.finalwork.service.impl;

import com.example.finalwork.mapper.CarSignalMapper;
import com.example.finalwork.entity.CarSignal;
import com.example.finalwork.service.CarSignalService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.RedisTemplate; // 引入 RedisTemplate
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;
import java.util.concurrent.TimeUnit; // 引入 TimeUnit

@Service
public class CarSignalServiceImpl implements CarSignalService {

    private final CarSignalMapper carSignalMapper;
    private final RedisTemplate<String, Object> redisTemplate; // 注入 RedisTemplate

    // 定义缓存 Key 的前缀
    private static final String CACHE_KEY_PREFIX_SINGLE_SIGNAL = "car_signal:sid:";
    private static final String CACHE_KEY_PREFIX_ALL_SIGNALS = "car_signal:all";
    private static final String CACHE_KEY_PREFIX_BY_FRAMENUMBER = "car_signal:frame:";

    @Autowired
    public CarSignalServiceImpl(CarSignalMapper carSignalMapper, RedisTemplate<String, Object> redisTemplate) {
        this.carSignalMapper = carSignalMapper;
        this.redisTemplate = redisTemplate;
    }

    @Override
    public int addCarSignal(CarSignal carSignal) {
        if (carSignal.getTime() == null) {
            carSignal.setTime(LocalDateTime.now());
        }
        int result = carSignalMapper.insertCarSignal(carSignal);
        // 增操作后，清除相关缓存，确保数据一致性
        if (result > 0) {
            // 清除所有信号的缓存
            redisTemplate.delete(CACHE_KEY_PREFIX_ALL_SIGNALS);
            // 清除按车架号查询的缓存
            redisTemplate.delete(CACHE_KEY_PREFIX_BY_FRAMENUMBER + carSignal.getFrameNumber());
            // 清除单条信号的缓存（如果 sid 已生成）
            if (carSignal.getSid() != null) {
                redisTemplate.delete(CACHE_KEY_PREFIX_SINGLE_SIGNAL + carSignal.getSid());
            }
        }
        return result;
    }

    @Override
    public CarSignal getCarSignalBySid(Integer sid) {
        String cacheKey = CACHE_KEY_PREFIX_SINGLE_SIGNAL + sid;
        // 1. 尝试从缓存中获取
        CarSignal carSignal = (CarSignal) redisTemplate.opsForValue().get(cacheKey);

        if (carSignal != null) {
            System.out.println("从 Redis 缓存获取信号: " + sid);
            return carSignal;
        }

        // 2. 缓存未命中，从数据库查询
        System.out.println("缓存未命中，从数据库查询信号: " + sid);
        carSignal = carSignalMapper.selectCarSignalBySid(sid);

        // 3. 将数据存入缓存 (设置过期时间，例如 5 分钟)
        if (carSignal != null) {
            redisTemplate.opsForValue().set(cacheKey, carSignal, 5, TimeUnit.MINUTES);
        }
        return carSignal;
    }

    @Override
    public List<CarSignal> getAllCarSignals() {
        String cacheKey = CACHE_KEY_PREFIX_ALL_SIGNALS;
        // 1. 尝试从缓存中获取
        // 注意：这里需要处理 List 的反序列化，RedisTemplate 默认可能返回 List<Object>
        // 或者需要自定义 List 的序列化器
        List<CarSignal> carSignals = (List<CarSignal>) redisTemplate.opsForValue().get(cacheKey);

        if (carSignals != null && !carSignals.isEmpty()) {
            System.out.println("从 Redis 缓存获取所有信号");
            return carSignals;
        }

        // 2. 缓存未命中，从数据库查询
        System.out.println("缓存未命中，从数据库查询所有信号");
        carSignals = carSignalMapper.selectAllCarSignals();

        // 3. 将数据存入缓存 (设置过期时间)
        if (carSignals != null && !carSignals.isEmpty()) {
            redisTemplate.opsForValue().set(cacheKey, carSignals, 5, TimeUnit.MINUTES);
        }
        return carSignals;
    }

    @Override
    public List<CarSignal> getCarSignalsByFrameNumber(Integer frameNumber) {
        String cacheKey = CACHE_KEY_PREFIX_BY_FRAMENUMBER + frameNumber;
        // 1. 尝试从缓存中获取
        List<CarSignal> carSignals = (List<CarSignal>) redisTemplate.opsForValue().get(cacheKey);

        if (carSignals != null && !carSignals.isEmpty()) {
            System.out.println("从 Redis 缓存获取车架号 " + frameNumber + " 的信号");
            return carSignals;
        }

        // 2. 缓存未命中，从数据库查询
        System.out.println("缓存未命中，从数据库查询车架号 " + frameNumber + " 的信号");
        carSignals = carSignalMapper.selectCarSignalsByFrameNumber(frameNumber);

        // 3. 将数据存入缓存 (设置过期时间)
        if (carSignals != null && !carSignals.isEmpty()) {
            redisTemplate.opsForValue().set(cacheKey, carSignals, 5, TimeUnit.MINUTES);
        }
        return carSignals;
    }

    @Override
    public int updateCarSignal(CarSignal carSignal) {
        int result = carSignalMapper.updateCarSignal(carSignal);
        // 改操作后，清除相关缓存，确保数据一致性
        if (result > 0) {
            redisTemplate.delete(CACHE_KEY_PREFIX_ALL_SIGNALS);
            redisTemplate.delete(CACHE_KEY_PREFIX_BY_FRAMENUMBER + carSignal.getFrameNumber());
            redisTemplate.delete(CACHE_KEY_PREFIX_SINGLE_SIGNAL + carSignal.getSid());
        }
        return result;
    }

    @Override
    public int deleteCarSignal(Integer sid) {
        // 先获取数据以便删除相关缓存，或者在删除前确定要清除哪些缓存
        CarSignal carSignalToDelete = carSignalMapper.selectCarSignalBySid(sid);
        int result = carSignalMapper.deleteCarSignalBySid(sid);
        // 删操作后，清除相关缓存，确保数据一致性
        if (result > 0 && carSignalToDelete != null) {
            redisTemplate.delete(CACHE_KEY_PREFIX_ALL_SIGNALS);
            redisTemplate.delete(CACHE_KEY_PREFIX_BY_FRAMENUMBER + carSignalToDelete.getFrameNumber());
            redisTemplate.delete(CACHE_KEY_PREFIX_SINGLE_SIGNAL + sid);
        }
        return result;
    }
}