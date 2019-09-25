package com.vijay.jsonwizard.listeners;

import android.content.Context;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;

import com.vijay.jsonwizard.R;
import com.vijay.jsonwizard.utils.FormUtils;
import com.vijay.jsonwizard.views.CustomTextView;

import org.json.JSONArray;
import org.json.JSONObject;

public class ExpansionPanelRecordButtonClickListener implements View.OnClickListener {
    private FormUtils formUtils = new FormUtils();

    @Override
    public void onClick(View view) {
        LinearLayout linearLayout;

        if (view instanceof RelativeLayout) {
            linearLayout = (LinearLayout) view.getParent().getParent();
        } else if (view instanceof ImageView || view instanceof CustomTextView) { // This caters for the different views that can be clicked to show the popup
            linearLayout = (LinearLayout) view.getParent().getParent().getParent();
        } else {
            linearLayout = (LinearLayout) view.getParent().getParent().getParent();
        }

        view.setTag(R.id.main_layout, linearLayout);
        String stepName = (String) view.getTag(R.id.specify_step_name);
        String type = (String) view.getTag(R.id.type);
        String key = (String) view.getTag(R.id.key);
        Context context = (Context) view.getTag(R.id.specify_context);
        JSONArray currentFields = formUtils.getFormFields(stepName, context);
        JSONObject realTimeJsonObject = FormUtils.getFieldJSONObject(currentFields, key);

        if (type != null) {
            view.setTag(R.id.secondaryValues, formUtils.getSecondaryValues(realTimeJsonObject, type));
        }

        formUtils.showGenericDialog(view);
    }
}
