package com.vijay.jsonwizard.fragments;

import android.content.Context;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.DialogFragment;
import android.support.v7.widget.Toolbar;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.FrameLayout;

import com.vijay.jsonwizard.R;
import com.vijay.jsonwizard.customviews.CustomFormError;
import com.vijay.jsonwizard.interfaces.OnFieldsInvalid;
import com.vijay.jsonwizard.utils.ValidationStatus;

import java.util.Map;

public class JsonFormErrorFragment extends DialogFragment implements View.OnClickListener {

    public static String TAG = JsonFormErrorFragment.class.getSimpleName();
    public OnFieldsInvalid onFieldsInvalid;
    private CustomFormError formError;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setStyle(DialogFragment.STYLE_NORMAL, R.style.NativeFormsFullScreenDialogStyle);
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.native_form_error_fragment, container, false);
        Toolbar dialogToolbar = view.findViewById(R.id.error_fragment_toolbar);
        FrameLayout frameLayout = view.findViewById(R.id.error_fragment_frame_layout);
        formError = new CustomFormError(getContext());
        handleFormErrors();
        frameLayout.addView(formError);
        dialogToolbar.setNavigationIcon(R.drawable.ic_action_close);
        dialogToolbar.setNavigationOnClickListener(this);
        dialogToolbar.setTitle(R.string.attention);
        dialogToolbar.setTitleTextColor(getContext().getResources().getColor(R.color.white));
        return view;
    }

    @Override
    public void onStart() {
        super.onStart();
        if (getDialog() != null) {
            int width = ViewGroup.LayoutParams.MATCH_PARENT;
            int height = ViewGroup.LayoutParams.MATCH_PARENT;
            if (getDialog().getWindow() != null) {
                getDialog().getWindow().setLayout(width, height);
            }
        }
    }

    @Override
    public void onClick(View v) {
        JsonFormErrorFragment.this.dismiss();
    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        try {
            onFieldsInvalid = (OnFieldsInvalid) getActivity();
        } catch (ClassCastException ex) {
            throw new ClassCastException("Error retrieving passed invalid fields");
        }
    }

    protected void handleFormErrors() {
        Map<String, ValidationStatus> invalidFields = onFieldsInvalid.getPassedInvalidFields();
        formError.setInvalidFields(invalidFields);
    }

}
