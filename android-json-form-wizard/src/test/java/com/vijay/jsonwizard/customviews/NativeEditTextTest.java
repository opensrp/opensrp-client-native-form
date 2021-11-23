package com.vijay.jsonwizard.customviews;

import android.content.Context;
import android.text.Editable;

import com.rengwuxian.materialedittext.validation.METValidator;
import com.vijay.jsonwizard.BaseTest;
import com.vijay.jsonwizard.activities.JsonFormActivity;

import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.mockito.Mock;
import org.mockito.invocation.InvocationOnMock;
import org.mockito.stubbing.Answer;
import org.powermock.reflect.Whitebox;
import org.robolectric.Robolectric;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyBoolean;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.spy;
import static org.mockito.Mockito.when;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;

public class NativeEditTextTest extends BaseTest {

    private NativeEditText nativeEditText;

    @Mock
    METValidator testValidator;

    @Before
    public void setUp() throws Exception {
        Context activity = Robolectric.buildActivity(JsonFormActivity.class, getJsonFormActivityIntent()).create().get();
        nativeEditText = spy(new NativeEditText(activity));
        when(nativeEditText.getText()).thenReturn(
                new Editable.Factory().newEditable("test"));

        when(testValidator.isValid(any(CharSequence.class), anyBoolean())).thenAnswer(new Answer<Boolean>() {
            @Override
            public Boolean answer(InvocationOnMock invocation) throws Throwable {
                String test = invocation.getArgument(0).toString();
                return test.equalsIgnoreCase("test");
            }
        });
    }

    @Test
    public void testIsValid() {
        String regex = ".*";
        boolean isValid = nativeEditText.isValid(regex);
        Assert.assertTrue(isValid);
    }

    @Test
    public void testInitWatchers() throws Exception {
        Whitebox.invokeMethod(nativeEditText, "initTextWatcher");
        nativeEditText.setText("new text");
        when(nativeEditText.getText()).thenCallRealMethod();
        Assert.assertEquals("new text", nativeEditText.getText().toString());
    }

    @Test
    public void testValidateWith() {
        METValidator isNotEmptyValidator = mock(METValidator.class);
        when(isNotEmptyValidator.isValid(any(CharSequence.class), anyBoolean())).thenAnswer(new Answer<Boolean>() {
            @Override
            public Boolean answer(InvocationOnMock invocation) {
                String text = invocation.getArgument(0).toString();
                return text.length() > 0;
            }
        });
        boolean isValid = nativeEditText.validateWith(isNotEmptyValidator);
        verify(isNotEmptyValidator).isValid(eq(nativeEditText.getText()), anyBoolean());
        Assert.assertTrue(isValid);
    }

    @Test
    public void testClearValidators() {
        Assert.assertFalse(nativeEditText.hasValidators());
        nativeEditText.addValidator(testValidator);
        Assert.assertTrue(nativeEditText.hasValidators());

        nativeEditText.clearValidators();
        Assert.assertFalse(nativeEditText.hasValidators());
    }


    @Test
    public void testIsFilledValidly() {
        nativeEditText.addValidator(testValidator);
        boolean isValid = nativeEditText.isFilledValidly();
        Assert.assertTrue(isValid);
    }

    @Test
    public void testValidate(){
        nativeEditText.addValidator(testValidator);
        boolean isValid = nativeEditText.validate();
        Assert.assertTrue(isValid);
    }

    @Test
    public void testIsEmpty() {
        Assert.assertEquals(nativeEditText.getText().length() == 0, nativeEditText.isEmpty());
    }


    @Test
    public void testAddValidator() {
        Assert.assertFalse(nativeEditText.hasValidators());
        nativeEditText.addValidator(testValidator);
        Assert.assertTrue(nativeEditText.hasValidators());
        Assert.assertNotNull(nativeEditText.getValidators());
        Assert.assertTrue(nativeEditText.getValidators().contains(testValidator));
    }
}