package com.vijay.jsonwizard.task;

import android.content.Context;
import android.graphics.Typeface;
import android.os.AsyncTask;
import android.text.SpannableString;
import android.text.style.StyleSpan;
import android.view.View;
import android.view.ViewGroup;
import android.view.ViewParent;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.vijay.jsonwizard.R;
import com.vijay.jsonwizard.constants.JsonFormConstants;
import com.vijay.jsonwizard.domain.WidgetArgs;
import com.vijay.jsonwizard.interfaces.FormWidgetFactory;
import com.vijay.jsonwizard.interfaces.JsonApi;
import com.vijay.jsonwizard.rules.RuleConstant;
import com.vijay.jsonwizard.utils.Utils;

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

import static com.vijay.jsonwizard.constants.JsonFormConstants.CALCULATION;
import static com.vijay.jsonwizard.constants.JsonFormConstants.FIELDS;
import static com.vijay.jsonwizard.constants.JsonFormConstants.KEY;
import static com.vijay.jsonwizard.constants.JsonFormConstants.RELEVANCE;
import static com.vijay.jsonwizard.constants.JsonFormConstants.TYPE;
import static com.vijay.jsonwizard.constants.JsonFormConstants.VALUE;
import static com.vijay.jsonwizard.constants.JsonFormConstants.V_RELATIVE_MAX;
import static com.vijay.jsonwizard.utils.Utils.hideProgressDialog;
import static com.vijay.jsonwizard.utils.Utils.showProgressDialog;

public class AttachRepeatingGroupTask extends AsyncTask<Void, Void, List<View>> {

