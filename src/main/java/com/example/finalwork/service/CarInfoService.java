package com.example.finalwork.service;

import com.example.finalwork.entity.CarInfo;

public interface CarInfoService {
    CarInfo getCarInfoByFrameNumber(Integer frameNumber);
    String getBatteryTypeByFrameNumber(Integer frameNumber);
}