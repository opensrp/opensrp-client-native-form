package com.vijay.jsonwizard.utils;

import com.vijay.jsonwizard.interactors.JsonFormInteractor;

import java.io.FileNotFoundException;
import java.io.InputStream;

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
            printToSystemOut("List of translatable widget fields:\n");
            for (String str : JsonFormInteractor.getInstance().getDefaultTranslatableWidgetFields()) {
                printToSystemOut(str);
            }

            String form = getFileContentsAsString(fileToTranslate);
            printToSystemOut("\nForm before interpolation:\n");
            printToSystemOut(form);
        } catch (FileNotFoundException e) {
            Timber.e(e);
        }
    }

    private static void printToSystemOut(String str) {
        System.out.println(str);
    }

    public static void main(String[] args) {
        processForm();
    }
}
