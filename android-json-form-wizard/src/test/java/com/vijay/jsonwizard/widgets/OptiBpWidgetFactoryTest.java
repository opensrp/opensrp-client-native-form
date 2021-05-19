package com.vijay.jsonwizard.widgets;

import android.content.res.Resources;
import android.view.View;
import android.view.ViewTreeObserver;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.rey.material.widget.Button;
import com.vijay.jsonwizard.R;
import com.vijay.jsonwizard.activities.JsonFormActivity;
import com.vijay.jsonwizard.fragments.JsonFormFragment;
import com.vijay.jsonwizard.interfaces.CommonListener;
import com.vijay.jsonwizard.utils.FormUtils;

import org.json.JSONException;
import org.json.JSONObject;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.mockito.Mock;
import org.mockito.Mockito;

import java.util.List;

import timber.log.Timber;


public class OptiBpWidgetFactoryTest extends FactoryTest {
    private OptiBPWidgetFactory factory;

    @Mock
    private JsonFormActivity context;

    @Mock
    private JsonFormFragment formFragment;

    @Mock
    private Resources resources;

    @Mock
    private CommonListener listener;

    @Mock
    private LinearLayout rootLayout;

    @Mock
    private TextView label;

    @Mock
    private Button launchButton;

    private final String optiBPWidgetString = "{\n" +
            "        \"key\": \"optipb_widget1\",\n" +
            "        \"openmrs_entity_parent\": \"\",\n" +
            "        \"openmrs_entity\": \"\",\n" +
            "        \"openmrs_entity_id\": \"\",\n" +
            "        \"type\": \"optibp\",\n" +
            "        \"label\": \"Measure the blood pressure using OptiBP\",\n" +
            "        \"optibp_button_bg_color\": \"#d32f2f\",\n" +
            "        \"optibp_button_text_color\": \"#FFFFFF\",\n" +
            "        \"read_only\": false,\n" +
            "        \"optibp_data\": {\n" +
            "          \"clientId\": \"sampleClientId\",\n" +
            "          \"clientOpenSRPId\": \"sampleClientOpenSRPId\"\n" +
            "        }," +
            "        \"fields_to_use_value\": [\n" +
            "          \"bp_systolic\",\n" +
            "          \"bp_diastolic\"\n" +
            "        ]" +
            "      }";

