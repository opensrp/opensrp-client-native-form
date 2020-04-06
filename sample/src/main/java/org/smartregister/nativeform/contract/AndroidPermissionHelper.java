package org.smartregister.nativeform.contract;

import java.util.List;

public interface AndroidPermissionHelper {

    interface Granter {

        boolean checkPermissions(List<String> requiredPermissions);

        void checkOrRequestPermissions(List<String> requiredPermissions, Requester requester);
    }

    interface Requester {

        void onHasPermissions();

        void onMissingPermissions(List<String> missingPermissions);

        /**
         * If the app did not have permissions before
         */
        void onPermissionsGranted();
    }

}
