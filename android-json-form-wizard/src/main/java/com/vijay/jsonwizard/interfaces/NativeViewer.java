package com.vijay.jsonwizard.interfaces;

import android.app.FragmentManager;
import android.content.Context;
import android.view.View;

/**
 * Interface for all views  (Activity / Fragment / Embedded View)
 * Functions implemented by the form views to render forms
 */
public interface NativeViewer {
    Context getContext();

    void scrollToView(View view);

    boolean nextClick();

    boolean save(boolean res);

    void backClick();

    JsonApi getJsonApi();

    FragmentManager getActivityFragmentManager();
}
