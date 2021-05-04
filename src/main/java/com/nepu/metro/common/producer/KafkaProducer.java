package com.nepu.metro.common.producer;

import org.springframework.context.annotation.Configuration;

@Configuration
public class KafkaProducer<T> implements Producer<T> {

    public void submit(T message) {

        //TODO: produce the message to kafka topic
        System.out.println(message);
    }

    @Override
    public void close() {

        System.out.println("Producer has stopped");
    }
}
