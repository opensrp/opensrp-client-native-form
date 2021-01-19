package com.vijay.jsonwizard.interactors;

import android.content.Intent;
import android.view.View;

import com.vijay.jsonwizard.BaseTest;
import com.vijay.jsonwizard.R;
import com.vijay.jsonwizard.activities.JsonFormActivity;
import com.vijay.jsonwizard.constants.JsonFormConstants;
import com.vijay.jsonwizard.fragments.JsonFormFragment;
import com.vijay.jsonwizard.interfaces.CommonListener;

import org.junit.After;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.mockito.ArgumentCaptor;
import org.mockito.Captor;
import org.mockito.Mockito;
import org.mockito.MockitoAnnotations;
import org.powermock.reflect.Whitebox;
import org.robolectric.Robolectric;
import org.robolectric.annotation.LooperMode;
import org.robolectric.util.ReflectionHelpers;

import java.util.ArrayList;

import static android.os.Looper.getMainLooper;
import static org.robolectric.Shadows.shadowOf;
import static org.robolectric.annotation.LooperMode.Mode.PAUSED;

/**
 * Created by Vincent Karuri on 09/12/2020
 */
@LooperMode(PAUSED)
public class JsonFormInteractorTest extends BaseTest {

    private JsonFormInteractor jsonFormInteractor;

    @Captor
    private ArgumentCaptor<Intent> intentArgumentCaptor;

    @Before
    public void setUp() {
        MockitoAnnotations.initMocks(this);
        jsonFormInteractor = JsonFormInteractor.getInstance();
    }

    @After
    public void tearDown() {
        ReflectionHelpers.setField(jsonFormInteractor, "INSTANCE", null);
    }

    @Test
    public void testFetchViewsShouldSetCorrectResultForRTE() throws Exception {
        ReflectionHelpers.setField(jsonFormInteractor, "map", null);

        JsonFormActivity activity = Mockito.spy(Robolectric.buildActivity(JsonFormActivity.class, getJsonFormActivityIntent()).create().start().get());
        JsonFormFragment jsonFormFragment = Mockito.mock(JsonFormFragment.class);
        Mockito.doReturn(activity).when(jsonFormFragment).getActivity();
        Mockito.doReturn(activity).when(jsonFormFragment).getJsonApi();

        Whitebox.invokeMethod(jsonFormInteractor, "fetchViews", new ArrayList<View>(), "",
                jsonFormFragment, "", null, Mockito.mock(CommonListener.class), false);

        shadowOf(getMainLooper()).idle();

        Mockito.verify(activity).getString(R.string.form_load_error);
        Mockito.verify(activity).setResult(Mockito.eq(JsonFormConstants.RESULT_CODE.RUNTIME_EXCEPTION_OCCURRED), intentArgumentCaptor.capture());
        Mockito.verify(activity).finish();

        Intent intent = intentArgumentCaptor.getValue();
        Assert.assertTrue(intent.getExtras().getSerializable(JsonFormConstants.RESULT_INTENT.RUNTIME_EXCEPTION) instanceof RuntimeException);

        activity.finish();
    }
}