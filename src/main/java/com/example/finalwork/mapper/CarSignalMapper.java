package com.example.finalwork.mapper;


import com.example.finalwork.entity.CarSignal;
import org.apache.ibatis.annotations.*;

import java.time.LocalDateTime;
import java.util.List;

@Mapper // 标识这是一个 MyBatis Mapper 接口
public interface CarSignalMapper {

    /**
     * 插入一条汽车信号数据
     */
    @Insert("INSERT INTO car_signal (frame_number, Mx, Mi, Ix, Ii, time) " +
            "VALUES (#{frameNumber}, #{mx}, #{mi}, #{ix}, #{ii}, #{time})")
    @Options(useGeneratedKeys = true, keyProperty = "sid", keyColumn = "sid")
    int insertCarSignal(CarSignal carSignal);

    /**
     * 根据主键 sid 查询汽车信号数据
     */
    @Select("SELECT sid, frame_number, Mx, Mi, Ix, Ii, time FROM car_signal WHERE sid = #{sid}")
    CarSignal selectCarSignalBySid(Integer sid);

    /**
     * 查询所有汽车信号数据
     */
    @Select("SELECT sid, frame_number, Mx, Mi, Ix, Ii, time FROM car_signal")
    List<CarSignal> selectAllCarSignals();

    /**
     * 根据主键 sid 更新汽车信号数据
     */
    @Update("UPDATE car_signal SET frame_number = #{frameNumber}, Mx = #{mx}, Mi = #{mi}, " +
            "Ix = #{ix}, Ii = #{ii}, time = #{time} WHERE sid = #{sid}")
    int updateCarSignal(CarSignal carSignal);

    /**
     * 根据车架编号查询汽车信号数据
     */
    @Select("SELECT sid, frame_number, Mx, Mi, Ix, Ii, time FROM car_signal WHERE frame_number = #{frameNumber}")
    List<CarSignal> selectCarSignalsByFrameNumber(Integer frameNumber);

    /**
     * 根据车架编号删除汽车信号数据（如果业务允许批量删除）
     */
    @Delete("DELETE FROM car_signal WHERE frame_number = #{frameNumber}")
    int deleteCarSignalsByFrameNumber(Integer frameNumber);
    /**
     * 根据主键 sid 删除汽车信号数据
     */
    @Delete("DELETE FROM car_signal WHERE sid = #{sid}")
    int deleteCarSignalBySid(Integer sid);


}
