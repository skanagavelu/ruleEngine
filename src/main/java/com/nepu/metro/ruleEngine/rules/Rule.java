package com.nepu.metro.ruleEngine.rules;

public interface Rule<T> {

    /**
     * Decides {@link #action(Object)} can be called or not.
     */
    boolean preCondition(T message);

    /**
     * action will be called only when {@link #preCondition(Object)} is successful
     */
    boolean action(T message);
}