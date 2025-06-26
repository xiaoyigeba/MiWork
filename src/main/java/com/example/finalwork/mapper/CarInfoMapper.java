package com.example.finalwork.mapper;

import com.example.finalwork.entity.CarInfo;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Select;

//@Mapper
//public interface CarInfoMapper {
//    @Select("SELECT vid, frame_number, battery_type, total_mileage, battery_health_status FROM vehicle_info WHERE frame_number = #{frameNumber}")
//    CarInfo findByFrameNumber(Integer frameNumber);
//
//    @Select("SELECT battery_type FROM vehicle_info WHERE frame_number = #{frameNumber}")
//    String findBatteryTypeByFrameNumber(Integer frameNumber);
//}
@Mapper
public interface CarInfoMapper {
    @Select("SELECT " +
            "vid AS vid, " +
            "frame_number AS frameNumber, " +
            "battery_type AS batteryType, " +
            "total_mileage AS totalMileage, " +
            "battery_health_status AS batteryHealthStatus " +
            "FROM vehicle_info WHERE frame_number = #{frameNumber}")
    CarInfo findByFrameNumber(Integer frameNumber);

    @Select("SELECT battery_type AS batteryType FROM vehicle_info WHERE frame_number = #{frameNumber}")
    String findBatteryTypeByFrameNumber(Integer frameNumber);
}