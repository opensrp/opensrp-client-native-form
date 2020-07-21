package com.vijay.jsonwizard.shadow;

import android.content.Context;

import com.vijay.jsonwizard.customviews.TreeViewDialog;

import org.json.JSONArray;
import org.robolectric.annotation.Implementation;
import org.robolectric.annotation.Implements;

import java.util.ArrayList;

@Implements(TreeViewDialog.class)
public class ShadowTreeViewDialog {
    private ArrayList<String> defaultValue;
    private ArrayList<String> value;
    private JSONArray structure;
    private Context context;

    @Implementation
    protected void __constructor__(Context context, JSONArray structure, ArrayList<String> defaultValue, ArrayList<String> value) {
        this.context = context;
        this.structure = structure;
        this.defaultValue = defaultValue;
        this.value = value;
    }
}
