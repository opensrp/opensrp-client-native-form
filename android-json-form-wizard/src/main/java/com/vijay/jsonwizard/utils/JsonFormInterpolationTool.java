package com.vijay.jsonwizard.utils;

import com.google.gson.Gson;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.vijay.jsonwizard.interactors.JsonFormInteractor;

import java.io.FileNotFoundException;
import java.util.Map;

import timber.log.Timber;

import static com.vijay.jsonwizard.utils.Utils.getFileContentsAsString;

/**
 * Created by Vincent Karuri on 12/03/2020
 */
public class JsonFormInterpolationTool {

    private static void processForm() {
        try {
            String fileToTranslate = System.getenv("FILE_TO_TRANSLATE");
            printToSystemOut("Interpolating form at path: " + fileToTranslate + " ...\n");

            String form = getFileContentsAsString(fileToTranslate);

            printToSystemOut("\nForm before interpolation:\n");
            printToSystemOut(form);

            performInterpolation(stringToJson(form));
        } catch (FileNotFoundException e) {
            Timber.e(e);
        }
    }

    private static JsonObject stringToJson(String json) {
        return new Gson().fromJson(json, JsonObject.class);
    }

    private static void performInterpolation(JsonObject jsonForm) {
        printToSystemOut("List of translatable widget fields:\n");
        for (String str : JsonFormInteractor.getInstance().getDefaultTranslatableWidgetFields()) {
            printToSystemOut(str);
        }
        for (Map.Entry<String, JsonElement> entry : jsonForm.entrySet()) {
            printToSystemOut("The key is: " + entry.getKey() + " and the value is: " + entry.getValue());
        }
    }

    private static void printToSystemOut(String str) {
        System.out.println(str);
    }

    public static void main(String[] args) {
        processForm();
    }
}
