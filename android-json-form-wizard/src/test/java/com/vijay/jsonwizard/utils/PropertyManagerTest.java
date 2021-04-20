package com.vijay.jsonwizard.utils;

import android.Manifest;
import android.content.Context;
import android.content.pm.PackageManager;
import android.telephony.TelephonyManager;

import com.vijay.jsonwizard.BaseTest;
import com.vijay.jsonwizard.activities.JsonFormActivity;
import com.vijay.jsonwizard.interfaces.OnActivityRequestPermissionResultListener;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.mockito.MockitoAnnotations;
import org.robolectric.Robolectric;
import org.robolectric.util.ReflectionHelpers;

import java.util.HashMap;

import static com.vijay.jsonwizard.utils.PropertyManager.DEVICE_ID_PROPERTY;
import static com.vijay.jsonwizard.utils.PropertyManager.PHONE_NUMBER_PROPERTY;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;
import static org.mockito.Mockito.doReturn;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.spy;

public class PropertyManagerTest extends BaseTest {

    private JsonFormActivity jsonFormActivity;

    private PropertyManager propertyManager;

    @Before
    public void setUp() {
        MockitoAnnotations.initMocks(this);
        jsonFormActivity = spy(Robolectric.buildActivity(JsonFormActivity.class, getJsonFormActivityIntent()).create().get());
        propertyManager = ReflectionHelpers.getField(jsonFormActivity, "propertyManager");
    }

    @Test
    public void testHandleOnRequestPermissionResultsShouldPopulatePropertiesIfPermissionIsGranted() {
        ReflectionHelpers.setField(propertyManager, "mContext", jsonFormActivity);
        doReturn(createMockTelephonyManager()).when(jsonFormActivity).getSystemService(Context.TELEPHONY_SERVICE);
        HashMap<Integer, OnActivityRequestPermissionResultListener> resultListenerHashMap = ReflectionHelpers.getField(jsonFormActivity, "onActivityRequestPermissionResultListeners");
        resultListenerHashMap.get(PermissionUtils.PHONE_STATE_PERMISSION)
                .onRequestPermissionResult(PermissionUtils.PHONE_STATE_PERMISSION, new String[]{Manifest.permission.READ_PHONE_STATE}, new int[]{PackageManager.PERMISSION_GRANTED});

        HashMap<String, String> mProperties = ReflectionHelpers.getField(propertyManager, "mProperties");
        assertEquals(4, mProperties.size());
        assertEquals("mda-2323", mProperties.get(DEVICE_ID_PROPERTY));
        assertEquals("tel-123", mProperties.get(PHONE_NUMBER_PROPERTY));
    }

    @Test
    public void testHandleOnRequestPermissionResultsShouldNotPopulatePropertiesIfPermissionIsDenied() {
        ReflectionHelpers.setField(propertyManager, "mContext", jsonFormActivity);
        HashMap<Integer, OnActivityRequestPermissionResultListener> resultListenerHashMap = ReflectionHelpers.getField(jsonFormActivity, "onActivityRequestPermissionResultListeners");
        resultListenerHashMap.get(PermissionUtils.PHONE_STATE_PERMISSION)
                .onRequestPermissionResult(PermissionUtils.PHONE_STATE_PERMISSION, new String[]{Manifest.permission.READ_PHONE_STATE}, new int[]{PackageManager.PERMISSION_DENIED});

        HashMap<String, String> mProperties = ReflectionHelpers.getField(propertyManager, "mProperties");
        assertTrue(mProperties.isEmpty());
    }

    private TelephonyManager createMockTelephonyManager() {
        TelephonyManager mockTelephonyManager = mock(TelephonyManager.class);
        doReturn(TelephonyManager.PHONE_TYPE_SIP).when(mockTelephonyManager).getPhoneType();
        doReturn("mda-2323").when(mockTelephonyManager).getDeviceId();
        doReturn("sub-2123").when(mockTelephonyManager).getSubscriberId();
        doReturn("sim-123").when(mockTelephonyManager).getSimSerialNumber();
        doReturn("tel-123").when(mockTelephonyManager).getLine1Number();
        return mockTelephonyManager;
    }

    @After
    public void tearDown() {
        if (jsonFormActivity != null)
            jsonFormActivity.finish();
    }
}