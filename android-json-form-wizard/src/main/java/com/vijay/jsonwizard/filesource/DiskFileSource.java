package com.vijay.jsonwizard.filesource;

import android.content.Context;
import android.os.Environment;

import com.vijay.jsonwizard.constants.JsonFormConstants;
import com.vijay.jsonwizard.interfaces.FormFileSource;

import org.jeasy.rules.api.Rules;
import org.jeasy.rules.mvel.MVELRuleFactory;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.InputStreamReader;

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
    public JSONObject getFormFromFile(Context context, String fileName) {
        return null;
    }

    @Override
    public byte[] getFileContent(Context context, String fileName) {
        return new byte[0];
    }

    private InputStreamReader getReaderForFile(String fileName) throws Exception {
        String root = Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DOWNLOADS).toString();
        File sourceFile = new File(root + "/" + JsonFormConstants.DEFAULT_FORMS_DIRECTORY + "/" + fileName);
        FileInputStream fin = new FileInputStream(sourceFile);
        return new InputStreamReader(fin);
    }
}