    private final String formString = "{\n" +
            "  \"show_errors_on_submit\": true,\n" +
            "  \"encounter_type\": \"OptiBP Demo\",\n" +
            "  \"count\": \"1\",\n" +
            "  \"entity_id\": \"\",\n" +
            "  \"relational_id\": \"\",\n" +
            "  \"metadata\": {\n" +
            "    \"start\": {\n" +
            "      \"openmrs_entity_parent\": \"\",\n" +
            "      \"openmrs_entity\": \"concept\",\n" +
            "      \"openmrs_data_type\": \"start\",\n" +
            "      \"openmrs_entity_id\": \"163137AAAAAAAAAAAAAAAAAAAAAAAAAAAAAA\"\n" +
            "    },\n" +
            "    \"end\": {\n" +
            "      \"openmrs_entity_parent\": \"\",\n" +
            "      \"openmrs_entity\": \"concept\",\n" +
            "      \"openmrs_data_type\": \"end\",\n" +
            "      \"openmrs_entity_id\": \"163138AAAAAAAAAAAAAAAAAAAAAAAAAAAAAA\"\n" +
            "    },\n" +
            "    \"today\": {\n" +
            "      \"openmrs_entity_parent\": \"\",\n" +
            "      \"openmrs_entity\": \"encounter\",\n" +
            "      \"openmrs_entity_id\": \"encounter_date\"\n" +
            "    },\n" +
            "    \"deviceid\": {\n" +
            "      \"openmrs_entity_parent\": \"\",\n" +
            "      \"openmrs_entity\": \"concept\",\n" +
            "      \"openmrs_data_type\": \"deviceid\",\n" +
            "      \"openmrs_entity_id\": \"163149AAAAAAAAAAAAAAAAAAAAAAAAAAAAAA\"\n" +
            "    },\n" +
            "    \"subscriberid\": {\n" +
            "      \"openmrs_entity_parent\": \"\",\n" +
            "      \"openmrs_entity\": \"concept\",\n" +
            "      \"openmrs_data_type\": \"subscriberid\",\n" +
            "      \"openmrs_entity_id\": \"163150AAAAAAAAAAAAAAAAAAAAAAAAAAAAAA\"\n" +
            "    },\n" +
            "    \"simserial\": {\n" +
            "      \"openmrs_entity_parent\": \"\",\n" +
            "      \"openmrs_entity\": \"concept\",\n" +
            "      \"openmrs_data_type\": \"simserial\",\n" +
            "      \"openmrs_entity_id\": \"163151AAAAAAAAAAAAAAAAAAAAAAAAAAAAAA\"\n" +
            "    },\n" +
            "    \"phonenumber\": {\n" +
            "      \"openmrs_entity_parent\": \"\",\n" +
            "      \"openmrs_entity\": \"concept\",\n" +
            "      \"openmrs_data_type\": \"phonenumber\",\n" +
            "      \"openmrs_entity_id\": \"163152AAAAAAAAAAAAAAAAAAAAAAAAAAAAAA\"\n" +
            "    },\n" +
            "    \"encounter_location\": \"\",\n" +
            "    \"look_up\": {\n" +
            "      \"entity_id\": \"\",\n" +
            "      \"value\": \"\"\n" +
            "    }\n" +
            "  },\n" +
            "  \"step1\": {\n" +
            "    \"title\": \"OptiBp Widget Demo\",\n" +
            "    \"fields\": [\n" +
            "      {\n" +
            "        \"key\": \"enabled_label\",\n" +
            "        \"type\": \"label\",\n" +
            "        \"text\": \"OptiBP Scan\",\n" +
            "        \"hint_on_text\": false,\n" +
            "        \"text_color\": \"#FFC100\",\n" +
            "        \"openmrs_entity_parent\": \"\",\n" +
            "        \"openmrs_entity\": \"\",\n" +
            "        \"openmrs_entity_id\": \"\",\n" +
            "        \"label_info_text\": \"Checking out the functionality for OptiBP widget\"\n" +
            "      },\n" +
            "      {\n" +
            "        \"key\": \"bp_systolic_label\",\n" +
            "        \"type\": \"label\",\n" +
            "        \"label_text_style\": \"bold\",\n" +
            "        \"text\": \"Systolic blood pressure (SBP) (mmHg)\",\n" +
            "        \"text_color\": \"#000000\",\n" +
            "        \"v_required\": {\n" +
            "          \"value\": true\n" +
            "        }\n" +
            "      },\n" +
            "      {\n" +
            "        \"key\": \"bp_systolic\",\n" +
            "        \"openmrs_entity_parent\": \"\",\n" +
            "        \"openmrs_entity\": \"\",\n" +
            "        \"openmrs_entity_id\": \"5090\",\n" +
            "        \"type\": \"normal_edit_text\",\n" +
            "        \"edit_text_style\": \"bordered\",\n" +
            "        \"edit_type\": \"number\",\n" +
            "        \"v_required\": {\n" +
            "          \"value\": \"true\",\n" +
            "          \"err\": \"Please enter BP systolic value\"\n" +
            "        },\n" +
            "        \"v_numeric\": {\n" +
            "          \"value\": \"true\",\n" +
            "          \"err\": \"\"\n" +
            "        }\n" +
            "      },\n" +
            "      {\n" +
            "        \"key\": \"spacer\",\n" +
            "        \"openmrs_entity_parent\": \"\",\n" +
            "        \"openmrs_entity\": \"\",\n" +
            "        \"openmrs_entity_id\": \"spacer\",\n" +
            "        \"type\": \"spacer\",\n" +
            "        \"spacer_height\": \"10sp\"\n" +
            "      },\n" +
            "      {\n" +
            "        \"key\": \"bp_diastolic_label\",\n" +
            "        \"type\": \"label\",\n" +
            "        \"label_text_style\": \"bold\",\n" +
            "        \"text\": \"Diastolic blood pressure (DBP) (mmHg)\",\n" +
            "        \"text_color\": \"#000000\",\n" +
            "        \"v_required\": {\n" +
            "          \"value\": true\n" +
            "        }\n" +
            "      },\n" +
            "      {\n" +
            "        \"key\": \"bp_diastolic\",\n" +
            "        \"openmrs_entity_parent\": \"\",\n" +
            "        \"openmrs_entity\": \"\",\n" +
            "        \"openmrs_entity_id\": \"5089\",\n" +
            "        \"type\": \"normal_edit_text\",\n" +
            "        \"edit_text_style\": \"bordered\",\n" +
            "        \"edit_type\": \"number\",\n" +
            "        \"v_numeric\": {\n" +
            "          \"value\": \"true\",\n" +
            "          \"err\": \"\"\n" +
            "        },\n" +
            "        \"v_required\": {\n" +
            "          \"value\": \"true\",\n" +
            "          \"err\": \"Please enter the BP diastolic value\"\n" +
            "        }\n" +
            "      },\n" +
            "      {\n" +
            "        \"key\": \"spacer\",\n" +
            "        \"openmrs_entity_parent\": \"\",\n" +
            "        \"openmrs_entity\": \"\",\n" +
            "        \"openmrs_entity_id\": \"spacer\",\n" +
            "        \"type\": \"spacer\",\n" +
            "        \"spacer_height\": \"10sp\"\n" +
            "      },\n" +
            "      {\n" +
            "        \"key\": \"optipb_widget1\",\n" +
            "        \"openmrs_entity_parent\": \"\",\n" +
            "        \"openmrs_entity\": \"\",\n" +
            "        \"openmrs_entity_id\": \"\",\n" +
            "        \"type\": \"optibp\",\n" +
            "        \"label\": \"Measure the blood pressure using OptiBP\",\n" +
            "        \"optibp_button_bg_color\": \"#d32f2f\",\n" +
            "        \"optibp_button_text_color\": \"#FFFFFF\",\n" +
            "        \"read_only\": false,\n" + "        " +
            "        \"fields_to_use_value\": [\n" +
            "          \"bp_systolic\",\n" +
            "          \"bp_diastolic\"\n" +
            "        ]," +
            "        \"optibp_data\": {\n" +
            "          \"clientId\": \"sampleClientId\",\n" +
            "          \"clientOpenSRPId\": \"sampleClientOpenSRPId\"\n" +
            "        }" +
            "      }\n" +
            "    ]\n" +
            "  }\n" +
            "}";

