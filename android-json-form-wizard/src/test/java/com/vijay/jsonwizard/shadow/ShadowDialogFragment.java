package com.vijay.jsonwizard.shadow;

import android.app.DialogFragment;
import android.app.FragmentTransaction;

import org.robolectric.annotation.Implementation;
import org.robolectric.annotation.Implements;

/**
 * Created by samuelgithengi on 9/8/20.
 */
@Implements(DialogFragment.class)
public class ShadowDialogFragment {

    @Implementation
    public int show(FragmentTransaction transaction, String tag) {
        return -1;
    }
}
