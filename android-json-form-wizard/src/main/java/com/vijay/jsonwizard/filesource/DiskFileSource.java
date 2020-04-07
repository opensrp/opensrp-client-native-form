package com.vijay.jsonwizard.filesource;

import android.content.Context;
import android.os.Environment;

import com.vijay.jsonwizard.constants.JsonFormConstants;
import com.vijay.jsonwizard.interfaces.FormFileSource;

import org.jeasy.rules.api.Rules;
import org.jeasy.rules.mvel.MVELRuleFactory;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;

import timber.log.Timber;

/**
 * Returns forms rules and other files stored on the devices
 * hard disk
 */
public class DiskFileSource implements FormFileSource {

    private DiskFileSource() {

    }

    public static DiskFileSource INSTANCE = new DiskFileSource();

    @Override
    public Rules getRulesFromFile(Context context, String fileName) throws Exception {
        BufferedReader bufferedReader = new BufferedReader(getReaderForFile(fileName));
        return MVELRuleFactory.createRulesFrom(bufferedReader);
    }

    @Override
    public JSONObject getFormFromFile(Context context, String fileName) throws Exception {
        try {
            String root = Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DOWNLOADS).toString();
            File sourceFile = new File(root + "/" + JsonFormConstants.DEFAULT_FORMS_DIRECTORY + "/json.form/" + fileName + ".json");
            return new JSONObject(getStringFromFile(sourceFile));
        } catch (IOException e) {
            Timber.e(e);
        } catch (JSONException e) {
            Timber.e(e);
        }

        return null;
    }

    @Override
    public InputStream getFileInputStream(Context context, String fileName) throws Exception {
        String root = Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DOWNLOADS).toString();
        File sourceFile = new File(root + "/" + JsonFormConstants.DEFAULT_FORMS_DIRECTORY + "/json.form/" + fileName + ".json");
        return new FileInputStream(sourceFile);
    }

    private String getStringFromFile(File sourceFile) throws Exception {
        FileInputStream fin = null;
        try {
            fin = new FileInputStream(sourceFile);
            return convertStreamToString(fin);
        } finally {
            if (fin != null)
                fin.close();
        }
    }

    private String convertStreamToString(InputStream is) throws Exception {
        BufferedReader reader = new BufferedReader(new InputStreamReader(is));
        StringBuilder sb = new StringBuilder();
        String line;
        while ((line = reader.readLine()) != null) {
            sb.append(line).append("\n");
        }
        reader.close();
        return sb.toString();
    }

    private InputStreamReader getReaderForFile(String fileName) throws Exception {
        String root = Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DOWNLOADS).toString();
        File sourceFile = new File(root + "/" + JsonFormConstants.DEFAULT_FORMS_DIRECTORY + "/" + fileName);
        FileInputStream fin = new FileInputStream(sourceFile);
        return new InputStreamReader(fin);
    }
}
