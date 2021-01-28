package com.vijay.jsonwizard.widgets;

import android.app.Activity;
import android.content.Context;
import android.content.DialogInterface;
import android.text.TextUtils;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.EditText;
import android.widget.RelativeLayout;

import com.rengwuxian.materialedittext.MaterialEditText;
import com.rey.material.util.ViewUtil;
import com.vijay.jsonwizard.R;
import com.vijay.jsonwizard.constants.JsonFormConstants;
import com.vijay.jsonwizard.customviews.GenericTextWatcher;
import com.vijay.jsonwizard.customviews.TreeViewDialog;
import com.vijay.jsonwizard.fragments.JsonFormFragment;
import com.vijay.jsonwizard.interfaces.CommonListener;
import com.vijay.jsonwizard.interfaces.FormWidgetFactory;
import com.vijay.jsonwizard.interfaces.JsonApi;
import com.vijay.jsonwizard.utils.FormUtils;
import com.vijay.jsonwizard.validators.edittext.RequiredValidator;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import timber.log.Timber;

import static android.view.inputmethod.InputMethodManager.HIDE_NOT_ALWAYS;

/**
 * @author Jason Rogena - jrogena@ona.io
 * @since 07/02/2017
 */
public class TreeViewFactory implements FormWidgetFactory {
    private static final String TAG = "TreeViewFactory";

    private static void showTreeDialog(TreeViewDialog treeViewDialog) {
        treeViewDialog.show();
    }

    private static void changeEditTextValue(EditText editText, String value, String name) {
        String readableValue = "";
        editText.setTag(R.id.raw_value, value);
        if (!TextUtils.isEmpty(name)) {
            try {
                JSONArray nameArray = new JSONArray(name);
                if (nameArray.length() > 0) {
                    readableValue = nameArray.getString(nameArray.length() - 1);

                    if (nameArray.length() > 1) {
                        readableValue = readableValue + ", "
                                + nameArray.getString(nameArray.length() - 2);
                    }
                }
            } catch (JSONException e) {
                Log.e(TAG, Log.getStackTraceString(e));
            }
        }

        editText.setText(readableValue);
    }

    @Override
    public List<View> getViewsFromJson(String stepName, final Context context, JsonFormFragment formFragment, JSONObject
            jsonObject, CommonListener listener, boolean popup) throws JSONException {
        return attachJson(stepName, context, formFragment, jsonObject, popup);
    }

    @Override
    public List<View> getViewsFromJson(String stepName, Context context, JsonFormFragment formFragment,
                                       JSONObject jsonObject, CommonListener listener) throws Exception {
        return attachJson(stepName, context, formFragment, jsonObject, false);
    }

    private List<View> attachJson(final String stepName, final Context context,
                                  final JsonFormFragment formFragment, final JSONObject jsonObject,
                                  boolean popup) throws JSONException {
        List<View> views = new ArrayList<>(1);
        String relevance = jsonObject.optString(JsonFormConstants.RELEVANCE);
        String constraints = jsonObject.optString(JsonFormConstants.CONSTRAINTS);
        final String defaultValueString = jsonObject.optString(JsonFormConstants.DEFAULT);
        final String valueString = jsonObject.optString(JsonFormConstants.VALUE);

        JSONArray canvasIds = new JSONArray();
        RelativeLayout rootLayout = (RelativeLayout) LayoutInflater.from(context).inflate(
                R.layout.native_form_item_edit_text, null);
        rootLayout.setId(ViewUtil.generateViewId());
        canvasIds.put(rootLayout.getId());

        final MaterialEditText editText = createEditText(rootLayout, jsonObject, popup, stepName);
        final ArrayList<String> defaultValue = new ArrayList<>();
        if (!TextUtils.isEmpty(defaultValueString)) {
            try {
                JSONArray jsonArray = new JSONArray(defaultValueString);
                for (int i = 0; i < jsonArray.length(); i++) {
                    defaultValue.add(jsonArray.getString(i));
                }
            } catch (JSONException e) {
                Timber.e(e);
            }
        }


        final ArrayList<String> value = new ArrayList<>();
        if (!TextUtils.isEmpty(valueString)) {
            try {
                JSONArray jsonArray = new JSONArray(valueString);
                for (int i = 0; i < jsonArray.length(); i++) {
                    value.add(jsonArray.getString(i));
                }
            } catch (JSONException e) {
                Timber.e(e);
            }
        }

        formFragment.getJsonApi().getAppExecutors().mainThread().execute(new Runnable() {
            @Override
            public void run() {
                try {
                    final TreeViewDialog treeViewDialog = new TreeViewDialog(context,
                            jsonObject.optJSONArray(JsonFormConstants.TREE), defaultValue, value);

                    if (!TextUtils.isEmpty(jsonObject.optString(JsonFormConstants.VALUE))) {
                        JSONArray name = new JSONArray(treeViewDialog.getName());
                        changeEditTextValue(editText, jsonObject.optString(JsonFormConstants.VALUE), name.toString());
                    }

                    addViewListeners(treeViewDialog, context, editText, stepName, formFragment);
                } catch (JSONException e) {
                    Timber.e(e);
                }
            }
        });


        prepareViewChecks(context, relevance, constraints, canvasIds, editText);

        ((JsonApi) context).addFormDataView(editText);
        views.add(rootLayout);

        return views;
    }

