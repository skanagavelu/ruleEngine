package com.nepu.metro.ruleEngine.rules;

import com.nepu.metro.common.utils.DateTimeUtils;
import com.nepu.metro.ruleEngine.events.model.Zone;
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

    /*
     * TODO: When more Zones are added, it is good to read from DB,
     *  Hence no code changes required
     */
    @Value("${rules.dailyCap.zone.one.to.one:100}")
    double dailyCapZoneOneToOne;
    @Value("${rules.dailyCap.zone.one.to.two:120}")
    double dailyCapZoneOneToTwo;
    @Value("${rules.dailyCap.zone.two.to.two:80}")
    double dailyCapZoneTwoToTwo;
    @Value("${rules.weeklyCap.zone.one.to.one:500}")
    double weeklyCapZoneOneToOne;
    @Value("${rules.weeklyCap.zone.one.to.two:600}")
    double weeklyCapZoneOneToTwo;
    @Value("${rules.weeklyCap.zone.two.to.two:400}")
    double weeklyCapZoneTwoToTwo;


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

    public double getRemainingDailyCap(double currentTravelCost, double totalDailyTravelSpent,
                                       double farthestJourneyDailyCap) {

        if ((farthestJourneyDailyCap - totalDailyTravelSpent) < currentTravelCost) {

            double computedTravelCost = farthestJourneyDailyCap - totalDailyTravelSpent;
            return  computedTravelCost <= 0 ? 0 : computedTravelCost;
        }
        return currentTravelCost;
    }

    public double getRemainingWeeklyCap(double currentTravelCost, double totalWeeklyTravelSpent,
                                       double farthestJourneyWeeklyCap) {

        if ((farthestJourneyWeeklyCap - totalWeeklyTravelSpent) < currentTravelCost) {

            double computedTravelCost = farthestJourneyWeeklyCap - totalWeeklyTravelSpent;
            return  computedTravelCost <= 0 ? 0 : computedTravelCost;
        }
        return currentTravelCost;
    }

    public double getFarthestJourneyWeeklyCap(Zone from, Zone to, double farthestJourneyWeeklyCapSoFar) {

        return getWeeklyCap(from, to) > farthestJourneyWeeklyCapSoFar ? getWeeklyCap(from, to)
                                                                                : farthestJourneyWeeklyCapSoFar;
    }

    public double getFarthestJourneyDailyCap(Zone from, Zone to, double farthestJourneyDailyCapSoFar) {

        return getDailyCap(from, to) > farthestJourneyDailyCapSoFar ? getDailyCap(from, to)
                                                                                : farthestJourneyDailyCapSoFar;
    }

    private double getDailyCap(Zone from, Zone to) {

        if (from.equals(Zone.ZONE_1) && to.equals(Zone.ZONE_1)) {

            return dailyCapZoneOneToOne;
        } else if (!from.equals(to)) {

            return dailyCapZoneOneToTwo;
        } else {
            return dailyCapZoneTwoToTwo;
        }
    }

    private double getWeeklyCap(Zone from, Zone to) {

        if (from.equals(Zone.ZONE_1) && to.equals(Zone.ZONE_1)) {

            return weeklyCapZoneOneToOne;
        } else if (!from.equals(to)) {

            return weeklyCapZoneOneToTwo;
        } else {
            return weeklyCapZoneTwoToTwo;
        }
    }
}
