package com.jsonwizard.utils.zing;

import android.view.View;

import com.jsonwizard.BaseTest;
import com.vijay.jsonwizard.utils.ValidationStatus;
import com.vijay.jsonwizard.views.JsonFormFragmentView;

import org.junit.Assert;
import org.junit.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;

/**
 * Created by onaio on 04/08/2017.
 */

public class ValidationStatusTest extends BaseTest {

    @InjectMocks
    private ValidationStatus activity;

    @Mock
    private JsonFormFragmentView formFragmentView;

    @Mock
    protected View view;

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
}