    private void addViewListeners(final TreeViewDialog treeViewDialog, final Context context, final MaterialEditText editText, String stepName, JsonFormFragment formFragment) {
        treeViewDialog.setOnShowListener(new DialogInterface.OnShowListener() {
            @Override
            public void onShow(DialogInterface dialog) {
                onShowAction(context);
            }
        });

        treeViewDialog.setOnDismissListener(new DialogInterface.OnDismissListener() {
            @Override
            public void onDismiss(DialogInterface dialog) {
                onDismissAction(treeViewDialog, editText);
            }
        });

        editText.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                showTreeDialog(treeViewDialog);
            }
        });

        editText.setOnLongClickListener(new View.OnLongClickListener() {
            @Override
            public boolean onLongClick(View v) {
                changeEditTextValue(editText, "", "");
                return true;
            }
        });

        GenericTextWatcher genericTextWatcher = new GenericTextWatcher(stepName, formFragment, editText);
        genericTextWatcher.addOnFocusChangeListener(new View.OnFocusChangeListener() {
            @Override
            public void onFocusChange(View v, boolean hasFocus) {
                if (hasFocus) {
                    showTreeDialog(treeViewDialog);
                }
            }
        });

        editText.addTextChangedListener(genericTextWatcher);
    }

    protected void onDismissAction(TreeViewDialog treeViewDialog, MaterialEditText editText) {
        ArrayList<String> value = treeViewDialog.getValue();
        if (value != null && value.size() > 0) {
            JSONArray array = new JSONArray(value);
            JSONArray name = new JSONArray(treeViewDialog.getName());
            changeEditTextValue(editText, array.toString(), name.toString());
        }
    }

    protected void onShowAction(Context context) {
        InputMethodManager inputManager = (InputMethodManager) context
                .getSystemService(Context.INPUT_METHOD_SERVICE);
        inputManager.hideSoftInputFromWindow(((Activity) context).getCurrentFocus().getWindowToken(),
                HIDE_NOT_ALWAYS);
    }

    private void prepareViewChecks(Context context, String relevance, String constraints, JSONArray canvasIds, MaterialEditText editText) {
        if (!TextUtils.isEmpty(relevance) && context instanceof JsonApi) {
            editText.setTag(R.id.relevance, relevance);
            ((JsonApi) context).addSkipLogicView(editText);
        }
        if (!TextUtils.isEmpty(constraints) && context instanceof JsonApi) {
            editText.setTag(R.id.constraints, constraints);
            ((JsonApi) context).addConstrainedView(editText);
        }
        editText.setTag(R.id.canvas_ids, canvasIds.toString());
    }

    private MaterialEditText createEditText(RelativeLayout rootLayout, JSONObject jsonObject, boolean popup, String stepName)
            throws JSONException {
        String openMrsEntityParent = jsonObject.getString(JsonFormConstants.OPENMRS_ENTITY_PARENT);
        String openMrsEntity = jsonObject.getString(JsonFormConstants.OPENMRS_ENTITY);
        String openMrsEntityId = jsonObject.getString(JsonFormConstants.OPENMRS_ENTITY_ID);
        final MaterialEditText editText = rootLayout.findViewById(R.id.edit_text);
        editText.setHint(jsonObject.getString(JsonFormConstants.HINT));
        editText.setFloatingLabelText(jsonObject.getString(JsonFormConstants.HINT));
        editText.setId(ViewUtil.generateViewId());
        editText.setTag(R.id.key, jsonObject.getString(JsonFormConstants.KEY));
        editText.setTag(R.id.openmrs_entity_parent, openMrsEntityParent);
        editText.setTag(R.id.openmrs_entity, openMrsEntity);
        editText.setTag(R.id.openmrs_entity_id, openMrsEntityId);
        editText.setTag(R.id.extraPopup, popup);
        editText.setTag(R.id.address, stepName + ":" + jsonObject.getString(JsonFormConstants.KEY));
        if (jsonObject.has(JsonFormConstants.V_REQUIRED)) {
            JSONObject requiredObject = jsonObject.optJSONObject(JsonFormConstants.V_REQUIRED);
            boolean requiredValue = requiredObject.getBoolean(JsonFormConstants.VALUE);
            if (Boolean.TRUE.equals(requiredValue)) {
                editText.addValidator(new RequiredValidator(requiredObject.getString(JsonFormConstants.ERR)));
                FormUtils.setRequiredOnHint(editText);
            }

        }

        if (jsonObject.has(JsonFormConstants.READ_ONLY)) {
            boolean readOnly = jsonObject.getBoolean(JsonFormConstants.READ_ONLY);
            editText.setEnabled(!readOnly);
            editText.setFocusable(!readOnly);
        }


        return editText;
    }

    @Override
    public Set<String> getCustomTranslatableWidgetFields() {
        return new HashSet<>();
    }
}