package com.example.finalwork.entity;

import lombok.Data;

import java.time.LocalDateTime;
@Data
public class CarSignal {

    private Integer sid; // INT，自增主键
    private Integer frameNumber; // 车架编号，INT
    private Float mx; // 最大电压，FLOAT
    private Float mi; // 最小电压，FLOAT
    private Float ix; // 最大电流，FLOAT
    private Float ii; // 最小电流，FLOAT
    private LocalDateTime time; // 时间，DATETIME
}