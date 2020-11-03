package com.vijay.jsonwizard.widgets;

import android.Manifest;
import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.graphics.drawable.ColorDrawable;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.RelativeLayout;

import com.google.android.gms.vision.barcode.Barcode;
import com.rengwuxian.materialedittext.MaterialEditText;
import com.rey.material.util.ViewUtil;
import com.rey.material.widget.Button;
import com.vijay.jsonwizard.R;
import com.vijay.jsonwizard.activities.JsonFormBarcodeScanActivity;
import com.vijay.jsonwizard.constants.JsonFormConstants;
import com.vijay.jsonwizard.customviews.GenericTextWatcher;
import com.vijay.jsonwizard.fragments.JsonFormFragment;
import com.vijay.jsonwizard.interfaces.CommonListener;
import com.vijay.jsonwizard.interfaces.FormWidgetFactory;
import com.vijay.jsonwizard.interfaces.JsonApi;
import com.vijay.jsonwizard.interfaces.OnActivityResultListener;
import com.vijay.jsonwizard.utils.FormUtils;
import com.vijay.jsonwizard.utils.PermissionUtils;
import com.vijay.jsonwizard.utils.Utils;
import com.vijay.jsonwizard.validators.edittext.RequiredValidator;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import timber.log.Timber;

import static android.app.Activity.RESULT_OK;
import static android.view.inputmethod.InputMethodManager.HIDE_NOT_ALWAYS;

/**
 * Created by Jason Rogena - jrogena@ona.io on 20/03/2017.
 */

public class BarcodeFactory implements FormWidgetFactory {
    private static final String TYPE_QR = "qrcode";
    public static final String SCAN_BUTTON_TEXT = "scanButtonText";


    @Override
    public List<View> getViewsFromJson(String stepName, Context context, JsonFormFragment formFragment, JSONObject jsonObject, CommonListener listener) throws Exception {
        return getViewsFromJson(stepName, context, formFragment, jsonObject, listener, false);
    }

    @Override
    public Set<String> getCustomTranslatableWidgetFields() {
        Set<String> customTranslatableWidgetFields = new HashSet<>();
        customTranslatableWidgetFields.add(SCAN_BUTTON_TEXT);
        return customTranslatableWidgetFields;
    }

    @Override
    public List<View> getViewsFromJson(final String stepName, final Context context,
                                       final JsonFormFragment formFragment, final JSONObject jsonObject,
                                       CommonListener listener, final boolean popup) throws Exception {

        List<View> views = new ArrayList<>(1);
        final RelativeLayout rootLayout = getRootLayout(context);
        final int canvasId = ViewUtil.generateViewId();
        formFragment.getJsonApi().getAppExecutors().mainThread().execute(new Runnable() {
            @Override
            public void run() {
                try {
                    rootLayout.setId(canvasId);
                    final MaterialEditText editText = createEditText(rootLayout, jsonObject, canvasId, stepName, popup);
                    attachJson(rootLayout, stepName, context, formFragment, jsonObject, editText);
                    ((JsonApi) context).addFormDataView(editText);
                } catch (JSONException e) {
                    Timber.e(e);
                }
            }
        });
        views.add(rootLayout);
        return views;
    }

