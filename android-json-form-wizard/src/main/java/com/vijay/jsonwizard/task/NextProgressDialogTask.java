package com.vijay.jsonwizard.task;

import android.app.ProgressDialog;
import android.content.Context;
import android.os.AsyncTask;

import com.vijay.jsonwizard.fragments.JsonWizardFormFragment;

public class NextProgressDialogTask extends AsyncTask<Void, Void, Void> {
    private JsonWizardFormFragment formFragment;
    private Context context;
    private ProgressDialog progressDialog;

    private void showDialog() {
        setProgressDialog(new ProgressDialog(getContext()));
        getProgressDialog().setCancelable(false);
        getProgressDialog().setTitle(getContext().getString(com.vijay.jsonwizard.R.string.loading));
        getProgressDialog().setMessage(getContext().getString(com.vijay.jsonwizard.R.string.loading_form_message));
        getProgressDialog().show();
    }

    private void hideDialog() {
        if (getProgressDialog() != null && getProgressDialog().isShowing()) {
            getProgressDialog().dismiss();
        }
    }

    public NextProgressDialogTask(JsonWizardFormFragment jsonFormFragment) {
        if (jsonFormFragment != null) {
            this.formFragment = jsonFormFragment;
            this.context = jsonFormFragment.getContext();
        }
    }

    @Override
    protected Void doInBackground(Void... voids) {
        getFormFragment().next();
        return null;
    }

    @Override
    protected void onPreExecute() {
        super.onPreExecute();
        showDialog();
    }

    @Override
    protected void onPostExecute(Void aVoid) {
        super.onPostExecute(aVoid);
        hideDialog();
    }

    public JsonWizardFormFragment getFormFragment() {
        return formFragment;
    }

    public Context getContext() {
        return context;
    }

    public ProgressDialog getProgressDialog() {
        return progressDialog;
    }

    public void setProgressDialog(ProgressDialog progressDialog) {
        this.progressDialog = progressDialog;
    }
}
