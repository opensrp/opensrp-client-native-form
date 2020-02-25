package com.vijay.jsonwizard.task;

import android.app.ProgressDialog;
import android.content.Context;
import android.os.AsyncTask;

import com.vijay.jsonwizard.R;
import com.vijay.jsonwizard.activities.JsonWizardFormActivity;
import com.vijay.jsonwizard.fragments.JsonFormFragment;

public class NextProgressDialogTask extends AsyncTask<Void, Void, Void> {
    private Context context;
    private ProgressDialog progressDialog;
    private JsonFormFragment formFragment;

    public NextProgressDialogTask(Context context, JsonFormFragment jsonFormFragment) {
        this.context = context;
        this.formFragment = jsonFormFragment;
        progressDialog = new ProgressDialog(context);
    }

    @Override
    protected Void doInBackground(Void... voids) {
        ((JsonWizardFormActivity) getContext()).getSupportFragmentManager().beginTransaction()
                .setCustomAnimations(R.anim.enter_from_right, R.anim.exit_to_left, R.anim.enter_from_left,
                        R.anim.exit_to_right).replace(R.id.container, getFormFragment()).addToBackStack(getFormFragment().getClass().getSimpleName())
                .commitAllowingStateLoss(); // use https://stackoverflow.com/a/10261449/9782187
        return null;
    }

    @Override
    protected void onPreExecute() {
        super.onPreExecute();
        progressDialog.setCancelable(false);
        progressDialog.setTitle(context.getString(R.string.loading));
        progressDialog.setMessage(context.getString(R.string.loading_form_message));
        progressDialog.show();
    }

    @Override
    protected void onPostExecute(Void aVoid) {
        super.onPostExecute(aVoid);
        if (progressDialog != null && progressDialog.isShowing()) {
            progressDialog.dismiss();
        }
    }

    public Context getContext() {
        return context;
    }

    public JsonFormFragment getFormFragment() {
        return formFragment;
    }
}
