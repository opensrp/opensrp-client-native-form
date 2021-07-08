package com.vijay.jsonwizard.activities;

import com.vijay.jsonwizard.utils.FormUtils;
import com.vijay.jsonwizard.utils.FormUtilsFactory;

public class NoLocaleJsonWizardFormActivity extends JsonWizardFormActivity {
    @Override
    protected FormUtils getFormUtils() {
        return FormUtilsFactory.noLocaleInstance();
    }
}
