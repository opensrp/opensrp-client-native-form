package org.smartregister.nativeform;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;

import com.vijay.jsonwizard.activities.JsonFormActivity;
import com.vijay.jsonwizard.activities.JsonWizardFormActivity;
import com.vijay.jsonwizard.constants.JsonFormConstants;
import com.vijay.jsonwizard.domain.Form;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;

public class MainActivity extends AppCompatActivity implements View.OnClickListener {
    private static final int REQUEST_CODE_GET_JSON = 1234;
    private static final String TAG = MainActivity.class.getCanonicalName();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        findViewById(R.id.child_enrollment).setOnClickListener(this);
        findViewById(R.id.wizard_form).setOnClickListener(this);
        findViewById(R.id.rules_engine_skip_logic).setOnClickListener(this);
        findViewById(R.id.numbers_selector_widget).setOnClickListener(this);
        findViewById(R.id.generic_dialog_button).setOnClickListener(this);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            return true;
        } else if (id == R.id.action_single) {
            try {
                startForm(REQUEST_CODE_GET_JSON, "single_form", null);
            } catch (Exception e) {
                Log.e(TAG, e.getMessage(), e);
            }
        } else if (id == R.id.action_wizard) {
            try {
                startForm(REQUEST_CODE_GET_JSON, "wizard_form", null);
            } catch (Exception e) {
                Log.e(TAG, e.getMessage(), e);
            }
        }

        return super.onOptionsItemSelected(item);
    }


    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (resultCode == RESULT_OK) {
            String jsonString = data.getStringExtra("json");
            Log.i(getClass().getName(), "Result json String !!!! " + jsonString);
        }
        super.onActivityResult(requestCode, resultCode, data);
    }


    public void startForm(int jsonFormActivityRequestCode,
                          String formName, String entityId) throws Exception {

        final String STEP1 = "step1";
        final String FIELDS = "fields";
        final String KEY = "key";
        final String ZEIR_ID = "ZEIR_ID";
        final String VALUE = "value";

        String currentLocationId = "Kenya";


        JSONObject jsonForm = getFormJson(formName);
        if (jsonForm != null) {
            jsonForm.getJSONObject("metadata").put("encounter_location", currentLocationId);

            if (formName.equals("wizard_form")) {
                Intent intent = new Intent(this, JsonWizardFormActivity.class);
                intent.putExtra("json", jsonForm.toString());
                Log.d(getClass().getName(), "form is " + jsonForm.toString());

                Form form = new Form();
                form.setName(getString(R.string.profile));
                form.setWizard(true);
                form.setActionBarBackground(R.color.profile_actionbar);
                form.setNavigationBackground(R.color.profile_navigation);
                form.setHideSaveLabel(true);
                form.setNextLabel(getString(R.string.next));
                form.setPreviousLabel(getString(R.string.previous));
                form.setSaveLabel(getString(R.string.save));
                form.setBackIcon(R.drawable.ic_icon_positive);
                intent.putExtra(JsonFormConstants.JSON_FORM_KEY.FORM, form);

                startActivityForResult(intent, jsonFormActivityRequestCode);
            } else {


                if (entityId == null) {
                    entityId = "ABC" + Math.random();
                }


                // Inject zeir id into the form
                JSONObject stepOne = jsonForm.getJSONObject(STEP1);
                JSONArray jsonArray = stepOne.getJSONArray(FIELDS);
                for (int i = 0; i < jsonArray.length(); i++) {
                    JSONObject jsonObject = jsonArray.getJSONObject(i);
                    if (jsonObject.getString(KEY)
                            .equalsIgnoreCase(ZEIR_ID)) {
                        jsonObject.remove(VALUE);
                        jsonObject.put(VALUE, entityId);
                        continue;
                    }
                }

                Intent intent = new Intent(this, JsonFormActivity.class);
                intent.putExtra("json", jsonForm.toString());
                Log.d(getClass().getName(), "form is " + jsonForm.toString());
                startActivityForResult(intent, jsonFormActivityRequestCode);
            }

        }

    }


    public JSONObject getFormJson(String formIdentity) {

        try {
            InputStream inputStream = getApplicationContext().getAssets()
                    .open("json.form/" + formIdentity + ".json");
            BufferedReader reader = new BufferedReader(new InputStreamReader(inputStream,
                    "UTF-8"));
            String jsonString;
            StringBuilder stringBuilder = new StringBuilder();
            while ((jsonString = reader.readLine()) != null) {
                stringBuilder.append(jsonString);
            }
            inputStream.close();

            return new JSONObject(stringBuilder.toString());
        } catch (IOException e) {
            Log.e(TAG, e.getMessage(), e);
            ;
        } catch (JSONException e) {
            Log.e(TAG, e.getMessage(), e);
            ;
        }

        return null;
    }

    @Override
    public void onClick(View view) {
        int id = view.getId();
        try {
            switch (id) {
                case R.id.child_enrollment:
                    startForm(REQUEST_CODE_GET_JSON, "single_form", null);
                    break;
                case R.id.wizard_form:
                    startForm(REQUEST_CODE_GET_JSON, "wizard_form", null);
                    break;
                case R.id.rules_engine_skip_logic:
                    startForm(REQUEST_CODE_GET_JSON, "rules_engine_demo", null);
                    break;
                case R.id.numbers_selector_widget:
                    startForm(REQUEST_CODE_GET_JSON, "constraints_demo", null);
                    break;
                case R.id.generic_dialog_button:
                    startForm(REQUEST_CODE_GET_JSON, "anc_counselling_treatment", null);
                    break;
                default:
                    break;
            }
        } catch (Exception e) {
            Log.e(TAG, e.getMessage(), e);
        }

    }
}
