package com.vijay.jsonwizard.rules;

import com.vijay.jsonwizard.utils.Utils;

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
}
