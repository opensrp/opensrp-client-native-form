package com.jsonwizard.utils.zing;

import android.content.Context;
import android.test.mock.MockContext;
import android.view.Display;
import android.view.WindowManager;
import com.jsonwizard.BaseTest;
import com.vijay.jsonwizard.utils.ImageUtils;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.MockitoAnnotations;

public class ImageUtilsTest extends BaseTest {
    @Mock
    private Display display;

    @Mock
    private WindowManager windowManager;

    @Mock
    private Context context;

    @Before
    public void setUp() {
        MockitoAnnotations.initMocks(this);
    }

    @Test
    public void testGetDeviceWidth() {
        MockContext mockContext = Mockito.spy(MockContext.class);
        Mockito.doReturn(context).when(mockContext).getApplicationContext();
        Assert.assertNotNull(context);
        Mockito.doReturn(windowManager).when(context).getSystemService(Context.WINDOW_SERVICE);
        Assert.assertNotNull(windowManager);
        Mockito.doReturn(display).when(windowManager).getDefaultDisplay();
        Assert.assertNotNull(display);

        int width = ImageUtils.getDeviceWidth(context);
        Assert.assertEquals(0, width);
        Mockito.verify(display).getWidth();
    }
}
