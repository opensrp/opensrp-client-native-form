package com.vijay.jsonwizard.views;

import android.Manifest;
import android.app.Activity;
import android.app.AlertDialog;
import android.app.FragmentManager;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Build;
import android.os.Environment;
import android.provider.MediaStore;
import android.support.annotation.RequiresApi;
import android.support.v4.content.FileProvider;
import android.support.v7.widget.AppCompatRadioButton;
import android.text.TextUtils;
import android.util.AttributeSet;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.InputMethodManager;
import android.widget.AdapterView;
import android.widget.CompoundButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RadioGroup;
import android.widget.RelativeLayout;
import android.widget.ScrollView;
import android.widget.Toast;

import com.rengwuxian.materialedittext.MaterialEditText;
import com.rey.material.widget.Button;
import com.vijay.jsonwizard.R;
import com.vijay.jsonwizard.constants.JsonFormConstants;
import com.vijay.jsonwizard.customviews.CheckBox;
import com.vijay.jsonwizard.customviews.MaterialSpinner;
import com.vijay.jsonwizard.customviews.NativeEditText;
import com.vijay.jsonwizard.customviews.RadioButton;
import com.vijay.jsonwizard.engine.JsonApiEngine;
import com.vijay.jsonwizard.interactors.NativeViewInteractor;
import com.vijay.jsonwizard.interfaces.CommonListener;
import com.vijay.jsonwizard.interfaces.JsonApi;
import com.vijay.jsonwizard.interfaces.NativeViewer;
import com.vijay.jsonwizard.utils.FormUtils;
import com.vijay.jsonwizard.utils.NativeViewUtils;
import com.vijay.jsonwizard.utils.PermissionUtils;
import com.vijay.jsonwizard.utils.ValidationStatus;
import com.vijay.jsonwizard.widgets.EditTextFactory;
import com.vijay.jsonwizard.widgets.GpsFactory;
import com.vijay.jsonwizard.widgets.ImagePickerFactory;
import com.vijay.jsonwizard.widgets.NativeEditTextFactory;
import com.vijay.jsonwizard.widgets.NativeRadioButtonFactory;
import com.vijay.jsonwizard.widgets.NumberSelectorFactory;
import com.vijay.jsonwizard.widgets.SpinnerFactory;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.File;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Set;

/**
 * Create and renders the native form view.
 * <p>
 * The view returns a {@link org.json.JSONObject} result via {@link #getJsonFrom()}
 * Initialize rendering via {@link #setJsonFrom(String)} or {@link #setJsonObject(JSONObject)}. This will load the form
 */
public class NativeForm extends RelativeLayout implements NativeViewer, CommonListener {

    private String TAG = NativeForm.class.getCanonicalName();
    private static String CONST_REAL_TIME_VALIDATION = "RealtimeValidation";
    private static String CONST_FRAGMENT_WRITEVALUE_CALLED = "Fragment write value called";

    private JsonApiEngine jsonApiEngine;
    private Context context;
    private String jsonFrom;
    private String mStepName = "step1";
    private String mPrevStep;
    private JSONObject mStepDetails;
    private FragmentManager fragmentManager;
    private View rootView;
    private List<String> formList = new ArrayList<>();
    private NativeViewInteractor nativeViewInteractor = NativeViewInteractor.getInstance();

    // presenter
    protected String key;
    protected String type;
    private String mCurrentKey;
    private String mCurrentPhotoPath;

    private static final int RESULT_LOAD_IMG = 1;

    public NativeForm(Context context) {
        super(context);
        this.context = context;
        init();
    }

    public NativeForm(Context context, AttributeSet attrs) {
        super(context, attrs);
        this.context = context;
        init();
    }

    public NativeForm(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        this.context = context;
        init();
    }

    @RequiresApi(api = Build.VERSION_CODES.LOLLIPOP)
    public NativeForm(Context context, AttributeSet attrs, int defStyleAttr, int defStyleRes) {
        super(context, attrs, defStyleAttr, defStyleRes);
        this.context = context;
        init();
    }

