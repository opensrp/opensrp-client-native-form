package com.vijay.jsonwizard.shadow;

import android.content.Context;
import android.support.annotation.NonNull;
import android.support.v4.content.ContextCompat;

import org.robolectric.annotation.Implementation;
import org.robolectric.annotation.Implements;

/**
 * Created by samuelgithengi on 3/17/20.
 */
@Implements(ContextCompat.class)
public class ShadowContextCompat {

    private static int permissionStatus = 1;

    @Implementation
    public static int checkSelfPermission(@NonNull Context context, @NonNull String permission){
        return permissionStatus;
    }

    public static void setPermissionStatus(int permissionStatus) {
        ShadowContextCompat.permissionStatus = permissionStatus;
    }
}
