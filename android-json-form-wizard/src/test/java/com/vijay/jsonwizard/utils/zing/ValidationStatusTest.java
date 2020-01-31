package com.vijay.jsonwizard.utils.zing;

import android.view.View;

import com.vijay.jsonwizard.BaseTest;
import com.vijay.jsonwizard.utils.ValidationStatus;
import com.vijay.jsonwizard.views.JsonFormFragmentView;

import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.MockitoAnnotations;

/**
 * Created by onaio on 04/08/2017.
 */

public class ValidationStatusTest extends BaseTest {
    @Mock
    private View view;

    @Mock
    private JsonFormFragmentView formFragmentView;

    @Before
    public void setUp() {
        MockitoAnnotations.initMocks(this);
    }

    @Test
    public void isValidShouldReturnTrueWhenSetValidIsSetTrue() {

        ValidationStatus validationStatus = new ValidationStatus(true, DEFAULT_ERROR_MSG, formFragmentView, view);
        Assert.assertTrue(validationStatus.isValid());

        validationStatus = new ValidationStatus(true, DEFAULT_ERROR_MSG, formFragmentView, view);
        validationStatus.setIsValid(true);
        Assert.assertTrue(validationStatus.isValid());

    }

    @Test
    public void isValidShouldReturnFalseWhenSetValidIsSetFalse() {

        ValidationStatus validationStatus = new ValidationStatus(false, DEFAULT_ERROR_MSG, formFragmentView, view);
        Assert.assertFalse(validationStatus.isValid());

        validationStatus = new ValidationStatus(true, DEFAULT_ERROR_MSG, formFragmentView, view);
        validationStatus.setIsValid(false);
        Assert.assertFalse(validationStatus.isValid());

    }

    @Test
    public void getErrorMessageShouldReturnNullIfNoMessageSet() {

        ValidationStatus validationStatus = new ValidationStatus(true, null, formFragmentView, view);
        Assert.assertNull(validationStatus.getErrorMessage());

    }

    @Test
    public void getErrorMessageShouldReturnDefaultErrorMessageSetByConstructor() {

        ValidationStatus validationStatus = new ValidationStatus(true, DEFAULT_ERROR_MSG, formFragmentView, view);
        Assert.assertNotNull(validationStatus.getErrorMessage());
        Assert.assertEquals(DEFAULT_ERROR_MSG, validationStatus.getErrorMessage());

    }

    @Test
    public void getErrorMessageShouldReturnCorrectSetErrorMessageBySetter() {

        ValidationStatus validationStatus = new ValidationStatus(true, DEFAULT_ERROR_MSG, formFragmentView, view);
        validationStatus.setErrorMessage(DEFAULT_ERROR_MSG + " NEW");
        Assert.assertNotNull(validationStatus.getErrorMessage());
        Assert.assertEquals(DEFAULT_ERROR_MSG + " NEW", validationStatus.getErrorMessage());

    }

    @Test
    public void testRequestAttention() {
        /*formFragmentView = Mockito.spy(JsonFormFragmentView.class);
        view = Mockito.spy(View.class);*/
        ValidationStatus validationStatus = new ValidationStatus(true, DEFAULT_ERROR_MSG, formFragmentView, view);
        validationStatus.requestAttention();
        Mockito.verify(formFragmentView).scrollToView(view);
    }

    @Test
    public void testRequestAttentionWhenViewAndFromFragmentAreNull() {
        ValidationStatus validationStatus = new ValidationStatus(true, DEFAULT_ERROR_MSG, formFragmentView, view);
        validationStatus.requestAttention();
        /*formFragmentView = Mockito.mock(OnFieldsInvalid.class);
        view = null;*/
        Mockito.verify(formFragmentView, Mockito.times(0)).scrollToView(null);
    }
}

