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
class GroovyTravelEventHandlerPeakTimeTest {

    Producer<TravelEvent> producer;

    @Mock
    RulesContainer rulesContainer;

    @Spy
    Function<TravelEvent, TravelEvent> dbBaseFareDetailsLoader;

    @Spy
    @Autowired
    GroovyRulesUtils utils;

    List<GroovyRule> rules = Arrays.asList(

        //Peak Hour Rule
        new GroovyRule(new RuleScript(
                    "import com.nepu.metro.ruleEngine.events.model.Zone;" +
                    "Date enteredAt = event.enteredAt;" +
                    "Date exitedAt = event.exitedAt;" +
                    "Zone from = event.from;" +
                    "Zone to = event.to;" +

                    "if (utils.isPeakMorningHours(enteredAt) || utils.isPeakMorningHours(exitedAt)) { " +

                    "      return true;" +
                    "} else if ((utils.isPeakEveningHours(enteredAt) || utils.isPeakEveningHours(exitedAt)) && " +
                    "           ( to.equals(new Zone(\"Zone_1\")) && !from.equals(new Zone(\"Zone_1\")) )) { " +

                    "      return true;" +
                    "} else { " +

                    "      return false; }",

            " event.cost = event.cost + 5; return true;"))

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
                //Friday peak hour
                { 21, "07:00", "10:00", Zone.ZONE_1, Zone.ZONE_2, 30.0d, 35.0d },//Add additional cost for peak time
                //Sunday peak hour
                { 23, "10:00", "11:00", Zone.ZONE_1, Zone.ZONE_2, 30.0d, 35.0d },//Add additional cost for peak time
                //Friday off-peak hour
                { 21, "11:30", "12:00", Zone.ZONE_1, Zone.ZONE_2, 30.0d, 30.0d },//No additional cost for off peak time
                //Sunday off-peak hour
                { 23, "11:00", "12:00", Zone.ZONE_1, Zone.ZONE_2, 30.0d, 30.0d }, //No additional cost for off peak time

                //Friday: starting peak hour, but ended off peak
                { 21, "07:10", "12:00", Zone.ZONE_1, Zone.ZONE_2, 30.0d, 35.0d },//Add additional cost for peak time
        };
    }

    @ParameterizedTest
    @MethodSource("testData")
    public void testPeakHours(int dayOfMonth, String entered, String exited, Zone from, Zone to, double baseFare,
                              double finalFare) {

        TravelEvent event = new TravelEvent();
        //Friday peak hour, so add additional 5 rupees
        LocalDate date = LocalDate.of(2021, Month.MAY, dayOfMonth);
        LocalDateTime enteredAt = LocalDateTime.of(date, LocalTime.parse(entered));
        LocalDateTime exitedAt = LocalDateTime.of(date, LocalTime.parse(exited));
        event.enteredAt = Date.from(enteredAt.atZone(ZoneId.systemDefault()).toInstant());
        event.exitedAt = Date.from(exitedAt.atZone(ZoneId.systemDefault()).toInstant());
        event.from = from;
        event.to = to;

        //TODO Set from DB
        TravelEvent dbFairCapDetailsLoadedEvent = event;
        dbFairCapDetailsLoadedEvent.cost = baseFare; //Base fare from Zone_1 to Zone_2
        Mockito.doReturn(dbFairCapDetailsLoadedEvent).when(dbBaseFareDetailsLoader).apply(event);

        Mockito.doReturn(rules).when(rulesContainer).getRules();
        handler.handle(event);

        //Sunday peak hour, so added additional 5 rupees
        Truth.assertThat(result.cost).isEqualTo(finalFare);
    }
}