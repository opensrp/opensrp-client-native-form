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

import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

public class CountDownTimerFactory implements FormWidgetFactory {

    private View rootLayout;

    @Override
    public List<View> getViewsFromJson(String stepName, Context context, JsonFormFragment formFragment, JSONObject jsonObject, CommonListener listener, boolean popup) throws Exception {
        List<View> views = new ArrayList<>(1);
        rootLayout = LayoutInflater.from(context).inflate(getLayout(), null);
        setWidgetTags(jsonObject);
        String secondsString = jsonObject.optString(JsonFormConstants.COUNTDOWN_TIMER_SECONDS);
        long seconds = (secondsString.isEmpty()) ? 0 : Long.parseLong(secondsString);
        final TextView countDownTextView = rootLayout.findViewById(R.id.countDown);
        startCountDown(seconds, countDownTextView);
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

    private int getLayout() {
        return R.layout.native_form_countdown_timer;
    }

    /**
     * Start the countdown
     *
     * @param seconds The number of seconds to count down to 0
     */
    private void startCountDown(long seconds, final TextView tv) {
        new CountDownTimer(seconds, 1000) {
            @Override
            public void onTick(long millisUntilFinished) {
             tv.setText(String.format("%d", millisUntilFinished));
            }

            @Override
            public void onFinish() {
                // TODO :: Ring the alarm!!
            }
        }.start();
    }
}
