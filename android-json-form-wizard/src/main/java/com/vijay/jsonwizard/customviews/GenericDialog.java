package com.vijay.jsonwizard.customviews;

import android.app.Activity;
import android.app.DialogFragment;
import android.content.Context;
import android.content.DialogInterface;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.LinearLayout;

import com.vijay.jsonwizard.R;
import com.vijay.jsonwizard.constants.JsonFormConstants;
import com.vijay.jsonwizard.fragments.JsonFormFragment;
import com.vijay.jsonwizard.interactors.JsonFormInteractor;
import com.vijay.jsonwizard.interfaces.CommonListener;
import com.vijay.jsonwizard.interfaces.JsonApi;
import com.vijay.jsonwizard.utils.Utils;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.List;

import static android.view.inputmethod.InputMethodManager.HIDE_NOT_ALWAYS;

public class GenericDialog extends DialogFragment {
    private static JsonFormInteractor jsonFormInteractor = new JsonFormInteractor();
    private Context context;
    private CommonListener commonListener;
    private JsonFormFragment formFragment;
    private String formIdentity;
    private String formLocation;
    private String stepName;
    private DialogInterface.OnShowListener onShowListener;

    public GenericDialog() {
    }

    public void setContext(Context context) throws IllegalStateException {
        this.context = context;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        if (context == null) {
            throw new IllegalStateException("The Context is not set. Did you forget to set context with Generic Dialog setContext method?");
        }
        setStyle(DialogFragment.STYLE_NO_TITLE, android.R.style.Theme_Holo_Light_Dialog);
    }

    public void setOnShowListener(DialogInterface.OnShowListener onShowListener_) {
        onShowListener = onShowListener_;
    }


    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        ViewGroup dialogView = (ViewGroup) inflater.inflate(R.layout.native_form_generic_dialog, container, false);

        Button cancelButton;
        Button okButton;
        JSONArray specifyContent = null;
        Activity activity = (Activity) context;

        JSONObject subForm = getSubFormJson(formLocation, context);
        if (subForm != null) {
            try {
                if (subForm.has(JsonFormConstants.SPECIFY_CONTENT)) {
                    specifyContent = subForm.getJSONArray(JsonFormConstants.SPECIFY_CONTENT);
                } else {
                    Utils.showToast(activity, activity.getApplicationContext().getResources().getString(R.string.please_specify_content));
                    GenericDialog.this.dismiss();
                }
            } catch (JSONException e) {
                e.printStackTrace();
            }

        }

        setOnShowListener(new DialogInterface.OnShowListener() {
            @Override
            public void onShow(DialogInterface dialog) {
                InputMethodManager inputManager = (InputMethodManager) context.getSystemService(Context.INPUT_METHOD_SERVICE);
                inputManager.hideSoftInputFromWindow(((Activity) context).getCurrentFocus().getWindowToken(),
                        HIDE_NOT_ALWAYS);
            }
        });

        List<View> listOfViews = new ArrayList<>();
        jsonFormInteractor.fetchFields(listOfViews, stepName, formFragment, specifyContent, commonListener, true);

        LinearLayout genericDialogContent = dialogView.findViewById(
                R.id.generic_dialog_content);
        for (View view : listOfViews) {
            genericDialogContent.addView(view);
        }

        JsonApi jsonApi = (JsonApi) activity;
        jsonApi.refreshSkipLogic(null, null, true);

        cancelButton = dialogView.findViewById(R.id.generic_dialog_cancel_button);
        cancelButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                GenericDialog.this.dismiss();
            }
        });

        okButton = dialogView.findViewById(R.id.generic_dialog_done_button);
        okButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                GenericDialog.this.dismiss();
            }
        });

        return dialogView;
    }

    public JSONObject getSubFormJson(String subFormsLocation, Context context) {
        String defaultSubFormLocation = "json/sub_form";
        if (subFormsLocation != null && !subFormsLocation.equals("")) {
            defaultSubFormLocation = subFormsLocation;
        }

        try {
            InputStream inputStream = context.getAssets().open(defaultSubFormLocation + "/" + formIdentity + ".json");
            BufferedReader reader = new BufferedReader(new InputStreamReader(inputStream,
                    "UTF-8"));
            String jsonString;
            StringBuilder stringBuilder = new StringBuilder();
            while ((jsonString = reader.readLine()) != null) {
                stringBuilder.append(jsonString);
            }
            inputStream.close();

            return new JSONObject(stringBuilder.toString());
        } catch (IOException e) {
            e.printStackTrace();
        } catch (JSONException e) {
            e.printStackTrace();
        }

        return null;
    }

    public void setCommonListener(CommonListener commonListener) {
        this.commonListener = commonListener;
    }

    public void setFormFragment(JsonFormFragment formFragment) {
        this.formFragment = formFragment;
    }

    public void setFormIdentity(String formIdentity) {
        this.formIdentity = formIdentity;
    }

    public void setFormLocation(String formLocation) {
        this.formLocation = formLocation;
    }

    public void setStepName(String stepName) {
        this.stepName = stepName;
    }
}
