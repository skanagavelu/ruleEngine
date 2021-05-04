package com.nepu.metro.ruleEngine.rules;

public interface Rule<T> {

    boolean preCondition(T message);
    boolean action(T message);
}