package com.nepu.metro.ruleEngine.rules;

import com.nepu.metro.common.controller.utils.GroovyEventMessage;

public class GroovyRule implements Rule<GroovyEventMessage>{

    RuleScript<String> script;

    public GroovyRule(RuleScript<String> script) {

        this.script = script;
    }

    @Override
    public boolean preCondition(GroovyEventMessage message) {

        return (boolean) message.shell.evaluate(script.preConditionScript);
    }

    @Override
    public boolean action(GroovyEventMessage message) {

        return (boolean) message.shell.evaluate(script.actionScript);
    }
}
