package com.nepu.metro.ruleEngine.events.model;

import java.io.Serializable;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

/*
 * TravelEvent that is going to be persisted for each travel.
 * Currently it is populated for demo.
 */
public class TravelEvent implements Serializable {

    private static final long serialVersionUID = -1015228389637127676L;

    public String id = UUID.randomUUID().toString();
    public String userId = UUID.randomUUID().toString();
    public String cardId = UUID.randomUUID().toString();
    public Zone from = new Zone(UUID.randomUUID().toString(), "Zone_1");
    public Zone to = new Zone(UUID.randomUUID().toString(), "Zone_2");
    public double cost = 20; //Base fare from Zone_1 to Zone_2
    public Date enteredAt;
    public Date exitedAt;
    public transient Map<String, Object> meta = new HashMap<>();

    public TravelEvent() {

        Calendar today = Calendar.getInstance();
        today.set(Calendar.HOUR_OF_DAY, 0);
        today.set(Calendar.MINUTE, 0);
        today.set(Calendar.SECOND, 0);
        this.enteredAt = today.getTime();
        today.set(Calendar.HOUR_OF_DAY, 5);
        this.exitedAt = today.getTime();
    }

    @Override
    public String toString() {

        return "TravelEvent{" +
               "id='" + id + '\'' +
               ", userId='" + userId + '\'' +
               ", cardId='" + cardId + '\'' +
               ", from=" + from +
               ", to=" + to +
               ", cost=" + cost +
               ", enteredAt=" + enteredAt +
               ", exitedAt=" + exitedAt +
               ", meta=" + meta +
               '}';
    }
}
