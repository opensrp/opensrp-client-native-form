package com.vijay.jsonwizard.presenters;

import android.Manifest;
import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.AlertDialog;
import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Environment;
import android.provider.MediaStore;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.support.v4.content.FileProvider;
import android.support.v7.widget.AppCompatButton;
import android.support.v7.widget.AppCompatRadioButton;
import android.text.TextUtils;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.widget.AdapterView;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.ImageView;
import android.widget.ImageView.ScaleType;
import android.widget.LinearLayout;
import android.widget.RadioGroup;
import android.widget.TextView;

import com.rengwuxian.materialedittext.MaterialEditText;
import com.rey.material.widget.Button;
import com.vijay.jsonwizard.R;
import com.vijay.jsonwizard.constants.JsonFormConstants;
import com.vijay.jsonwizard.customviews.MaterialSpinner;
import com.vijay.jsonwizard.customviews.NativeEditText;
import com.vijay.jsonwizard.customviews.RadioButton;
import com.vijay.jsonwizard.fragments.JsonFormErrorFragment;
import com.vijay.jsonwizard.fragments.JsonFormFragment;
import com.vijay.jsonwizard.interactors.JsonFormInteractor;
import com.vijay.jsonwizard.mvp.MvpBasePresenter;
import com.vijay.jsonwizard.task.ExpansionPanelGenericPopupDialogTask;
import com.vijay.jsonwizard.utils.FormUtils;
import com.vijay.jsonwizard.utils.ImageUtils;
import com.vijay.jsonwizard.utils.PermissionUtils;
import com.vijay.jsonwizard.utils.ValidationStatus;
import com.vijay.jsonwizard.views.CustomTextView;
import com.vijay.jsonwizard.views.JsonFormFragmentView;
import com.vijay.jsonwizard.viewstates.JsonFormFragmentViewState;
import com.vijay.jsonwizard.widgets.CheckBoxFactory;
import com.vijay.jsonwizard.widgets.CountDownTimerFactory;
import com.vijay.jsonwizard.widgets.EditTextFactory;
import com.vijay.jsonwizard.widgets.GpsFactory;
import com.vijay.jsonwizard.widgets.ImagePickerFactory;
import com.vijay.jsonwizard.widgets.NativeEditTextFactory;
import com.vijay.jsonwizard.widgets.NativeRadioButtonFactory;
import com.vijay.jsonwizard.widgets.NumberSelectorFactory;
import com.vijay.jsonwizard.widgets.SpinnerFactory;

import org.apache.commons.lang3.StringUtils;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.File;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.Stack;

import timber.log.Timber;

import static com.vijay.jsonwizard.utils.FormUtils.dpToPixels;

/**
 * Created by vijay on 5/14/15.
 */
