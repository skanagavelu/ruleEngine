package com.nepu.metro.ruleEngine.events.handler;

import java.util.function.Function;

import com.nepu.metro.common.consumer.handler.Converter;
import com.nepu.metro.ruleEngine.events.model.TravelEvent;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class TravelEventConvertor implements Converter<TravelEvent, TravelEvent> {

    @Bean
    @Qualifier("converter")
    /**
     * Enriches the raw event with additional details from DB
     */
    public Function<TravelEvent, TravelEvent> convert()
    {
        return event ->  {
            event.meta.put("sex", "M"); //F/T
            event.meta.put("age", 67);
            return event;
        };
    }
}
