package com.vijay.jsonwizard.rules;

import android.content.Context;
import android.util.Log;

import org.jeasy.rules.api.Facts;
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

public class RulesEngineHelper {
    public final String TAG = RulesEngineHelper.class.getCanonicalName();
    private Context context;
    private RulesEngine inferentialRulesEngine;
    private RulesEngine defaultRulesEngine;
    private Map<String, Rules> ruleMap;

    public RulesEngineHelper(Context context) {
        this.context = context;
        this.inferentialRulesEngine = new InferenceRulesEngine();
        RulesEngineParameters parameters = new RulesEngineParameters().skipOnFirstAppliedRule(true);
        this.defaultRulesEngine = new DefaultRulesEngine(parameters);
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

    public boolean getRelevance(RelevanceFact relevanceFact, String rulesFile) {

        Facts facts = new Facts();
        facts.put("pop_hepb", 1);
        facts.put("pop_hepb_screening", "3");
        facts.put("hiv_positive", false);
        facts.put("isRelevant", false);

        Rules rules = getRulesFromAsset("rule/" + rulesFile);
        if (rules == null) {
            return false;
        }

        processDefaultRules(rules, facts);
      /*  for (Object fact: facts) {
           RelevanceFact relFact= (RelevanceFact)fact;
           relFact.isRelevant ;

        }*/

        return facts.get("isRelevant");
    }


}
