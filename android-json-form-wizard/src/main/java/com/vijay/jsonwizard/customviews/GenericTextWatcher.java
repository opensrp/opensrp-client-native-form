package com.vijay.jsonwizard.customviews;

import android.app.Activity;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.View;

import com.vijay.jsonwizard.R;
import com.vijay.jsonwizard.exceptions.JsonFormRuntimeException;
import com.vijay.jsonwizard.fragments.JsonFormFragment;
import com.vijay.jsonwizard.interfaces.JsonApi;
import com.vijay.jsonwizard.presenters.JsonFormFragmentPresenter;
import com.vijay.jsonwizard.utils.ValidationStatus;

import org.json.JSONException;

import java.util.ArrayList;

import timber.log.Timber;

public class GenericTextWatcher implements TextWatcher, View.OnFocusChangeListener {

    private static String TAG = GenericTextWatcher.class.getCanonicalName();
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

    @Override
    public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {
        //any code here should check if correct view has current focus , see afterTextChanged
    }

    @Override
    public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {
        //any code here should check if correct view has current focus, see afterTextChanged
    }

    @Override
    public synchronized void afterTextChanged(Editable editable) {

        if (editable != null && isRedundantRepetition(editable.toString())) {
            return;
        }

        String text = (String) mView.getTag(R.id.raw_value);

        if (text == null) {
            text = editable.toString();
        }

        mView.setTag(R.id.previous, editable.toString());

        JsonApi api;
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
        popup = popup == null ? false : popup; // Handle nulls as a result of injected values
        ValidationStatus validationStatus = JsonFormFragmentPresenter.validate(formFragment, mView,
                false);
        if (validationStatus.isValid()) {
            try {
                api.writeValue(mStepName, key, text, openMrsEntityParent, openMrsEntity,
                        openMrsEntityId, popup);
            } catch (JSONException e) {
                Timber.e(e);
            }
        }

    }

    @Override
    public void onFocusChange(View v, boolean hasFocus) {
        for (View.OnFocusChangeListener curListener : onFocusChangeListeners) {
            curListener.onFocusChange(v, hasFocus);
        }
    }

    private boolean isRedundantRepetition(String text) {
        View currentFocus = formFragment.getContext() != null ? ((Activity) formFragment.getContext()).getCurrentFocus() : null;

        String prev = mView.getTag(R.id.previous) != null ? mView.getTag(R.id.previous).toString() : null;

        //Check if trigger is Automatic and that text hasn't changed
        return ((currentFocus == null || !currentFocus.equals(mView)) && (prev != null && prev.equals(text)));


    }
}
