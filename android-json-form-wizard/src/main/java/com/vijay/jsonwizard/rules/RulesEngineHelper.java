package com.vijay.jsonwizard.rules;

import android.content.Context;
import android.util.Log;

import org.jeasy.rules.api.Facts;
import org.jeasy.rules.api.Rule;
import org.jeasy.rules.api.RuleListener;
import org.jeasy.rules.api.Rules;
import org.jeasy.rules.api.RulesEngine;
import org.jeasy.rules.core.DefaultRulesEngine;
import org.jeasy.rules.core.InferenceRulesEngine;
import org.jeasy.rules.core.RulesEngineParameters;
import org.jeasy.rules.mvel.MVELRuleFactory;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.HashMap;
import java.util.Map;

public class RulesEngineHelper implements RuleListener {
    public final String TAG = RulesEngineHelper.class.getCanonicalName();
    private Context context;
    private RulesEngine inferentialRulesEngine;
    private RulesEngine defaultRulesEngine;
    private Map<String, Rules> ruleMap;
    private final String RULE_FOLDER_PATH = "rule/";
    private Rules rules;
    private String ruleName;

    public RulesEngineHelper(Context context) {
        this.context = context;
        this.inferentialRulesEngine = new InferenceRulesEngine();
        RulesEngineParameters parameters = new RulesEngineParameters().skipOnFirstAppliedRule(true);
        this.defaultRulesEngine = new DefaultRulesEngine(parameters);
        ((DefaultRulesEngine) this.defaultRulesEngine).registerRuleListener(this);
        this.ruleMap = new HashMap<>();

    }

    private Rules getRulesFromAsset(String fileName) {
        try {
            if (!ruleMap.containsKey(fileName)) {

                BufferedReader bufferedReader = new BufferedReader(new InputStreamReader(context.getAssets().open(fileName)));
                ruleMap.put(fileName, MVELRuleFactory.createRulesFrom(bufferedReader));
            }
            return ruleMap.get(fileName);
        } catch (IOException e) {
            Log.e(TAG, e.getMessage(), e);
            return null;
        }
    }

    protected void processInferentialRules(Rules rules, Facts facts) {

        inferentialRulesEngine.fire(rules, facts);
    }

    protected void processDefaultRules(Rules rules, Facts facts) {

        defaultRulesEngine.fire(rules, facts);
    }

    public boolean getRelevance(Map<String, String> relevanceFact, String ruleFilename, String ruleOfChoice) {
        ruleName = ruleOfChoice;

        Facts facts = new Facts();

        for (Map.Entry<String, String> entry : relevanceFact.entrySet()) {

            facts.put(entry.getKey(), entry.getValue());
        }

        facts.put(RuleConstant.IS_RELEVANT, false);


        rules = getRulesFromAsset(RULE_FOLDER_PATH + ruleFilename);

        processDefaultRules(rules, facts);

        return facts.get(RuleConstant.IS_RELEVANT);
    }

    @Override
    public boolean beforeEvaluate(Rule rule, Facts facts) {
        return ruleName.equals(rule.getName());
    }

    @Override
    public void afterEvaluate(Rule rule, Facts facts, boolean evaluationResult) {

    }

    @Override
    public void beforeExecute(Rule rule, Facts facts) {

    }

    @Override
    public void onSuccess(Rule rule, Facts facts) {

    }

    @Override
    public void onFailure(Rule rule, Facts facts, Exception exception) {

    }

}
