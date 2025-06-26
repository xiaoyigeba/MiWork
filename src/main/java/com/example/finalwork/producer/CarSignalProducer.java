//package com.example.finalwork.producer;
//
//import com.example.finalwork.dto.SingleWarningRequestItem;
//import org.apache.rocketmq.spring.core.RocketMQTemplate;
//import org.springframework.beans.factory.annotation.Autowired;
//import org.springframework.beans.factory.annotation.Value;
//import org.springframework.messaging.Message;
//import org.springframework.messaging.support.MessageBuilder;
//import org.springframework.stereotype.Component;
//import com.fasterxml.jackson.databind.ObjectMapper; // 用于将 List<SingleWarningRequestItem> 转换为 JSON 字符串
//
//import java.util.List;
//
//@Component
//public class CarSignalProducer {
//
//    @Value("${rocketmq.topic.battery-signal}")
//    private String batterySignalTopic;
//
//    private final RocketMQTemplate rocketMQTemplate;
//    private final ObjectMapper objectMapper; // Spring Boot 自动配置了 ObjectMapper
//
//    @Autowired
//    public CarSignalProducer(RocketMQTemplate rocketMQTemplate, ObjectMapper objectMapper) {
//        this.rocketMQTemplate = rocketMQTemplate;
//        this.objectMapper = objectMapper;
//    }
//
//    /**
//     * 发送一组电池信号数据到 RocketMQ。
//     * @param signalDataList 包含多个 SingleWarningRequestItem 的列表
//     */
//    public void sendBatterySignalBatch(List<SingleWarningRequestItem> signalDataList) {
//        try {
//            // 将 List<SingleWarningRequestItem> 转换为 JSON 字符串作为消息体
//            String jsonMessage = objectMapper.writeValueAsString(signalDataList);
//
//            // 构建消息
//            Message<String> message = MessageBuilder.withPayload(jsonMessage)
//                    .setHeader("KEYS", "batch_" + System.currentTimeMillis()) // 可以设置业务 KEY
//                    .build();
//
//            rocketMQTemplate.send(batterySignalTopic, message);
//            System.out.println("成功发送一组电池信号数据到主题: " + batterySignalTopic);
//        } catch (Exception e) {
//            System.err.println("发送电池信号数据失败: " + e.getMessage());
//            e.printStackTrace();
//        }
//    }
//}