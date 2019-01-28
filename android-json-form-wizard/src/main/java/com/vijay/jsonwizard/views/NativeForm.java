package com.vijay.jsonwizard.views;

import android.Manifest;
import android.app.Activity;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Color;
import android.net.Uri;
import android.os.Build;
import android.os.Environment;
import android.provider.MediaStore;
import android.support.annotation.RequiresApi;
import android.support.v4.content.FileProvider;
import android.support.v4.content.LocalBroadcastManager;
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
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RadioGroup;
import android.widget.RelativeLayout;
import android.widget.ScrollView;
import android.widget.TextView;
import android.widget.Toast;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.rengwuxian.materialedittext.MaterialEditText;
import com.rey.material.widget.Button;
import com.shashank.sony.fancydialoglib.Animation;
import com.shashank.sony.fancydialoglib.FancyAlertDialog;
import com.shashank.sony.fancydialoglib.Icon;
import com.vijay.jsonwizard.R;
import com.vijay.jsonwizard.comparisons.Comparison;
import com.vijay.jsonwizard.comparisons.EqualToComparison;
import com.vijay.jsonwizard.comparisons.GreaterThanComparison;
import com.vijay.jsonwizard.comparisons.GreaterThanEqualToComparison;
import com.vijay.jsonwizard.comparisons.LessThanComparison;
import com.vijay.jsonwizard.comparisons.LessThanEqualToComparison;
import com.vijay.jsonwizard.comparisons.NotEqualToComparison;
import com.vijay.jsonwizard.comparisons.RegexComparison;
import com.vijay.jsonwizard.constants.JsonFormConstants;
import com.vijay.jsonwizard.customviews.CheckBox;
import com.vijay.jsonwizard.customviews.GenericPopupDialog;
import com.vijay.jsonwizard.customviews.MaterialSpinner;
import com.vijay.jsonwizard.customviews.NativeEditText;
import com.vijay.jsonwizard.customviews.RadioButton;
import com.vijay.jsonwizard.customviews.TextableView;
import com.vijay.jsonwizard.domain.Form;
import com.vijay.jsonwizard.interactors.NativeViewInteractor;
import com.vijay.jsonwizard.interfaces.CommonListener;
import com.vijay.jsonwizard.interfaces.GenericDialogInterface;
import com.vijay.jsonwizard.interfaces.JsonApi;
import com.vijay.jsonwizard.interfaces.LifeCycleListener;
import com.vijay.jsonwizard.interfaces.NativeViewer;
import com.vijay.jsonwizard.interfaces.OnActivityRequestPermissionResultListener;
import com.vijay.jsonwizard.interfaces.OnActivityResultListener;
import com.vijay.jsonwizard.rules.RuleConstant;
import com.vijay.jsonwizard.rules.RulesEngineFactory;
import com.vijay.jsonwizard.utils.ExObjectResult;
import com.vijay.jsonwizard.utils.FormUtils;
import com.vijay.jsonwizard.utils.NativeViewUtils;
import com.vijay.jsonwizard.utils.PermissionUtils;
import com.vijay.jsonwizard.utils.PropertyManager;
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
import org.yaml.snakeyaml.Yaml;

import java.io.File;
import java.io.IOException;
import java.io.InputStreamReader;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.Date;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import static android.view.inputmethod.InputMethodManager.HIDE_NOT_ALWAYS;

/**
 * Create and renders the native form view.
 * <p>
 * The view returns a {@link org.json.JSONObject} result via {@link #getJsonFrom()}
 * Initialize rendering via {@link #setJsonFrom(String)} or {@link #setJsonObject(JSONObject)}. This will load the form
 */
public class NativeForm extends RelativeLayout implements NativeViewer, CommonListener, JsonApi {

    private String TAG = NativeForm.class.getCanonicalName();
    private static String CONST_REAL_TIME_VALIDATION = "RealtimeValidation";
    private static String CONST_FRAGMENT_WRITEVALUE_CALLED = "Fragment write value called";

    private Context context;
    private String jsonFrom;
    private String mStepName = "step1";
    private String mPrevStep;
    private JSONObject mJSONObject;
    private JSONObject mStepDetails;
    private View rootView;
    private NativeViewInteractor nativeViewInteractor = NativeViewInteractor.getInstance();

    // presenter
    protected String key;
    protected String type;
    private String mCurrentKey;
    private String mCurrentPhotoPath;

    private static final int RESULT_LOAD_IMG = 1;

    // activity
    private static final String JSON_STATE = "jsonState";
    private static final String FORM_STATE = "formState";
    private final Set<Character> JAVA_OPERATORS = new HashSet<>(Arrays.asList('(', '!', ',', '?', '+', '-', '*', '/', '%', '+', '-', '.', '^', ')', '<', '>', '=', '{', '}', ':', ';'));
    private final List<String> PREFICES_OF_INTEREST = Arrays.asList(RuleConstant.PREFIX.GLOBAL, RuleConstant.STEP);
    private FormUtils formUtils = new FormUtils();
    private PropertyManager propertyManager;
    private HashMap<String, View> skipLogicViews;
    private HashMap<String, View> calculationLogicViews;
    private HashMap<String, View> constrainedViews;
    private ArrayList<View> formDataViews;
    private String functionRegex;
    private HashMap<String, Comparison> comparisons;
    private HashMap<Integer, OnActivityResultListener> onActivityResultListeners;
    private HashMap<Integer, OnActivityRequestPermissionResultListener> onActivityRequestPermissionResultListeners;
    private List<LifeCycleListener> lifeCycleListeners;
    private String confirmCloseTitle;
    private String confirmCloseMessage;
    private Map<String, List<String>> ruleKeys = new HashMap<>();
    private GenericDialogInterface genericDialogInterface;
    private JSONArray extraFieldsWithValues;
    private Form form;
    private Map<String, String> globalValues = null;
    private RulesEngineFactory rulesEngineFactory = null;
    private LocalBroadcastManager localBroadcastManager;


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

