package com.vijay.jsonwizard.interactors;

import android.view.View;

import com.vijay.jsonwizard.BaseTest;
import com.vijay.jsonwizard.activities.JsonFormActivity;
import com.vijay.jsonwizard.constants.JsonFormConstants;
import com.vijay.jsonwizard.fragments.JsonFormFragment;
import com.vijay.jsonwizard.interfaces.CommonListener;

import org.json.JSONObject;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.mockito.Mockito;
import org.mockito.MockitoAnnotations;
import org.powermock.reflect.Whitebox;
import org.robolectric.util.ReflectionHelpers;

import java.util.ArrayList;

/**
 * Created by Vincent Karuri on 09/12/2020
 */
public class JsonFormInteractorTest extends BaseTest {

    private JsonFormInteractor jsonFormInteractor;

    @Before
    public void setUp() {
        jsonFormInteractor = JsonFormInteractor.getInstance();
        MockitoAnnotations.initMocks(this);
    }

    @After
    public void tearDown() {
        ReflectionHelpers.setField(jsonFormInteractor, "INSTANCE", null);
    }

    @Test
    public void testFetchViewsShouldSetCorrectResultForRTE() throws Exception {
        ReflectionHelpers.setField(jsonFormInteractor, "map", null);

        JsonFormActivity activity = Mockito.mock(JsonFormActivity.class);
        JsonFormFragment jsonFormFragment = Mockito.mock(JsonFormFragment.class);
        Mockito.doReturn(activity).when(jsonFormFragment).getActivity();

        Whitebox.invokeMethod(jsonFormInteractor, "fetchViews", new ArrayList<View>(), "",
                jsonFormFragment, "", new JSONObject(), Mockito.mock(CommonListener.class), false);

        Mockito.verify(activity).setResult(JsonFormConstants.RESULT_CODE.RUNTIME_EXCEPTION_OCCURRED);
        Mockito.verify(activity).finish();
    }
}