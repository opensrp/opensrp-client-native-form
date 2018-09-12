package com.jsonwizard.utils;

import android.graphics.BitmapFactory;

import com.jsonwizard.BaseTest;
import com.vijay.jsonwizard.utils.ImageUtils;

import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.powermock.api.mockito.PowerMockito;
import org.powermock.core.classloader.annotations.PrepareForTest;
import org.powermock.modules.junit4.PowerMockRunner;

@RunWith(PowerMockRunner.class)
public class ImageUtilsTest extends BaseTest {

    @Mock
    private BitmapFactory.Options bitmapFactory;

    @Before
    public void setUp() {
        MockitoAnnotations.initMocks(this);
        bitmapFactory = new BitmapFactory.Options();
    }

    @PrepareForTest({BitmapFactory.class, ImageUtils.class})
    @Test
    public void testCalculateInSampleSize() {
        PowerMockito.mockStatic(BitmapFactory.class);
        PowerMockito.mockStatic(ImageUtils.class);

        int inSampleSize = 0;
        PowerMockito.when(ImageUtils.calculateInSampleSize(bitmapFactory, 23, 23)).thenReturn(inSampleSize);

        int test = ImageUtils.calculateInSampleSize(bitmapFactory, 23, 23);

        Assert.assertEquals(inSampleSize, test);

        PowerMockito.verifyStatic(BitmapFactory.class);

    }
}
