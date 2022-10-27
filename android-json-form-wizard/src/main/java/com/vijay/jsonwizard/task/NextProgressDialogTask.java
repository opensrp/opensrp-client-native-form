package com.vijay.jsonwizard.task;

import android.app.ProgressDialog;
import android.content.Context;

import com.vijay.jsonwizard.fragments.JsonWizardFormFragment;
import com.vijay.jsonwizard.utils.AppExecutors;

public class NextProgressDialogTask {
    private JsonWizardFormFragment formFragment;
    private Context context;
    private ProgressDialog progressDialog;
    private AppExecutors appExecutors;

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

    public void init() {
        appExecutors = getFormFragment().getJsonApi().getAppExecutors();
        appExecutors.mainThread().execute(this::showDialog);
        appExecutors.diskIO().execute(() -> {
            getFormFragment().next();
            appExecutors.mainThread().execute(this::hideDialog);
        });
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
