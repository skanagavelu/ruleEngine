package com.nepu.metro.ruleEngine.events.model;

import java.util.Objects;

public class Zone {

    public static final Zone ZONE_1 = new Zone("ZONE_1");
    public static final Zone ZONE_2 = new Zone("ZONE_2");

    public String name;

    public Zone(String name) {
        this.name = name;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Zone zone = (Zone) o;
        return Objects.equals(name, zone.name);
    }

    @Override
    public int hashCode() {
        return Objects.hash(name);
    }
}
