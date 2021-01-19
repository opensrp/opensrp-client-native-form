package com.vijay.jsonwizard.listeners;

import android.app.AlertDialog;
import android.app.Application;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;

import com.vijay.jsonwizard.R;
import com.vijay.jsonwizard.activities.JsonFormActivity;
import com.vijay.jsonwizard.constants.JsonFormConstants;
import com.vijay.jsonwizard.interfaces.JsonApi;
import com.vijay.jsonwizard.utils.FormUtils;
import com.vijay.jsonwizard.views.CustomTextView;
import com.vijay.jsonwizard.widgets.FactoryTest;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.mockito.ArgumentCaptor;
import org.mockito.ArgumentMatchers;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.MockitoAnnotations;
import org.robolectric.RuntimeEnvironment;
import org.robolectric.shadows.ShadowAlertDialog;
import org.robolectric.util.ReflectionHelpers;

import static org.mockito.Mockito.when;

public class ExpansionPanelUndoButtonClickListenerTest extends FactoryTest {

    private ExpansionPanelUndoButtonClickListener expansionPanelUndoButtonClickListener;

    @Mock
    private JsonApi jsonApi;

    private String strForm = "{\"validate_on_submit\":true,\"count\":\"1\",\"encounter_type\":\"Expansion Panel\",\"entity_id\":\"\",\"relational_id\":\"\",\"form_version\":\"0.0.1\",\"metadata\":{\"start\":{\"openmrs_entity_parent\":\"\",\"openmrs_entity\":\"concept\",\"openmrs_data_type\":\"start\",\"openmrs_entity_id\":\"163137AAAAAAAAAAAAAAAAAAAAAAAAAAAAAA\",\"value\":\"2020-09-01 10:47:42\"},\"end\":{\"openmrs_entity_parent\":\"\",\"openmrs_entity\":\"concept\",\"openmrs_data_type\":\"end\",\"openmrs_entity_id\":\"163138AAAAAAAAAAAAAAAAAAAAAAAAAAAAAA\"},\"today\":{\"openmrs_entity_parent\":\"\",\"openmrs_entity\":\"encounter\",\"openmrs_entity_id\":\"encounter_date\"},\"deviceid\":{\"openmrs_entity_parent\":\"\",\"openmrs_entity\":\"concept\",\"openmrs_data_type\":\"deviceid\",\"openmrs_entity_id\":\"163149AAAAAAAAAAAAAAAAAAAAAAAAAAAAAA\",\"value\":\"358240051111110\"},\"subscriberid\":{\"openmrs_entity_parent\":\"\",\"openmrs_entity\":\"concept\",\"openmrs_data_type\":\"subscriberid\",\"openmrs_entity_id\":\"163150AAAAAAAAAAAAAAAAAAAAAAAAAAAAAA\",\"value\":\"310260000000000\"},\"simserial\":{\"openmrs_entity_parent\":\"\",\"openmrs_entity\":\"concept\",\"openmrs_data_type\":\"simserial\",\"openmrs_entity_id\":\"163151AAAAAAAAAAAAAAAAAAAAAAAAAAAAAA\",\"value\":\"89014103211118510720\"},\"phonenumber\":{\"openmrs_entity_parent\":\"\",\"openmrs_entity\":\"concept\",\"openmrs_data_type\":\"phonenumber\",\"openmrs_entity_id\":\"163152AAAAAAAAAAAAAAAAAAAAAAAAAAAAAA\",\"value\":\"+15555215554\"},\"encounter_location\":\"Kenya\",\"look_up\":{\"entity_id\":\"\",\"value\":\"\"}},\"step1\":{\"title\":\"Expansion Panel\",\"fields\":[{\"key\":\"accordion_panel_demo\",\"openmrs_entity_parent\":\"\",\"openmrs_entity\":\"\",\"openmrs_entity_id\":\"\",\"text\":\"Expansion Panel Demo\",\"type\":\"expansion_panel\",\"content_form\":\"expansion_panel_sub_form\",\"container\":\"anc_test\",\"value\":[{\"key\":\"blood_type_test_status\",\"type\":\"extended_radio_button\",\"label\":\"Blood type test\",\"index\":0,\"values\":[\"done_today:Done today\"],\"openmrs_attributes\":{\"openmrs_entity_parent\":\"\",\"openmrs_entity\":\"concept\",\"openmrs_entity_id\":\"165383AAAAAAAAAAAAAAAAAAAAAAAAAAAAAA\"},\"value_openmrs_attributes\":[{\"key\":\"blood_type_test_status\",\"openmrs_entity_parent\":\"\",\"openmrs_entity\":\"concept\",\"openmrs_entity_id\":\"165383AAAAAAAAAAAAAAAAAAAAAAAAAAAAAA\"}]},{\"key\":\"blood_type_test_date_today_hidden\",\"type\":\"hidden\",\"label\":\"\",\"index\":2,\"values\":[\"01-09-2020\"],\"openmrs_attributes\":{\"openmrs_entity_parent\":\"\",\"openmrs_entity\":\"\",\"openmrs_entity_id\":\"\"}},{\"key\":\"blood_type_test_date\",\"type\":\"date_picker\",\"label\":\"Blood type test date\",\"index\":3,\"values\":[\"01-09-2020\"],\"openmrs_attributes\":{\"openmrs_entity_parent\":\"300AAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAA\",\"openmrs_entity\":\"concept\",\"openmrs_entity_id\":\"163724AAAAAAAAAAAAAAAAAAAAAAAAAAAAAA\"}},{\"key\":\"blood_type\",\"type\":\"native_radio\",\"label\":\"Blood type\",\"index\":4,\"values\":[\"b:B\"],\"openmrs_attributes\":{\"openmrs_entity_parent\":\"\",\"openmrs_entity\":\"concept\",\"openmrs_entity_id\":\"163126AAAAAAAAAAAAAAAAAAAAAAAAAAAAAA\"},\"value_openmrs_attributes\":[{\"key\":\"blood_type\",\"openmrs_entity_parent\":\"\",\"openmrs_entity\":\"concept\",\"openmrs_entity_id\":\"163116AAAAAAAAAAAAAAAAAAAAAAAAAAAAAA\"}]},{\"key\":\"rh_factor\",\"type\":\"native_radio\",\"label\":\"Rh factor\",\"index\":5,\"values\":[\"positive:Positive\"],\"openmrs_attributes\":{\"openmrs_entity_parent\":\"\",\"openmrs_entity\":\"concept\",\"openmrs_entity_id\":\"160232AAAAAAAAAAAAAAAAAAAAAAAAAAAAAA\"},\"value_openmrs_attributes\":[{\"key\":\"rh_factor\",\"openmrs_entity_parent\":\"\",\"openmrs_entity\":\"concept\",\"openmrs_entity_id\":\"703AAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAA\"}]}],\"required_fields\":[\"blood_type_test_status\",\"blood_type\",\"rh_factor\"]}]},\"invisible_required_fields\":\"[blood_type_test_date]\"}";

