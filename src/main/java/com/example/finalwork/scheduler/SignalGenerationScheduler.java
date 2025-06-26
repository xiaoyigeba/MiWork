//package com.example.finalwork.scheduler;
//
//import com.example.finalwork.producer.CarSignalProducer;
//import org.springframework.beans.factory.annotation.Autowired;
//import org.springframework.scheduling.annotation.Scheduled;
//import org.springframework.stereotype.Component;
//
//@Component
//public class SignalGenerationScheduler {
//
//    private final CarSignalProducer carSignalProducer;
//
//    @Autowired
//    public SignalGenerationScheduler(CarSignalProducer carSignalProducer) {
//        this.carSignalProducer = carSignalProducer;
//    }
//
//    @Scheduled(fixedRate = 1000)
//    public void triggerSignalGeneration() {
//        carSignalProducer.generateAndSendSignalData();
//    }
//}