    private void attachJson(final RelativeLayout rootLayout, final String stepName, final Context context, final JsonFormFragment formFragment,
                            final JSONObject jsonObject, final MaterialEditText editText) throws JSONException {
        final String relevance = jsonObject.optString(JsonFormConstants.RELEVANCE);
        final String calculation = jsonObject.optString(JsonFormConstants.CALCULATION);
        final String constraints = jsonObject.optString(JsonFormConstants.CONSTRAINTS);
        final String value = jsonObject.optString(JsonFormConstants.VALUE, null);


        if (value != null && !checkValue(value)) {
            editText.setText(value);
        }

        editText.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                addOnClickActions(context, editText, jsonObject.optString(JsonFormConstants.BARCODE_TYPE));
            }
        });

        editText.setOnLongClickListener(new View.OnLongClickListener() {
            @Override
            public boolean onLongClick(View v) {
                editText.setText("");
                return true;
            }
        });

        GenericTextWatcher textWatcher = new GenericTextWatcher(stepName, formFragment, editText);
        textWatcher.addOnFocusChangeListener(new View.OnFocusChangeListener() {
            @Override
            public void onFocusChange(View v, boolean hasFocus) {
                if (hasFocus) {
                    addOnClickActions(context, editText, jsonObject.optString(JsonFormConstants.BARCODE_TYPE));
                }
            }
        });

        addScanButton(context, jsonObject, editText, rootLayout);

        editText.addTextChangedListener(textWatcher);
        attachRefreshLogic(context, relevance, calculation, constraints, editText);
    }

    private void attachRefreshLogic(Context context, String relevance, String calculation, String constraints, MaterialEditText editText) {
        if (!TextUtils.isEmpty(relevance) && context instanceof JsonApi) {
            editText.setTag(R.id.relevance, relevance);
            ((JsonApi) context).addSkipLogicView(editText);
        }
        if (!TextUtils.isEmpty(constraints) && context instanceof JsonApi) {
            editText.setTag(R.id.constraints, constraints);
            ((JsonApi) context).addConstrainedView(editText);
        }
        if (!TextUtils.isEmpty(calculation) && context instanceof JsonApi) {
            editText.setTag(R.id.calculation, calculation);
            ((JsonApi) context).addCalculationLogicView(editText);
        }
    }

    public RelativeLayout getRootLayout(Context context) {
        return (RelativeLayout) LayoutInflater.from(context).inflate(R.layout.native_form_item_barcode, null);
    }

    private void addOnClickActions(Context context, MaterialEditText editText, String barcodeType) {
        addOnBarCodeResultListeners(context, editText);
        launchBarcodeScanner((Activity) context, editText, barcodeType);
    }

    protected void addOnBarCodeResultListeners(final Context context, final MaterialEditText editText) {
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
                                    Timber.d("Scanned QR Code %s ", barcode.displayValue);
                                    editText.setText(barcode.displayValue);
                                } else
                                    Timber.i("NO RESULT FOR QR CODE");
                            }
                        }
                    });
        }
    }

    private MaterialEditText createEditText(RelativeLayout rootLayout, JSONObject jsonObject, int canvasId, String stepName, boolean popup) throws JSONException {
        final MaterialEditText editText = rootLayout.findViewById(R.id.edit_text);
        String openMrsEntityParent = jsonObject.getString(JsonFormConstants.OPENMRS_ENTITY_PARENT);
        String openMrsEntity = jsonObject.getString(JsonFormConstants.OPENMRS_ENTITY);
        String openMrsEntityId = jsonObject.getString(JsonFormConstants.OPENMRS_ENTITY_ID);
        editText.setHint(jsonObject.getString(JsonFormConstants.HINT));
        JSONArray canvasIdsArray = new JSONArray();
        canvasIdsArray.put(canvasId);
        editText.setTag(R.id.canvas_ids, canvasIdsArray.toString());
        editText.setTag(R.id.address, stepName + ":" + jsonObject.getString(JsonFormConstants.KEY));
        editText.setFloatingLabelText(jsonObject.getString(JsonFormConstants.HINT));
        editText.setId(ViewUtil.generateViewId());
        editText.setTag(R.id.key, jsonObject.getString(JsonFormConstants.KEY));
        editText.setTag(R.id.openmrs_entity_parent, openMrsEntityParent);
        editText.setTag(R.id.extraPopup, popup);
        editText.setTag(R.id.openmrs_entity, openMrsEntity);
        editText.setTag(R.id.openmrs_entity_id, openMrsEntityId);
        if (jsonObject.has(JsonFormConstants.V_REQUIRED)) {
            JSONObject requiredObject = jsonObject.optJSONObject(JsonFormConstants.V_REQUIRED);
            boolean requiredValue = requiredObject.getBoolean(JsonFormConstants.VALUE);
            if (Boolean.TRUE.equals(requiredValue)) {
                editText.addValidator(
                        new RequiredValidator(requiredObject.getString(JsonFormConstants.ERR)));
                FormUtils.setRequiredOnHint(editText);
            }
        }
        return editText;
    }

    private void addScanButton(final Context context, final JSONObject jsonObject, final MaterialEditText editText, RelativeLayout rootLayout) throws JSONException {
        Button scanButton = rootLayout.findViewById(R.id.scan_button);
        scanButton.setBackgroundColor(context.getResources().getColor(R.color.primary));
        scanButton.setMinHeight(0);
        scanButton.setMinimumHeight(0);
        scanButton.setText(jsonObject.getString(SCAN_BUTTON_TEXT));
        scanButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                addOnClickActions(context, editText, jsonObject.optString(JsonFormConstants.BARCODE_TYPE));
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
    }

    protected void launchBarcodeScanner(Activity activity, MaterialEditText editText, String barcodeType) {
        InputMethodManager inputManager = (InputMethodManager) activity.getSystemService(
                Context.INPUT_METHOD_SERVICE);
        inputManager.hideSoftInputFromWindow(editText.getWindowToken(), HIDE_NOT_ALWAYS);
        if (barcodeType != null && barcodeType.equals(TYPE_QR) && PermissionUtils.isPermissionGranted(activity, Manifest.permission.CAMERA, PermissionUtils.CAMERA_PERMISSION_REQUEST_CODE)) {
            try {
                Intent intent = new Intent(activity, JsonFormBarcodeScanActivity.class);
                activity.startActivityForResult(intent, JsonFormConstants.BARCODE_CONSTANTS.BARCODE_REQUEST_CODE);
            } catch (SecurityException e) {
                Utils.showToast(activity, activity.getApplicationContext().getResources().getString(R.string.allow_camera_management));
            }
        }
    }

    private boolean checkValue(String value) {
        return value.contains("ABC");
    }
}