package com.jsonwizard.widgets;

import android.widget.EditText;

import com.rengwuxian.materialedittext.validation.METValidator;
import com.vijay.jsonwizard.widgets.NativeEditTextFactory;

import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.powermock.api.mockito.PowerMockito;
import org.powermock.core.classloader.annotations.PrepareForTest;
import org.powermock.modules.junit4.PowerMockRunner;
import org.powermock.reflect.Whitebox;

@RunWith(PowerMockRunner.class)
@PrepareForTest(NativeEditTextFactory.class)
public class NativeEditTextFactoryTest {

    @Mock
    private METValidator validator;

    @Mock
    private NativeEditTextFactory editTextFactory;

    @Mock
    private EditText editText;

    @Before
    public void setUp() {
        MockitoAnnotations.initMocks(this);
        PowerMockito.mockStatic(NativeEditTextFactory.class);

    }

    @Test
    public void testAddValidator() {
        editTextFactory.addValidator(validator);
        Assert.assertNotNull(NativeEditTextFactory.validators());
    }
    @Test
    public void testValidate() throws Exception {
        boolean isValid = Whitebox.invokeMethod(editTextFactory, "validate",editText);
        Assert.assertFalse(isValid);
    }
}
