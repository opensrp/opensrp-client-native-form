package com.vijay.jsonwizard.rules;

import org.jeasy.rules.support.RuleDefinition;
import org.jeasy.rules.support.reader.YamlRuleDefinitionReader;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

/**
 * Created by Ephraim Kigamba - nek.eam@gmail.com on 22-11-2022.
 */
public class YamlRuleDefinitionReaderExt extends YamlRuleDefinitionReader {


    @Override
    protected RuleDefinition createRuleDefinition(Map<String, Object> map) {
        RuleDefinition ruleDefinition = super.createRuleDefinition(map);

        List<String> actionList = ruleDefinition.getActions();
        List<String> newActionList = new ArrayList<>();

        for (int i = 0; i < actionList.size(); i++) {
            newActionList.add("facts." + actionList.get(i));
        }

        actionList.clear();
        actionList.addAll(newActionList);

        return ruleDefinition;
    }
}
