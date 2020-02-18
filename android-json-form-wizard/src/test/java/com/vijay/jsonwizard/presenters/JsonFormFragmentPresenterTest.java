package com.vijay.jsonwizard.presenters;

import com.vijay.jsonwizard.constants.JsonFormConstants;
import com.vijay.jsonwizard.fragments.JsonFormFragment;
import com.vijay.jsonwizard.interfaces.JsonApi;

import org.json.JSONException;
import org.json.JSONObject;
import org.junit.Before;
import org.junit.Test;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import static org.junit.Assert.assertTrue;
import static org.mockito.Mockito.doReturn;
import static org.mockito.Mockito.mock;

/**
 * Created by Vincent Karuri on 18/02/2020
 */
public class JsonFormFragmentPresenterTest {

    private JsonFormFragmentPresenter jsonFormFragmentPresenter;

    @Mock
    private JsonFormFragment jsonFormFragment;

    @Before
    public void setUp() throws JSONException {
        MockitoAnnotations.initMocks(this);
        setUpJsonFormFragment();
        jsonFormFragmentPresenter = new JsonFormFragmentPresenter(jsonFormFragment);
    }

    @Test
    public void testValidateOnSubmitShouldReturnCorrectValidationStatus() {
        assertTrue(jsonFormFragmentPresenter.validateOnSubmit());
    }

    private void setUpJsonFormFragment() throws JSONException {
        JsonApi jsonApi = mock(JsonApi.class);
        JSONObject jsonObject = new JSONObject();
        jsonObject.put(JsonFormConstants.VALIDATE_ON_SUBMIT, true);
        doReturn(jsonApi).when(jsonFormFragment).getJsonApi();
        doReturn(jsonObject).when(jsonApi).getmJSONObject();
    }
}
