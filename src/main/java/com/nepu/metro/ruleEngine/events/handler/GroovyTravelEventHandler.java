package com.nepu.metro.ruleEngine.events.handler;

import java.util.Arrays;
import java.util.List;
import java.util.Optional;
import java.util.function.Function;
import java.util.function.Predicate;

import com.nepu.metro.common.consumer.handler.EventHandlerBase;
import com.nepu.metro.common.controller.utils.GroovyEventMessage;
import com.nepu.metro.common.producer.Producer;
import com.nepu.metro.ruleEngine.events.model.TravelEvent;
import com.nepu.metro.ruleEngine.rules.GroovyRule;
import com.nepu.metro.ruleEngine.rules.Rule;
import com.nepu.metro.ruleEngine.rules.RuleScript;
import groovy.lang.Binding;
import groovy.lang.GroovyShell;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Configuration;

@Configuration
public class GroovyTravelEventHandler extends EventHandlerBase<TravelEvent, TravelEvent>  {

    @Autowired
    Producer<TravelEvent> producer;

    //TODO Read from DB via cache
    List<? extends Rule<GroovyEventMessage>> rules = Arrays.asList(

            //Senior citizen rule: 5 rupees discount
            new GroovyRule(new RuleScript("return ((int)event.meta.get(\"age\")) > 60",
                                          "event.cost = event.cost - 5; return true")),

            //Concession for women: 5 rupees discount
            new GroovyRule(new RuleScript("return ((String)event.meta.get(\"sex\")) == \"F\"",
                                          "event.cost = event.cost - 5; return true;"))
            );

    public GroovyTravelEventHandler(Function<TravelEvent, TravelEvent> converter) {

        super(converter);
    }

    @Override
    public void handle(TravelEvent rawInput) {

        final TravelEvent event = converter.apply(rawInput);
        Binding binding = new Binding();
        binding.setVariable("event", event);
        GroovyShell shell = new GroovyShell(binding);
        GroovyEventMessage message = new GroovyEventMessage(event, shell);

        //Chain of responsibility: Rules execution
        Optional<? extends Rule<GroovyEventMessage>> skipRule = rules.stream()
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
