package com.nepu.metro.ruleEngine.events.handler;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.time.Month;
import java.time.ZoneId;
import java.util.Arrays;
import java.util.Date;
import java.util.List;
import java.util.function.Function;

import com.google.common.truth.Truth;
import com.nepu.metro.common.producer.Producer;
import com.nepu.metro.ruleEngine.events.model.TravelEvent;
import com.nepu.metro.ruleEngine.events.model.Zone;
import com.nepu.metro.ruleEngine.rules.GroovyRule;
import com.nepu.metro.ruleEngine.rules.GroovyRulesUtils;
import com.nepu.metro.ruleEngine.rules.RuleScript;
import com.nepu.metro.ruleEngine.rules.RulesContainer;
import com.tngtech.java.junit.dataprovider.DataProvider;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.MethodSource;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.Spy;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;

@RunWith(SpringRunner.class)
@SpringBootTest
class GroovyTravelEventHandlerFareCappingTest {

    Producer<TravelEvent> producer;

    @Mock
    RulesContainer rulesContainer;

    @Spy
    Function<TravelEvent, TravelEvent> dbFairCapDetailsLoader;

    @Spy
    @Autowired
    GroovyRulesUtils utils;

    List<GroovyRule> rules = Arrays.asList(

    //Fair capping Rule
    new GroovyRule(new RuleScript(
        "import com.nepu.metro.ruleEngine.events.model.Zone;" +
        "Date enteredAt = event.enteredAt;" +
        "Date exitedAt = event.exitedAt;" +
        "Zone from = event.from;" +
        "Zone to = event.to;" +

        "double farthestJourneyDailyCap = event.meta.get(\"farthestJourneyDailyCap\");" +
        "double farthestJourneyWeeklyCap = event.meta.get(\"farthestJourneyWeeklyCap\");" +
        "double totalDailyTravelSpent = event.meta.get(\"totalDailyTravelSpent\");" +
        "double totalWeeklyTravelSpent = event.meta.get(\"totalWeeklyTravelSpent\");" +

        "farthestJourneyDailyCap = utils.getFarthestJourneyDailyCap(from, to, farthestJourneyDailyCap);" +
        "event.meta.put(\"farthestJourneyDailyCap\", farthestJourneyDailyCap);" +
        "farthestJourneyWeeklyCap = utils.getFarthestJourneyWeeklyCap(from, to, farthestJourneyWeeklyCap);" +
        "event.meta.put(\"farthestJourneyWeeklyCap\", farthestJourneyWeeklyCap);" +


        "if (utils.getRemainingDailyCap(event.cost, totalDailyTravelSpent, farthestJourneyDailyCap) != event.cost) { " +

        "     event.cost = utils.getRemainingDailyCap(event.cost, totalDailyTravelSpent, farthestJourneyDailyCap); "+
        "     return true;" +
        "} else if (utils.getRemainingWeeklyCap(event.cost, totalWeeklyTravelSpent, farthestJourneyWeeklyCap) != event.cost) { " +

        "     event.cost = utils.getRemainingWeeklyCap(event.cost, totalWeeklyTravelSpent, farthestJourneyWeeklyCap); "+
        "     return true;" +
        "} else { " +

        "      return false; }",

"return true;"))

    );


    @InjectMocks
    GroovyTravelEventHandler handler;

    TravelEvent result;

    @BeforeEach
    public void setup() {

        //Hold the result
        this.producer = new Producer<>() {

            @Override
            public void submit(TravelEvent message) {
                result = message;
            }

            @Override
            public void close() {}
        };
        //Can not do both constructor args and setters injection; do setters injection manually
        handler.producer = this.producer;
        handler.rulesContainer = rulesContainer;
        handler.utils = utils;
    }

    @DataProvider
    public static Object[][] testData() {

        return new Object[][] {
                //Daily cap reached
                { 17, "10:20", "10:40", Zone.ZONE_1, Zone.ZONE_2, 35.0d, 115.0d, 120.0d, 400d, 600.0d, 5d},
                //Weekly cap reached
                { 17, "10:20", "10:40", Zone.ZONE_1, Zone.ZONE_2, 35.0d, 15.0d, 120.0d, 580d, 600.0d, 20d},
                //Daily/Weekly cap not reached
                { 17, "10:20", "10:40", Zone.ZONE_1, Zone.ZONE_2, 35.0d, 50.0d, 120.0d, 400d, 600.0d, 35d},
        };
    }

    @ParameterizedTest
    @MethodSource("testData")
    public void testFareCapping(int dayOfMonth, String entered, String exited, Zone from, Zone to, double calculatedFare,
                                double totalDailyTravelSpent, double farthestJourneyDailyCap,
                                double totalWeeklyTravelSpent, double farthestJourneyWeeklyCap,
                                double finalFare) {

        TravelEvent event = new TravelEvent();
        LocalDate date = LocalDate.of(2021, Month.MAY, dayOfMonth);
        LocalDateTime enteredAt = LocalDateTime.of(date, LocalTime.parse(entered));
        LocalDateTime exitedAt = LocalDateTime.of(date, LocalTime.parse(exited));
        event.enteredAt = Date.from(enteredAt.atZone(ZoneId.systemDefault()).toInstant());
        event.exitedAt = Date.from(exitedAt.atZone(ZoneId.systemDefault()).toInstant());
        event.from = from;
        event.to = to;
        event.cost = calculatedFare;

        //TODO Set from DB
        TravelEvent dbFairCapDetailsLoadedEvent = event;
        dbFairCapDetailsLoadedEvent.meta.put("totalDailyTravelSpent", totalDailyTravelSpent);
        dbFairCapDetailsLoadedEvent.meta.put("farthestJourneyDailyCap", farthestJourneyDailyCap);
        dbFairCapDetailsLoadedEvent.meta.put("totalWeeklyTravelSpent", totalWeeklyTravelSpent);
        dbFairCapDetailsLoadedEvent.meta.put("farthestJourneyWeeklyCap", farthestJourneyWeeklyCap);
        Mockito.doReturn(dbFairCapDetailsLoadedEvent).when(dbFairCapDetailsLoader).apply(event);

        Mockito.doReturn(rules).when(rulesContainer).getRules();
        handler.handle(dbFairCapDetailsLoadedEvent);

        //Sunday peak hour, so added additional 5 rupees
        Truth.assertThat(result.cost).isEqualTo(finalFare);
    }
}