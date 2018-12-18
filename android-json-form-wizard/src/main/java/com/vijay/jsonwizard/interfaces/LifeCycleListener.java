package com.vijay.jsonwizard.interfaces;

import android.os.Bundle;

/**
 * Created by samuelgithengi on 12/18/18.
 */
public interface LifeCycleListener {

    void onCreate(Bundle savedInstanceState);

    void onStart();

    void onResume();

    void onPause();

    void onStop();

    void onSaveInstanceState(Bundle outState);

    void onLowMemory();

    void onDestroy();

}
