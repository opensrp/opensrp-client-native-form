package com.vijay.jsonwizard.shadow;

import com.vijay.jsonwizard.rules.RulesEngineDateUtil;

import org.joda.time.LocalDateTime;
import org.robolectric.annotation.Implementation;
import org.robolectric.annotation.Implements;

import java.time.Clock;
import java.time.Instant;
import java.time.ZoneId;

import static com.vijay.jsonwizard.rules.RulesEngineHelperTest.TEST_TIME_EXPECTED;

@Implements(RulesEngineDateUtil.class)
public class ShadowRulesEngineDateUtil {

    @Implementation
    public String getDateTimeToday() {
        Clock clock = Clock.fixed(Instant.parse(TEST_TIME_EXPECTED), ZoneId.systemDefault());
        return new LocalDateTime(clock.millis()).toString("yyyy-MM-dd HH:mm:ss");
    }
}
