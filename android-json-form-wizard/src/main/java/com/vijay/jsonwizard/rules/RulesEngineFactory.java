package com.vijay.jsonwizard.rules;

import android.content.Context;

import com.google.gson.Gson;

import org.jeasy.rules.api.Facts;
import org.jeasy.rules.api.Rule;
import org.jeasy.rules.api.RuleListener;
import org.jeasy.rules.api.Rules;
import org.jeasy.rules.api.RulesEngine;
import org.jeasy.rules.core.DefaultRulesEngine;
import org.jeasy.rules.core.RulesEngineParameters;
import org.jeasy.rules.mvel.MVELRuleFactory;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

import timber.log.Timber;

public class RulesEngineFactory implements RuleListener {
    public final String TAG = RulesEngineFactory.class.getCanonicalName();
    private Context context;
    private RulesEngine defaultRulesEngine;
    private Map<String, Rules> ruleMap;
    private String RULE_FOLDER_PATH = "rule/";
    private Rules rules;
    private String selectedRuleName;
    private Gson gson;
    private Map<String, String> globalValues;
    private RulesEngineHelper rulesEngineHelper;

    public RulesEngineFactory(Context context, Map<String, String> globalValues) {
        this.context = context;
        RulesEngineParameters parameters = new RulesEngineParameters().skipOnFirstAppliedRule(true);
        this.defaultRulesEngine = new DefaultRulesEngine(parameters);
        ((DefaultRulesEngine) this.defaultRulesEngine).registerRuleListener(this);
        this.ruleMap = new HashMap<>();
        gson = new Gson();
        this.globalValues = globalValues;
        this.rulesEngineHelper = new RulesEngineHelper();

    }

    public RulesEngineFactory() {
    }

    public boolean getRelevance(Facts relevanceFact, String ruleFilename) {
        Facts facts = initializeFacts(relevanceFact);
        facts.put(RuleConstant.IS_RELEVANT, false);
        rules = getRulesFromAsset(RULE_FOLDER_PATH + ruleFilename);
        processDefaultRules(rules, facts);

        return facts.get(RuleConstant.IS_RELEVANT);
    }

    protected Facts initializeFacts(Facts facts) {
        if (globalValues != null) {
            for (Map.Entry<String, String> entry : globalValues.entrySet()) {
                facts.put(RuleConstant.PREFIX.GLOBAL + entry.getKey(), getValue(entry.getValue()));
            }

            facts.asMap().putAll(globalValues);
        }
        selectedRuleName = facts.get(RuleConstant.SELECTED_RULE);
        facts.put("helper", rulesEngineHelper);
        return facts;
    }

    private Rules getRulesFromAsset(String fileName) {
        try {
            if (!ruleMap.containsKey(fileName)) {

                BufferedReader bufferedReader = new BufferedReader(
                        new InputStreamReader(context.getAssets().open(fileName)));
                ruleMap.put(fileName, MVELRuleFactory.createRulesFrom(bufferedReader));
            }
            return ruleMap.get(fileName);
        } catch (IOException e) {
            Timber.e(e, "%s getRulesFromAsset", this.getClass().getCanonicalName());
            return null;
        }
    }

    protected void processDefaultRules(Rules rules, Facts facts) {
        defaultRulesEngine.fire(rules, facts);
    }

    protected Object getValue(String value) {
        String rawValue = value.trim();
        if (isList(rawValue)) {
            return gson.fromJson(rawValue, ArrayList.class);

        } else if ("true".equals(rawValue) || "false".equals(rawValue)) {
            return Boolean.valueOf(rawValue);
        } else {
            try {
                return Integer.valueOf(rawValue);
            } catch (NumberFormatException e) {
                try {
                    return Float.valueOf(rawValue);
                } catch (NumberFormatException e2) {
                    return rawValue;
                }
            }
        }
    }

    private boolean isList(String value) {
        return !value.isEmpty() && value.charAt(0) == '[';
    }

    public String getCalculation(Facts calculationFact, String ruleFilename) {
        //need to clean curValue map as constraint depend on valid values, empties wont do
        Facts facts = initializeFacts(calculationFact);
        facts.put(RuleConstant.CALCULATION, "");
        rules = getRulesFromAsset(RULE_FOLDER_PATH + ruleFilename);
        processDefaultRules(rules, facts);

        return formatCalculationReturnValue(facts.get(RuleConstant.CALCULATION));
    }

    private String formatCalculationReturnValue(Object rawValue) {
        String value = String.valueOf(rawValue).trim();
        if (value.isEmpty()) {
            return "";
        } else if (rawValue instanceof Map) {
            return new JSONObject((Map<String, String>) rawValue).toString();
        } else if (value.contains(".")) {
            try {
                value = String.valueOf((float) Math.round(Float.valueOf(value) * 100) / 100);
            } catch (NumberFormatException e) {
                Timber.e(e, "%s formatCalculationReturnValue", this.getClass().getCanonicalName());
            }
        }
        return value;
    }

    public String getConstraint(Facts constraintFact, String ruleFilename) {
        Facts facts = initializeFacts(constraintFact);
        facts.put(RuleConstant.CONSTRAINT, "0");
        rules = getRulesFromAsset(RULE_FOLDER_PATH + ruleFilename);
        processDefaultRules(rules, facts);

        return formatCalculationReturnValue(facts.get(RuleConstant.CONSTRAINT));
    }

    @Override
    public boolean beforeEvaluate(Rule rule, Facts facts) {
        return selectedRuleName != null && selectedRuleName.equals(rule.getName());
    }

    @Override
    public void afterEvaluate(Rule rule, Facts facts, boolean evaluationResult) {
        //Overriden
    }

    @Override
    public void beforeExecute(Rule rule, Facts facts) {
        //Overriden
    }

    @Override
    public void onSuccess(Rule rule, Facts facts) {
        //Overriden
    }

    @Override
    public void onFailure(Rule rule, Facts facts, Exception exception) {
        //Overriden
    }

    public String getRulesFolderPath() {
        return RULE_FOLDER_PATH;
    }

    public void setRulesFolderPath(String path) {
        RULE_FOLDER_PATH = path;
    }

}
