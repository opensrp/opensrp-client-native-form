package com.vijay.jsonwizard.widgets;

import com.rengwuxian.materialedittext.validation.METValidator;
import com.vijay.jsonwizard.BaseTest;
import com.vijay.jsonwizard.customviews.NativeEditText;

import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.powermock.reflect.Whitebox;
import org.robolectric.RuntimeEnvironment;

import java.util.List;

public class NativeEditTextFactoryTest extends BaseTest {

    @Mock
    private METValidator validator;

    @Mock
    private NativeEditText nativeEditText;


    @Before
    public void setUp() {
        MockitoAnnotations.initMocks(this);
    }

    @Test
    public void testAddValidator() {
        NativeEditText editText = new NativeEditText(RuntimeEnvironment.application);
        editText.addValidator(validator);
        List<METValidator> result = editText.getValidators();
        Assert.assertNotNull(result);
        Assert.assertEquals(result.size(), 1);
    }

    @Test
    public void testValidate() throws Exception {
        boolean isValid = Whitebox.invokeMethod(nativeEditText, "validate");
        Assert.assertFalse(isValid);
    }
}
