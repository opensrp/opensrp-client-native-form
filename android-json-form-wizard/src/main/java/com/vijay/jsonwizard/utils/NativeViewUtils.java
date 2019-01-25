package com.vijay.jsonwizard.utils;

import android.content.Context;
import android.util.Log;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;

public class NativeViewUtils {

    private static String TAG = NativeViewUtils.class.getCanonicalName();

    public static JSONObject getFormJson(Context context, String formIdentity) {

        try {
            String url = String.format("json.form/%s/.json", formIdentity);
            InputStream inputStream = context.getAssets().open(url);
            BufferedReader reader = new BufferedReader(new InputStreamReader(inputStream,
                    "UTF-8"));
            String jsonString;
            StringBuilder stringBuilder = new StringBuilder();
            while ((jsonString = reader.readLine()) != null) {
                stringBuilder.append(jsonString);
            }
            inputStream.close();

            return new JSONObject(stringBuilder.toString());
        } catch (IOException e) {
            Log.e(TAG, e.getMessage(), e);
            ;
        } catch (JSONException e) {
            Log.e(TAG, e.getMessage(), e);
            ;
        }

        return null;
    }
}
