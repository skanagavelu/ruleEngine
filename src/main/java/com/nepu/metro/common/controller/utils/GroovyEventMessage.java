package com.nepu.metro.common.controller.utils;

import com.nepu.metro.ruleEngine.events.model.TravelEvent;
import groovy.lang.GroovyShell;

public class GroovyEventMessage {

    public TravelEvent event;
    public GroovyShell shell;

    public GroovyEventMessage(TravelEvent event, GroovyShell shell) {

        this.event = event;
        this.shell = shell;
    }
}
