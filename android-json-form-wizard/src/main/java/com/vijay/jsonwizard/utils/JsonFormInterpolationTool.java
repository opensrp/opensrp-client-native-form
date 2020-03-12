package com.vijay.jsonwizard.utils;

import com.vijay.jsonwizard.interactors.JsonFormInteractor;
import com.vijay.jsonwizard.interfaces.FormWidgetFactory;

import java.util.Map;

/**
 * Created by Vincent Karuri on 12/03/2020
 */
public class JsonFormInterpolationTool {

    public static void main(String[] args) {
        System.out.println("Performing form interpolation...");
        for (Map.Entry<String, FormWidgetFactory > entry : JsonFormInteractor.getInstance().map.entrySet()) {
            System.out.println("The key is: " + entry.getKey() + " and its value is: " + entry.getValue());
        }
    }
}