public class JsonFormFragmentPresenter extends
        MvpBasePresenter<JsonFormFragmentView<JsonFormFragmentViewState>> {

    private static final String TAG = "FormFragmentPresenter";
    protected static final int RESULT_LOAD_IMG = 1;
    private final JsonFormFragment formFragment;
    protected JSONObject mStepDetails;
    protected String key;
    protected String type;
    private String mStepName;
    private String mCurrentKey;
    private String mCurrentPhotoPath;
    private JsonFormInteractor mJsonFormInteractor;
    private Map<String, ValidationStatus> invalidFields;
    private Stack<String> incorrectlyFormattedFields;
    private JsonFormErrorFragment errorFragment;
    private FormUtils formUtils = new FormUtils();

    public JsonFormFragmentPresenter(JsonFormFragment formFragment,
                                     JsonFormInteractor jsonFormInteractor) {
        this(formFragment);
        mJsonFormInteractor = jsonFormInteractor;
    }

    public JsonFormFragmentPresenter(JsonFormFragment formFragment) {
        this.formFragment = formFragment;
        mJsonFormInteractor = JsonFormInteractor.getInstance();
        invalidFields = this.formFragment.getJsonApi().getInvalidFields();
        incorrectlyFormattedFields = new Stack<>();
    }

    public void addFormElements() {
        mStepName = getView().getArguments().getString("stepName");
        JSONObject step = getView().getStep(mStepName);
        try {
            mStepDetails = new JSONObject(step.toString());
        } catch (JSONException e) {
            Log.e(TAG, e.getMessage(), e);
        }
        List<View> views = mJsonFormInteractor
                .fetchFormElements(mStepName, formFragment, mStepDetails, getView().getCommonListener(),
                        false);
        getView().addFormElements(views);

    }

    @SuppressLint("ResourceAsColor")
    public void setUpToolBar() {
        getView().setActionBarTitle(mStepDetails.optString(JsonFormConstants.STEP_TITLE));
        getView().setToolbarTitleColor(R.color.white);
        if (mStepDetails.has("bottom_navigation")) {
            getView().updateVisibilityOfNextAndSave(false, false);
            return;
        }
        if (!mStepName.equals(JsonFormConstants.FIRST_STEP_NAME)) {
            getView().setUpBackButton();
        }

        if (mStepDetails.has("next")) {
            getView().updateVisibilityOfNextAndSave(true, false);
        } else {
            getView().updateVisibilityOfNextAndSave(false, true);
        }
    }

    public void onBackClick() {
        getView().hideKeyBoard();
        getView().backClick();
    }

    public Stack<String> getIncorrectlyFormattedFields() {
        return incorrectlyFormattedFields;
    }

    public JsonFormErrorFragment getErrorFragment() {
        return errorFragment;
    }

    public void setErrorFragment(JsonFormErrorFragment errorFragment) {
        this.errorFragment = errorFragment;
    }

    public boolean onNextClick(LinearLayout mainView) {
        validateAndWriteValues();
        checkAndStopCountdownAlarm();
        boolean validateOnSubmit = validateOnSubmit();
        if (validateOnSubmit && incorrectlyFormattedFields.isEmpty()) {
            return moveToNextStep();
        } else if (isFormValid()) {
            return moveToNextStep();
        } else {
            getView().showSnackBar(
                    getView().getContext().getResources().getString(R.string.json_form_on_next_error_msg));
        }
        return false;
    }

    public void validateAndWriteValues() {
        for (View childAt : formFragment.getJsonApi().getFormDataViews()) {
            ValidationStatus validationStatus = validateView(childAt);
            String key = (String) childAt.getTag(R.id.key);
            String openMrsEntityParent = (String) childAt.getTag(R.id.openmrs_entity_parent);
            String openMrsEntity = (String) childAt.getTag(R.id.openmrs_entity);
            String openMrsEntityId = (String) childAt.getTag(R.id.openmrs_entity_id);
            Boolean popup = (Boolean) childAt.getTag(R.id.extraPopup);
            String fieldKey = mStepName + "#" + getStepTitle() + ":" + key;

            if (childAt instanceof MaterialEditText) {
                MaterialEditText editText = (MaterialEditText) childAt;

                String rawValue = (String) editText.getTag(R.id.raw_value);
                if (rawValue == null) {
                    rawValue = editText.getText().toString();
                }

                handleWrongFormatInputs(validationStatus, fieldKey, rawValue);

                String type = (String) childAt.getTag(R.id.type);
                rawValue = JsonFormConstants.DATE_PICKER.equals(type) || JsonFormConstants.TIME_PICKER.
                        equals(type) ? childAt.getTag(R.id.locale_independent_value).toString() : rawValue;
                Log.d("Writing values ..", key + " " + rawValue);

                getView().writeValue(mStepName, key, rawValue, openMrsEntityParent, openMrsEntity,
                        openMrsEntityId, popup);
            } else if (childAt instanceof NativeEditText) {
                NativeEditText editText = (NativeEditText) childAt;

                String rawValue = (String) editText.getTag(R.id.raw_value);
                if (rawValue == null) {
                    rawValue = editText.getText().toString();
                }

                handleWrongFormatInputs(validationStatus, fieldKey, rawValue);

                getView().writeValue(mStepName, key, rawValue, openMrsEntityParent, openMrsEntity,
                        openMrsEntityId, popup);
            } else if (childAt instanceof ImageView) {
                Object path = childAt.getTag(R.id.imagePath);
                if (path instanceof String) {
                    getView().writeValue(mStepName, key, (String) path, openMrsEntityParent, openMrsEntity,
                            openMrsEntityId,
                            popup);
                }
            } else if (childAt instanceof CheckBox) {
                String parentKey = (String) childAt.getTag(R.id.key);
                String childKey = (String) childAt.getTag(R.id.childKey);
                getView().writeValue(mStepName, parentKey, JsonFormConstants.OPTIONS_FIELD_NAME, childKey,
                        String.valueOf(((CheckBox) childAt).isChecked()), openMrsEntityParent, openMrsEntity,
                        openMrsEntityId, popup);
            } else if (childAt instanceof RadioButton) {
                String parentKey = (String) childAt.getTag(R.id.key);
                String childKey = (String) childAt.getTag(R.id.childKey);
                if (((RadioButton) childAt).isChecked()) {
                    getView().writeValue(mStepName, parentKey, childKey, openMrsEntityParent, openMrsEntity,
                            openMrsEntityId,
                            popup);
                }
            } else if (childAt instanceof Button) {
                Button button = (Button) childAt;
                String rawValue = (String) button.getTag(R.id.raw_value);
                getView().writeValue(mStepName, key, rawValue, openMrsEntityParent, openMrsEntity,
                        openMrsEntityId, popup);
            }

            if (!validationStatus.isValid()) {
                invalidFields.put(fieldKey, validationStatus);
            } else {
                if (invalidFields.size() > 0) {
                    invalidFields.remove(fieldKey);
                }
            }

        }
        formFragment.onFieldsInvalid.passInvalidFields(invalidFields);
    }

    /**
     * Check if alarm is ringing and stop it if so
     */
    public void checkAndStopCountdownAlarm() {
        try {
            JSONObject formJSONObject = new JSONObject(formFragment.getCurrentJsonState());
            JSONArray fields = FormUtils.fields(formJSONObject, mStepName);
            for (int i = 0; i < fields.length(); i++) {
                JSONObject fieldObject = (JSONObject) fields.get(i);
                if (fieldObject.has(JsonFormConstants.COUNTDOWN_TIME_VALUE)) {
                    CountDownTimerFactory.stopAlarm();
                }
            }
        } catch (Exception e) {
            Timber.e(e, "Countdown alarm could not be stopped!");
        }
    }

    public boolean validateOnSubmit() {
        JSONObject entireJsonForm = formFragment.getJsonApi().getmJSONObject();
        return entireJsonForm.optBoolean(JsonFormConstants.VALIDATE_ON_SUBMIT, false);
    }

    private boolean moveToNextStep() {
        if (!"".equals(mStepDetails.optString(JsonFormConstants.NEXT))) {
            JsonFormFragment next = JsonFormFragment
                    .getFormFragment(mStepDetails.optString(JsonFormConstants.NEXT));
            getView().hideKeyBoard();
            getView().transactThis(next);
            return true;
        }
        return false;
    }

    /**
     * Check if form is valid
     *
     * @return true if invalidFields is empty otherwise false
     */
    public boolean isFormValid() {
        return getInvalidFields().size() == 0;
    }

    /**
     * Validates the passed view
     *
     * @param childAt view to be validated
     * @return ValidationStatus for the view
     */
    private ValidationStatus validateView(View childAt) {
        return validate(getView(), childAt, true);
    }

    private String getStepTitle() {
        return mStepDetails.optString(JsonFormConstants.STEP_TITLE);
    }

    private void handleWrongFormatInputs(ValidationStatus validationStatus, String fieldKey,
                                         String rawValue) {
        if (!TextUtils.isEmpty(rawValue) && !validationStatus.isValid()) {
            if (!incorrectlyFormattedFields.contains(fieldKey)) {
                incorrectlyFormattedFields.push(fieldKey);
            }
        } else if (!TextUtils.isEmpty(rawValue) && validationStatus.isValid()) {
            incorrectlyFormattedFields.remove(fieldKey);
        } else if ((TextUtils.isEmpty(rawValue) && !validationStatus.isValid())) {
            incorrectlyFormattedFields.remove(fieldKey);
        }
    }

    public Map<String, ValidationStatus> getInvalidFields() {
        return invalidFields;
    }

    public static ValidationStatus validate(JsonFormFragmentView formFragmentView, View childAt,
                                            boolean requestFocus) {
        if (childAt instanceof RadioGroup) {
            RadioGroup radioGroup = (RadioGroup) childAt;
            ValidationStatus validationStatus = NativeRadioButtonFactory
                    .validate(formFragmentView, radioGroup);
            if (!validationStatus.isValid()) {
                if (requestFocus) {
                    validationStatus.requestAttention();
                }
                return validationStatus;
            }
        } else if (childAt instanceof NativeEditText) {
            NativeEditText editText = (NativeEditText) childAt;
            ValidationStatus validationStatus = NativeEditTextFactory
                    .validate(formFragmentView, editText);
            if (!validationStatus.isValid()) {
                if (requestFocus) {
                    validationStatus.requestAttention();
                }
                return validationStatus;
            }
        } else if (childAt instanceof MaterialEditText) {
            MaterialEditText editText = (MaterialEditText) childAt;
            ValidationStatus validationStatus = EditTextFactory.validate(formFragmentView, editText);
            if (!validationStatus.isValid()) {
                if (requestFocus) {
                    validationStatus.requestAttention();
                }
                return validationStatus;
            }
        } else if (childAt instanceof ImageView) {
            ValidationStatus validationStatus = ImagePickerFactory
                    .validate(formFragmentView, (ImageView) childAt);
            if (!validationStatus.isValid()) {
                if (requestFocus) {
                    validationStatus.requestAttention();
                }
                return validationStatus;
            }
        } else if (childAt instanceof Button) {
            String type = (String) childAt.getTag(R.id.type);
            if (!TextUtils.isEmpty(type) && type.equals(JsonFormConstants.GPS)) {
                ValidationStatus validationStatus = GpsFactory.validate(formFragmentView, (Button) childAt);
                if (!validationStatus.isValid()) {
                    if (requestFocus) {
                        validationStatus.requestAttention();
                    }
                    return validationStatus;
                }
            }
        } else if (childAt instanceof MaterialSpinner) {
            MaterialSpinner spinner = (MaterialSpinner) childAt;
            ValidationStatus validationStatus = SpinnerFactory.validate(formFragmentView, spinner);
            if (!validationStatus.isValid()) {
                if (requestFocus) {
                    validationStatus.requestAttention();
                }
                setSpinnerError(spinner, validationStatus.getErrorMessage());
                return validationStatus;
            }
        } else if (childAt instanceof ViewGroup
                && childAt.getTag(R.id.is_checkbox_linear_layout) != null &&
                Boolean.TRUE.equals(childAt.getTag(R.id.is_checkbox_linear_layout))) {
            LinearLayout checkboxLinearLayout = (LinearLayout) childAt;
            ValidationStatus validationStatus = CheckBoxFactory
                    .validate(formFragmentView, checkboxLinearLayout);
            if (!validationStatus.isValid()) {
                if (requestFocus) {
                    validationStatus.requestAttention();
                }
                return validationStatus;
            }

        } else if (childAt instanceof ViewGroup
                && childAt.getTag(R.id.is_number_selector_linear_layout) != null &&
                Boolean.TRUE.equals(childAt.getTag(R.id.is_number_selector_linear_layout))) {
            ValidationStatus validationStatus = NumberSelectorFactory
                    .validate(formFragmentView, (ViewGroup) childAt);
            if (!validationStatus.isValid()) {
                if (requestFocus) {
                    validationStatus.requestAttention();
                }
                return validationStatus;
            }
        }

        return new ValidationStatus(true, null, null, null);
    }

    private static void setSpinnerError(MaterialSpinner spinner, String spinnerError) {
        try {
            spinner.setError(spinnerError);
        } catch (IllegalArgumentException e) {
            Log.e(TAG, e.getMessage(), e);
        }
    }

    public void onSaveClick(LinearLayout mainView) {
        validateAndWriteValues();
        checkAndStopCountdownAlarm();
        boolean isFormValid = isFormValid();
        if (isFormValid || Boolean.valueOf(mainView.getTag(R.id.skip_validation).toString())) {
            Intent returnIntent = new Intent();
            getView().onFormFinish();
            returnIntent.putExtra("json", formUtils.addFormDetails(getView().getCurrentJsonState()));
            returnIntent.putExtra(JsonFormConstants.SKIP_VALIDATION,
                    Boolean.valueOf(mainView.getTag(R.id.skip_validation).toString()));
            getView().finishWithResult(returnIntent);
        } else {
            if (showErrorsOnSubmit()) {
                launchErrorDialog();
                getView().showToast(getView().getContext().getResources()
                        .getString(R.string.json_form_error_msg, getInvalidFields().size()));
            } else {
                getView().showSnackBar(getView().getContext().getResources()
                        .getString(R.string.json_form_error_msg, getInvalidFields().size()));

            }
        }
    }

    public boolean showErrorsOnSubmit() {
        JSONObject entireJsonForm = formFragment.getJsonApi().getmJSONObject();
        return entireJsonForm.optBoolean(JsonFormConstants.SHOW_ERRORS_ON_SUBMIT, false);
    }

    protected void launchErrorDialog() {
        if (errorFragment == null) {
            errorFragment = new JsonFormErrorFragment();
        }
        FragmentManager fm = ((JsonFormFragment) getView()).getChildFragmentManager();
        @SuppressLint("CommitTransaction") FragmentTransaction ft = fm.beginTransaction();
        errorFragment.show(ft, JsonFormErrorFragment.TAG);
    }

    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (requestCode == RESULT_LOAD_IMG && resultCode == Activity.RESULT_OK) {
            String imagePath = mCurrentPhotoPath;
            getView().updateRelevantImageView(ImageUtils
                    .loadBitmapFromFile(getView().getContext(), imagePath,
                            ImageUtils.getDeviceWidth(getView().getContext()),
                            dpToPixels(getView().getContext(), 200)), imagePath, mCurrentKey);
            //cursor.close();
        }
    }

    public void onRequestPermissionsResult(int requestCode, String[] permissions,
                                           int[] grantResults) {
        Log.d(TAG, "Permission callback called-------");

        if (grantResults.length == 0) {
            return;
        }

        switch (requestCode) {
            case PermissionUtils.CAMERA_PERMISSION_REQUEST_CODE:
                if (PermissionUtils
                        .verifyPermissionGranted(permissions, grantResults, Manifest.permission.CAMERA,
                                Manifest.permission.READ_EXTERNAL_STORAGE,
                                Manifest.permission.WRITE_EXTERNAL_STORAGE)) {
                    dispatchTakePictureIntent(key, type);
                }
                break;

            case PermissionUtils.PHONE_STATE_PERMISSION_REQUEST_CODE:
                //TODO Find out functionality which uses Read Phone State permission
                break;
            default:
                break;

        }
    }

    private void dispatchTakePictureIntent(String key, String type) {
        if (PermissionUtils.isPermissionGranted(formFragment,
                new String[]{Manifest.permission.CAMERA, Manifest.permission.READ_EXTERNAL_STORAGE,
                        Manifest.permission.WRITE_EXTERNAL_STORAGE},
                PermissionUtils.CAMERA_PERMISSION_REQUEST_CODE)) {

            if (JsonFormConstants.CHOOSE_IMAGE.equals(type)) {
                getView().hideKeyBoard();
                mCurrentKey = key;
                Intent takePictureIntent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
                if (takePictureIntent.resolveActivity(getView().getContext().getPackageManager()) != null) {
                    File imageFile = null;
                    try {
                        imageFile = createImageFile();
                    } catch (IOException e) {
                        Log.e(TAG, Log.getStackTraceString(e));
                    }

                    if (imageFile != null) {
                        Uri photoURI = FileProvider.getUriForFile(getView().getContext(),
                                getView().getContext().getPackageName() + "" + ".fileprovider", imageFile);

                        // Grant permission to the default camera app
                        PackageManager packageManager = getView().getContext().getPackageManager();
                        Context applicationContext = getView().getContext().getApplicationContext();

                        applicationContext
                                .grantUriPermission(
                                        takePictureIntent.resolveActivity(packageManager).getPackageName(),
                                        photoURI,
                                        Intent.FLAG_GRANT_READ_URI_PERMISSION | Intent.FLAG_GRANT_WRITE_URI_PERMISSION);

                        applicationContext.grantUriPermission("com.vijay.jsonwizard", photoURI,
                                Intent.FLAG_GRANT_READ_URI_PERMISSION | Intent.FLAG_GRANT_WRITE_URI_PERMISSION);

                        takePictureIntent.putExtra(MediaStore.EXTRA_OUTPUT, photoURI);
                        getView().startActivityForResult(takePictureIntent, RESULT_LOAD_IMG);
                    }
                }
            }
        }
    }

    private File createImageFile() throws IOException {
        // Create an image file name
        String timeStamp = new SimpleDateFormat("yyyyMMdd_HHmmss").format(new Date());
        String imageFileName = "JPEG_" + timeStamp + "_";
        File storageDir = getView().getContext().getExternalFilesDir(Environment.DIRECTORY_PICTURES);
        File image = File.createTempFile(imageFileName, ".jpg", storageDir);

        // Save a file: path for use with ACTION_VIEW intents
        mCurrentPhotoPath = image.getAbsolutePath();
        return image;
    }

    public void onClick(View v) {
        key = (String) v.getTag(R.id.key);
        type = (String) v.getTag(R.id.type);
        switch (type) {
            case JsonFormConstants.FINGER_PRINT:
                openSimprints(v);
                break;
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
                String infoIcon = (String) v.getTag(R.id.label_dialog_info);
                if (!TextUtils.isEmpty(infoIcon)) {
                    showInformationDialog(v);
                } else {
                    setViewEditable(v);
                }
                break;
            case JsonFormConstants.NATIVE_EDIT_TEXT:
                setViewEditable(v);
                break;
            case JsonFormConstants.DATE_PICKER:
                if (v.getId() == R.id.date_picker_info_icon) {
                    showInformationDialog(v);
                }
                break;
            default:

                break;
        }
    }

    private void openSimprints(View v) {
        String projectId = (String) v.getTag(R.id.project_id);
        String userId = (String) v.getTag(R.id.user_id);
        String moduleId = (String) v.getTag(R.id.module_id);
        String fingerPrintOption = (String) v.getTag(R.id.finger_print_option);
        if (!TextUtils.isEmpty(fingerPrintOption) && fingerPrintOption.equalsIgnoreCase(JsonFormConstants.SIMPRINTS_OPTION_REGISTER)) {
            getView().startSimprintsRegistration(projectId, userId, moduleId);

        } else if (!TextUtils.isEmpty(fingerPrintOption) && fingerPrintOption.equalsIgnoreCase(JsonFormConstants.SIMPRINTS_OPTION_VERIFY)) {
            String guId = (String) v.getTag(R.id.guid);
            getView().startSimprintsVerification(projectId, userId, moduleId, guId);
        }

    }

    protected void nativeRadioButtonClickActions(View view) {
        String type = (String) view.getTag(R.id.specify_type);
        String specifyWidget = (String) view.getTag(R.id.specify_widget);
        Timber.i("The dialog content widget is this: " + specifyWidget);
        if (JsonFormConstants.CONTENT_INFO.equals(type) && specifyWidget
                .equals(JsonFormConstants.DATE_PICKER)) {
            NativeRadioButtonFactory.showDateDialog(view);
        } else if (JsonFormConstants.CONTENT_INFO.equals(type) && !specifyWidget.equals(JsonFormConstants.DATE_PICKER)) {
            new ExpansionPanelGenericPopupDialogTask(view).execute();
        } else if (view.getId() == R.id.label_edit_button) {
            setRadioViewsEditable(view);
        } else {
            showInformationDialog(view);
        }
    }

    private void setViewEditable(View editButton) {
        resetWidgetReadOnly(editButton);
        View editableView = (View) editButton.getTag(R.id.editable_view);
        editableView.setEnabled(true);
        editableView.setFocusable(true);
        editableView.requestFocus();
        editableView.requestFocusFromTouch();
    }

    private void resetWidgetReadOnly(View view) {
        JSONObject jsonForm = this.formFragment.getJsonApi().getmJSONObject();
        JSONObject field;
        try {
            field = FormUtils.getFieldFromForm(jsonForm, (String) view.getTag(R.id.key));
            if (field.has(JsonFormConstants.READ_ONLY) && field.getBoolean(JsonFormConstants.READ_ONLY)) {
                field.put(JsonFormConstants.READ_ONLY, false);
            }
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }

    @SuppressWarnings({"unchecked"})
    private void setCheckboxesEditable(View editButton) {
        List<View> checkboxLayouts = (ArrayList<View>) editButton.getTag(R.id.editable_view);
        resetWidgetReadOnly(editButton);
        for (View checkboxLayout : checkboxLayouts) {
            setViewGroupEditable(checkboxLayout);
        }
    }

    protected void setRadioViewsEditable(View editButton) {
        RadioGroup radioGroup = (RadioGroup) editButton.getTag(R.id.editable_view);
        resetWidgetReadOnly(editButton);
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

        if (view.getTag(R.id.label_dialog_image_src) != null) {
            showCustomDialog(view);
        } else {
            showAlertDialog(view);
        }

    }

    private void showAlertDialog(View view) {
        AlertDialog.Builder builderSingle = new AlertDialog.Builder(getView().getContext(),
                R.style.AppThemeAlertDialog);
        builderSingle.setTitle((String) view.getTag(R.id.label_dialog_title));
        builderSingle.setMessage((String) view.getTag(R.id.label_dialog_info));
        builderSingle.setIcon(R.drawable.dialog_info_filled);

        builderSingle.setNegativeButton(getView().getContext().getResources().getString(R.string.ok),
                new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        dialog.dismiss();
                    }
                });

        builderSingle.show();
    }

    private void showCustomDialog(View view) {
        final Dialog dialog = new Dialog(view.getContext());
        dialog.setContentView(R.layout.native_form_custom_dialog);
        if (dialog.getWindow() != null) {
            WindowManager.LayoutParams params = dialog.getWindow().getAttributes();
            params.width = (int) (view.getContext().getResources().getDisplayMetrics().widthPixels
                    * 0.90);
            params.height = ViewGroup.LayoutParams.WRAP_CONTENT;
        }


        String titleString = (String) view.getTag(R.id.label_dialog_title);
        if (StringUtils.isNotBlank(titleString)) {
            TextView dialogTitle = dialog.findViewById(R.id.dialogTitle);
            dialogTitle.setText(titleString);
            dialogTitle.setVisibility(View.VISIBLE);
        }

        String dialogString = (String) view.getTag(R.id.label_dialog_info);
        if (StringUtils.isNotBlank(dialogString)) {
            TextView dialogText = dialog.findViewById(R.id.dialogText);
            dialogText.setText(dialogString);
            dialogText.setVisibility(View.VISIBLE);
        }

        ImageView dialogImage = dialog.findViewById(R.id.dialogImage);
        dialogImage.setFitsSystemWindows(true);
        dialogImage.setScaleType(ScaleType.FIT_XY);
        AppCompatButton dialogButton = dialog.findViewById(R.id.dialogButton);
        String imagePath = (String) view.getTag(R.id.label_dialog_image_src);


        dialogImage.setVisibility(View.VISIBLE);
        try {
            dialogImage.setImageDrawable(FormUtils.readImageFromAsset(view.getContext(), imagePath));
        } catch (IOException e) {
            Log.e(TAG, "Encountered an error reading image from assets" + e);
        }
        dialogButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dialog.dismiss();
            }
        });
        dialog.show();
    }

    //household_photo,JsonFormConstants.CHOOSE_IMAGE
    public void onClickCameraIcon(String key, String type) {

        dispatchTakePictureIntent(key, type);
    }

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

            if (formObjectForStep != null && formObjectForStep
                    .has(JsonFormConstants.JSON_FORM_KEY.EXCLUSIVE)) {
                try {
                    JSONArray exclusiveArray = formObjectForStep
                            .getJSONArray(JsonFormConstants.JSON_FORM_KEY.EXCLUSIVE);
                    Set<String> exclusiveSet = new HashSet<>();
                    for (int i = 0; i < exclusiveArray.length(); i++) {
                        exclusiveSet.add(exclusiveArray.getString(i));
                    }

                    if (isChecked) {
                        if (exclusiveSet.contains(childKey)) {
                            getView().unCheckAllExcept(parentKey, childKey, compoundButton);
                        } else {
                            for (String excludeKey : exclusiveSet) {
                                getView().unCheck(parentKey, excludeKey, compoundButton);
                            }
                        }
                    }

                } catch (Exception e) {
                    Log.e(TAG, e.getMessage(), e);
                }
            }

            getView().writeValue(mStepName, parentKey, JsonFormConstants.OPTIONS_FIELD_NAME, childKey,
                    String.valueOf(compoundButton.isChecked()), openMrsEntityParent, openMrsEntity,
                    openMrsEntityId, popup);
        } else if (
                (compoundButton instanceof AppCompatRadioButton || compoundButton instanceof RadioButton)
                        && isChecked) {
            String parentKey = (String) compoundButton.getTag(R.id.key);
            String openMrsEntityParent = (String) compoundButton.getTag(R.id.openmrs_entity_parent);
            String openMrsEntity = (String) compoundButton.getTag(R.id.openmrs_entity);
            String openMrsEntityId = (String) compoundButton.getTag(R.id.openmrs_entity_id);
            String childKey = (String) compoundButton.getTag(R.id.childKey);
            Boolean popup = (Boolean) compoundButton.getTag(R.id.extraPopup);
            if (popup == null) {
                popup = false;
            }

            String specifyWidget = (String) compoundButton.getTag(R.id.specify_widget);
            if (!TextUtils.isEmpty(specifyWidget)) {
                nativeRadioButtonClickActions(compoundButton);
            }

            getView().unCheckAllExcept(parentKey, childKey, compoundButton);
            getView().writeValue(mStepName, parentKey, childKey, openMrsEntityParent, openMrsEntity,
                    openMrsEntityId, popup);
        }
    }

    public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
        String parentKey = (String) parent.getTag(R.id.key);
        String openMrsEntityParent = (String) parent.getTag(R.id.openmrs_entity_parent);
        String openMrsEntity = (String) parent.getTag(R.id.openmrs_entity);
        String openMrsEntityId = (String) parent.getTag(R.id.openmrs_entity_id);
        JSONArray jsonArray = (JSONArray) parent.getTag(R.id.keys);
        Boolean popup = (Boolean) parent.getTag(R.id.extraPopup);
        if (popup == null) {
            popup = false;
        }

        String value = parent.getItemAtPosition(position).toString();
        if (jsonArray != null && position > -1 && jsonArray.length() > 0) {
            try {
                value = jsonArray.getString(position);
            } catch (JSONException e) {
                Log.e(TAG, e.toString());
            }
        }
        if (getView() != null) {
            getView().writeValue(mStepName, parentKey, value, openMrsEntityParent, openMrsEntity,
                    openMrsEntityId, popup);
        }
    }

    private void createNumberSelector(View view) {
        Object isNumberSelectorTextView = view.getTag(R.id.is_number_selector_dialog_textview);
        CustomTextView customTextView = (CustomTextView) view;

        if (isNumberSelectorTextView != null && (boolean) isNumberSelectorTextView) {
            NumberSelectorFactory.createNumberSelector(customTextView);
            //Save the last value if none is selected
            if (customTextView.getText().toString().contains("+")) {
                String currentText = customTextView.getText().toString().replace("+", "");
                customTextView.setText(currentText);
                saveValueFromCustomView(customTextView);
            }
        } else {
            int item = (int) customTextView.getTag(R.id.number_selector_item);
            int numberOfSelectors = (int) customTextView.getTag(R.id.number_selector_number_of_selectors);
            if (item <= (numberOfSelectors - 1)) {
                NumberSelectorFactory.setBackgrounds(customTextView);
            }
            NumberSelectorFactory.setSelectedTextViews(customTextView);
            saveValueFromCustomView(customTextView);
        }

    }

    private void saveValueFromCustomView(CustomTextView customTextView) {
        //String parentKey = (String) customTextView.getTag(R.id.key);
        String parentKey = (String) ((View) customTextView.getParent()).getTag(R.id.key);
        String openMrsEntityParent = (String) customTextView.getTag(R.id.openmrs_entity_parent);
        String openMrsEntity = (String) customTextView.getTag(R.id.openmrs_entity);
        String openMrsEntityId = (String) customTextView.getTag(R.id.openmrs_entity_id);
        Boolean popup = (Boolean) customTextView.getTag(R.id.extraPopup);

        if (popup == null) {
            popup = false;
        }
        String value = String.valueOf(customTextView.getText());
        getView().writeValue(mStepName, parentKey, value, openMrsEntityParent, openMrsEntity,
                openMrsEntityId, popup);
    }

    private JSONObject getFormObjectForStep(String mStepName, String fieldKey) {
        try {
            JSONArray array = getView().getStep(mStepName).getJSONArray(JsonFormConstants.FIELDS);

            for (int i = 0; i < array.length(); i++) {
                if (array.getJSONObject(i).get(JsonFormConstants.KEY).equals(fieldKey)) {
                    return array.getJSONObject(i);
                }
            }

        } catch (JSONException e) {
            Log.e(TAG, e.getMessage(), e);
        }
        return null;
    }

    public JsonFormInteractor getInteractor() {
        return mJsonFormInteractor;
    }

    public boolean onMenuItemClick(MenuItem item) {
        Intent intent = item.getIntent();
        if (intent != null && intent.getBooleanExtra(JsonFormConstants.IS_NUMBER_SELECTOR_MENU, true)) {
            String parentKey = intent.getStringExtra(JsonFormConstants.PARENT_KEY);
            String value = item.getTitle().toString();
            String openMrsEntityParent = intent.getStringExtra(JsonFormConstants.OPENMRS_ENTITY_PARENT);
            String openMrsEntity = intent.getStringExtra(JsonFormConstants.OPENMRS_ENTITY);
            String openMrsEntityId = intent.getStringExtra(JsonFormConstants.OPENMRS_ENTITY_ID);
            boolean popup = intent.getBooleanExtra(JsonFormConstants.IS_POPUP, false);
            NumberSelectorFactory.setSelectedTextViewText(value);
            getView().writeValue(mStepName, parentKey, value, openMrsEntityParent, openMrsEntity,
                    openMrsEntityId, popup);

            return true;
        }

        return false;
    }

    public String getmCurrentPhotoPath() {
        return mCurrentPhotoPath;
    }
}
