package com.vijay.jsonwizard.widgets;

import android.content.Context;
import android.graphics.Typeface;
import android.os.AsyncTask;
import android.text.Editable;
import android.text.InputType;
import android.text.SpannableString;
import android.text.TextWatcher;
import android.text.style.StyleSpan;
import android.util.Log;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.ViewParent;
import android.view.inputmethod.EditorInfo;
import android.view.inputmethod.InputMethodManager;
import android.widget.EditText;
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
import com.vijay.jsonwizard.rules.RuleConstant;
import com.vijay.jsonwizard.utils.FormUtils;
import com.vijay.jsonwizard.utils.Utils;
import com.vijay.jsonwizard.validators.edittext.MaxNumericValidator;
import com.vijay.jsonwizard.validators.edittext.MinNumericValidator;
import com.vijay.jsonwizard.validators.edittext.RequiredValidator;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.UUID;

import timber.log.Timber;

import static com.vijay.jsonwizard.constants.JsonFormConstants.FIELDS;
import static com.vijay.jsonwizard.constants.JsonFormConstants.KEY;
import static com.vijay.jsonwizard.constants.JsonFormConstants.RELEVANCE;
import static com.vijay.jsonwizard.constants.JsonFormConstants.TYPE;
import static com.vijay.jsonwizard.constants.JsonFormConstants.VALUE;
import static com.vijay.jsonwizard.constants.JsonFormConstants.V_RELATIVE_MAX;
import static com.vijay.jsonwizard.utils.Utils.hideProgressDialog;
import static com.vijay.jsonwizard.utils.Utils.showProgressDialog;

/**
 * @author Vincent Karuri
 */
public class RepeatingGroupFactory implements FormWidgetFactory {

    private final String TAG = RepeatingGroupFactory.class.getName();

    protected int MAX_NUM_REPEATING_GROUPS = 35;

