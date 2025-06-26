package com.example.finalwork.service;

import com.example.finalwork.entity.CarSignal;

import java.util.List;

public interface CarSignalService {

    /**
     * 上报/添加汽车信号数据
     *
     * @param carSignal 汽车信号实体
     * @return 插入成功的记录数
     */
    int addCarSignal(CarSignal carSignal);

    /**
     * 根据 sid 查询汽车信号
     *
     * @param sid 信号ID/规则编号
     * @return 汽车信号实体
     */
    CarSignal getCarSignalBySid(Integer sid);

    /**
     * 查询所有汽车信号
     *
     * @return 汽车信号列表
     */
    List<CarSignal> getAllCarSignals();

    /**
     * 根据车架编号查询汽车信号
     *
     * @param frameNumber 车架编号
     * @return 汽车信号列表
     */
    List<CarSignal> getCarSignalsByFrameNumber(Integer frameNumber);

    /**
     * 更新汽车信号数据
     *
     * @param carSignal 待更新的汽车信号实体
     * @return 更新成功的记录数
     */
    int updateCarSignal(CarSignal carSignal);

    /**
     * 删除汽车信号数据
     *
     * @param sid 信号ID/规则编号
     * @return 删除成功的记录数
     */
    int deleteCarSignal(Integer sid);
}