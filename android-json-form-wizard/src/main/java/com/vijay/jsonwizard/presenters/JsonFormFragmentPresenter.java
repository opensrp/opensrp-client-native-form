package com.vijay.jsonwizard.presenters;

import android.Manifest;
import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Environment;
import android.provider.MediaStore;
import android.support.v4.content.FileProvider;
import android.support.v7.widget.AppCompatRadioButton;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.CompoundButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RadioGroup;
import android.widget.Toast;

import com.rengwuxian.materialedittext.MaterialEditText;
import com.rey.material.widget.Button;
import com.vijay.jsonwizard.R;
import com.vijay.jsonwizard.constants.JsonFormConstants;
import com.vijay.jsonwizard.customviews.CheckBox;
import com.vijay.jsonwizard.customviews.MaterialSpinner;
import com.vijay.jsonwizard.customviews.NativeEditText;
import com.vijay.jsonwizard.customviews.RadioButton;
import com.vijay.jsonwizard.fragments.JsonFormFragment;
import com.vijay.jsonwizard.interactors.JsonFormInteractor;
import com.vijay.jsonwizard.interfaces.NativeViewer;
import com.vijay.jsonwizard.mvp.MvpBasePresenter;
import com.vijay.jsonwizard.utils.FormUtils;
import com.vijay.jsonwizard.utils.ImageUtils;
import com.vijay.jsonwizard.utils.PermissionUtils;
import com.vijay.jsonwizard.utils.ValidationStatus;
import com.vijay.jsonwizard.views.CustomTextView;
import com.vijay.jsonwizard.views.JsonFormFragmentView;
import com.vijay.jsonwizard.viewstates.JsonFormFragmentViewState;
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
import java.util.List;
import java.util.Set;

import static com.vijay.jsonwizard.utils.FormUtils.dpToPixels;

/**
 * Created by vijay on 5/14/15.
 */
public class JsonFormFragmentPresenter extends MvpBasePresenter<JsonFormFragmentView<JsonFormFragmentViewState>> {
    private static final String TAG = "FormFragmentPresenter";
    private static final int RESULT_LOAD_IMG = 1;
    private final JsonFormFragment formFragment;
    protected JSONObject mStepDetails;
    protected String key;
    protected String type;
    private String mStepName;
    private String mCurrentKey;
    private String mCurrentPhotoPath;
    private JsonFormInteractor mJsonFormInteractor;

    public JsonFormFragmentPresenter(JsonFormFragment formFragment) {
        this.formFragment = formFragment;
        mJsonFormInteractor = JsonFormInteractor.getInstance();
    }

