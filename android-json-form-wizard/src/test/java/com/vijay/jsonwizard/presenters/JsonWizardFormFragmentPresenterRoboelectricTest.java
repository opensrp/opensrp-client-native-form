package com.vijay.jsonwizard.presenters;

import android.content.Context;
import android.view.View;

import com.vijay.jsonwizard.BaseTest;
import com.vijay.jsonwizard.R;
import com.vijay.jsonwizard.TestConstants;
import com.vijay.jsonwizard.activities.JsonFormActivity;
import com.vijay.jsonwizard.constants.JsonFormConstants;
import com.vijay.jsonwizard.fragments.JsonWizardFormFragment;
import com.vijay.jsonwizard.interactors.JsonFormInteractor;
import com.vijay.jsonwizard.interfaces.OnFieldsInvalid;

import org.json.JSONException;
import org.json.JSONObject;
import org.junit.Before;
import org.junit.Test;
import org.mockito.Mock;
import org.robolectric.RuntimeEnvironment;

import java.util.UUID;

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

    private Context context = RuntimeEnvironment.application;

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

    @Test
    public void testOnClickShouldOpenSimPrintsRegistration() {
        View view = new View(context);
        view.setTag(R.id.type, JsonFormConstants.FINGER_PRINT);
        view.setTag(R.id.finger_print_option, JsonFormConstants.SIMPRINTS_OPTION_REGISTER);
        view.setTag(R.id.project_id, "test");
        view.setTag(R.id.module_id, "nf");
        view.setTag(R.id.user_id, "jdoe");
        formFragmentPresenter.onClick(view);
        verify(formFragment).startSimprintsRegistration("test", "jdoe", "nf");
    }

    @Test
    public void testOnClickShouldOpenSimPrintsVerification() {
        View view = new View(context);
        view.setTag(R.id.type, JsonFormConstants.FINGER_PRINT);
        view.setTag(R.id.finger_print_option, JsonFormConstants.SIMPRINTS_OPTION_VERIFY);
        view.setTag(R.id.project_id, "test");
        view.setTag(R.id.module_id, "nf");
        view.setTag(R.id.user_id, "jdoe");
        String guid = UUID.randomUUID().toString();
        view.setTag(R.id.guid, guid);
        formFragmentPresenter.onClick(view);
        verify(formFragment).startSimprintsVerification("test", "jdoe", "nf", guid);
    }
}
