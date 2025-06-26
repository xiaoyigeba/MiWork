package com.example.finalwork.producer;

import com.alibaba.fastjson2.JSON;
import com.alibaba.fastjson2.JSONObject;
import com.example.finalwork.dto.SingleWarningRequestItem;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import java.util.*;

@Component
public class CarSignalProducer {

    private final RabbitTemplate rabbitTemplate;
    private final ObjectMapper objectMapper;
    private final Random random = new Random();

    @Value("${spring.rabbitmq.exchange:car-signal-exchange}")
    private String exchangeName;

    @Value("${spring.rabbitmq.routing-key:car-signal-key}")
    private String routingKey;

    // carId 范围
    private static final int CAR_ID_MIN = 1001;
    private static final int CAR_ID_MAX = 2000;

    @Autowired
    public CarSignalProducer(RabbitTemplate rabbitTemplate, ObjectMapper objectMapper) {
        this.rabbitTemplate = rabbitTemplate;
        this.objectMapper = objectMapper;
    }

    /**
     * 每秒钟生成一组电池信号数据并发送到 RabbitMQ。
     */
    @Scheduled(fixedRate = 1000) // 每1000毫秒执行一次
    public void generateAndSendSignalData() {
        // 每批次生成100个不同 carId 的数据
        List<SingleWarningRequestItem> batchData = generateBatchData(100);
        sendBatterySignalBatch(batchData);
    }

    /**
     * 生成批量的信号数据
     * @param batchSize 批次大小
     * @return 生成的数据列表
     */
    public List<SingleWarningRequestItem> generateBatchData(int batchSize) {
        List<SingleWarningRequestItem> batchData = new ArrayList<>();
        Set<Integer> usedCarIds = new HashSet<>(); // 用于确保 carId 唯一

        while (batchData.size() < batchSize) { // 生成指定数量的不同 carId
            int carId = CAR_ID_MIN + random.nextInt(CAR_ID_MAX - CAR_ID_MIN + 1);
            if (usedCarIds.add(carId)) { // 确保 carId 不重复
                batchData.add(generateSingleWarningRequestItem(carId));
            }
        }
        return batchData;
    }

    /**
     * 生成单个 SingleWarningRequestItem 实例的数据。
     * @param carId 指定的 carId
     * @return 生成的 SingleWarningRequestItem
     */
    private SingleWarningRequestItem generateSingleWarningRequestItem(int carId) {
        SingleWarningRequestItem item = new SingleWarningRequestItem();
        item.setFrameNumber(carId);

        int warnIdChoice = random.nextInt(3); // 0: 无warnId, 1: warnId=1, 2: warnId=2

        try {
            Map<String, Double> signalMap = new HashMap<>();

            if (warnIdChoice == 1) { // warnId = 1: Mx, Mi
                item.setRuleNumber(1);
                double mi = generateRandomDouble(3.0, 8.0);
                double mx = generateRandomDouble(mi + 0.1, 10.0); // 确保 Mx > Mi
                signalMap.put("Mx", mx);
                signalMap.put("Mi", mi);
            } else if (warnIdChoice == 2) { // warnId = 2: Ix, Ii
                item.setRuleNumber(2);
                double ii = generateRandomDouble(4.0, 8.0);
                double ix = generateRandomDouble(ii + 0.1, 8.0); // 确保 Ix > Ii
                signalMap.put("Ix", ix);
                signalMap.put("Ii", ii);
            } else { // 无 warnId: Mx, Mi, Ix, Ii 都有
                item.setRuleNumber(null); // 或者不设置，取决于JSON序列化行为
                double mi = generateRandomDouble(3.0, 8.0);
                double mx = generateRandomDouble(mi + 0.1, 10.0);
                double ii = generateRandomDouble(4.0, 8.0);
                double ix = generateRandomDouble(ii + 0.1, 8.0);

                signalMap.put("Mx", mx);
                signalMap.put("Mi", mi);
                signalMap.put("Ix", ix);
                signalMap.put("Ii", ii);
            }

            // 将 Map 转换为 JSON 字符串
            item.setSignalData(objectMapper.writeValueAsString(signalMap));

        } catch (Exception e) {
            System.err.println("生成信号数据失败: " + e.getMessage());
            item.setSignalData("{}"); // 发生错误时设置为空 JSON
        }
        return item;
    }

    /**
     * 生成指定范围内的随机双精度浮点数。
     * @param min 最小值 (包含)
     * @param max 最大值 (包含)
     * @return 随机浮点数
     */
    private double generateRandomDouble(double min, double max) {
        // 确保 min 不大于 max
        if (min > max) {
            double temp = min;
            min = max;
            max = temp;
        }
        return min + (max - min) * random.nextDouble();
    }

    /**
     * 批量发送电池信号数据到RabbitMQ
     * @param batchData 批量数据列表
     */
    public void sendBatterySignalBatch(List<SingleWarningRequestItem> batchData) {
        try {

            JSONObject jsonObject = new JSONObject();
            jsonObject.put("batchData",batchData);

            // 发送消息到RabbitMQ
            rabbitTemplate.convertAndSend(exchangeName, routingKey, jsonObject.toJSONString());

            System.out.println("发送批次消息到RabbitMQ，大小: " + batchData.size());
        } catch (Exception e) {
            System.err.println("发送消息到RabbitMQ失败: " + e.getMessage());
            // 这里可以添加重试逻辑或错误处理
        }
    }
}