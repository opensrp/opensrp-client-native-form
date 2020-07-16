package com.vijay.jsonwizard.utils.zing;

import android.content.Context;
import android.content.res.Resources;
import android.graphics.BitmapFactory;

import com.vijay.jsonwizard.BaseTest;
import com.vijay.jsonwizard.utils.ImageUtils;

import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.powermock.reflect.Whitebox;

public class ImageUtilsTest extends BaseTest {
    @Mock
    private Context context;

    @Mock
    private BitmapFactory.Options options;

    @Before
    public void setUp() {
        MockitoAnnotations.initMocks(this);
    }

    @Test
    public void testGetDeviceWidth() {
        int width = ImageUtils.getDeviceWidth(context);
        Assert.assertEquals(Resources.getSystem().getDisplayMetrics().widthPixels, width);
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
