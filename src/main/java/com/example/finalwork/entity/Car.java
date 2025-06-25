package com.example.finalwork.entity;

import lombok.Data;

@Data
public class Car {
    private String vid; // 车辆识别码，VARCHAR(16)，主键
    private Integer frameNumber; // 车架编号，INT
    private String batteryType; // 电池类型，VARCHAR(20)
    private Integer totalMileage; // 总里程(km)，INT
    private Integer batteryHealth; // 电池健康状态(%)，INT
}
