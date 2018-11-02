package com.vijay.jsonwizard.widgets;

import android.Manifest;
import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.graphics.drawable.ColorDrawable;
import android.text.TextUtils;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.RelativeLayout;

import com.google.android.gms.vision.barcode.Barcode;
import com.rengwuxian.materialedittext.MaterialEditText;
import com.rey.material.util.ViewUtil;
import com.rey.material.widget.Button;
import com.vijay.jsonwizard.R;
import com.vijay.jsonwizard.activities.BarcodeScanActivity;
import com.vijay.jsonwizard.constants.JsonFormConstants;
import com.vijay.jsonwizard.customviews.GenericTextWatcher;
import com.vijay.jsonwizard.fragments.JsonFormFragment;
import com.vijay.jsonwizard.interfaces.CommonListener;
import com.vijay.jsonwizard.interfaces.FormWidgetFactory;
import com.vijay.jsonwizard.interfaces.JsonApi;
import com.vijay.jsonwizard.interfaces.OnActivityResultListener;
import com.vijay.jsonwizard.utils.PermissionUtils;
import com.vijay.jsonwizard.utils.Utils;
import com.vijay.jsonwizard.validators.edittext.RequiredValidator;

import org.json.JSONArray;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

import static android.app.Activity.RESULT_OK;
import static android.view.inputmethod.InputMethodManager.HIDE_NOT_ALWAYS;

/**
 * Created by Jason Rogena - jrogena@ona.io on 20/03/2017.
 */

public class BarcodeFactory implements FormWidgetFactory {
    private static final String TYPE_QR = "qrcode";

