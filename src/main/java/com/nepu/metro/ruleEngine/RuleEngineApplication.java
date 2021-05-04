package com.nepu.metro.ruleEngine;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

/**
 * <PRE>
 * Application responsible for
 * 1. Reading the raw event from {@link com.nepu.metro.common.consumer.Consumer consumer},
 * 2. {@link com.nepu.metro.common.consumer.handler.Converter Enrich} it for rules execution
 * 3. {@link com.nepu.metro.common.consumer.handler.EventHandler Execute} the business rules, and update the event
 * 4. {@link com.nepu.metro.common.producer.Producer Push} back to producer for further processing or storing into db.
 * </PRE>
 */
@SpringBootApplication(scanBasePackages={ "com.nepu.metro"})
public class RuleEngineApplication {

    public static void main(String[] args) {

        SpringApplication.run(RuleEngineApplication.class, args);
    }
}
