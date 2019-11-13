package com.vijay.jsonwizard.listeners;

import android.app.ProgressDialog;
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
    private ProgressDialog progressDialog;

    @Override
    public void onClick(View view) {
        Context context = (Context) view.getTag(R.id.specify_context);
        initializeProgressDialog(context);

        if (progressDialog.isShowing()) {
            LinearLayout linearLayout = getLinearLayout(view);
            view.setTag(R.id.main_layout, linearLayout);
            String stepName = (String) view.getTag(R.id.specify_step_name);
            String type = (String) view.getTag(R.id.type);
            String key = (String) view.getTag(R.id.key);
            JSONArray currentFields = formUtils.getFormFields(stepName, context);
            JSONObject realTimeJsonObject = FormUtils.getFieldJSONObject(currentFields, key);

            if (type != null) {
                view.setTag(R.id.secondaryValues, formUtils.getSecondaryValues(realTimeJsonObject, type));
            }
            view.setTag(R.id.progress_dialog, progressDialog);

            formUtils.showGenericDialog(view);
        }
    }

    /**
     * Intializes the popup form loading progress dialog
     *
     * @param context {@link Context}
     */
    private void initializeProgressDialog(Context context) {
        progressDialog = new ProgressDialog(context);
        progressDialog.setCancelable(false);
        progressDialog.setTitle(context.getString(R.string.loading));
        progressDialog.setMessage(context.getString(R.string.loading_form_message));
        progressDialog.show();
    }

    /**
     * Gets the main layout from the different clickable views on the expansion panel row.
     *
     * @param view {@link View}
     * @return linearLayout {@link LinearLayout}
     */
    private LinearLayout getLinearLayout(View view) {
        LinearLayout linearLayout;
        if (view instanceof RelativeLayout) {
            linearLayout = (LinearLayout) view.getParent().getParent();
        } else if (view instanceof ImageView || view instanceof CustomTextView) { // This caters for the different views that can be clicked to show the popup
            linearLayout = (LinearLayout) view.getParent().getParent().getParent();
        } else {
            linearLayout = (LinearLayout) view.getParent().getParent().getParent();
        }
        return linearLayout;
    }
}
