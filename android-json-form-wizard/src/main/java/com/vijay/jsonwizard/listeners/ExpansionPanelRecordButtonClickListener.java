package com.vijay.jsonwizard.listeners;

import android.content.Context;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;

import com.vijay.jsonwizard.R;
import com.vijay.jsonwizard.task.ExpansionPanelGenericPopupDialogTask;
import com.vijay.jsonwizard.utils.FormUtils;
import com.vijay.jsonwizard.views.CustomTextView;

import org.json.JSONArray;
import org.json.JSONObject;

/**
 * Performs the click actions required by the {@link com.vijay.jsonwizard.widgets.ExpansionPanelFactory} widget record button.
 */
public class ExpansionPanelRecordButtonClickListener implements View.OnClickListener {
    private FormUtils formUtils = new FormUtils();

    @Override
    public void onClick(View view) {
        Context context = (Context) view.getTag(R.id.specify_context);

        LinearLayout linearLayout = getLinearLayout(view);
        disableExpansionPanelViews(linearLayout);

        view.setTag(R.id.main_layout, linearLayout);
        String stepName = (String) view.getTag(R.id.specify_step_name);
        String type = (String) view.getTag(R.id.type);
        String key = (String) view.getTag(R.id.key);
        JSONArray currentFields = formUtils.getFormFields(stepName, context);
        JSONObject realTimeJsonObject = FormUtils.getFieldJSONObject(currentFields, key);

        if (type != null) {
            view.setTag(R.id.secondaryValues, formUtils.getSecondaryValues(realTimeJsonObject, type));
        }

        initiateTask(view);
    }

    protected void initiateTask(View view) {
        new ExpansionPanelGenericPopupDialogTask(view).execute();
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

    /**
     * Disable the expansion panel views in the main view from {@link ExpansionPanelRecordButtonClickListener#getLinearLayout(View)} after they are clicked
     *
     * @param linearLayout {@link LinearLayout}
     */
    private void disableExpansionPanelViews(LinearLayout linearLayout) {
        RelativeLayout layoutHeader = (RelativeLayout) linearLayout.getChildAt(0);
        RelativeLayout expansionHeaderLayout = layoutHeader.findViewById(R.id.expansion_header_layout);
        expansionHeaderLayout.setEnabled(false);
        expansionHeaderLayout.setClickable(false);

        ImageView statusImageView = expansionHeaderLayout.findViewById(R.id.statusImageView);
        statusImageView.setEnabled(false);
        statusImageView.setClickable(false);

        CustomTextView topBarTextView = expansionHeaderLayout.findViewById(R.id.topBarTextView);
        topBarTextView.setClickable(false);
        topBarTextView.setEnabled(false);

        LinearLayout contentLayout = (LinearLayout) linearLayout.getChildAt(1);
        LinearLayout buttonLayout = contentLayout.findViewById(R.id.accordion_bottom_navigation);
        Button okButton = buttonLayout.findViewById(R.id.ok_button);
        okButton.setEnabled(false);
        okButton.setClickable(false);

    }
}
