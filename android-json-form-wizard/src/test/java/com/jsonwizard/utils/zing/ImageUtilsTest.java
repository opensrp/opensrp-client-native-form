package com.jsonwizard.utils.zing;

import android.app.Application;
import android.content.Context;
import android.graphics.BitmapFactory;
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
import org.powermock.reflect.Whitebox;

public class ImageUtilsTest extends BaseTest {
    @Mock
    private WindowManager windowManager;

    @Mock
    private Context context;

    @Mock
    private Display display;

    @Mock
    private BitmapFactory.Options options;

    @Before
    public void setUp() {
        MockitoAnnotations.initMocks(this);
    }

    @Test
    public void testGetDeviceWidth() {
        Application application = Mockito.spy(Application.class);
        Mockito.doReturn(context).when(application).getApplicationContext();
        Mockito.doReturn(windowManager).when(context).getSystemService(Context.WINDOW_SERVICE);
        Assert.assertNotNull(windowManager);
        Mockito.doReturn(display).when(windowManager).getDefaultDisplay();
        Assert.assertNotNull(display);

        int width = ImageUtils.getDeviceWidth(context);
        Assert.assertEquals(0, width);
        Mockito.verify(display).getWidth();
    }

    @Test
    public void testCalculateInSampleSize() {
        options = new BitmapFactory.Options();
        Assert.assertNotNull(options);

        Whitebox.setInternalState(options, "outHeight", 50);
        Whitebox.setInternalState(options, "outWidth", 24);

        int inSampleSize = ImageUtils.calculateInSampleSize(options, 3, 5);
        Assert.assertEquals(4, inSampleSize);
    }

    @Test
    public void testCalculateInSampleSizeWithSmallerHeight() {
        options = new BitmapFactory.Options();
        Assert.assertNotNull(options);

        Whitebox.setInternalState(options, "outHeight", 1);
        Whitebox.setInternalState(options, "outWidth", 1);

        int inSampleSize = ImageUtils.calculateInSampleSize(options, 3, 5);
        Assert.assertEquals(1, inSampleSize);
    }

    @Test
    public void testCalculateInSampleSizeWithSmallHalfDimension() {
        options = new BitmapFactory.Options();
        Assert.assertNotNull(options);

        Whitebox.setInternalState(options, "outHeight", 4);
        Whitebox.setInternalState(options, "outWidth", 4);

        int inSampleSize = ImageUtils.calculateInSampleSize(options, 3, 5);
        Assert.assertEquals(1, inSampleSize);
    }
}
