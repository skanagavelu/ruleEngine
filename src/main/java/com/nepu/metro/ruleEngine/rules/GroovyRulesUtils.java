package com.nepu.metro.ruleEngine.rules;

import com.nepu.metro.common.utils.DateTimeUtils;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Configuration;

import javax.annotation.PostConstruct;
import java.time.LocalTime;
import java.util.Date;

import static com.nepu.metro.common.utils.DateTimeUtils.isWeekend;
import static com.nepu.metro.common.utils.DateTimeUtils.TimeRange;

@Configuration
public class GroovyRulesUtils {

    @Value("#{T(java.time.LocalTime).parse('${rules.peakHours.weekday.morning.start}')}")
    LocalTime weekDayMorningPeakStart;
    @Value("#{T(java.time.LocalTime).parse('${rules.peakHours.weekday.morning.end}')}")
    LocalTime weekDayMorningPeakEnd;
    @Value("#{T(java.time.LocalTime).parse('${rules.peakHours.weekday.evening.start}')}")
    LocalTime weekDayEveningPeakStart;
    @Value("#{T(java.time.LocalTime).parse('${rules.peakHours.weekday.evening.end}')}")
    LocalTime weekDayEveningPeakEnd;
    @Value("#{T(java.time.LocalTime).parse('${rules.peakHours.weekend.morning.start}')}")
    LocalTime weekendMorningPeakStart;
    @Value("#{T(java.time.LocalTime).parse('${rules.peakHours.weekend.morning.end}')}")
    LocalTime weekendMorningPeakEnd;
    @Value("#{T(java.time.LocalTime).parse('${rules.peakHours.weekend.evening.start}')}")
    LocalTime weekendEveningPeakStart;
    @Value("#{T(java.time.LocalTime).parse('${rules.peakHours.weekend.evening.end}')}")
    LocalTime weekendEveningPeakEnd;

    TimeRange weekDayMorningPeakRange;
    TimeRange weekDayEveningPeakRange;
    TimeRange weekEndMorningPeakRange;
    TimeRange weekEndEveningPeakRange;

    @PostConstruct
    public void init() {

        this.weekDayMorningPeakRange = new TimeRange(weekDayMorningPeakStart, weekDayMorningPeakEnd);
        this.weekDayEveningPeakRange = new TimeRange(weekDayEveningPeakStart, weekDayEveningPeakEnd);
        this.weekEndMorningPeakRange = new TimeRange(weekendMorningPeakStart, weekendMorningPeakEnd);
        this.weekEndEveningPeakRange = new TimeRange(weekendEveningPeakStart, weekendEveningPeakEnd);
    }



    public boolean isPeakMorningHours(Date date) {

        if(isWeekend(date)) {
            return weekEndMorningPeakRange.isInRange(date);
        } else {
            return weekDayMorningPeakRange.isInRange(date);
        }
    }

    public boolean isPeakEveningHours(Date date) {

        if(isWeekend(date)) {
            return weekEndEveningPeakRange.isInRange(date);
        } else {
            return weekDayEveningPeakRange.isInRange(date);
        }
    }
}
