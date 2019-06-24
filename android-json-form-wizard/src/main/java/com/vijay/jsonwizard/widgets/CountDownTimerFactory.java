package com.vijay.jsonwizard.widgets;

import android.content.Context;
import android.os.CountDownTimer;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.TextView;

import com.vijay.jsonwizard.R;
import com.vijay.jsonwizard.constants.JsonFormConstants;
import com.vijay.jsonwizard.fragments.JsonFormFragment;
import com.vijay.jsonwizard.interfaces.CommonListener;
import com.vijay.jsonwizard.interfaces.FormWidgetFactory;
import com.vijay.jsonwizard.utils.FormUtils;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.TimeUnit;

public class CountDownTimerFactory implements FormWidgetFactory {

    private View rootLayout;
    private TextView countDownTextView;

    @Override
    public List<View> getViewsFromJson(String stepName, Context context, JsonFormFragment formFragment, JSONObject jsonObject, CommonListener listener, boolean popup) throws Exception {
        List<View> views = new ArrayList<>(1);
        rootLayout = LayoutInflater.from(context).inflate(getLayout(), null);
        countDownTextView = rootLayout.findViewById(R.id.countDown);

        setWidgetTags(jsonObject);
        formatWidget(jsonObject, context);

        String secondsString = jsonObject.optString(JsonFormConstants.COUNTDOWN_TIMER_SECONDS);
        String interValString = jsonObject.optString(JsonFormConstants.COUNTDOWN_INTERVAL);
        long seconds = (secondsString.isEmpty()) ? 0 : Long.parseLong(secondsString);
        long defaultCountdownInterval = 1000;
        long interval = (interValString.isEmpty()) ? defaultCountdownInterval : Long.parseLong(interValString);
        views.add(rootLayout);
        startCountDown(seconds, interval, countDownTextView);
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
        countDownTextView.setTextSize((FormUtils.getValueFromSpOrDpOrPx(textSize, context)));
    }

    private int getLayout() {
        return R.layout.native_form_countdown_timer;
    }

    /**
     * Start the countdown
     *
     * @param seconds           The time count down to
     * @param countdownInterval The intervals for running the countdown
     * @param tv                The TextView to show the countdown
     */
    private void startCountDown(long seconds, long countdownInterval, final TextView tv) {
        new CountDownTimer(seconds, countdownInterval) {
            @Override
            public void onTick(long millisUntilFinished) {
                tv.setText(String.format("%1d :%2d", TimeUnit.MILLISECONDS.toMinutes(millisUntilFinished), TimeUnit.MILLISECONDS.toSeconds(millisUntilFinished)));
            }

            @Override
            public void onFinish() {
                // TODO :: Ring the alarm!!
            }
        }.start();
    }
}