        rootView.findViewById(R.id.tvNext).setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                next();
            }
        });

        rootView.findViewById(R.id.tvPrev).setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                backClick();
            }
        });


        skipLogicViews = new HashMap<>();
        calculationLogicViews = new HashMap<>();
        onActivityResultListeners = new HashMap<>();
        onActivityRequestPermissionResultListeners = new HashMap<>();
        lifeCycleListeners = new ArrayList<>();

        clearFormDataViews();
    }

    public void init(String json) {
        try {
            mJSONObject = new JSONObject(json);
            if (!mJSONObject.has("encounter_type")) {
                mJSONObject = new JSONObject();
                throw new JSONException("Form encounter_type not set");
            }

            //populate them global values
            if (mJSONObject.has(JsonFormConstants.JSON_FORM_KEY.GLOBAL)) {
                globalValues = new Gson().fromJson(mJSONObject.getJSONObject(JsonFormConstants.JSON_FORM_KEY.GLOBAL).toString(), new TypeToken<HashMap<String, String>>() {
                }.getType());
            } else {
                globalValues = new HashMap<>();
            }

            rulesEngineFactory = new RulesEngineFactory(context, globalValues);

            confirmCloseTitle = context.getString(R.string.confirm_form_close);
            confirmCloseMessage = context.getString(R.string.confirm_form_close_explanation);
            localBroadcastManager = LocalBroadcastManager.getInstance(context);

        } catch (JSONException e) {
            Log.d(TAG, "Initialization error. Json passed is invalid : " + e.getMessage(), e);
        }
    }


    public void clearFormDataViews() {
        formDataViews = new ArrayList<>();
        clearSkipLogicViews();
        clearConstrainedViews();
        clearCalculationLogicViews();
    }

    @Override
    public void addSkipLogicView(View view) {
        skipLogicViews.put(getViewKey(view), view);
    }

    @Override
    public void addCalculationLogicView(View view) {
        calculationLogicViews.put(getViewKey(view), view);
    }

    @Override
    public void addConstrainedView(View view) {
        constrainedViews.put(getViewKey(view), view);
    }

    @Override
    public void refreshHiddenViews(boolean popup) {
        for (View curView : formDataViews) {
            String addressString = (String) curView.getTag(R.id.address);
            String[] address = addressString.split(":");
            try {
                JSONObject viewData = getObjectUsingAddress(address, popup);
                if (viewData.has("hidden") && viewData.getBoolean("hidden")) {
                    toggleViewVisibility(curView, false, popup);
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }

    public void clearSkipLogicViews() {
        skipLogicViews.clear();
    }

    public void clearCalculationLogicViews() {
        calculationLogicViews.clear();
    }

    public void clearConstrainedViews() {
        constrainedViews = new HashMap<>();
    }

    public String getJsonFrom() {
        return jsonFrom;
    }

    public void setJsonFrom(String jsonFrom) {
        this.jsonFrom = jsonFrom;
        mJSONObject = NativeViewUtils.getFormJson(context, jsonFrom);
        loadForm();
    }

    public JSONObject getJsonObject() {
        return mJSONObject;
    }

    public void setJsonObject(JSONObject jsonObject) {
        this.mJSONObject = jsonObject;
        loadForm();
    }

    private void loadForm() {
        if (mJSONObject == null || rootView == null) {
            throw new RuntimeException("Error processing file");
        } else {
            init(mJSONObject.toString());
        }


        try {
            //populate them global values
            if (mJSONObject.has(JsonFormConstants.JSON_FORM_KEY.GLOBAL)) {
                globalValues = new Gson().fromJson(mJSONObject.getJSONObject(JsonFormConstants.JSON_FORM_KEY.GLOBAL).toString(), new TypeToken<HashMap<String, String>>() {
                }.getType());
            } else {
                globalValues = new HashMap<>();
            }
            rulesEngineFactory = new RulesEngineFactory(getContext(), globalValues);
        } catch (Exception e) {

        }

        // add views
        renderViews();
    }

    // render/re-render view
    private void renderViews() {

        JSONObject step = getStep(mStepName);
        try {
            mStepDetails = new JSONObject(step.toString());
        } catch (JSONException e) {
            Log.e(TAG, e.getMessage(), e);
        }

        List<View> views = nativeViewInteractor.fetchFormElements(mStepName, this, mStepDetails, this, false);

        LinearLayout linearLayout = rootView.findViewById(R.id.main_layout);
        linearLayout.removeAllViews();
        for (View view : views) {
            linearLayout.addView(view);
        }
    }

    public synchronized JSONObject getStep(String name) {
        synchronized (mJSONObject) {
            try {
                return mJSONObject.getJSONObject(name);
            } catch (JSONException e) {
                e.printStackTrace();
            }
        }
        return null;
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
                        try{
                            ((Activity) context).startActivityForResult(takePictureIntent, RESULT_LOAD_IMG);
                        }catch (Exception e){
                            Log.e(TAG,e.toString());
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
            writeValue(mStepName, parentKey, value, openMrsEntityParent, openMrsEntity, openMrsEntityId, popup);
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
                writeValue(mStepName, parentKey, value, openMrsEntityParent, openMrsEntity,
                        openMrsEntityId, popup);
            } catch (JSONException e) {
                e.printStackTrace();
            }
        }

        if (JsonFormConstants.NUMBERS_SELECTOR.equals(type)) {
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
                writeValue(mStepName, parentKey, JsonFormConstants.OPTIONS_FIELD_NAME, childKey, String.valueOf(compoundButton.isChecked()), openMrsEntityParent, openMrsEntity, openMrsEntityId, popup);
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
                writeValue(mStepName, parentKey, childKey, openMrsEntityParent,
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
            JSONArray array = getStep(mStepName).getJSONArray(JsonFormConstants.FIELDS);

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

    public void writeValue(String stepName, String key, String value, String openMrsEntityParent,
                           String openMrsEntity, String openMrsEntityId, boolean popup) throws JSONException {
        widgetsWriteValue(stepName, key, value, openMrsEntityParent, openMrsEntity, openMrsEntityId, popup);
    }

    public void writeValue(String stepName, String parentKey, String childObjectKey, String childKey,
                           String value, String openMrsEntityParent, String openMrsEntity,
                           String openMrsEntityId, boolean popup)
            throws JSONException {
        checkBoxWriteValue(stepName, parentKey, childObjectKey, childKey, value, popup);
    }

    public void writeValue(String stepName, String key, String value, String openMrsEntityParent, String openMrsEntity, String openMrsEntityId) throws JSONException {
        widgetsWriteValue(stepName, key, value, openMrsEntityParent, openMrsEntity, openMrsEntityId, false);
    }

    public void writeValue(String stepName, String parentKey, String childObjectKey, String childKey, String value, String openMrsEntityParent, String openMrsEntity, String openMrsEntityId) throws JSONException {
        checkBoxWriteValue(stepName, parentKey, childObjectKey, childKey, value, false);
    }

    @Override
    public void writeMetaDataValue(String metaDataKey, Map<String, String> values) throws JSONException {
        synchronized (mJSONObject) {
            if (mJSONObject.has(FormUtils.METADATA_PROPERTY) && !values.isEmpty() && (mJSONObject.getJSONObject(FormUtils.METADATA_PROPERTY).has(metaDataKey))) {
                JSONObject metaData = mJSONObject.getJSONObject(FormUtils.METADATA_PROPERTY).getJSONObject(metaDataKey);
                for (Map.Entry<String, String> entry : values.entrySet()) {
                    String key = entry.getKey();
                    String value = entry.getValue();
                    if (value == null) value = "";
                    metaData.put(key, value);
                }
            }

        }
    }

    @Override
    public String currentJsonState() {
        synchronized (mJSONObject) {
            return mJSONObject.toString();
        }
    }

    @Override
    public String getCount() {
        synchronized (mJSONObject) {
            return mJSONObject.optString("count");
        }
    }

    @Override
    public void onFormStart() {
        try {
            if (propertyManager == null) {
                propertyManager = new PropertyManager(context);
            }
            FormUtils.updateStartProperties(propertyManager, mJSONObject);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @Override
    public void onFormFinish() {
        try {
            if (propertyManager == null) {
                propertyManager = new PropertyManager(context);
            }
            FormUtils.updateEndProperties(propertyManager, mJSONObject);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    protected void checkBoxWriteValue(String stepName, String parentKey, String childObjectKey, String childKey, String value, boolean popup) throws JSONException {
        synchronized (mJSONObject) {
            JSONObject jsonObject = mJSONObject.getJSONObject(stepName);
            JSONArray fields = fetchFields(jsonObject, popup);
            for (int i = 0; i < fields.length(); i++) {
                JSONObject item = fields.getJSONObject(i);
                String keyAtIndex = item.getString(JsonFormConstants.KEY);
                String itemType = "";
                if (popup) {
                    itemType = item.getString(JsonFormConstants.TYPE);
                }
                if (parentKey.equals(keyAtIndex)) {
                    JSONArray jsonArray = item.getJSONArray(childObjectKey);
                    for (int j = 0; j < jsonArray.length(); j++) {
                        JSONObject innerItem = jsonArray.getJSONObject(j);
                        String anotherKeyAtIndex = innerItem.getString(JsonFormConstants.KEY);
                        String itemText = "";
                        if (itemType.equals(JsonFormConstants.CHECK_BOX)) {
                            itemText = innerItem.getString(JsonFormConstants.TEXT);
                        }
                        if (childKey.equals(anotherKeyAtIndex)) {
                            innerItem.put(JsonFormConstants.VALUE, value);
                            if (popup) {
                                genericDialogInterface.addSelectedValues(formUtils.addAssignedValue(keyAtIndex, childKey, value, itemType, itemText));
                                extraFieldsWithValues = fields;
                            }
                            refreshCalculationLogic(parentKey, childKey, popup);
                            refreshSkipLogic(parentKey, childKey, popup);
                            refreshConstraints(parentKey, childKey);
                            return;
                        }
                    }
                }
            }
        }
    }

    public void refreshConstraints(String parentKey, String childKey) {
        initComparisons();

        // Priorities constraints on the view that has just been changed
        String changedViewKey = parentKey;
        if (changedViewKey != null && childKey != null) {
            changedViewKey = changedViewKey + ":" + childKey;
        }

        if (changedViewKey != null && constrainedViews.containsKey(changedViewKey)) {
            checkViewConstraints(constrainedViews.get(changedViewKey), false);
        }

        for (View curView : constrainedViews.values()) {
            if (changedViewKey == null || !getViewKey(curView).equals(changedViewKey)) {
                checkViewConstraints(curView, false);
            }
        }
    }

    @Override
    public void addOnActivityResultListener(Integer requestCode, OnActivityResultListener onActivityResultListener) {
        onActivityResultListeners.put(requestCode, onActivityResultListener);
    }

    @Override
    public void addOnActivityRequestPermissionResultListener(Integer requestCode, OnActivityRequestPermissionResultListener onActivityRequestPermissionResultListener) {
        onActivityRequestPermissionResultListeners.put(requestCode, onActivityRequestPermissionResultListener);
    }

    @Override
    public void removeOnActivityRequestPermissionResultListener(Integer requestCode) {
        onActivityRequestPermissionResultListeners.remove(requestCode);
    }

    @Override
    public void resetFocus() {
        EditText defaultFocusView = findViewById(R.id.default_focus_view);
        defaultFocusView.requestFocus();
        InputMethodManager inputManager = (InputMethodManager) context.getSystemService(context.INPUT_METHOD_SERVICE);
        if (inputManager != null && ((Activity) context).getCurrentFocus() != null) {
            inputManager.hideSoftInputFromWindow(((Activity) context).getCurrentFocus().getWindowToken(), HIDE_NOT_ALWAYS);
        }
    }

    @Override
    public JSONObject getmJSONObject() {
        return mJSONObject;
    }

    @Override
    public void setmJSONObject(JSONObject jsonObject) {
        this.mJSONObject = mJSONObject;
    }

    @Override
    public void updateGenericPopupSecondaryValues(JSONArray jsonArray) {
        setExtraFieldsWithValues(jsonArray);
    }

    public void setExtraFieldsWithValues(JSONArray extraFieldsWithValues) {
        this.extraFieldsWithValues = extraFieldsWithValues;
    }

    @Override
    public void registerLifecycleListener(LifeCycleListener lifeCycleListener) {
        lifeCycleListeners.add(lifeCycleListener);
    }

    @Override
    public void unregisterLifecycleListener(LifeCycleListener lifeCycleListener) {
        lifeCycleListeners.remove(lifeCycleListener);
    }

    @Override
    public void setGenericPopup(GenericPopupDialog context) {
        genericDialogInterface = context;
    }

    private String getViewKey(View view) {
        String key = (String) view.getTag(R.id.key);
        if (view.getTag(R.id.childKey) != null) {
            key = key + ":" + view.getTag(R.id.childKey);
        }

        return key;
    }

    private boolean isNumberSelectorConstraint(View view) {
        return view instanceof LinearLayout && view.getTag(R.id.key).toString().startsWith(JsonFormConstants.NUMBERS_SELECTOR);
    }

    private String enforceConstraint(String value, View view, JSONObject constraint) throws
            Exception {

        String type = constraint.getString("type").toLowerCase();
        String ex = constraint.getString("ex");
        String errorMessage = type.equals(JsonFormConstants.NUMBERS_SELECTOR) ? constraint.optString(JsonFormConstants.ERR) : constraint.getString(JsonFormConstants.ERR);
        Pattern pattern = Pattern.compile("(" + functionRegex + ")\\((.*)\\)");
        Matcher matcher = pattern.matcher(ex);
        if (matcher.find()) {
            String functionName = matcher.group(1);
            String b = matcher.group(2);
            String[] args = getFunctionArgs(b, value);

            boolean viewDoesntHaveValue = TextUtils.isEmpty(value);
            if (view instanceof CheckBox) {
                viewDoesntHaveValue = !((CheckBox) view).isChecked();
            } else if (isNumberSelectorConstraint(view)) {
                return args.length > 1 ? args[1] : "";//clever fix to pass back the max value for number selectors

            }

            if (viewDoesntHaveValue
                    || TextUtils.isEmpty(args[0])
                    || TextUtils.isEmpty(args[1])
                    || comparisons.get(functionName).compare(args[0], type, args[1])) {
                return null;
            }
        } else {
            Log.d(TAG, "Matcher didn't work with function");
        }

        return errorMessage;
    }

    private String enforceConstraint(Map<String, String> curValueMap, View view, JSONObject constraint) throws
            Exception {
        String errorMessage = "";
        if (isNumberSelectorConstraint(view)) {

            errorMessage = curValueMap.size() == 0 ? "" : rulesEngineFactory.getConstraint(curValueMap, constraint.getJSONObject(JsonFormConstants.JSON_FORM_KEY.EX_RULES).getString(RuleConstant.RULES_FILE));
        } else if (isDatePickerNativeRadio(view)) {

            errorMessage = curValueMap.size() == 0 ? "" : rulesEngineFactory.getConstraint(curValueMap, constraint.getJSONObject(JsonFormConstants.JSON_FORM_KEY.EX_RULES).getString(RuleConstant.RULES_FILE));
        }
        return errorMessage;
    }

    private boolean isDatePickerNativeRadio(View view) {
        return view.getTag(R.id.type).toString().equals(JsonFormConstants.NATIVE_RADIO_BUTTON);
    }

    private void checkViewConstraints(View curView, boolean popup) {
        String constraintTag = (String) curView.getTag(R.id.constraints);
        if (constraintTag != null && constraintTag.length() > 0) {
            try {

                String errorMessage = null;
                String[] address = null;

                if (constraintTag.charAt(0) == '[') {
                    String addressString = (String) curView.getTag(R.id.address);
                    address = addressString.split(":");

                    JSONArray constraint = new JSONArray(constraintTag);
                    for (int i = 0; i < constraint.length(); i++) {
                        JSONObject curConstraint = constraint.getJSONObject(i);
                        if (address.length == 2) {
                            String value = getValueFromAddress(address, popup).get(JsonFormConstants.VALUE);
                            errorMessage = enforceConstraint(value, curView, curConstraint);
                            if (errorMessage != null) break;
                        }
                    }

                } else {
//Rules Engine
                    JSONObject constraint = new JSONObject(constraintTag);
                    Iterator<String> keys = constraint.keys();
                    while (keys.hasNext()) {
                        String curKey = keys.next();

                        JSONObject curConstraint = constraint.getJSONObject(curKey);

                        address = getAddress(curView, curKey, curConstraint);

                        Map<String, String> curValueMap = getValueFromAddress(address, popup);

                        errorMessage = enforceConstraint(curValueMap, curView, curConstraint);
                        if (errorMessage != null) break;
                    }
                }

                if (errorMessage != null) {
                    if (curView instanceof MaterialEditText) {
                        ((MaterialEditText) curView).setText(null);
                        ((MaterialEditText) curView).setError(errorMessage);
                    } else if (curView instanceof CheckBox) {
                        ((CheckBox) curView).setChecked(false);
                        Toast.makeText(context, errorMessage, Toast.LENGTH_SHORT).show();
                        String checkBoxKey = (String) curView.getTag(R.id.childKey);

                        JSONObject questionObject = getObjectUsingAddress(address, popup);
                        for (int i = 0; i < questionObject.getJSONArray(JsonFormConstants.OPTIONS_FIELD_NAME).length(); i++) {
                            JSONObject curOption = questionObject.getJSONArray(JsonFormConstants.OPTIONS_FIELD_NAME).getJSONObject(i);
                            if (curOption.getString(JsonFormConstants.KEY).equals(checkBoxKey)) {
                                curOption.put(JsonFormConstants.VALUE, "false");
                                break;
                            }
                        }
                    } else if (curView instanceof LinearLayout && curView.getTag(R.id.key).toString().startsWith(JsonFormConstants.NUMBERS_SELECTOR) && !TextUtils.isEmpty(errorMessage) && (curView.getTag(R.id.previous) == null || !curView.getTag(R.id.previous).equals(errorMessage))) {

                        if (!"false".equals(errorMessage)) {
                            Intent localIntent = new Intent(JsonFormConstants.INTENT_ACTION.NUMBER_SELECTOR_FACTORY);
                            localIntent.putExtra(JsonFormConstants.MAX_SELECTION_VALUE, Integer.valueOf(errorMessage));
                            localIntent.putExtra(JsonFormConstants.JSON_OBJECT_KEY, curView.getTag(R.id.key).toString());
                            localIntent.putExtra(JsonFormConstants.STEPNAME, address[0]);
                            localIntent.putExtra(JsonFormConstants.IS_POPUP, popup);
                            localBroadcastManager.sendBroadcast(localIntent);
                            curView.setTag(R.id.previous, errorMessage); //Store value to avoid re-fires
                        }

                    } else if (curView instanceof RadioGroup && curView.getTag(R.id.type).toString().equals(JsonFormConstants.NATIVE_RADIO_BUTTON) && !TextUtils.isEmpty(errorMessage) && (curView.getTag(R.id.previous) == null || !curView.getTag(R.id.previous).equals(errorMessage))) {

                        JSONObject jsonObject = (JSONObject) curView.getTag(R.id.json_object);
                        JSONObject jsonObjectNew = new JSONObject(errorMessage);
                        Iterator<String> keys = jsonObjectNew.keys();

                        while (keys.hasNext()) {
                            String key = keys.next();
                            jsonObject.put(key, jsonObjectNew.getString(key));
                        }

                    }
                }
            } catch (Exception e) {
                Log.e(TAG, e.getMessage(), e);
            }
        }
    }

    public void refreshSkipLogic(String parentKey, String childKey, boolean popup) {
        initComparisons();
        for (View curView : skipLogicViews.values()) {
            addRelevance(curView, popup);
        }
    }

    private void addRelevance(View view, Boolean popup) {
        String relevanceTag = (String) view.getTag(R.id.relevance);
        if (relevanceTag != null && relevanceTag.length() > 0) {
            try {
                JSONObject relevance = new JSONObject(relevanceTag);
                Iterator<String> keys = relevance.keys();
                boolean ok = true;
                while (keys.hasNext()) {
                    String curKey = keys.next();
                    JSONObject curRelevance = relevance.has(curKey) ? relevance.getJSONObject(curKey) : null;


                    String[] address = getAddress(view, curKey, curRelevance);
                    if (address.length > 1) {
                        Map<String, String> curValueMap = getValueFromAddress(address, popup);
                        try {
                            boolean comparison = isRelevant(curValueMap, curRelevance);

                            ok = ok && comparison;
                            if (!ok) break;
                        } catch (Exception e) {
                            Log.e(TAG, e.getMessage(), e);
                        }

                    }
                }
                toggleViewVisibility(view, ok, popup);
            } catch (Exception e) {
                Log.e(TAG, e.getMessage(), e);
            }
        }
    }

    protected void toggleViewVisibility(View view, boolean visible, boolean popup) {
        try {
            JSONArray canvasViewIds = new JSONArray((String) view.getTag(R.id.canvas_ids));
            String addressString = (String) view.getTag(R.id.address);
            String[] address = addressString.split(":");
            JSONObject object = getObjectUsingAddress(address, popup);
            boolean enabled = visible;
            if (object != null && object.has(JsonFormConstants.READ_ONLY) && object.getBoolean(JsonFormConstants.READ_ONLY) && visible) {
                enabled = false;
            }

            view.setEnabled(enabled);
            if (view instanceof MaterialEditText || view instanceof RelativeLayout || view instanceof LinearLayout) {
                view.setFocusable(enabled);
                if (view instanceof MaterialEditText) {
                    view.setFocusableInTouchMode(enabled);
                }
            }

            updateCanvas(view, visible, canvasViewIds);
            setReadOnlyAndFocus(view, visible, popup);
        } catch (Exception e) {
            Log.e(TAG, view.toString());
            Log.e(TAG, Log.getStackTraceString(e));
        }
    }

    private void updateCanvas(View view, boolean visible, JSONArray canvasViewIds) throws JSONException {
        for (int i = 0; i < canvasViewIds.length(); i++) {

            int curId = canvasViewIds.getInt(i);

            View curCanvasView = view.getRootView().findViewById(curId);

            if (curCanvasView == null) {

                continue;
            }

            if (visible) {
                curCanvasView.setEnabled(true);
                curCanvasView.setVisibility(View.VISIBLE);

                if (curCanvasView instanceof RelativeLayout || view instanceof LinearLayout) {
                    curCanvasView.setFocusable(true);
                }
                if (view instanceof EditText) {
                    view.setFocusable(true);
                }
            } else {
                curCanvasView.setEnabled(false);
                curCanvasView.setVisibility(View.GONE);

                if (view instanceof EditText) {
                    EditText editText = (EditText) view;
                    if (!TextUtils.isEmpty(editText.getText().toString())) {
                        editText.setText("");
                    }

                }
            }
        }
    }

    private void setReadOnlyAndFocus(View view, boolean visible, boolean popup) {
        try {
            String addressString = (String) view.getTag(R.id.address);
            String[] address = addressString.split(":");
            JSONObject object = getObjectUsingAddress(address, popup);

            boolean enabled = visible;
            if (object != null && object.has(JsonFormConstants.READ_ONLY) && object.getBoolean(JsonFormConstants.READ_ONLY) && visible) {
                enabled = false;
            }

            view.setEnabled(enabled);
            if (view instanceof MaterialEditText || view instanceof RelativeLayout || view instanceof LinearLayout) {
                view.setFocusable(enabled);
                if (view instanceof MaterialEditText) {
                    view.setFocusableInTouchMode(enabled);
                }
            }
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }

    private String[] getAddress(View view, String curKey, JSONObject curRelevance) throws JSONException {
        return curKey.contains(":") ? curKey.split(":") : new String[]{curKey, curRelevance.getJSONObject(JsonFormConstants.JSON_FORM_KEY.EX_RULES).getString(RuleConstant.RULES_FILE), view.getTag(R.id.address).toString().replace(':', '_')};
    }

    private boolean isRelevant(Map<String, String> curValueMap, JSONObject curRelevance) throws
            Exception {

        if (curRelevance.has(JsonFormConstants.JSON_FORM_KEY.EX_RULES)) {
            return curValueMap.size() == 0 ? false : rulesEngineFactory.getRelevance(curValueMap, curRelevance.getJSONObject(JsonFormConstants.JSON_FORM_KEY.EX_RULES).getString(RuleConstant.RULES_FILE));
        } else if (curRelevance.has(JsonFormConstants.JSON_FORM_KEY.EX_CHECKBOX)) {
            JSONArray exArray = curRelevance.getJSONArray(JsonFormConstants.JSON_FORM_KEY.EX_CHECKBOX);

            for (int i = 0; i < exArray.length(); i++) {
                ExObjectResult exObjectResult = isExObjectRelevant(curValueMap, exArray.getJSONObject(i));
                if (exObjectResult.isRelevant()) {
                    return true;
                } else if (!exObjectResult.isRelevant() && exObjectResult.isFinal()) {
                    return false;
                }

            }
            return false;
        } else {
            return doComparison(curValueMap.get(JsonFormConstants.VALUE), curRelevance);
        }

    }

    private ExObjectResult isExObjectRelevant
            (Map<String, String> curValueMap, JSONObject object) throws Exception {
        if (object.has(JsonFormConstants.JSON_FORM_KEY.NOT)) {
            JSONArray orArray = object.getJSONArray(JsonFormConstants.JSON_FORM_KEY.NOT);

            for (int i = 0; i < orArray.length(); i++) {
                if (!Boolean.valueOf(curValueMap.get(orArray.getString(i)))) {
                    return new ExObjectResult(true, false);
                } else {
                    return new ExObjectResult(false, true);
                }
            }
        }

        if (object.has(JsonFormConstants.JSON_FORM_KEY.OR)) {
            JSONArray orArray = object.getJSONArray(JsonFormConstants.JSON_FORM_KEY.OR);

            for (int i = 0; i < orArray.length(); i++) {
                if (Boolean.valueOf(curValueMap.get(orArray.getString(i)))) {
                    return new ExObjectResult(true, true);
                }

            }

        }

        if (object.has(JsonFormConstants.JSON_FORM_KEY.AND)) {
            JSONArray andArray = object.getJSONArray(JsonFormConstants.JSON_FORM_KEY.AND);

            for (int i = 0; i < andArray.length(); i++) {
                if (!Boolean.valueOf(curValueMap.get(andArray.getString(i)))) {
                    return new ExObjectResult(false, false);
                }
            }
            return new ExObjectResult(true, false);

        }

        return new ExObjectResult(false, false);
    }

    private boolean doComparison(String value, JSONObject comparison) throws Exception {
        String type = comparison.getString("type").toLowerCase();
        String ex = comparison.getString("ex");

        Pattern pattern = Pattern.compile("(" + functionRegex + ")\\((.*)\\)");
        Matcher matcher = pattern.matcher(ex);
        if (matcher.find()) {
            String functionName = matcher.group(1);
            String b = matcher.group(2);//functions arguments should be two, and should either be addresses or values (enclosed using "")
            String[] args = getFunctionArgs(b, value);
            return comparisons.get(functionName).compare(args[0], type, args[1]);
        }

        return false;
    }

    private String[] getFunctionArgs(String functionArgs, String value) {
        String[] args = new String[2];
        String[] splitArgs = functionArgs.split(",");
        if (splitArgs.length == 2) {
            Pattern valueRegex = Pattern.compile("\"(.*)\"");
            for (int i = 0; i < splitArgs.length; i++) {
                String curArg = splitArgs[i].trim();

                if (".".equals(curArg)) {
                    args[i] = value;
                } else {
                    Matcher valueMatcher = valueRegex.matcher(curArg);
                    if (valueMatcher.find()) {
                        args[i] = valueMatcher.group(1);
                    } else {
                        try {
                            args[i] = getValueFromAddress(curArg.split(":"), false).get(JsonFormConstants.VALUE);
                        } catch (Exception e) {
                            e.printStackTrace();
                        }
                    }
                }
            }
        }

        return args;
    }

    private void initComparisons() {
        if (comparisons == null) {
            functionRegex = "";
            comparisons = new HashMap<>();

            LessThanComparison lessThanComparison = new LessThanComparison();
            functionRegex += lessThanComparison.getFunctionName();
            comparisons.put(lessThanComparison.getFunctionName(), lessThanComparison);

            LessThanEqualToComparison lessThanEqualToComparison = new LessThanEqualToComparison();
            functionRegex += "|" + lessThanEqualToComparison.getFunctionName();
            comparisons.put(lessThanEqualToComparison.getFunctionName(), lessThanEqualToComparison);

            EqualToComparison equalToComparison = new EqualToComparison();
            functionRegex += "|" + equalToComparison.getFunctionName();
            comparisons.put(equalToComparison.getFunctionName(), equalToComparison);

            NotEqualToComparison notEqualToComparer = new NotEqualToComparison();
            functionRegex += "|" + notEqualToComparer.getFunctionName();
            comparisons.put(notEqualToComparer.getFunctionName(), notEqualToComparer);

            GreaterThanComparison greaterThanComparison = new GreaterThanComparison();
            functionRegex += "|" + greaterThanComparison.getFunctionName();
            comparisons.put(greaterThanComparison.getFunctionName(), greaterThanComparison);

            GreaterThanEqualToComparison greaterThanEqualToComparison = new GreaterThanEqualToComparison();
            functionRegex += "|" + greaterThanEqualToComparison.getFunctionName();
            comparisons.put(greaterThanEqualToComparison.getFunctionName(), greaterThanEqualToComparison);

            RegexComparison regexComparison = new RegexComparison();
            functionRegex += "|" + regexComparison.getFunctionName();
            comparisons.put(regexComparison.getFunctionName(), regexComparison);
        }
    }

    public void refreshCalculationLogic(String parentKey, String childKey, boolean popup) {
        Collection<View> views = calculationLogicViews.values();
        for (View curView : views) {
            String calculationTag = (String) curView.getTag(R.id.calculation);
            if (calculationTag != null && calculationTag.length() > 0) {
                try {
                    JSONObject calculation = new JSONObject(calculationTag);
                    Iterator<String> keys = calculation.keys();

                    while (keys.hasNext()) {
                        String curKey = keys.next();

                        JSONObject curRelevance = calculation.getJSONObject(curKey);

                        String[] address = new String[]{curKey, curRelevance.getJSONObject(JsonFormConstants.JSON_FORM_KEY.EX_RULES).getString(RuleConstant.RULES_FILE), curView.getTag(R.id.address).toString().replace(':', '_')};

                        Map<String, String> curValueMap = getValueFromAddress(address, popup);

                        updateCalculation(curValueMap, curView, address[1]);
                    }

                } catch (Exception e) {
                    Log.e(TAG, e.getMessage(), e);

                }
            }
        }
    }

    @Override
    public void addFormDataView(View view) {
        formDataViews.add(view);
    }

    @Override
    public ArrayList<View> getFormDataViews() {
        return formDataViews;
    }

    private Map<String, String> getValueFromAddress(String[] address, boolean popup) throws Exception {
        Map<String, String> result = new HashMap<>();

        JSONObject object = getObjectUsingAddress(address, popup);

        if (object != null) {

            //reset the rules check value
            object.put(RuleConstant.IS_RULE_CHECK, false);

            if (object.has(RuleConstant.RESULT)) {
                JSONArray jsonArray = object.getJSONArray(RuleConstant.RESULT);

                for (int i = 0; i < jsonArray.length(); i++) {
                    JSONObject formObject = jsonArray.getJSONObject(i);

                    formObject.put(RuleConstant.IS_RULE_CHECK, true);
                    formObject.put(RuleConstant.STEP, formObject.getString(RuleConstant.STEP));

                    result.putAll(getValueFromAddressCore(formObject));
                }

                result.put(RuleConstant.SELECTED_RULE, address[2]);

            } else {

                result = getValueFromAddressCore(object);

            }
        }

        return result;
    }

    protected Map<String, String> getValueFromAddressCore(JSONObject object) throws JSONException {
        Map<String, String> result = new HashMap<>();

        if (object != null) {
            switch (object.getString(JsonFormConstants.TYPE)) {
                case JsonFormConstants.CHECK_BOX:
                    JSONArray options = object.getJSONArray(JsonFormConstants.OPTIONS_FIELD_NAME);
                    for (int j = 0; j < options.length(); j++) {
                        if (options.getJSONObject(j).has(JsonFormConstants.VALUE)) {
                            if (object.has(RuleConstant.IS_RULE_CHECK) && object.getBoolean(RuleConstant.IS_RULE_CHECK)) {
                                if (Boolean.valueOf(options.getJSONObject(j).getString(JsonFormConstants.VALUE))) {//Rules engine useth only true values
                                    result.put(options.getJSONObject(j).getString(JsonFormConstants.KEY), options.getJSONObject(j).getString(JsonFormConstants.VALUE));
                                }
                            } else {
                                result.put(options.getJSONObject(j).getString(JsonFormConstants.KEY), options.getJSONObject(j).getString(JsonFormConstants.VALUE));
                            }
                        } else {
                            Log.e(TAG, "option for Key " + options.getJSONObject(j).getString(JsonFormConstants.KEY) + " has NO value");
                        }

                        //Backward compatibility Fix
                        if (object.has(RuleConstant.IS_RULE_CHECK) && !object.getBoolean(RuleConstant.IS_RULE_CHECK)) {
                            if (options.getJSONObject(j).has(JsonFormConstants.VALUE)) {
                                result.put(JsonFormConstants.VALUE, options.getJSONObject(j).getString(JsonFormConstants.VALUE));
                            } else {
                                result.put(JsonFormConstants.VALUE, "false");
                            }
                        }
                    }
                    break;

                case JsonFormConstants.NATIVE_RADIO_BUTTON:
                    Boolean multiRelevance = object.optBoolean(JsonFormConstants.NATIVE_RADIO_BUTTON_MULTI_RELEVANCE, false);
                    if (multiRelevance) {
                        JSONArray jsonArray = object.getJSONArray(JsonFormConstants.OPTIONS_FIELD_NAME);
                        for (int j = 0; j < jsonArray.length(); j++) {
                            if (object.has(JsonFormConstants.VALUE)) {
                                if (object.getString(JsonFormConstants.VALUE).equals(jsonArray.getJSONObject(j).getString(JsonFormConstants.KEY))) {
                                    result.put(jsonArray.getJSONObject(j).getString(JsonFormConstants.KEY), String.valueOf(true));
                                } else {
                                    if (!object.has(RuleConstant.IS_RULE_CHECK) || !object.getBoolean(RuleConstant.IS_RULE_CHECK)) {
                                        result.put(jsonArray.getJSONObject(j).getString(JsonFormConstants.KEY), String.valueOf(false));
                                    }
                                }
                            } else {
                                Log.e(TAG, "option for Key " + jsonArray.getJSONObject(j).getString(JsonFormConstants.KEY) + " has NO value");
                            }
                        }
                    } else {
                        result.put(getKey(object), getValue(object));
                    }
                    break;

                default:
                    result.put(getKey(object), getValue(object));
                    break;
            }

            if (object.has(RuleConstant.IS_RULE_CHECK) && object.getBoolean(RuleConstant.IS_RULE_CHECK) && (object.getString(JsonFormConstants.TYPE).equals(JsonFormConstants.CHECK_BOX) || (object.getString(JsonFormConstants.TYPE).equals(JsonFormConstants.NATIVE_RADIO_BUTTON) && object.optBoolean(JsonFormConstants.NATIVE_RADIO_BUTTON_MULTI_RELEVANCE, false)))) {
                List<String> selectedValues = new ArrayList<>(result.keySet());
                result.clear();
                result.put(getKey(object), selectedValues.toString());
            }
        }
        return result;
    }

    protected String getValue(JSONObject object) throws JSONException {
        String value = object.optString(JsonFormConstants.VALUE);

        if (object.has(JsonFormConstants.EDIT_TYPE) && object.getString(JsonFormConstants.EDIT_TYPE).equals(JsonFormConstants.EDIT_TEXT_TYPE.NUMBER) && TextUtils.isEmpty(object.optString(JsonFormConstants.VALUE))) {
            value = "0";
        }

        return value;
    }

    protected String getKey(JSONObject object) throws JSONException {
        return object.has(RuleConstant.IS_RULE_CHECK) && object.getBoolean(RuleConstant.IS_RULE_CHECK) ? object.get(RuleConstant.STEP) + "_" + object.get(JsonFormConstants.KEY) : JsonFormConstants.VALUE;
    }

    private List<String> getRules(String filename, String fieldKey) {

        List<String> rules = ruleKeys.get(filename + ":" + fieldKey);

        try {

            if (rules == null) {

                Yaml yaml = new Yaml();
                InputStreamReader inputStreamReader = new InputStreamReader(context.getAssets().open((rulesEngineFactory.getRulesFolderPath() + filename)));
                Iterable<Object> ruleObjects = yaml.loadAll(inputStreamReader);

                for (Object object : ruleObjects) {

                    Map<String, Object> map = ((Map<String, Object>) object);

                    String name = map.get(RuleConstant.NAME).toString();
                    if (ruleKeys.containsKey(filename + ":" + name)) {
                        continue;
                    }

                    List<String> actions = new ArrayList<>();

                    String conditionString = map.get(RuleConstant.CONDITION).toString();

                    List<String> fields = (List<String>) map.get(RuleConstant.ACTIONS);
                    if (fields != null) {
                        for (String field : fields) {
                            if (field.trim().startsWith(RuleConstant.CALCULATION) || field.trim().startsWith(RuleConstant.CONSTRAINT)) {
                                conditionString += " " + field;
                            }
                        }

                    }

                    actions.addAll(getConditionKeys(conditionString));
                    ruleKeys.put(filename + ":" + name, actions);

                    if (name.equals(fieldKey)) {
                        break;
                    }

                }
            }

        } catch (Exception e) {
            Log.e(TAG, e.getMessage(), e);
        }

        return ruleKeys.get(filename + ":" + fieldKey);
    }

    private List<String> getConditionKeys(String condition) {
        String cleanString = cleanConditionString(condition);
        String[] conditionTokens = cleanString.split(" ");
        Map<String, Boolean> conditionKeys = new HashMap<>();

        for (int i = 0; i < conditionTokens.length; i++) {

            if (conditionTokens[i].contains(RuleConstant.STEP) || conditionTokens[i].contains(RuleConstant.PREFIX.GLOBAL)) {
                String conditionToken = cleanToken(conditionTokens[i]);

                conditionKeys.put(conditionToken, true);
            }
        }

        return new ArrayList<>(conditionKeys.keySet());


    }

    private String cleanConditionString(String conditionStringRaw) {
        String conditionString = conditionStringRaw;

        for (String token : PREFICES_OF_INTEREST) {

            conditionString = conditionString.replaceAll(token, " " + token);
        }

        return conditionString.replaceAll("  ", " ");
    }

    private String cleanToken(String conditionTokenRaw) {

        String conditionToken = conditionTokenRaw.trim();


        for (int i = 0; i < conditionToken.length(); i++) {

            if (JAVA_OPERATORS.contains(conditionToken.charAt(i))) {

                if (i == 0) {
                    conditionToken = cleanToken(conditionToken.substring(1));
                } else {

                    conditionToken = conditionToken.substring(0, conditionToken.indexOf(conditionToken.charAt(i)));

                    break;
                }
            }

        }

        return conditionToken;
    }

    protected JSONArray fetchFields(JSONObject parentJson, Boolean popup) {
        JSONArray fields = new JSONArray();
        try {
            if (parentJson.has(JsonFormConstants.SECTIONS) && parentJson.get(JsonFormConstants.SECTIONS) instanceof JSONArray) {
                JSONArray sections = parentJson.getJSONArray(JsonFormConstants.SECTIONS);

                for (int i = 0; i < sections.length(); i++) {
                    JSONObject sectionJson = sections.getJSONObject(i);
                    if (sectionJson.has(JsonFormConstants.FIELDS)) {
                        if (popup) {
                            JSONArray jsonArray = sectionJson.getJSONArray(JsonFormConstants.FIELDS);
                            for (int k = 0; k < jsonArray.length(); k++) {
                                JSONObject item = jsonArray.getJSONObject(k);
                                if (item.getString(JsonFormConstants.KEY).equals(genericDialogInterface.getParentKey())) {
                                    if (item.has(JsonFormConstants.EXTRA_REL) && item.has(JsonFormConstants.HAS_EXTRA_REL)) {
                                        fields = formUtils.concatArray(fields, specifyFields(item));
                                    }
                                }
                            }
                        } else {
                            fields = formUtils.concatArray(fields, sectionJson.getJSONArray(JsonFormConstants.FIELDS));
                        }

                    }
                }
            } else if (parentJson.has(JsonFormConstants.FIELDS) && parentJson.get(JsonFormConstants.FIELDS) instanceof JSONArray) {
                if (popup) {
                    JSONArray jsonArray = parentJson.getJSONArray(JsonFormConstants.FIELDS);
                    for (int k = 0; k < jsonArray.length(); k++) {
                        JSONObject item = jsonArray.getJSONObject(k);
                        if (item.getString(JsonFormConstants.KEY).equals(genericDialogInterface.getParentKey()) && item.has(JsonFormConstants.EXTRA_REL) && item.has(JsonFormConstants.HAS_EXTRA_REL)) {
                            fields = specifyFields(item);
                        }
                    }
                } else {
                    fields = parentJson.getJSONArray(JsonFormConstants.FIELDS);
                }

            }
        } catch (JSONException e) {
            e.printStackTrace();
        }

        return fields;
    }

    protected JSONArray specifyFields(JSONObject parentJson) {
        JSONArray fields = new JSONArray();
        if (parentJson.has(JsonFormConstants.HAS_EXTRA_REL)) {
            String optionKey;
            try {
                optionKey = (String) parentJson.get(JsonFormConstants.HAS_EXTRA_REL);
                JSONArray options = parentJson.getJSONArray(JsonFormConstants.OPTIONS_FIELD_NAME);
                if (options.length() > 0) {
                    for (int j = 0; j < options.length(); j++) {
                        JSONObject jsonObject = options.getJSONObject(j);
                        String objectKey = (String) jsonObject.get(JsonFormConstants.KEY);
                        if (objectKey.equals(optionKey) && jsonObject.has(JsonFormConstants.CONTENT_FORM)) {
                            if (extraFieldsWithValues != null) {
                                fields = extraFieldsWithValues;
                            } else {
                                String formLocation = jsonObject.has(JsonFormConstants.CONTENT_FORM_LOCATION) ? jsonObject.getString(JsonFormConstants.CONTENT_FORM_LOCATION) : "";
                                fields = getSubFormFields(jsonObject.get(JsonFormConstants.CONTENT_FORM).toString(), formLocation, fields);
                            }
                        }
                    }
                }
            } catch (JSONException e) {
                e.printStackTrace();
            }
        }
        return fields;
    }

    protected JSONArray getSubFormFields(String subFormName, String subFormLocation, JSONArray
            fields) {
        JSONArray fieldArray = new JSONArray();
        JSONObject jsonObject = null;
        try {
            jsonObject = FormUtils.getSubFormJson(subFormName, subFormLocation, context);
        } catch (Exception e) {
            e.printStackTrace();
        }
        if (jsonObject != null) {
            try {
                JSONArray jsonArray = jsonObject.getJSONArray(JsonFormConstants.CONTENT_FORM);
                if (jsonArray != null && jsonArray.length() > 0) {
                    fieldArray = formUtils.concatArray(fields, jsonArray);
                }
            } catch (JSONException e) {
                e.printStackTrace();
            }
        }

        return fieldArray;
    }

    public JSONObject getObjectUsingAddress(String[] address, boolean popup) throws JSONException {
        if (address != null && address.length > 1) {

            if (RuleConstant.RULES_ENGINE.equals(address[0])) {

                String fieldKey = address[2];

                List<String> rulesList = getRules(address[1], fieldKey);
                if (rulesList != null) {
                    JSONObject result = new JSONObject();

                    JSONArray rulesArray = new JSONArray();

                    for (Integer h = 1; h < mJSONObject.getInt(JsonFormConstants.COUNT) + 1; h++) {
                        JSONArray fields = fetchFields(mJSONObject.getJSONObject(RuleConstant.STEP + h), popup);
                        for (int i = 0; i < fields.length(); i++) {
                            if (rulesList.contains(RuleConstant.STEP + h + "_" + fields.getJSONObject(i).getString(JsonFormConstants.KEY))) {

                                JSONObject fieldObject = fields.getJSONObject(i);
                                fieldObject.put(RuleConstant.STEP, RuleConstant.STEP + h);

                                rulesArray.put(fieldObject);
                            }
                        }
                    }
                    result.put(RuleConstant.RESULT, rulesArray);
                    return result;
                }


            } else {


                JSONArray fields = fetchFields(mJSONObject.getJSONObject(address[0]), popup);
                for (int i = 0; i < fields.length(); i++) {
                    if (fields.getJSONObject(i).getString(JsonFormConstants.KEY).equals(address[1])) {
                        return fields.getJSONObject(i);
                    }
                }
            }
        }

        return null;
    }

    private boolean isNumberSelector(String itemKey, String selectedKey) {
        return selectedKey.startsWith(JsonFormConstants.NUMBERS_SELECTOR) && ((itemKey.substring(0, itemKey.lastIndexOf('_')).equals(selectedKey.substring(0, selectedKey.lastIndexOf('_'))) || selectedKey.equals(itemKey + JsonFormConstants.SUFFIX.SPINNER)));
    }

    protected void widgetsWriteValue(String stepName, String key, String value, String openMrsEntityParent, String openMrsEntity, String openMrsEntityId, boolean popup) throws JSONException {
        synchronized (mJSONObject) {
            JSONObject jsonObject = mJSONObject.getJSONObject(stepName);
            JSONArray fields = fetchFields(jsonObject, popup);
            for (int i = 0; i < fields.length(); i++) {
                JSONObject item = fields.getJSONObject(i);
                String keyAtIndex = item.getString(JsonFormConstants.KEY);
                String itemType = item.has(JsonFormConstants.TYPE) ? item.getString(JsonFormConstants.TYPE) : "";
                keyAtIndex = itemType.equals(JsonFormConstants.NUMBERS_SELECTOR) ? keyAtIndex + JsonFormConstants.SUFFIX.SPINNER : keyAtIndex;
                if (key.equals(keyAtIndex) || isNumberSelector(key, keyAtIndex)) {
                    if (item.has(JsonFormConstants.TEXT)) {
                        item.put(JsonFormConstants.TEXT, value);
                    } else {
                        if (popup) {
                            String itemText = "";
                            if (itemType.equals(JsonFormConstants.NATIVE_RADIO_BUTTON)) {
                                itemText = formUtils.getRadioButtonText(item, value);
                            }
                            genericDialogInterface.addSelectedValues(formUtils.addAssignedValue(keyAtIndex, "", value, itemType, itemText));
                            extraFieldsWithValues = fields;
                        }
                        item.put(JsonFormConstants.VALUE, value);
                    }
                    item.put(JsonFormConstants.OPENMRS_ENTITY_PARENT, openMrsEntityParent);
                    item.put(JsonFormConstants.OPENMRS_ENTITY, openMrsEntity);
                    item.put(JsonFormConstants.OPENMRS_ENTITY_ID, openMrsEntityId);
                    refreshCalculationLogic(key, null, popup);
                    refreshSkipLogic(key, null, popup);
                    refreshConstraints(key, null);
                    refreshMediaLogic(key, value);
                    return;
                }
            }
        }
    }

    protected void refreshMediaLogic(String key, String value) {
        try {
            JSONObject object = getStep("step1");
            JSONArray fields = object.getJSONArray("fields");
            for (int i = 0; i < fields.length(); i++) {
                JSONObject questionGroup = fields.getJSONObject(i);
                if ((questionGroup.has("key") && questionGroup.has("has_media_content"))
                        && (questionGroup.getString("key").equalsIgnoreCase(key))
                        && (questionGroup.getBoolean("has_media_content"))) {
                    JSONArray medias = questionGroup.getJSONArray("media");
                    for (int j = 0; j < medias.length(); j++) {
                        JSONObject media = medias.getJSONObject(j);
                        mediaDialog(media, value);
                    }
                }
            }
        } catch (Exception e) {

        }
    }

    public void mediaDialog(JSONObject media, String value) {
        try {
            if (media.getString("media_trigger_value").equalsIgnoreCase(value)) {
                String mediatype = media.getString("media_type");
                String medialink = media.getString("media_link");
                String mediatext = media.getString("media_text");

                infoDialog(mediatype, medialink, mediatext);
            }
        } catch (Exception e) {

        }
    }

    private void infoDialog(String mediatype, String medialink, String mediatext) {
        final FancyAlertDialog.Builder builder = new FancyAlertDialog.Builder((Activity) context);
        builder.setTitle("Info");
        builder.setBackgroundColor(Color.parseColor("#208CC5")).setPositiveBtnBackground(Color.parseColor("#208CC5"))  //Don't pass R.color.colorvalue
                .setPositiveBtnText("OK").setAnimation(Animation.SLIDE)
                .isCancellable(true)
                .setIcon(com.shashank.sony.fancydialoglib.R.drawable.ic_person_black_24dp, Icon.Visible);
        builder.setMessage(mediatext);
        if (mediatype.equalsIgnoreCase("image")) {
            builder.setImagetoshow(medialink);
        } else if (mediatype.equalsIgnoreCase("video")) {
            builder.setVideopath(medialink);
        }
        builder.build();
    }

    private void updateCalculation(Map<String, String> valueMap, View view, String rulesFile) {

        try {

            String calculation = rulesEngineFactory.getCalculation(valueMap, rulesFile);

            if (view instanceof CheckBox) {

                //For now were only handling checkbox titles only

                TextView checkboxLabel = ((View) view.getParent().getParent()).findViewById(R.id.label_text);
                if (checkboxLabel != null) {
                    checkboxLabel.setText(getRenderText(calculation, checkboxLabel.getTag(R.id.original_text).toString()));
                }


            } else if (view instanceof TextableView) {
                TextableView textView = ((TextableView) view);
                textView.setText(calculation.charAt(0) == '{' ? getRenderText(calculation, textView.getTag(R.id.original_text).toString()) : calculation);
            } else if (view instanceof EditText) {

                ((EditText) view).setText(calculation);

            } else if (view instanceof RadioGroup) {
                RadioGroup radioButton = (RadioGroup) view;
                int count = radioButton.getChildCount();
                for (int i = 0; i < count; i++) {
                    TextView renderView = radioButton.getChildAt(i).findViewById(R.id.extraInfoTextView);

                    // if (((AppCompatRadioButton) ((ViewGroup) radioButton.getChildAt(i).findViewById(R.id.radioContentLinearLayout)).getChildAt(0)).isChecked()) {

                    if (renderView.getTag(R.id.original_text) == null) {
                        renderView.setTag(R.id.original_text, renderView.getText());
                    }
                    renderView.setText(calculation.charAt(0) == '{' ? getRenderText(calculation, renderView.getTag(R.id.original_text).toString()) : calculation);

                    renderView.setVisibility(renderView.getText().toString().contains("{") || renderView.getText().toString().equals("0") ? View.GONE : View.VISIBLE);
                    // break;
                    //} else {
                    //  renderView.setVisibility(renderView.getText().toString().contains("{") ? View.GONE : View.VISIBLE);
                    //}

                }

            } else {

                ((TextView) view).setText(calculation);
            }

        } catch (Exception e) {
            Log.e(TAG, e.getMessage(), e);
            Log.d(TAG, "calling updateCalculation on Non TextView or Text View decendant");
        }

    }

    private String getRenderText(String calculation, String textTemplate) {
        Map<String, Object> valueMap = new Gson().fromJson(calculation, new TypeToken<HashMap<String, Object>>() {
        }.getType());

        return stringFormat(textTemplate, valueMap);
    }

    public String stringFormat(String string, Map<String, Object> valueMap) {
        String resString = string;
        for (Map.Entry<String, Object> entry : valueMap.entrySet()) {
            resString = resString.replace("{" + entry.getKey() + "}", getTemplateValue(entry.getValue()));
        }

        return resString;
    }


    private String getTemplateValue(Object object) {
        String result = "";
        if (object instanceof List) {
            List<String> valueList = (List<String>) object;
            for (int i = 0; i < valueList.size(); i++) {
                result += valueList.get(i);
                if (i != (valueList.size() - 1)) {
                    result += ", ";
                }
            }
        } else {
            result = object.toString();
            result = result.contains(".0") ? result.substring(0, result.indexOf(".0")) : result;//Fix automatic conversion float bug
        }

        return result;
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
    public boolean next() {
        LinearLayout mainView = rootView.findViewById(R.id.main_layout);
        ValidationStatus validationStatus = null;
        try {
            validationStatus = writeValuesAndValidate(mainView);
            if (validationStatus.isValid()) {

                mStepName = mStepDetails.optString("next");
                hideKeyBoard();

                clearFormDataViews();
                loadForm();
                refreshCalculationLogic(null, null, false);
                refreshSkipLogic(null, null, false);
                refreshConstraints(null, null);

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
        for (View childAt : getFormDataViews()) {
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

                writeValue(mStepName, key, rawValue,
                        openMrsEntityParent, openMrsEntity, openMrsEntityId, popup);
            } else if (childAt instanceof NativeEditText) {
                NativeEditText editText = (NativeEditText) childAt;

                String rawValue = (String) editText.getTag(R.id.raw_value);
                if (rawValue == null) {
                    rawValue = editText.getText().toString();
                }

                writeValue(mStepName, key, rawValue,
                        openMrsEntityParent, openMrsEntity, openMrsEntityId, popup);
            } else if (childAt instanceof ImageView) {
                Object path = childAt.getTag(R.id.imagePath);
                if (path instanceof String) {
                    writeValue(mStepName, key, (String) path, openMrsEntityParent,
                            openMrsEntity, openMrsEntityId, popup);
                }
            } else if (childAt instanceof CheckBox) {
                String parentKey = (String) childAt.getTag(R.id.key);
                String childKey = (String) childAt.getTag(R.id.childKey);
                writeValue(mStepName, parentKey, JsonFormConstants.OPTIONS_FIELD_NAME, childKey,
                        String.valueOf(((CheckBox) childAt).isChecked()), openMrsEntityParent,
                        openMrsEntity, openMrsEntityId, popup);
            } else if (childAt instanceof RadioButton) {
                String parentKey = (String) childAt.getTag(R.id.key);
                String childKey = (String) childAt.getTag(R.id.childKey);
                if (((RadioButton) childAt).isChecked()) {
                    writeValue(mStepName, parentKey, childKey, openMrsEntityParent,
                            openMrsEntity, openMrsEntityId, popup);
                }
            } else if (childAt instanceof Button) {
                Button button = (Button) childAt;
                String rawValue = (String) button.getTag(R.id.raw_value);
                writeValue(mStepName, key, rawValue, openMrsEntityParent, openMrsEntity, openMrsEntityId, popup);
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

    public void onSaveClick(LinearLayout mainView) throws JSONException {
        ValidationStatus validationStatus = writeValuesAndValidate(mainView);
        if (validationStatus.isValid() || Boolean.valueOf(mainView.getTag(R.id.skip_validation).toString())) {
            Intent returnIntent = new Intent();
            onFormFinish();
            returnIntent.putExtra("json", currentJsonState());
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

                mStepName = "step1";
                hideKeyBoard();

                clearFormDataViews();
                loadForm();
                refreshCalculationLogic(null, null, false);
                refreshSkipLogic(null, null, false);
                refreshConstraints(null, null);

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
        return this;
    }
}