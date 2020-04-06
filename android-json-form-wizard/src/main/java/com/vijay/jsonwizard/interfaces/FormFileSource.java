package com.vijay.jsonwizard.interfaces;

import android.content.Context;

import org.jeasy.rules.api.Rules;
import org.json.JSONObject;

import java.io.IOException;

public interface FormFileSource {

    Rules getRulesFromFile(Context context, String fileName) throws Exception;

    JSONObject getFormFromFile(Context context, String fileName);

    /***
     *
     * @param context
     * @param fileName
     * @return
     */
    byte[] getFileContent(Context context, String fileName);
}
