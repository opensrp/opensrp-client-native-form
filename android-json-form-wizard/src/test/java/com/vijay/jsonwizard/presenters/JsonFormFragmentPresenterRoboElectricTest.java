package com.vijay.jsonwizard.presenters;

import android.content.Context;
import android.os.Bundle;
import android.view.View;
import android.widget.TextView;

import com.vijay.jsonwizard.BaseTest;
import com.vijay.jsonwizard.TestConstants;
import com.vijay.jsonwizard.activities.JsonFormActivity;
import com.vijay.jsonwizard.constants.JsonFormConstants;
import com.vijay.jsonwizard.fragments.JsonFormFragment;
import com.vijay.jsonwizard.interactors.JsonFormInteractor;
import com.vijay.jsonwizard.interfaces.CommonListener;

import org.json.JSONException;
import org.json.JSONObject;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.mockito.ArgumentCaptor;
import org.mockito.Captor;
import org.mockito.Mock;
import org.mockito.junit.MockitoJUnit;
import org.mockito.junit.MockitoRule;
import org.powermock.reflect.Whitebox;
import org.robolectric.RuntimeEnvironment;

import java.lang.ref.WeakReference;
import java.util.Collections;
import java.util.List;

import static com.vijay.jsonwizard.constants.JsonFormConstants.STEP1;
import static org.junit.Assert.assertEquals;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyBoolean;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.ArgumentMatchers.isNull;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

/**
 * Created by samuelgithengi on 3/3/20.
 */
public class JsonFormFragmentPresenterRoboElectricTest extends BaseTest {

    @Rule
    public MockitoRule rule = MockitoJUnit.rule();

    @Mock
    private JsonFormFragment formFragment;

    @Mock
    private JsonFormInteractor jsonFormInteractor;

    private JsonFormFragmentPresenter presenter;

    @Mock
    private JsonFormActivity jsonFormActivity;

    @Captor
    private ArgumentCaptor<JSONObject> jsonArgumentCaptor;

    private JSONObject jsonForm;

    private JSONObject mStepDetails;

    private View textView;

    private Context context = RuntimeEnvironment.application;


    @Before
    public void setUp() throws JSONException {
        when(formFragment.getJsonApi()).thenReturn(jsonFormActivity);
        presenter = new JsonFormFragmentPresenter(formFragment, jsonFormInteractor);
        Whitebox.setInternalState(presenter, "viewRef", new WeakReference<>(formFragment));
        textView = new TextView(context);
        jsonForm = new JSONObject(TestConstants.PAOT_TEST_FORM);
        mStepDetails = jsonForm.getJSONObject(STEP1);
    }

    @Test
    public void testAddFormElements() {
        Bundle bundle = new Bundle();
        bundle.putString(JsonFormConstants.JSON_FORM_KEY.STEPNAME, STEP1);
        when(formFragment.getArguments()).thenReturn(bundle);
        when(formFragment.getStep(STEP1)).thenReturn(mStepDetails);
        List<View> views = Collections.singletonList(textView);
        when(jsonFormInteractor.fetchFormElements(anyString(), any(JsonFormFragment.class), any(JSONObject.class), isNull(CommonListener.class), anyBoolean())).thenReturn(views);
        presenter.addFormElements();
        verify(jsonFormInteractor).fetchFormElements(eq(STEP1), eq(formFragment), jsonArgumentCaptor.capture(), isNull(CommonListener.class), eq(false));
        assertEquals(mStepDetails.toString(), jsonArgumentCaptor.getValue().toString());
        verify(formFragment).addFormElements(views);
    }

    @Test
    public void testSetUpToolBarForBottomNavigation() throws JSONException {
        Whitebox.setInternalState(presenter, "mStepDetails", mStepDetails);
        mStepDetails.put("bottom_navigation", true);
        presenter.setUpToolBar();
        verify(formFragment).updateVisibilityOfNextAndSave(false, false);

    }

    @Test
    public void testSetUpToolBarForStep2AndMore() {
        Whitebox.setInternalState(presenter, "mStepDetails", mStepDetails);
        Whitebox.setInternalState(presenter, "mStepName", "step2");
        presenter.setUpToolBar();
        verify(formFragment).setUpBackButton();

    }


    @Test
    public void testSetUpToolBarIfStepHasNext() throws JSONException {
        Whitebox.setInternalState(presenter, "mStepDetails", mStepDetails);
        Whitebox.setInternalState(presenter, "mStepName", "step2");
        mStepDetails.put("next", true);
        presenter.setUpToolBar();
        verify(formFragment).updateVisibilityOfNextAndSave(true, false);

    }


    @Test
    public void testSetUpToolBarForLastLastStep() {
        Whitebox.setInternalState(presenter, "mStepDetails", mStepDetails);
        Whitebox.setInternalState(presenter, "mStepName", "step2");
        presenter.setUpToolBar();
        verify(formFragment).updateVisibilityOfNextAndSave(false, true);

    }

    @Test
    public void testOnBackClick() {
        presenter.onBackClick();
        verify(formFragment).hideKeyBoard();
        verify(formFragment).backClick();

    }

}