    private final ViewGroup.LayoutParams WIDTH_MATCH_PARENT_HEIGHT_WRAP_CONTENT = new LinearLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT);
    private static Map<Integer, String> repeatingGroupLayouts = new HashMap<>();

    private final String REFERENCE_EDIT_TEXT_HINT = "reference_edit_text_hint";
    private final String REPEATING_GROUP_LABEL = "repeating_group_label";

    protected final int REPEATING_GROUP_LABEL_TEXT_COLOR = R.color.black;

    private ImageButton doneButton;
    private WidgetArgs widgetArgs;
    private Map<String, List<Map<String, Object>>> readFileListMap = new HashMap<>();

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

        return views;
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

        referenceEditText.setHint(referenceEditTextHint);
        referenceEditText.setTag(R.id.repeating_group_label, repeatingGroupLabel);
        referenceEditText.setTag(R.id.extraPopup, false);
        referenceEditText.setTag(R.id.repeating_group_item_count, 1);
        referenceEditText.setTag(R.id.key, "reference_edit_text");
        referenceEditText.setTag(R.id.openmrs_entity_parent, "");
        referenceEditText.setTag(R.id.openmrs_entity, "");
        referenceEditText.setTag(R.id.openmrs_entity_id, "");

        referenceEditText.setInputType(InputType.TYPE_CLASS_NUMBER | InputType.TYPE_NUMBER_FLAG_SIGNED);
        referenceEditText.addValidator(new RegexpValidator(context.getString(R.string.repeating_group_number_format_err_msg), "\\d*"));
        referenceEditText.addValidator(new MaxNumericValidator(context.getString(R.string.repeating_group_max_value_err_msg, MAX_NUM_REPEATING_GROUPS), MAX_NUM_REPEATING_GROUPS));
        referenceEditText.addValidator(new MinNumericValidator(context.getString(R.string.repeating_group_min_value_err_msg), 0));

        addRequiredValidator(widgetArgs.getJsonObject(), referenceEditText);
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

    private void addOnDoneAction(TextView textView) {
        try {
            InputMethodManager inputMethodManager = (InputMethodManager) widgetArgs.getFormFragment().getContext().getSystemService(Context.INPUT_METHOD_SERVICE);
            inputMethodManager.hideSoftInputFromWindow(textView.getWindowToken(), 0);
            textView.clearFocus();
            attachRepeatingGroup(textView.getParent().getParent(), Integer.parseInt(textView.getText().toString()));
        } catch (Exception e) {
            Log.e(TAG, e.getStackTrace().toString());
        }
    }

    private void attachRepeatingGroup(final ViewParent parent, final int numRepeatingGroups) {

        if (numRepeatingGroups > MAX_NUM_REPEATING_GROUPS) {
            return;
        }

        class AttachRepeatingGroupTask extends AsyncTask<Void, Void, List<View>> {
            private LinearLayout rootLayout = (LinearLayout) parent;
            private List<View> repeatingGroups = new ArrayList<>();
            private int diff = 0;

            @Override
            protected void onPreExecute() {
                showProgressDialog(R.string.please_wait_title, R.string.creating_repeating_group_message, widgetArgs.getFormFragment().getContext());
            }

            @Override
            protected List<View> doInBackground(Void... objects) {
                int currNumRepeatingGroups = ((ViewGroup) parent).getChildCount() - 1;
                diff = numRepeatingGroups - currNumRepeatingGroups;
                for (int i = 0; i < diff; i++) {
                    try {
                        repeatingGroups.add(buildRepeatingGroupLayout(parent));
                    } catch (Exception e) {
                        Timber.e(e);
                    }
                }
                return repeatingGroups;
            }

            @Override
            protected void onPostExecute(List<View> result) {
                if (diff < 0) {
                    try {
                        JSONObject step = ((JsonApi) widgetArgs.getContext()).getmJSONObject().getJSONObject(widgetArgs.getStepName());
                        JSONArray fields = step.getJSONArray(FIELDS);
                        int currNumRepeatingGroups = rootLayout.getChildCount() - 1;
                        Set<String> keysToRemove = new HashSet<>();
                        for (int i = currNumRepeatingGroups; i > numRepeatingGroups; i--) {
                            String repeatingGroupKey = (String) rootLayout.getChildAt(i).getTag(R.id.repeating_group_key);
                            keysToRemove.add(repeatingGroupKey);
                            rootLayout.removeViewAt(i);
                        }
                        // remove deleted fields from form json
                        int len = fields.length();
                        for (int i = len - 1; i >= 0; i--) {
                            String[] key = ((String) fields.getJSONObject(i).get(KEY)).split("_");
                            if (keysToRemove.contains(key[key.length - 1])) {
                                fields.remove(i);
                            }
                        }
                        LinearLayout referenceLayout = (LinearLayout) ((LinearLayout) parent).getChildAt(0);
                        referenceLayout.getChildAt(0).setTag(R.id.repeating_group_item_count, rootLayout.getChildCount());
                    } catch (JSONException e) {
                        Log.e(TAG, e.getStackTrace().toString());
                    }
                } else {
                    for (View repeatingGroup : repeatingGroups) {
                        rootLayout.addView(repeatingGroup);
                    }
                }

                ((JsonApi) widgetArgs.getContext()).invokeRefreshLogic(null, false, null, null);
                hideProgressDialog();
                doneButton.setImageResource(R.drawable.ic_done_green);
            }
        }

        new AttachRepeatingGroupTask().execute();
    }

    private LinearLayout buildRepeatingGroupLayout(ViewParent parent) throws Exception {
        Context context = widgetArgs.getContext();

        LinearLayout repeatingGroup = new LinearLayout(context);
        repeatingGroup.setLayoutParams(WIDTH_MATCH_PARENT_HEIGHT_WRAP_CONTENT);
        repeatingGroup.setOrientation(LinearLayout.VERTICAL);

        LinearLayout rootLayout = (LinearLayout) ((LinearLayout) parent).getChildAt(0);
        EditText referenceEditText = (EditText) rootLayout.getChildAt(0);
        TextView repeatingGroupLabel = new TextView(context);
        formatRepeatingGroupLabelText(referenceEditText, repeatingGroupLabel, context);
        repeatingGroup.addView(repeatingGroupLabel);

        JSONArray repeatingGroupJson = new JSONArray(repeatingGroupLayouts.get(((LinearLayout) parent).getId()));
        String groupUniqueId = UUID.randomUUID().toString().replace("-", "");
        for (int i = 0; i < repeatingGroupJson.length(); i++) {
            JSONObject element = repeatingGroupJson.getJSONObject(i);
            String elementType = element.optString(TYPE, null);
            if (elementType != null) {
                addUniqueIdentifiers(element, groupUniqueId);
                FormWidgetFactory factory = widgetArgs.getFormFragment().getPresenter().getInteractor().map.get(elementType);
                List<View> widgetViews = factory.getViewsFromJson(widgetArgs.getStepName(), context, widgetArgs.getFormFragment(), element, widgetArgs.getListener(), widgetArgs.isPopup());
                for (View view : widgetViews) {
                    view.setLayoutParams(WIDTH_MATCH_PARENT_HEIGHT_WRAP_CONTENT);
                    repeatingGroup.addView(view);
                }
                // add element to json form object to be written into
                JSONObject step = ((JsonApi) widgetArgs.getContext()).getmJSONObject().getJSONObject(widgetArgs.getStepName());
                step.getJSONArray(FIELDS).put(element);
            }
        }
        repeatingGroup.setTag(R.id.repeating_group_key, groupUniqueId);

        return repeatingGroup;
    }

    private void formatRepeatingGroupLabelText(EditText referenceEditText, TextView repeatingGroupLabel, Context context) {
        int repeatingGroupItemCount = (Integer) referenceEditText.getTag(R.id.repeating_group_item_count);
        String repeatingGroupLabelTxt = (String) referenceEditText.getTag(R.id.repeating_group_label);
        SpannableString formattedLabel = new SpannableString(repeatingGroupLabelTxt + " " + repeatingGroupItemCount);
        formattedLabel.setSpan(new StyleSpan(Typeface.BOLD), 0, formattedLabel.length(), 0);
        formattedLabel.setSpan(new StyleSpan(Typeface.ITALIC), 0, formattedLabel.length(), 0);
        repeatingGroupLabel.setText(formattedLabel);
        repeatingGroupLabel.setTextSize(context.getResources().getInteger(R.integer.repeating_group_label_text_size));
        repeatingGroupLabel.setTextColor(context.getColor(REPEATING_GROUP_LABEL_TEXT_COLOR));
        referenceEditText.setTag(R.id.repeating_group_item_count, repeatingGroupItemCount + 1);
    }

    protected void addUniqueIdentifiers(JSONObject element, String uniqueId) throws JSONException {
        // make repeating group element key unique
        String currKey = element.getString(KEY);
        currKey += ("_" + uniqueId);
        element.put(KEY, currKey);
        // modify relevance to reflect changes in unique key name
        buildRelevanceWithUniqueIds(element, uniqueId);
        // modify relative max validator to reflect changes in unique key name
        JSONObject relativeMaxValidator = element.optJSONObject(V_RELATIVE_MAX);
        if (relativeMaxValidator != null) {
            String currRelativeMaxValidatorValue = relativeMaxValidator.getString(VALUE);
            String newRelativeMaxValidatorValue = currRelativeMaxValidatorValue + "_" + uniqueId;
            relativeMaxValidator.put(VALUE, newRelativeMaxValidatorValue);
        }
    }

    private void buildRelevanceWithUniqueIds(JSONObject element, String uniqueId) throws JSONException {
        JSONObject relevance = element.optJSONObject(RELEVANCE);
        if (relevance != null) {
            if (relevance.has(RuleConstant.RULES_ENGINE) && widgetArgs != null) {
                JSONObject jsonRulesEngineObject = relevance.optJSONObject(RuleConstant.RULES_ENGINE);
                JSONObject jsonExRules = jsonRulesEngineObject.optJSONObject(JsonFormConstants.JSON_FORM_KEY.EX_RULES);
                String fileName = "rule/" + jsonExRules.optString(RuleConstant.DYNAMIC);

                if (!readFileListMap.containsKey(fileName)) {
                    Iterable<Object> objectIterable = Utils.readYamlFile(fileName, widgetArgs.getContext());
                    List<Map<String, Object>> arrayList = new ArrayList<>();
                    while (objectIterable.iterator().hasNext()) {
                        Map<String, Object> map = (Map<String, Object>) objectIterable.iterator().next();
                        if (map != null) {
                            arrayList.add(map);
                        }
                    }
                    readFileListMap.put(fileName, arrayList);
                }

                List<Map<String, Object>> mapArrayList = readFileListMap.get(fileName);

                JSONArray jsonArrayRules = new JSONArray();
                JSONObject keyJsonObject = new JSONObject();
                keyJsonObject.put(KEY, uniqueId);
                jsonArrayRules.put(keyJsonObject);
                for (Map<String, Object> map : mapArrayList) {
                    JSONObject jsonRulesDynamicObject = new JSONObject();
                    String strCondition = (String) map.get(RuleConstant.CONDITION);
                    List<String> stringList = Utils.getConditionKeys(strCondition);
                    for (String s : stringList) {
                        strCondition = strCondition.replace(s, s + "_" + uniqueId);
                    }
                    jsonRulesDynamicObject.put(RuleConstant.NAME, String.valueOf(map.get(RuleConstant.NAME)).concat("_").concat(uniqueId));
                    jsonRulesDynamicObject.put(RuleConstant.DESCRIPTION, String.valueOf(map.get(RuleConstant.DESCRIPTION)).concat("_").concat(uniqueId));
                    jsonRulesDynamicObject.put(RuleConstant.PRIORITY, map.get(RuleConstant.PRIORITY));
                    jsonRulesDynamicObject.put(RuleConstant.ACTIONS, ((ArrayList<String>) map.get(RuleConstant.ACTIONS)).get(0));
                    jsonRulesDynamicObject.put(RuleConstant.CONDITION, String.valueOf(strCondition));
                    jsonArrayRules.put(jsonRulesDynamicObject);
                }

                jsonExRules.put(RuleConstant.DYNAMIC, jsonArrayRules);

            } else {
                String currRelevanceKey = relevance.keys().next();
                JSONObject relevanceObj = relevance.getJSONObject(currRelevanceKey);
                String newRelevanceKey = currRelevanceKey + "_" + uniqueId;
                relevance.remove(currRelevanceKey);
                relevance.put(newRelevanceKey, relevanceObj);
            }
        }

    }

    protected int getLayout() {
        return R.layout.native_form_repeating_group;
    }
}