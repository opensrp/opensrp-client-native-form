package com.vijay.jsonwizard.widgets;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.mockito.ArgumentMatchers.anyInt;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;


import com.rey.material.widget.Button;
import com.vijay.jsonwizard.R;
import com.vijay.jsonwizard.activities.JsonFormActivity;
import com.vijay.jsonwizard.domain.WidgetArgs;
import com.vijay.jsonwizard.fragments.JsonFormFragment;
import com.vijay.jsonwizard.interfaces.CommonListener;
import com.vijay.jsonwizard.interfaces.JsonApi;
import com.vijay.jsonwizard.interfaces.OnActivityResultListener;
import com.vijay.jsonwizard.utils.FormUtils;

import org.json.JSONException;
import org.json.JSONObject;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.mockito.ArgumentCaptor;
import org.mockito.ArgumentMatchers;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.robolectric.Shadows;
import org.robolectric.shadows.ShadowLooper;
import org.robolectric.shadows.ShadowToast;

import java.time.Duration;
import java.util.List;

import android.app.Activity;
import android.content.Intent;
import android.content.res.Resources;
import android.os.Looper;
import android.view.View;
import android.view.ViewTreeObserver;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;
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

    WidgetArgs widgetArgs;

    @Mock
    Intent mockIntent;

    EditText systolicBp;

    EditText diastolicBp;


    private final String optiBPWidgetString = "{\n" +
            "            \"key\":\"optipb_widget1\",\n" +
            "            \"openmrs_entity_parent\":\"\",\n" +
            "            \"openmrs_entity\":\"\",\n" +
            "            \"openmrs_entity_id\":\"\",\n" +
            "            \"type\":\"optibp\",\n" +
            "            \"label\":\"Measure the blood pressure using OptiBP\",\n" +
            "            \"optibp_button_bg_color\":\"#d32f2f\",\n" +
            "            \"optibp_button_text_color\":\"#FFFFFF\",\n" +
            "            \"read_only\":false,\n" +
            "            \"fields_to_use_value\":[\n" +
            "               \"bp_systolic\",\n" +
            "               \"bp_diastolic\"\n" +
            "            ],\n" +
            "            \"optibp_data\":{\n" +
            "               \"clientId\":\"sampleClientId\",\n" +
            "               \"clientOpenSRPId\":\"sampleClientOpenSRPId\",\n" +
            "               \"calibration\":\"\"\n" +
            "            }\n" +
            "}";

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
            "          \"clientOpenSRPId\": \"sampleClientOpenSRPId\",\"calibration\":\"\"\n" +
            "        }\n" +
            "      },\n" +
            "      {\n" +
            "        \"key\": \"optibp_client_calibration_data\",\n" +
            "        \"openmrs_entity_parent\": \"\",\n" +
            "        \"openmrs_entity\": \"\",\n" +
            "        \"openmrs_entity_id\": \"\",\n" +
            "        \"type\": \"hidden\",\n" +
            "        \"value\": \"[{\\\"date\\\":\\\"2019-03-26T11:20:33+0800\\\",\\\"model\\\":\\\"device model\\\",\\\"height\\\":70,\\\"weight\\\":180,\\\"comperatives\\\":[{\\\"systolic\\\":120,\\\"diastolic\\\":80,\\\"cuffSystolic\\\":120,\\\"cuffDiastolic\\\":80,\\\"features\\\":{\\\"$key\\\":\\\"0.2f\\\"}}]}]\"\n" +
            "      }\n" +
            "    ]\n" +
            "  }\n" +
            "}";

    private final String resultJson = "{\n" +
            "  \"identifier\": [\n" +
            "    {\n" +
            "      \"use\": \"official\",\n" +
            "      \"value\": \"dc2122cf-213c-469f-a1a8-f42035bc3559\"\n" +
            "    }\n" +
            "  ],\n" +
            "  \"resourceType\": \"DiagnosticReport\",\n" +
            "  \"result\": [\n" +
            "    {\n" +
            "      \"category\": [\n" +
            "        {\n" +
            "          \"coding\": [\n" +
            "            {\n" +
            "              \"code\": \"vital-signs\",\n" +
            "              \"display\": \"vital-signs\",\n" +
            "              \"system\": \"http://terminology.hl7.org/CodeSystem/observation-category\"\n" +
            "            }\n" +
            "          ]\n" +
            "        }\n" +
            "      ],\n" +
            "      \"code\": {\n" +
            "        \"coding\": [\n" +
            "          {\n" +
            "            \"code\": \"55284-4\",\n" +
            "            \"display\": \"Blood Pressure\",\n" +
            "            \"system\": \"http://loinc.org\"\n" +
            "          }\n" +
            "        ],\n" +
            "        \"text\": \"Blood Pressure\"\n" +
            "      },\n" +
            "      \"component\": [\n" +
            "        {\n" +
            "          \"valueQuantity\": {\n" +
            "            \"value\": -10\n" +
            "          },\n" +
            "          \"code\": {\n" +
            "            \"coding\": [\n" +
            "              {\n" +
            "                \"code\": \"8462-4\",\n" +
            "                \"display\": \"Diastolic Blood Pressure\",\n" +
            "                \"system\": \"http://loinc.org\"\n" +
            "              }\n" +
            "            ],\n" +
            "            \"text\": \"Diastolic Blood Pressure\"\n" +
            "          }\n" +
            "        },\n" +
            "        {\n" +
            "          \"valueQuantity\": {\n" +
            "            \"value\": 72\n" +
            "          },\n" +
            "          \"code\": {\n" +
            "            \"coding\": [\n" +
            "              {\n" +
            "                \"code\": \"8462-6\",\n" +
            "                \"display\": \"Systolic Blood Pressure\",\n" +
            "                \"system\": \"http://loinc.org\"\n" +
            "              }\n" +
            "            ],\n" +
            "            \"text\": \"Systolic Blood Pressure\"\n" +
            "          }\n" +
            "        },\n" +
            "        {\n" +
            "          \"valueString\": \"[{\\\"comperatives\\\":[{\\\"cuffDiastolic\\\":100,\\\"cuffSystolic\\\":180,\\\"diastolic\\\":85,\\\"systolic\\\":180}],\\\"date\\\":\\\"2023-07-14T11:44:19+0500\\\",\\\"height\\\":100,\\\"model\\\":\\\"SM-T540\\\",\\\"version\\\":1,\\\"weight\\\":66}]\",\n" +
            "          \"code\": {\n" +
            "            \"coding\": [\n" +
            "              {\n" +
            "                \"code\": \"bp_calibration_data\",\n" +
            "                \"display\": \"Blood Pressure Calibration Data\",\n" +
            "                \"system\": \"http://loinc.org\"\n" +
            "              }\n" +
            "            ],\n" +
            "            \"text\": \"Blood Pressure Calibration Data\"\n" +
            "          }\n" +
            "        }\n" +
            "      ],\n" +
            "      \"status\": \"final\"\n" +
            "    }\n" +
            "  ],\n" +
            "  \"subject\": \"c55e3d2e-398d-4b7b-aedd-5378b5113d71\"\n" +
            "}";

    @Before
    public void setUp() {
        super.setUp();
        try {
            jsonFormActivity.setmJSONObject(new JSONObject(formString));
        } catch (JSONException e) {
            Timber.e(e);
        }
        systolicBp = new EditText(jsonFormActivity);
        systolicBp.setTag(R.id.address, "step1:bp_systolic");
        jsonFormActivity.addFormDataView(systolicBp);
        diastolicBp = new EditText(jsonFormActivity);
        diastolicBp.setTag(R.id.address, "step1:bp_diastolic");
        jsonFormActivity.addFormDataView(diastolicBp);
        factory = new OptiBPWidgetFactory();
        widgetArgs = new WidgetArgs();
    }

    @Test
    public void testFactoryInstantiatesViewsCorrectly() throws Exception {
        assertNotNull(factory);
        OptiBPWidgetFactory factorySpy = Mockito.spy(factory);
        assertNotNull(factorySpy);

        FormUtils formUtils = new FormUtils();
        FormUtils formUtilsSpy = Mockito.spy(formUtils);
        assertNotNull(formUtilsSpy);

        Mockito.doReturn(rootLayout).when(factorySpy).getRootLayout(context);
        assertNotNull(rootLayout);

        Mockito.doReturn(Mockito.mock(ViewTreeObserver.class)).when(rootLayout).getViewTreeObserver();

        Mockito.doReturn(resources).when(context).getResources();
        assertNotNull(resources);

        Mockito.doReturn(label).when(rootLayout).findViewById(R.id.optibp_label);
        assertNotNull(label);

        Mockito.doReturn(launchButton).when(rootLayout).findViewById(R.id.optibp_launch_button);
        assertNotNull(launchButton);

        //noinspection ResultOfMethodCallIgnored
        Mockito.doReturn(jsonFormActivity).when(formFragment).getJsonApi();

        List<View> viewList = factorySpy.getViewsFromJson("step1", context, formFragment, new JSONObject(optiBPWidgetString), listener);
        assertNotNull(viewList);
        Assert.assertTrue(viewList.size() > 0);
    }

    @Test
    public void testInputJson() throws JSONException {
        assertNotNull(factory);
        OptiBPWidgetFactory factorySpy = Mockito.spy(factory);
        assertNotNull(factorySpy);
        WidgetArgs widgetArgs = Mockito.mock(WidgetArgs.class);
        JsonFormFragment formFragment = Mockito.mock(JsonFormFragment.class);
        JsonApi jsonApi = Mockito.mock(JsonApi.class);
        Mockito.doReturn(formFragment).when(widgetArgs).getFormFragment();
        Mockito.doReturn(jsonApi).when(formFragment).getJsonApi();
        String step1Json = "{\n" +
                "   \"title\":\"OptiBp Widget Demo\",\n" +
                "   \"fields\":[\n" +
                "      {\n" +
                "         \"key\":\"enabled_label\",\n" +
                "         \"type\":\"label\",\n" +
                "         \"text\":\"OptiBP Scan\",\n" +
                "         \"hint_on_text\":false,\n" +
                "         \"text_color\":\"#FFC100\",\n" +
                "         \"openmrs_entity_parent\":\"\",\n" +
                "         \"openmrs_entity\":\"\",\n" +
                "         \"openmrs_entity_id\":\"\",\n" +
                "         \"label_info_text\":\"Checking out the functionality for OptiBP widget\"\n" +
                "      },\n" +
                "      {\n" +
                "         \"key\":\"bp_systolic_label\",\n" +
                "         \"type\":\"label\",\n" +
                "         \"label_text_style\":\"bold\",\n" +
                "         \"text\":\"Systolic blood pressure (SBP) (mmHg)\",\n" +
                "         \"text_color\":\"#000000\",\n" +
                "         \"v_required\":{\n" +
                "            \"value\":true\n" +
                "         }\n" +
                "      },\n" +
                "      {\n" +
                "         \"key\":\"bp_systolic\",\n" +
                "         \"openmrs_entity_parent\":\"\",\n" +
                "         \"openmrs_entity\":\"\",\n" +
                "         \"openmrs_entity_id\":\"5090\",\n" +
                "         \"type\":\"normal_edit_text\",\n" +
                "         \"edit_text_style\":\"bordered\",\n" +
                "         \"edit_type\":\"number\",\n" +
                "         \"v_required\":{\n" +
                "            \"value\":\"true\",\n" +
                "            \"err\":\"Please enter BP systolic value\"\n" +
                "         },\n" +
                "         \"v_numeric\":{\n" +
                "            \"value\":\"true\",\n" +
                "            \"err\":\"\"\n" +
                "         }\n" +
                "      },\n" +
                "      {\n" +
                "         \"key\":\"spacer\",\n" +
                "         \"openmrs_entity_parent\":\"\",\n" +
                "         \"openmrs_entity\":\"\",\n" +
                "         \"openmrs_entity_id\":\"spacer\",\n" +
                "         \"type\":\"spacer\",\n" +
                "         \"spacer_height\":\"10sp\"\n" +
                "      },\n" +
                "      {\n" +
                "         \"key\":\"bp_diastolic_label\",\n" +
                "         \"type\":\"label\",\n" +
                "         \"label_text_style\":\"bold\",\n" +
                "         \"text\":\"Diastolic blood pressure (DBP) (mmHg)\",\n" +
                "         \"text_color\":\"#000000\",\n" +
                "         \"v_required\":{\n" +
                "            \"value\":true\n" +
                "         }\n" +
                "      },\n" +
                "      {\n" +
                "         \"key\":\"bp_diastolic\",\n" +
                "         \"openmrs_entity_parent\":\"\",\n" +
                "         \"openmrs_entity\":\"\",\n" +
                "         \"openmrs_entity_id\":\"5089\",\n" +
                "         \"type\":\"normal_edit_text\",\n" +
                "         \"edit_text_style\":\"bordered\",\n" +
                "         \"edit_type\":\"number\",\n" +
                "         \"v_numeric\":{\n" +
                "            \"value\":\"true\",\n" +
                "            \"err\":\"\"\n" +
                "         },\n" +
                "         \"v_required\":{\n" +
                "            \"value\":\"true\",\n" +
                "            \"err\":\"Please enter the BP diastolic value\"\n" +
                "         }\n" +
                "      },\n" +
                "      {\n" +
                "         \"key\":\"spacer\",\n" +
                "         \"openmrs_entity_parent\":\"\",\n" +
                "         \"openmrs_entity\":\"\",\n" +
                "         \"openmrs_entity_id\":\"spacer\",\n" +
                "         \"type\":\"spacer\",\n" +
                "         \"spacer_height\":\"10sp\"\n" +
                "      },\n" +
                "      {\n" +
                "         \"key\":\"optipb_widget1\",\n" +
                "         \"openmrs_entity_parent\":\"\",\n" +
                "         \"openmrs_entity\":\"\",\n" +
                "         \"openmrs_entity_id\":\"\",\n" +
                "         \"type\":\"optibp\",\n" +
                "         \"label\":\"Measure the blood pressure using OptiBP\",\n" +
                "         \"optibp_button_bg_color\":\"#d32f2f\",\n" +
                "         \"optibp_button_text_color\":\"#FFFFFF\",\n" +
                "         \"read_only\":false,\n" +
                "         \"fields_to_use_value\":[\n" +
                "            \"bp_systolic\",\n" +
                "            \"bp_diastolic\"\n" +
                "         ],\n" +
                "         \"optibp_data\":{\n" +
                "            \"clientId\":\"sampleClientId\",\n" +
                "            \"clientOpenSRPId\":\"sampleClientOpenSRPId\",\n" +
                "            \"calibration\":\"\"\n" +
                "         }\n" +
                "      },\n" +
                "      {\n" +
                "         \"key\":\"optibp_client_calibration_data\",\n" +
                "         \"openmrs_entity_parent\":\"\",\n" +
                "         \"openmrs_entity\":\"\",\n" +
                "         \"openmrs_entity_id\":\"\",\n" +
                "         \"type\":\"hidden\",\n" +
                "         \"value\":\"[{\\\"date\\\":\\\"2019-03-26T11:20:33+0800\\\",\\\"model\\\":\\\"device model\\\",\\\"height\\\":70,\\\"weight\\\":180,\\\"comperatives\\\":[{\\\"systolic\\\":120,\\\"diastolic\\\":80,\\\"cuffSystolic\\\":120,\\\"cuffDiastolic\\\":80,\\\"features\\\":{\\\"$key\\\":\\\"0.2f\\\"}}]}]\"\n" +
                "      }\n" +
                "   ]\n" +
                "}";
        Mockito.doReturn(new JSONObject(step1Json)).when(jsonApi).getStep(ArgumentMatchers.anyString());
        String inputJson = factorySpy.getInputJsonString(jsonFormActivity, new JSONObject(optiBPWidgetString), widgetArgs);
        assertEquals(inputJson, "{\"clientId\":\"sampleClientId\",\"clientOpenSRPId\":\"sampleClientOpenSRPId\"}");
    }

    @Test
    public void testResultJson() throws JSONException {
        assertNotNull(factory);
        OptiBPWidgetFactory factorySpy = Mockito.spy(factory);
        assertNotNull(factorySpy);
        String systolic = factorySpy.getBPValue(resultJson, OptiBPWidgetFactory.BPFieldType.SYSTOLIC_BP);
        String diastolic = factorySpy.getBPValue(resultJson, OptiBPWidgetFactory.BPFieldType.DIASTOLIC_BP);
        assertEquals(systolic, "72");
        assertEquals(diastolic, "-10");
    }

    @Test
    public void testPopulateETValues() throws JSONException {
        assertNotNull(factory);
        OptiBPWidgetFactory factorySpy = Mockito.spy(factory);
        assertNotNull(factorySpy);
        EditText sbp = Mockito.mock(EditText.class);
        EditText dbp = Mockito.mock(EditText.class);
        widgetArgs = Mockito.mock(WidgetArgs.class);
        factorySpy.populateBPEditTextValues(resultJson, sbp, dbp, widgetArgs);
        verify(sbp).setEnabled(false);
        verify(dbp).setEnabled(false);
    }

    @Test
    public void testSetUpOptiBpActivityResultListenerReturnsResultOK() {
        WidgetArgs widgetArgs = Mockito.mock(WidgetArgs.class);
        when(widgetArgs.getContext()).thenReturn(jsonFormActivity);
        when(mockIntent.getStringExtra(Intent.EXTRA_TEXT)).thenReturn(resultJson);
        int requestCode = 301;
        factory.setUpOptiBpActivityResultListener(widgetArgs, requestCode, rootLayout, systolicBp, diastolicBp);
        ArgumentCaptor<OnActivityResultListener> resultListenerArgumentCaptor = ArgumentCaptor.forClass(OnActivityResultListener.class);
        verify(jsonFormActivity).addOnActivityResultListener(anyInt(), resultListenerArgumentCaptor.capture());
        resultListenerArgumentCaptor.getValue().onActivityResult(requestCode,Activity.RESULT_OK,mockIntent);

    }
    @Test
    public void testSetUpOptiBpActivityResultListenerShowsToast() {
        WidgetArgs widgetArgs = Mockito.mock(WidgetArgs.class);
        when(widgetArgs.getContext()).thenReturn(jsonFormActivity);
        String badJson="{\n" +
                "  \"identifier\": [\n" +
                "    {\n" +
                "      \"use\": \"official\",\n" +
                "      \"value\": \"dc2122cf-213c-469f-a1a8-f42035bc3559\"\n" +
                "    }\n" +
                "  ],\n" +
                "  \"resourceType\": \"DiagnosticReport\",\n" +
                "  \"result\": [\n" +
                "    {\n" +
                "      \"category\": [\n" +
                "        {\n" +
                "          \"coding\": [\n" +
                "            {\n" +
                "              \"code\": \"vital-signs\",\n" +
                "              \"display\": \"vital-signs\",\n" +
                "              \"system\": \"http://terminology.hl7.org/CodeSystem/observation-category\"\n" +
                "            }\n" +
                "          ]\n" +
                "        }\n" +
                "      ],\n" +
                "      \"code\": {\n" +
                "        \"coding\": [\n" +
                "          {\n" +
                "            \"code\": \"55284-4\",\n" +
                "            \"display\": \"Blood Pressure\",\n" +
                "            \"system\": \"http://loinc.org\"\n" +
                "          }\n" +
                "        ],\n" +
                "        \"text\": \"Blood Pressure\"\n" +
                "      },\n" +
                "      \"component\": [\n" +
                "        {\n" +
                "          \"valueQuantity\": {\n" +
                "            \"value\": -10\n" +
                "          },\n" +
                "          \"code\": {\n" +
                "            \"coding\": [\n" +
                "              {\n" +
                "                \"code\": \"8462-4\",\n" +
                "                \"display\": \"Diastolic Blood Pressure\",\n" +
                "                \"system\": \"http://loinc.org\"\n" +
                "              }\n" +
                "            ],\n" +
                "            \"text\": \"Diastolic Blood Pressure\"\n" +
                "          }\n" +
                "        },\n" +
                "        {\n" +
                "          \"valueQuantity\": {\n" +
                "            \"value\": 72\n" +
                "          },\n" +
                "          \"code\": {\n" +
                "            \"coding\": [\n" +
                "              {\n" +
                "                \"code\": \"8462-6\",\n" +
                "                \"display\": \"Systolic Blood Pressure\",\n" +
                "                \"system\": \"http://loinc.org\"\n" +
                "              }\n" +
                "            ],\n" +
                "            \"text\": \"Systolic Blood Pressure\"\n" +
                "          }\n" +
                "        },\n" +
                "        {\n" +
                "          \"valueString\": \"\",\n" +
                "          \"code\": {\n" +
                "            \"coding\": [\n" +
                "              {\n" +
                "                \"code\": \"bp_calibration_data\",\n" +
                "                \"display\": \"Blood Pressure Calibration Data\",\n" +
                "                \"system\": \"http://loinc.org\"\n" +
                "              }\n" +
                "            ],\n" +
                "            \"text\": \"Blood Pressure Calibration Data\"\n" +
                "          }\n" +
                "        }\n" +
                "      ],\n" +
                "      \"status\": \"final\"\n" +
                "    }\n" +
                "  ],\n" +
                "  \"subject\": \"c55e3d2e-398d-4b7b-aedd-5378b5113d71\"\n" +
                "}";
        when(mockIntent.getStringExtra(Intent.EXTRA_TEXT)).thenReturn(badJson);
        int requestCode = 301;
        factory.setUpOptiBpActivityResultListener(widgetArgs, requestCode, rootLayout, systolicBp, diastolicBp);
        ArgumentCaptor<OnActivityResultListener> resultListenerArgumentCaptor = ArgumentCaptor.forClass(OnActivityResultListener.class);
        verify(jsonFormActivity).addOnActivityResultListener(anyInt(), resultListenerArgumentCaptor.capture());
        resultListenerArgumentCaptor.getValue().onActivityResult(requestCode,Activity.RESULT_OK,mockIntent);
        ShadowLooper shadowLooper = Shadows.shadowOf(Looper.getMainLooper());
        shadowLooper.idleFor(Duration.ofDays(Toast.LENGTH_SHORT));
        Toast toast = ShadowToast.getLatestToast();
        assertEquals(Toast.LENGTH_SHORT, toast.getDuration());

    }
}