    public void init() {
        // View v = LayoutInflater.from(context).inflate(R.layout.native_form, null);
        rootView = LayoutInflater.from(context).inflate(R.layout.native_form_view, this, true);

        jsonApiEngine = new JsonApiEngine(context, rootView);
    }

    public String getJsonFrom() {
        return jsonFrom;
    }

    public void setJsonFrom(String jsonFrom) {
        this.jsonFrom = jsonFrom;
        jsonApiEngine.setmJSONObject(NativeViewUtils.getFormJson(context, jsonFrom));
        loadForm();
    }

    public JSONObject getJsonObject() {
        return jsonApiEngine.getmJSONObject();
    }

    public void setJsonObject(JSONObject jsonObject) {
        jsonApiEngine.setmJSONObject(jsonObject);
        loadForm();
    }

    private void loadForm() {
        if (jsonApiEngine.getmJSONObject() == null || rootView == null) {
            throw new RuntimeException("Error processing file");
        } else {
            jsonApiEngine.init(jsonApiEngine.getmJSONObject().toString());
        }

        // add views


        jsonApiEngine.clearFormDataViews();
        renderViews();
        jsonApiEngine.invokeRefreshLogic(null, false, null, null);

    }

    // render/re-render view
    private void renderViews() {

        JSONObject step = getJsonApi().getStep(mStepName);
        try {
            mStepDetails = new JSONObject(step.toString());
        } catch (JSONException e) {
            Log.e(TAG, e.getMessage(), e);
        }

        jsonApiEngine.getFormDataViews().addAll(nativeViewInteractor.fetchFormElements(mStepName, this, mStepDetails, this, false));

        List<View> views = nativeViewInteractor.fetchFormElements(mStepName, this, mStepDetails, this, false);

        LinearLayout linearLayout = rootView.findViewById(R.id.main_layout);
        linearLayout.removeAllViews();
        for (View view : views) {
            linearLayout.addView(view);
        }
    }

    @Override
    public void onClick(View v) {
        key = (String) v.getTag(R.id.key);
        type = (String) v.getTag(R.id.type);
        switch (type) {
            case JsonFormConstants.CHOOSE_IMAGE:
                dispatchTakePictureIntent(key, type);
                break;
            case JsonFormConstants.NATIVE_RADIO_BUTTON:
                nativeRadioButtonClickActions(v);
                break;
            case JsonFormConstants.CHECK_BOX:
                if (v.getId() == R.id.label_edit_button) {
                    setCheckboxesEditable(v);
                } else {
                    showInformationDialog(v);
                }
                break;
            case JsonFormConstants.LABEL:
                showInformationDialog(v);
                break;
            case JsonFormConstants.TOASTER_NOTES:
                String info = (String) v.getTag(R.id.label_dialog_info);
                if (!TextUtils.isEmpty(info)) {
                    showInformationDialog(v);
                }
                break;
            case JsonFormConstants.NUMBER_SELECTOR:
                createNumberSelector(v);
                break;
            case JsonFormConstants.SPINNER:
                if (v.getId() == R.id.spinner_edit_button) {
                    setViewEditable(v);
                } else {
                    showInformationDialog(v);
                }
                break;
            case JsonFormConstants.EDIT_TEXT:
                setViewEditable(v);
                break;
            //case JsonFormConstants.NORMAL_EDIT_TEXT:
            //setViewEditable(v);
            //break;
            default:
                break;
        }
    }

