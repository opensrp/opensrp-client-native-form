package org.smartregister.nativeform.domain;

import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.annotation.WorkerThread;

import com.vijay.jsonwizard.constants.JsonFormConstants;

import org.json.JSONObject;
import org.smartregister.nativeform.contract.FormTesterContract;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.InputStream;
import java.io.InputStreamReader;

import timber.log.Timber;


/***
 * This is an Immutable class.
 * The class constructor is heavy and should be created in a back ground thread
 */
public class JsonForm implements FormTesterContract.NativeForm {

    private String formName;
    private String fileName;
    private boolean isValid = false;
    private JSONObject jsonObject;

    @WorkerThread
    public JsonForm(@NonNull File sourceFile) {
        fileName = sourceFile.getName();
        try {
            jsonObject = new JSONObject(getStringFromFile(sourceFile));
            if (jsonObject.has(JsonFormConstants.ENCOUNTER_TYPE)) {
                formName = jsonObject.getString(JsonFormConstants.ENCOUNTER_TYPE);
                isValid = true;
            }
        } catch (Exception e) {
            Timber.e(e);
        }
    }

    private String getStringFromFile(File sourceFile) throws Exception {
        FileInputStream fin = new FileInputStream(sourceFile);
        String ret = convertStreamToString(fin);
        //Make sure you close all streams.
        fin.close();
        return ret;
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

    @Nullable
    @Override
    public String getFormName() {
        return formName;
    }

    @Override
    public String getFileName() {
        return fileName;
    }

    @Override
    public boolean isValid() {
        return isValid;
    }

    @Nullable
    @Override
    public JSONObject getJsonForm() {
        return jsonObject;
    }
}
