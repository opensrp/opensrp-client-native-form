package com.vijay.jsonwizard.widgets;

import android.content.Context;
import android.support.annotation.NonNull;
import android.text.Editable;
import android.text.InputType;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewParent;
import android.view.inputmethod.EditorInfo;
import android.view.inputmethod.InputMethodManager;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.rengwuxian.materialedittext.MaterialEditText;
import com.rengwuxian.materialedittext.validation.RegexpValidator;
import com.vijay.jsonwizard.R;
import com.vijay.jsonwizard.constants.JsonFormConstants;
import com.vijay.jsonwizard.domain.WidgetArgs;
import com.vijay.jsonwizard.fragments.JsonFormFragment;
import com.vijay.jsonwizard.interfaces.CommonListener;
import com.vijay.jsonwizard.interfaces.FormWidgetFactory;
import com.vijay.jsonwizard.interfaces.JsonApi;
import com.vijay.jsonwizard.presenters.JsonFormFragmentPresenter;
import com.vijay.jsonwizard.task.AttachRepeatingGroupTask;
import com.vijay.jsonwizard.utils.FormUtils;
import com.vijay.jsonwizard.validators.edittext.MaxNumericValidator;
import com.vijay.jsonwizard.validators.edittext.MinNumericValidator;
import com.vijay.jsonwizard.validators.edittext.RequiredValidator;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import timber.log.Timber;

import static com.vijay.jsonwizard.constants.JsonFormConstants.KEY;
import static com.vijay.jsonwizard.constants.JsonFormConstants.VALUE;

/**
 * @author Vincent Karuri
 */
public class RepeatingGroupFactory implements FormWidgetFactory {
    protected int MAX_NUM_REPEATING_GROUPS = 35;
    private final String REFERENCE_EDIT_TEXT_HINT = "reference_edit_text_hint";
    private final String REPEATING_GROUP_LABEL = "repeating_group_label";
    private static Map<Integer, String> repeatingGroupLayouts = new HashMap<>();

    private ImageButton doneButton;
    private WidgetArgs widgetArgs;