    private void dispatchTakePictureIntent(String key, String type) {
        if (PermissionUtils.isPermissionGranted(context, new String[]{Manifest.permission.CAMERA, Manifest.permission.READ_EXTERNAL_STORAGE,
                Manifest.permission.WRITE_EXTERNAL_STORAGE}, PermissionUtils.CAMERA_PERMISSION_REQUEST_CODE)) {

            if (JsonFormConstants.CHOOSE_IMAGE.equals(type)) {
                hideKeyBoard();
                mCurrentKey = key;
                Intent takePictureIntent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
                if (takePictureIntent.resolveActivity(context.getPackageManager()) != null) {
                    File imageFile = null;
                    try {
                        imageFile = createImageFile();
                    } catch (IOException e) {
                        Log.e(TAG, Log.getStackTraceString(e));
                    }

                    if (imageFile != null) {
                        Uri photoURI = FileProvider.getUriForFile(context,
                                context.getPackageName() + ".fileprovider",
                                imageFile);

                        // Grant permission to the default camera app
                        PackageManager packageManager = context.getPackageManager();
                        Context applicationContext = context.getApplicationContext();

                        applicationContext.grantUriPermission(
                                takePictureIntent.resolveActivity(packageManager).getPackageName(),
                                photoURI,
                                Intent.FLAG_GRANT_READ_URI_PERMISSION
                                        | Intent.FLAG_GRANT_WRITE_URI_PERMISSION);

                        applicationContext.grantUriPermission(
                                "com.vijay.jsonwizard",
                                photoURI,
                                Intent.FLAG_GRANT_READ_URI_PERMISSION
                                        | Intent.FLAG_GRANT_WRITE_URI_PERMISSION);

                        takePictureIntent.putExtra(MediaStore.EXTRA_OUTPUT, photoURI);
                        try {
                            ((Activity) context).startActivityForResult(takePictureIntent, RESULT_LOAD_IMG);
                        } catch (Exception e) {
                            Log.e(TAG, e.toString());
                        }
                    }
                }
            }
        }
    }

    private File createImageFile() throws IOException {
        // Create an image file name
        String timeStamp = new SimpleDateFormat("yyyyMMdd_HHmmss").format(new Date());
        String imageFileName = "JPEG_" + timeStamp + "_";
        File storageDir = context.getExternalFilesDir(Environment.DIRECTORY_PICTURES);
        File image = File.createTempFile(
                imageFileName,
                ".jpg",
                storageDir
        );

        // Save a file: path for use with ACTION_VIEW intents
        mCurrentPhotoPath = image.getAbsolutePath();
        return image;
    }

    protected void nativeRadioButtonClickActions(View view) {
        String type = (String) view.getTag(R.id.specify_type);
        String specifyWidget = (String) view.getTag(R.id.specify_widget);
        Log.i(TAG, "The dialog content widget is this: " + specifyWidget);
        if (JsonFormConstants.CONTENT_INFO.equals(type) &&
                specifyWidget.equals(JsonFormConstants.DATE_PICKER)) {
            NativeRadioButtonFactory.showDateDialog(view, this);
        } else if (JsonFormConstants.CONTENT_INFO.equals(type) &&
                !specifyWidget.equals(JsonFormConstants.DATE_PICKER)) {
            FormUtils formUtils = new FormUtils();
            formUtils.showGenericDialog(view, this);
        } else if (view.getId() == R.id.label_edit_button) {
            setRadioViewsEditable(view);
        } else {
            showInformationDialog(view);
        }
    }

    protected void setRadioViewsEditable(View editButton) {
        RadioGroup radioGroup = (RadioGroup) editButton.getTag(R.id.editable_view);
        radioGroup.setEnabled(true);
        radioGroup.setFocusable(true);
        for (int i = 0; i < radioGroup.getChildCount(); i++) {
            View childElement = radioGroup.getChildAt(i);
            setViewGroupEditable(childElement);
        }
    }

    private void setViewGroupEditable(View childElement) {
        if (childElement instanceof ViewGroup) {
            ViewGroup group = (ViewGroup) childElement;
            for (int id = 0; id < group.getChildCount(); id++) {
                group.getChildAt(id).setFocusable(true);
                group.getChildAt(id).setEnabled(true);
                setViewGroupEditable(group.getChildAt(id));
            }
        }
    }

