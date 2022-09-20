package com.vijay.jsonwizard.presenters;

import android.content.Context;
import android.content.res.Resources;
import android.widget.LinearLayout;

import com.vijay.jsonwizard.constants.JsonFormConstants;
import com.vijay.jsonwizard.fragments.JsonWizardFormFragment;
import com.vijay.jsonwizard.interactors.JsonFormInteractor;
import com.vijay.jsonwizard.interfaces.JsonApi;
import com.vijay.jsonwizard.interfaces.OnFieldsInvalid;
import com.vijay.jsonwizard.utils.AppExecutors;
import com.vijay.jsonwizard.utils.ValidationStatus;

import org.json.JSONException;
import org.json.JSONObject;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.MockitoAnnotations;
import org.mockito.stubbing.Answer;
import org.powermock.api.mockito.PowerMockito;
import org.powermock.core.classloader.annotations.PrepareForTest;
import org.powermock.modules.junit4.PowerMockRunner;
import org.powermock.reflect.Whitebox;

import java.lang.ref.WeakReference;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.Executor;

import static org.mockito.ArgumentMatchers.anyInt;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.doReturn;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.powermock.api.mockito.PowerMockito.mockStatic;

@RunWith(PowerMockRunner.class)
@PrepareForTest(JsonWizardFormFragment.class)
public class JsonWizardFormFragmentPresenterTest {

    private JsonWizardFormFragmentPresenter presenter;

    @Mock
    private JsonFormInteractor interactor;

    @Mock
    private JsonWizardFormFragment formFragment;

    @Mock
    private JsonApi jsonApi;

    @Mock
    private JSONObject mJsonObject;

    @Mock
    private JSONObject mStepDetails;

    @Mock
    private Context context;

    @Mock
    private Resources resources;

    @Mock
    private AppExecutors appExecutors;

    Executor executor;

    @Before
    public void setUp() {
        MockitoAnnotations.initMocks(this);
        doReturn(new JSONObject().toString()).when(formFragment).getCurrentJsonState();
        doReturn(mJsonObject).when(jsonApi).getmJSONObject();
        doReturn(jsonApi).when(formFragment).getJsonApi();
        doReturn(mock(OnFieldsInvalid.class)).when(formFragment).getOnFieldsInvalidCallback();
        doReturn(context).when(formFragment).getContext();
        doReturn(resources).when(context).getResources();
        doReturn("string").when(resources).getString(anyInt());
        presenter = new JsonWizardFormFragmentPresenter(formFragment, interactor);
        Whitebox.setInternalState(presenter, "viewRef", new WeakReference<>(formFragment));
        doReturn("step1").when(mStepDetails).optString(anyString());
        Whitebox.setInternalState(presenter, "mStepDetails", mStepDetails);
        executor = Mockito.mock(Executor.class);
        appExecutors = Mockito.mock(AppExecutors.class);
    }

    @Test
    public void testOnNextClickShouldPerformCorrectAction() throws JSONException, InterruptedException {

        Mockito.doAnswer((Answer<Void>) invocation -> {
            Runnable runnable = invocation.getArgument(0);
            runnable.run();
            return null;
        }).when(executor).execute(Mockito.any(Runnable.class));
        Mockito.when(formFragment.getJsonApi().getAppExecutors()).thenReturn(appExecutors);
        Mockito.when(appExecutors.diskIO()).thenReturn(executor);
        Thread.sleep(1000);

        mockStatic(JsonWizardFormFragment.class);
        PowerMockito.when(JsonWizardFormFragment.getFormFragment(anyString())).thenReturn(formFragment);

        presenter = Mockito.spy(presenter);
        // when no incorrectly formatted fields
        mJsonObject.put(JsonFormConstants.VALIDATE_ON_SUBMIT, true);
        presenter.onNextClick(mock(LinearLayout.class));

        verifyMovesToNextStep(1);


        // when form is valid
        mJsonObject.put(JsonFormConstants.VALIDATE_ON_SUBMIT, false);
        presenter.onNextClick(mock(LinearLayout.class));
        verifyMovesToNextStep(2);


        // when form has errors
        Map<String, ValidationStatus> invalidFields = new HashMap<>();
        invalidFields.put("step1#key", mock(ValidationStatus.class));
        Whitebox.setInternalState(presenter, "invalidFields", invalidFields);
        Whitebox.setInternalState(presenter, "mStepName", JsonFormConstants.STEP1);
        presenter.onNextClick(mock(LinearLayout.class));
        verify(formFragment).showSnackBar(eq("string"));
    }

    private void verifyMovesToNextStep(int times) {
        verify(presenter, times(times)).executeRefreshLogicForNextStep();
    }
}
