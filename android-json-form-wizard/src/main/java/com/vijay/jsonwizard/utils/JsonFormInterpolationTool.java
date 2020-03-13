package com.vijay.jsonwizard.utils;

import com.vijay.jsonwizard.interactors.JsonFormInteractor;

/**
 * Created by Vincent Karuri on 12/03/2020
 */
public class JsonFormInterpolationTool {

    public static void main(String[] args) {
        System.out.println("Performing form interpolation...");
        System.out.println("Listing translatable widget fields:");
        for (String str : JsonFormInteractor.getInstance().getDefaultTranslatableWidgetFields()) {
            System.out.println(str);
        }
    }
}
