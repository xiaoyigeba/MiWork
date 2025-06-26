package com.example.finalwork.consumer;

import com.example.finalwork.config.RabbitMQConfig;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

@Component
public class MessageConsumer {

    private static final int BATCH_SIZE = 500;
    private static final int MAX_WAIT_TIME_SECONDS = 5;

    private final List<String> messageBatch = new ArrayList<>(BATCH_SIZE);
    private final ScheduledExecutorService scheduler = Executors.newSingleThreadScheduledExecutor();

    public MessageConsumer() {
        // 启动定时任务，每5秒检查一次是否有批量消息需要处理
        scheduler.scheduleAtFixedRate(this::processBatchIfNeeded,
                MAX_WAIT_TIME_SECONDS,
                MAX_WAIT_TIME_SECONDS,
                TimeUnit.SECONDS);
    }

    @RabbitListener(queues = RabbitMQConfig.QUEUE_NAME)
    public void receiveMessage(String message) {
        synchronized (messageBatch) {
            messageBatch.add(message);
            System.out.println("收到消息: " + message + "，当前批次大小: " + messageBatch.size());

            if (messageBatch.size() >= BATCH_SIZE) {
                processBatch();
            }
        }
    }

    private void processBatchIfNeeded() {
        synchronized (messageBatch) {
            if (!messageBatch.isEmpty()) {
                processBatch();
            }
        }
    }

    private void processBatch() {
        List<String> messagesToProcess = new ArrayList<>(messageBatch);
        messageBatch.clear();

        // 模拟调用API
        System.out.println("处理批次，大小: " + messagesToProcess.size());
        callExternalApi(messagesToProcess);
    }

    private void callExternalApi(List<String> messages) {
        // 这里是实际调用外部API的代码
        System.out.println("调用API，发送 " + messages.size() + " 条消息");
        // 实际项目中可以使用RestTemplate或WebClient来调用API
        // 示例: restTemplate.postForEntity(apiUrl, messages, Response.class);
    }
}