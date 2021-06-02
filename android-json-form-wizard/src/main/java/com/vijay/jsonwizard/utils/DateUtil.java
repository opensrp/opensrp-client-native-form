package com.vijay.jsonwizard.utils;

import android.content.Context;

import com.vijay.jsonwizard.R;

import java.util.Calendar;
import java.util.Locale;
import java.util.concurrent.TimeUnit;

import timber.log.Timber;

/**
 * Created by ndegwamartin on 2020-03-21.
 */
public class DateUtil {

    public static long getDurationTimeDifference(String date, String endDate) {
        if (date != null) {
            Calendar calendar = FormUtils.getDate(date);
            calendar.set(Calendar.HOUR_OF_DAY, 0);
            calendar.set(Calendar.MINUTE, 0);
            calendar.set(Calendar.SECOND, 0);
            calendar.set(Calendar.MILLISECOND, 0);

            Calendar now = Calendar.getInstance();
            if (endDate != null) {
                try {
                    now = FormUtils.getDate(endDate);
                } catch (Exception e) {
                    Timber.e(e, " --> getDuration");
                }
            }
            now.set(Calendar.HOUR_OF_DAY, 0);
            now.set(Calendar.MINUTE, 0);
            now.set(Calendar.SECOND, 0);
            now.set(Calendar.MILLISECOND, 0);

            long timeDiff = Math.abs(now.getTimeInMillis() - calendar.getTimeInMillis());
            return timeDiff;
        }
        return 0l;
    }

    public static String getDuration(long timeDiff, Locale locale, Context context) {
        String duration = "";
        if (timeDiff >= 0
                && timeDiff <= TimeUnit.MILLISECONDS.convert(13, TimeUnit.DAYS)) {
            // Represent in days
            long days = TimeUnit.DAYS.convert(timeDiff, TimeUnit.MILLISECONDS);
            duration = String.format(locale, context.getResources().getString(R.string.x_days), days);
        } else if (timeDiff > TimeUnit.MILLISECONDS.convert(13, TimeUnit.DAYS)
                && timeDiff <= TimeUnit.MILLISECONDS.convert(97, TimeUnit.DAYS)) {
            // Represent in weeks and days
            int weeks = (int) Math.floor((float) timeDiff /
                    TimeUnit.MILLISECONDS.convert(7, TimeUnit.DAYS));
            int days = (int) Math.floor((float) (timeDiff -
                    TimeUnit.MILLISECONDS.convert(weeks * 7, TimeUnit.DAYS)) /
                    TimeUnit.MILLISECONDS.convert(1, TimeUnit.DAYS));

            if (days >= 7) {
                days = 0;
                weeks++;
            }

            if (days > 0) {
                duration = String.format(locale, context.getResources().getString(R.string.x_weeks_days), weeks, days);
            } else {
                duration = String.format(locale, context.getResources().getString(R.string.x_weeks), weeks);
            }

        } else if (timeDiff > TimeUnit.MILLISECONDS.convert(97, TimeUnit.DAYS)
                && timeDiff <= TimeUnit.MILLISECONDS.convert(363, TimeUnit.DAYS)) {
            // Represent in months and weeks
            int months = (int) Math.floor((float) timeDiff /
                    TimeUnit.MILLISECONDS.convert(30, TimeUnit.DAYS));
            int weeks = (int) Math.floor((float) (timeDiff - TimeUnit.MILLISECONDS.convert(
                    months * 30, TimeUnit.DAYS)) /
                    TimeUnit.MILLISECONDS.convert(7, TimeUnit.DAYS));

            if (weeks >= 4) {
                weeks = 0;
                months++;
            }

            if (months < 12) {
                if (weeks > 0) {
                    duration = String.format(locale, context.getResources().getString(R.string.x_months_weeks), months, weeks);
                } else {
                    duration = String.format(locale, context.getResources().getString(R.string.x_months), months);
                }
            } else {
                duration = String.format(locale, context.getResources().getString(R.string.x_years), 1);
            }
        } else {
            // Represent in years and months
            int years = (int) Math.floor((float) timeDiff
                    / TimeUnit.MILLISECONDS.convert(365, TimeUnit.DAYS));
            int months = (int) Math.floor((float) (timeDiff -
                    TimeUnit.MILLISECONDS.convert(years * 365, TimeUnit.DAYS)) /
                    TimeUnit.MILLISECONDS.convert(30, TimeUnit.DAYS));

            if (months >= 12) {
                months = 0;
                years++;
            }

            if (months > 0) {
                duration = String.format(locale, context.getResources().getString(R.string.x_years_months), years, months);
            } else {
                duration = String.format(locale, context.getResources().getString(R.string.x_years), years);
            }
        }

        return duration;

    }
}