    private LinearLayout rootLayout;
    private final ViewParent parent;
    private List<View> repeatingGroups = new ArrayList<>();
    private int diff = 0;
    private ImageButton doneButton;
    private WidgetArgs widgetArgs;
    private int numRepeatingGroups;
    private final ViewGroup.LayoutParams WIDTH_MATCH_PARENT_HEIGHT_WRAP_CONTENT = new LinearLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT);
    protected final int REPEATING_GROUP_LABEL_TEXT_COLOR = R.color.black;
    private Map<String, List<Map<String, Object>>> rulesFileMap = new HashMap<>();
    private Map<Integer, String> repeatingGroupLayouts;
    private int currNumRepeatingGroups;

    public AttachRepeatingGroupTask(final ViewParent parent, int numRepeatingGroups, Map<Integer, String> repeatingGroupLayouts, WidgetArgs widgetArgs, ImageButton doneButton) {
        this.rootLayout = (LinearLayout) parent;
        this.parent = parent;
        this.numRepeatingGroups = numRepeatingGroups;
        this.widgetArgs = widgetArgs;
        this.doneButton = doneButton;
        this.repeatingGroupLayouts = repeatingGroupLayouts;
        currNumRepeatingGroups = ((ViewGroup) parent).getChildCount() - 1;
    }

    @Override
    protected void onPreExecute() {
        showProgressDialog(R.string.please_wait_title, R.string.creating_repeating_group_message, widgetArgs.getFormFragment().getContext());
    }

    @Override
    protected List<View> doInBackground(Void... voids) {
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
                Timber.e(e, " --> onPostExecute");
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

    private LinearLayout buildRepeatingGroupLayout(final ViewParent parent) throws Exception {
        Context context = widgetArgs.getContext();

        LinearLayout repeatingGroup = new LinearLayout(context);
        repeatingGroup.setLayoutParams(WIDTH_MATCH_PARENT_HEIGHT_WRAP_CONTENT);
        repeatingGroup.setOrientation(LinearLayout.VERTICAL);

        LinearLayout rootLayout = (LinearLayout) ((LinearLayout) parent).getChildAt(0);
        EditText referenceEditText = (EditText) rootLayout.getChildAt(0);
        TextView repeatingGroupLabel = new TextView(context);
        if (widgetArgs.getJsonObject().optBoolean(JsonFormConstants.RepeatingGroupFactory.SHOW_GROUP_LABEL, true)) {
            formatRepeatingGroupLabelText(referenceEditText, repeatingGroupLabel, context);
        }

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
        repeatingGroupLabel.setTextColor(context.getResources().getColor(REPEATING_GROUP_LABEL_TEXT_COLOR));
        referenceEditText.setTag(R.id.repeating_group_item_count, repeatingGroupItemCount + 1);
    }

    protected void addUniqueIdentifiers(JSONObject element, String uniqueId) throws JSONException {
        // make repeating group element key unique
        String currKey = element.getString(KEY);
        currKey += ("_" + uniqueId);
        element.put(KEY, currKey);
        // modify relevance to reflect changes in unique key name
        buildRelevanceWithUniqueId(element, uniqueId);
        buildCalculationWithUniqueId(element, uniqueId);
        // modify relative max validator to reflect changes in unique key name
        JSONObject relativeMaxValidator = element.optJSONObject(V_RELATIVE_MAX);
        if (relativeMaxValidator != null) {
            String currRelativeMaxValidatorValue = relativeMaxValidator.getString(VALUE);
            String newRelativeMaxValidatorValue = currRelativeMaxValidatorValue + "_" + uniqueId;
            relativeMaxValidator.put(VALUE, newRelativeMaxValidatorValue);
        }
    }

    private void buildRelevanceWithUniqueId(JSONObject element, String uniqueId) throws JSONException {
        JSONObject relevance = element.optJSONObject(RELEVANCE);
        if (relevance != null) {
            if (relevance.has(RuleConstant.RULES_ENGINE) && widgetArgs != null) {
                JSONObject jsonRulesEngineObject = relevance.optJSONObject(RuleConstant.RULES_ENGINE);
                JSONObject jsonExRules = jsonRulesEngineObject.optJSONObject(JsonFormConstants.JSON_FORM_KEY.EX_RULES);
                String fileName = JsonFormConstants.RULE + jsonExRules.optString(RuleConstant.RULES_DYNAMIC);

                if (!rulesFileMap.containsKey(fileName)) {
                    Iterable<Object> objectIterable = Utils.readYamlFile(fileName, widgetArgs.getContext());
                    List<Map<String, Object>> arrayList = new ArrayList<>();
                    while (objectIterable.iterator().hasNext()) {
                        Map<String, Object> map = (Map<String, Object>) objectIterable.iterator().next();
                        if (map != null) {
                            arrayList.add(map);
                        }
                    }
                    rulesFileMap.put(fileName, arrayList);
                }

                List<Map<String, Object>> mapArrayList = rulesFileMap.get(fileName);

                JSONArray jsonArrayRules = new JSONArray();
                JSONObject keyJsonObject = new JSONObject();
                keyJsonObject.put(KEY, uniqueId);
                jsonArrayRules.put(keyJsonObject);
                for (Map<String, Object> map : mapArrayList) {
                    JSONObject jsonRulesDynamicObject = new JSONObject();
                    String strCondition = (String) map.get(RuleConstant.CONDITION);
                    List<String> conditionKeys = Utils.getConditionKeys(strCondition);
                    for (String conditionKey : conditionKeys) {
                        strCondition = strCondition.replace(conditionKey, conditionKey + "_" + uniqueId);
                    }
                    jsonRulesDynamicObject.put(RuleConstant.NAME, String.valueOf(map.get(RuleConstant.NAME)).concat("_").concat(uniqueId));
                    jsonRulesDynamicObject.put(RuleConstant.DESCRIPTION, String.valueOf(map.get(RuleConstant.DESCRIPTION)).concat("_").concat(uniqueId));
                    jsonRulesDynamicObject.put(RuleConstant.PRIORITY, map.get(RuleConstant.PRIORITY));
                    jsonRulesDynamicObject.put(RuleConstant.ACTIONS, ((ArrayList<String>) map.get(RuleConstant.ACTIONS)).get(0));
                    jsonRulesDynamicObject.put(RuleConstant.CONDITION, String.valueOf(strCondition));
                    jsonArrayRules.put(jsonRulesDynamicObject);
                }

                jsonExRules.put(RuleConstant.RULES_DYNAMIC, jsonArrayRules);

            } else {
                String currRelevanceKey = relevance.keys().next();
                JSONObject relevanceObj = relevance.getJSONObject(currRelevanceKey);
                String newRelevanceKey = currRelevanceKey + "_" + uniqueId;
                relevance.remove(currRelevanceKey);
                relevance.put(newRelevanceKey, relevanceObj);
            }
        }

    }

    private void buildCalculationWithUniqueId(JSONObject element, String uniqueId) throws JSONException {
        JSONObject calculation = element.optJSONObject(CALCULATION);
        if (calculation != null) {
            if (calculation.has(RuleConstant.RULES_ENGINE) && widgetArgs != null) {
                JSONObject jsonRulesEngineObject = calculation.optJSONObject(RuleConstant.RULES_ENGINE);
                JSONObject jsonExRules = jsonRulesEngineObject.optJSONObject(JsonFormConstants.JSON_FORM_KEY.EX_RULES);
                String fileName = JsonFormConstants.RULE + jsonExRules.optString(RuleConstant.RULES_DYNAMIC);

                if (!rulesFileMap.containsKey(fileName)) {
                    Iterable<Object> objectIterable = Utils.readYamlFile(fileName, widgetArgs.getContext());
                    List<Map<String, Object>> arrayList = new ArrayList<>();
                    while (objectIterable.iterator().hasNext()) {
                        Map<String, Object> map = (Map<String, Object>) objectIterable.iterator().next();
                        if (map != null) {
                            arrayList.add(map);
                        }
                    }
                    rulesFileMap.put(fileName, arrayList);
                }

                List<Map<String, Object>> mapArrayList = rulesFileMap.get(fileName);

                JSONArray jsonArrayRules = new JSONArray();
                JSONObject keyJsonObject = new JSONObject();
                keyJsonObject.put(KEY, uniqueId);
                jsonArrayRules.put(keyJsonObject);
                for (Map<String, Object> map : mapArrayList) {
                    JSONObject jsonRulesDynamicObject = new JSONObject();
                    String strCondition = (String) map.get(RuleConstant.CONDITION);
                    List<String> conditionKeys = Utils.getConditionKeys(strCondition);
                    for (String conditionKey : conditionKeys) {
                        strCondition = strCondition.replace(conditionKey, conditionKey + "_" + uniqueId);
                    }
                    jsonRulesDynamicObject.put(RuleConstant.NAME, String.valueOf(map.get(RuleConstant.NAME)).concat("_").concat(uniqueId));
                    jsonRulesDynamicObject.put(RuleConstant.DESCRIPTION, String.valueOf(map.get(RuleConstant.DESCRIPTION)).concat("_").concat(uniqueId));
                    jsonRulesDynamicObject.put(RuleConstant.PRIORITY, map.get(RuleConstant.PRIORITY));
                    jsonRulesDynamicObject.put(RuleConstant.ACTIONS, ((ArrayList<String>) map.get(RuleConstant.ACTIONS)).get(0));
                    jsonRulesDynamicObject.put(RuleConstant.CONDITION, String.valueOf(strCondition));
                    jsonArrayRules.put(jsonRulesDynamicObject);
                }

                jsonExRules.put(RuleConstant.RULES_DYNAMIC, jsonArrayRules);

            } else {
                String currCalculationKey = calculation.keys().next();
                JSONObject calculationObj = calculation.getJSONObject(currCalculationKey);
                String newCalculationKey = currCalculationKey + "_" + uniqueId;
                calculation.remove(currCalculationKey);
                calculation.put(newCalculationKey, calculationObj);
            }
        }

    }
}
