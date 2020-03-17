package com.vijay.jsonwizard.utils;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.vijay.jsonwizard.constants.JsonFormConstants;
import com.vijay.jsonwizard.interactors.JsonFormInteractor;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileWriter;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;
import java.util.Set;

import timber.log.Timber;

import static com.vijay.jsonwizard.constants.JsonFormConstants.KEY;
import static com.vijay.jsonwizard.constants.JsonFormConstants.MLS.PROPERTIES_FILE_NAME;
import static com.vijay.jsonwizard.constants.JsonFormConstants.STEP;
import static com.vijay.jsonwizard.utils.Utils.getFileContentsAsString;

/**
 * Created by Vincent Karuri on 12/03/2020
 */
public class JsonFormPlaceholderGenerator {

    private static Map<String, String> interpolationToTranslationMap = new HashMap<>();
    private static String formName;

    /**
     *
     * Processes the {@param formToTranslate} outputting a placeholder-injected form
     * and its corresponding property file
     *
     * @param formToTranslate
     */
    public static void processForm(String formToTranslate) {
        try {
            String form = getFileContentsAsString(formToTranslate);

            printToSystemOut("\nForm before interpolation:\n");
            printToSystemOut(form);

            String[] formPath = formToTranslate.split(File.separator);
            formName = formPath[formPath.length - 1].split("\\.")[0] + "_interpolated";
            JsonObject interpolatedForm = performInterpolation(stringToJson(form), formName);
            interpolatedForm.addProperty(PROPERTIES_FILE_NAME, formName);

            Gson gson = new GsonBuilder().setPrettyPrinting().create();
            writeToFile(gson.toJson(interpolatedForm, JsonObject.class), File.separator + "tmp" + File.separator + formName + ".json");

            createTranslationsPropertyFile();
        } catch (FileNotFoundException e) {
            Timber.e(e);
        }
    }

    /**
     *
     * Converts a {@link String} representation of JSON into a {@link JsonObject}
     *
     * @param json
     * @return
     */
    private static JsonObject stringToJson(String json) {
        return new Gson().fromJson(json, JsonObject.class);
    }

    /**
     *
     * Replaces {@link String} literals in the {@param jsonForm} with placeholders.
     *
     * The {@link String} literals (fields) to be replaced have to be defined either globally or as part of a widget's definition.
     *
     * The placeholders follow the scheme : {{form_name.step_name.widget_key.field_identifier}}
     *
     * @param jsonForm
     * @param formName
     * @return
     */
    private static JsonObject performInterpolation(JsonObject jsonForm, String formName) {
        printToSystemOut("List of translatable widget fields:\n");
        for (String str : JsonFormInteractor.getInstance().getDefaultTranslatableWidgetFields()) {
            printToSystemOut(str);
        }

        int numOfSteps = getNumOfSteps(jsonForm);
        for (int i = 1; i <= numOfSteps; i++) {
            String stepName = STEP + i;
            JsonArray stepWidgets = getWidgets(jsonForm, stepName);
            printToSystemOut("The key is: " + stepName + " and the value is: " + stepWidgets);
            replaceStringLiterals(formName + "." + stepName, stepWidgets, JsonFormInteractor.getInstance().getDefaultTranslatableWidgetFields());
        }

        printToSystemOut("Interpolated string: " + jsonForm);

        return jsonForm;
    }

    /**
     *
     * Replaces {@link String} literals with the appropriate placeholders
     *
     * @param interpolationStrPrefix
     * @param stepWidgets
     * @param fieldsToTranslate
     */
    private static void replaceStringLiterals(String interpolationStrPrefix, JsonArray stepWidgets, Set<String> fieldsToTranslate) {
        for (int i = 0; i < stepWidgets.size(); i++) {
            JsonObject widget = stepWidgets.get(i).getAsJsonObject();
            String widgetKey = widget.get(KEY).getAsString();
            printToSystemOut(widget.toString());
            for (String fieldName : fieldsToTranslate) {
                String[] fieldHierarchy = fieldName.split("\\.");
                JsonObject fieldToInterpolate = widget;
                for (int j = 0; j < fieldHierarchy.length - 1; j++) {
                    if (fieldToInterpolate != null) {
                        fieldToInterpolate = fieldToInterpolate.getAsJsonObject(fieldHierarchy[j]);
                    }
                }

                String propertyName = interpolationStrPrefix + "." + widgetKey + "." + fieldName;
                String interpolationStr = "{{" + propertyName + "}}";
                if (fieldToInterpolate != null) {
                    JsonElement strLiteralElement = fieldToInterpolate.get(fieldHierarchy[fieldHierarchy.length - 1]);
                    if (strLiteralElement != null) {
                        interpolationToTranslationMap.put(propertyName, strLiteralElement.getAsString());
                        fieldToInterpolate.addProperty(fieldHierarchy[fieldHierarchy.length - 1], interpolationStr);
                    }
                }

                printToSystemOut("Interpolation String for widget " + widgetKey +  " is: " + interpolationStr);
            }
        }
    }

    /**
     *
     * Gets all the widget definitions for a particular {@param step} in the {@param jsonForm}
     *
     * @param jsonForm
     * @param step
     * @return
     */
    private static JsonArray getWidgets(JsonObject jsonForm, String step) {
        JsonObject stepJsonObject = jsonForm.has(step) ? jsonForm.getAsJsonObject(step) : null;
        if (stepJsonObject == null) {
            return null;
        }

        return stepJsonObject.has(JsonFormConstants.FIELDS) ? stepJsonObject
                .getAsJsonArray(JsonFormConstants.FIELDS) : null;
    }

    /**
     *
     * Extracts the number of steps in a form
     *
     * @param jsonForm
     * @return
     */
    private static int getNumOfSteps(JsonObject jsonForm) {
        return jsonForm.has(JsonFormConstants.COUNT) ? jsonForm.get(JsonFormConstants.COUNT).getAsInt() : -1;
    }

    /**
     *
     * Utility that prints to system out for debugging
     *
     * @param str
     */
    private static void printToSystemOut(String str) {
        System.out.println(str);
    }

    /**
     *
     * Creates a property file and writes it to disk
     *
     */
    private static void createTranslationsPropertyFile() {
        StringBuilder stringBuilder = new StringBuilder();
        for (Map.Entry<String, String> entry : interpolationToTranslationMap.entrySet()) {
            stringBuilder.append(entry.getKey() + " = " + entry.getValue() + "\n");
        }
        writeToFile(stringBuilder.toString(), File.separator + "tmp" + File.separator + formName + ".properties");
    }

    /**
     *
     * Writes {@param data} to disk at the specified {@param path}
     *
     * @param data
     * @param path
     */
    private static void writeToFile(String data, String path) {
        FileWriter fileWriter = null;
        try {
            File file = new File(path);
            fileWriter = new FileWriter(file);
            fileWriter.write(data);
        } catch (IOException e) {
            Timber.e(e);
        } finally {
            if (fileWriter != null) {
                try {
                    fileWriter.close();
                } catch (IOException e) {
                    Timber.e(e);
                }
            }
        }
    }

    public static void main(String[] args) {
        String formToTranslate = System.getenv("FORM_TO_TRANSLATE");
        printToSystemOut("Interpolating form at path: " + formToTranslate + " ...\n");
        processForm(formToTranslate);
    }
}
