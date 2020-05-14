package com.vijay.jsonwizard.interfaces;

import android.content.Context;
import android.support.annotation.NonNull;

import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.IOException;

/**
 * Created by Ephraim Kigamba - nek.eam@gmail.com on 29-04-2020.
 */
public interface JsonSubFormAndRulesLoader {

    @NonNull
    JSONObject getSubForm(String formIdentity, String subFormsLocation, Context context, boolean translateSubForm)  throws Exception;

    @NonNull
    BufferedReader getRules(@NonNull Context context, @NonNull String fileName) throws IOException;
}
