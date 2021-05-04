package com.nepu.metro.ruleEngine.rules;

public class RuleScript<T> {

    public RuleScript(T preConditionScript, T actionScript) {

        this.preConditionScript = preConditionScript;
        this.actionScript = actionScript;
    }

    final T preConditionScript;
    final T actionScript;
}
