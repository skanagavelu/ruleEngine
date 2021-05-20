package com.nepu.metro.common.utils;

import java.time.LocalDateTime;
import java.time.LocalTime;
import java.time.ZoneId;
import java.util.Date;

public class DateTimeUtils {

    public static boolean isWeekend(Date givenDate) {

        LocalDateTime dt = getLocalDateTime(givenDate);
        switch(dt.getDayOfWeek()) {
            case SATURDAY:
            case SUNDAY:
                return true;
            default:
                return false;
        }
    }

    public static LocalDateTime getLocalDateTime(Date date){

        return LocalDateTime.ofInstant(date.toInstant(), ZoneId.systemDefault());
    }

    public static class TimeRange {

        LocalTime from;
        LocalTime to;

        public TimeRange(LocalTime from, LocalTime to) {
            this.from = from;
            this.to = to;
        }

        public boolean isInRange(Date givenDate) {

            LocalTime givenLocalTime = getLocalDateTime(givenDate).toLocalTime();
            return givenLocalTime.isAfter(from) && givenLocalTime.isBefore(to);
        }
    }
}