    private final String resultJson = "{\n" +
            " \"resourceType\": \"DiagnosticReport\",\n" +
            " \"identifier\": [\n" +
            "  {\n" +
            "   \"use\": \"official\",\n" +
            "   \"value\": \"uuid\" \n" +
            "  }\n" +
            " ],\n" +
            " \"subject\": \"patient-uuid\", \n" +
            " \"result\": [\n" +
            "  {\n" +
            "   \"status\": \"final\",\n" +
            "   \"category\": [\n" +
            "    {\n" +
            "     \"coding\": [\n" +
            "      {\n" +
            "       \"system\": \"http://terminology.hl7.org/CodeSystem/observation-category\",\n" +
            "       \"code\": \"vital-signs\",\n" +
            "       \"display\": \"vital-signs\"\n" +
            "      }\n" +
            "     ]\n" +
            "    }\n" +
            "   ],\n" +
            "   \"code\": {\n" +
            "    \"coding\": [\n" +
            "     {\n" +
            "      \"system\": \"http://loinc.org\",\n" +
            "      \"code\": \"55284-4\",\n" +
            "      \"display\": \"Blood Pressure\"\n" +
            "     }\n" +
            "    ],\n" +
            "    \"text\": \"Blood Pressure\"\n" +
            "   },\n" +
            "   \"component\": [\n" +
            "    {\n" +
            "     \"code\": {\n" +
            "      \"coding\": [\n" +
            "       {\n" +
            "        \"system\": \"http://loinc.org\",\n" +
            "        \"code\": \"8462-4\",\n" +
            "        \"display\": \"Diastolic Blood Pressure\"\n" +
            "       }\n" +
            "      ],\n" +
            "      \"text\": \"Diastolic Blood Pressure\"\n" +
            "     },\n" +
            "     \"valueQuantity\": {\n" +
            "      \"value\": 70\n" +
            "     }\n" +
            "    },\n" +
            "    {\n" +
            "     \"code\": {\n" +
            "      \"coding\": [\n" +
            "       {\n" +
            "        \"system\": \"http://loinc.org\",\n" +
            "        \"code\": \"8480-6\",\n" +
            "        \"display\": \"Systolic Blood Pressure\"\n" +
            "       }\n" +
            "      ],\n" +
            "      \"text\": \"Systolic Blood Pressure\"\n" +
            "     },\n" +
            "     \"valueQuantity\": {\n" +
            "      \"value\": 110\n" +
            "     }\n" +
            "    }\n" +
            "   ]\n" +
            "  }\n" +
            " ]\n" +
            "}";

