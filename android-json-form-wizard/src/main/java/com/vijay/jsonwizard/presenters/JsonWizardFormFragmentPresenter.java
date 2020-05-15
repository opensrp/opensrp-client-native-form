package com.vijay.jsonwizard.presenters;

import android.widget.LinearLayout;

import com.vijay.jsonwizard.R;
import com.vijay.jsonwizard.fragments.JsonFormFragment;
import com.vijay.jsonwizard.fragments.JsonWizardFormFragment;
import com.vijay.jsonwizard.interactors.JsonFormInteractor;

import org.apache.commons.lang3.StringUtils;

/**
 * Created by keyman on 04/12/18.
 */
public class JsonWizardFormFragmentPresenter extends JsonFormFragmentPresenter {
    public JsonWizardFormFragmentPresenter(JsonFormFragment formFragment, JsonFormInteractor jsonFormInteractor) {
        super(formFragment, jsonFormInteractor);
    }

    @Override
    public void setUpToolBar() {
        super.setUpToolBar();
    }

    @Override
    public boolean onNextClick(LinearLayout mainView) {
        validateAndWriteValues();
        checkAndStopCountdownAlarm();
        boolean validateOnSubmit = validateOnSubmit();
        if (validateOnSubmit && getIncorrectlyFormattedFields().isEmpty()) {
            executeRefreshLogicForNextStep();
            return moveToNextWizardStep();
        } else if (isFormValid()) {
            return moveToNextWizardStep();
        } else {
            getView().showSnackBar(getView().getContext().getResources()
                    .getString(R.string.json_form_on_next_error_msg));
        }
        return false;
    }


    public void executeRefreshLogicForNextStep() {

        final String nextStep = ((JsonWizardFormFragment) getFormFragment()).getNextStep();
        if (StringUtils.isNotBlank(nextStep)) {
            getmJsonFormInteractor().fetchFormElements(nextStep, getFormFragment(), getFormFragment().getJsonApi().getmJSONObject().optJSONObject(nextStep), getView().getCommonListener(), false);
            getFormFragment().getJsonApi().initializeDependencyMaps();
            getFormFragment().getJsonApi().invokeRefreshLogic(null, false, null, null, nextStep);
            getFormFragment().setShouldSkipStep(true);
            ((JsonWizardFormFragment) getFormFragment()).skipStepsOnNextPressed(nextStep);
        }

    }

    protected boolean moveToNextWizardStep() {
        if (!"".equals(((JsonWizardFormFragment) getFormFragment()).getNextStep())) {
            JsonFormFragment next = JsonWizardFormFragment.getFormFragment(((JsonWizardFormFragment) getFormFragment()).getNextStep());
            getView().hideKeyBoard();
            getView().transactThis(next);
        }
        return false;
    }

}
