package com.nepu.metro.ruleEngine.events.handler;

import com.google.common.truth.Truth;
import com.nepu.metro.common.producer.Producer;
import com.nepu.metro.ruleEngine.events.model.TravelEvent;
import com.nepu.metro.ruleEngine.events.model.Zone;
import com.nepu.metro.ruleEngine.rules.GroovyRule;
import com.nepu.metro.ruleEngine.rules.GroovyRulesUtils;
import com.nepu.metro.ruleEngine.rules.RuleScript;
import com.nepu.metro.ruleEngine.rules.RulesContainer;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.Spy;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import java.time.*;
import java.util.Arrays;
import java.util.Date;
import java.util.List;
import java.util.function.Function;

//@ExtendWith(MockitoExtension.class)
@SpringBootTest
class GroovyTravelEventHandlerTest {

    Producer<TravelEvent> producer;

    @Mock
    RulesContainer rulesContainer;

    @Spy
    Function<TravelEvent, TravelEvent> dbUserDetailsLoader;

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
                            "           ( to.equals(new Zone(\"Zone_1\")) && !from.equals(new Zone(\"Zone_1\")))) { " +

                            "      return true;" +
                            "}",
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
        handler.producer = this.producer;
        handler.rulesContainer = rulesContainer;
        handler.utils = utils;
    }

    @Test
    void testWeekendPeakHoursTravel() {

        TravelEvent event = new TravelEvent();
        Mockito.doReturn(event).when(dbUserDetailsLoader).apply(event);

        //Sunday peak hour
        LocalDate date = LocalDate.of(2021, Month.MAY, 16);
        LocalDateTime enteredAt = LocalDateTime.of(date, LocalTime.of(10, 0));
        LocalDateTime exitedAt = LocalDateTime.of(date, LocalTime.of(11, 0));
        event.enteredAt = Date.from(enteredAt.atZone(ZoneId.systemDefault()).toInstant());
        event.exitedAt = Date.from(exitedAt.atZone(ZoneId.systemDefault()).toInstant());
        event.from = Zone.ZONE_1;
        event.to = Zone.ZONE_2;
        event.cost = 35; //Base fare from Zone_1 to Zone_2

        Mockito.doReturn(rules).when(rulesContainer).getRules();
        handler.handle(event);
        Truth.assertThat(result.cost).isAtMost(40);
    }
}