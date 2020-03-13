package com.vijay.jsonwizard.utils;

import com.google.gson.Gson;
import com.google.gson.JsonArray;
import com.google.gson.JsonObject;
import com.vijay.jsonwizard.constants.JsonFormConstants;
import com.vijay.jsonwizard.interactors.JsonFormInteractor;

import java.io.FileNotFoundException;

import timber.log.Timber;

import static com.vijay.jsonwizard.constants.JsonFormConstants.STEP;
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

        int numOfSteps = getNumOfSteps(jsonForm);
        for (int i = 1; i <= numOfSteps; i++) {
            String stepName = STEP + i;
            printToSystemOut("The key is: " + stepName + " and the value is: " + getWidgets(jsonForm, stepName));
        }
    }

    private static void printToSystemOut(String str) {
        System.out.println(str);
    }

    public static void main(String[] args) {
        processForm();
    }

    private static JsonArray getWidgets(JsonObject jsonForm, String step) {
        JsonObject stepJsonObject = jsonForm.has(step) ? jsonForm.getAsJsonObject(step) : null;
        if (stepJsonObject == null) {
            return null;
        }

        return stepJsonObject.has(JsonFormConstants.FIELDS) ? stepJsonObject
                .getAsJsonArray(JsonFormConstants.FIELDS) : null;
    }

    private static int getNumOfSteps(JsonObject jsonForm) {
        return jsonForm.has(JsonFormConstants.COUNT) ? jsonForm.get(JsonFormConstants.COUNT).getAsInt() : -1;
    }
}
