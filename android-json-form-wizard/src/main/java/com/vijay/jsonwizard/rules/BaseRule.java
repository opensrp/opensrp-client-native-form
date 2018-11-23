package com.vijay.jsonwizard.rules;

import org.jeasy.rules.api.Facts;
import org.jeasy.rules.api.Rule;

/**
 * Created by ndegwamartin on 23/11/2018.
 */
public abstract class BaseRule implements Rule {
    @Override
    public String getName() {
        return null;
    }

    @Override
    public String getDescription() {
        return null;
    }

    @Override
    public int getPriority() {
        return 0;
    }

    @Override
    public boolean evaluate(Facts facts) {
        return false;
    }

    @Override
    public void execute(Facts facts) throws Exception {

    }

    @Override
    public int compareTo(Rule rule) {
        return 0;
    }
}
