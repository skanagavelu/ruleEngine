package com.nepu.metro.ruleEngine.events.model;

public class Zone {

    public Zone(String id, String name) {

        this.id = id;
        this.name = name;
    }

    public String id;
    public String name;

    @Override
    public String toString() {

        return "Zone{" +
               "id='" + id + '\'' +
               ", name='" + name + '\'' +
               '}';
    }
}
