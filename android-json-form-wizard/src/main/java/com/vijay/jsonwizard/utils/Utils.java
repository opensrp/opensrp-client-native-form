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

import com.vijay.jsonwizard.constants.JsonFormConstants;
import com.vijay.jsonwizard.event.BaseEvent;
import com.vijay.jsonwizard.widgets.DatePickerFactory;

import org.greenrobot.eventbus.EventBus;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.text.ParseException;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
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

    public static int pixelToDp(int dpValue, Context context) {
        float dpRatio = context.getResources().getDisplayMetrics().density;
        float pixelForDp =  dpValue * dpRatio;

        return (int) pixelForDp;
    }

    public static void postEvent(BaseEvent event) {
        EventBus.getDefault().post(event);
    }

    public List<String> createExpansionPanelChildren(JSONArray jsonArray) throws JSONException {
        List<String> stringList = new ArrayList<>();
        String label;
        for (int i = 0; i < jsonArray.length(); i++) {
            if (!jsonArray.isNull(i)) {
                JSONObject jsonObject = jsonArray.getJSONObject(i);
                if (jsonObject.has(JsonFormConstants.VALUES) && jsonObject.has(JsonFormConstants.LABEL) &&
                        !"".equals(jsonObject.getString(JsonFormConstants.LABEL))) {
                    //Get label and replace any colon in some labels. Not needed at this point
                    label = jsonObject.getString(JsonFormConstants.LABEL).replace(":", "");
                    stringList.add(label + ":" + getStringValue(jsonObject));
                }
            }
        }

        return stringList;
    }

    private String getStringValue(JSONObject jsonObject) throws JSONException {
        StringBuilder value = new StringBuilder();
        if (jsonObject != null) {
            JSONArray jsonArray = jsonObject.getJSONArray(JsonFormConstants.VALUES);
            for (int i = 0; i < jsonArray.length(); i++) {
                String stringValue = jsonArray.getString(i);
                value.append(getValueFromSecondaryValues(stringValue));
                value.append(", ");
            }
        }

        return value.toString().replaceAll(", $", "");
    }

    private String getValueFromSecondaryValues(String itemString) {
        String[] strings = itemString.split(":");
        return strings.length > 1 ? strings[1] : strings[0];
    }

}
