//package com.example.finalwork.consumer;
//
//import com.alibaba.fastjson2.JSON;
//import com.alibaba.fastjson2.JSONArray;
//import com.alibaba.fastjson2.JSONObject;
//import com.example.finalwork.dto.SingleWarningRequestItem;
//import com.fasterxml.jackson.databind.JavaType;
//import com.fasterxml.jackson.databind.ObjectMapper;
//import org.springframework.amqp.rabbit.annotation.RabbitListener;
//import org.springframework.beans.factory.annotation.Autowired;
//import org.springframework.beans.factory.annotation.Value;
//import org.springframework.http.HttpEntity;
//import org.springframework.http.HttpHeaders;
//import org.springframework.http.MediaType;
//import org.springframework.http.ResponseEntity;
//import org.springframework.scheduling.annotation.Scheduled;
//import org.springframework.stereotype.Component;
//import org.springframework.web.client.RestTemplate;
//
//import java.util.*;
//import java.util.concurrent.ConcurrentHashMap;
//import java.util.concurrent.ConcurrentLinkedQueue;
//import java.util.concurrent.atomic.AtomicInteger;
//
//@Component
//public class CarSignalConsumer {
//
//    private final ObjectMapper objectMapper;
//    private final RestTemplate restTemplate;
//
//    @Value("${api.endpoint.url}")
//    private String apiEndpoint;
//
//    @Value("${batch.size:500}")
//    private int batchSize;
//
//    @Value("${timeout.seconds:5}")
//    private int timeoutSeconds;
//
//    // 每个消费者的消息队列 - 使用线程安全队列
//    private final Map<Integer, ConcurrentLinkedQueue<SingleWarningRequestItem>> consumerQueues = new ConcurrentHashMap<>();
//
//    // 每个消费者的最后处理时间
//    private final Map<Integer, Long> lastProcessTime = new ConcurrentHashMap<>();
//
//    // 消费者计数器
//    private final AtomicInteger consumerCounter = new AtomicInteger(0);
//
//    @Autowired
//    public CarSignalConsumer(ObjectMapper objectMapper, RestTemplate restTemplate) {
//        this.objectMapper = objectMapper;
//        this.restTemplate = restTemplate;
//
//        // 初始化10个消费者的队列
//        for (int i = 0; i < 10; i++) {
//            consumerQueues.put(i, new ConcurrentLinkedQueue<>());
//            lastProcessTime.put(i, System.currentTimeMillis());
//        }
//    }
//
//    @RabbitListener(queues = "${spring.rabbitmq.queue:car-signal-queue}", concurrency = "10")
//    public void processMessage(String message) {
//        try {
//            int consumerId = consumerCounter.getAndIncrement() % 10;
//
//            // 尝试作为数组解析
////            JavaType type = objectMapper.getTypeFactory().constructCollectionType(List.class, SingleWarningRequestItem.class);
//            JSONObject jsonObject = JSONObject.parseObject(message);
//            JSONArray batchData = jsonObject.getJSONArray("batchData");
//            List<SingleWarningRequestItem> items = batchData.toJavaList(SingleWarningRequestItem.class);
//
//            // 将解析出的所有消息添加到队列
//            ConcurrentLinkedQueue<SingleWarningRequestItem> queue = consumerQueues.get(consumerId);
//            queue.addAll(items);
//
//            // 检查是否达到批量处理大小
//            if (queue.size() >= batchSize) {
//                processBatch(consumerId);
//            }
//
//        } catch (Exception e) {
//            System.err.println("处理消息失败: " + e.getMessage());
//        }
//    }
//
//    @Scheduled(fixedRate = 1000) // 每秒检查一次
//    public void checkTimeouts() {
//        long currentTime = System.currentTimeMillis();
//
//        for (int consumerId : consumerQueues.keySet()) {
//            ConcurrentLinkedQueue<SingleWarningRequestItem> queue = consumerQueues.get(consumerId);
//            long lastTime = lastProcessTime.get(consumerId);
//
//            // 检查是否超时且队列不为空
//            if (!queue.isEmpty() && (currentTime - lastTime) >= (timeoutSeconds * 1000)) {
//                processBatch(consumerId);
//            }
//        }
//    }
//
//    private void processBatch(int consumerId) {
//        ConcurrentLinkedQueue<SingleWarningRequestItem> queue = consumerQueues.get(consumerId);
//
//        // 提取当前批次要处理的消息
//        List<SingleWarningRequestItem> batchItems = new ArrayList<>(batchSize);
//        SingleWarningRequestItem item;
//        while ((item = queue.poll()) != null && batchItems.size() < batchSize) {
//            batchItems.add(item);
//        }
//
//        if (!batchItems.isEmpty()) {
//            try {
//                // 调用API
//                sendToApi(batchItems);
//
//                // 更新最后处理时间
//                lastProcessTime.put(consumerId, System.currentTimeMillis());
//
//                System.out.println("消费者 " + consumerId + " 处理了 " + batchItems.size() + " 条消息");
//
//            } catch (Exception e) {
//                // 处理失败，将消息重新放回队列
//                queue.addAll(batchItems);
//                System.err.println("API调用失败，已将消息放回队列: " + e.getMessage());
//            }
//        }
//    }
//
//    private void sendToApi(List<SingleWarningRequestItem> items) {
//        try {
//            // 设置请求头
//            HttpHeaders headers = new HttpHeaders();
//            headers.setContentType(MediaType.APPLICATION_JSON);
//
//            // 创建请求实体
//            HttpEntity<List<SingleWarningRequestItem>> request = new HttpEntity<>(items, headers);
//
//            // 调用API
//            ResponseEntity<String> response = restTemplate.postForEntity(apiEndpoint, request, String.class);
//
//            if (response.getStatusCode().is2xxSuccessful()) {
//                System.out.println("API调用成功，处理了 " + items.size() + " 条消息");
//            } else {
//                throw new RuntimeException("API返回非成功状态码: " + response.getStatusCode());
//            }
//
//        } catch (Exception e) {
//            throw new RuntimeException("调用API失败: " + e.getMessage(), e);
//        }
//    }
//}