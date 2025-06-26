package com.example.finalwork.consumer;

import com.example.finalwork.dto.SingleWarningRequestItem;
import com.example.finalwork.dto.WarningResponse;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.apache.rocketmq.spring.annotation.RocketMQMessageListener;
import org.apache.rocketmq.spring.core.RocketMQListener;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpEntity; // 导入 HttpEntity
import org.springframework.http.HttpHeaders; // 导入 HttpHeaders
import org.springframework.http.HttpStatus; // 导入 HttpStatus
import org.springframework.http.MediaType; // 导入 MediaType
import org.springframework.http.ResponseEntity; // 导入 ResponseEntity
import org.springframework.scheduling.annotation.Scheduled; // 导入 @Scheduled
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestTemplate; // 导入 RestTemplate

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ConcurrentLinkedQueue; // 导入 ConcurrentLinkedQueue
import java.util.concurrent.atomic.AtomicLong; // 导入 AtomicLong

@Component
@RocketMQMessageListener(
        topic = "${rocketmq.topic.battery-signal}",
        consumerGroup = "${rocketmq.consumer.group}"
)
public class CarSignalConsumer implements RocketMQListener<String> {

    private final ObjectMapper objectMapper;
    private final RestTemplate restTemplate; // 用于调用 HTTP 接口
    private final ConcurrentLinkedQueue<SingleWarningRequestItem> buffer = new ConcurrentLinkedQueue<>(); // 线程安全的缓冲区
    private final AtomicLong lastFlushTime = new AtomicLong(System.currentTimeMillis()); // 记录上次发送时间

    private static final int BATCH_SIZE = 5; // 批处理大小
    private static final long TIME_THRESHOLD_MS = 10 * 1000; // 时间阈值：10 秒

    @Autowired
    public CarSignalConsumer(ObjectMapper objectMapper, RestTemplate restTemplate) {
        this.objectMapper = objectMapper;
        this.restTemplate = restTemplate;
    }

    /**
     * 消费者接收到 RocketMQ 消息时的处理逻辑。
     * 将收到的电池信号数据（列表）添加到内部缓冲区。
     * @param messageJson 消息内容（JSON 字符串）
     */
    @Override
    public void onMessage(String messageJson) {
        System.out.println("RocketMQ 消费者收到消息: " + messageJson);
        try {
            // 将接收到的 JSON 字符串反序列化为 List<SingleWarningRequestItem>
            List<SingleWarningRequestItem> signalDataList = objectMapper.readValue(messageJson, new TypeReference<List<SingleWarningRequestItem>>() {});

            if (signalDataList != null && !signalDataList.isEmpty()) {
                buffer.addAll(signalDataList); // 将所有接收到的项添加到缓冲区
                System.out.println("已将 " + signalDataList.size() + " 个信号项添加到缓冲区。当前缓冲区大小: " + buffer.size());
            }

        } catch (Exception e) {
            System.err.println("处理 RocketMQ 消息时发生反序列化错误: " + e.getMessage());
            e.printStackTrace();
        }
    }

    /**
     * 定时任务，负责检查缓冲区并触发批量 API 调用。
     * 每秒钟执行一次，检查是否满足发送条件。
     */
    @Scheduled(fixedRate = 1000) // 每 1000 毫秒（1秒）执行一次
    public void flushBufferAndCallApi() {
        // 条件：缓冲区大小达到 BATCH_SIZE 或 距离上次请求/清空已超过 TIME_THRESHOLD_MS
        // 并且缓冲区中确实有数据才进行处理
        if (!buffer.isEmpty() && (buffer.size() >= BATCH_SIZE || (System.currentTimeMillis() - lastFlushTime.get() >= TIME_THRESHOLD_MS))) {
            List<SingleWarningRequestItem> batchToSend = new ArrayList<>();
            // 从缓冲区取出最多 BATCH_SIZE 个元素
            for (int i = 0; i < BATCH_SIZE && !buffer.isEmpty(); i++) {
                batchToSend.add(buffer.poll()); // 从队列头部取出并移除
            }

            if (!batchToSend.isEmpty()) {
                System.out.println("触发批量请求 API。发送 " + batchToSend.size() + " 个信号项。当前缓冲区剩余: " + buffer.size());
                callWarningApi(batchToSend);
                lastFlushTime.set(System.currentTimeMillis()); // 更新上次刷新时间
            }
        }
    }

    /**
     * 调用 HTTP API (/api/warning/calculate) 发送批量信号数据。
     * @param items 待发送的信号数据列表
     */
    private void callWarningApi(List<SingleWarningRequestItem> items) {
        String apiUrl = "http://localhost:8080/api/warning/calculate";
        try {
            // 设置请求头为 JSON
            HttpHeaders headers = new HttpHeaders();
            headers.setContentType(MediaType.APPLICATION_JSON);

            // 构建请求实体
            HttpEntity<List<SingleWarningRequestItem>> requestEntity = new HttpEntity<>(items, headers);

            // 发送 POST 请求并接收响应
            ResponseEntity<WarningResponse> responseEntity = restTemplate.postForEntity(apiUrl, requestEntity, WarningResponse.class);

            if (responseEntity.getStatusCode().is2xxSuccessful()) {
                WarningResponse responseBody = responseEntity.getBody();
                if (responseBody != null && responseBody.getStatus() == HttpStatus.OK.value()) {
                    System.out.println("API 调用成功。返回报警数: " + (responseBody.getParts() != null ? responseBody.getParts().size() : 0));
                    if (responseBody.getParts() != null && !responseBody.getParts().isEmpty()) {
                        System.out.println("触发报警详情: " + responseBody.getParts());
                    }
                } else {
                    System.err.println("API 调用成功但业务处理失败: " + (responseBody != null ? responseBody.getMessage() : "无消息"));
                }
            } else {
                System.err.println("API 调用失败，HTTP 状态码: " + responseEntity.getStatusCode() + ", 响应体: " + responseEntity.getBody());
            }
        } catch (Exception e) {
            System.err.println("调用预警 API [" + apiUrl + "] 失败: " + e.getMessage());
            e.printStackTrace();
        }
    }
}