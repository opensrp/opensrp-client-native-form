package com.vijay.jsonwizard.utils;

import android.content.pm.PackageManager;
import android.support.v4.app.Fragment;
import android.support.v4.content.ContextCompat;

import java.util.ArrayList;
import java.util.List;

public class PermissionUtils {

    public static final int CAMERA_PERMISSION_REQUEST_CODE = 15141;
    public static final int PHONE_STATE_PERMISSION_REQUEST_CODE = 14151;


    public static boolean isPermissionGranted(Fragment fragment, String permission, int requestCode) {
        if (ContextCompat.checkSelfPermission(fragment.getActivity(), permission) != PackageManager.PERMISSION_GRANTED) {
            // No explanation needed; request the permission
            fragment.requestPermissions(new String[]{permission}, requestCode);
            return false;
        } else {
            return true;
        }
    }

    public static boolean isPermissionGranted(Fragment fragment, String[] permissions, int requestCode) {
        if (permissions == null || permissions.length == 0) {
            return false;
        }

        List<String> notGranted = new ArrayList<>();
        for (String permission : permissions) {
            if (ContextCompat.checkSelfPermission(fragment.getActivity(), permission) != PackageManager.PERMISSION_GRANTED) {
                notGranted.add(permission);
            }
        }

        if (notGranted.isEmpty()) {
            return true;
        }

        fragment.requestPermissions(notGranted.toArray(new String[notGranted.size()]), requestCode);
        return false;

    }
}