    public JsonFormFragmentPresenter(JsonFormFragment formFragment, JsonFormInteractor jsonFormInteractor) {
        this(formFragment);
        mJsonFormInteractor = jsonFormInteractor;
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
            if (!TextUtils.isEmpty(type) && type.equals(JsonFormConstants.NUMBERS_SELECTOR)) {
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

    public void addFormElements() {
        mStepName = getView().getArguments().getString("stepName");
        JSONObject step = getView().getStep(mStepName);
        try {
            mStepDetails = new JSONObject(step.toString());
        } catch (JSONException e) {
            Log.e(TAG, e.getMessage(), e);
        }
        List<View> views = mJsonFormInteractor.fetchFormElements(mStepName, formFragment, mStepDetails,
                getView().getCommonListener(), false);
        getView().addFormElements(views);

    }

    @SuppressLint("ResourceAsColor")
    public void setUpToolBar() {
        getView().setActionBarTitle(mStepDetails.optString("title"));
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

    public void onNextClick(LinearLayout mainView) {
        ValidationStatus validationStatus = writeValuesAndValidate(mainView);
        if (validationStatus.isValid()) {
            JsonFormFragment next = JsonFormFragment.getFormFragment(mStepDetails.optString("next"));
            getView().hideKeyBoard();
            getView().transactThis(next);
        } else {
            validationStatus.requestAttention();
            getView().showToast(validationStatus.getErrorMessage());
        }
    }


    public ValidationStatus writeValuesAndValidate(LinearLayout mainView) {
        ValidationStatus firstError = null;
        for (View childAt : formFragment.getJsonApi().getFormDataViews()) {
            String key = (String) childAt.getTag(R.id.key);
            String openMrsEntityParent = (String) childAt.getTag(R.id.openmrs_entity_parent);
            String openMrsEntity = (String) childAt.getTag(R.id.openmrs_entity);
            String openMrsEntityId = (String) childAt.getTag(R.id.openmrs_entity_id);
            Boolean popup = (Boolean) childAt.getTag(R.id.extraPopup);

            ValidationStatus validationStatus = validate((NativeViewer)getView(), childAt, firstError == null);
            if (firstError == null && !validationStatus.isValid()) {
                firstError = validationStatus;
            }

            if (childAt instanceof MaterialEditText) {
                MaterialEditText editText = (MaterialEditText) childAt;

                String rawValue = (String) editText.getTag(R.id.raw_value);
                if (rawValue == null) {
                    rawValue = editText.getText().toString();
                }

                getView().writeValue(mStepName, key, rawValue,
                        openMrsEntityParent, openMrsEntity, openMrsEntityId, popup);
            } else if (childAt instanceof NativeEditText) {
                NativeEditText editText = (NativeEditText) childAt;

                String rawValue = (String) editText.getTag(R.id.raw_value);
                if (rawValue == null) {
                    rawValue = editText.getText().toString();
                }

                getView().writeValue(mStepName, key, rawValue,
                        openMrsEntityParent, openMrsEntity, openMrsEntityId, popup);
            } else if (childAt instanceof ImageView) {
                Object path = childAt.getTag(R.id.imagePath);
                if (path instanceof String) {
                    getView().writeValue(mStepName, key, (String) path, openMrsEntityParent,
                            openMrsEntity, openMrsEntityId, popup);
                }
            } else if (childAt instanceof CheckBox) {
                String parentKey = (String) childAt.getTag(R.id.key);
                String childKey = (String) childAt.getTag(R.id.childKey);
                getView().writeValue(mStepName, parentKey, JsonFormConstants.OPTIONS_FIELD_NAME, childKey,
                        String.valueOf(((CheckBox) childAt).isChecked()), openMrsEntityParent,
                        openMrsEntity, openMrsEntityId, popup);
            } else if (childAt instanceof RadioButton) {
                String parentKey = (String) childAt.getTag(R.id.key);
                String childKey = (String) childAt.getTag(R.id.childKey);
                if (((RadioButton) childAt).isChecked()) {
                    getView().writeValue(mStepName, parentKey, childKey, openMrsEntityParent,
                            openMrsEntity, openMrsEntityId, popup);
                }
            } else if (childAt instanceof Button) {
                Button button = (Button) childAt;
                String rawValue = (String) button.getTag(R.id.raw_value);
                getView().writeValue(mStepName, key, rawValue, openMrsEntityParent, openMrsEntity, openMrsEntityId, popup);
            }
        }

        if (firstError == null) {
            return new ValidationStatus(true, null, null, null);
        } else {
            return firstError;
        }
    }

    public void onSaveClick(LinearLayout mainView) {
        ValidationStatus validationStatus = writeValuesAndValidate(mainView);
        if (validationStatus.isValid() || Boolean.valueOf(mainView.getTag(R.id.skip_validation).toString())) {
            Intent returnIntent = new Intent();
            getView().onFormFinish();
            returnIntent.putExtra("json", getView().getCurrentJsonState());
            returnIntent.putExtra(JsonFormConstants.SKIP_VALIDATION, Boolean.valueOf(mainView.getTag(R.id.skip_validation).toString()));
            getView().finishWithResult(returnIntent);
        } else {
            Toast.makeText(getView().getContext(), validationStatus.getErrorMessage(), Toast.LENGTH_LONG).show();
            validationStatus.requestAttention();
        }
    }

    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (requestCode == RESULT_LOAD_IMG && resultCode == Activity.RESULT_OK) {
            String imagePath = mCurrentPhotoPath;
            getView().updateRelevantImageView(ImageUtils.loadBitmapFromFile(getView().getContext(), imagePath, ImageUtils.getDeviceWidth(getView()
                    .getContext()), dpToPixels(getView().getContext(), 200)), imagePath, mCurrentKey);
            //cursor.close();
        }
    }

    public void onRequestPermissionsResult(int requestCode,
                                           String permissions[], int[] grantResults) {
        Log.d(TAG, "Permission callback called-------");

        if (grantResults.length == 0) {
            return;
        }

        switch (requestCode) {
            case PermissionUtils.CAMERA_PERMISSION_REQUEST_CODE:
                if (PermissionUtils.verifyPermissionGranted(permissions, grantResults, Manifest.permission.CAMERA, Manifest.permission
                        .READ_EXTERNAL_STORAGE, Manifest.permission.WRITE_EXTERNAL_STORAGE)) {
                    dispatchTakePictureIntent(key, type);
                }
                break;

            case PermissionUtils.PHONE_STATE_PERMISSION_REQUEST_CODE:
                if (PermissionUtils.verifyPermissionGranted(permissions, grantResults, Manifest.permission.READ_PHONE_STATE)) {
                    //TODO Find out functionality which uses Read Phone State permission
                }
                break;
            default:
                break;

        }
    }

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
            case JsonFormConstants.NUMBERS_SELECTOR:
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
            case JsonFormConstants.NORMAL_EDIT_TEXT:
                setViewEditable(v);
                break;
            default:
                break;
        }
    }


