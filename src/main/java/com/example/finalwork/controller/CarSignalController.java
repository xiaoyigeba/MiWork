package com.example.finalwork.controller;

import com.example.finalwork.entity.CarSignal;
import com.example.finalwork.service.CarSignalService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController // 标识这是一个 Spring REST Controller
@RequestMapping("/api/car-signals") // 定义基础请求路径
public class CarSignalController {

    private final CarSignalService carSignalService;

    @Autowired // 注入 CarSignalService
    public CarSignalController(CarSignalService carSignalService) {
        this.carSignalService = carSignalService;
    }

    /**
     * POST /api/car-signals
     * 接口：上报/添加汽车信号状态
     * @param carSignal 请求体中的汽车信号数据
     * @return 响应实体
     */
    @PostMapping
    public ResponseEntity<String> addCarSignal(@RequestBody CarSignal carSignal) {
        try {
            // 基本的数据校验
            if (carSignal.getFrameNumber() == null) {
                return new ResponseEntity<>("Error:frameNumber are required.", HttpStatus.BAD_REQUEST);
            }
            int result = carSignalService.addCarSignal(carSignal);
            if (result > 0) {
                return new ResponseEntity<>("Car signal added successfully.", HttpStatus.CREATED); // 201 Created
            } else {
                return new ResponseEntity<>("Failed to add car signal.", HttpStatus.INTERNAL_SERVER_ERROR);
            }
        } catch (Exception e) {
            // 记录异常日志 (实际项目中应使用日志框架)
            e.printStackTrace();
            return new ResponseEntity<>("An error occurred: " + e.getMessage(), HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    /**
     * GET /api/car-signals/query
     * 接口：根据 sid 查询汽车信号
     * @param sid 信号ID
     * @return 响应实体
     */
    @GetMapping("/query")
    public ResponseEntity<CarSignal> getCarSignalBySid(@RequestParam Integer sid) {
        CarSignal carSignal = carSignalService.getCarSignalBySid(sid);
        if (carSignal != null) {
            return new ResponseEntity<>(carSignal, HttpStatus.OK);
        } else {
            return new ResponseEntity<>(HttpStatus.NOT_FOUND); // 404 Not Found
        }
    }

    /**
     * GET /api/car-signals
     * 接口：查询所有汽车信号
     * @return 响应实体列表
     */
    @GetMapping
    public ResponseEntity<List<CarSignal>> getAllCarSignals() {
        List<CarSignal> carSignals = carSignalService.getAllCarSignals();
        return new ResponseEntity<>(carSignals, HttpStatus.OK);
    }

    /**
     * GET /api/car-signals/by-frame/{frameNumber}
     * 接口：根据车架编号查询汽车信号
     * @param frameNumber 车架编号
     * @return 响应实体列表
     */
    @GetMapping("/by-frame/{frameNumber}")
    public ResponseEntity<List<CarSignal>> getCarSignalsByFrameNumber(@PathVariable Integer frameNumber) {
        List<CarSignal> carSignals = carSignalService.getCarSignalsByFrameNumber(frameNumber);
        if (carSignals != null && !carSignals.isEmpty()) {
            return new ResponseEntity<>(carSignals, HttpStatus.OK);
        } else {
            return new ResponseEntity<>(HttpStatus.NOT_FOUND);
        }
    }

    /**
     * PUT /api/car-signals/{sid}
     * 接口：更新汽车信号数据
     * @param sid 要更新的信号ID/规则编号
     * @param carSignal 请求体中包含更新数据的实体
     * @return 响应实体
     */
    @PutMapping("/{sid}")
    public ResponseEntity<String> updateCarSignal(@PathVariable Integer sid, @RequestBody CarSignal carSignal) {
        if (!sid.equals(carSignal.getSid())) {
            return new ResponseEntity<>("Error: Path variable sid does not match request body sid.", HttpStatus.BAD_REQUEST);
        }
        try {
            int result = carSignalService.updateCarSignal(carSignal);
            if (result > 0) {
                return new ResponseEntity<>("Car signal updated successfully.", HttpStatus.OK);
            } else {
                return new ResponseEntity<>("Car signal not found or no changes made.", HttpStatus.NOT_FOUND); // 或 200 OK
            }
        } catch (Exception e) {
            e.printStackTrace();
            return new ResponseEntity<>("An error occurred: " + e.getMessage(), HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    /**
     * DELETE /api/car-signals/{sid}
     * 接口：删除汽车信号数据
     * @param sid 要删除的信号ID/规则编号
     * @return 响应实体
     */
    @DeleteMapping("/{sid}")
    public ResponseEntity<String> deleteCarSignal(@PathVariable Integer sid) {
        try {
            int result = carSignalService.deleteCarSignal(sid);
            if (result > 0) {
                return new ResponseEntity<>("Car signal deleted successfully.", HttpStatus.NO_CONTENT); // 204 No Content
            } else {
                return new ResponseEntity<>("Car signal not found.", HttpStatus.NOT_FOUND);
            }
        } catch (Exception e) {
            e.printStackTrace();
            return new ResponseEntity<>("An error occurred: " + e.getMessage(), HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }
}