    @Override
    public List<View> getViewsFromJson(String stepName, final Context context,
                                       JsonFormFragment formFragment, final JSONObject jsonObject,
                                       CommonListener listener) {
        List<View> views = new ArrayList<>(1);
        try {
            String openMrsEntityParent = jsonObject.getString("openmrs_entity_parent");
            String openMrsEntity = jsonObject.getString("openmrs_entity");
            String openMrsEntityId = jsonObject.getString("openmrs_entity_id");
            String relevance = jsonObject.optString("relevance");
            final String constraints = jsonObject.optString("constraints");
            String value = jsonObject.optString(JsonFormConstants.VALUE, null);

            RelativeLayout rootLayout = (RelativeLayout) LayoutInflater.from(context)
                    .inflate(R.layout.native_form_item_barcode, null);
            final int canvasId = ViewUtil.generateViewId();
            rootLayout.setId(canvasId);
            final MaterialEditText editText = rootLayout
                    .findViewById(R.id.edit_text);
            editText.setHint(jsonObject.getString(JsonFormConstants.HINT));
            JSONArray canvasIdsArray = new JSONArray();
            canvasIdsArray.put(canvasId);
            editText.setTag(R.id.canvas_ids, canvasIdsArray.toString());
            editText.setTag(R.id.address, stepName + ":" + jsonObject.getString(JsonFormConstants.KEY));
            editText.setFloatingLabelText(jsonObject.getString(JsonFormConstants.HINT));
            editText.setId(ViewUtil.generateViewId());
            editText.setTag(R.id.key, jsonObject.getString(JsonFormConstants.KEY));
            editText.setTag(R.id.openmrs_entity_parent, openMrsEntityParent);
            editText.setTag(R.id.openmrs_entity, openMrsEntity);
            editText.setTag(R.id.openmrs_entity_id, openMrsEntityId);
            if (jsonObject.has(JsonFormConstants.V_REQUIRED)) {
                JSONObject requiredObject = jsonObject.optJSONObject(JsonFormConstants.V_REQUIRED);
                String requiredValue = requiredObject.getString(JsonFormConstants.VALUE);
                if (!TextUtils.isEmpty(requiredValue) && (Boolean.TRUE.toString().equalsIgnoreCase(requiredValue))) {
                    editText.addValidator(
                            new RequiredValidator(requiredObject.getString(JsonFormConstants.ERR)));
                }
            }


            if (value != null && !checkValue(value)) {
                editText.setText(value);
            }

            editText.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    launchBarcodeScanner((Activity) context, editText,
                            jsonObject.optString("barcode_type"));
                }
            });

            editText.setOnLongClickListener(new View.OnLongClickListener() {
                @Override
                public boolean onLongClick(View v) {
                    editText.setText("");
                    return true;
                }
            });

            if (context instanceof JsonApi) {
                JsonApi jsonApi = (JsonApi) context;
                jsonApi.addOnActivityResultListener(JsonFormConstants.BARCODE_CONSTANTS.BARCODE_REQUEST_CODE,
                        new OnActivityResultListener() {
                            @Override
                            public void onActivityResult(int requestCode,
                                                         int resultCode, Intent data) {
                                if (requestCode == JsonFormConstants.BARCODE_CONSTANTS.BARCODE_REQUEST_CODE && resultCode == RESULT_OK) {
                                    if (data != null) {
                                        Barcode barcode = data.getParcelableExtra(JsonFormConstants.BARCODE_CONSTANTS.BARCODE_KEY);
                                        Log.d("Scanned QR Code", barcode.displayValue);
                                        editText.setText(barcode.displayValue);
                                    } else
                                        Log.i("", "NO RESULT FOR QR CODE");
                                }
                            }
                        });
            }

            GenericTextWatcher textWatcher = new GenericTextWatcher(stepName,
                    formFragment,
                    editText);
            textWatcher.addOnFocusChangeListener(new View.OnFocusChangeListener() {
                @Override
                public void onFocusChange(View v, boolean hasFocus) {
                    if (hasFocus) {
                        launchBarcodeScanner((Activity) context, editText,
                                jsonObject.optString("barcode_type"));
                    }
                }
            });

            Button scanButton = rootLayout.findViewById(R.id.scan_button);
            scanButton.setBackgroundColor(context.getResources().getColor(R.color.primary));
            scanButton.setMinHeight(0);
            scanButton.setMinimumHeight(0);
            scanButton.setText(jsonObject.getString("scanButtonText"));
            scanButton.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    launchBarcodeScanner((Activity) context, editText,
                            jsonObject.optString("barcode_type"));
                }
            });

            if (jsonObject.has(JsonFormConstants.READ_ONLY)) {
                boolean readOnly = jsonObject.getBoolean(JsonFormConstants.READ_ONLY);
                editText.setEnabled(!readOnly);
                editText.setFocusable(!readOnly);
                if (readOnly) {
                    scanButton.setBackgroundDrawable(new ColorDrawable(context.getResources()
                            .getColor(android.R.color.darker_gray)));
                    scanButton.setClickable(false);
                    scanButton.setEnabled(false);
                    scanButton.setFocusable(false);
                }
            }

            editText.addTextChangedListener(textWatcher);
            if (relevance != null && context instanceof JsonApi) {
                editText.setTag(R.id.relevance, relevance);
                ((JsonApi) context).addSkipLogicView(editText);
            }
            if (constraints != null && context instanceof JsonApi) {
                editText.setTag(R.id.constraints, constraints);
                ((JsonApi) context).addConstrainedView(editText);
            }

            ((JsonApi) context).addFormDataView(editText);

            views.add(rootLayout);
        } catch (Exception e) {
            e.printStackTrace();
        }

        return views;
    }

    private void launchBarcodeScanner(Activity activity, MaterialEditText editText, String barcodeType) {
        InputMethodManager inputManager = (InputMethodManager) activity.getSystemService(
                Context.INPUT_METHOD_SERVICE);
        inputManager.hideSoftInputFromWindow(editText.getWindowToken(), HIDE_NOT_ALWAYS);
        if (barcodeType != null && barcodeType.equals(TYPE_QR)) {
            if (PermissionUtils.isPermissionGranted(activity, Manifest.permission.CAMERA, PermissionUtils.CAMERA_PERMISSION_REQUEST_CODE)) {
                try {
                    Intent intent = new Intent(activity, BarcodeScanActivity.class);
                    activity.startActivityForResult(intent, JsonFormConstants.BARCODE_CONSTANTS.BARCODE_REQUEST_CODE);
                } catch (SecurityException e) {
                    Utils.showToast(activity, activity.getApplicationContext().getResources().getString(R.string.allow_camera_management));
                }
            }
        }
    }

    private boolean checkValue(String value) {
        return value.contains("ABC0.");
    }

}
