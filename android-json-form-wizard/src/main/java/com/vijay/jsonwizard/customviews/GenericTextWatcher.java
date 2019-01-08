package com.vijay.jsonwizard.customviews;

import android.app.Activity;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.View;

import com.vijay.jsonwizard.R;
import com.vijay.jsonwizard.exceptions.JsonFormRuntimeException;
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
    private static String TAG = GenericTextWatcher.class.getCanonicalName();

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

    public synchronized void afterTextChanged(Editable editable) {

        if (editable != null && isRedundantRepetition(editable.toString())) {

            return;
        }

        String text = (String) mView.getTag(R.id.raw_value);

        if (text == null) {
            text = editable.toString();
        }

        mView.setTag(R.id.previous, text);

        Log.d("RealtimeValidation", "afterTextChanged called");
        JsonApi api = null;
        if (formFragment.getContext() instanceof JsonApi) {
            api = (JsonApi) formFragment.getContext();
        } else {
            throw new JsonFormRuntimeException("Could not fetch context");
        }

        String key = (String) mView.getTag(R.id.key);
        String openMrsEntityParent = (String) mView.getTag(R.id.openmrs_entity_parent);
        String openMrsEntity = (String) mView.getTag(R.id.openmrs_entity);
        String openMrsEntityId = (String) mView.getTag(R.id.openmrs_entity_id);
        Boolean popup = (Boolean) mView.getTag(R.id.extraPopup);
        try {
            api.writeValue(mStepName, key, text, openMrsEntityParent, openMrsEntity, openMrsEntityId, popup);
        } catch (JSONException e) {
            Log.e(TAG, e.getMessage(), e);
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

    private boolean isRedundantRepetition(String text) {
        View currentFocus = ((Activity) formFragment.getContext()).getCurrentFocus();

        String prev = mView.getTag(R.id.previous) != null ? mView.getTag(R.id.previous).toString() : null;

        //Check if trigger is Automatic and that text hasn't changed
        return ((currentFocus == null || !currentFocus.equals(mView)) && (prev != null && prev.equals(text)));


    }
}