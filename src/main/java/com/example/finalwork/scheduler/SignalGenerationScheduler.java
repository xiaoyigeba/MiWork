//package com.example.finalwork.scheduler;
//
//import com.example.finalwork.dto.SingleWarningRequestItem;
//import com.example.finalwork.producer.CarSignalProducer;
//import com.fasterxml.jackson.databind.ObjectMapper;
//import org.springframework.beans.factory.annotation.Autowired;
//import org.springframework.scheduling.annotation.Scheduled;
//import org.springframework.stereotype.Component;
//
//import java.util.*;
//
//@Component
//public class SignalGenerationScheduler {
//
//    private final CarSignalProducer carSignalProducer;
//    private final ObjectMapper objectMapper; // 用于构建 signal JSON
//    private final Random random = new Random();
//
//    // carId 范围
//    private static final int CAR_ID_MIN = 1001;
//    private static final int CAR_ID_MAX = 2000;
//
//    @Autowired
//    public SignalGenerationScheduler(CarSignalProducer carSignalProducer, ObjectMapper objectMapper) {
//        this.carSignalProducer = carSignalProducer;
//        this.objectMapper = objectMapper;
//    }
//
//    /**
//     * 每秒钟生成一组电池信号数据并发送到 RocketMQ。
//     */
//    @Scheduled(fixedRate = 1000) // 每1000毫秒执行一次
//    public void generateAndSendSignalData() {
//        // 每批次生成3个不同 carId 的数据，与示例保持一致
//        List<SingleWarningRequestItem> batchData = new ArrayList<>();
//        Set<Integer> usedCarIds = new HashSet<>(); // 用于确保 carId 唯一
//
//        while (batchData.size() < 3) { // 生成3个不同的 carId
//            int carId = CAR_ID_MIN + random.nextInt(CAR_ID_MAX - CAR_ID_MIN + 1);
//            if (usedCarIds.add(carId)) { // 确保 carId 不重复
//                batchData.add(generateSingleWarningRequestItem(carId));
//            }
//        }
//
//        carSignalProducer.sendBatterySignalBatch(batchData);
//    }
//
//    /**
//     * 生成单个 SingleWarningRequestItem 实例的数据。
//     * @param carId 指定的 carId
//     * @return 生成的 SingleWarningRequestItem
//     */
//    private SingleWarningRequestItem generateSingleWarningRequestItem(int carId) {
//        SingleWarningRequestItem item = new SingleWarningRequestItem();
//        item.setFrameNumber(carId);
//
//        int warnIdChoice = random.nextInt(3); // 0: 无warnId, 1: warnId=1, 2: warnId=2
//
//        try {
//            Map<String, Double> signalMap = new HashMap<>();
//
//            if (warnIdChoice == 1) { // warnId = 1: Mx, Mi
//                item.setRuleNumber(1);
//                double mi = generateRandomDouble(3.0, 8.0);
//                double mx = generateRandomDouble(mi + 0.1, 10.0); // 确保 Mx > Mi
//                signalMap.put("Mx", mx);
//                signalMap.put("Mi", mi);
//            } else if (warnIdChoice == 2) { // warnId = 2: Ix, Ii
//                item.setRuleNumber(2);
//                double ii = generateRandomDouble(4.0, 8.0);
//                double ix = generateRandomDouble(ii + 0.1, 8.0); // 确保 Ix > Ii
//                signalMap.put("Ix", ix);
//                signalMap.put("Ii", ii);
//            } else { // 无 warnId: Mx, Mi, Ix, Ii 都有
//                item.setRuleNumber(null); // 或者不设置，取决于JSON序列化行为
//                double mi = generateRandomDouble(3.0, 8.0);
//                double mx = generateRandomDouble(mi + 0.1, 10.0);
//                double ii = generateRandomDouble(4.0, 8.0);
//                double ix = generateRandomDouble(ii + 0.1, 8.0);
//
//                signalMap.put("Mx", mx);
//                signalMap.put("Mi", mi);
//                signalMap.put("Ix", ix);
//                signalMap.put("Ii", ii);
//            }
//
//            // 将 Map 转换为 JSON 字符串
//            item.setSignalData(objectMapper.writeValueAsString(signalMap));
//
//        } catch (Exception e) {
//            System.err.println("生成信号数据失败: " + e.getMessage());
//            item.setSignalData("{}"); // 发生错误时设置为空 JSON
//        }
//        return item;
//    }
//
//    /**
//     * 生成指定范围内的随机双精度浮点数。
//     * @param min 最小值 (包含)
//     * @param max 最大值 (包含)
//     * @return 随机浮点数
//     */
//    private double generateRandomDouble(double min, double max) {
//        // 确保 min 不大于 max
//        if (min > max) {
//            double temp = min;
//            min = max;
//            max = temp;
//        }
//        return min + (max - min) * random.nextDouble();
//    }
//}