package com.vijay.jsonwizard.widgets;

import android.content.Context;
import android.media.Ringtone;
import android.media.RingtoneManager;
import android.net.Uri;
import android.os.CountDownTimer;
import android.provider.Settings;
import android.view.LayoutInflater;
import android.view.View;

import com.emredavarci.circleprogressbar.CircleProgressBar;
import com.vijay.jsonwizard.R;
import com.vijay.jsonwizard.constants.JsonFormConstants;
import com.vijay.jsonwizard.fragments.JsonFormFragment;
import com.vijay.jsonwizard.interfaces.CommonListener;
import com.vijay.jsonwizard.interfaces.FormWidgetFactory;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;
import java.util.Locale;
import java.util.concurrent.TimeUnit;

public class CountDownTimerFactory implements FormWidgetFactory {

    private View rootLayout;
    private CircleProgressBar progressBar;
    private int elapsedCount = 0;
    private int progressBarMaxValue = 100;

    @Override
    public List<View> getViewsFromJson(String stepName, Context context, JsonFormFragment formFragment, JSONObject jsonObject, CommonListener listener, boolean popup) throws Exception {
        List<View> views = new ArrayList<>(1);
        rootLayout = LayoutInflater.from(context).inflate(getLayout(), null);
        progressBar = rootLayout.findViewById(R.id.progressBar);
        progressBar.setStrokeWidthDimension(20);
        // TextView timerText = rootLayout.findViewById(R.id.text);

        setWidgetTags(jsonObject);
        formatWidget(jsonObject, context);

        String secondsString = jsonObject.optString(JsonFormConstants.COUNTDOWN_TIMER_SECONDS);
        String interValString = jsonObject.optString(JsonFormConstants.COUNTDOWN_INTERVAL);
        long seconds = (secondsString.isEmpty()) ? 0 : Long.parseLong(secondsString) * 1000;
        long defaultCountdownInterval = 1000;
        long interval = (interValString.isEmpty()) ? defaultCountdownInterval : Long.parseLong(interValString);
        progressBar.setMaxValue(progressBarMaxValue);
        startCountDown(seconds, interval, context);
        views.add(rootLayout);
        return views;
    }

    @Override
    public List<View> getViewsFromJson(String stepName, Context context, JsonFormFragment formFragment, JSONObject jsonObject, CommonListener listener) throws Exception {
        return getViewsFromJson(stepName, context, formFragment, jsonObject, listener, false);
    }

    private void setWidgetTags(JSONObject jsonObject) throws JSONException {
        String key = jsonObject.getString(JsonFormConstants.KEY);
        String openMrsEntityParent = jsonObject.getString(JsonFormConstants.OPENMRS_ENTITY_PARENT);
        String openMrsEntity = jsonObject.getString(JsonFormConstants.OPENMRS_ENTITY);
        String openMrsEntityId = jsonObject.getString(JsonFormConstants.OPENMRS_ENTITY_ID);

        rootLayout.setTag(key);
        rootLayout.setTag(openMrsEntityParent);
        rootLayout.setTag(openMrsEntity);
        rootLayout.setTag(openMrsEntityId);
    }

    private void formatWidget(JSONObject jsonObject, Context context) {
        String textSize = jsonObject.optString(JsonFormConstants.TEXT_SIZE);
        String defaultTimerTextSize = "30dp";
        if (textSize.isEmpty()) {
            textSize = defaultTimerTextSize;
        }
        // countDownView.setTextSize((FormUtils.getValueFromSpOrDpOrPx(textSize, context)));
    }

    private int getLayout() {
        return R.layout.native_form_countdown_timer;
    }

    /**
     * Start the countdown
     *
     * @param seconds           The time count down to
     * @param countdownInterval The intervals for running the countdown
     */
    private void startCountDown(final long seconds, long countdownInterval, final Context context) {
        new CountDownTimer(seconds, countdownInterval) {
            @Override
            public void onTick(long millisUntilFinished) {
                elapsedCount++;
                progressBar.setText(getFormattedTimeText(millisUntilFinished));
                int progress = (int) (elapsedCount * 100 / (seconds / 1000));
                progressBar.setProgress(progress);
            }

            @Override
            public void onFinish() {
                elapsedCount = 0;
                progressBar.setText(getFormattedTimeText(seconds));
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
