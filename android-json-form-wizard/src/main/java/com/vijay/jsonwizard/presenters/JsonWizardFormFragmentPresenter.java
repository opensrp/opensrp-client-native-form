package com.vijay.jsonwizard.presenters;

import android.widget.LinearLayout;

import com.vijay.jsonwizard.R;
import com.vijay.jsonwizard.constants.JsonFormConstants;
import com.vijay.jsonwizard.fragments.JsonFormFragment;
import com.vijay.jsonwizard.fragments.JsonWizardFormFragment;
import com.vijay.jsonwizard.interactors.JsonFormInteractor;

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
            return moveToNextWizardStep();
        } else if (isFormValid()) {
            return moveToNextWizardStep();
        } else {
            getView().showSnackBar(getView().getContext().getResources()
                    .getString(R.string.json_form_on_next_error_msg));
        }
        return false;
    }

    protected boolean moveToNextWizardStep() {
        if (!"".equals(mStepDetails.optString(JsonFormConstants.NEXT))) {
            JsonFormFragment next = JsonWizardFormFragment.getFormFragment(mStepDetails.optString(JsonFormConstants.NEXT));
            getView().hideKeyBoard();
            getView().transactThis(next);
        }
        return false;
    }

}
