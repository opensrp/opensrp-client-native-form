package com.vijay.jsonwizard.rules;

import java.util.List;

/**
 * Created by ndegwamartin on 17/12/2018.
 * This class is used by the rules engine while it parses configurations written in teh yaml.
 * On can for instance reference the get difference days in 'calculations' like helper.getDifferenceDays("2018-12-20") and use the value like any other. See native form sample app
 */
public class RulesEngineHelper {

    private RulesEngineDateUtil rulesEngineDateUtil = new RulesEngineDateUtil();

    public long getDifferenceDays(String dateString) {
        return rulesEngineDateUtil.getDifferenceDays(dateString);
    }

    public long getDifferenceDays(String dateString, String dateString2) {
        return rulesEngineDateUtil.getDifferenceDays(dateString, dateString2);
    }

    public String addDuration(String dateString, String durationString) {
        return rulesEngineDateUtil.addDuration(dateString, durationString);
    }

    public String subtractDuration(String dateString, String durationString) {
        return rulesEngineDateUtil.subtractDuration(dateString, durationString);
    }

    public String getDuration(String date) {
        return rulesEngineDateUtil.getDuration(date);
    }

    public String getDuration(String date, String endDate) {
        return rulesEngineDateUtil.getDuration(date, endDate);
    }

    public String getWeeksAndDaysFromDays(Integer days) {
        return rulesEngineDateUtil.getWeeksAndDaysFromDays(days);
    }

    public String formatDate(String dateString, String duration) {
        return rulesEngineDateUtil.formatDate(dateString, duration);
    }

    public String addDuration(String durationString) {
        return rulesEngineDateUtil.addDuration(durationString);
    }

    public String subtractDuration(String durationString) {
        return rulesEngineDateUtil.subtractDuration(durationString);
    }

    public String minDate(String minimumDate) {
        return rulesEngineDateUtil.minDate(minimumDate);
    }

    public String maxDate(String maximumDate) {
        return rulesEngineDateUtil.maxDate(maximumDate);
    }

    public String getDOBFromAge(Integer age) {
        return rulesEngineDateUtil.getDOBFromAge(age);
    }

    public String getDateToday() {
        return rulesEngineDateUtil.getDateToday();
    }

    public String getDateTimeToday() {
        return rulesEngineDateUtil.getDateTimeToday();
    }

    //A secondary value has the format key:name e.g. ultrasound_done:yes
    public String getSecondaryValue(String value) {
        if (value.contains(":")) {
            String[] valArray = value.split(":");
            return valArray[1];
        } else {
            return value;
        }
    }

    public String ifNull(String value, String defaultIfNull) {
        return value == null || value.isEmpty() ? defaultIfNull : value;
    }

    public String getNonBlankValue(List<String> values) {
        if (values != null) {
            for (String value : values) {
                if (value != null && !value.isEmpty()) {
                    return value;
                }
            }
        }
        return "";
    }

}
