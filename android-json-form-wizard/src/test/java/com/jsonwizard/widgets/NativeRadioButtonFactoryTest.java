package com.jsonwizard.widgets;

import com.jsonwizard.BaseTest;
import com.vijay.jsonwizard.widgets.NativeRadioButtonFactory;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.MockitoAnnotations;
import org.powermock.modules.junit4.PowerMockRunner;
import org.powermock.reflect.Whitebox;

@RunWith(PowerMockRunner.class)
public class NativeRadioButtonFactoryTest extends BaseTest {
    @Before
    public void setUp() {
        MockitoAnnotations.initMocks(this);
    }

    @Test
    public void testCreateSpecifyTextWhenTextIsSet() throws Exception {
        String result = Whitebox.invokeMethod(new NativeRadioButtonFactory(), "createSpecifyText", "test");
        Assert.assertEquals("(test)", result);
    }

    @Test
    public void testCreateSpecifyTextWhenTextIsEmpty() throws Exception {
        String result = Whitebox.invokeMethod(new NativeRadioButtonFactory(), "createSpecifyText", "");
        Assert.assertEquals("", result);
    }

    @Test
    public void testCreateSpecifyTextWhenTextIsNull() throws Exception {
        String result = Whitebox.invokeMethod(new NativeRadioButtonFactory(), "createSpecifyText", (Object) null);
        Assert.assertEquals("", result);
    }
}
