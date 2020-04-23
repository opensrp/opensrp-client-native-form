package com.vijay.jsonwizard.presenters;

import android.view.View;
import android.widget.LinearLayout;

import com.vijay.jsonwizard.R;
import com.vijay.jsonwizard.constants.JsonFormConstants;
import com.vijay.jsonwizard.fragments.JsonFormFragment;
import com.vijay.jsonwizard.fragments.JsonWizardFormFragment;
import com.vijay.jsonwizard.interactors.JsonFormInteractor;

import org.apache.commons.lang3.StringUtils;

import java.util.List;

import timber.log.Timber;

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
        executeRefreshLogicForNextStep();
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


    public void executeRefreshLogicForNextStep() {
        final String stepName = mStepDetails.optString(JsonFormConstants.NEXT);
        if (StringUtils.isNotBlank(stepName)) {
            final List<View> views = getmJsonFormInteractor().fetchFormElements(stepName, getFormFragment(), getFormFragment().getJsonApi().getmJSONObject().optJSONObject(stepName), getView().getCommonListener(), false);
            getFormFragment().getTempViews().clear();
            getFormFragment().getTempViews().addAll(views);
            getFormFragment().getJsonApi().initializeDependencyMaps();

            getFormFragment().getJsonApi().invokeRefreshLogic(null, false, null, null, stepName);
            getFormFragment().getJsonApi().refreshHiddenViews(false);
            getFormFragment().getJsonApi().resetFocus();
        }
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
