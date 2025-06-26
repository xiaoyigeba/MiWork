package com.example.finalwork.producer;

import com.example.finalwork.config.RabbitMQConfig;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import java.time.LocalDateTime;

@Component
public class MessageProducer {

    private final RabbitTemplate rabbitTemplate;

    public MessageProducer(RabbitTemplate rabbitTemplate) {
        this.rabbitTemplate = rabbitTemplate;
    }

//    @Scheduled(fixedRate = 1000) // 每秒执行一次
//    public void sendMessage() {
//        String message = "Message_" + LocalDateTime.now();
//        rabbitTemplate.convertAndSend(RabbitMQConfig.QUEUE_NAME, message);
//        System.out.println("发送消息: " + message);
//    }
}