    @Before
    public void setUp() {
        super.setUp();
        try {
            jsonFormActivity.setmJSONObject(new JSONObject(formString));
        } catch (JSONException e) {
            Timber.e(e);
        }
        EditText sbp = new EditText(jsonFormActivity);
        sbp.setTag(R.id.address, "step1:bp_systolic");
        jsonFormActivity.addFormDataView(sbp);
        EditText dbp = new EditText(jsonFormActivity);
        dbp.setTag(R.id.address, "step1:bp_diastolic");
        jsonFormActivity.addFormDataView(dbp);
        factory = new OptiBPWidgetFactory();
    }

    @Test
    public void testFactoryInstantiatesViewsCorrectly() throws Exception {
        Assert.assertNotNull(factory);
        OptiBPWidgetFactory factorySpy = Mockito.spy(factory);
        Assert.assertNotNull(factorySpy);

        FormUtils formUtils = new FormUtils();
        FormUtils formUtilsSpy = Mockito.spy(formUtils);
        Assert.assertNotNull(formUtilsSpy);

        Mockito.doReturn(rootLayout).when(factorySpy).getRootLayout(context);
        Assert.assertNotNull(rootLayout);

        Mockito.doReturn(Mockito.mock(ViewTreeObserver.class)).when(rootLayout).getViewTreeObserver();

        Mockito.doReturn(resources).when(context).getResources();
        Assert.assertNotNull(resources);

        Mockito.doReturn(label).when(rootLayout).findViewById(R.id.optibp_label);
        Assert.assertNotNull(label);

        Mockito.doReturn(launchButton).when(rootLayout).findViewById(R.id.optibp_launch_button);
        Assert.assertNotNull(launchButton);

        //noinspection ResultOfMethodCallIgnored
        Mockito.doReturn(jsonFormActivity).when(formFragment).getJsonApi();

        List<View> viewList = factorySpy.getViewsFromJson("step1", context, formFragment, new JSONObject(optiBPWidgetString), listener);
        Assert.assertNotNull(viewList);
        Assert.assertTrue(viewList.size() > 0);
    }

    @Test
    public void testInputJson() throws JSONException {
        Assert.assertNotNull(factory);
        OptiBPWidgetFactory factorySpy = Mockito.spy(factory);
        Assert.assertNotNull(factorySpy);

        String inputJson = factorySpy.getInputJsonString(jsonFormActivity, new JSONObject(optiBPWidgetString));

        Assert.assertEquals(inputJson, "{\"clientId\":\"sampleClientId\",\"clientOpenSRPId\":\"sampleClientOpenSRPId\"}");
    }

    @Test
    public void testResultJson() throws JSONException {
        Assert.assertNotNull(factory);
        OptiBPWidgetFactory factorySpy = Mockito.spy(factory);
        Assert.assertNotNull(factorySpy);

        String systolic = factorySpy.getBPValue(resultJson, OptiBPWidgetFactory.BPFieldType.SYSTOLIC_BP);
        String diastolic = factorySpy.getBPValue(resultJson, OptiBPWidgetFactory.BPFieldType.DIASTOLIC_BP);

        Assert.assertEquals(systolic, "110");
        Assert.assertEquals(diastolic, "70");
    }

    @Test
    public void testPopulateETValues() throws JSONException {
        Assert.assertNotNull(factory);
        OptiBPWidgetFactory factorySpy = Mockito.spy(factory);
        Assert.assertNotNull(factorySpy);

        EditText sbp = Mockito.mock(EditText.class);
        EditText dbp = Mockito.mock(EditText.class);
        factorySpy.populateBPEditTextValues(resultJson, sbp, dbp);

        Mockito.verify(sbp).setEnabled(false);
        Mockito.verify(dbp).setEnabled(false);
    }
}
