package com.nepu.metro.ruleEngine.rules;

import com.nepu.metro.common.controller.utils.GroovyEventMessage;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Arrays;
import java.util.List;

@Service
public class RulesContainer {

    @Autowired
    GroovyRulesUtils utils;

    //TODO Read from DB via cache
    public List<? extends Rule<GroovyEventMessage>> getRules() {

        return Arrays.asList(

                //Senior citizen rule: 5 rupees discount
                new GroovyRule(new RuleScript("return ((int)event.meta.get(\"age\")) > 60",
                        "event.cost = event.cost - 5; return true")),

                //Concession for women: 5 rupees discount
                new GroovyRule(new RuleScript("return ((String)event.meta.get(\"sex\")) == \"F\"",
                        "event.cost = event.cost - 5; return true;"))
        );
    }
}