    protected void showInformationDialog(View view) {
        AlertDialog.Builder builderSingle = new AlertDialog.Builder(context, R.style.AppThemeAlertDialog);
        builderSingle.setTitle((String) view.getTag(R.id.label_dialog_title));
        builderSingle.setMessage((String) view.getTag(R.id.label_dialog_info));
        builderSingle.setIcon(R.drawable.ic_icon_info_filled);

        builderSingle.setNegativeButton(context.getResources().getString(R.string.ok), new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                dialog.dismiss();
            }
        });

        builderSingle.show();
    }

    private void createNumberSelector(View view) {
        CustomTextView customTextView = (CustomTextView) view;
        int item = (int) customTextView.getTag(R.id.number_selector_item);
        int numberOfSelectors = (int) customTextView.getTag(R.id.number_selector_number_of_selectors);
        if (item <= (numberOfSelectors - 1)) {
            NumberSelectorFactory.setBackgrounds(customTextView);
        }
        NumberSelectorFactory.setSelectedTextViews(customTextView);
        String parentKey = (String) customTextView.getTag(R.id.key);
        String openMrsEntityParent = (String) customTextView.getTag(R.id.openmrs_entity_parent);
        String openMrsEntity = (String) customTextView.getTag(R.id.openmrs_entity);
        String openMrsEntityId = (String) customTextView.getTag(R.id.openmrs_entity_id);
        Boolean popup = (Boolean) customTextView.getTag(R.id.extraPopup);
        if (popup == null) {
            popup = false;
        }
        String value = String.valueOf(customTextView.getText());
        try {
            getJsonApi().writeValue(mStepName, parentKey, value, openMrsEntityParent, openMrsEntity, openMrsEntityId, popup);
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }

    private void setViewEditable(View editButton) {
        View editableView = (View) editButton.getTag(R.id.editable_view);
        editableView.setEnabled(true);
        editableView.setFocusable(true);
        editableView.requestFocus();
        editableView.requestFocusFromTouch();
    }

    private void setCheckboxesEditable(View editButton) {
        List<View> checkboxLayouts = (ArrayList<View>) editButton.getTag(R.id.editable_view);
        for (View checkboxLayout : checkboxLayouts) {
            setViewGroupEditable(checkboxLayout);
        }
    }

    @Override
    public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
        String parentKey = (String) parent.getTag(R.id.key);
        String type = (String) parent.getTag(R.id.type);
        String openMrsEntityParent = (String) parent.getTag(R.id.openmrs_entity_parent);
        String openMrsEntity = (String) parent.getTag(R.id.openmrs_entity);
        String openMrsEntityId = (String) parent.getTag(R.id.openmrs_entity_id);
        CustomTextView customTextView = (CustomTextView) parent.getTag(R.id.number_selector_textview);
        Boolean popup = (Boolean) parent.getTag(R.id.extraPopup);
        if (popup == null) {
            popup = false;
        }
        if (position >= 0) {
            String value = (String) parent.getItemAtPosition(position);
            try {
                getJsonApi().writeValue(mStepName, parentKey, value, openMrsEntityParent, openMrsEntity,
                        openMrsEntityId, popup);
            } catch (JSONException e) {
                e.printStackTrace();
            }
        }

        if (JsonFormConstants.NUMBER_SELECTOR.equals(type)) {
            NumberSelectorFactory.setBackgrounds(customTextView);
            NumberSelectorFactory.setSelectedTextViews(customTextView);
            NumberSelectorFactory.setSelectedTextViewText((String) parent.getItemAtPosition(position));
        }
    }

    @Override
    public void onNothingSelected(AdapterView<?> parent) {

    }

    @Override
    public void onCheckedChanged(CompoundButton compoundButton, boolean isChecked) {

        if (compoundButton instanceof CheckBox) {
            String parentKey = (String) compoundButton.getTag(R.id.key);
            String openMrsEntityParent = (String) compoundButton.getTag(R.id.openmrs_entity_parent);
            String openMrsEntity = (String) compoundButton.getTag(R.id.openmrs_entity);
            String openMrsEntityId = (String) compoundButton.getTag(R.id.openmrs_entity_id);
            String childKey = (String) compoundButton.getTag(R.id.childKey);
            Boolean popup = (Boolean) compoundButton.getTag(R.id.extraPopup);
            if (popup == null) {
                popup = false;
            }
            JSONObject formObjectForStep = getFormObjectForStep(mStepName, parentKey);

            if (formObjectForStep != null && formObjectForStep.has(JsonFormConstants.JSON_FORM_KEY.EXCLUSIVE)) {
                try {
                    JSONArray exclusiveArray = formObjectForStep.getJSONArray(JsonFormConstants.JSON_FORM_KEY.EXCLUSIVE);
                    Set<String> exclusiveSet = new HashSet<>();
                    for (int i = 0; i < exclusiveArray.length(); i++) {
                        exclusiveSet.add(exclusiveArray.getString(i));
                    }

                    if (isChecked) {
                        if (exclusiveSet.contains(childKey)) {
                            unCheckAllExcept(parentKey, childKey, compoundButton);
                        } else {
                            for (String excludeKey : exclusiveSet) {
                                unCheck(parentKey, excludeKey, compoundButton);
                            }
                        }
                    }

                } catch (Exception e) {
                    Log.d(TAG, e.getMessage());
                }
            }


            try {
                getJsonApi().writeValue(mStepName, parentKey, JsonFormConstants.OPTIONS_FIELD_NAME, childKey, String.valueOf(compoundButton.isChecked()), openMrsEntityParent, openMrsEntity, openMrsEntityId, popup);
            } catch (JSONException e) {
                e.printStackTrace();
            }
        } else if ((compoundButton instanceof AppCompatRadioButton || compoundButton instanceof RadioButton) && isChecked) {
            String parentKey = (String) compoundButton.getTag(R.id.key);
            String openMrsEntityParent = (String) compoundButton.getTag(R.id.openmrs_entity_parent);
            String openMrsEntity = (String) compoundButton.getTag(R.id.openmrs_entity);
            String openMrsEntityId = (String) compoundButton.getTag(R.id.openmrs_entity_id);
            String childKey = (String) compoundButton.getTag(R.id.childKey);
            Boolean popup = (Boolean) compoundButton.getTag(R.id.extraPopup);
            if (popup == null) {
                popup = false;
            }

            unCheckAllExcept(parentKey, childKey, compoundButton);

            try {
                getJsonApi().writeValue(mStepName, parentKey, childKey, openMrsEntityParent,
                        openMrsEntity, openMrsEntityId, popup);
            } catch (JSONException e) {
                e.printStackTrace();
            }
        }
    }

    public void unCheck(String parentKey, String exclusiveKey, CompoundButton compoundButton) {

        ViewGroup mMainView = compoundButton instanceof CheckBox ? (ViewGroup) compoundButton.getParent().getParent() : (ViewGroup) compoundButton.getParent().getParent().getParent();
        int childCount = mMainView.getChildCount();
        for (int i = 0; i < childCount; i++) {
            View view = mMainView.getChildAt(i);

            if (view instanceof RadioButton) {
                AppCompatRadioButton radio = (((ViewGroup) view).getChildAt(0)).findViewWithTag(JsonFormConstants.NATIVE_RADIO_BUTTON);
                String parentKeyAtIndex = (String) radio.getTag(R.id.key);
                String childKeyAtIndex = (String) radio.getTag(R.id.childKey);
                if (radio.isChecked() && parentKeyAtIndex.equals(parentKey) && childKeyAtIndex.equals(exclusiveKey)) {
                    radio.setChecked(false);
                    break;
                }
            } else if (isCheckbox(view)) {
                CheckBox checkBox = ((LinearLayout) view).findViewWithTag(JsonFormConstants.CHECK_BOX);
                String parentKeyAtIndex = (String) checkBox.getTag(R.id.key);
                String childKeyAtIndex = (String) checkBox.getTag(R.id.childKey);
                if (checkBox.isChecked() && parentKeyAtIndex.equals(parentKey) && childKeyAtIndex.equals(exclusiveKey)) {
                    checkBox.setChecked(false);
                    break;
                }
            }
        }
    }

    private JSONObject getFormObjectForStep(String mStepName, String fieldKey) {
        try {
            JSONArray array = getJsonApi().getStep(mStepName).getJSONArray(JsonFormConstants.FIELDS);

            for (int i = 0; i < array.length(); i++) {
                if (array.getJSONObject(i).get(JsonFormConstants.KEY).equals(fieldKey)) {
                    return array.getJSONObject(i);
                }
            }

        } catch (JSONException e) {
            Log.d(TAG, e.getMessage());
        }
        return null;
    }

    public void unCheckAllExcept(String parentKey, String childKey, CompoundButton compoundButton) {

        ViewGroup mMainView = null;
        if (compoundButton instanceof CheckBox) {
            mMainView = (ViewGroup) compoundButton.getParent().getParent();
        }
        if (mMainView != null) {
            int childCount = mMainView.getChildCount();
            for (int i = 0; i < childCount; i++) {
                View view = mMainView.getChildAt(i);
                if (isCheckbox(view)) {
                    uncheckCheckbox(view, parentKey, childKey);
                }
            }
        }
    }

    private boolean isCheckbox(View view) {
        return view instanceof LinearLayout && view.getTag(R.id.type).equals(JsonFormConstants.CHECK_BOX + "_parent");
    }

    private void uncheckCheckbox(View view, String parentKey, String childKey) {
        CheckBox checkBox = view.findViewWithTag(JsonFormConstants.CHECK_BOX);
        String parentKeyAtIndex = (String) checkBox.getTag(R.id.key);
        String childKeyAtIndex = (String) checkBox.getTag(R.id.childKey);
        if (checkBox.isChecked() && parentKeyAtIndex.equals(parentKey) && !childKeyAtIndex.equals(childKey)) {
            checkBox.setChecked(false);
        }
    }

    @Override
    public void scrollToView(final View view) {
        view.requestFocus();
        final ScrollView mScrollView = rootView.findViewById(R.id.scroll_view);
        if (!(view instanceof MaterialEditText)) {
            mScrollView.post(new Runnable() {
                @Override
                public void run() {
                    int y = view.getBottom() - view.getHeight();
                    if (y < 0) {
                        y = 0;
                    }
                    mScrollView.scrollTo(0, y);
                }
            });
        }
    }

    @Override
    public boolean nextClick() {
        LinearLayout mainView = rootView.findViewById(R.id.main_layout);
        ValidationStatus validationStatus = null;
        try {
            validationStatus = writeValuesAndValidate(mainView);
            if (validationStatus.isValid()) {

                mStepName = mStepDetails.optString("next");
                formList.add(mStepName);
                hideKeyBoard();

                getJsonApi().clearFormDataViews();
                loadForm();
                getJsonApi().refreshCalculationLogic(null, null, false);
                getJsonApi().refreshSkipLogic(null, null, false);
                getJsonApi().refreshConstraints(null, null, false);

            } else {
                validationStatus.requestAttention();
                showToast(validationStatus.getErrorMessage());
            }
            return true;
        } catch (JSONException e) {
            e.printStackTrace();
            return false;
        }
    }

    public void hideKeyBoard() {
        View view = ((Activity) context).getCurrentFocus();
        if (view != null) {
            InputMethodManager inputManager = (InputMethodManager) context.getSystemService(
                    Context.INPUT_METHOD_SERVICE);
            inputManager.hideSoftInputFromWindow(view.getWindowToken(), InputMethodManager.HIDE_NOT_ALWAYS);
        }
    }

    public void showToast(String message) {
        Toast.makeText(context, message, Toast.LENGTH_SHORT).show();
    }

    public ValidationStatus writeValuesAndValidate(LinearLayout mainView) throws JSONException {
        ValidationStatus firstError = null;
        for (View childAt : getJsonApi().getFormDataViews()) {
            String key = (String) childAt.getTag(R.id.key);
            String openMrsEntityParent = (String) childAt.getTag(R.id.openmrs_entity_parent);
            String openMrsEntity = (String) childAt.getTag(R.id.openmrs_entity);
            String openMrsEntityId = (String) childAt.getTag(R.id.openmrs_entity_id);
            Boolean popup = (Boolean) childAt.getTag(R.id.extraPopup);

            ValidationStatus validationStatus = validate(this, childAt, firstError == null);
            if (firstError == null && !validationStatus.isValid()) {
                firstError = validationStatus;
            }

            if (childAt instanceof MaterialEditText) {
                MaterialEditText editText = (MaterialEditText) childAt;

                String rawValue = (String) editText.getTag(R.id.raw_value);
                if (rawValue == null) {
                    rawValue = editText.getText().toString();
                }

                getJsonApi().writeValue(mStepName, key, rawValue,
                        openMrsEntityParent, openMrsEntity, openMrsEntityId, popup);
            } else if (childAt instanceof NativeEditText) {
                NativeEditText editText = (NativeEditText) childAt;

                String rawValue = (String) editText.getTag(R.id.raw_value);
                if (rawValue == null) {
                    rawValue = editText.getText().toString();
                }

                getJsonApi().writeValue(mStepName, key, rawValue,
                        openMrsEntityParent, openMrsEntity, openMrsEntityId, popup);
            } else if (childAt instanceof ImageView) {
                Object path = childAt.getTag(R.id.imagePath);
                if (path instanceof String) {
                    getJsonApi().writeValue(mStepName, key, (String) path, openMrsEntityParent,
                            openMrsEntity, openMrsEntityId, popup);
                }
            } else if (childAt instanceof CheckBox) {
                String parentKey = (String) childAt.getTag(R.id.key);
                String childKey = (String) childAt.getTag(R.id.childKey);
                getJsonApi().writeValue(mStepName, parentKey, JsonFormConstants.OPTIONS_FIELD_NAME, childKey,
                        String.valueOf(((CheckBox) childAt).isChecked()), openMrsEntityParent,
                        openMrsEntity, openMrsEntityId, popup);
            } else if (childAt instanceof RadioButton) {
                String parentKey = (String) childAt.getTag(R.id.key);
                String childKey = (String) childAt.getTag(R.id.childKey);
                if (((RadioButton) childAt).isChecked()) {
                    getJsonApi().writeValue(mStepName, parentKey, childKey, openMrsEntityParent,
                            openMrsEntity, openMrsEntityId, popup);
                }
            } else if (childAt instanceof Button) {
                Button button = (Button) childAt;
                String rawValue = (String) button.getTag(R.id.raw_value);
                getJsonApi().writeValue(mStepName, key, rawValue, openMrsEntityParent, openMrsEntity, openMrsEntityId, popup);
            }
        }

        if (firstError == null) {
            return new ValidationStatus(true, null, null, null);
        } else {
            return firstError;
        }
    }

    public static ValidationStatus validate(NativeViewer formFragmentView, View childAt, boolean requestFocus) {
        if (childAt instanceof NativeEditText) {
            NativeEditText editText = (NativeEditText) childAt;
            ValidationStatus validationStatus = NativeEditTextFactory.validate(formFragmentView, editText);
            if (!validationStatus.isValid()) {
                if (requestFocus) validationStatus.requestAttention();
                return validationStatus;
            }
        } else if (childAt instanceof MaterialEditText) {
            MaterialEditText editText = (MaterialEditText) childAt;
            ValidationStatus validationStatus = EditTextFactory.validate(formFragmentView, editText);
            if (!validationStatus.isValid()) {
                if (requestFocus) validationStatus.requestAttention();
                return validationStatus;
            }
        } else if (childAt instanceof ImageView) {
            ValidationStatus validationStatus = ImagePickerFactory.validate(formFragmentView,
                    (ImageView) childAt);
            if (!validationStatus.isValid()) {
                if (requestFocus) validationStatus.requestAttention();
                return validationStatus;
            }
        } else if (childAt instanceof Button) {
            String type = (String) childAt.getTag(R.id.type);
            if (!TextUtils.isEmpty(type) && type.equals(JsonFormConstants.GPS)) {
                ValidationStatus validationStatus = GpsFactory.validate(formFragmentView, (Button) childAt);
                if (!validationStatus.isValid()) {
                    if (requestFocus) validationStatus.requestAttention();
                    return validationStatus;
                }
            }
        } else if (childAt instanceof MaterialSpinner) {
            MaterialSpinner spinner = (MaterialSpinner) childAt;
            ValidationStatus validationStatus = SpinnerFactory.validate(formFragmentView, spinner);
            if (!validationStatus.isValid()) {
                if (requestFocus) validationStatus.requestAttention();
                spinner.setError(validationStatus.getErrorMessage());
                return validationStatus;
            } else {
                spinner.setError(null);
            }
        } else if (childAt instanceof CustomTextView) {
            CustomTextView customTextView = (CustomTextView) childAt;
            String type = (String) childAt.getTag(R.id.type);
            if (!TextUtils.isEmpty(type) && type.equals(JsonFormConstants.NUMBER_SELECTOR)) {
                ValidationStatus validationStatus = NumberSelectorFactory.validate(formFragmentView, customTextView);
                if (!validationStatus.isValid()) {
                    if (requestFocus) validationStatus.requestAttention();
                    customTextView.setError(validationStatus.getErrorMessage());
                    return validationStatus;
                } else {
                    customTextView.setError(null);
                }
            }
        }

        return new ValidationStatus(true, null, null, null);
    }

    public void onSaveClick(LinearLayout mainView) throws JSONException {
        ValidationStatus validationStatus = writeValuesAndValidate(mainView);
        if (validationStatus.isValid() || Boolean.valueOf(mainView.getTag(R.id.skip_validation).toString())) {
            Intent returnIntent = new Intent();
            getJsonApi().onFormFinish();
            returnIntent.putExtra("json", getJsonApi().currentJsonState());
            returnIntent.putExtra(JsonFormConstants.SKIP_VALIDATION, Boolean.valueOf(mainView.getTag(R.id.skip_validation).toString()));
            // finishWithResult(returnIntent);
        } else {
            Toast.makeText(context, validationStatus.getErrorMessage(), Toast.LENGTH_LONG).show();
            validationStatus.requestAttention();
        }
    }

    @Override
    public boolean save(boolean res) {
        LinearLayout mainView = rootView.findViewById(R.id.main_layout);
        try {
            onSaveClick(mainView);
            return false;
        } catch (JSONException e) {
            Log.e(TAG, e.toString());
            return true;
        }
    }

    @Override
    public void backClick() {
        LinearLayout mainView = rootView.findViewById(R.id.main_layout);
        ValidationStatus validationStatus = null;
        try {
            validationStatus = writeValuesAndValidate(mainView);
            if (validationStatus.isValid()) {

                int pos = (formList.indexOf(mStepName)) - 1;

                if(pos > 0){
                    mStepName = formList.get(pos);
                }else{
                    mStepName = "step1";
                }

                hideKeyBoard();

                getJsonApi().clearFormDataViews();
                loadForm();
                getJsonApi().refreshCalculationLogic(null, null, false);
                getJsonApi().refreshSkipLogic(null, null, false);
                getJsonApi().refreshCalculationLogic(null,null, false);

            } else {
                validationStatus.requestAttention();
                showToast(validationStatus.getErrorMessage());
            }
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }

    @Override
    public JsonApi getJsonApi() {
        return jsonApiEngine;
    }

    @Override
    public FragmentManager getActivityFragmentManager() {
        return fragmentManager;
    }

    public void setFragmentManager(FragmentManager fragmentManager) {
        this.fragmentManager = fragmentManager;
    }
}