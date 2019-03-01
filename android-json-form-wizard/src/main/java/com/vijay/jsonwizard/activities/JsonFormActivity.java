package com.vijay.jsonwizard.activities;

import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.ViewGroup;

import com.vijay.jsonwizard.R;
import com.vijay.jsonwizard.constants.JsonFormConstants;
import com.vijay.jsonwizard.domain.Form;
import com.vijay.jsonwizard.engine.JsonApiEngine;
import com.vijay.jsonwizard.fragments.JsonFormFragment;

public class JsonFormActivity extends AppCompatActivity {

    private JsonApiEngine jsonApiEngine;
    private static final String TAG = JsonFormActivity.class.getSimpleName();
    private static final String JSON_STATE = "jsonState";
    private static final String FORM_STATE = "formState";
    private Toolbar mToolbar;
    private Form form;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.native_form_activity_json_form);
        jsonApiEngine = new JsonApiEngine(this, ((ViewGroup) (findViewById(android.R.id.content))).getChildAt(0));


        mToolbar = findViewById(R.id.tb_top);
        setSupportActionBar(mToolbar);
        if (savedInstanceState == null) {
            jsonApiEngine.init(getIntent().getStringExtra(JsonFormConstants.JSON_FORM_KEY.JSON));
            initializeFormFragment();
            jsonApiEngine.onFormStart();
            this.form = jsonApiEngine.extractForm(getIntent().getSerializableExtra(JsonFormConstants.JSON_FORM_KEY.FORM));
        } else {
            jsonApiEngine.init(savedInstanceState.getString(JSON_STATE));
            this.form = jsonApiEngine.extractForm(savedInstanceState.getSerializable(FORM_STATE));
        }

        jsonApiEngine.onCreate(savedInstanceState);
    }

    public JsonApiEngine getJsonApiEngine() {
        return jsonApiEngine;
    }

    public void setJsonApiEngine(JsonApiEngine jsonApiEngine) {
        this.jsonApiEngine = jsonApiEngine;
    }

    public void initializeFormFragment() {
        getSupportFragmentManager().beginTransaction()
                .add(R.id.container, JsonFormFragment.getFormFragment(JsonFormConstants.FIRST_STEP_NAME)).commit();
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (!jsonApiEngine.onActivityResult(requestCode, resultCode, data)) {
            super.onActivityResult(requestCode, resultCode, data);
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, String permissions[], int[] grantResults) {
        if (!jsonApiEngine.onRequestPermissionsResult(requestCode, permissions, grantResults)) {
            super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        }
    }

    public Form getForm() {
        return form;
    }

    public Toolbar getToolbar() {
        return mToolbar;
    }

    @Override
    public void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        outState.putString(JSON_STATE, jsonApiEngine.getmJSONObject().toString());
        outState.putSerializable(FORM_STATE, form);
        jsonApiEngine.onSaveInstanceState(outState);
    }

    @Override
    public void onBackPressed() {
        AlertDialog dialog = new AlertDialog.Builder(this, R.style.AppThemeAlertDialog).setTitle(jsonApiEngine.getConfirmCloseTitle())
                .setMessage(jsonApiEngine.getConfirmCloseMessage()).setNegativeButton(R.string.yes, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        JsonFormActivity.this.finish();
                    }
                }).setPositiveButton(R.string.no, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        Log.d(TAG, "No button on dialog in " + JsonFormActivity.class.getCanonicalName());
                    }
                }).create();

        dialog.show();
    }

    @Override
    protected void onStart() {
        super.onStart();
        jsonApiEngine.onStart();
    }

    @Override
    protected void onResume() {
        super.onResume();
        jsonApiEngine.onResume();
    }

    @Override
    protected void onPause() {
        jsonApiEngine.onBeforePause();
        super.onPause();
        jsonApiEngine.onPause();
    }

    @Override
    protected void onStop() {
        super.onStop();
        jsonApiEngine.onStop();
    }

    @Override
    public void onLowMemory() {
        super.onLowMemory();
        jsonApiEngine.onLowMemory();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        jsonApiEngine.onDestroy();
    }
}
