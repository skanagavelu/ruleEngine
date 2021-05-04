package com.nepu.metro.common.consumer;

import com.nepu.metro.ruleEngine.events.model.TravelEvent;
import org.springframework.context.annotation.Configuration;

@Configuration
public class KafkaConsumer<T> implements Consumer<T> {

    @Override
    public void start() {
        //Start kafka high level consumer
    }

    @Override
    public void close() {
        //Kafka consumer close
    }

    @Override
    public boolean hasNext() {

        return true;
    }

    @Override
    @SuppressWarnings("unchecked")
    public T next() {

        //TODO: Demo: Consumer to return events from queue
        return (T) new TravelEvent();
    }
}
