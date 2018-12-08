package com.vijay.jsonwizard.customviews;

import android.app.Activity;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.View;

import com.vijay.jsonwizard.R;
import com.vijay.jsonwizard.fragments.JsonFormFragment;
import com.vijay.jsonwizard.interfaces.JsonApi;
import com.vijay.jsonwizard.presenters.JsonFormFragmentPresenter;

import org.json.JSONException;

import java.util.ArrayList;

public class GenericTextWatcher implements TextWatcher, View.OnFocusChangeListener {

    private View mView;
    private String mStepName;
    private ArrayList<View.OnFocusChangeListener> onFocusChangeListeners;
    private JsonFormFragment formFragment;

    public GenericTextWatcher(String stepName, JsonFormFragment formFragment, View view) {
        this.formFragment = formFragment;
        mView = view;
        mStepName = stepName;
        onFocusChangeListeners = new ArrayList<>();
        mView.setOnFocusChangeListener(this);
    }

    public void addOnFocusChangeListener(View.OnFocusChangeListener onFocusChangeListener) {
        onFocusChangeListeners.add(onFocusChangeListener);
    }

    public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {
        //any code here should check if correct view has current focus , see afterTextChanged
        Log.d("GenericTextWatcher", "beforeTextChanged called");
    }

    public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {
        //any code here should check if correct view has current focus, see afterTextChanged
        Log.d("GenericTextWatcher", "beforeTextChanged called");
    }

    public void afterTextChanged(Editable editable) {

        //Check if trigger is Automatic
        if (!((Activity) formFragment.getContext()).getCurrentFocus().equals(mView)) {
            if (mView.getTag(R.id.is_first_time) != null) {

                mView.setTag(R.id.is_first_time, null);
                return;
            } else {

                mView.setTag(R.id.is_first_time, true);
            }
        }

        String text = (String) mView.getTag(R.id.raw_value);

        if (text == null) {
            text = editable.toString();
        }

        Log.d("RealtimeValidation", "afterTextChanged called");
        JsonApi api = null;
        if (formFragment.getContext() instanceof JsonApi) {
            api = (JsonApi) formFragment.getContext();
        } else {
            throw new RuntimeException("Could not fetch context");
        }

        String key = (String) mView.getTag(R.id.key);
        String openMrsEntityParent = (String) mView.getTag(R.id.openmrs_entity_parent);
        String openMrsEntity = (String) mView.getTag(R.id.openmrs_entity);
        String openMrsEntityId = (String) mView.getTag(R.id.openmrs_entity_id);
        Boolean popup = (Boolean) mView.getTag(R.id.extraPopup);
        try {
            api.writeValue(mStepName, key, text, openMrsEntityParent, openMrsEntity, openMrsEntityId, popup);
        } catch (JSONException e) {
            // TODO- handle
            e.printStackTrace();
        }


    }

    @Override
    public void onFocusChange(View v, boolean hasFocus) {
        if (!hasFocus) {
            JsonFormFragmentPresenter.validate(formFragment, mView, false);
        }
        for (View.OnFocusChangeListener curListener : onFocusChangeListeners) {
            curListener.onFocusChange(v, hasFocus);
        }
    }
}