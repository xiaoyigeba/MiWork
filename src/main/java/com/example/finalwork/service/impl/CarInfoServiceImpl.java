package com.example.finalwork.service.impl;

import com.example.finalwork.entity.CarInfo;
import com.example.finalwork.mapper.CarInfoMapper;
import com.example.finalwork.service.CarInfoService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class CarInfoServiceImpl implements CarInfoService {

    private final CarInfoMapper carInfoMapper;

    @Autowired
    public CarInfoServiceImpl(CarInfoMapper carInfoMapper) {
        this.carInfoMapper = carInfoMapper;
    }

    @Override
    public CarInfo getCarInfoByFrameNumber(Integer frameNumber) {
        return carInfoMapper.findByFrameNumber(frameNumber);
    }

    @Override
    public String getBatteryTypeByFrameNumber(Integer frameNumber) {
        return carInfoMapper.findBatteryTypeByFrameNumber(frameNumber);
    }
}