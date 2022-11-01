package org.smartregister.nativeform;

import android.app.Application;

import io.sentry.android.core.SentryAndroid;

public class MainApplication extends Application {

    @Override
    public void onCreate() {
        super.onCreate();

        //noinspection ConstantConditions
        if (!BuildConfig.SENTRY_DSN.trim().isEmpty()) {
            SentryAndroid.init(this, options -> {
                options.setEnvironment("opensrp-native-form-sample");
                options.setDsn(BuildConfig.SENTRY_DSN.trim());
            });
        }

    }
}
