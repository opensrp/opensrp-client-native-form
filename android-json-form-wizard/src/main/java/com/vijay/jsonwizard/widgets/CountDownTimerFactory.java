package com.vijay.jsonwizard.widgets;

import android.content.Context;
import android.graphics.Color;
import android.media.Ringtone;
import android.media.RingtoneManager;
import android.net.Uri;
import android.os.CountDownTimer;
import android.provider.Settings;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.TextView;

import com.emredavarci.circleprogressbar.CircleProgressBar;
import com.vijay.jsonwizard.R;
import com.vijay.jsonwizard.constants.JsonFormConstants;
import com.vijay.jsonwizard.fragments.JsonFormFragment;
import com.vijay.jsonwizard.interfaces.CommonListener;
import com.vijay.jsonwizard.interfaces.FormWidgetFactory;
import com.vijay.jsonwizard.utils.FormUtils;

import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;
import java.util.Locale;
import java.util.concurrent.TimeUnit;

public class CountDownTimerFactory implements FormWidgetFactory {

    private View rootLayout;
    private TextView labelView;
    private CircleProgressBar progressBar;
    private int elapsedCount = 0;
    private int progressBarMaxValue = 100;
    private long millis;
    private long interval;

    @Override
    public List<View> getViewsFromJson(String stepName, Context context, JsonFormFragment formFragment, JSONObject jsonObject, CommonListener listener, boolean popup) throws Exception {
        List<View> views = new ArrayList<>(1);
        rootLayout = LayoutInflater.from(context).inflate(getLayout(), null);
        setWidgetTags(jsonObject);
        initializeViewConfigs(jsonObject);
        formatWidget(jsonObject, context);
        startCountDown(millis, interval, context);
        views.add(rootLayout);
        return views;
    }

    @Override
    public List<View> getViewsFromJson(String stepName, Context context, JsonFormFragment formFragment, JSONObject jsonObject, CommonListener listener) throws Exception {
        return getViewsFromJson(stepName, context, formFragment, jsonObject, listener, false);
    }

    private void setWidgetTags(JSONObject jsonObject) {
        String key = jsonObject.optString(JsonFormConstants.KEY, "");
        String openMrsEntityParent = jsonObject.optString(JsonFormConstants.OPENMRS_ENTITY_PARENT, "");
        String openMrsEntity = jsonObject.optString(JsonFormConstants.OPENMRS_ENTITY, "");
        String openMrsEntityId = jsonObject.optString(JsonFormConstants.OPENMRS_ENTITY_ID, "");

        rootLayout.setTag(key);
        rootLayout.setTag(openMrsEntityParent);
        rootLayout.setTag(openMrsEntity);
        rootLayout.setTag(openMrsEntityId);
    }

    private void initializeViewConfigs(JSONObject jsonObject) {
        labelView = rootLayout.findViewById(R.id.label);
        progressBar = rootLayout.findViewById(R.id.progressBar);

        String label = jsonObject.optString(JsonFormConstants.LABEL, "");

        labelView.setText(label);

        progressBar.setStrokeWidthDimension(20);
        progressBar.setMaxValue(progressBarMaxValue);

        String countdownTimeUnit = jsonObject.optString(JsonFormConstants.COUNTDOWN_TIME_UNIT, JsonFormConstants.DEFAULT_COUNTDOWN_TIME_UNIT);
        String countdownTimeValue = jsonObject.optString(JsonFormConstants.COUNTDOWN_TIME_VALUE, "0");
        String countdownInterval = jsonObject.optString(JsonFormConstants.COUNTDOWN_INTERVAL, "1000");
        long time = Long.parseLong(countdownTimeValue);
        if (countdownTimeUnit.equals(JsonFormConstants.DEFAULT_COUNTDOWN_TIME_UNIT)) {
            millis = time * 1000;
        } else if (countdownTimeUnit.equals(JsonFormConstants.MINUTES_COUNTDOWN_TIME_UNIT)) {
            millis = time * 60 * 1000;
        }
        interval = Long.parseLong(countdownInterval);
    }

    private void formatWidget(JSONObject jsonObject, Context context) {

        String labelTextSize = jsonObject.optString(JsonFormConstants.LABEL_TEXT_SIZE, "8sp");
        String labelTextColor = jsonObject.optString(JsonFormConstants.LABEL_TEXT_COLOR, "#535F67");
        String progressBarBackgroundColor = jsonObject.optString(JsonFormConstants.PROGRESSBAR_BACKGROUND_COLOR, "#B6BBBE");
        String progressBarColor = jsonObject.optString(JsonFormConstants.PROGRESSBAR_COLOR, "#535F67");
        String progressBarTextColor = jsonObject.optString(JsonFormConstants.PROGRESSBAR_TEXT_COLOR, "#535F67");

        labelView.setTextSize(FormUtils.getValueFromSpOrDpOrPx(labelTextSize, context));
        labelView.setTextColor(Color.parseColor(labelTextColor));

        progressBar.setBackgroundColor(progressBarBackgroundColor);
        progressBar.setProgressColor(progressBarColor);
        progressBar.setTextColor(progressBarTextColor);
    }

    private int getLayout() {
        return R.layout.native_form_countdown_timer;
    }

    /**
     * Count down to the end of the time specified
     *
     * @param millis            The time in milliseconds to count down to
     * @param countdownInterval The intervals for running the countdown
     */
    private void startCountDown(final long millis, long countdownInterval, final Context context) {
        new CountDownTimer(millis, countdownInterval) {
            @Override
            public void onTick(long millisUntilFinished) {
                elapsedCount++;
                progressBar.setText(getFormattedTimeText(millisUntilFinished));
                int progress = (int) (elapsedCount * 100 / (millis / 1000));
                progressBar.setProgress(progress);
            }

            @Override
            public void onFinish() {
                elapsedCount = 0;
                progressBar.setText(getFormattedTimeText(0));
                progressBar.setProgress(progressBarMaxValue);
                ringAlarm(context);
            }
        }.start();
    }

    private void ringAlarm(Context context) {
        Uri alert = RingtoneManager.getDefaultUri(RingtoneManager.TYPE_ALARM);
        Ringtone alarmTone = (alert == null) ? RingtoneManager.getRingtone(context, Settings.System.DEFAULT_RINGTONE_URI) : RingtoneManager.getRingtone(context, alert);
        if (!alarmTone.isPlaying()) {
            alarmTone.play();
        }
    }


    private String getFormattedTimeText(long timeValue) {
        return String.format(Locale.getDefault(), "%02d:%02d",
                TimeUnit.MILLISECONDS.toMinutes(timeValue), TimeUnit.MILLISECONDS.toSeconds(timeValue));
    }
}
