package com.example.finalwork.entity;

import java.time.LocalDateTime;

public class CarSignal {
    // 原始SQL中 sid 既是主键又是“规则编号”。
    // 在实体类中，通常会有一个唯一的ID作为主键，
    // 如果 sid 确实意指“规则编号”，那么可能需要一个独立的 ID 字段。
    // 这里我们先按照SQL定义来，将其作为主键，并保留其“规则编号”的语义。
    private Integer sid; // 规则编号，INT，主键
    private Integer frameNumber; // 车架编号，INT
    private Float mx; // 最大电压，FLOAT
    private Float mi; // 最小电压，FLOAT
    private Float ix; // 最大电流，FLOAT
    private Float ii; // 最小电流，FLOAT
    private LocalDateTime time; // 时间，DATETIME
}