package com.vijay.jsonwizard.filesource;

import android.content.Context;

import com.vijay.jsonwizard.constants.JsonFormConstants;
import com.vijay.jsonwizard.interfaces.FormFileSource;
import com.vijay.jsonwizard.utils.FileReaderUtil;

import org.jeasy.rules.api.Rules;
import org.jeasy.rules.mvel.MVELRuleFactory;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;

import timber.log.Timber;

/***
 * used to read files stored on the APP's asset folder
 *
 */
public class AssetsFileSource implements FormFileSource {

    public static AssetsFileSource INSTANCE = new AssetsFileSource();

    private AssetsFileSource() {
    }

    @Override
    public Rules getRulesFromFile(Context context, String fileName) throws Exception {
        BufferedReader bufferedReader = new BufferedReader(new InputStreamReader(context.getAssets().open(fileName)));
        return MVELRuleFactory.createRulesFrom(bufferedReader);
    }

    @Override
    public JSONObject getFormFromFile(Context context, String fileName) {
        try {
            String newFileName = JsonFormConstants.JSON_FORM_DIRECTORY + "/" + fileName + ".json";

            InputStream inputStream = context.getAssets()
                    .open(newFileName);

            return new JSONObject(FileReaderUtil.convertStreamToString(inputStream));
        } catch (IOException e) {
            Timber.e(e);
        } catch (JSONException e) {
            Timber.e(e);
        }
        return null;
    }

    @Override
    public InputStream getFileInputStream(Context context, String fileName) throws Exception {
        return context.getAssets()
                .open(fileName);
    }
}
