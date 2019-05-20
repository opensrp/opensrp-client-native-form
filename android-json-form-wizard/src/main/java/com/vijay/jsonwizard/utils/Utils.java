package com.vijay.jsonwizard.utils;

import android.app.ProgressDialog;
import android.content.Context;
import android.support.annotation.StringRes;
import android.support.design.widget.Snackbar;
import android.support.v4.util.TimeUtils;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.widget.Toast;

import com.vijay.jsonwizard.widgets.DatePickerFactory;

import java.text.ParseException;
import java.util.Calendar;
import java.util.Date;
import java.util.concurrent.TimeUnit;

public class Utils {

    private static final String TAG = Utils.class.getCanonicalName();

    private static ProgressDialog progressDialog;

    public static void showToast(Context context, String message) {
        Toast.makeText(context, message, Toast.LENGTH_LONG).show();
    }

    public static void showSnackBar(View view, String message) {
        Snackbar.make(view, message, Snackbar.LENGTH_LONG).show();
    }

    public static Date getDateFromString(String dtStart) {
        if (!"0".equals(dtStart)) {
            try {
                Date date = DatePickerFactory.DATE_FORMAT.parse(dtStart);
                return date;
            } catch (ParseException e) {
                Log.e(TAG, e.getMessage(), e);
                return null;
            }
        } else {
            return null;
        }
    }

    public static String getStringFromDate(Date date) {
        try {
            return DatePickerFactory.DATE_FORMAT.format(date);
        } catch (Exception e) {
            Log.e(TAG, e.getMessage(), e);
            return null;
        }
    }

    public static String reverseDateString(String str, String delimiter) {
        String[] strr = str.split(delimiter);
        return strr[2] + "-" + strr[1] + "-" + strr[0];
    }

    public static String getDuration(String date) {
        return getDuration(date, null);
    }

    public static String getDuration(String date, String endDate) {
        if (!TextUtils.isEmpty(date)) {
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
                    Log.e(TAG, e.getMessage(), e);
                }
            }
            now.set(Calendar.HOUR_OF_DAY, 0);
            now.set(Calendar.MINUTE, 0);
            now.set(Calendar.SECOND, 0);
            now.set(Calendar.MILLISECOND, 0);

            long timeDiff = Math.abs(now.getTimeInMillis() - calendar.getTimeInMillis());
            StringBuilder builder = new StringBuilder();
            TimeUtils.formatDuration(timeDiff, builder);
            String duration = "";
            if (timeDiff >= 0 && timeDiff <= TimeUnit.MILLISECONDS.convert(13, TimeUnit.DAYS)) {
                // Represent in days
                long days = TimeUnit.DAYS.convert(timeDiff, TimeUnit.MILLISECONDS);
                duration = days + "d";
            } else if (timeDiff > TimeUnit.MILLISECONDS.convert(13, TimeUnit.DAYS) &&
                    timeDiff <= TimeUnit.MILLISECONDS.convert(97, TimeUnit.DAYS)) {
                // Represent in weeks and days
                int weeks = (int) Math.floor((float) timeDiff / TimeUnit.MILLISECONDS.convert(7, TimeUnit.DAYS));
                int days = (int) Math.floor((float) (timeDiff - TimeUnit.MILLISECONDS.convert(weeks * 7, TimeUnit.DAYS)) /
                        TimeUnit.MILLISECONDS.convert(1, TimeUnit.DAYS));

                if (days >= 7) {
                    days = 0;
                    weeks++;
                }

                duration = weeks + "w";
                if (days > 0) {
                    duration += " " + days + "d";
                }
            } else if (timeDiff > TimeUnit.MILLISECONDS.convert(97, TimeUnit.DAYS) &&
                    timeDiff <= TimeUnit.MILLISECONDS.convert(363, TimeUnit.DAYS)) {
                // Represent in months and weeks
                int months = (int) Math.floor((float) timeDiff / TimeUnit.MILLISECONDS.convert(30, TimeUnit.DAYS));
                int weeks = (int) Math.floor((float) (timeDiff - TimeUnit.MILLISECONDS.convert(months * 30, TimeUnit.DAYS)) /
                        TimeUnit.MILLISECONDS.convert(7, TimeUnit.DAYS));

                if (weeks >= 4) {
                    weeks = 0;
                    months++;
                }

                if (months < 12) {
                    duration = months + "m";
                    if (weeks > 0 && months < 12) {
                        duration += " " + weeks + "w";
                    }
                } else if (months >= 12) {
                    duration = "1y";
                }
            } else {
                // Represent in years and months
                int years = (int) Math.floor((float) timeDiff / TimeUnit.MILLISECONDS.convert(365, TimeUnit.DAYS));
                int months = (int) Math
                        .floor((float) (timeDiff - TimeUnit.MILLISECONDS.convert(years * 365, TimeUnit.DAYS)) /
                                TimeUnit.MILLISECONDS.convert(30, TimeUnit.DAYS));

                if (months >= 12) {
                    months = 0;
                    years++;
                }

                duration = years + "y";
                if (months > 0) {
                    duration += " " + months + "m";
                }
            }

            return duration;
        }
        return null;
    }

    public static void showProgressDialog(@StringRes int title, @StringRes int message, Context context) {
        progressDialog = new ProgressDialog(context);
        progressDialog.setCancelable(false);
        progressDialog.setTitle(context.getString(title));
        progressDialog.setMessage(context.getString(message));
        progressDialog.show();
    }

    public static void hideProgressDialog() {
        if (progressDialog != null) {
            progressDialog.dismiss();
        }
    }

    public static String convertArabicToEnglish(String str) {
        String answer = str;
        answer = answer.replace("١", "1");
        answer = answer.replace("٢", "2");
        answer = answer.replace("٣", "3");
        answer = answer.replace("٤", "4");
        answer = answer.replace("٥", "5");
        answer = answer.replace("٦", "6");
        answer = answer.replace("٧", "7");
        answer = answer.replace("٨", "8");
        answer = answer.replace("٩", "9");
        answer = answer.replace("٠", "0");
        return answer;
    }

    public static String convertEnglishToArabic(String str) {
        String answer = str;
        answer = answer.replace("1","١");
        answer = answer.replace("2","٢");
        answer = answer.replace("3","٣");
        answer = answer.replace("4","٤");
        answer = answer.replace("5","٥");
        answer = answer.replace("6","٦");
        answer = answer.replace("7","٧");
        answer = answer.replace("8","٨");
        answer = answer.replace("9","٩");
        answer = answer.replace("0","٠");
        return answer;
    }
}