    @Override
    public List<View> getViewsFromJson(final String stepName, final Context context, final JsonFormFragment formFragment, final JSONObject jsonObject, final CommonListener listener, final boolean popup) throws Exception {
        List<View> views = new ArrayList<>(1);
        LinearLayout rootLayout = (LinearLayout) LayoutInflater.from(context).inflate(getLayout(), null);

        final int rootLayoutId = View.generateViewId();
        rootLayout.setId(rootLayoutId);
        views.add(rootLayout);

        JSONArray repeatingGroupLayout = jsonObject.getJSONArray(VALUE);
        repeatingGroupLayouts.put(rootLayoutId, repeatingGroupLayout.toString());

        this.widgetArgs = new WidgetArgs()
                .withStepName(stepName)
                .withContext(context)
                .withFormFragment(formFragment)
                .withJsonObject(jsonObject)
                .withListener(listener)
                .withPopup(popup);

        final MaterialEditText referenceEditText = rootLayout.findViewById(R.id.reference_edit_text);
        final String referenceEditTextHint = jsonObject.optString(REFERENCE_EDIT_TEXT_HINT, context.getString(R.string.enter_number_of_repeating_group_items));
        final String repeatingGroupLabel = jsonObject.optString(REPEATING_GROUP_LABEL, context.getString(R.string.repeating_group_item));
        setUpReferenceEditText(referenceEditText, referenceEditTextHint, repeatingGroupLabel);

        doneButton = rootLayout.findViewById(R.id.btn_repeating_group_done);
        doneButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                addOnDoneAction(referenceEditText);
            }
        });

        ((JsonApi) context).addFormDataView(referenceEditText);
        setViewTags(rootLayout);
        prepareViewChecks(rootLayout, context);

        return views;
    }

    private void setViewTags(@NonNull LinearLayout rootLayout) {
        JSONArray canvasIds = new JSONArray();
        canvasIds.put(rootLayout.getId());
        rootLayout.setTag(R.id.canvas_ids, canvasIds.toString());
        rootLayout.setTag(R.id.key, widgetArgs.getJsonObject().optString(KEY));
        rootLayout.setTag(R.id.type, widgetArgs.getJsonObject().optString(JsonFormConstants.TYPE));
        rootLayout.setTag(R.id.extraPopup, widgetArgs.isPopup());
        rootLayout.setTag(R.id.address, widgetArgs.getStepName() + ":" + widgetArgs.getJsonObject().optString(KEY));
    }

    private void prepareViewChecks(@NonNull LinearLayout view, @NonNull Context context) {
        String relevance = widgetArgs.getJsonObject().optString(JsonFormConstants.RELEVANCE);
        String constraints = widgetArgs.getJsonObject().optString(JsonFormConstants.CONSTRAINTS);
        String calculation = widgetArgs.getJsonObject().optString(JsonFormConstants.CALCULATION);

        if (!TextUtils.isEmpty(relevance) && context instanceof JsonApi) {
            view.setTag(R.id.relevance, relevance);
            ((JsonApi) context).addSkipLogicView(view);
        }

        if (!TextUtils.isEmpty(constraints) && context instanceof JsonApi) {
            view.setTag(R.id.constraints, constraints);
            ((JsonApi) context).addConstrainedView(view);
        }

        if (!TextUtils.isEmpty(calculation) && context instanceof JsonApi) {
            view.setTag(R.id.calculation, calculation);
            ((JsonApi) context).addCalculationLogicView(view);
        }

    }

    private void setUpReferenceEditText(final MaterialEditText referenceEditText, String referenceEditTextHint, String repeatingGroupLabel) throws JSONException {
        Context context = widgetArgs.getContext();
        referenceEditText.setOnEditorActionListener(new TextView.OnEditorActionListener() {
            @Override
            public boolean onEditorAction(TextView v, int actionId, KeyEvent event) {
                if (actionId == EditorInfo.IME_ACTION_DONE) {
                    addOnDoneAction(v);
                    return true;
                }
                return false;
            }
        });

        referenceEditText.setTag(R.id.address, widgetArgs.getStepName() + ":" + widgetArgs.getJsonObject().getString(KEY));
        attachTextChangedListener(referenceEditText);
        referenceEditText.setHint(referenceEditTextHint);
        referenceEditText.setTag(R.id.repeating_group_label, repeatingGroupLabel);
        referenceEditText.setTag(R.id.extraPopup, false);
        referenceEditText.setTag(R.id.repeating_group_item_count, 1);
        referenceEditText.setTag(R.id.key, JsonFormConstants.REFERENCE_EDIT_TEXT);
        referenceEditText.setTag(R.id.openmrs_entity_parent, "");
        referenceEditText.setTag(R.id.openmrs_entity, "");
        referenceEditText.setTag(R.id.openmrs_entity_id, "");

        referenceEditText.setInputType(InputType.TYPE_CLASS_NUMBER | InputType.TYPE_NUMBER_FLAG_SIGNED);
        referenceEditText.addValidator(new RegexpValidator(context.getString(R.string.repeating_group_number_format_err_msg), "\\d*"));
        referenceEditText.addValidator(new MaxNumericValidator(context.getString(R.string.repeating_group_max_value_err_msg, MAX_NUM_REPEATING_GROUPS), MAX_NUM_REPEATING_GROUPS));
        referenceEditText.addValidator(new MinNumericValidator(context.getString(R.string.repeating_group_min_value_err_msg), 0));

        addRequiredValidator(widgetArgs.getJsonObject(), referenceEditText);
    }

    private void attachTextChangedListener(final MaterialEditText referenceEditText) {
        referenceEditText.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
                // do nothing
            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                // do nothing
            }

            @Override
            public void afterTextChanged(Editable s) {
                doneButton.setImageResource(R.drawable.ic_done_grey);
                JsonFormFragmentPresenter.validate(widgetArgs.getFormFragment(), referenceEditText, false);
            }
        });
    }

    private void addRequiredValidator(JSONObject jsonObject, MaterialEditText editText) throws JSONException {
        JSONObject requiredObject = jsonObject.optJSONObject(JsonFormConstants.V_REQUIRED);
        if (requiredObject != null) {
            boolean requiredValue = requiredObject.getBoolean(VALUE);
            if (Boolean.TRUE.equals(requiredValue)) {
                editText.addValidator(new RequiredValidator(requiredObject.getString(JsonFormConstants.ERR)));
                FormUtils.setRequiredOnHint(editText);
            }
        }
    }

    @Override
    public List<View> getViewsFromJson(final String stepName, Context context, JsonFormFragment formFragment, JSONObject jsonObject, CommonListener listener) throws Exception {
        return getViewsFromJson(stepName, context, formFragment, jsonObject, listener, false);
    }

    protected void addOnDoneAction(TextView textView) {
        try {
            InputMethodManager inputMethodManager = (InputMethodManager) widgetArgs.getFormFragment().getContext().getSystemService(Context.INPUT_METHOD_SERVICE);
            inputMethodManager.hideSoftInputFromWindow(textView.getWindowToken(), 0);
            textView.clearFocus();
            attachRepeatingGroup(textView.getParent().getParent(), Integer.parseInt(textView.getText().toString()));
        } catch (Exception e) {
            Timber.e(e, " --> addOnDoneAction");
        }
    }

    private void attachRepeatingGroup(final ViewParent parent, final int numRepeatingGroups) {
        if (numRepeatingGroups > MAX_NUM_REPEATING_GROUPS) {
            return;
        }

        new AttachRepeatingGroupTask(parent, numRepeatingGroups, repeatingGroupLayouts, widgetArgs, doneButton).execute();
    }

    protected int getLayout() {
        return R.layout.native_form_repeating_group;
    }
}