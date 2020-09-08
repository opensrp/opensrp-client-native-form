package com.vijay.jsonwizard.presenters;

import com.vijay.jsonwizard.BaseTest;
import com.vijay.jsonwizard.TestConstants;
import com.vijay.jsonwizard.activities.JsonFormActivity;
import com.vijay.jsonwizard.fragments.JsonWizardFormFragment;
import com.vijay.jsonwizard.interactors.JsonFormInteractor;
import com.vijay.jsonwizard.interfaces.OnFieldsInvalid;

import org.json.JSONException;
import org.json.JSONObject;
import org.junit.Before;
import org.junit.Test;
import org.mockito.Mock;

import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

/**
 * Created by samuelgithengi on 9/8/20.
 */
public class JsonWizardFormFragmentPresenterRoboelectricTest extends BaseTest {

    @Mock
    private JsonWizardFormFragment formFragment;

    @Mock
    private JsonFormActivity formActivity;

    @Mock
    private JsonFormInteractor jsonFormInteractor;

    @Mock
    private OnFieldsInvalid onFieldsInvalid;

    private JsonWizardFormFragmentPresenter formFragmentPresenter;

    @Before
    public void setUp() throws JSONException {
        when(formFragment.getJsonApi()).thenReturn(formActivity);
        when(formFragment.getOnFieldsInvalidCallback()).thenReturn(onFieldsInvalid);
        when(formActivity.getmJSONObject()).thenReturn(new JSONObject(TestConstants.BASIC_FORM));
        formFragmentPresenter = new JsonWizardFormFragmentPresenter(formFragment, jsonFormInteractor);
        formFragmentPresenter.attachView(formFragment);

    }

    @Test
    public void testOnNextClickShouldMoveToNextStep() throws JSONException {
        when(formActivity.nextStep()).thenReturn("step2");
        formFragmentPresenter.onNextClick(null);
        verify(jsonFormInteractor).fetchFormElements("step2", formFragment, formActivity.getmJSONObject().getJSONObject("step2"), null, false);
        verify(formActivity).initializeDependencyMaps();
        verify(formActivity).setNextStepRelevant(false);
        verify(formFragment).skipStepsOnNextPressed("step2");
    }
}
