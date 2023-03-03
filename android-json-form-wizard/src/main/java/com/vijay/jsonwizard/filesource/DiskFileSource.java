package com.vijay.jsonwizard.filesource;

import android.content.Context;
import android.os.Environment;
import androidx.annotation.VisibleForTesting;

import com.vijay.jsonwizard.constants.JsonFormConstants;
import com.vijay.jsonwizard.interfaces.FormFileSource;
import com.vijay.jsonwizard.rules.YamlRuleDefinitionReaderExt;
import com.vijay.jsonwizard.utils.NativeFormsProperties;
import com.vijay.jsonwizard.utils.Utils;

import org.jeasy.rules.api.Rules;
import org.jeasy.rules.mvel.MVELRuleFactory;
import org.jeasy.rules.support.reader.YamlRuleDefinitionReader;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.InputStream;
import java.io.InputStreamReader;

import timber.log.Timber;


/**
 * Returns forms rules and other files stored on the devices
 * hard disk
 */
public class DiskFileSource implements FormFileSource {

    public static DiskFileSource INSTANCE = new DiskFileSource();
    private MVELRuleFactory mvelRuleFactory;

    private DiskFileSource() {
        if(Utils.enabledProperty(NativeFormsProperties.KEY.EASY_RULES_V3_COMPATIBILITY)){
            this.mvelRuleFactory = new MVELRuleFactory(new YamlRuleDefinitionReaderExt());
            Timber.e("Disk File source Mvel backward compat engaged");

        }
        else {
            this.mvelRuleFactory = new MVELRuleFactory(new YamlRuleDefinitionReader());
            Timber.e("Disk File source Mvel backward compat  not engaged");
        }
    }

    @Override
    public Rules getRulesFromFile(Context context, String fileName) throws Exception {
        BufferedReader bufferedReader = new BufferedReader(new InputStreamReader(getInputStream(fileName)));
        return this.mvelRuleFactory.createRules(bufferedReader);
    }

    @Override
    public JSONObject getFormFromFile(Context context, String fileName) throws Exception {
        String content = Utils.convertStreamToString(getFileInputStream(context, fileName));
        return new JSONObject(content);
    }

    @Override
    public InputStream getFileInputStream(Context context, String fileName) throws Exception {
        return getInputStream(JsonFormConstants.JSON_FORM_DIRECTORY + "/" + fileName + ".json");
    }

    private InputStream getInputStream(String fileName) throws Exception {
        String root = Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DOWNLOADS).toString();
        File sourceFile = new File(root + "/" + JsonFormConstants.DEFAULT_FORMS_DIRECTORY + "/" + fileName);
        return getInputStream(sourceFile);
    }

    @VisibleForTesting
    public InputStream getInputStream(File sourceFile) throws FileNotFoundException {
        return new FileInputStream(sourceFile);
    }

}