    protected void nativeRadioButtonClickActions(View view) {
        String type = (String) view.getTag(R.id.specify_type);
        String specifyWidget = (String) view.getTag(R.id.specify_widget);
        Log.i(TAG, "The dialog content widget is this: " + specifyWidget);
        if (JsonFormConstants.CONTENT_INFO.equals(type) &&
                specifyWidget.equals(JsonFormConstants.DATE_PICKER)) {
            NativeRadioButtonFactory.showDateDialog(view);
        } else if (JsonFormConstants.CONTENT_INFO.equals(type) &&
                !specifyWidget.equals(JsonFormConstants.DATE_PICKER)) {
            FormUtils formUtils = new FormUtils();
            formUtils.showGenericDialog(view);
        } else if (view.getId() == R.id.label_edit_button) {
            setRadioViewsEditable(view);
        } else {
            showInformationDialog(view);
        }
    }

    private void setViewEditable(View editButton) {
        View editableView = (View) editButton.getTag(R.id.editable_view);
        editableView.setEnabled(true);
        editableView.setFocusable(true);
        editableView.requestFocus();
        editableView.requestFocusFromTouch();
    }

    @SuppressWarnings({"unchecked"})
    private void setCheckboxesEditable(View editButton) {
        List<View> checkboxLayouts = (ArrayList<View>) editButton.getTag(R.id.editable_view);
        for (View checkboxLayout : checkboxLayouts) {
            setViewGroupEditable(checkboxLayout);
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
        AlertDialog.Builder builderSingle = new AlertDialog.Builder(getView().getContext(), R.style.AppThemeAlertDialog);
        builderSingle.setTitle((String) view.getTag(R.id.label_dialog_title));
        builderSingle.setMessage((String) view.getTag(R.id.label_dialog_info));
        builderSingle.setIcon(R.drawable.ic_icon_info_filled);

        builderSingle.setNegativeButton(getView().getContext().getResources().getString(R.string.ok), new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                dialog.dismiss();
            }
        });

        builderSingle.show();
    }

    private void dispatchTakePictureIntent(String key, String type) {
        if (PermissionUtils.isPermissionGranted(formFragment, new String[]{Manifest.permission.CAMERA, Manifest.permission.READ_EXTERNAL_STORAGE,
                Manifest.permission.WRITE_EXTERNAL_STORAGE}, PermissionUtils.CAMERA_PERMISSION_REQUEST_CODE)) {

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
                                getView().getContext().getPackageName() + ".fileprovider",
                                imageFile);

                        // Grant permission to the default camera app
                        PackageManager packageManager = getView().getContext().getPackageManager();
                        Context applicationContext = getView().getContext().getApplicationContext();

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
        File image = File.createTempFile(
                imageFileName,
                ".jpg",
                storageDir
        );

        // Save a file: path for use with ACTION_VIEW intents
        mCurrentPhotoPath = image.getAbsolutePath();
        return image;
    }

    public void onCheckedChanged(CompoundButton compoundButton, boolean isChecked) {

        Log.d(TAG, "onCheckedChanged called");

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
                            getView().unCheckAllExcept(parentKey, childKey, compoundButton);
                        } else {
                            for (String excludeKey : exclusiveSet) {
                                getView().unCheck(parentKey, excludeKey, compoundButton);
                            }
                        }
                    }

                } catch (Exception e) {
                    Log.d(TAG, e.getMessage());
                }
            }


            getView().writeValue(mStepName, parentKey, JsonFormConstants.OPTIONS_FIELD_NAME, childKey, String.valueOf(compoundButton.isChecked()), openMrsEntityParent, openMrsEntity, openMrsEntityId, popup);
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

            getView().unCheckAllExcept(parentKey, childKey, compoundButton);

            getView().writeValue(mStepName, parentKey, childKey, openMrsEntityParent,
                    openMrsEntity, openMrsEntityId, popup);
        }
    }

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
            getView().writeValue(mStepName, parentKey, value, openMrsEntityParent, openMrsEntity,
                    openMrsEntityId, popup);
        }

        if (JsonFormConstants.NUMBERS_SELECTOR.equals(type)) {
            NumberSelectorFactory.setBackgrounds(customTextView);
            NumberSelectorFactory.setSelectedTextViews(customTextView);
            NumberSelectorFactory.setSelectedTextViewText((String) parent.getItemAtPosition(position));
        }
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
        getView().writeValue(mStepName, parentKey, value, openMrsEntityParent, openMrsEntity, openMrsEntityId, popup);
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
            Log.d(TAG, e.getMessage());
        }
        return null;
    }
}
