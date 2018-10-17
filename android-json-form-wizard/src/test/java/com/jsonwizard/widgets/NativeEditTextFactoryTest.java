package com.jsonwizard.widgets;

import com.rengwuxian.materialedittext.validation.METValidator;
import com.vijay.jsonwizard.customviews.NativeEditText;
import com.vijay.jsonwizard.widgets.NativeEditTextFactory;

import org.junit.Assert;
import org.junit.Before;
import org.junit.Ignore;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.powermock.api.mockito.PowerMockito;
import org.powermock.core.classloader.annotations.PrepareForTest;
import org.powermock.modules.junit4.PowerMockRunner;
import org.powermock.reflect.Whitebox;

import java.util.List;

@RunWith(PowerMockRunner.class)
@PrepareForTest(NativeEditTextFactory.class)
public class NativeEditTextFactoryTest {

    @Mock
    private METValidator validator;

    @Mock
    private NativeEditText nativeEditText;


    @Before
    public void setUp() {
        MockitoAnnotations.initMocks(this);
        PowerMockito.mockStatic(NativeEditTextFactory.class);

    }

    @Test
    @Ignore
    public void testAddValidator() throws Exception {
        //nativeEditText.addValidator(validator);
        List<METValidator> result = Whitebox.invokeMethod(nativeEditText, "addValidator", validator);
        Assert.assertEquals(result.size(), 0);
    }

    @Test
    public void testValidate() throws Exception {
        boolean isValid = Whitebox.invokeMethod(nativeEditText, "validate");
        Assert.assertFalse(isValid);
    }
}
