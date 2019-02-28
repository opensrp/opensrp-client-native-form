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

    public static final String TAG = JsonWizardFormFragmentPresenter.class.getName();

    public JsonWizardFormFragmentPresenter(JsonFormFragment formFragment, JsonFormInteractor jsonFormInteractor) {
        super(formFragment, jsonFormInteractor);
    }

    @Override
    public void setUpToolBar() {
        super.setUpToolBar();
    }

    @Override
    public void onNextClick(LinearLayout mainView) {
        validateAndWriteValues();
        boolean validateOnSubmit = validateOnSubmit();
        if (validateOnSubmit && getIncorrectlyFormattedFields().isEmpty()) {
            getIncorrectlyFormattedFields().clear();
            moveToNextWizardStep();
        } else if (isFormValid()) {
            moveToNextWizardStep();
        } else {
            getView().showSnackBar(getView().getContext().getResources()
                    .getString(R.string.json_form_error_msg, getInvalidFields().size()));
        }
    }
    private void moveToNextWizardStep() {
        JsonFormFragment next = JsonWizardFormFragment.getFormFragment(mStepDetails.optString(JsonFormConstants.NEXT));
        getView().hideKeyBoard();
        getView().transactThis(next);
    }
}
