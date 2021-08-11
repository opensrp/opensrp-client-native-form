package com.vijay.jsonwizard.delegates;

import androidx.annotation.NonNull;
import androidx.fragment.app.FragmentManager;

import com.vijay.jsonwizard.constants.JsonFormConstants;
import com.vijay.jsonwizard.fragments.JsonWizardFormFragment;

public class JsonWizardFormDelegate {

    public void initializeFormFragmentCore(@NonNull FragmentManager supportFragmentManager){
        JsonWizardFormFragment jsonWizardFormFragment = JsonWizardFormFragment.getFormFragment(JsonFormConstants.FIRST_STEP_NAME);
        supportFragmentManager.beginTransaction()
                .add(com.vijay.jsonwizard.R.id.container, jsonWizardFormFragment)
                .commit();
    }

}
