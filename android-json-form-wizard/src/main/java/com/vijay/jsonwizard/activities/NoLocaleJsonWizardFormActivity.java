package com.vijay.jsonwizard.activities;

import com.vijay.jsonwizard.utils.FormUtils;

public class NoLocaleJsonWizardFormActivity extends JsonWizardFormActivity {
    @Override
    protected FormUtils getFormUtils() {
        return FormUtils.newInstanceWithNoLocale();
    }
}
