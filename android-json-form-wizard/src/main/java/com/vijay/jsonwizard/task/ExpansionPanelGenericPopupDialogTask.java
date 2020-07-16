package com.vijay.jsonwizard.task;

import android.app.Activity;
import android.app.FragmentTransaction;
import android.content.Context;
import android.os.AsyncTask;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.Toast;

import com.vijay.jsonwizard.R;
import com.vijay.jsonwizard.constants.JsonFormConstants;
import com.vijay.jsonwizard.customviews.ExpansionPanelGenericPopupDialog;
import com.vijay.jsonwizard.fragments.JsonFormFragment;
import com.vijay.jsonwizard.interfaces.CommonListener;
import com.vijay.jsonwizard.utils.FormUtils;
import com.vijay.jsonwizard.utils.Utils;
import com.vijay.jsonwizard.views.CustomTextView;

import org.json.JSONArray;

import timber.log.Timber;

/**
 * The {@link AsyncTask} to start and set the required variables on the {@link ExpansionPanelGenericPopupDialog}
 */
public class ExpansionPanelGenericPopupDialogTask extends AsyncTask<Void, Void, Void> {
    private FormUtils formUtils = new FormUtils();
    private Utils utils = new Utils();
    private View view;


    public ExpansionPanelGenericPopupDialogTask(View view) {
        this.view = view;
    }

    @Override
    protected void onPreExecute() {
        super.onPreExecute();
        Context context = (Context) view.getTag(R.id.specify_context);
        Utils.showProgressDialog(R.string.loading, R.string.loading_form_message, context);
    }

    @Override
    protected Void doInBackground(Void... voids) {
        Context context = (Context) view.getTag(R.id.specify_context);
        String specifyContent = (String) view.getTag(R.id.specify_content);
        String specifyContentForm = (String) view.getTag(R.id.specify_content_form);
        String stepName = (String) view.getTag(R.id.specify_step_name);
        CommonListener listener = (CommonListener) view.getTag(R.id.specify_listener);
        JsonFormFragment formFragment = (JsonFormFragment) view.getTag(R.id.specify_fragment);
        JSONArray jsonArray = (JSONArray) view.getTag(R.id.secondaryValues);
        String parentKey = (String) view.getTag(R.id.key);
        String type = (String) view.getTag(R.id.type);
        CustomTextView customTextView = (CustomTextView) view.getTag(R.id.specify_textview);
        CustomTextView reasonsTextView = (CustomTextView) view.getTag(R.id.specify_reasons_textview);
        String toolbarHeader = "";
        String container = "";
        LinearLayout rootLayout = (LinearLayout) view.getTag(R.id.main_layout);
        if (type != null && type.equals(JsonFormConstants.EXPANSION_PANEL)) {
            toolbarHeader = (String) view.getTag(R.id.header);
            container = (String) view.getTag(R.id.contact_container);
        }

        if (specifyContent != null) {
            ExpansionPanelGenericPopupDialog genericPopupDialog = new ExpansionPanelGenericPopupDialog();
            genericPopupDialog.setCommonListener(listener);
            genericPopupDialog.setFormFragment(formFragment);
            genericPopupDialog.setFormIdentity(specifyContent);
            genericPopupDialog.setFormLocation(specifyContentForm);
            genericPopupDialog.setStepName(stepName);
            genericPopupDialog.setSecondaryValues(jsonArray);
            genericPopupDialog.setParentKey(parentKey);
            genericPopupDialog.setLinearLayout(rootLayout);
            genericPopupDialog.setContext(context);
            utils.setExpansionPanelDetails(type, toolbarHeader, container, genericPopupDialog);
            genericPopupDialog.setWidgetType(type);
            if (customTextView != null && reasonsTextView != null) {
                genericPopupDialog.setCustomTextView(customTextView);
                genericPopupDialog.setPopupReasonsTextView(reasonsTextView);
            }
            utils.setChildKey(view, type, genericPopupDialog);

            FragmentTransaction fragmentTransaction = utils.getFragmentTransaction((Activity) context);
            genericPopupDialog.show(fragmentTransaction, "GenericPopup");
            formUtils.resetFocus(context);
        } else {
            Toast.makeText(context, "Please specify the sub form to display ", Toast.LENGTH_LONG).show();
            Timber.e("No sub form specified. Please specify one in order to use the expansion panel.");
        }
        return null;
    }
}
