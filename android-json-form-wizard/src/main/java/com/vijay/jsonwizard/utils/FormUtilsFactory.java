package com.vijay.jsonwizard.utils;

import android.content.Context;

public class FormUtilsFactory {
    public static FormUtils newInstance(){
        return new FormUtils();
    }

    public static FormUtils noLocaleInstance(){
        return new FormUtils(){
            @Override
            protected String getLocaleFormIdentity(Context context, String formIdentity) {
                return formIdentity;
            }
        };
    }
}
