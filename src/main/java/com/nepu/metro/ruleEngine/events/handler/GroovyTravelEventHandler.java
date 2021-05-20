package com.nepu.metro.ruleEngine.events.handler;

import com.nepu.metro.common.consumer.handler.EventHandlerBase;
import com.nepu.metro.common.controller.utils.GroovyEventMessage;
import com.nepu.metro.common.producer.Producer;
import com.nepu.metro.ruleEngine.events.model.TravelEvent;
import com.nepu.metro.ruleEngine.rules.Rule;
import com.nepu.metro.ruleEngine.rules.GroovyRulesUtils;
import com.nepu.metro.ruleEngine.rules.RulesContainer;
import groovy.lang.Binding;
import groovy.lang.GroovyShell;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Configuration;

import java.util.Optional;
import java.util.function.Function;
import java.util.function.Predicate;

@Configuration
public class GroovyTravelEventHandler extends EventHandlerBase<TravelEvent, TravelEvent>  {

    @Autowired
    public Producer<TravelEvent> producer;

    @Autowired
    public RulesContainer rulesContainer;

    @Autowired
    public GroovyRulesUtils utils;

    public GroovyTravelEventHandler(Function<TravelEvent, TravelEvent> converter) {

        super(converter);
    }

    @Override
    public void handle(TravelEvent rawInput) {

        final TravelEvent event = converter.apply(rawInput);
        Binding binding = new Binding();
        binding.setVariable("event", event);
        binding.setVariable("utils", utils);
        GroovyShell shell = new GroovyShell(binding);
        GroovyEventMessage message = new GroovyEventMessage(event, shell);

        //Chain of responsibility: Rules execution
        Optional<? extends Rule<GroovyEventMessage>> skipRule = rulesContainer.getRules().stream()
                                                .filter(rule -> rule.preCondition(message))
                                                .filter(Predicate.not(rule -> rule.action(message)))
                                                .findFirst();

        skipRule.ifPresent(rule -> System.out.println("Rules after the rule:" + rule + " are skipped!"));

        //TODO Kafka/DB PUSH
        producer.submit(event);
    }

    @Override
    public void done() {

        producer.close();
    }
}
