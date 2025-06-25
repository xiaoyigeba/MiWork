package com.example.finalwork.service.impl; // 注意包名通常会有一个 impl 子包

import com.example.finalwork.mapper.CarSignalMapper;
import com.example.finalwork.entity.CarSignal;
import com.example.finalwork.service.CarSignalService; // 引入接口
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;

@Service // 标识这是一个 Spring Service 组件
public class CarSignalServiceImpl implements CarSignalService { // 实现 CarSignalService 接口

    private final CarSignalMapper carSignalMapper;

    @Autowired // 注入 CarSignalMapper
    public CarSignalServiceImpl(CarSignalMapper carSignalMapper) {
        this.carSignalMapper = carSignalMapper;
    }

    /**
     * 上报/添加汽车信号数据
     * @param carSignal 汽车信号实体
     * @return 插入成功的记录数
     */
    @Override // 标记这是接口方法的实现
    public int addCarSignal(CarSignal carSignal) {
        // 可以在这里添加业务逻辑，例如数据校验、默认值设置等
        if (carSignal.getTime() == null) {
            carSignal.setTime(LocalDateTime.now()); // 如果时间未提供，则设置为当前时间
        }
        return carSignalMapper.insertCarSignal(carSignal);
    }

    /**
     * 根据 sid 查询汽车信号
     * @param sid 信号ID/规则编号
     * @return 汽车信号实体
     */
    @Override
    public CarSignal getCarSignalBySid(Integer sid) {
        return carSignalMapper.selectCarSignalBySid(sid);
    }

    /**
     * 查询所有汽车信号
     * @return 汽车信号列表
     */
    @Override
    public List<CarSignal> getAllCarSignals() {
        return carSignalMapper.selectAllCarSignals();
    }

    /**
     * 根据车架编号查询汽车信号
     * @param frameNumber 车架编号
     * @return 汽车信号列表
     */
    @Override
    public List<CarSignal> getCarSignalsByFrameNumber(Integer frameNumber) {
        return carSignalMapper.selectCarSignalsByFrameNumber(frameNumber);
    }

    /**
     * 更新汽车信号数据
     * @param carSignal 待更新的汽车信号实体
     * @return 更新成功的记录数
     */
    @Override
    public int updateCarSignal(CarSignal carSignal) {
        // 可以在这里添加数据校验，确保 sid 存在等
        return carSignalMapper.updateCarSignal(carSignal);
    }

    /**
     * 删除汽车信号数据
     * @param sid 信号ID/规则编号
     * @return 删除成功的记录数
     */
    @Override
    public int deleteCarSignal(Integer sid) {
        return carSignalMapper.deleteCarSignalBySid(sid);
    }
}