    @Before
    public void setUp() {
        super.setUp();
        expansionPanelUndoButtonClickListener = new ExpansionPanelUndoButtonClickListener();
        MockitoAnnotations.initMocks(this);
    }

    @Test
    public void testUndoButtonOnClickShouldRemoveValueAttribute() throws JSONException {
        JSONObject form = new JSONObject(strForm);

        //check for value attribute before
        JSONObject jsonObject = FormUtils.getFieldJSONObject(FormUtils.getMultiStepFormFields(form), "accordion_panel_demo");
        Assert.assertTrue(jsonObject.has(JsonFormConstants.VALUE));

        ExpansionPanelUndoButtonClickListener expansionPanelUndoButtonClickListenerSpy = Mockito.spy(expansionPanelUndoButtonClickListener);

        Mockito.doReturn(form).when(jsonApi).getmJSONObject();

        Mockito.doReturn(jsonApi).when(expansionPanelUndoButtonClickListenerSpy)
                .getJsonApi();

        Application application = RuntimeEnvironment.application;

        View view = jsonFormActivity.getLayoutInflater().inflate(R.layout.expasion_panel_undo_dialog, null);

        JsonFormActivity jsonFormActivitySpy = Mockito.spy(jsonFormActivity);

        AlertDialog alertDialogSpy = Mockito.spy(new AlertDialog.Builder(jsonFormActivitySpy)
                .setView(view).create());

        Mockito.doReturn(alertDialogSpy).when(expansionPanelUndoButtonClickListenerSpy)
                .getUndoAlertDialog(jsonFormActivitySpy, view);

        Mockito.doReturn(view).when(expansionPanelUndoButtonClickListenerSpy)
                .getUndoDialogLayout(Mockito.any(LayoutInflater.class));

        Mockito.doReturn(form).when(jsonFormActivitySpy).getmJSONObject();

        Button button = new Button(application);
        button.setTag(R.id.key, "accordion_panel_demo");
        button.setTag(R.id.specify_context, jsonFormActivitySpy);
        button.setTag(R.id.specify_step_name, "step1");

        expansionPanelUndoButtonClickListenerSpy.onClick(button);

        CustomTextView headerTextView = view.findViewById(R.id.txt_title_label);
        Assert.assertFalse(headerTextView.getText().toString().isEmpty());

        Mockito.verify(alertDialogSpy, Mockito.times(1)).show();

        Button undo = view.findViewById(R.id.undo_button);
        undo.performClick();
        Mockito.verify(alertDialogSpy, Mockito.times(1)).dismiss();

        ArgumentCaptor<JSONObject> jsonObjectArgumentCaptor = ArgumentCaptor.forClass(JSONObject.class);
        Mockito.verify(jsonApi, Mockito.times(1))
                .setmJSONObject(jsonObjectArgumentCaptor.capture());

        JSONObject formResult = jsonObjectArgumentCaptor.getValue();
        Assert.assertNotNull(formResult);

        //check for value attribute after
        JSONObject jsonObjectResult = FormUtils.getFieldJSONObject(FormUtils.getMultiStepFormFields(formResult), "accordion_panel_demo");
        Assert.assertFalse(jsonObjectResult.has(JsonFormConstants.VALUE));

    }


    @Test
    public void onClickShouldSetUpExpansionPanel() throws JSONException {
        final String KEY = "key";
        final String VALUE = "value";

        View view = new View(RuntimeEnvironment.application);
        view.setTag(R.id.key, KEY);
        view.setTag(R.id.specify_context, jsonFormActivity);
        view.setTag(R.id.specify_step_name, "step1");

        FormUtils formUtils = Mockito.mock(FormUtils.class);
        JSONArray fields = new JSONArray();
        JSONObject field = new JSONObject();
        field.put(JsonFormConstants.KEY, KEY);
        field.put(JsonFormConstants.VALUE, VALUE);
        field.put(JsonFormConstants.TEXT, "header");
        fields.put(field);
        Mockito.doReturn(fields).when(formUtils).getFormFields(ArgumentMatchers.anyString(),
                ArgumentMatchers.any(Context.class));

        ReflectionHelpers.setField(expansionPanelUndoButtonClickListener, "formUtils", formUtils);
        when(jsonFormActivity.getmJSONObject()).thenReturn(new JSONObject(strForm));
        expansionPanelUndoButtonClickListener.onClick(view);

        Assert.assertNotNull(ShadowAlertDialog.getLatestAlertDialog());
    }
}