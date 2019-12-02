package com.vijay.jsonwizard.task;

import android.content.Context;
import android.os.AsyncTask;

import com.vijay.jsonwizard.customviews.ExpansionPanelGenericPopupDialog;
import com.vijay.jsonwizard.utils.FormUtils;
import com.vijay.jsonwizard.utils.Utils;

import org.json.JSONException;

import timber.log.Timber;

public class ExpansionPanelGenericPopupDialogTask extends AsyncTask<Void, Void, Void> {
    private FormUtils formUtils = new FormUtils();
    private ExpansionPanelGenericPopupDialog genericPopupDialog;
    private Context context;

    public ExpansionPanelGenericPopupDialogTask(ExpansionPanelGenericPopupDialog genericPopupDialog, Context context) {
        this.genericPopupDialog = genericPopupDialog;
        this.context = context;
    }

    @Override
    protected void onPreExecute() {

    }

    @Override
    protected Void doInBackground(Void... voids) {
        try {
            genericPopupDialog.setMainFormFields(formUtils.getFormFields(genericPopupDialog.getStepName(), context));
            genericPopupDialog.getJsonApi().setGenericPopup(genericPopupDialog);
            genericPopupDialog.setGenericPopUpDialog();
            genericPopupDialog.loadPartialSecondaryValues();
            genericPopupDialog.createSecondaryValuesMap();
            genericPopupDialog.loadSubForms();
            genericPopupDialog.getJsonApi().updateGenericPopupSecondaryValues(genericPopupDialog.getSpecifyContent());
        } catch (JSONException e) {
            Timber.e(e, "ExpansionPanelGenericPopupDialogTask --> doInBackground");
        }
        return null;

    }


    @Override
    protected void onPostExecute(Void aVoid) {
       Utils.hideProgressDialog();
    }
}
