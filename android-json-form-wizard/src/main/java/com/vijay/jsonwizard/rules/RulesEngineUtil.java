package com.vijay.jsonwizard.rules;

import android.support.annotation.NonNull;
import android.text.TextUtils;

import com.vijay.jsonwizard.utils.FormUtils;
import com.vijay.jsonwizard.utils.Utils;

import org.joda.time.Days;
import org.joda.time.LocalDate;
import org.joda.time.Months;
import org.joda.time.Weeks;
import org.joda.time.Years;

import java.util.Calendar;
import java.util.Date;
import java.util.concurrent.TimeUnit;

/**
 * Created by ndegwamartin on 17/12/2018.
 */
public class RulesEngineUtil {

    public long getDifferenceDays(String dateString) {

        Date date = Utils.getDateFromString(dateString);

        if (date != null) {

            long msDiff = Calendar.getInstance().getTimeInMillis() - date.getTime();

            return Math.abs(TimeUnit.MILLISECONDS.toDays(msDiff));
        } else {
            return 0;
        }

    }

    public long getDifferenceDays(String dateString, String dateString2) {

        Date date = Utils.getDateFromString(dateString);

        Date date2 = Utils.getDateFromString(dateString2);

        if (date != null && date2 != null) {

            long msDiff = date.getTime() - date2.getTime();

            return Math.abs(TimeUnit.MILLISECONDS.toDays(msDiff));
        } else {
            return 0;
        }

    }

    /**
     * @param dateString
     * @param durationString
     * @return String with date
     */
    public String addDuration(String dateString, String durationString) {

        LocalDate date = new LocalDate(Utils.reverseDateString(dateString, "-"));

        String[] durationArr = getDurationArray(durationString);

        for (int i = 0; i < durationArr.length; i++) {

            char suffix = durationArr[i].charAt(durationArr[i].length() - 1);
            switch (suffix) {
                case 'd':
                    date = date.plusDays(getDurationValue(durationArr[i]));

                    break;
                case 'w':
                    date = date.plusWeeks(getDurationValue(durationArr[i]));

                    break;
                case 'm':
                    date = date.plusMonths(getDurationValue(durationArr[i]));

                    break;
                case 'y':
                    date = date.plusYears(getDurationValue(durationArr[i]));
                    break;

                default:
                    break;

            }
        }

        return date.toString(FormUtils.NATIIVE_FORM_DATE_FORMAT_PATTERN);
    }

    @NonNull
    private String[] getDurationArray(String durationString) {
        String cleanDurationString = durationString.trim().toLowerCase();

        return cleanDurationString.contains("-") ? cleanDurationString.trim().split("-") : new String[]{cleanDurationString};
    }


    /**
     * @param dateString
     * @param durationString
     * @return String with date
     */
    public String subtractDuration(String dateString, String durationString) {

        LocalDate date = new LocalDate(Utils.reverseDateString(dateString, "-"));

        String[] durationArr = getDurationArray(durationString);

        for (int i = 0; i < durationArr.length; i++) {

            char suffix = durationArr[i].charAt(durationArr[i].length() - 1);
            switch (suffix) {
                case 'd':
                    date = date.minusDays(getDurationValue(durationArr[i]));

                    break;
                case 'w':
                    date = date.minusWeeks(getDurationValue(durationArr[i]));

                    break;
                case 'm':
                    date = date.minusMonths(getDurationValue(durationArr[i]));

                    break;
                case 'y':
                    date = date.minusYears(getDurationValue(durationArr[i]));
                    break;

                default:
                    break;

            }

        }

        return date.toString(FormUtils.NATIIVE_FORM_DATE_FORMAT_PATTERN);
    }

    public String getDuration(String date) {
        return Utils.getDuration(date);

    }

    public String getWeeksAndDaysFromDays(Integer days) {

        double weeks = Math.round(Math.floor(days / 7));
        Integer dayz = days % 7;

        return String.format("%.0f weeks %d days", weeks, dayz);
    }

    public String formatDate(String dateString, String duration) {

        LocalDate date = new LocalDate(Utils.reverseDateString(dateString, "-"));
        int result = 0;

        String cleanDuration = duration.trim().toLowerCase();

        if (cleanDuration.length() == 1) {

            switch (cleanDuration) {
                case "d":
                    result = Days.daysBetween(date, LocalDate.now()).getDays();

                    break;
                case "w":
                    result = Weeks.weeksBetween(date, LocalDate.now()).getWeeks();

                    break;
                case "m":
                    result = Months.monthsBetween(date, LocalDate.now()).getMonths();

                    break;
                case "y":
                    result = Years.yearsBetween(date, LocalDate.now()).getYears();
                    break;

                default:
                    break;

            }
        }

        return "wd".equals(cleanDuration) ? getDuration(dateString).replace("w", " weeks").replace("d", " days") : String.valueOf(Math.abs(result));

    }

    /**
     * @param durationString
     * @return String with date
     */
    public String addDuration(String durationString) {
        return addDuration((new LocalDate()).toString(FormUtils.NATIIVE_FORM_DATE_FORMAT_PATTERN), durationString);
    }

    /**
     * @param durationString
     * @return String with date
     */
    public String subtractDuration(String durationString) {
        return subtractDuration((new LocalDate()).toString(FormUtils.NATIIVE_FORM_DATE_FORMAT_PATTERN), durationString);
    }

    private Integer getDurationValue(String s) {
        return Integer.valueOf(s.substring(0, s.length() - 1));
    }

    public String minDate(String minimumDate) {

        if (!TextUtils.isEmpty(minimumDate)) {
            Calendar minDate = FormUtils.getDate(minimumDate);
            minDate.set(Calendar.HOUR_OF_DAY, 0);
            minDate.set(Calendar.MINUTE, 0);
            minDate.set(Calendar.SECOND, 0);
            minDate.set(Calendar.MILLISECOND, 0);
            return Utils.getStringFromDate(minDate.getTime());
        } else {
            return "";
        }
    }

    public String maxDate(String maximumDate) {

        if (!TextUtils.isEmpty(maximumDate)) {
            Calendar maxDate = FormUtils.getDate(maximumDate);
            maxDate.set(Calendar.HOUR_OF_DAY, 23);
            maxDate.set(Calendar.MINUTE, 59);
            maxDate.set(Calendar.SECOND, 59);
            maxDate.set(Calendar.MILLISECOND, 999);
            return Utils.getStringFromDate(maxDate.getTime());
        } else {
            return "";
        }
    }

}
