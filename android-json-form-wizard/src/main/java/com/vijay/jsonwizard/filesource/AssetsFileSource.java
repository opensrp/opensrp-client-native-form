package com.vijay.jsonwizard.filesource;

import android.content.Context;

import com.vijay.jsonwizard.interfaces.FormFileSource;

import org.jeasy.rules.api.Rules;
import org.jeasy.rules.mvel.MVELRuleFactory;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.InputStreamReader;

/***
 * used to read files stored on the APP's asset folder
 *
 */
public class AssetsFileSource implements FormFileSource {

    private AssetsFileSource() {
    }

    public static AssetsFileSource INSTANCE = new AssetsFileSource();

    @Override
    public Rules getRulesFromFile(Context context, String fileName) throws Exception {
        BufferedReader bufferedReader = new BufferedReader(new InputStreamReader(context.getAssets().open(fileName)));
        return MVELRuleFactory.createRulesFrom(bufferedReader);
    }

    @Override
    public JSONObject getFormFromFile(Context context, String fileName) {
        return null;
    }

    @Override
    public byte[] getFileContent(Context context, String fileName) {
        return new byte[0];
    }
}
